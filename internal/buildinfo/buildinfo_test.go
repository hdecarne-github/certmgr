// Copyright (C) 2015-2024 Holger de Carne and contributors
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

func TestVersion(t *testing.T) {
	versionPattern := regexp.MustCompile(`^(<dev build>|\d+\.\d+\.\d+)$`)
	require.True(t, versionPattern.MatchString(buildinfo.Version()))
}
func TestTimestamp(t *testing.T) {
	timestampPattern := regexp.MustCompile(`^(<dev build>|\d+)$`)
	require.True(t, timestampPattern.MatchString(buildinfo.Timestamp()))
}

func TestFullVersion(t *testing.T) {
	fullVersionPattern := regexp.MustCompile(`^certmgr version (<dev build>|\d+\.\d+\.\d+) \((<dev build>|\d+)\) .*/.*$`)
	require.True(t, fullVersionPattern.MatchString(buildinfo.FullVersion()))
}
