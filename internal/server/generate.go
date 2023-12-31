// Copyright (C) 2015-2024 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package server

import (
	"net/http"

	"github.com/gin-gonic/gin"
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
	keyAlg, err := keys.AlgorithmFromString(request.KeyType)
	if err != nil {
		c.AbortWithError(http.StatusBadRequest, err)
		return
	}
	keyPairFactory := keyAlg.NewKeyPairFactory()
	factory := certs.NewLocalCertificateFactory(template, keyPairFactory, nil, nil)
	name, err := server.registry.CreateCertificate(request.Name, factory, c.ClientIP())
	if err != nil {
		c.AbortWithError(http.StatusInternalServerError, err)
		return
	}
	entry, err := server.registry.Entry(name)
	if err != nil {
		c.AbortWithError(http.StatusInternalServerError, err)
		return
	}
	response := newEntry(entry)
	c.JSON(http.StatusOK, response)
}
