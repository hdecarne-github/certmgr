/*
 * Copyright (c) 2014-2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.store;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.prefs.Preferences;

import de.carne.certmgr.store.provider.StoreProvider;
import de.carne.certmgr.store.x509.CertificateValidity;
import de.carne.certmgr.store.x509.EncodedX509Extension;
import de.carne.certmgr.store.x509.KeyParams;
import de.carne.certmgr.store.x509.RevokeReason;
import de.carne.certmgr.store.x509.X509CRLParams;
import de.carne.certmgr.store.x509.X509CertificateParams;
import de.carne.util.logging.Log;
import de.carne.util.prefs.PropertiesPreferencesFactory;

/**
 * Class used to create and maintain a certificate store.
 * <p>
 * Store certificates are maintained in the following directory structure:
 *
 * <pre>
 * ./*             (store directory)
 * ./certs/*.crt   (certificate files)
 * ./crl/*.crl     (certificate revocation lists)
 * ./csr/*.csr     (certificate signing requests)
 * ./private/*.key (key files)
 * </pre>
 * </p>
 */
public final class CertStore {

	private static final Log LOG = new Log(CertStore.class);

	static final StoreProvider PROVIDER = StoreProvider.getInstance();

	private static final String DEFAULT_ALIAS_PATTERN = "cert{0}";

	private static final String DIR_KEY = "private";
	private static final String DIR_CRT = "certs";
	private static final String DIR_CSR = "csr";
	private static final String DIR_CRL = "crl";

	private static final String EXT_KEY = ".key";
	private static final String EXT_CRT = ".crt";
	private static final String EXT_CSR = ".csr";
	private static final String EXT_CRL = ".crl";

	private static final String STORE_PREFERENCES = ".preferences.properties";

	private Path storeHomePath;

	private CertEntryMap<Entry> storeEntries = new CertEntryMap<>();

	/**
	 * Get the provider's info string containing of name and version.
	 *
	 * @return The provider's info string.
	 */
	public static String getProviderInfo() {
		return PROVIDER.getInfo();
	}

	/**
	 * Get the supported key algorithms.
	 *
	 * @return The supported key algorithms.
	 */
	public static SortedSet<String> getKeyAlgs() {
		return PROVIDER.getKeyAlgs();
	}

	/**
	 * Get the default key algorithm.
	 *
	 * @return The default key algorithm.
	 */
	public static String getDefaultKeyAlg() {
		return PROVIDER.getDefaultKeyAlg();
	}

	/**
	 * Get the supported key sizes.
	 *
	 * @param keyAlg The key algorithm to get the key sizes for.
	 * @return The supported key sizes.
	 */
	public static SortedSet<Integer> getKeySizes(String keyAlg) {
		assert keyAlg != null;

		return PROVIDER.getKeySizes(keyAlg);
	}

	/**
	 * Get the default key size.
	 *
	 * @param keyAlg The key algorithm to get the default key size for.
	 * @return The default key size.
	 */
	public static Integer getDefaultKeySize(String keyAlg) {
		assert keyAlg != null;

		return PROVIDER.getDefaultKeySize(keyAlg);
	}

	/**
	 * Get the supported signature algorithms.
	 *
	 * @param keyAlg The key algorithm to get the signature algorithms for.
	 * @return The supported signature algorithms.
	 */
	public static SortedSet<String> getSigAlgs(String keyAlg) {
		assert keyAlg != null;

		return PROVIDER.getSigAlgs(keyAlg);
	}

	/**
	 * Get the default signature algorithm.
	 *
	 * @param keyAlg The key algorithm to get the default signature algorithm
	 *        for.
	 * @return The default signature algorithm.
	 */
	public static String getDefaultSigAlg(String keyAlg) {
		assert keyAlg != null;

		return PROVIDER.getDefaultSigAlg(keyAlg);
	}

	/**
	 * Decode a CRT object's extensions.
	 *
	 * @param crt The CRT object to decode.
	 * @return The decoded extensions.
	 * @throws IOException if an I/O error occurs while decoding the CRT object.
	 */
	public static Collection<EncodedX509Extension> decodeCRTExtensions(X509Certificate crt) throws IOException {
		assert crt != null;

		ArrayList<EncodedX509Extension> extensions = new ArrayList<>();
		Set<String> criticalExtensionOIDs = crt.getCriticalExtensionOIDs();

		if (criticalExtensionOIDs != null) {
			for (String oid : criticalExtensionOIDs) {
				extensions.add(PROVIDER.decodeExtension(oid, true, crt.getExtensionValue(oid)));
			}
		}

		Set<String> nonCriticalExtensionOIDs = crt.getNonCriticalExtensionOIDs();

		if (nonCriticalExtensionOIDs != null) {
			for (String oid : nonCriticalExtensionOIDs) {
				extensions.add(PROVIDER.decodeExtension(oid, false, crt.getExtensionValue(oid)));
			}
		}
		return extensions;
	}

