// Copyright (C) 2015-2023 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package buildinfo

import (
	"fmt"
	"runtime"
)

const undefined = "<dev build>"

var version = undefined
var timestamp = undefined

func Version() string {
	return version
}

func Timestamp() string {
	return timestamp
}

func FullVersion() string {
	return fmt.Sprintf("certmgr version %s (%s) %s/%s", Version(), Timestamp(), runtime.GOOS, runtime.GOARCH)
}
