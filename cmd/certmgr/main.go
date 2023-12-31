// Copyright (C) 2015-2024 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package main

import (
	"fmt"

	"github.com/hdecarne-github/certmgr"
)

func main() {
	err := certmgr.Run(certmgr.NewDefaultRunner())
	if err != nil {
		fmt.Printf("certmgr command failed: %s\n", err)
	}
}
