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
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PublicKey;
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

import org.checkerframework.checker.nullness.qual.Nullable;

import de.carne.certmgr.certs.generator.GenerateCertRequest;
import de.carne.certmgr.certs.io.CertReaders;
import de.carne.certmgr.certs.io.FileCertReaderInput;
import de.carne.certmgr.certs.io.JKSCertReaderWriter;
import de.carne.certmgr.certs.io.StringCertReaderInput;
import de.carne.certmgr.certs.io.URLCertReaderInput;
import de.carne.certmgr.certs.net.SSLPeer;
import de.carne.certmgr.certs.security.PlatformKeyStore;
import de.carne.certmgr.certs.spi.CertGenerator;
import de.carne.certmgr.certs.x509.KeyHelper;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;
import de.carne.nio.FileAttributes;
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
 * @see #createFromServer(SSLPeer.Protocol, String, int)
 * @see #createFromData(String, String, PasswordCallback)
 */
public final class UserCertStore {

	private static final Log LOG = new Log();

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
	 * @throws FileAlreadyExistsException if the directory path already exists.
	 * @throws IOException if an I/O error occurs while creating the store.
	 * @see PersistentUserCertStoreHandler
	 */
	public static UserCertStore createStore(Path storeHome) throws IOException {
		assert storeHome != null;

		if (Files.exists(storeHome)) {
			throw new FileAlreadyExistsException("Store home path already exists: " + storeHome);
		}

		Path createdStoreHome = Files.createDirectories(storeHome,
				FileAttributes.defaultUserDirectoryAttributes(storeHome));

		return openStore(createdStoreHome);
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

		PersistentUserCertStoreHandler persistentStoreHandler = new PersistentUserCertStoreHandler(storeHome);

		Map<UserCertStoreEntryId, PersistentEntry> persistentEntries = persistentStoreHandler.scanStore();
		UserCertStore store = new UserCertStore(persistentStoreHandler);

		store.loadPersistentEntries(persistentEntries);
		return store;
	}

	/**
	 * Create a certificate store backed up by a platform key store.
	 *
	 * @param platformKeyStore The platform key store providing the certificate
	 *        data.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The created certificate store.
	 * @throws PasswordRequiredException if no valid password was given.
	 * @throws IOException if an I/O error occurs while reading/decoding
	 *         certificate data.
	 */
	public static UserCertStore createFromPlatformKeyStore(PlatformKeyStore platformKeyStore, PasswordCallback password)
			throws IOException {
		assert platformKeyStore != null;
		assert password != null;

		return createFromCertObjects(JKSCertReaderWriter.readPlatformKeyStore(platformKeyStore, password));
	}

