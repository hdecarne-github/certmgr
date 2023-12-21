// Copyright (C) 2015-2023 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package web

import (
	"embed"
	"io/fs"
)

//go:embed all:* build/*
var docs embed.FS

func Docs() (fs.FS, error) {
	return fs.Sub(docs, "build")
}
