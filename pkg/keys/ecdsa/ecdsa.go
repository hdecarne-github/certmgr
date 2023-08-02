// Copyright (C) 2015-2023 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

// Package ecdsa implements ECDSA related key functions.
package ecdsa

import (
	"crypto"
	algorithm "crypto/ecdsa"
	"crypto/elliptic"
	"crypto/rand"

	"github.com/hdecarne-github/certmgr/pkg/keys"
)

// Name of the ECDSA key provider.
const ProviderName = "ECDSA"

// ECDSAKeyPair provides the KeyPair interface for ECDSA keys.
type ECDSAKeyPair struct {
	key *algorithm.PrivateKey
}

// NewECDSAKeyPair creates a new ECDSA key pair for the given curve.
func NewECDSAKeyPair(curve elliptic.Curve) (keys.KeyPair, error) {
	key, err := algorithm.GenerateKey(curve, rand.Reader)
	if err != nil {
		return nil, err
	}
	return &ECDSAKeyPair{key: key}, nil
}

// Public returns the public key of the ECDSA key pair.
func (keypair *ECDSAKeyPair) Public() crypto.PublicKey {
	return keypair.key.Public()
}

// Private returns the private key of the ECDSA key pair.
func (keypair *ECDSAKeyPair) Private() crypto.PrivateKey {
	return keypair.key
}

// ECDSAKeyPairFactory provides the KeyPairFactory interface for ECDSA keys.
type ECDSAKeyPairFactory struct {
	curve elliptic.Curve
}

// NewECDSAKeyPairFactory creates a new ECDSA key pair factory for the given curve.
func NewECDSAKeyPairFactory(curve elliptic.Curve) keys.KeyPairFactory {
	return &ECDSAKeyPairFactory{curve: curve}
}

// Name returns the name of this ECDSA key pair factory.
func (factory *ECDSAKeyPairFactory) Name() string {
	return ProviderName + " " + factory.curve.Params().Name
}

// New generates a new ECDSA key pair
func (factory *ECDSAKeyPairFactory) New() (keys.KeyPair, error) {
	return NewECDSAKeyPair(factory.curve)
}

// StandardKeys returns key pair factories for the standard ECDSA curves (P224, P256, P384, P521).
func StandardKeys() []keys.KeyPairFactory {
	return []keys.KeyPairFactory{
		NewECDSAKeyPairFactory(elliptic.P224()),
		NewECDSAKeyPairFactory(elliptic.P256()),
		NewECDSAKeyPairFactory(elliptic.P384()),
		NewECDSAKeyPairFactory(elliptic.P521()),
	}
}
