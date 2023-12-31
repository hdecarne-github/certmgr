// Copyright (C) 2015-2024 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package server

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/hdecarne-github/certmgr/internal/buildinfo"
)

func (server *serverInstance) about(c *gin.Context) {
	response := &AboutInfo{
		Version:   buildinfo.Version(),
		Timestamp: buildinfo.Timestamp(),
	}
	c.JSON(http.StatusOK, response)
}
