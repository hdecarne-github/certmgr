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
package de.carne.certmgr.certs;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.carne.certmgr.certs.io.CertReaders;
import de.carne.certmgr.certs.io.FileCertReaderInput;
import de.carne.certmgr.certs.io.StringCertReaderInput;
import de.carne.certmgr.certs.io.URLCertReaderInput;
import de.carne.certmgr.certs.net.SSLPeer;
import de.carne.util.Exceptions;
import de.carne.util.logging.Log;

/**
 * This class provides the actual certificate store functionality.
 * <p>
 * The actual kind of certificate store provided by this class depends on how
 * the instance was created.
 *
 * @see #createStore(Path)
 * @see #openStore(Path)
 * @see #createFromFile(Path, PasswordCallback)
 * @see #createFromFiles(Collection, PasswordCallback)
 * @see #createFromURL(URL, PasswordCallback)
 * @see #createFromServer(String, int)
 * @see #createFromData(String, String, PasswordCallback)
 */
public final class UserCertStore {

	private static final Log LOG = new Log();

	private static final Provider PROVIDER = new BouncyCastleProvider();

	static {
		LOG.info("Adding BouncyCastle security provider...");
		Security.addProvider(PROVIDER);
	}

	private final UserCertStoreHandler storeHandler;

	private final Map<UserCertStoreEntryId, Entry> storeEntries = new HashMap<>();

	private UserCertStore(UserCertStoreHandler storeHandler) {
		this.storeHandler = storeHandler;
	}

	/**
	 * Create a certificate store backed up by a local directory structure.
	 * <p>
	 * The created certificate store supports read and write access.
	 *
	 * @param storeHome The directory path to use for certificate storage.
	 * @return The created certificate store.
	 * @throws IOException if an I/O error occurs while creating the store.
	 * @see PersistentUserCertStoreHandler
	 */
	public static UserCertStore createStore(Path storeHome) throws IOException {
		assert storeHome != null;

		return null;
	}

	/**
	 * Open a certificate store previously created via a
	 * {@link #createStore(Path)} call.
	 *
	 * @param storeHome The directory path to use for certificate storage.
	 * @return The opened certificate store.
	 * @throws IOException if an I/O error occurs while opening the store.
	 */
	public static UserCertStore openStore(Path storeHome) throws IOException {
		assert storeHome != null;

		return null;
	}

	/**
	 * Create a certificate store backed up by a single file.
	 * <p>
	 * The created certificate store supports only read access.
	 *
	 * @param file The file providing the certificate data.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The created certificate store.
	 * @throws PasswordRequiredException if no valid password was given.
	 * @throws IOException if an I/O error occurs while reading/decoding
	 *         certificate data.
	 */
	public static UserCertStore createFromFile(Path file, PasswordCallback password) throws IOException {
		assert file != null;
		assert password != null;

		return createFromFiles(Arrays.asList(file), password);
	}

	/**
	 * Create a certificate store backed up by multiple files.
	 * <p>
	 * The created certificate store supports only read access.
	 *
	 * @param files The files providing the certificate data.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The created certificate store.
	 * @throws PasswordRequiredException if no valid password was given.
	 * @throws IOException if an I/O error occurs while reading/decoding
	 *         certificate data.
	 */
	public static UserCertStore createFromFiles(Collection<Path> files, PasswordCallback password) throws IOException {
		assert files != null;
		assert password != null;

		List<Object> certObjects = new ArrayList<>();

		for (Path file : files) {
			try (FileCertReaderInput fileInput = new FileCertReaderInput(file)) {
				List<Object> fileCertObjects = CertReaders.read(fileInput, password);

				if (fileCertObjects != null) {
					certObjects.addAll(fileCertObjects);
				} else {
					LOG.warning("Ignoring file ''{0}'' due to unrecognized file format or missing password", file);
				}
			}
		}
		return createFromCertObjects(certObjects);
	}

