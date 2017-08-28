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
package de.carne.certmgr.certs;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;
import de.carne.util.Exceptions;
import de.carne.util.logging.Log;

/**
 * Class used to collect/transfer individual certificate objects for reading and writing.
 */
public final class CertObjectStore implements Iterable<CertObjectStore.Entry> {

	private static final Log LOG = new Log(CertObjectStoreI18N.BUNDLE);

	/**
	 * This class represents a single certificate object.
	 */
	public final class Entry {

		private final String alias;
		private final CertObjectType type;
		private final Object object;

		Entry(String alias, CertObjectType type, Object object) {
			this.alias = alias;
			this.type = type;
			this.object = object;
		}

		/**
		 * Get this certificate object's alias.
		 *
		 * @return This certificate object's alias.
		 */
		public String alias() {
			return this.alias;
		}

		/**
		 * Get this certificate object's type.
		 *
		 * @return This certificate object's type.
		 * @see CertObjectType
		 */
		public CertObjectType type() {
			return this.type;
		}

		/**
		 * Get the CRT ({@link X509Certificate}) represented by this certificate object.
		 *
		 * @return The CRT represented by this certificate object.
		 * @throws ClassCastException if this certificate is not of type CRT.
		 * @see #type()
		 */
		public X509Certificate getCRT() throws ClassCastException {
			return (X509Certificate) this.object;
		}

		/**
		 * Get the Key ({@link KeyPair}) represented by this certificate object.
		 *
		 * @return The Key represented by this certificate object.
		 * @throws ClassCastException if this certificate is not of type Key.
		 * @see #type()
		 */
		public KeyPair getKey() throws ClassCastException {
			return (KeyPair) this.object;
		}

		/**
		 * Get the CSR ({@link PKCS10CertificateRequest}) represented by this certificate object.
		 *
		 * @return The CSR represented by this certificate object.
		 * @throws ClassCastException if this certificate is not of type CSR.
		 * @see #type()
		 */
		public PKCS10CertificateRequest getCSR() throws ClassCastException {
			return (PKCS10CertificateRequest) this.object;
		}

		/**
		 * Get the CRL ({@link X509CRL}) represented by this certificate object.
		 *
		 * @return The CRL represented by this certificate object.
		 * @throws ClassCastException if this certificate is not of type CRL.
		 * @see #type()
		 */
		public X509CRL getCRL() throws ClassCastException {
			return (X509CRL) this.object;
		}

		@Override
		public String toString() {
			return this.alias + ":" + this.type;
		}

	}

	private static final Set<String> PREFERRED_DIGESTS = new HashSet<>(Arrays.asList("SHA1", "SHA256"));
	private static final Pattern SIGNATURE_ALGORITHM_PATTERN = Pattern.compile("(.+)with(.+)",
			Pattern.CASE_INSENSITIVE);
	private static final byte[] SIGNATURE_TEST_BYTES = CertObjectStore.class.getName().getBytes();

	private final Map<String, Signature> signatureCache = new HashMap<>();
	private final Map<PrivateKey, byte[]> privateKeySignatures = new HashMap<>();

	private boolean mergePrivateKey(String alias, PrivateKey privateKey) {
		boolean merged = false;

		try {
			for (int entryIndex = 0; entryIndex < this.entries.size(); entryIndex++) {
				Entry entry = this.entries.get(entryIndex);

				if (entry.type() == CertObjectType.KEY) {
					KeyPair entryKey = entry.getKey();

					if (entryKey.getPrivate() == null && matchKeys(privateKey, entryKey.getPublic())) {
						Entry mergedEntry = new Entry(entry.alias(), entry.type(),
								new KeyPair(entryKey.getPublic(), privateKey));

						this.entries.set(entryIndex, mergedEntry);
						merged = true;
						break;
					}
				}
			}
			if (!merged) {
				for (Entry entry : this.entries) {
					PublicKey entryPublicKey = null;

					if (entry.type() == CertObjectType.CRT) {
						entryPublicKey = entry.getCRT().getPublicKey();
					} else if (entry.type() == CertObjectType.CSR) {
						entryPublicKey = entry.getCSR().getPublicKey();
					}
					if (entryPublicKey != null && matchKeys(privateKey, entryPublicKey)) {
						Entry mergedEntry = new Entry(alias, CertObjectType.KEY,
								new KeyPair(entryPublicKey, privateKey));

						this.entries.add(mergedEntry);
						merged = true;
						break;
					}
				}
				if (!merged) {
					Entry mergedEntry = new Entry(alias, CertObjectType.KEY, new KeyPair(null, privateKey));

					this.entries.add(mergedEntry);
					merged = true;
				}
			}
		} catch (GeneralSecurityException e) {
			LOG.warning(e, CertObjectStoreI18N.STR_MESSAGE_PRIVATE_KEY_FAILURE, alias);
		}
		return merged;
	}

