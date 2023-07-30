// Copyright (C) 2015-2023 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package keys

import (
	"crypto/ecdsa"
	"crypto/ed25519"
	"crypto/elliptic"
	"crypto/rand"
	"crypto/rsa"
	"testing"

	"github.com/stretchr/testify/require"
)

func TestEqual(t *testing.T) {
	ecdsaPrivateKey, err := ecdsa.GenerateKey(elliptic.P224(), rand.Reader)
	require.NoError(t, err)
	ed25519PublicKey, ed25519PrivateKey, err := ed25519.GenerateKey(rand.Reader)
	require.NoError(t, err)
	rsaPrivateKey, err := rsa.GenerateKey(rand.Reader, 2048)
	require.NoError(t, err)

	// ecdsa
	require.True(t, PrivatesEqual(ecdsaPrivateKey, ecdsaPrivateKey))
	require.False(t, PrivatesEqual(ecdsaPrivateKey, ed25519PrivateKey))
	require.False(t, PrivatesEqual(ecdsaPrivateKey, rsaPrivateKey))

	require.True(t, PublicsEqual(&ecdsaPrivateKey.PublicKey, &ecdsaPrivateKey.PublicKey))
	require.False(t, PublicsEqual(&ecdsaPrivateKey.PublicKey, ed25519PublicKey))
	require.False(t, PublicsEqual(&ecdsaPrivateKey.PublicKey, &rsaPrivateKey.PublicKey))

	// ed25519
	require.False(t, PrivatesEqual(ed25519PrivateKey, ecdsaPrivateKey))
	require.True(t, PrivatesEqual(ed25519PrivateKey, ed25519PrivateKey))
	require.False(t, PrivatesEqual(ed25519PrivateKey, rsaPrivateKey))

	require.False(t, PublicsEqual(ed25519PublicKey, &ecdsaPrivateKey.PublicKey))
	require.True(t, PublicsEqual(ed25519PublicKey, ed25519PublicKey))
	require.False(t, PublicsEqual(ed25519PublicKey, &rsaPrivateKey.PublicKey))

	// rsa
	require.False(t, PrivatesEqual(rsaPrivateKey, ecdsaPrivateKey))
	require.False(t, PrivatesEqual(rsaPrivateKey, ed25519PrivateKey))
	require.True(t, PrivatesEqual(rsaPrivateKey, rsaPrivateKey))

	require.False(t, PublicsEqual(&rsaPrivateKey.PublicKey, &ecdsaPrivateKey.PublicKey))
	require.False(t, PublicsEqual(&rsaPrivateKey.PublicKey, ed25519PublicKey))
	require.True(t, PublicsEqual(&rsaPrivateKey.PublicKey, &rsaPrivateKey.PublicKey))
}
