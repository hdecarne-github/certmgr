// Copyright (C) 2015-2024 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package server

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/hdecarne-github/go-certstore/certs/acme"
)

const localCAName string = "Local"
const remoteCAName string = "Remote"
const acmeCAPrefix string = "ACME:"

func (server *serverInstance) cas(c *gin.Context) {
	response := &CAs{
		CAs: make([]CA, 0, 3),
	}
	response.CAs = append(response.CAs, CA{Name: localCAName})
	response.CAs = append(response.CAs, CA{Name: remoteCAName})
	acmeConfigPath := server.config.ResolveConfigFile(server.config.ACMEConfig)
	server.logger.Debug().Msgf("using ACME config '%s'", acmeConfigPath)
	acmeConfig, err := acme.LoadConfig(acmeConfigPath)
	if err != nil {
		c.AbortWithError(http.StatusInternalServerError, err)
		return
	}
	for _, acmeProvider := range acmeConfig.Providers {
		response.CAs = append(response.CAs, CA{Name: acmeCAPrefix + acmeProvider.Name})
	}
	c.JSON(http.StatusOK, response)
}