	/**
	 * Create a certificate store backed up by a single file.
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
	 *
	 * @param protocol The protocol to use for accessing the server.
	 * @param host The host to retrieve the certificate data from.
	 * @param port The port to retrieve the certificate data from.
	 * @return The created certificate store.
	 * @throws IOException if an I/O error occurs while reading/decoding
	 *         certificate data.
	 */
	public static UserCertStore createFromServer(SSLPeer.Protocol protocol, String host, int port) throws IOException {
		SSLPeer sslPeer = SSLPeer.getInstance(host, port);
		Certificate[] certificates = sslPeer.readCertificates(protocol);
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

	/**
	 * Get this store's home path.
	 * <p>
	 * This path is only available if this store was created/opened via
	 * {@linkplain #createStore(Path)} or {@linkplain #openStore(Path)}.
	 * Otherwise this path is {@code null} indicating that the store is
	 * transient and only supports read access.
	 *
	 * @return This store's home path, or {@code null} if this store only
	 *         supports read access.
	 */
	@Nullable
	public Path storeHome() {
		return this.storeHandler.storeHome();
	}

	/**
	 * Get this store's name.
	 * <p>
	 * A store's name is derived from it's home path.
	 *
	 * @return This store's name, or {@code null} if this store is transient.
	 * @see #storeHome()
	 */
	@Nullable
	public String storeName() {
		Path storeHome = this.storeHandler.storeHome();

		return (storeHome != null ? storeHome.getFileName().toString() : null);
	}

	/**
	 * Get this store's preferences object.
	 *
	 * @return This store's preferences object, or {@code null} if this store is
	 *         transient.
	 * @see #storeHome()
	 */
	public UserCertStorePreferences storePreferences() {
		Path storeHome = this.storeHandler.storeHome();

		return (storeHome != null ? new UserCertStorePreferences(storeHome) : null);
	}

	/**
	 * Generate a new entry id.
	 *
	 * @param aliasHint The preferred alias for the generated id.
	 * @return A entry id not yet used in this store.
	 */
	public UserCertStoreEntryId generateEntryId(String aliasHint) {
		return this.storeHandler.nextEntryId(aliasHint);
	}

	/**
	 * Generate a new store entry.
	 *
	 * @param generator The {@link CertGenerator} to use for generation.
	 * @param request The generation parameters.
	 * @param password The password to use for password querying.
	 * @param newPassword The password callback to use for new password
	 *        querying.
	 * @param aliasHint The preferred alias for the generated entry's id.
	 * @return The generated entry.
	 * @throws IOException if an I/O error occurs during import.
	 */
	public UserCertStoreEntry generateEntry(CertGenerator generator, GenerateCertRequest request,
			PasswordCallback password, PasswordCallback newPassword, String aliasHint) throws IOException {
		assert generator != null;
		assert request != null;
		assert password != null;

		List<Object> certObjects = generator.generateCert(request, password);
		Set<UserCertStoreEntry> mergedEntries = mergeCertObjects(certObjects, newPassword, aliasHint);

		assert mergedEntries.size() == 1;

		return mergedEntries.iterator().next();
	}

	/**
	 * Import an store entry from another store by merging the entry's
	 * certificate objects.
	 *
	 * @param entry The store entry to merge.
	 * @param newPassword The password callback to use for new password
	 *        querying.
	 * @param aliasHint The preferred alias for entry id generation.
	 * @return The generated or merged entry or {@code null} if nothing has been
	 *         imported.
	 * @throws IOException if an I/O error occurs during import.
	 */
	public UserCertStoreEntry importEntry(UserCertStoreEntry entry, PasswordCallback newPassword, String aliasHint)
			throws IOException {
		assert entry != null;
		assert newPassword != null;

		List<Object> certObjects = new ArrayList<>();

		if (entry.hasCRT()) {
			certObjects.add(entry.getCRT());
		}
		if (entry.hasKey()) {
			certObjects.add(entry.getKey());
		}
		if (entry.hasCSR()) {
			certObjects.add(entry.getCSR());
		}
		if (entry.hasCRL()) {
			certObjects.add(entry.getCRL());
		}

		Set<UserCertStoreEntry> mergedEntries = mergeCertObjects(certObjects, newPassword, aliasHint);

		assert mergedEntries.size() <= 1;

		return (!mergedEntries.isEmpty() ? mergedEntries.iterator().next() : null);
	}

	/**
	 * Delete a store entry.
	 *
	 * @param entryId The entry id to delete.
	 * @throws IOException if an I/O error occurs during deletion.
	 */
	public synchronized void deleteEntry(UserCertStoreEntryId entryId) throws IOException {
		assert entryId != null;

		if (!this.storeEntries.containsKey(entryId)) {
			throw new IllegalArgumentException("Invalid entry: " + entryId);
		}
		this.storeEntries.remove(entryId);
		this.storeHandler.deleteEntry(entryId);
		resetIssuers();
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
	 * Get this store's entries.
	 *
	 * @return This store's entries.
	 */
	public synchronized Set<UserCertStoreEntry> getEntries() {
		return new HashSet<>(this.storeEntries.values());
	}

	/**
	 * Get this store's root entries.
	 *
	 * @return This store's root entries.
	 */
	public synchronized Set<UserCertStoreEntry> getRootEntries() {
		Set<UserCertStoreEntry> rootEntries = new HashSet<>();

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
	public synchronized Set<UserCertStoreEntry> getIssuedEntries(UserCertStoreEntry entry) {
		assert entry != null;

		Set<UserCertStoreEntry> issuedEntries = new HashSet<>();

		for (Entry issuedEntry : this.storeEntries.values()) {
			if (!entry.equals(issuedEntry) && issuedEntry.issuer().equals(entry)) {
				issuedEntries.add(issuedEntry);
			}
		}
		return issuedEntries;
	}

	private synchronized void loadPersistentEntries(Map<UserCertStoreEntryId, PersistentEntry> entries)
			throws IOException {
		for (Map.Entry<UserCertStoreEntryId, PersistentEntry> persistentEntryPathsEntry : entries.entrySet()) {
			UserCertStoreEntryId entryId = persistentEntryPathsEntry.getKey();
			PersistentEntry entry = persistentEntryPathsEntry.getValue();
			CRTEntry crtEntry = entry.crtEntry();
			KeyEntry keyEntry = entry.keyEntry();
			CSREntry csrEntry = entry.csrEntry();
			CRLEntry crlEntry = entry.crlEntry();
			X500Principal entryDN = null;

			if (crtEntry != null) {
				entryDN = crtEntry.getCRT().getSubjectX500Principal();
			} else if (csrEntry != null) {
				entryDN = csrEntry.getCSR().getSubjectX500Principal();
			} else if (crlEntry != null) {
				entryDN = crlEntry.getCRL().getIssuerX500Principal();
			} else {
				LOG.warning("Ignoring incompliete store entry ''{0}''", entryId);
			}
			if (entryDN != null) {
				Entry storeEntry = new Entry(entryId, entryDN, crtEntry, keyEntry, csrEntry, crlEntry);

				this.storeEntries.put(entryId, storeEntry);
			}
		}
		resetIssuers();
	}

	private static UserCertStore createFromCertObjects(List<Object> certObjects) throws IOException {
		UserCertStore store = null;

		if (certObjects != null && !certObjects.isEmpty()) {
			store = new UserCertStore(new TransientUserCertStoreHandler());
			store.mergeCertObjects(certObjects, NoPassword.getInstance(), null);
		}
		return store;
	}

	private synchronized Set<UserCertStoreEntry> mergeCertObjects(List<Object> certObjects,
			PasswordCallback newPassword, String aliasHint) throws IOException {
		Set<UserCertStoreEntry> mergedEntries = new HashSet<>();

		// First merge CRT and CSR objects as they provide the entry's DN
		for (Object certObject : certObjects) {
			UserCertStoreEntry mergedEntry = null;

			if (certObject instanceof X509Certificate) {
				mergedEntry = mergeX509Certificate((X509Certificate) certObject, aliasHint);
			} else if (certObject instanceof PKCS10CertificateRequest) {
				mergedEntry = mergePKCS10CertificateRequest((PKCS10CertificateRequest) certObject, aliasHint);
			}
			if (mergedEntry != null) {
				mergedEntries.add(mergedEntry);
			}
		}
		for (Object certObject : certObjects) {
			UserCertStoreEntry mergedEntry = null;

			if (certObject instanceof KeyPair) {
				mergedEntry = mergeKey((KeyPair) certObject, newPassword);
			} else if (certObject instanceof X509CRL) {
				mergedEntry = mergeX509CRL((X509CRL) certObject, aliasHint);
			}
			if (mergedEntry != null) {
				mergedEntries.add(mergedEntry);
			}
		}
		resetIssuers();
		return mergedEntries;
	}

	private Entry mergeX509Certificate(X509Certificate crt, String aliasHint) throws IOException {
		Entry matchingEntry = matchX509Certificate(crt);

		if (matchingEntry != null) {
			if (!matchingEntry.hasCRT()) {
				CRTEntry crtEntry = this.storeHandler.createCRTEntry(matchingEntry.id(), crt);

				matchingEntry.setCRT(crtEntry);
			} else {
				LOG.warning(UserCertStoreI18N.formatSTR_MESSAGE_CRT_ALREADY_SET(matchingEntry));
			}
		} else {
			UserCertStoreEntryId entryId = this.storeHandler.nextEntryId(aliasHint);
			CRTEntry crtEntry = this.storeHandler.createCRTEntry(entryId, crt);

			matchingEntry = new Entry(entryId, crt.getSubjectX500Principal(), crtEntry);
			this.storeEntries.put(entryId, matchingEntry);
		}
		return matchingEntry;
	}

	private Entry mergeKey(KeyPair key, PasswordCallback newPassword) throws IOException {
		Entry matchingEntry = matchKey(key);

		if (matchingEntry != null) {
			if (!matchingEntry.hasKey()) {
				KeyEntry keyEntry = this.storeHandler.createKeyEntry(matchingEntry.id(), key, newPassword);

				matchingEntry.setKey(keyEntry);
			} else {
				LOG.warning(UserCertStoreI18N.formatSTR_MESSAGE_KEY_ALREADY_SET(matchingEntry));
			}
		} else {
			LOG.warning(UserCertStoreI18N.formatSTR_MESSAGE_DANGLING_KEY(KeyHelper.toString(key.getPublic())));
		}
		return matchingEntry;
	}

	private Entry mergePKCS10CertificateRequest(PKCS10CertificateRequest csr, String aliasHint) throws IOException {
		Entry matchingEntry = matchPKCS10CertificateRequest(csr);

		if (matchingEntry != null) {
			if (!matchingEntry.hasCSR()) {
				CSREntry csrEntry = this.storeHandler.createCSREntry(matchingEntry.id(), csr);

				matchingEntry.setCSR(csrEntry);
			} else {
				LOG.warning(UserCertStoreI18N.formatSTR_MESSAGE_CSR_ALREADY_SET(matchingEntry));
			}
		} else {
			UserCertStoreEntryId entryId = this.storeHandler.nextEntryId(aliasHint);
			CSREntry csrEntry = this.storeHandler.createCSREntry(entryId, csr);

			matchingEntry = new Entry(entryId, csr.getSubjectX500Principal(), csrEntry);
			this.storeEntries.put(entryId, matchingEntry);
		}
		return matchingEntry;
	}

	private Entry mergeX509CRL(X509CRL crl, String aliasHint) throws IOException {
		Entry matchingEntry = matchX509CRL(crl);

		if (matchingEntry != null) {
			if (!matchingEntry.hasCRL()) {
				CRLEntry crlEntry = this.storeHandler.createCRLEntry(matchingEntry.id(), crl);

				matchingEntry.setCRL(crlEntry);
			} else {
				LOG.warning(UserCertStoreI18N.formatSTR_MESSAGE_CRL_ALREADY_SET(matchingEntry));
			}
		} else {
			UserCertStoreEntryId entryId = this.storeHandler.nextEntryId(aliasHint);
			CRLEntry crlEntry = this.storeHandler.createCRLEntry(entryId, crl);

			matchingEntry = new Entry(entryId, crl.getIssuerX500Principal(), crlEntry);
			this.storeEntries.put(entryId, matchingEntry);
		}
		return matchingEntry;
	}

	private void resetIssuers() throws IOException {
		// Collect external issuers and clear invalid issuers
		Map<X500Principal, Entry> externalIssuers = new HashMap<>(this.storeEntries.size());

		for (Entry entry : this.storeEntries.values()) {
			UserCertStoreEntry issuer = entry.issuer();

			if (issuer != null) {
				if (issuer.isExternal()) {
					externalIssuers.put(issuer.dn(), this.storeEntries.get(issuer.id()));
				} else if (!this.storeEntries.containsKey(issuer.id())) {
					entry.setIssuer(null);
				}
			}
		}

		// Update all missing and external issuer references
		Set<UserCertStoreEntryId> usedExternalIssuerIds = new HashSet<>(this.storeEntries.size());

		for (Entry entry : this.storeEntries.values()) {
			UserCertStoreEntry issuer = entry.issuer();

			if (issuer == null || issuer.isExternal()) {
				if (entry.hasCRT()) {
					X509Certificate entryCRT = entry.getCRT();
					X500Principal issuerDN = entryCRT.getIssuerX500Principal();
					Entry foundIssuerEntry = null;

					for (Entry issuerEntry : this.storeEntries.values()) {
						if (issuerDN.equals(issuerEntry.dn()) && issuerEntry.hasPublicKey()
								&& isX509CertificateIssuedBy(entryCRT, issuerEntry.getPublicKey())) {
							foundIssuerEntry = issuerEntry;
							break;
						}
					}
					if (foundIssuerEntry != null) {
						entry.setIssuer(foundIssuerEntry);
					} else {
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

			if (usedExternalIssuerIds.contains(externalIssuer.id())) {
				this.storeEntries.put(externalIssuerId, externalIssuer);
			} else {
				this.storeEntries.remove(externalIssuerId);
			}
		}
	}

	private static boolean isX509CertificateIssuedBy(X509Certificate crt, PublicKey publicKey) throws IOException {
		boolean isIssuedBy = false;

		try {
			crt.verify(publicKey);
			isIssuedBy = true;
		} catch (SignatureException | InvalidKeyException e) {
			Exceptions.ignore(e);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return isIssuedBy;
	}

	private static boolean isX509CRLIssuedBy(X509CRL crl, PublicKey issuerKey) throws IOException {
		boolean isIssuedBy = false;

		try {
			crl.verify(issuerKey);
			isIssuedBy = true;
		} catch (SignatureException | InvalidKeyException e) {
			Exceptions.ignore(e);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return isIssuedBy;
	}

	private Entry matchX509Certificate(X509Certificate crt) throws IOException {
		X500Principal crtDN = crt.getSubjectX500Principal();
		PublicKey crtPublicKey = crt.getPublicKey();
		Entry matchingEntry = null;

		for (Entry entry : this.storeEntries.values()) {
			if (crtDN.equals(entry.dn())) {
				if (crtPublicKey.equals(entry.getPublicKey())) {
					matchingEntry = entry;
					break;
				}
				if (entry.hasCRL() && isX509CRLIssuedBy(entry.getCRL(), crtPublicKey)) {
					matchingEntry = entry;
					break;
				}
			}
		}
		return matchingEntry;
	}

	private Entry matchKey(KeyPair key) throws IOException {
		PublicKey publicKey = key.getPublic();
		Entry matchingEntry = null;

		for (Entry entry : this.storeEntries.values()) {
			if (publicKey.equals(entry.getPublicKey())) {
				matchingEntry = entry;
				break;
			}
			if (entry.hasCRL() && isX509CRLIssuedBy(entry.getCRL(), publicKey)) {
				matchingEntry = entry;
				break;
			}
		}
		return matchingEntry;
	}

	private Entry matchPKCS10CertificateRequest(PKCS10CertificateRequest csr) throws IOException {
		X500Principal csrDN = csr.getSubjectX500Principal();
		PublicKey csrPublicKey = csr.getPublicKey();
		Entry matchingEntry = null;

		for (Entry entry : this.storeEntries.values()) {
			if (csrDN.equals(entry.dn()) && csrPublicKey.equals(entry.getPublicKey())) {
				matchingEntry = entry;
				break;
			}
			if (entry.hasCRL() && isX509CRLIssuedBy(entry.getCRL(), csrPublicKey)) {
				matchingEntry = entry;
				break;
			}
		}
		return matchingEntry;
	}

	private Entry matchX509CRL(X509CRL crl) throws IOException {
		X500Principal crlDN = crl.getIssuerX500Principal();
		Entry matchingEntry = null;

		for (Entry entry : this.storeEntries.values()) {
			if (crlDN.equals(entry.dn())) {
				if (entry.hasPublicKey() && isX509CRLIssuedBy(crl, entry.getPublicKey())) {
					matchingEntry = entry;
					break;
				}
				if (entry.hasCRL() && entry.getCRL().equals(crl)) {
					matchingEntry = entry;
					break;
				}
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

		Entry(UserCertStoreEntryId id, X500Principal dn, CRTEntry crtEntry, KeyEntry keyEntry, CSREntry csrEntry,
				CRLEntry crlEntry) {
			super(id, dn);
			this.crtEntry = crtEntry;
			this.keyEntry = keyEntry;
			this.csrEntry = csrEntry;
			this.crlEntry = crlEntry;
		}

		Entry(UserCertStoreEntryId id, X500Principal dn) {
			this(id, dn, null, null, null, null);
		}

		Entry(UserCertStoreEntryId id, X500Principal dn, CRTEntry crtEntry) {
			this(id, dn, crtEntry, null, null, null);
		}

		Entry(UserCertStoreEntryId id, X500Principal dn, CSREntry csrEntry) {
			this(id, dn, null, null, csrEntry, null);
		}

		Entry(UserCertStoreEntryId id, X500Principal dn, CRLEntry crlEntry) {
			this(id, dn, null, null, null, crlEntry);
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

		void setCRT(CRTEntry crtEntry) {
			this.crtEntry = crtEntry;
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

		void setKey(KeyEntry keyEntry) {
			this.keyEntry = keyEntry;
		}

		@Override
		public boolean hasCSR() {
			return this.csrEntry != null;
		}

		@Override
		public PKCS10CertificateRequest getCSR() throws IOException {
			return (this.csrEntry != null ? this.csrEntry.getCSR() : null);
		}

		void setCSR(CSREntry csrEntry) {
			this.csrEntry = csrEntry;
		}

		@Override
		public boolean hasCRL() {
			return this.crlEntry != null;
		}

		@Override
		public X509CRL getCRL() throws IOException {
			return (this.crlEntry != null ? this.crlEntry.getCRL() : null);
		}

		void setCRL(CRLEntry crlEntry) {
			this.crlEntry = crlEntry;
		}

		public boolean hasPublicKey() {
			return hasCRT() || hasDecryptedKey() || hasCSR();
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
