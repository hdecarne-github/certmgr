// Copyright (C) 2015-2023 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package config

import (
	_ "embed"
	"fmt"
	"os"
	"path/filepath"

	"github.com/alecthomas/kong"
	"github.com/mattn/go-isatty"
	"gopkg.in/yaml.v3"
)

type Runner interface {
	Version(debug bool) error
	Server(config *Server) error
}

func EvalAndRun(runner Runner) error {
	context := &evalContext{runner: runner}
	return kong.Parse(context).Run(context)
}

type evalContext struct {
	cmdLine
	runner Runner
}

type cmdLine struct {
	Version versionCmd `cmd:"" help:"Display version info and exit"`
	Server  serverCmd  `cmd:"" help:"Run server"`
	Verbose bool       `help:"Enable verbose output"`
	Debug   bool       `help:"Enable debug output"`
	ANSI    bool       `help:"Force ANSI colored output"`
}

type versionCmd struct{}

func (cmd *versionCmd) Run(context *evalContext) error {
	return context.runner.Version(context.Debug)
}

type serverCmd struct {
	Config    string `help:"The configuration file to use (defaults to /etc/certmgr/certmgr.yaml)"`
	ServerURL string `help:"The server URL to listen on (defaults to http://localhost:10509)"`
	StatePath string `help:"The state path to use (defaults to /var/lib/certmgr)"`
}

func (cmd *serverCmd) Run(context *evalContext) error {
	config, err := cmd.loadConfig()
	if err != nil {
		return err
	}
	config.applyServerCmdLine(&context.cmdLine)
	return context.runner.Server(&config.ServerConfig)
}

const defaultServerConfig = "/etc/certmgr/certmgr.yaml"

func (cmd *serverCmd) loadConfig() (*Global, error) {
	config := defaultServerConfig
	if cmd.Config != "" {
		config = cmd.Config
	}
	return Load(config)
}

type Global struct {
	Debug        bool   `yaml:"debug"`
	Verbose      bool   `yaml:"verbose"`
	ANSI         bool   `yaml:"ansi"`
	ServerConfig Server `yaml:"server"`
	CLIConfig    CLI    `yaml:"cli"`
}

func (config *Global) applyRuntimeDefaults() {
	config.ANSI = ansiDefault(os.Stdout)
}

func (config *Global) applyConfigPath(configPath string) {
	config.ServerConfig.ConfigPath = configPath
	config.ServerConfig.StatePath = resolveRelativePath(configPath, config.ServerConfig.StatePath)
	config.ServerConfig.ConfigPath = resolveRelativePath(configPath, config.ServerConfig.ConfigPath)
}

func (config *Global) applyGlobalCmdLine(cmd *cmdLine) {
	if cmd.Verbose {
		config.Verbose = true
	}
	if cmd.Debug {
		config.Debug = true
	}
	if cmd.ANSI {
		config.ANSI = true
	}
}

func (config *Global) applyServerCmdLine(cmd *cmdLine) {
	config.applyGlobalCmdLine(cmd)
	if cmd.Server.ServerURL != "" {
		config.ServerConfig.ServerURL = cmd.Server.ServerURL
	}
	if cmd.Server.StatePath != "" {
		config.ServerConfig.StatePath = cmd.Server.StatePath
	}
}

type Server struct {
	ConfigPath string `yaml:"-"`
	ServerURL  string `yaml:"server_url"`
	StatePath  string `yaml:"state_path"`
	ACMEConfig string `yaml:"acme_config"`
}

type CLI struct {
	ServerURL string `yaml:"server_url"`
}

//go:embed global_defaults.yaml
var globalDefaults []byte

func Defaults() *Global {
	defaults := &Global{}
	err := yaml.Unmarshal(globalDefaults, defaults)
	if err != nil {
		panic(err)
	}
	defaults.applyRuntimeDefaults()
	return defaults
}

func Load(path string) (*Global, error) {
	configBytes, err := os.ReadFile(path)
	if err != nil {
		return nil, fmt.Errorf("failed to read configuration file '%s' (cause: %w)", path, err)
	}
	config := Defaults()
	err = yaml.Unmarshal(configBytes, config)
	if err != nil {
		return nil, fmt.Errorf("failed to parse configuration file '%s' (cause: %w)", path, err)
	}
	configPath, err := filepath.Abs(filepath.Dir(path))
	if err != nil {
		return nil, fmt.Errorf("failed to resolve configuration file path '%s' (cause: %w)", path, err)
	}
	config.applyConfigPath(configPath)
	return config, nil
}

func resolveRelativePath(basePath string, path string) string {
	if filepath.IsAbs(path) {
		return path
	}
	return filepath.Join(basePath, path)
}

func ansiDefault(out *os.File) bool {
	return isatty.IsTerminal(out.Fd()) || isatty.IsCygwinTerminal(out.Fd())
}
