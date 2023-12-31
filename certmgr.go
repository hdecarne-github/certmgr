// Copyright (C) 2015-2024 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package certmgr

import (
	"fmt"
	module "runtime/debug"

	"github.com/hdecarne-github/certmgr/internal/buildinfo"
	"github.com/hdecarne-github/certmgr/internal/config"
	"github.com/hdecarne-github/certmgr/internal/server"
)

func Run(runner config.Runner) error {
	return config.EvalAndRun(runner)
}

func NewDefaultRunner() config.Runner {
	return &defaultRunner{}
}

type defaultRunner struct{}

func (runner *defaultRunner) Version(debug bool) error {
	fmt.Println(buildinfo.FullVersion())
	if debug {
		buildinfo, ok := module.ReadBuildInfo()
		if ok {
			fmt.Println("build info:")
			fmt.Print(buildinfo.String())
		}
	}
	return nil
}

func (runner *defaultRunner) Server(config *config.Server) error {
	return server.Run(config)
}
