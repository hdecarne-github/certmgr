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

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PublicKey;
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

import de.carne.certmgr.certs.io.CertReaders;
import de.carne.certmgr.certs.io.JKSCertReaderWriter;
import de.carne.certmgr.certs.net.SSLPeer;
import de.carne.certmgr.certs.security.PlatformKeyStore;
import de.carne.certmgr.certs.spi.CertGenerator;
import de.carne.certmgr.certs.x509.GenerateCertRequest;
import de.carne.certmgr.certs.x509.KeyHelper;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;
import de.carne.certmgr.certs.x509.UpdateCRLRequest;
import de.carne.certmgr.certs.x509.X509CRLHelper;
import de.carne.certmgr.certs.x509.X509CertificateHelper;
import de.carne.nio.FileAttributes;
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

		List<CertObjectStore> certObjectStores = new ArrayList<>();

		for (Path file : files) {
			try {
				CertObjectStore certObjectStore = CertReaders.readFile(file, password);

				if (certObjectStore != null) {
					certObjectStores.add(certObjectStore);
				} else {
					LOG.warning("Ignoring file ''{0}'' due to unrecognized file format or missing password", file);
				}
			} catch (IOException e) {
				LOG.warning(e, "Ignoring file ''{0}'' due to read error: {1}", file, e.getLocalizedMessage());
			}
		}
		return createFromCertObjects(certObjectStores.toArray(new CertObjectStore[certObjectStores.size()]));
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

		CertObjectStore certObjects = CertReaders.readURL(url, password);

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
		CertObjectStore certObjects = new CertObjectStore();

		if (certificates != null) {
			for (Certificate certificate : certificates) {
				if (certificate instanceof X509Certificate) {
					certObjects.addCRT((X509Certificate) certificate);
				} else {

				}
			}
		}
		return createFromCertObjects(certObjects);
	}

	/**
	 * Create a certificate store backed up by string data.
	 *
	 * @param data The text containing the certificate data.
	 * @param resource The name of the resource providing the string data.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The created certificate store.
	 * @throws PasswordRequiredException if no valid password was given.
	 * @throws IOException if an I/O error occurs while reading/decoding
	 *         certificate data.
	 */
	public static UserCertStore createFromData(String data, String resource, PasswordCallback password)
			throws IOException {
		assert data != null;
		assert resource != null;
		assert password != null;

		CertObjectStore certObjects = CertReaders.readString(data, resource, password);

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

		CertObjectStore certObjects = generator.generateCert(request, password);
		Set<UserCertStoreEntry> mergedEntries = mergeCertObjects(certObjects, newPassword, aliasHint);

		assert mergedEntries.size() == 1;

		return mergedEntries.iterator().next();
	}

	/**
	 * Update an entry's CRL object.
	 *
	 * @param issuerEntry The entry to update the CRL for.
	 * @param request The update request information.
	 * @param password The password callback to use for password querying.
	 * @throws IOException if an I/O error occurs during the update.
	 */
	public synchronized void updateEntryCRL(UserCertStoreEntry issuerEntry, UpdateCRLRequest request,
			PasswordCallback password) throws IOException {
		assert issuerEntry != null;
		assert request != null;
		assert password != null;

		Entry storeEntry = this.storeEntries.get(issuerEntry.id());
		X509CRL crl = X509CRLHelper.generateCRL(storeEntry.getCRL(), request.lastUpdate(), request.nextUpdate(),
				request.getRevokeEntries(), storeEntry.dn(), storeEntry.getKey(password), request.signatureAlgorithm());
		CertObjectHolder<X509CRL> crlHolder = this.storeHandler.createCRL(storeEntry.id(), crl);

		storeEntry.setCRL(crlHolder);
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

		CertObjectStore certObjects = new CertObjectStore();
		String entryAlias = entry.id().getAlias();

		if (entry.hasCRT()) {
			certObjects.addCRT(entryAlias, entry.getCRT());
		}
		if (entry.hasKey()) {
			certObjects.addKey(entryAlias, entry.getKey());
		}
		if (entry.hasCSR()) {
			certObjects.addCSR(entryAlias, entry.getCSR());
		}
		if (entry.hasCRL()) {
			certObjects.addCRL(entryAlias, entry.getCRL());
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
			CertObjectHolder<X509Certificate> crtHolder = entry.crt();
			SecureCertObjectHolder<KeyPair> keyHolder = entry.key();
			CertObjectHolder<PKCS10CertificateRequest> csrHolder = entry.csr();
			CertObjectHolder<X509CRL> crlHolder = entry.crl();
			X500Principal entryDN = null;

			if (crtHolder != null) {
				entryDN = crtHolder.get().getSubjectX500Principal();
			} else if (csrHolder != null) {
				entryDN = csrHolder.get().getSubjectX500Principal();
			} else if (crlHolder != null) {
				entryDN = crlHolder.get().getIssuerX500Principal();
			} else {
				LOG.warning("Ignoring incompliete store entry ''{0}''", entryId);
			}
			if (entryDN != null) {
				Entry storeEntry = new Entry(entryId, entryDN, crtHolder, keyHolder, csrHolder, crlHolder);

				this.storeEntries.put(entryId, storeEntry);
			}
		}
		resetIssuers();
	}

	private static UserCertStore createFromCertObjects(CertObjectStore... certObjectStores) throws IOException {
		UserCertStore store = null;

		for (CertObjectStore certObjectStore : certObjectStores) {
			if (certObjectStore.size() > 0) {
				if (store == null) {
					store = new UserCertStore(new TransientUserCertStoreHandler());
				}
				store.mergeCertObjects(certObjectStore, NoPassword.getInstance(), null);
			}
		}
		return store;
	}

	private synchronized Set<UserCertStoreEntry> mergeCertObjects(CertObjectStore certObjects,
			PasswordCallback newPassword, String aliasHint) throws IOException {
		Set<UserCertStoreEntry> mergedEntries = new HashSet<>();

		// First merge CRT and CSR objects as they provide the entry's DN
		for (CertObjectStore.Entry certObject : certObjects) {
			UserCertStoreEntry mergedEntry = null;

			if (certObject.type() == CertObjectType.CRT) {
				mergedEntry = mergeX509Certificate(certObject.getCRT(), aliasHint);
			} else if (certObject.type() == CertObjectType.CSR) {
				mergedEntry = mergePKCS10CertificateRequest(certObject.getCSR(), aliasHint);
			}
			if (mergedEntry != null) {
				mergedEntries.add(mergedEntry);
			}
		}
		for (CertObjectStore.Entry certObject : certObjects) {
			UserCertStoreEntry mergedEntry = null;

			if (certObject.type() == CertObjectType.KEY) {
				mergedEntry = mergeKey(certObject.getKey(), newPassword);
			} else if (certObject.type() == CertObjectType.CRL) {
				mergedEntry = mergeX509CRL(certObject.getCRL(), aliasHint);
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
				CertObjectHolder<X509Certificate> crtHolder = this.storeHandler.createCRT(matchingEntry.id(), crt);

				matchingEntry.setCRT(crtHolder);
			} else {
				LOG.debug("Skipping duplicate CRT ''{0}''.", matchingEntry);
			}
		} else {
			UserCertStoreEntryId entryId = this.storeHandler.nextEntryId(aliasHint);
			CertObjectHolder<X509Certificate> crtHolder = this.storeHandler.createCRT(entryId, crt);

			matchingEntry = new Entry(entryId, crt.getSubjectX500Principal(), crtHolder, null, null, null);
			this.storeEntries.put(entryId, matchingEntry);
		}
		return matchingEntry;
	}

	private Entry mergeKey(KeyPair key, PasswordCallback newPassword) throws IOException {
		Entry matchingEntry = matchKey(key);

		if (matchingEntry != null) {
			if (!matchingEntry.hasKey()) {
				SecureCertObjectHolder<KeyPair> keyHolder = this.storeHandler.createKey(matchingEntry.id(), key,
						newPassword);

				matchingEntry.setKey(keyHolder);
			} else {
				LOG.info("Skipping duplicate Key ''{0}''.", matchingEntry);
			}
		} else {
			LOG.info("Skipping non-matching Key ''{0}''.", KeyHelper.toString(key.getPublic()));
		}
		return matchingEntry;
	}

	private Entry mergePKCS10CertificateRequest(PKCS10CertificateRequest csr, String aliasHint) throws IOException {
		Entry matchingEntry = matchPKCS10CertificateRequest(csr);

		if (matchingEntry != null) {
			if (!matchingEntry.hasCSR()) {
				CertObjectHolder<PKCS10CertificateRequest> csrHolder = this.storeHandler.createCSR(matchingEntry.id(),
						csr);

				matchingEntry.setCSR(csrHolder);
			} else {
				LOG.info("Skipping duplicate CSR ''{0}''.", matchingEntry);
			}
		} else {
			UserCertStoreEntryId entryId = this.storeHandler.nextEntryId(aliasHint);
			CertObjectHolder<PKCS10CertificateRequest> csrHolder = this.storeHandler.createCSR(entryId, csr);

			matchingEntry = new Entry(entryId, csr.getSubjectX500Principal(), null, null, csrHolder, null);
			this.storeEntries.put(entryId, matchingEntry);
		}
		return matchingEntry;
	}

	private Entry mergeX509CRL(X509CRL crl, String aliasHint) throws IOException {
		Entry matchingEntry = matchX509CRL(crl);

		if (matchingEntry != null) {
			if (!matchingEntry.hasCRL()) {
				CertObjectHolder<X509CRL> crlHolder = this.storeHandler.createCRL(matchingEntry.id(), crl);

				matchingEntry.setCRL(crlHolder);
			} else {
				LOG.info("Skipping duplicate CRL ''{0}''.", matchingEntry);
			}
		} else {
			UserCertStoreEntryId entryId = this.storeHandler.nextEntryId(aliasHint);
			CertObjectHolder<X509CRL> crlHolder = this.storeHandler.createCRL(entryId, crl);

			matchingEntry = new Entry(entryId, crl.getIssuerX500Principal(), null, null, null, crlHolder);
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
								&& X509CertificateHelper.isCRTSignedBy(entryCRT, issuerEntry.getPublicKey())) {
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
				if (entry.hasCRL() && X509CRLHelper.isCRLSignedBy(entry.getCRL(), crtPublicKey)) {
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
			if (entry.hasCRL() && X509CRLHelper.isCRLSignedBy(entry.getCRL(), publicKey)) {
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
			if (entry.hasCRL() && X509CRLHelper.isCRLSignedBy(entry.getCRL(), csrPublicKey)) {
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
				if (entry.hasPublicKey() && X509CRLHelper.isCRLSignedBy(crl, entry.getPublicKey())) {
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

		private CertObjectHolder<X509Certificate> crtHolder;

		private SecureCertObjectHolder<KeyPair> keyHolder;

		private CertObjectHolder<PKCS10CertificateRequest> csrHolder;

		private CertObjectHolder<X509CRL> crlHolder;

		Entry(UserCertStoreEntryId id, X500Principal dn, CertObjectHolder<X509Certificate> crtHolder,
				SecureCertObjectHolder<KeyPair> keyHolder, CertObjectHolder<PKCS10CertificateRequest> csrHolder,
				CertObjectHolder<X509CRL> crlHolder) {
			super(id, dn);
			this.crtHolder = crtHolder;
			this.keyHolder = keyHolder;
			this.csrHolder = csrHolder;
			this.crlHolder = crlHolder;
		}

		Entry(UserCertStoreEntryId id, X500Principal dn) {
			this(id, dn, null, null, null, null);
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
			return this.crtHolder != null;
		}

		@Override
		public X509Certificate getCRT() throws IOException {
			return (this.crtHolder != null ? this.crtHolder.get() : null);
		}

		void setCRT(CertObjectHolder<X509Certificate> crtHolder) {
			this.crtHolder = crtHolder;
		}

		@Override
		public boolean hasDecryptedKey() {
			return this.keyHolder != null && !this.keyHolder.isSecured();
		}

		@Override
		public boolean hasKey() {
			return this.keyHolder != null;
		}

		@Override
		public KeyPair getKey(PasswordCallback password) throws IOException {
			return (this.keyHolder != null ? this.keyHolder.get(password) : null);
		}

		void setKey(SecureCertObjectHolder<KeyPair> keyHolder) {
			this.keyHolder = keyHolder;
		}

		@Override
		public boolean hasCSR() {
			return this.csrHolder != null;
		}

		@Override
		public PKCS10CertificateRequest getCSR() throws IOException {
			return (this.csrHolder != null ? this.csrHolder.get() : null);
		}

		void setCSR(CertObjectHolder<PKCS10CertificateRequest> csrHolder) {
			this.csrHolder = csrHolder;
		}

		@Override
		public boolean hasCRL() {
			return this.crlHolder != null;
		}

		@Override
		public X509CRL getCRL() throws IOException {
			return (this.crlHolder != null ? this.crlHolder.get() : null);
		}

		void setCRL(CertObjectHolder<X509CRL> crlHolder) {
			this.crlHolder = crlHolder;
		}

		@Override
		public List<Path> getFilePaths() {
			List<Path> filePaths = new ArrayList<>();

			if (this.crtHolder != null) {
				Path crtPath = this.crtHolder.path();

				if (crtPath != null) {
					filePaths.add(crtPath);
				}
			}
			if (this.keyHolder != null) {
				Path keyPath = this.keyHolder.path();

				if (keyPath != null) {
					filePaths.add(keyPath);
				}
			}
			if (this.csrHolder != null) {
				Path csrPath = this.csrHolder.path();

				if (csrPath != null) {
					filePaths.add(csrPath);
				}
			}
			if (this.crlHolder != null) {
				Path crlPath = this.crlHolder.path();

				if (crlPath != null) {
					filePaths.add(crlPath);
				}
			}
			return filePaths;
		}

	}

}
