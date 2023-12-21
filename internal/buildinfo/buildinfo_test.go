// Copyright (C) 2015-2023 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package buildinfo_test

import (
	"regexp"
	"testing"

	"github.com/hdecarne-github/certmgr/internal/buildinfo"
	"github.com/stretchr/testify/require"
)

const undefined = "<dev build>"

func TestVersion(t *testing.T) {
	require.Equal(t, undefined, buildinfo.Version())
}
func TestTimestamp(t *testing.T) {
	require.Equal(t, undefined, buildinfo.Timestamp())
}

func TestFullVersion(t *testing.T) {
	fullVersionPattern := regexp.MustCompile(`^certmgr version <dev build> \(<dev build>\) .*/.*$`)
	require.True(t, fullVersionPattern.Match([]byte(buildinfo.FullVersion())))
}
