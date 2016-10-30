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
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.carne.certmgr.certs.io.PEMCertReaderWriter;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;
import de.carne.util.logging.Log;

/**
 * {@link UserCertStoreHandler} implementation providing file based storage.
 * <p>
 * Certificate objects are organized in the following directory structure:
 *
 * <pre>
 * ./*             (store directory)
 * ./certs/*.crt   (certificate files)
 * ./crl/*.crl     (certificate revocation lists)
 * ./csr/*.csr     (certificate signing requests)
 * ./private/*.key (encrypted key files)
 * </pre>
 *
 * A certificate object's file names are determined based upon the corresponding
 * entry id's alias attributes.
 */
class PersistentUserCertStoreHandler extends UserCertStoreHandler {

	private static final Log LOG = new Log();

	private static final Pattern INDEXED_ALIAS_PATTERN = Pattern.compile("(.*)\\d+$");
	private static final String INDEXED_ALIAS_FORMAT = "%s%d";

	static final String DIR_CRT = "certs";
	static final String DIR_KEY = "private";
	static final String DIR_CSR = "csr";
	static final String DIR_CRL = "crl";

	static final String EXTENSION_CRT = ".crt";
	static final String EXTENSION_KEY = ".key";
	static final String EXTENSION_CSR = ".csr";
	static final String EXTENSION_CRL = ".crl";

	final static PEMCertReaderWriter PEM_IO = new PEMCertReaderWriter();

	private int nextId = 1;

	PersistentUserCertStoreHandler(Path storeHome) {
		super(storeHome);
	}

	public Map<UserCertStoreEntryId, PersistentEntry> scanStore() throws IOException {
		PersistentEntryPathsScanner scanner = new PersistentEntryPathsScanner(LOG);

		Files.walkFileTree(storeHome(), scanner);
		return scanner.getResult();
	}

	@Override
	public UserCertStoreEntryId nextEntryId(String aliasHint) {
		String alias = null;

		if (aliasHint != null) {
			if (!isAliasInUse(aliasHint)) {
				alias = aliasHint;
			} else {
				Matcher indexedAliasMatcher = INDEXED_ALIAS_PATTERN.matcher(aliasHint);
				String indexedAliasBase = (indexedAliasMatcher.matches() ? indexedAliasMatcher.group(1) : aliasHint);
				int aliasIndex = 1;

				while (true) {
					alias = String.format(INDEXED_ALIAS_FORMAT, indexedAliasBase, aliasIndex);
					if (!isAliasInUse(alias)) {
						break;
					}
				}
			}
		}
		return (alias != null ? new UserCertStoreEntryId(0, alias) : new UserCertStoreEntryId(this.nextId++, null));
	}

	@Override
	public CRTEntry createCRTEntry(UserCertStoreEntryId id, X509Certificate crt) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KeyEntry createKeyEntry(UserCertStoreEntryId id, KeyPair key, PasswordCallback password) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CSREntry createCSREntry(UserCertStoreEntryId id, PKCS10CertificateRequest csr) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CRLEntry createCRLEntry(UserCertStoreEntryId id, X509CRL crl) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isAliasInUse(String alias) {
		return Files.exists(entryPath(DIR_CRT, alias, EXTENSION_CRT))
				|| Files.exists(entryPath(DIR_KEY, alias, EXTENSION_KEY))
				|| Files.exists(entryPath(DIR_CSR, alias, EXTENSION_CSR))
				|| Files.exists(entryPath(DIR_CRL, alias, EXTENSION_CRL));
	}

	PathMatcher entryMatcher(String dir, String ext) {
		return storeHome().getFileSystem().getPathMatcher("glob:**/" + dir + "/*" + ext);
	}

	Path entryPath(String dir, String alias, String ext) {
		return storeHome().resolve(dir).resolve(alias + ext);
	}

	UserCertStoreEntryId pathId(Path path) {
		String fileName = path.getFileName().toString();
		int extIndex = fileName.lastIndexOf('.');

		return new UserCertStoreEntryId(0, (extIndex > 0 ? fileName.substring(0, extIndex) : fileName));
	}

	CRTEntry toCRTEntry(UserCertStoreEntryId id, Path crtPath) {
		return (crtPath != null ? new PersistentCRTEntry(id) : null);
	}

	KeyEntry toKeyEntry(UserCertStoreEntryId id, Path keyPath) {
		return (keyPath != null ? new PersistentKeyEntry(id) : null);
	}

	CSREntry toCSREntry(UserCertStoreEntryId id, Path csrPath) {
		return (csrPath != null ? new PersistentCSREntry(id) : null);
	}

	CRLEntry toCRLEntry(UserCertStoreEntryId id, Path crlPath) {
		return (crlPath != null ? new PersistentCRLEntry(id) : null);
	}

	private class PersistentEntryPathsScanner extends SimpleFileVisitor<Path> {

		private final PathMatcher crtMatcher = entryMatcher(DIR_CRT, EXTENSION_CRT);
		private final PathMatcher keyMatcher = entryMatcher(DIR_KEY, EXTENSION_KEY);
		private final PathMatcher csrMatcher = entryMatcher(DIR_CSR, EXTENSION_CSR);
		private final PathMatcher crlMatcher = entryMatcher(DIR_CRL, EXTENSION_CRL);

		private final Log log;

