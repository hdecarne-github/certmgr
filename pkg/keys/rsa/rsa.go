// Copyright (C) 2015-2023 Holger de Carne
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package rsa

import (
	"crypto"
	"crypto/rand"
	algorithm "crypto/rsa"
	"strconv"

	"github.com/hdecarne-github/certmgr/pkg/keys"
)

const ProviderName = "RSA"

type RSAKeyPair struct {
	key *algorithm.PrivateKey
}

func NewRSAKeyPair(bits int) (keys.KeyPair, error) {
	key, err := algorithm.GenerateKey(rand.Reader, bits)
	if err != nil {
		return nil, err
	}
	return &RSAKeyPair{key: key}, nil
}

func (keypair *RSAKeyPair) Public() crypto.PublicKey {
	return &keypair.key.PublicKey
}

func (keypair *RSAKeyPair) Private() crypto.PrivateKey {
	return keypair.key
}

type RSAKeyPairFactory struct {
	bits int
}

func NewRSAKeyPairFactory(bits int) keys.KeyPairFactory {
	return &RSAKeyPairFactory{bits: bits}
}

func (factory *RSAKeyPairFactory) Name() string {
	return ProviderName + " " + strconv.Itoa(factory.bits)
}

func (factory *RSAKeyPairFactory) New() (keys.KeyPair, error) {
	return NewRSAKeyPair(factory.bits)
}

func StandardKeys() []keys.KeyPairFactory {
	return []keys.KeyPairFactory{
		NewRSAKeyPairFactory(2048),
		NewRSAKeyPairFactory(3072),
		NewRSAKeyPairFactory(4096),
	}
}
