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
package de.carne.certmgr.test.certs;

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
	public static void setUpBeforeClass() {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Test the algorithm provisioning.
	 */
	@Test
	public void testGetAll() {
		for (KeyPairAlgorithm keyPairAlgorithm : KeyPairAlgorithm.getAll(false)) {
			Set<SignatureAlgorithm> standardAlgorithms = SignatureAlgorithm.getAll(keyPairAlgorithm.algorithm(), false);

			System.out.println(keyPairAlgorithm.algorithm() + " standard algorithms:");
			for (SignatureAlgorithm standardAlgorithm : standardAlgorithms) {
				System.out.println(standardAlgorithm);
			}
			Assert.assertTrue(standardAlgorithms.size() > 0);
		}
		for (KeyPairAlgorithm keyPairAlgorithm : KeyPairAlgorithm.getAll(true)) {
			Set<SignatureAlgorithm> expertAlgorithms = SignatureAlgorithm.getAll(keyPairAlgorithm.algorithm(), true);

			System.out.println(keyPairAlgorithm.algorithm() + " expert algorithms:");
			for (SignatureAlgorithm expertAlgorithm : expertAlgorithms) {
				System.out.println(expertAlgorithm);
			}
			Assert.assertTrue(expertAlgorithms.size() > 0);
		}
	}

}
