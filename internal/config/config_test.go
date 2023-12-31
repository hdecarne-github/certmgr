// Copyright (C) 2015-2024 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package config_test

import (
	"errors"
	"os"
	"strings"
	"testing"

	"github.com/hdecarne-github/certmgr"
	"github.com/hdecarne-github/certmgr/internal/config"
	"github.com/hdecarne-github/go-certstore/storage"
	"github.com/stretchr/testify/require"
)

func TestVersionCmd(t *testing.T) {
	runner := &testRunner{}
	// cmdline: certmgr version
	os.Args = []string{os.Args[0], "version"}
	err := certmgr.Run(runner)
	require.ErrorIs(t, err, errors.ErrUnsupported)
	require.Equal(t, 1, runner.VersionCalls)
	require.Equal(t, false, runner.LastVersionDebug)
	// cmdline: certmgr version --debug
	os.Args = []string{os.Args[0], "version", "--debug"}
	err = certmgr.Run(runner)
	require.ErrorIs(t, err, errors.ErrUnsupported)
	require.Equal(t, 2, runner.VersionCalls)
	require.Equal(t, true, runner.LastVersionDebug)
}
func TestServerCmd(t *testing.T) {
	runner := &testRunner{}
	// cmdline: certmgr server --config=testdata/certmgr-test.yaml --debug
	os.Args = []string{os.Args[0], "server", "--config=testdata/certmgr-test.yaml", "--debug"}
	err := certmgr.Run(runner)
	require.ErrorIs(t, err, errors.ErrUnsupported)
	require.Equal(t, 1, runner.ServerCalls)
	require.Equal(t, "http://localhost:10509", runner.LastServerConfig.ServerURL)
	require.True(t, strings.HasSuffix(runner.LastServerConfig.ConfigPath, "/testdata"))
	require.True(t, strings.HasSuffix(runner.LastServerConfig.StatePath, "/lib"))
	require.Equal(t, "acme.yaml", runner.LastServerConfig.ACMEConfig)
}

type testRunner struct {
	VersionCalls     int
	LastVersionDebug bool
	ServerCalls      int
	LastServerConfig config.Server
}

func (runner *testRunner) Version(debug bool) error {
	runner.VersionCalls++
	runner.LastVersionDebug = debug
	return errors.ErrUnsupported
}

func (runner *testRunner) Server(config *config.Server) error {
	runner.ServerCalls++
	runner.LastServerConfig = *config
	return errors.ErrUnsupported
}

func TestDefaults(t *testing.T) {
	defaults := config.Defaults()
	require.NotNil(t, defaults)
	require.Equal(t, "fs://./certstore?cache_ttl=60s&version_limit=10", defaults.ServerConfig.CertStoreURI())
}

func TestLoad(t *testing.T) {
	config, err := config.Load("./testdata/certmgr-test.yaml")
	require.NoError(t, err)
	require.NotNil(t, config)
	require.True(t, strings.HasSuffix(config.ServerConfig.StatePath, "/lib"))
	require.Equal(t, "600s", config.ServerConfig.StoreCacheTTL)
	require.Equal(t, storage.VersionLimit(1), config.ServerConfig.StoreVersionLimit)
}

func TestLoadMissing(t *testing.T) {
	config, err := config.Load("./testdata/certmgr-missing.yaml")
	require.Error(t, err)
	require.Nil(t, config)
}

func TestLoadInvalid(t *testing.T) {
	config, err := config.Load("./testdata/certmgr-invalid.yaml")
	require.Error(t, err)
	require.Nil(t, config)
}
