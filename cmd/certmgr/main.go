// Copyright (C) 2015-2023 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package main

import (
	"github.com/hdecarne-github/certmgr"
)

func main() {
	_ = certmgr.Run(certmgr.NewDefaultRunner())
}