	/**
	 * Create a certificate store backed up by an {@link URL}.
	 * <p>
	 * The created certificate store supports only read access.
	 *
	 * @param url The URL to use for certificate data access.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The created certificate store.
	 * @throws PasswordRequiredException if no valid password was given.
	 * @throws IOException if an I/O error occurs while reading/decoding
	 *         certificate data.
	 */
	public static UserCertStore createFromURL(URL url, PasswordCallback password) throws IOException {
		assert url != null;
		assert password != null;

		List<Object> certObjects = new ArrayList<>();

		try (URLCertReaderInput urlInput = new URLCertReaderInput(url)) {
			certObjects = CertReaders.read(urlInput, password);
		}
		return createFromCertObjects(certObjects);
	}

	/**
	 * Create a certificate store backed up by server provided certificate data.
	 * <p>
	 * The created certificate store supports only read access.
	 *
	 * @param host The host to retrieve the certificate data from.
	 * @param port The port to retrieve the certificate data from.
	 * @return The created certificate store.
	 * @throws IOException if an I/O error occurs while reading/decoding
	 *         certificate data.
	 */
	public static UserCertStore createFromServer(String host, int port) throws IOException {
		SSLPeer sslPeer = SSLPeer.getInstance(host, port);
		Certificate[] certificates = sslPeer.readCertificates();
		List<Object> certObjects = new ArrayList<>();

		if (certificates != null) {
			for (Certificate cert : certificates) {
				if (cert instanceof X509Certificate) {
					certObjects.add(cert);
				}
			}
		}
		return createFromCertObjects(certObjects);
	}

	/**
	 * Create a certificate store backed up by text data.
	 * <p>
	 * The created certificate store supports only read access.
	 *
	 * @param data The text containing the certificate data.
	 * @param name The name to use when referring to the text data (e.g. during
	 *        password queries).
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The created certificate store.
	 * @throws PasswordRequiredException if no valid password was given.
	 * @throws IOException if an I/O error occurs while reading/decoding
	 *         certificate data.
	 */
	public static UserCertStore createFromData(String data, String name, PasswordCallback password) throws IOException {
		assert data != null;
		assert name != null;
		assert password != null;

		List<Object> certObjects = new ArrayList<>();

		try (StringCertReaderInput dataInput = new StringCertReaderInput(data, name)) {
			certObjects = CertReaders.read(dataInput, password);
		}
		return createFromCertObjects(certObjects);
	}

	private static UserCertStore createFromCertObjects(List<Object> certObjects) throws IOException {
		UserCertStore store = null;

		if (!certObjects.isEmpty()) {
			store = new UserCertStore(new TransientUserCertStoreHandler());
			store.mergeCertObjects(certObjects, NoPassword.getInstance());
		}
		return store;
	}

	/**
	 * Get this store's home path.
	 * <p>
	 * This path is only available if this store was created/opened via
	 * {@linkplain #createStore(Path)} or {@linkplain #openStore(Path)}.
	 * Otherwise this path is {@code null} indicating that the store only
	 * support read access.
	 *
	 * @return This store's home path or {@code null} if this store only
	 *         supports read access.
	 */
	public Path storeHome() {
		return this.storeHandler.storeHome();
	}

	/**
	 * Get this store's entry count.
	 * 
	 * @return This store's entry count.
	 */
	public synchronized int size() {
		return this.storeEntries.size();
	}

	/**
	 * Get this store's root entries.
	 *
	 * @return This store's root entries.
	 */
	public synchronized List<UserCertStoreEntry> getRootEntries() {
		List<UserCertStoreEntry> rootEntries = new ArrayList<>();

		for (Entry entry : this.storeEntries.values()) {
			if (entry.isSelfSigned()) {
				rootEntries.add(entry);
			}
		}
		return rootEntries;
	}

