// Copyright (C) 2015-2023 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package ecdsa

import (
	"crypto"
	algorithm "crypto/ecdsa"
	"crypto/elliptic"
	"crypto/rand"

	"github.com/hdecarne-github/certmgr/pkg/keys"
)

const ProviderName = "ECDSA"

type ECDSAKeyPair struct {
	key *algorithm.PrivateKey
}

func NewECDSAKeyPair(curve elliptic.Curve) (keys.KeyPair, error) {
	key, err := algorithm.GenerateKey(curve, rand.Reader)
	if err != nil {
		return nil, err
	}
	return &ECDSAKeyPair{key: key}, nil
}

func (keypair *ECDSAKeyPair) Public() crypto.PublicKey {
	return keypair.key.Public()
}

func (keypair *ECDSAKeyPair) Private() crypto.PrivateKey {
	return keypair.key
}

type ECDSAKeyPairFactory struct {
	curve elliptic.Curve
}

func NewECDSAKeyPairFactory(curve elliptic.Curve) keys.KeyPairFactory {
	return &ECDSAKeyPairFactory{curve: curve}
}

func (factory *ECDSAKeyPairFactory) Name() string {
	return ProviderName + " " + factory.curve.Params().Name
}

func (factory *ECDSAKeyPairFactory) New() (keys.KeyPair, error) {
	return NewECDSAKeyPair(factory.curve)
}

func StandardKeys() []keys.KeyPairFactory {
	return []keys.KeyPairFactory{
		NewECDSAKeyPairFactory(elliptic.P224()),
		NewECDSAKeyPairFactory(elliptic.P256()),
		NewECDSAKeyPairFactory(elliptic.P384()),
		NewECDSAKeyPairFactory(elliptic.P521()),
	}
}