		private final Map<UserCertStoreEntryId, PersistentEntry> result = new HashMap<>();

		PersistentEntryPathsScanner(Log log) {
			this.log = log;
		}

		public Map<UserCertStoreEntryId, PersistentEntry> getResult() {
			return this.result;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			if (this.crtMatcher.matches(file)) {
				this.log.info("Found CRT file: ''{0}''", file);
				updateResult(pathId(file), file, null, null, null);
			} else if (this.keyMatcher.matches(file)) {
				this.log.info("Found Key file: ''{0}''", file);
				updateResult(pathId(file), null, file, null, null);
			} else if (this.csrMatcher.matches(file)) {
				this.log.info("Found CSR file: ''{0}''", file);
				updateResult(pathId(file), null, null, file, null);
			} else if (this.crlMatcher.matches(file)) {
				this.log.info("Found CRL file: ''{0}''", file);
				updateResult(pathId(file), null, null, null, file);
			} else {
				this.log.debug("Ignoring file: ''{0}''", file);
			}
			return FileVisitResult.CONTINUE;
		}

		private void updateResult(UserCertStoreEntryId id, Path crtPath, Path keyPath, Path csrPath, Path crlPath) {
			PersistentEntry resultEntry = new PersistentEntry(this.result.get(id), toCRTEntry(id, crtPath),
					toKeyEntry(id, keyPath), toCSREntry(id, csrPath), toCRLEntry(id, crlPath));

			this.result.put(id, resultEntry);
		}

	}

	private class PersistentCRTEntry extends PersistentObjectEntry<X509Certificate> implements CRTEntry {

		private final UserCertStoreEntryId id;

		PersistentCRTEntry(UserCertStoreEntryId id) {
			super(entryPath(DIR_CRT, id.getAlias(), EXTENSION_CRT));
			this.id = id;
		}

		PersistentCRTEntry(UserCertStoreEntryId id, X509Certificate crt, FileTime crtFileTime) {
			super(entryPath(DIR_CRT, id.getAlias(), EXTENSION_CRT), crt, crtFileTime);
			this.id = id;
		}

		@Override
		public X509Certificate getCRT() throws IOException {
			return getEntry(NoPassword.getInstance());
		}

		@Override
		protected X509Certificate decodeEntryInput(InputStream input, PasswordCallback password) throws IOException {
			return toEntryObject(X509Certificate.class, PEM_IO.read(input, this.id.getAlias(), password));
		}

	}

	private class PersistentKeyEntry extends PersistentObjectEntry<KeyPair> implements KeyEntry {

		private final UserCertStoreEntryId id;

		PersistentKeyEntry(UserCertStoreEntryId id) {
			super(entryPath(DIR_KEY, id.getAlias(), EXTENSION_KEY));
			this.id = id;
		}

		PersistentKeyEntry(UserCertStoreEntryId id, KeyPair key, FileTime keyFileTime) {
			super(entryPath(DIR_KEY, id.getAlias(), EXTENSION_KEY), key, keyFileTime);
			this.id = id;
		}

		@Override
		public boolean isDecrypted() {
			return false;
		}

		@Override
		public KeyPair getKey(PasswordCallback password) throws IOException {
			return getEntry(password);
		}

		@Override
		protected KeyPair decodeEntryInput(InputStream input, PasswordCallback password) throws IOException {
			return toEntryObject(KeyPair.class, PEM_IO.read(input, this.id.getAlias(), password));
		}

	}

	private class PersistentCSREntry extends PersistentObjectEntry<PKCS10CertificateRequest> implements CSREntry {

		private final UserCertStoreEntryId id;

		PersistentCSREntry(UserCertStoreEntryId id) {
			super(entryPath(DIR_CSR, id.getAlias(), EXTENSION_CSR));
			this.id = id;
		}

		PersistentCSREntry(UserCertStoreEntryId id, PKCS10CertificateRequest csr, FileTime csrFileTime) {
			super(entryPath(DIR_CSR, id.getAlias(), EXTENSION_CSR), csr, csrFileTime);
			this.id = id;
		}

		@Override
		public PKCS10CertificateRequest getCSR() throws IOException {
			return getEntry(NoPassword.getInstance());
		}

		@Override
		protected PKCS10CertificateRequest decodeEntryInput(InputStream input, PasswordCallback password)
				throws IOException {
			return toEntryObject(PKCS10CertificateRequest.class, PEM_IO.read(input, this.id.getAlias(), password));
		}

	}

	private class PersistentCRLEntry extends PersistentObjectEntry<X509CRL> implements CRLEntry {

		private final UserCertStoreEntryId id;

		PersistentCRLEntry(UserCertStoreEntryId id) {
			super(entryPath(DIR_CRL, id.getAlias(), EXTENSION_CRL));
			this.id = id;
		}

		PersistentCRLEntry(UserCertStoreEntryId id, X509CRL crl, FileTime crlFileTime) {
			super(entryPath(DIR_CRL, id.getAlias(), EXTENSION_CRL), crl, crlFileTime);
			this.id = id;
		}

		@Override
		public X509CRL getCRL() throws IOException {
			return getEntry(NoPassword.getInstance());
		}

		@Override
		protected X509CRL decodeEntryInput(InputStream input, PasswordCallback password) throws IOException {
			return toEntryObject(X509CRL.class, PEM_IO.read(input, this.id.getAlias(), password));
		}

	}

}
