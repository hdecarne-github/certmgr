/*
 * Copyright (c) 2015-2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.test.certs.io;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.util.KeyPairResolver;
import de.carne.util.DefaultSet;

/**
 * Test {@link KeyPairResolver} class functionality.
 */
public class KeyPairResolverTest {

	/**
	 * Register BouncyCastle Provider.
	 */
	@BeforeClass
	public static void registerBouncyCastle() {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Test key pair resolving.
	 *
	 * @throws GeneralSecurityException if an error occurs.
	 */
	@Test
	public void testKeyPairResolver() throws GeneralSecurityException {
		DefaultSet<KeyPairAlgorithm> algorithms = KeyPairAlgorithm.getDefaultSet(null, false);
		KeyPairResolver resolver = new KeyPairResolver();

		for (KeyPairAlgorithm algorithm : algorithms) {
			KeyPairGenerator generator = algorithm.getInstance();

			generator.initialize(algorithm.getStandardKeySizes(null).getDefault());

			KeyPair keyPair = generator.generateKeyPair();

			resolver.addPrivateKey(keyPair.getPrivate());
			resolver.addPublicKey(keyPair.getPublic());
		}

		List<Object> resolvedKeyPairs = resolver.resolve();

		Assert.assertEquals(algorithms.size(), resolvedKeyPairs.size());
	}

}
