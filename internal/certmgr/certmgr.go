// Copyright (C) 2015-2023 Holger de Carne
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package certmgr

type Runner interface {
	Version() error
}

func Run(runner Runner) error {
	return nil
}
