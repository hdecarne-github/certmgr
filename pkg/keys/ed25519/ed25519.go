// Copyright (C) 2015-2023 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package ed25519

import (
	"crypto"
	algorithm "crypto/ed25519"
	"crypto/rand"

	"github.com/hdecarne-github/certmgr/pkg/keys"
)

const ProviderName = "ED25519"

type ED25519KeyPair struct {
	public  algorithm.PublicKey
	private algorithm.PrivateKey
}

func NewED25519KeyPair() (keys.KeyPair, error) {
	public, private, err := algorithm.GenerateKey(rand.Reader)
	if err != nil {
		return nil, err
	}
	return &ED25519KeyPair{public: public, private: private}, nil
}

func (keypair *ED25519KeyPair) Public() crypto.PublicKey {
	return keypair.public
}

func (keypair *ED25519KeyPair) Private() crypto.PrivateKey {
	return keypair.private
}

type ED25519KeyPairFactory struct{}

func NewED25519KeyPairFactory() keys.KeyPairFactory {
	return &ED25519KeyPairFactory{}
}

func (factory *ED25519KeyPairFactory) Name() string {
	return ProviderName
}

func (factory *ED25519KeyPairFactory) New() (keys.KeyPair, error) {
	return NewED25519KeyPair()
}

func StandardKeys() []keys.KeyPairFactory {
	return []keys.KeyPairFactory{
		NewED25519KeyPairFactory(),
	}
}
