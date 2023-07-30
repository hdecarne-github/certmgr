// Copyright (C) 2015-2023 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package registry

import (
	"testing"

	"github.com/stretchr/testify/require"
)

func TestRegistry(t *testing.T) {
	for _, providerName := range KeyProviders() {
		standardKeys := StandardKeys(providerName)
		require.NotNil(t, standardKeys)
		require.NotEqual(t, 0, len(standardKeys))
		for _, standardKey := range standardKeys {
			key := StandardKey(standardKey.Name())
			require.NotNil(t, key)
			require.Equal(t, standardKey.Name(), key.Name())
		}
	}
}
