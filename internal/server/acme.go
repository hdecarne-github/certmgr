// Copyright (C) 2015-2024 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package server

import (
	"fmt"
	"strings"

	"github.com/hdecarne-github/go-certstore/certs/acme"
)

func (server *serverInstance) loadACMEConfig() (*acme.Config, error) {
	configPath := server.config.ResolveConfigFile(server.config.ACMEConfig)
	server.logger.Debug().Msgf("using ACME config '%s'", configPath)
	config, err := acme.LoadConfig(configPath)
	if err != nil {
		return nil, err
	}
	return config, nil
}

const acmeCAPrefix string = "ACME:"

func (server *serverInstance) caFromACMEProvider(name string) string {
	return acmeCAPrefix + name
}

func (server *serverInstance) acmeProviderFromCA(ca string) (string, error) {
	if !strings.HasPrefix(ca, acmeCAPrefix) {
		return "", fmt.Errorf("unexpected ACME CA: '%s'", ca)
	}
	return string(ca[len(acmeCAPrefix):]), nil
}