	/**
	 * Decode a CSR object's extensions.
	 *
	 * @param csr The CSR object to decode.
	 * @return The decoded extensions.
	 * @throws IOException if an I/O error occurs while decoding the CSR object.
	 */
	public static Collection<EncodedX509Extension> decodeCSRExtensions(PKCS10Object csr) throws IOException {
		assert csr != null;

		ArrayList<EncodedX509Extension> extensions = new ArrayList<>();
		Set<String> extensionOIDs = csr.getExtensionOIDs();

		if (extensionOIDs != null) {
			for (String oid : extensionOIDs) {
				extensions.add(PROVIDER.decodeExtension(oid, true, csr.getExtensionValue(oid)));
			}
		}
		return extensions;
	}

	/**
	 * Decode a CRL object's extensions.
	 *
	 * @param crl The CRL object to decode.
	 * @return The decoded extensions.
	 * @throws IOException if an I/O error occurs while decoding the CRL object.
	 */
	public static Collection<EncodedX509Extension> decodeCRLExtensions(X509CRL crl) throws IOException {
		assert crl != null;

		ArrayList<EncodedX509Extension> extensions = new ArrayList<>();
		Set<String> criticalExtensionOIDs = crl.getCriticalExtensionOIDs();

		if (criticalExtensionOIDs != null) {
			for (String oid : criticalExtensionOIDs) {
				extensions.add(PROVIDER.decodeExtension(oid, true, crl.getExtensionValue(oid)));
			}
		}

		Set<String> nonCriticalExtensionOIDs = crl.getCriticalExtensionOIDs();

		if (nonCriticalExtensionOIDs != null) {
			for (String oid : nonCriticalExtensionOIDs) {
				extensions.add(PROVIDER.decodeExtension(oid, false, crl.getExtensionValue(oid)));
			}
		}
		return extensions;
	}

	/**
	 * Create a new certificate store.
	 *
	 * @param storeHomeName The directory path where to create the new store
	 *        (must not yet exist).
	 * @return The created certificate store.
	 * @throws IOException if an I/O error occurs while creating the certificate
	 *         store.
	 */
	public static CertStore create(String storeHomeName) throws IOException {
		assert storeHomeName != null;

		return create(Paths.get(storeHomeName));
	}

	/**
	 * Create a new certificate store.
	 *
	 * @param storeHomePath The directory path where to create the new store
	 *        (must not yet exist).
	 * @return The created certificate store.
	 * @throws IOException if an I/O error occurs while creating the certificate
	 *         store.
	 */
	public static CertStore create(Path storeHomePath) throws IOException {
		assert storeHomePath != null;

		LOG.notice(I18N.bundle(), I18N.MESSAGE_CREATESTORE, storeHomePath);
		Files.createDirectory(storeHomePath);
		return new CertStore(storeHomePath);
	}

	/**
	 * Read an existing certificate store.
	 *
	 * @param storeHomeName The directory path where to read the store from.
	 * @return The opened certificate store.
	 * @throws IOException if an I/O error occurs while reading the certificate
	 *         store.
	 */
	public static CertStore open(String storeHomeName) throws IOException {
		assert storeHomeName != null;

		return open(Paths.get(storeHomeName));
	}

	/**
	 * Read an existing certificate store.
	 *
	 * @param storeHomePath The directory path where to read the store from.
	 * @return The opened certificate store.
	 * @throws IOException if an I/O error occurs while reading the certificate
	 *         store.
	 */
	public static CertStore open(Path storeHomePath) throws IOException {
		assert storeHomePath != null;

		LOG.notice(I18N.bundle(), I18N.MESSAGE_OPENSTORE, storeHomePath);
		return new CertStore(storeHomePath);
	}

