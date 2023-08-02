// Copyright (C) 2015-2023 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

// Package ed25519 implements ED25519 related key functions.
package ed25519

import (
	"crypto"
	algorithm "crypto/ed25519"
	"crypto/rand"

	"github.com/hdecarne-github/certmgr/pkg/keys"
)

// Name of the ED25519 key provider.
const ProviderName = "ED25519"

// ED25519KeyPair provides the KeyPair interface for ED25519 keys.
type ED25519KeyPair struct {
	public  algorithm.PublicKey
	private algorithm.PrivateKey
}

// NewED25519KeyPair creates a new ED25519 key pair.
func NewED25519KeyPair() (keys.KeyPair, error) {
	public, private, err := algorithm.GenerateKey(rand.Reader)
	if err != nil {
		return nil, err
	}
	return &ED25519KeyPair{public: public, private: private}, nil
}

// Public returns the public key of the ED25519 key pair.
func (keypair *ED25519KeyPair) Public() crypto.PublicKey {
	return keypair.public
}

// Private returns the private key of the ED25519 key pair.
func (keypair *ED25519KeyPair) Private() crypto.PrivateKey {
	return keypair.private
}

// ED25519KeyPairFactory provides the KeyPairFactory interface for ED25519 keys.
type ED25519KeyPairFactory struct{}

// NewED25519KeyPairFactory creates a new ED25519 key pair factory.
func NewED25519KeyPairFactory() keys.KeyPairFactory {
	return &ED25519KeyPairFactory{}
}

// Name returns the name of this ED25519 key pair factory.
func (factory *ED25519KeyPairFactory) Name() string {
	return ProviderName
}

// New generates a new ED25519 key pair
func (factory *ED25519KeyPairFactory) New() (keys.KeyPair, error) {
	return NewED25519KeyPair()
}

// StandardKeys returns the standard ED25519 key pair factory.
func StandardKeys() []keys.KeyPairFactory {
	return []keys.KeyPairFactory{
		NewED25519KeyPairFactory(),
	}
}
