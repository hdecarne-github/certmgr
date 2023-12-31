// Copyright (C) 2015-2024 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package web

import (
	"embed"
	"fmt"
	"io/fs"
	"net/http"
)

//go:embed all:build/*
var docs embed.FS

func Docs() (http.FileSystem, error) {
	docs, err := fs.Sub(docs, "build")
	if err != nil {
		return nil, fmt.Errorf("unexpected web document structure (cause: %w)", err)
	}
	return http.FS(docs), nil
}