	/**
	 * Get this store's entries which are issued by a specific store entry.
	 *
	 * @param entry The store entry to get the issued entries for.
	 * @return The store entries which are issued by the submitted store entry.
	 */
	public synchronized List<UserCertStoreEntry> getIssuedEntries(UserCertStoreEntry entry) {
		assert entry != null;

		List<UserCertStoreEntry> issuedEntries = new ArrayList<>();

		for (Entry issuedEntry : this.storeEntries.values()) {
			if (!entry.equals(issuedEntry) && issuedEntry.issuer().equals(entry)) {
				issuedEntries.add(issuedEntry);
			}
		}
		return issuedEntries;
	}

	private synchronized void mergeCertObjects(List<Object> certObjects, PasswordCallback password) throws IOException {
		for (Object certObject : certObjects) {
			if (certObject instanceof X509Certificate) {
				mergeX509Certificate((X509Certificate) certObject);
			}
		}
		resetIssuers();
	}

	private Entry mergeX509Certificate(X509Certificate crt) throws IOException {
		Entry matchingEntry = matchX509Certificate(crt);

		if (matchingEntry == null) {
			UserCertStoreEntryId entryId = this.storeHandler.nextEntryId(null);
			CRTEntry crtEntry = this.storeHandler.createCRTEntry(entryId, crt);

			matchingEntry = new Entry(entryId, crt.getSubjectX500Principal(), crtEntry);
			this.storeEntries.put(entryId, matchingEntry);
		}
		return matchingEntry;
	}

	private void resetIssuers() throws IOException {
		// Clear all external and invalid issuer references
		Map<X500Principal, Entry> externalIssuers = new HashMap<>(this.storeEntries.size());

		for (Entry entry : this.storeEntries.values()) {
			UserCertStoreEntry issuer = entry.issuer();

			if (issuer != null && !entry.isSelfSigned()) {
				if (issuer.isExternal()) {
					entry.setIssuer(null);
					externalIssuers.put(issuer.dn(), this.storeEntries.get(issuer.id()));
				} else if (!this.storeEntries.containsKey(issuer.id())) {
					entry.setIssuer(null);
				}
			}
		}

		// Update all missing issuer references
		Set<UserCertStoreEntryId> usedExternalIssuerIds = new HashSet<>(this.storeEntries.size());

		for (Entry entry : this.storeEntries.values()) {
			if (entry.issuer() == null) {
				if (entry.hasCRT()) {
					X509Certificate entryCRT = entry.getCRT();
					Entry foundIssuerEntry = null;

					for (Entry issuerEntry : this.storeEntries.values()) {
						if (issuerEntry.hasCRT()) {
							X509Certificate issuerEntryCRT = issuerEntry.getCRT();

							if (isX509CertificateIssuedBy(entryCRT, issuerEntryCRT)) {
								foundIssuerEntry = issuerEntry;
								break;
							}
						}
					}
					if (foundIssuerEntry != null) {
						entry.setIssuer(foundIssuerEntry);
					} else {
						X500Principal issuerDN = entryCRT.getIssuerX500Principal();
						Entry externalIssuer = externalIssuers.get(issuerDN);

						if (externalIssuer == null) {
							externalIssuer = new Entry(this.storeHandler.nextEntryId(null), issuerDN);
							externalIssuer.setIssuer(externalIssuer);
							externalIssuers.put(issuerDN, externalIssuer);
						}
						entry.setIssuer(externalIssuer);
						usedExternalIssuerIds.add(externalIssuer.id());
					}
				} else {
					// Without a CRT an entry is always self-signed
					entry.setIssuer(entry);
				}
			}
		}

		// Also update external issuer entries as needed
		for (Entry externalIssuer : externalIssuers.values()) {
			UserCertStoreEntryId externalIssuerId = externalIssuer.id();

			if (usedExternalIssuerIds.contains(externalIssuerId)) {
				this.storeEntries.put(externalIssuerId, externalIssuer);
			} else {
				this.storeEntries.remove(externalIssuerId);
			}
		}
	}

