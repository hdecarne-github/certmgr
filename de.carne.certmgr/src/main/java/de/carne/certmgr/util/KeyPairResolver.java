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
package de.carne.certmgr.util;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.carne.util.Exceptions;

/**
 * Utility class for paring public and private key objects.
 */
public final class KeyPairResolver {

	private static final Set<String> PREFERRED_DIGESTS = new HashSet<>(Arrays.asList("SHA1", "SHA256"));

	private static final Pattern SIGNATURE_ALGORITHM_PATTERN = Pattern.compile("(.+)with(.+)",
			Pattern.CASE_INSENSITIVE);

	private static final byte[] SIGNATURE_TEST_BYTES = KeyPairResolver.class.getName().getBytes();

	private final Map<String, Signature> signatures = new HashMap<>();

	private final Map<PrivateKey, byte[]> privateKeys = new HashMap<>();
	private final Set<PublicKey> publicKeys = new HashSet<>();

	/**
	 * Add a private key for resolving.
	 *
	 * @param privateKey The private key to add.
	 * @throws GeneralSecurityException if no test signature can be generated
	 *         for the private key.
	 */
	public void addPrivateKey(PrivateKey privateKey) throws GeneralSecurityException {
		Signature signature = getSignatureInstance(privateKey.getAlgorithm());

		signature.initSign(privateKey);
		signature.update(SIGNATURE_TEST_BYTES);

		byte[] signatureBytes = signature.sign();
		this.privateKeys.put(privateKey, signatureBytes);
	}

	/**
	 * Add a public key for resolving.
	 *
	 * @param publicKey The public key to add.
	 */
	public void addPublicKey(PublicKey publicKey) {
		this.publicKeys.add(publicKey);
	}

	/**
	 * Resolve the added keys.
	 *
	 * @return The resolved key pairs or an empty list if no matching pair was
	 *         found.
	 * @throws GeneralSecurityException if an error occurs during test signature
	 *         processing.
	 */
	public List<Object> resolve() throws GeneralSecurityException {
		List<Object> keyPairObjects = new ArrayList<>();

		for (PublicKey publicKey : this.publicKeys) {
			for (Map.Entry<PrivateKey, byte[]> privateKeyEntry : this.privateKeys.entrySet()) {
				PrivateKey privateKey = privateKeyEntry.getKey();
				byte[] signatureBytes = privateKeyEntry.getValue();

				if (publicKey.getAlgorithm().equals(privateKey.getAlgorithm())) {
					Signature signature = getSignatureInstance(privateKey.getAlgorithm());

					signature.initVerify(publicKey);
					signature.update(SIGNATURE_TEST_BYTES);
					try {
						if (signature.verify(signatureBytes)) {
							keyPairObjects.add(new KeyPair(publicKey, privateKey));
						}
					} catch (SignatureException e) {
						Exceptions.ignore(e);
					}
				}
			}
		}
		return keyPairObjects;
	}

	private Signature getSignatureInstance(String encryptionAlgorithm) throws GeneralSecurityException {
		Signature signature = this.signatures.get(encryptionAlgorithm);

		if (signature == null) {
			Set<String> signatureAlgorithms = Security.getAlgorithms("Signature");
			String signatureInstanceAlgorithm = null;

			for (String signatureAlgorithm : signatureAlgorithms) {
				Matcher matcher = SIGNATURE_ALGORITHM_PATTERN.matcher(signatureAlgorithm);

				if (matcher.matches()) {
					String digest = matcher.group(1).toUpperCase();
					String encryptionSuffix = matcher.group(2).toUpperCase();

					if (encryptionSuffix.startsWith(encryptionAlgorithm)) {
						signatureInstanceAlgorithm = signatureAlgorithm;
						if (PREFERRED_DIGESTS.contains(digest)) {
							break;
						}
					}
				}
			}
			if (signatureInstanceAlgorithm == null) {
				throw new NoSuchAlgorithmException("No suitable signature algorihm found for: " + encryptionAlgorithm);
			}
			signature = Signature.getInstance(signatureInstanceAlgorithm);
			this.signatures.put(encryptionAlgorithm, signature);
		}
		return signature;
	}
}
