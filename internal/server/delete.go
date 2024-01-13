// Copyright (C) 2015-2024 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package server

import (
	"errors"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/hdecarne-github/go-certstore/storage"
)

func (server *serverInstance) delete(c *gin.Context) {
	name := c.Param("name")
	err := server.registry.Delete(name, c.ClientIP())
	if errors.Is(err, storage.ErrNotExist) {
		c.AbortWithError(http.StatusNotFound, err)
		return
	} else if err != nil {
		c.AbortWithError(http.StatusInternalServerError, err)
		return
	}
	c.Status(http.StatusOK)
}
