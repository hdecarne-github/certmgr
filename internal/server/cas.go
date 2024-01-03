// Copyright (C) 2015-2024 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package server

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

const localCAName string = "Local"
const remoteCAName string = "Remote"

func (server *serverInstance) cas(c *gin.Context) {
	response := &CAs{
		CAs: make([]CA, 0, 3),
	}
	response.CAs = append(response.CAs, CA{Name: localCAName})
	response.CAs = append(response.CAs, CA{Name: remoteCAName})
	acmeConfig, err := server.loadACMEConfig()
	if err != nil {
		c.AbortWithError(http.StatusInternalServerError, err)
		return
	}
	for _, acmeProvider := range acmeConfig.Providers {
		response.CAs = append(response.CAs, CA{Name: server.caFromACMEProvider(acmeProvider.Name)})
	}
	c.JSON(http.StatusOK, response)
}