	private CertStore(Path storeHomePath) throws IOException {
		this.storeHomePath = storeHomePath.toAbsolutePath();
		PathMatcher keyMatcher = storeHomePath.getFileSystem().getPathMatcher("glob:**/" + DIR_KEY + "/*" + EXT_KEY);
		PathMatcher crtMatcher = storeHomePath.getFileSystem().getPathMatcher("glob:**/" + DIR_CRT + "/*" + EXT_CRT);
		PathMatcher csrMatcher = storeHomePath.getFileSystem().getPathMatcher("glob:**/" + DIR_CSR + "/*" + EXT_CSR);
		PathMatcher crlMatcher = storeHomePath.getFileSystem().getPathMatcher("glob:**/" + DIR_CRL + "/*" + EXT_CRL);
		CertStoreScanner scanner = new CertStoreScanner(keyMatcher, crtMatcher, csrMatcher, crlMatcher);

		Files.walkFileTree(storeHomePath, scanner);

		for (String alias : scanner.getFoundAliases()) {
			Path keyFile = scanner.getFoundKeyFile(alias);
			Path crtFile = scanner.getFoundCRTFile(alias);
			Path csrFile = scanner.getFoundCSRFile(alias);
			Path crlFile = scanner.getFoundCRLFile(alias);

			if (crtFile != null || csrFile != null) {
				String entryName = null;
				Entry entry = null;

				try {
					if (crtFile != null) {
						X509Certificate crt = PROVIDER.readCRT(crtFile);

						entryName = CertObject.getCRTName(crt);
					} else {
						PKCS10Object csr = PROVIDER.readCSR(csrFile);

						entryName = CertObject.getCSRName(csr);
					}
					entry = new Entry(entryName, alias, keyFile, crtFile, csrFile, crlFile);
					mergeEntry0(entry);
				} catch (IOException e) {
					LOG.warning(e, I18N.bundle(), I18N.MESSAGE_CERTENTRYERROR, alias, e.getLocalizedMessage());
					LOG.warning(I18N.bundle(), I18N.MESSAGE_INVALIDCERTENTRY, alias, keyFile, crtFile, csrFile,
							crlFile);
				}
			} else {
				LOG.warning(I18N.bundle(), I18N.MESSAGE_INCOMPLETECERTENTRY, alias, keyFile, crtFile, csrFile, crlFile);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.storeHomePath.toString();
	}

	/**
	 * Get the store's home path.
	 *
	 * @return The store's home path.
	 */
	public Path getHome() {
		return this.storeHomePath;
	}

	/**
	 * Get the number of certificate entries currently in the store.
	 *
	 * @return The number of certificate entries currently in the store.
	 */
	public int getEntryCount() {
		return this.storeEntries.size();
	}

	/**
	 * Get the store's preferences.
	 *
	 * @return The store's preferences.
	 */
	public Preferences getPreferences() {
		return PropertiesPreferencesFactory.fromFile(this.storeHomePath.resolve(STORE_PREFERENCES));
	}

	/**
	 * Create a new alias name.
	 *
	 * @return The created alias.
	 */
	public synchronized String createAlias() {
		int aliasNumber = this.storeEntries.size() + 1;
		String alias;

		while (true) {
			alias = MessageFormat.format(DEFAULT_ALIAS_PATTERN, aliasNumber);
			if (isNewAlias(alias)) {
				break;
			}
			aliasNumber++;
		}
		return alias;
	}

	/**
	 * Check whether an alias name is not yet in use.
	 *
	 * @param alias The alias name to check.
	 * @return true, if the alias is not yet in use.
	 */
	public boolean isNewAlias(String alias) {
		Path keyFile = this.storeHomePath.resolve(DIR_KEY).resolve(alias + EXT_KEY);
		Path crtFile = this.storeHomePath.resolve(DIR_CRT).resolve(alias + EXT_CRT);
		Path csrFile = this.storeHomePath.resolve(DIR_CSR).resolve(alias + EXT_CSR);
		Path crlFile = this.storeHomePath.resolve(DIR_CRL).resolve(alias + EXT_CRL);

		return !Files.exists(keyFile) && !Files.exists(crtFile) && !Files.exists(csrFile) && !Files.exists(crlFile);
	}

	/**
	 * Get the root certificate entries currently in the store.
	 *
	 * @return The root certificate entries currently in the store.
	 */
	public synchronized Collection<CertStoreEntry> getRootEntries() {
		return Collections.unmodifiableCollection(this.storeEntries.values(e -> e.isRoot()));
	}

	/**
	 * Get a certificate entry's issued certificate entries.
	 *
	 * @param entry The certificate entry to get the issued certificate entries
	 *        for.
	 * @return The submitted certificate entry's issued certificate entries.
	 */
	public synchronized Collection<CertStoreEntry> getIssuedEntries(CertStoreEntry entry) {
		assert entry != null;

		return Collections
				.unmodifiableCollection(this.storeEntries.values(e -> !e.isRoot() && entry.equals(e.getIssuer())));
	}

	/**
	 * Rename a certificate store entry's alias.
	 *
	 * @param entry The certificate store entry to rename.
	 * @param newAlias The new alias name.
	 * @throws IOException if an I/O error occurs while renaming the entry
	 *         files.
	 */
	public synchronized void renameEntry(CertStoreEntry entry, String newAlias) throws IOException {
		Entry renameEntry = this.storeEntries.get(entry);

		if (renameEntry != null && !renameEntry.getAlias().equals(newAlias)) {
			Path newKeyFile = this.storeHomePath.resolve(DIR_KEY).resolve(newAlias + EXT_KEY);
			Path newCRTFile = this.storeHomePath.resolve(DIR_CRT).resolve(newAlias + EXT_CRT);
			Path newCSRFile = this.storeHomePath.resolve(DIR_CSR).resolve(newAlias + EXT_CSR);
			Path newCRLFile = this.storeHomePath.resolve(DIR_CRL).resolve(newAlias + EXT_CRL);

			renameEntry.rename(newAlias, newKeyFile, newCRTFile, newCSRFile, newCRLFile);
		}
	}

	/**
	 * Change a certificate store entry's key password.
	 *
	 * @param entry The certificate store entry to change the password for.
	 * @param oldPassword The password callback to use for key decryption.
	 * @param newPassword The password callback to use for key encryption.
	 * @throws IOException if an I/O error occurs while changing the password.
	 */
	public synchronized void changePassword(CertStoreEntry entry, PasswordCallback oldPassword,
			PasswordCallback newPassword) throws IOException {
		assert entry != null;
		assert oldPassword != null;
		assert newPassword != null;

		if (entry.hasKey()) {
			KeyPair key = entry.getKey(oldPassword).getObject();

			writeKey(entry.getAlias(), key, newPassword, entry.getName());
		}
	}

	/**
	 * Delete a certificate entry and all of it's issued entries.
	 *
	 * @param entry The entry to delete.
	 * @throws IOException if an I/O error occurs while deleting the entry.
	 */
	public synchronized void deleteEntry(CertStoreEntry entry) throws IOException {
		Entry deleteEntry = this.storeEntries.get(entry);

		if (deleteEntry != null) {
			ArrayList<Entry> issuedEntries = new ArrayList<>();

			for (Entry storeEntry : this.storeEntries.values()) {
				if (entry.equals(storeEntry.getIssuer()) && !storeEntry.isRoot()) {
					issuedEntries.add(storeEntry);
				}
			}
			for (Entry issuedEntry : issuedEntries) {
				deleteEntry(issuedEntry);
			}
			this.storeEntries.remove(deleteEntry);
			deleteEntry.delete();
		}
	}

	/**
	 * Get a store entry.
	 *
	 * @param entry The the certificate entry to retrieve from the store.
	 * @return The found store entry or null, if the certificate entry is not
	 *         known in the store.
	 */
	public synchronized CertStoreEntry getEntry(CertEntry entry) {
		return this.storeEntries.get(entry);
	}

	/**
	 * Find matching store entry.
	 *
	 * @param entry The certificate entry to match.
	 * @return The matching entry or null if none matches.
	 * @throws IOException If an I/O exception occurs while accessing the
	 *         certificate data.
	 */
	public synchronized CertStoreEntry matchEntry(CertEntry entry) throws IOException {
		assert entry != null;

		return matchEntry0(entry);
	}

	/**
	 * Import certificate entry into the store.
	 *
	 * @param entry The certificate entry to import (must contain either CRT or
	 *        CSR object).
	 * @param password The password callback to use for key encryption (may be
	 *        null).
	 * @param overwrite Whether to overwrite certificate objects already
	 *        available in the store or not.
	 * @return The imported certificate store entry.
	 * @throws PasswordRequiredException if a password callback was submitted
	 *         but provided no password.
	 * @throws IOException if an I/O error occurs during import.
	 */
	public synchronized CertStoreEntry importEntry(CertEntry entry, PasswordCallback password, boolean overwrite)
			throws PasswordRequiredException, IOException {
		Entry importedEntry = matchEntry0(entry);

		if (importedEntry != null) {
			String name = importedEntry.getName();
			String alias = importedEntry.getAlias();
			Path keyFile = null;
			Path crtFile = null;
			Path csrFile = null;
			Path crlFile = null;

			if ((overwrite || !importedEntry.hasKey(true)) && entry.hasKey()) {
				keyFile = writeKey(alias, entry.getKey().getObject(), password, name);
			}
			if ((overwrite || !importedEntry.hasCRT()) && entry.hasCRT()) {
				crtFile = writeCRT(alias, entry.getCRT().getObject());
			}
			if ((overwrite || !importedEntry.hasCSR()) && entry.hasCSR()) {
				csrFile = writeCSR(alias, entry.getCSR().getObject());
			}
			if ((overwrite || !importedEntry.hasCRL()) && entry.hasCRL()) {
				crlFile = writeCRL(alias, entry.getCRL().getObject());
			}
			if (keyFile != null || crtFile != null || csrFile != null || crlFile != null) {
				importedEntry.merge(alias, keyFile, crtFile, csrFile, crlFile);
			} else {
				LOG.notice(I18N.bundle(), I18N.MESSAGE_NOTHINGTOIMPORT, name);
			}
		} else if (entry.hasCRT() || entry.hasCSR()) {
			String name = (entry.hasCRT() ? CertObject.getCRTName(entry.getCRT().getObject())
					: CertObject.getCSRName(entry.getCSR().getObject()));
			String alias = createAlias();
			Path keyFile = null;
			Path crtFile = null;
			Path csrFile = null;
			Path crlFile = null;

			if (entry.hasKey()) {
				keyFile = writeKey(alias, entry.getKey().getObject(), password, name);
			}
			if (entry.hasCRT()) {
				crtFile = writeCRT(alias, entry.getCRT().getObject());
			}
			if (entry.hasCSR()) {
				csrFile = writeCSR(alias, entry.getCSR().getObject());
			}
			if (entry.hasCRL()) {
				crlFile = writeCRL(alias, entry.getCRL().getObject());
			}
			importedEntry = new Entry(name, alias, keyFile, crtFile, csrFile, crlFile);
		} else {
			throw new IllegalArgumentException("Import entry '" + entry.getName() + "' has no CRT object");
		}
		mergeEntry0(importedEntry);
		return importedEntry;
	}

	/**
	 * Generate and sign a CRT entry.
	 *
	 * @param alias The alias to use for certificate store entry creation.
	 * @param keyParams The parameters to use for key creation.
	 * @param certificateParams The parameters to use for CRT creation.
	 * @param certificateValidity The validity range for the certificate.
	 * @param password The password callback to use to query the password for
	 *        the certificate.
	 * @param issuerEntry The (optional) issuer entry.
	 * @param issuerPassword The password callback to user to query the issuer
	 *        entry's key password.
	 * @return The created certificate store entry.
	 * @throws IOException if an I/O error occurs during entry creation.
	 * @throws GeneralSecurityException if an security provider related error
	 *         occurs.
	 */
	public synchronized CertStoreEntry generateAndSignCRT(String alias, KeyParams keyParams,
			X509CertificateParams certificateParams, CertificateValidity certificateValidity, PasswordCallback password,
			CertStoreEntry issuerEntry, PasswordCallback issuerPassword) throws IOException, GeneralSecurityException {
		assert alias != null;
		assert keyParams != null;
		assert certificateParams != null;
		assert certificateValidity != null;
		assert password != null;
		assert issuerEntry == null || issuerPassword != null;

		KeyPair issuerKey = null;
		X509Certificate issuerCRT = null;
		BigInteger serial = BigInteger.ONE;

		if (issuerEntry != null) {
			issuerKey = issuerEntry.getKey(issuerPassword).getObject();
			issuerCRT = issuerEntry.getCRT().getObject();
			serial = nextSerial(issuerEntry);
		}

		KeyPair key = PROVIDER.generateKey(keyParams);
		X509Certificate crt = PROVIDER.generateAndSignCRT(key, certificateParams, certificateValidity, issuerKey,
				issuerCRT, serial);
		String entryName = crt.getSubjectX500Principal().toString();
		Path keyFile = writeKey(alias, key, password, entryName);
		Path crtFile = writeCRT(alias, crt);
		Entry entry = new Entry(entryName, alias, keyFile, crtFile, null, null);

		mergeEntry0(entry);
		return entry;
	}

	/**
	 * Re-generate and re-sign a CRT entry.
	 *
	 * @param entry The certificate store entry to re-generate and re-sign.
	 * @param certificateParams The parameters to use for CRT creation.
	 * @param certificateValidity The validity range for the certificate.
	 * @param issuerPassword The password callback to user to query the issuer
	 *        entry's key password.
	 * @return The updated certificate store entry.
	 * @throws IOException if an I/O error occurs during entry creation.
	 * @throws GeneralSecurityException if an security provider related error
	 *         occurs.
	 */
	public synchronized CertStoreEntry generateAndSignCRT(CertStoreEntry entry, X509CertificateParams certificateParams,
			CertificateValidity certificateValidity, PasswordCallback issuerPassword)
			throws IOException, GeneralSecurityException {
		assert entry != null;
		assert certificateParams != null;
		assert certificateValidity != null;

		KeyPair issuerKey = entry.getIssuer().getKey(issuerPassword).getObject();
		X509Certificate crt = PROVIDER.generateAndSignCRT(entry.getCRT().getObject(), certificateParams,
				certificateValidity, issuerKey);

		writeCRT(entry.getAlias(), crt);
		return entry;
	}

	/**
	 * Generate and sign a CSR entry.
	 *
	 * @param alias The alias to use for certificate store entry creation.
	 * @param keyParams The parameters to use for key creation.
	 * @param certificateParams The parameters to use for CRT creation.
	 * @param password The password callback to use to query the password for
	 *        the certificate.
	 * @return The created certificate store entry.
	 * @throws IOException if an I/O error occurs during entry creation.
	 * @throws GeneralSecurityException if an security provider related error
	 *         occurs.
	 */
	public synchronized CertStoreEntry generateAndSignCSR(String alias, KeyParams keyParams,
			X509CertificateParams certificateParams, PasswordCallback password)
			throws IOException, GeneralSecurityException {
		assert alias != null;
		assert keyParams != null;
		assert certificateParams != null;
		assert password != null;

		KeyPair key = PROVIDER.generateKey(keyParams);
		PKCS10Object csr = PROVIDER.generateAndSignCSR(key, certificateParams);
		String entryName = csr.getSubjectX500Principal().toString();
		Path keyFile = writeKey(alias, key, password, entryName);
		Path csrFile = writeCSR(alias, csr);
		Entry entry = new Entry(entryName, alias, keyFile, null, csrFile, null);

		mergeEntry0(entry);
		return entry;
	}

	/**
	 * Re-generate and re-sign a CSR entry.
	 *
	 * @param entry The certificate store entry to re-generate and re-sign.
	 * @param certificateParams The parameters to use for CRT creation.
	 * @param password The password callback to use to query the password for
	 *        the certificate.
	 * @return The update certificate store entry.
	 * @throws IOException if an I/O error occurs during entry creation.
	 * @throws GeneralSecurityException if an security provider related error
	 *         occurs.
	 */
	public synchronized CertStoreEntry generateAndSignCSR(CertStoreEntry entry, X509CertificateParams certificateParams,
			PasswordCallback password) throws IOException, GeneralSecurityException {
		assert entry != null;
		assert certificateParams != null;
		assert password != null;

		KeyPair key = entry.getKey(password).getObject();
		PKCS10Object csr = PROVIDER.generateAndSignCSR(entry.getCSR().getObject(), key, certificateParams);

		writeCSR(entry.getAlias(), csr);
		return entry;
	}

	/**
	 * Generate and sign a CRL.
	 *
	 * @param entry The certificate store entry to generate the CRL for.
	 * @param crlParams The parameters to use for CRL generation.
	 * @param revokeEntries The issued entries to revoke.
	 * @param password The password to use to query the password for CRL
	 *        signing.
	 * @param append Whether to append or replace the CRLs revoke list.
	 * @return The update certificate store entry.
	 * @throws IOException if an I/O error occurs during entry modification.
	 * @throws GeneralSecurityException if an security provider related error
	 *         occurs.
	 */
	public synchronized CertStoreEntry generateAndSignCRL(CertStoreEntry entry, X509CRLParams crlParams,
			Map<CertStoreEntry, RevokeReason> revokeEntries, PasswordCallback password, boolean append)
			throws IOException, GeneralSecurityException {
		assert entry != null;
		assert crlParams != null;
		assert revokeEntries != null;
		assert password != null;

		Entry storeEntry = this.storeEntries.get(entry);

		assert storeEntry != null;

		HashMap<BigInteger, RevokeReason> revokeSerials = new HashMap<>(revokeEntries.size());
		X509CRL currentCRL = null;

		if (storeEntry.hasCRL()) {
			currentCRL = storeEntry.getCRL().getObject();
			if (append) {
				for (X509CRLEntry crlEntry : currentCRL.getRevokedCertificates()) {
					revokeSerials.put(crlEntry.getSerialNumber(),
							RevokeReason.valueOf(crlEntry.getRevocationReason().ordinal()));
				}
			}
		}
		for (Map.Entry<CertStoreEntry, RevokeReason> revokeEntry : revokeEntries.entrySet()) {
			BigInteger revokeSerial = revokeEntry.getKey().getCRT().getObject().getSerialNumber();

			revokeSerials.put(revokeSerial, revokeEntry.getValue());
		}

		KeyPair issuerKey = storeEntry.getKey(password).getObject();
		X509Certificate issuerCRT = storeEntry.getCRT().getObject();
		X509CRL crl = PROVIDER.generateAndSignCRL(currentCRL, crlParams, revokeSerials, issuerKey, issuerCRT);

		Path crlFile = writeCRL(storeEntry.getAlias(), crl);

		storeEntry.setCRL(crlFile);
		return entry;
	}

	private BigInteger nextSerial(CertEntry issuerEntry) throws IOException {
		CertEntry rootEntry = issuerEntry.getRootIssuer();
		BigInteger maxSerial = BigInteger.ZERO;

		for (CertStoreEntry entry : this.storeEntries.values()) {
			if (rootEntry.equals(entry.getRootIssuer())) {
				BigInteger entrySerial = entry.getCRT().getObject().getSerialNumber();

				if (entrySerial.compareTo(maxSerial) > 0) {
					maxSerial = entrySerial;
				}
			}
		}
		return maxSerial.add(BigInteger.ONE);
	}

	private Entry matchEntry0(CertEntry entry) throws IOException {
		assert entry != null;

		Entry matchingEntry = null;

		if (entry.hasKey(false)) {
			matchingEntry = this.storeEntries.matchKey(entry.getKey().getObject());
		} else if (entry.hasCRT()) {
			matchingEntry = this.storeEntries.matchCRT(entry.getCRT().getObject());
		} else if (entry.hasCSR()) {
			matchingEntry = this.storeEntries.matchCSR(entry.getCSR().getObject());
		} else if (entry.hasCRL()) {
			matchingEntry = this.storeEntries.matchCRL(entry.getCRL().getObject());
		}
		return matchingEntry;
	}

	private void mergeEntry0(Entry entry) throws IOException {
		this.storeEntries.put(entry, entry);
		if (entry.hasCRT()) {
			Entry issuerEntry = null;
			Entry externalIssuerEntry = null;
			ArrayList<Entry> issuedEntries = new ArrayList<>();
			X509Certificate crt = entry.getCRT().getObject();
			String crtIssuerName = CertObject.getCRTIssuerName(crt);

			for (Entry storeEntry : this.storeEntries.values(e -> e.isExternal() || e.hasCRT())) {
				if (issuerEntry == null) {
					if (entry.isIssuedBy(storeEntry)) {
						issuerEntry = storeEntry;
					} else if (storeEntry.isExternal() && externalIssuerEntry == null
							&& storeEntry.getName().equals(crtIssuerName)) {
						externalIssuerEntry = storeEntry;
					}
				}
				if (!storeEntry.equals(entry) && storeEntry.isIssuedBy(entry)) {
					issuedEntries.add(storeEntry);
				}
			}
			if (issuerEntry == null) {
				issuerEntry = externalIssuerEntry;
				if (issuerEntry == null) {
					issuerEntry = new Entry(crtIssuerName);
					this.storeEntries.put(issuerEntry, issuerEntry);
				}
			}
			entry.setIssuer(issuerEntry);

			HashSet<Entry> obsoleteIssuers = new HashSet<>(issuedEntries.size());

			for (Entry issuedEntry : issuedEntries) {
				obsoleteIssuers.add(this.storeEntries.get(issuedEntry.getIssuer()));
				issuedEntry.setIssuer(entry);
			}
			for (Entry obsoleteIssuer : obsoleteIssuers) {
				if (obsoleteIssuer.isExternal()) {
					boolean obsoleteIssuerInUse = false;

					for (Entry storeEntry : this.storeEntries.values()) {
						if (!storeEntry.isRoot() && storeEntry.getIssuer().equals(obsoleteIssuer)) {
							obsoleteIssuerInUse = true;
							break;
						}
					}
					if (!obsoleteIssuerInUse) {
						this.storeEntries.remove(obsoleteIssuer);
					}
				}
			}
		} else {
			assert entry.hasCSR();

			entry.setIssuer(entry);
		}
	}

	private Path writeKey(String alias, KeyPair key, PasswordCallback password, String resource) throws IOException {
		Path keyFileHome = this.storeHomePath.resolve(DIR_KEY);

		Files.createDirectories(keyFileHome);

		Path keyFile = keyFileHome.resolve(alias + EXT_KEY);

		PROVIDER.writeKey(key, keyFile, password, resource);
		return keyFile;
	}

	private Path writeCRT(String alias, X509Certificate crt) throws IOException {
		Path crtFileHome = this.storeHomePath.resolve(DIR_CRT);

		Files.createDirectories(crtFileHome);

		Path crtFile = crtFileHome.resolve(alias + EXT_CRT);

		PROVIDER.writeCRT(crt, crtFile);
		return crtFile;
	}

	private Path writeCSR(String alias, PKCS10Object csr) throws IOException {
		Path csrFileHome = this.storeHomePath.resolve(DIR_CSR);

		Files.createDirectories(csrFileHome);

		Path csrFile = csrFileHome.resolve(alias + EXT_CSR);

		PROVIDER.writeCSR(csr, csrFile);
		return csrFile;
	}

	private Path writeCRL(String alias, X509CRL crl) throws IOException {
		Path crlFileHome = this.storeHomePath.resolve(DIR_CRL);

		Files.createDirectories(crlFileHome);

		Path crlFile = crlFileHome.resolve(alias + EXT_CRL);

		PROVIDER.writeCRL(crl, crlFile);
		return crlFile;
	}

	private class Entry extends CertStoreEntry {

		private String name;
		private String alias;
		private Path keyFile;
		private Path crtFile;
		private Path csrFile;
		private Path crlFile;
		private Entry issuer;

		Entry(String name) {
			this(name, null, null, null, null, null);
			this.issuer = this;
		}

		Entry(String name, String alias, Path keyFile, Path crtFile, Path csrFile, Path crlFile) {
			this.name = name;
			this.alias = alias;
			this.keyFile = keyFile;
			this.crtFile = crtFile;
			this.csrFile = csrFile;
			this.crlFile = crlFile;
			this.issuer = null;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#getName()
		 */
		@Override
		public String getName() {
			return this.name;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertStoreEntry#getStore()
		 */
		@Override
		public CertStore getStore() {
			return CertStore.this;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertStoreEntry#getAlias()
		 */
		@Override
		public String getAlias() {
			return this.alias;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertStoreEntry#isExternal()
		 */
		@Override
		public boolean isExternal() {
			return this.alias == null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * de.carne.certmgr.store.CertStoreEntry#copy(de.carne.certmgr.store.
		 * ExportTarget)
		 */
		@Override
		public void copy(ExportTarget exportTarget) throws IOException {
			if (exportTarget instanceof ExportTarget.FileList) {
				ExportTarget.FileList fileListExportTarget = (ExportTarget.FileList) exportTarget;
				ArrayList<Path> fileList = new ArrayList<>();

				if (this.keyFile != null) {
					fileList.add(this.keyFile);
				}
				if (this.crtFile != null) {
					fileList.add(this.crtFile);
				}
				if (this.csrFile != null) {
					fileList.add(this.csrFile);
				}
				if (this.crlFile != null) {
					fileList.add(this.crlFile);
				}
				fileListExportTarget.putFileList(fileList);
			} else {
				throw new UnsupportedExportTargetException(exportTarget);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#hasKey(boolean)
		 */
		@Override
		public boolean hasKey(boolean havePassword) {
			// Store key files are always password protected, hence password is
			// required.
			return this.keyFile != null && havePassword;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#getKey(de.carne.certmgr.store.
		 * PasswordCallback)
		 */
		@Override
		public CertObject<KeyPair> getKey(PasswordCallback password) throws PasswordRequiredException, IOException {
			KeyPair key = CertStore.PROVIDER.readKey(this.keyFile, password, this.name);
			String keyName = CertObject.getKeyName(key);

			return new SimpleCertObject<>(keyName, key);
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#hasCRT()
		 */
		@Override
		public boolean hasCRT() {
			return this.crtFile != null;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#getCRT()
		 */
		@Override
		public CertObject<X509Certificate> getCRT() throws IOException {
			X509Certificate crt = CertStore.PROVIDER.readCRT(this.crtFile);
			FileTime crtFileTime = Files.getLastModifiedTime(this.crtFile);
			String crtName = CertObject.getCRTName(crt);

			return new StoredCertObject<X509Certificate>(crtName, this.crtFile, crtFileTime, crt) {

				@Override
				protected X509Certificate readObject(Path objectFile) throws IOException {
					return CertStore.PROVIDER.readCRT(objectFile);
				}

			};
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#hasCSR()
		 */
		@Override
		public boolean hasCSR() {
			return this.csrFile != null;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#getCSR()
		 */
		@Override
		public CertObject<PKCS10Object> getCSR() throws IOException {
			PKCS10Object csr = CertStore.PROVIDER.readCSR(this.csrFile);
			FileTime csrFileTime = Files.getLastModifiedTime(this.csrFile);
			String csrName = CertObject.getCSRName(csr);

			return new StoredCertObject<PKCS10Object>(csrName, this.csrFile, csrFileTime, csr) {

				@Override
				protected PKCS10Object readObject(Path objectFile) throws IOException {
					return CertStore.PROVIDER.readCSR(objectFile);
				}

			};
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#hasCRL()
		 */
		@Override
		public boolean hasCRL() {
			return this.crlFile != null;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#getCRL()
		 */
		@Override
		public CertObject<X509CRL> getCRL() throws IOException {
			X509CRL crl = CertStore.PROVIDER.readCRL(this.crlFile);
			FileTime crlFileTime = Files.getLastModifiedTime(this.crlFile);
			String crlName = CertObject.getCRLName(crl);

			return new StoredCertObject<X509CRL>(crlName, this.crlFile, crlFileTime, crl) {

				@Override
				protected X509CRL readObject(Path objectFile) throws IOException {
					return CertStore.PROVIDER.readCRL(objectFile);
				}

			};
		}

		void setCRL(Path crlFile) {
			this.crlFile = crlFile;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#getIssuer()
		 */
		@Override
		public CertEntry getIssuer() {
			return this.issuer;
		}

		void setIssuer(Entry issuer) {
			this.issuer = issuer;
		}

		void merge(String newAlias, Path newKeyFile, Path newCRTFile, Path newCSRFile, Path newCRLFile) {
			if (newAlias != null) {
				this.alias = newAlias;
			}
			if (newKeyFile != null) {
				this.keyFile = newKeyFile;
			}
			if (newCRTFile != null) {
				this.crtFile = newCRTFile;
			}
			if (newCSRFile != null) {
				this.csrFile = newCSRFile;
			}
			if (newCRLFile != null) {
				this.crlFile = newCRLFile;
			}
		}

		void rename(String newAlias, Path newKeyFile, Path newCRTFile, Path newCSRFile, Path newCRLFile)
				throws IOException {
			Path oldKeyFile = this.keyFile;
			Path oldCRTFile = this.crtFile;
			Path oldCSRFile = this.csrFile;
			Path oldCRLFile = this.crlFile;

			if (oldKeyFile != null) {
				Files.copy(oldKeyFile, newKeyFile, StandardCopyOption.COPY_ATTRIBUTES);
			}
			if (oldCRTFile != null) {
				Files.copy(oldCRTFile, newCRTFile, StandardCopyOption.COPY_ATTRIBUTES);
			}
			if (oldCSRFile != null) {
				Files.copy(oldCSRFile, newCSRFile, StandardCopyOption.COPY_ATTRIBUTES);
			}
			if (oldCRLFile != null) {
				Files.copy(oldCRLFile, newCRLFile, StandardCopyOption.COPY_ATTRIBUTES);
			}
			if (oldKeyFile != null) {
				this.keyFile = newKeyFile;
			}
			if (oldCRTFile != null) {
				this.crtFile = newCRTFile;
			}
			if (oldCSRFile != null) {
				this.csrFile = newCSRFile;
			}
			if (oldCRLFile != null) {
				this.crlFile = newCRLFile;
			}
			this.alias = newAlias;
			if (oldKeyFile != null) {
				Files.delete(oldKeyFile);
			}
			if (oldCRTFile != null) {
				Files.delete(oldCRTFile);
			}
			if (oldCSRFile != null) {
				Files.delete(oldCSRFile);
			}
			if (oldCRLFile != null) {
				Files.delete(oldCRTFile);
			}
		}

		void delete() throws IOException {
			if (this.keyFile != null) {
				Files.delete(this.keyFile);
			}
			if (this.crtFile != null) {
				Files.delete(this.crtFile);
			}
			if (this.csrFile != null) {
				Files.delete(this.csrFile);
			}
			if (this.crlFile != null) {
				Files.delete(this.crlFile);
			}
		}

	}

}
