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
package de.carne.certmgr.certs.x509;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;

import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.check.Nullable;
import de.carne.util.logging.Log;

/**
 * Utility class providing security key related functions.
 */
public final class KeyHelper {

	private KeyHelper() {
		// Make sure this class is not instantiated from outside
	}

	private static Log LOG = new Log();

	/**
	 * Get the public key's algorithm.
	 *
	 * @param publicKey The public key to get the algorithm for.
	 * @return The public key's algorithm or {@code null} if the key size is indeterminable.
	 */
	@Nullable
	public static KeyPairAlgorithm getKeyAlg(PublicKey publicKey) {
		return getKeyAlg(publicKey.getAlgorithm());
	}

	/**
	 * Get the key pair algorithm for a specific algorithm name.
	 *
	 * @param algorithm The algorithm name to get the key pair algorithm for.
	 * @return The public key's algorithm or {@code null} if the key size is indeterminable.
	 */
	@Nullable
	public static KeyPairAlgorithm getKeyAlg(String algorithm) {
		return KeyPairAlgorithm.getDefaultSet(algorithm, false).getDefault();
	}

	/**
	 * Get the public key's key size.
	 *
	 * @param publicKey The public key to get the key size for.
	 * @return The public key's key size or {@code null} if the key size is indeterminable.
	 */
	@Nullable
	public static Integer getKeySize(PublicKey publicKey) {
		Integer keySize = null;

		if (publicKey instanceof RSAPublicKey) {
			RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

			keySize = rsaPublicKey.getModulus().bitLength();
		} else if (publicKey instanceof ECPublicKey) {
			ECPublicKey ecPublicKey = (ECPublicKey) publicKey;

			keySize = ecPublicKey.getParams().getCurve().getField().getFieldSize();
		}
		return keySize;
	}

	/**
	 * Get the public key's string representation.
	 *
	 * @param publicKey The public key to format.
	 * @return The public key's string representation.
	 */
	public static String toString(PublicKey publicKey) {
		StringBuilder buffer = new StringBuilder();

		buffer.append(publicKey.getAlgorithm());
		if (publicKey instanceof RSAPublicKey) {
			RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

			buffer.append("/").append(rsaPublicKey.getModulus().bitLength());
		} else if (publicKey instanceof ECPublicKey) {
			ECPublicKey ecPublicKey = (ECPublicKey) publicKey;

			buffer.append("/").append(ecPublicKey.getParams().getCurve().getField().getFieldSize());
		}
		return buffer.toString();
	}

	/**
	 * Generate a Key object.
	 *
	 * @param algorithm The key pair algorithm to use.
	 * @param keySize The key size to use.
	 * @return The generated Key object.
	 * @throws IOException if an error occurs during generation.
	 */
	public static KeyPair generateKey(KeyPairAlgorithm algorithm, int keySize) throws IOException {
		KeyPair keyPair;

		try {
			LOG.info("Key pair generation {0}/{1} started...", algorithm, Integer.toString(keySize));

			KeyPairGenerator keyGenerator = algorithm.getInstance();

			keyGenerator.initialize(keySize);
			keyPair = keyGenerator.generateKeyPair();

			LOG.info("Key pair generation {0} done...", KeyHelper.toString(keyPair.getPublic()));
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return keyPair;
	}

}
