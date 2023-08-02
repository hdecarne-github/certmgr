// Copyright (C) 2015-2023 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

// Package registry provides generic access to all key providers.
package registry

import (
	"github.com/hdecarne-github/certmgr/pkg/keys"
	"github.com/hdecarne-github/certmgr/pkg/keys/ecdsa"
	"github.com/hdecarne-github/certmgr/pkg/keys/ed25519"
	"github.com/hdecarne-github/certmgr/pkg/keys/rsa"
)

var providerNames = []string{}
var providerStandardKeys = make(map[string]func() []keys.KeyPairFactory, 0)
var standardKeys = make(map[string]keys.KeyPairFactory, 0)

// KeyProviders returns the known key providers (ECDSA, ED25519, RSA).
func KeyProviders() []string {
	names := providerNames
	return names
}

// StandardKeys returns the standard keys for the given key provider.
func StandardKeys(name string) []keys.KeyPairFactory {
	return providerStandardKeys[name]()
}

// StandardKey returns the key pair factory corresponding to the given key pair factory name.
func StandardKey(name string) keys.KeyPairFactory {
	return standardKeys[name]
}

func init() {
	providerNames = append(providerNames, ecdsa.ProviderName, ed25519.ProviderName, rsa.ProviderName)
	providerStandardKeys[ecdsa.ProviderName] = ecdsa.StandardKeys
	for _, key := range ecdsa.StandardKeys() {
		standardKeys[key.Name()] = key
	}
	providerStandardKeys[ed25519.ProviderName] = ed25519.StandardKeys
	for _, key := range ed25519.StandardKeys() {
		standardKeys[key.Name()] = key
	}
	providerStandardKeys[rsa.ProviderName] = rsa.StandardKeys
	for _, key := range rsa.StandardKeys() {
		standardKeys[key.Name()] = key
	}
}
