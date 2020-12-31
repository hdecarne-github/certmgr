/*
 * Copyright (c) 2015-2021 Holger de Carne and contributors, All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.carne.certmgr.test.certs.security;

import java.security.Security;
import java.util.Set;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import de.carne.certmgr.certs.security.PlatformKeyStore;

/**
 * Test {@link PlatformKeyStore} class functionality.
 */
public class PlatformKeyStoreTest {

	/**
	 * Register BouncyCastle Provider.
	 */
	@BeforeClass
	public static void registerBouncyCastle() {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Test the key store provisioning.
	 */
	@Test
	public void testGetDefaultSet() {
		Set<PlatformKeyStore> platformKeyStores = PlatformKeyStore.getDefaultSet();

		System.out.println("Platform key stores:");
		for (PlatformKeyStore platformKeyStore : platformKeyStores) {
			System.out.println(platformKeyStore);
		}
	}

}
