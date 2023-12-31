// Copyright (C) 2015-2024 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package server

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

func (server *serverInstance) entries(c *gin.Context) {
	request := &EntriesFilter{}
	err := c.BindQuery(&request)
	if err != nil {
		return
	}
	entries, err := server.registry.Entries()
	if err != nil {
		c.AbortWithError(http.StatusInternalServerError, err)
		return
	}
	response := &Entries{
		Entries: make([]Entry, 0),
		Start:   request.Start,
	}
	for {
		entry, err := entries.Next()
		if err != nil {
			c.AbortWithError(http.StatusInternalServerError, err)
			return
		}
		if entry == nil {
			break
		}
		if response.Total >= response.Start && (request.Limit <= 0 || (response.Start-response.Total) <= request.Limit) {
			response.Entries = append(response.Entries, *newEntry(entry))
		}
		response.Total++
	}
	c.JSON(http.StatusOK, response)
}
