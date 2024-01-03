// Copyright (C) 2015-2024 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package server

import (
	"crypto/x509"
	"fmt"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/hdecarne-github/go-certstore"
	"github.com/hdecarne-github/go-certstore/certs"
	"github.com/hdecarne-github/go-certstore/keys"
)

func (server *serverInstance) generateLocal(c *gin.Context) {
	request := &GenerateLocal{}
	err := c.BindJSON(request)
	if err != nil {
		return
	}
	err = request.validate()
	if err != nil {
		c.AbortWithError(http.StatusBadRequest, err)
		return
	}
	template, err := request.toTemplate()
	if err != nil {
		c.AbortWithError(http.StatusBadRequest, err)
		return
	}
	keyPairFactory, err := server.resolveKeyPairFactory(request.KeyType)
	if err != nil {
		c.AbortWithError(http.StatusBadRequest, err)
		return
	}
	issuerEntry, err := server.resolveIssuer(request.Issuer)
	if err != nil {
		c.AbortWithError(http.StatusBadRequest, err)
		return
	}
	var factory certs.CertificateFactory
	if issuerEntry != nil {
		factory = certs.NewLocalCertificateFactory(template, keyPairFactory, issuerEntry.Certificate(), issuerEntry.Key(c.ClientIP()))
	} else {
		factory = certs.NewLocalCertificateFactory(template, keyPairFactory, nil, nil)
	}
	entryName, err := server.registry.CreateCertificate(request.Name, factory, c.ClientIP())
	if err != nil {
		c.AbortWithError(http.StatusInternalServerError, err)
		return
	}
	entry, err := server.registry.Entry(entryName)
	if err != nil {
		c.AbortWithError(http.StatusInternalServerError, err)
		return
	}
	response := newEntry(entry)
	c.JSON(http.StatusOK, response)
}

func (server *serverInstance) generateRemote(c *gin.Context) {
	request := &GenerateRemote{}
	err := c.BindJSON(request)
	if err != nil {
		return
	}
	err = request.validate()
	if err != nil {
		c.AbortWithError(http.StatusBadRequest, err)
		return
	}
	template, err := request.toTemplate()
	if err != nil {
		c.AbortWithError(http.StatusBadRequest, err)
		return
	}
	keyPairFactory, err := server.resolveKeyPairFactory(request.KeyType)
	if err != nil {
		c.AbortWithError(http.StatusBadRequest, err)
		return
	}
	factory := certs.NewRemoteCertificateRequestFactory(template, keyPairFactory)
	entryName, err := server.registry.CreateCertificateRequest(request.Name, factory, c.ClientIP())
	if err != nil {
		c.AbortWithError(http.StatusInternalServerError, err)
		return
	}
	entry, err := server.registry.Entry(entryName)
	if err != nil {
		c.AbortWithError(http.StatusInternalServerError, err)
		return
	}
	response := newEntry(entry)
	c.JSON(http.StatusOK, response)
}

func (server *serverInstance) generateACME(c *gin.Context) {
	request := &GenerateACME{}
	err := c.BindJSON(request)
	if err != nil {
		return
	}
	err = request.validate()
	if err != nil {
		c.AbortWithError(http.StatusBadRequest, err)
		return
	}
	config, err := server.loadACMEConfig()
	if err != nil {
		c.AbortWithError(http.StatusInternalServerError, err)
		return
	}
	provider, err := server.acmeProviderFromCA(request.CA)
	if err != nil {
		c.AbortWithError(http.StatusBadRequest, err)
		return
	}
	acmeRequest, err := config.ResolveCertificateRequest(request.Domains, provider)
	if err != nil {
		c.AbortWithError(http.StatusBadRequest, err)
		return
	}
	keyPairFactory, err := server.resolveKeyPairFactory(request.KeyType)
	if err != nil {
		c.AbortWithError(http.StatusBadRequest, err)
		return
	}
	factory := certs.NewACMECertificateFactory(acmeRequest, keyPairFactory)
	entryName, err := server.registry.CreateCertificate(request.Name, factory, c.ClientIP())
	if err != nil {
		c.AbortWithError(http.StatusInternalServerError, err)
		return
	}
	entry, err := server.registry.Entry(entryName)
	if err != nil {
		c.AbortWithError(http.StatusInternalServerError, err)
		return
	}
	response := newEntry(entry)
	c.JSON(http.StatusOK, response)
}

func (server *serverInstance) resolveKeyPairFactory(keyType string) (keys.KeyPairFactory, error) {
	keyAlg, err := keys.AlgorithmFromString(keyType)
	if err != nil {
		return nil, err
	}
	keyPairFactory := keyAlg.NewKeyPairFactory()
	return keyPairFactory, nil
}

func (server *serverInstance) resolveIssuer(name string) (*certstore.RegistryEntry, error) {
	if name == "" {
		return nil, nil
	}
	entry, err := server.registry.Entry(name)
	if err != nil {
		return nil, err
	}
	if !entry.CanIssue(x509.KeyUsageCertSign) {
		return nil, fmt.Errorf("invalid issuer: '%s'", name)
	}
	return entry, nil
}
