// Copyright (C) 2015-2023 Holger de Carne
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package keys

import (
	"crypto"
)

type KeyPair interface {
	Public() crypto.PublicKey
	Private() crypto.PrivateKey
}

type KeyPairFactory interface {
	Name() string
	New() (KeyPair, error)
}
