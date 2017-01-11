/*
 * Copyright (c) 2015-2017 Holger de Carne and contributors, All Rights Reserved.
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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.security.SignatureAlgorithm;

/**
 * Test {@link SignatureAlgorithm} class functionality.
 */
public class SignatureAlgorithmTest {

	/**
	 * Register BouncyCastle Provider.
	 */
	@BeforeClass
	public static void registerBouncyCastle() {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Test the algorithm provisioning.
	 */
	@Test
	public void testGetDefaultSet() {
		for (KeyPairAlgorithm keyPairAlgorithm : KeyPairAlgorithm.getDefaultSet(null, false)) {
			Set<SignatureAlgorithm> standardAlgorithms = SignatureAlgorithm.getDefaultSet(keyPairAlgorithm.algorithm(),
					null, false);

			System.out.println(keyPairAlgorithm.algorithm() + " standard algorithms:");
			for (SignatureAlgorithm standardAlgorithm : standardAlgorithms) {
				System.out.println(standardAlgorithm);
			}
			Assert.assertTrue(standardAlgorithms.size() > 0);
		}
		for (KeyPairAlgorithm keyPairAlgorithm : KeyPairAlgorithm.getDefaultSet(null, true)) {
			Set<SignatureAlgorithm> expertAlgorithms = SignatureAlgorithm.getDefaultSet(keyPairAlgorithm.algorithm(),
					null, true);

			System.out.println(keyPairAlgorithm.algorithm() + " expert algorithms:");
			for (SignatureAlgorithm expertAlgorithm : expertAlgorithms) {
				System.out.println(expertAlgorithm);
			}
			Assert.assertTrue(expertAlgorithms.size() > 0);
		}
	}

}
