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

import java.security.GeneralSecurityException;
import java.security.InvalidParameterException;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.util.Set;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.util.DefaultSet;

/**
 * Test {@link KeyPairAlgorithm} class functionality.
 */
public class KeyPairAlgorithmTest {

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
	public void testGetDefaultSet() {
		Set<KeyPairAlgorithm> standardAlgorithms = KeyPairAlgorithm.getDefaultSet(null, false);

		System.out.println("Standard algorithms:");
		for (KeyPairAlgorithm standardAlgorithm : standardAlgorithms) {
			System.out.println(standardAlgorithm);
		}

		Set<KeyPairAlgorithm> expertAlgorithms = KeyPairAlgorithm.getDefaultSet(null, true);

		System.out.println("Expert algorithms:");
		for (KeyPairAlgorithm expertAlgorithm : expertAlgorithms) {
			System.out.println(expertAlgorithm);
		}
		// The standard ones should always be available
		Assert.assertTrue(standardAlgorithms.size() == 3);
		// The exact number of expert ones will vary, but should always be more
		Assert.assertTrue(expertAlgorithms.size() > standardAlgorithms.size());
	}

	/**
	 * Test whether the standard key sizes are valid.
	 */
	@Test
	public void testKeySizes() {
		DefaultSet<KeyPairAlgorithm> algorithms = KeyPairAlgorithm.getDefaultSet(null, true);

		Assert.assertTrue(algorithms.contains(algorithms.getDefault()));
		for (KeyPairAlgorithm algorithm : algorithms) {
			System.out.println("Algorithm: " + algorithm);
			try {
				KeyPairGenerator generator = algorithm.getInstance();
				DefaultSet<Integer> keySizes = algorithm.getStandardKeySizes(null);

				if (keySizes.size() > 0) {
					Integer defaultKeySize = keySizes.getDefault();

					Assert.assertTrue(keySizes.contains(defaultKeySize));
					for (Integer keySize : keySizes) {
						System.out.println("Key size: " + keySize);
						try {
							generator.initialize(keySize);
						} catch (InvalidParameterException e) {
							Assert.fail("Cannot get initialize algorithm '" + algorithm + "' with key size " + keySize
									+ ": " + e.getMessage());
						}
					}
				}
			} catch (GeneralSecurityException e) {
				Assert.fail("Cannot get instance for algorithm '" + algorithm + "': " + e.getMessage());
			}
		}
	}

}