	private boolean mergePublicKey(String alias, PublicKey publicKey, boolean mergeOnly) {
		boolean merged = false;

		try {
			for (int entryIndex = 0; entryIndex < this.entries.size(); entryIndex++) {
				Entry entry = this.entries.get(entryIndex);

				if (entry.type() == CertObjectType.KEY) {
					KeyPair entryKey = entry.getKey();

					if (entryKey.getPublic() == null && matchKeys(entryKey.getPrivate(), publicKey)) {
						Entry mergedEntry = new Entry(entry.alias(), entry.type(),
								new KeyPair(publicKey, entryKey.getPrivate()));

						this.entries.set(entryIndex, mergedEntry);
						merged = true;
						break;
					}
				}
			}
			if (!merged && !mergeOnly) {
				Entry mergedEntry = new Entry(alias, CertObjectType.KEY, new KeyPair(publicKey, null));

				this.entries.add(mergedEntry);
				merged = true;
			}
		} catch (GeneralSecurityException e) {
			LOG.warning(e, CertObjectStoreI18N.STR_MESSAGE_PUBLIC_KEY_FAILURE, alias);
		}
		return merged;
	}

	private boolean matchKeys(PrivateKey privateKey, PublicKey publicKey) throws GeneralSecurityException {
		boolean match = false;

		if (privateKey.getAlgorithm().equals(publicKey.getAlgorithm())) {
			byte[] signatureBytes = getPrivateKeySignature(privateKey);
			Signature signature = getSignatureInstance(privateKey.getAlgorithm());

			signature.initVerify(publicKey);
			signature.update(SIGNATURE_TEST_BYTES);
			try {
				match = signature.verify(signatureBytes);
			} catch (SignatureException e) {
				Exceptions.ignore(e);
			}
		}
		return match;
	}

	private byte[] getPrivateKeySignature(PrivateKey privateKey) throws GeneralSecurityException {
		byte[] signatureBytes = this.privateKeySignatures.get(privateKey);

		if (signatureBytes == null) {
			Signature signature = getSignatureInstance(privateKey.getAlgorithm());

			signature.initSign(privateKey);
			signature.update(SIGNATURE_TEST_BYTES);
			signatureBytes = signature.sign();
			this.privateKeySignatures.put(privateKey, signatureBytes);
		}
		return signatureBytes;
	}

