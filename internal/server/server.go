// Copyright (C) 2015-2023 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package server

import (
	"context"
	"crypto/tls"
	"fmt"
	"net/http"
	"os"
	"os/signal"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/hdecarne-github/certmgr/internal/config"
	"github.com/hdecarne-github/certmgr/internal/web"
	"github.com/hdecarne-github/go-certstore"
	"github.com/hdecarne-github/go-log"
	"github.com/rs/zerolog"
)

func Run(config *config.Server) error {
	// setup logger
	logger := log.RootLogger().With().Str("server", config.ServerURL).Logger()
	logger.Info().Msg("starting server...")
	// create/open cert store
	certStoreURI := config.CertStoreURI()
	logger.Info().Msgf("using certificate store '%s'", certStoreURI)
	registry, err := certstore.NewStoreFromURI(certStoreURI, config.StatePath)
	if err != nil {
		return err
	}
	// startup and run server
	server := &serverInstance{
		config:   config,
		registry: registry,
		logger:   &logger,
	}
	return server.setupAndListen()
}

type serverInstance struct {
	config   *config.Server
	registry *certstore.Registry
	logger   *zerolog.Logger
}

func (server *serverInstance) setupAndListen() error {
	useTLS, listen, prefix, err := server.splitServerURL()
	if err != nil {
		return err
	}
	router, err := server.setupRouter(prefix)
	if err != nil {
		return err
	}
	var tlsConfig *tls.Config
	if useTLS {
		tlsCertificate, err := server.loadTLSCertificate()
		if err != nil {
			return err
		}
		tlsConfig = &tls.Config{Certificates: []tls.Certificate{*tlsCertificate}}
	}
	httpServer := &http.Server{
		Addr:      listen,
		Handler:   router,
		TLSConfig: tlsConfig,
	}
	return server.listen(httpServer)
}

const httpPrefix = "http://"
const httpsPrefix = "https://"

func (server *serverInstance) splitServerURL() (bool, string, string, error) {
	remaining := server.config.ServerURL
	var useTLS bool
	if strings.HasPrefix(remaining, httpPrefix) {
		remaining = strings.TrimPrefix(remaining, httpPrefix)
	} else if strings.HasPrefix(remaining, httpsPrefix) {
		useTLS = true
		remaining = strings.TrimPrefix(remaining, httpsPrefix)
	} else {
		return false, "", "", fmt.Errorf("unrecognized server URL '%s'; unknown protocol", server.config.ServerURL)
	}
	remainings := strings.SplitN(remaining, "/", 2)
	listen := remainings[0]
	prefix := "/"
	if len(remainings) == 2 {
		prefix = prefix + remainings[1]
	}
	prefix = strings.TrimSuffix(prefix, "/")
	return useTLS, listen, prefix, nil
}

func (server *serverInstance) setupRouter(prefix string) (*gin.Engine, error) {
	gin.SetMode(gin.ReleaseMode)
	router := gin.New()
	router.Use(server.loggerMiddleware(), gin.Recovery())
	webdocs, err := web.Docs()
	if err != nil {
		return nil, err
	}
	router.NoRoute(server.webdocsMiddleware(webdocs, prefix))
	return router, nil
}

func (server *serverInstance) loadTLSCertificate() (*tls.Certificate, error) {
	if server.config.ServerCRT == "" {
		return nil, fmt.Errorf("no server certificate file defined")
	}
	if server.config.ServerKey == "" {
		return nil, fmt.Errorf("no server key file defined")
	}
	tlsCertificate, err := tls.LoadX509KeyPair(server.config.ServerCRT, server.config.ServerKey)
	if err != nil {
		return nil, fmt.Errorf("failed to load server certificate (cause: %w)", err)
	}
	return &tlsCertificate, nil
}

func (server *serverInstance) listen(httpServer *http.Server) error {
	sigint := make(chan os.Signal, 1)
	signal.Notify(sigint, os.Interrupt)
	sigintCtx, cancelListenAndServe := context.WithCancel(context.Background())
	go func() {
		<-sigint
		server.logger.Info().Msg("SIGINT received; stopping server...")
		cancelListenAndServe()
	}()
	go func() {
		err := httpServer.ListenAndServe()
		if err != http.ErrServerClosed {
			server.logger.Error().Err(err).Msgf("server failure (cause: %s)", err)
		}
	}()
	server.logger.Info().Msg("listening...")
	<-sigintCtx.Done()
	shutdownCtx, cancelShutdown := context.WithTimeout(context.Background(), time.Second)
	defer cancelShutdown()
	err := httpServer.Shutdown(shutdownCtx)
	if err != nil {
		return fmt.Errorf("shutdown failure (cause: %w)", err)
	}
	server.logger.Info().Msg("shutdown complete")
	return nil
}

func (server *serverInstance) loggerMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		if !server.logger.Debug().Enabled() {
			c.Next()
			return
		}
		path := c.Request.URL.Path
		raw := c.Request.URL.RawQuery
		if raw != "" {
			path = path + "?" + raw
		}
		start := time.Now()
		c.Next()
		elapsed := time.Since(start)
		clientIP := c.ClientIP()
		method := c.Request.Method
		status := c.Writer.Status()
		server.logger.Debug().Msgf("%s %s %s - %d (%s)", clientIP, method, path, status, elapsed)
	}
}

func (server *serverInstance) webdocsMiddleware(webdocs http.FileSystem, prefix string) gin.HandlerFunc {
	fileServer := http.FileServer(webdocs)
	if prefix != "" {
		fileServer = http.StripPrefix(prefix, fileServer)
	}
	return func(c *gin.Context) {
		fileServer.ServeHTTP(c.Writer, c.Request)
	}
}