	private boolean isX509CertificateIssuedBy(X509Certificate crt, X509Certificate issuerCRT) throws IOException {
		boolean isIssuedBy = false;

		if (issuerCRT.getSubjectX500Principal().equals(crt.getIssuerX500Principal())) {
			try {
				crt.verify(issuerCRT.getPublicKey());
				isIssuedBy = true;
			} catch (SignatureException e) {
				Exceptions.ignore(e);
			} catch (GeneralSecurityException e) {
				throw new CertProviderException(e);
			}
		}
		return isIssuedBy;
	}

	private Entry matchX509Certificate(X509Certificate crt) throws IOException {
		X500Principal crtDN = crt.getSubjectX500Principal();
		PublicKey crtPublicKey = crt.getPublicKey();
		Entry matchingEntry = null;

		for (Entry entry : this.storeEntries.values()) {
			if (crtDN.equals(entry.dn()) && crtPublicKey.equals(entry.getPublicKey())) {
				matchingEntry = entry;
				break;
			}
		}
		return matchingEntry;
	}

	private class Entry extends UserCertStoreEntry {

		private Entry issuer = null;

		private CRTEntry crtEntry;

		private KeyEntry keyEntry;

		private CSREntry csrEntry;

		private CRLEntry crlEntry;

		Entry(UserCertStoreEntryId id, X500Principal dn) {
			super(id, dn);
			this.crtEntry = null;
			this.keyEntry = null;
			this.csrEntry = null;
			this.crlEntry = null;
		}

		Entry(UserCertStoreEntryId id, X500Principal dn, CRTEntry crtEntry) {
			super(id, dn);
			this.crtEntry = crtEntry;
			this.keyEntry = null;
			this.csrEntry = null;
			this.crlEntry = null;
		}

		Entry(UserCertStoreEntryId id, X500Principal dn, KeyEntry keyEntry) {
			super(id, dn);
			this.crtEntry = null;
			this.keyEntry = keyEntry;
			this.csrEntry = null;
			this.crlEntry = null;
		}

		Entry(UserCertStoreEntryId id, X500Principal dn, CSREntry csrEntry) {
			super(id, dn);
			this.crtEntry = null;
			this.keyEntry = null;
			this.csrEntry = csrEntry;
			this.crlEntry = null;
		}

		Entry(UserCertStoreEntryId id, X500Principal dn, CRLEntry crlEntry) {
			super(id, dn);
			this.crtEntry = null;
			this.keyEntry = null;
			this.csrEntry = null;
			this.crlEntry = crlEntry;
		}

		@Override
		public UserCertStore store() {
			return UserCertStore.this;
		}

		@Override
		public UserCertStoreEntry issuer() {
			return this.issuer;
		}

		void setIssuer(Entry issuer) {
			this.issuer = issuer;
		}

		@Override
		public boolean hasCRT() {
			return this.crtEntry != null;
		}

		@Override
		public X509Certificate getCRT() throws IOException {
			return (this.crtEntry != null ? this.crtEntry.getCRT() : null);
		}

		@Override
		public boolean hasDecryptedKey() {
			return this.keyEntry != null && this.keyEntry.isDecrypted();
		}

		@Override
		public boolean hasKey() {
			return this.keyEntry != null;
		}

		@Override
		public KeyPair getKey(PasswordCallback password) throws IOException {
			return (this.keyEntry != null ? this.keyEntry.getKey(password) : null);
		}

		@Override
		public boolean hasCSR() {
			return this.csrEntry != null;
		}

		@Override
		public PKCS10CertificateRequest getCSR() throws IOException {
			return (this.csrEntry != null ? this.csrEntry.getCSR() : null);
		}

		@Override
		public boolean hasCRL() {
			return this.crlEntry != null;
		}

		@Override
		public X509CRL getCRL() throws IOException {
			return (this.crlEntry != null ? this.crlEntry.getCRL() : null);
		}

		public PublicKey getPublicKey() throws IOException {
			PublicKey publicKey = null;

			if (hasCRT()) {
				publicKey = getCRT().getPublicKey();
			} else if (hasDecryptedKey()) {
				publicKey = getKey().getPublic();
			} else if (hasCSR()) {
				publicKey = getCSR().getPublicKey();
			}
			return publicKey;
		}

	}

}