	private Signature getSignatureInstance(String encryptionAlgorithm) throws GeneralSecurityException {
		Signature signature = this.signatureCache.get(encryptionAlgorithm);

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
			this.signatureCache.put(encryptionAlgorithm, signature);
		}
		return signature;
	}

	private final List<Entry> entries = new ArrayList<>();
	private int crtNumber = 1;
	private int keyNumber = 1;
	private int csrNumber = 1;
	private int crlNumber = 1;

	/**
	 * Wrap a single store entry into a store.
	 *
	 * @param entry The entry to wrap.
	 * @return The certificate store containing the submitted entry object.
	 */
	public static CertObjectStore wrap(Entry entry) {
		CertObjectStore store = new CertObjectStore();

		switch (entry.type()) {
		case CRT:
			store.addCRT(entry.alias(), entry.getCRT());
			break;
		case KEY:
			store.addKey(entry.alias(), entry.getKey());
			break;
		case CSR:
			store.addCSR(entry.alias(), entry.getCSR());
			break;
		case CRL:
			store.addCRL(entry.alias(), entry.getCRL());
			break;
		}
		return store;
	}

	/**
	 * Add a CRT object to the store.
	 *
	 * @param crt The CRT object to add.
	 */
	public void addCRT(X509Certificate crt) {
		addCRT("crt" + this.crtNumber, crt);
		this.crtNumber++;
	}

	/**
	 * Add a CRT object to the store.
	 *
	 * @param alias The alias to use.
	 * @param crt The CRT object to add.
	 */
	public void addCRT(String alias, X509Certificate crt) {
		this.entries.add(new Entry(alias, CertObjectType.CRT, crt));
		mergePublicKey(alias, crt.getPublicKey(), true);
	}

	/**
	 * Add a Key object to the store.
	 *
	 * @param key The Key object to add.
	 */
	public void addKey(KeyPair key) {
		addKey("key" + this.keyNumber, key);
		this.keyNumber++;
	}

	/**
	 * Add a Key object to the store.
	 *
	 * @param alias The alias to use.
	 * @param key The Key object to add.
	 */
	public void addKey(String alias, KeyPair key) {
		this.entries.add(new Entry(alias, CertObjectType.KEY, key));
	}

	/**
	 * Add a Private Key object to the store.
	 *
	 * @param privateKey The Private Key object to add.
	 */
	public void addPrivateKey(PrivateKey privateKey) {
		if (mergePrivateKey("key" + this.keyNumber, privateKey)) {
			this.keyNumber++;
		}
	}

	/**
	 * Add a Private Key object to the store.
	 *
	 * @param alias The alias to use.
	 * @param privateKey The Private Key object to add.
	 */
	public void addPrivateKey(String alias, PrivateKey privateKey) {
		mergePrivateKey(alias, privateKey);
	}

	/**
	 * Add a Public Key object to the store.
	 *
	 * @param publicKey The Public Key object to add.
	 */
	public void addPublicKey(PublicKey publicKey) {
		if (mergePublicKey("key" + this.keyNumber, publicKey, false)) {
			this.keyNumber++;
		}
	}

	/**
	 * Add a Public Key object to the store.
	 *
	 * @param alias The alias to use.
	 * @param publicKey The Public Key object to add.
	 */
	public void addPublicKey(String alias, PublicKey publicKey) {
		mergePublicKey("key" + this.keyNumber, publicKey, false);
	}

	/**
	 * Add a CSR object to the store.
	 *
	 * @param csr The CSR object to add.
	 */
	public void addCSR(PKCS10CertificateRequest csr) {
		addCSR("csr" + this.csrNumber, csr);
		this.csrNumber++;
	}

	/**
	 * Add a CSR object to the store.
	 *
	 * @param alias The alias to use.
	 * @param csr The CSR object to add.
	 */
	public void addCSR(String alias, PKCS10CertificateRequest csr) {
		this.entries.add(new Entry(alias, CertObjectType.CSR, csr));
		mergePublicKey(alias, csr.getPublicKey(), true);
	}

	/**
	 * Add a CRL object to the store.
	 *
	 * @param crl The CRL object to add.
	 */
	public void addCRL(X509CRL crl) {
		addCRL("crl" + this.crlNumber, crl);
		this.crlNumber++;
	}

	/**
	 * Add a CRL object to the store.
	 *
	 * @param alias The alias to use.
	 * @param crl The CRL object to add.
	 */
	public void addCRL(String alias, X509CRL crl) {
		this.entries.add(new Entry(alias, CertObjectType.CRL, crl));
	}

	/**
	 * Get the number of certificate objects in this store.
	 *
	 * @return The number of certificate objects in this store.
	 */
	public int size() {
		return this.entries.stream().filter((entry) -> filterIncompleteKeys(entry)).mapToInt((entry) -> 1).sum();
	}

	@Override
	public Iterator<Entry> iterator() {
		return this.entries.stream().filter((entry) -> filterIncompleteKeys(entry)).iterator();
	}

	private static boolean filterIncompleteKeys(Entry entry) {
		boolean filter = true;

		if (entry.type() == CertObjectType.KEY) {
			KeyPair key = entry.getKey();

			if (key.getPrivate() == null || key.getPublic() == null) {
				LOG.warning(CertObjectStoreI18N.STR_MESSAGE_INCOMPLETE_KEY, entry.alias());
				filter = false;
			}
		}
		return filter;
	}

}
