/*
 * Copyright (c) 2015-2018 Holger de Carne and contributors, All Rights Reserved.
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
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.carne.boot.logging.Log;
import de.carne.certmgr.certs.io.IOResource;
import de.carne.certmgr.certs.io.PEMCertReaderWriter;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;
import de.carne.check.Check;
import de.carne.check.Nullable;
import de.carne.nio.file.attribute.FileAttributes;

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
 * A certificate object's file names are determined based upon the corresponding entry id's alias attributes.
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

	private int nextId = 1;

	private final Path storeHome;

	PersistentUserCertStoreHandler(Path storeHome) {
		this.storeHome = storeHome;
	}

	@Override
	public Path storeHome() {
		return this.storeHome;
	}

	public Map<UserCertStoreEntryId, PersistentEntry> scanStore() throws IOException {
		PersistentEntryPathsScanner scanner = new PersistentEntryPathsScanner(LOG);

		Files.walkFileTree(storeHome(), scanner);
		return scanner.getResult();
	}

	@Override
	public UserCertStoreEntryId nextEntryId(@Nullable String aliasHint) {
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
					aliasIndex++;
				}
			}
		}
		return (alias != null ? new UserCertStoreEntryId(0, alias) : new UserCertStoreEntryId(this.nextId++, null));
	}

	@Override
	public CertObjectHolder<X509Certificate> createCRT(UserCertStoreEntryId id, X509Certificate crt)
			throws IOException {
		String alias = Check.notNull(id.getAlias());
		Path crtPath = entryPath(DIR_CRT, alias, EXTENSION_CRT);

		Files.createDirectories(crtPath.getParent(), FileAttributes.userDirectoryDefault(storeHome()));
		try (IOResource<OutputStream> out = IOResource.newOutputStream(alias, crtPath, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			PEMCertReaderWriter.writeCRTBinary(out, crt);
		}
		return new PersistentCRTEntry(id, crt, Files.getLastModifiedTime(crtPath));
	}

	@Override
	public SecureCertObjectHolder<KeyPair> createKey(UserCertStoreEntryId id, KeyPair key, PasswordCallback newPassword)
			throws IOException {
		String alias = Check.notNull(id.getAlias());
		Path keyPath = entryPath(DIR_KEY, alias, EXTENSION_KEY);

		Files.createDirectories(keyPath.getParent(), FileAttributes.userDirectoryDefault(storeHome()));
		try (IOResource<OutputStream> out = IOResource.newOutputStream(alias, keyPath, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			PEMCertReaderWriter.writeKeyBinary(out, key, newPassword);
		}
		return new PersistentKeyEntry(id);
	}

	@Override
	public CertObjectHolder<PKCS10CertificateRequest> createCSR(UserCertStoreEntryId id, PKCS10CertificateRequest csr)
			throws IOException {
		String alias = Check.notNull(id.getAlias());
		Path csrPath = entryPath(DIR_CSR, alias, EXTENSION_CSR);

		Files.createDirectories(csrPath.getParent(), FileAttributes.userDirectoryDefault(storeHome()));
		try (IOResource<OutputStream> out = IOResource.newOutputStream(alias, csrPath, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			PEMCertReaderWriter.writeCSRBinary(out, csr);
		}
		return new PersistentCSREntry(id, csr, Files.getLastModifiedTime(csrPath));
	}

	@Override
	public CertObjectHolder<X509CRL> createCRL(UserCertStoreEntryId id, X509CRL crl) throws IOException {
		String alias = Check.notNull(id.getAlias());
		Path crlPath = entryPath(DIR_CRL, alias, EXTENSION_CRL);

		Files.createDirectories(crlPath.getParent(), FileAttributes.userDirectoryDefault(storeHome()));
		try (IOResource<OutputStream> out = IOResource.newOutputStream(alias, crlPath, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			PEMCertReaderWriter.writeCRLBinary(out, crl);
		}
		return new PersistentCRLEntry(id, crl, Files.getLastModifiedTime(crlPath));
	}

	@Override
	public void deleteEntry(UserCertStoreEntryId id) throws IOException {
		String alias = id.getAlias();

		if (alias != null) {
			Files.deleteIfExists(entryPath(DIR_CRT, alias, EXTENSION_CRT));
			Files.deleteIfExists(entryPath(DIR_KEY, alias, EXTENSION_KEY));
			Files.deleteIfExists(entryPath(DIR_CSR, alias, EXTENSION_CSR));
			Files.deleteIfExists(entryPath(DIR_CRL, alias, EXTENSION_CRL));
		}
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

	@Nullable
	CertObjectHolder<X509Certificate> toCRT(UserCertStoreEntryId id, @Nullable Path crtPath) {
		return (crtPath != null ? new PersistentCRTEntry(id) : null);
	}

	@Nullable
	SecureCertObjectHolder<KeyPair> toKey(UserCertStoreEntryId id, @Nullable Path keyPath) {
		return (keyPath != null ? new PersistentKeyEntry(id) : null);
	}

	@Nullable
	CertObjectHolder<PKCS10CertificateRequest> toCSR(UserCertStoreEntryId id, @Nullable Path csrPath) {
		return (csrPath != null ? new PersistentCSREntry(id) : null);
	}

	@Nullable
	CertObjectHolder<X509CRL> toCRL(UserCertStoreEntryId id, @Nullable Path crlPath) {
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
		public FileVisitResult visitFile(@Nullable Path file, @Nullable BasicFileAttributes attrs) throws IOException {
			assert file != null;

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

		private void updateResult(UserCertStoreEntryId id, @Nullable Path crtPath, @Nullable Path keyPath,
				@Nullable Path csrPath, @Nullable Path crlPath) {
			PersistentEntry resultEntry = new PersistentEntry(this.result.get(id), toCRT(id, crtPath),
					toKey(id, keyPath), toCSR(id, csrPath), toCRL(id, crlPath));

			this.result.put(id, resultEntry);
		}

	}

	private abstract class PersistentCertObjectHolder<T> implements CertObjectHolder<T> {

		private final UserCertStoreEntryId id;

		private final Path path;

		private SoftReference<T> cached;
		private FileTime cachedFileTime;

		protected PersistentCertObjectHolder(UserCertStoreEntryId id, Path path) {
			this(id, path, null, null);
		}

		protected PersistentCertObjectHolder(UserCertStoreEntryId id, Path path, @Nullable T object,
				@Nullable FileTime fileTime) {
			this.id = id;
			this.path = path;
			this.cached = new SoftReference<>(object);
			this.cachedFileTime = (fileTime != null ? fileTime : FileTime.fromMillis(0));
		}

		@Override
		public Path path() {
			return this.path;
		}

		@Override
		public synchronized T get() throws IOException {
			T object = this.cached.get();
			FileTime pathFileTime = Files.getLastModifiedTime(this.path);

			if (object == null || !this.cachedFileTime.equals(pathFileTime)) {
				try (IOResource<InputStream> in = IOResource.newInputStream(Check.notNull(this.id.getAlias()),
						this.path, StandardOpenOption.READ)) {
					object = read(in);
				}
				this.cached = new SoftReference<>(object);
				this.cachedFileTime = pathFileTime;
			}
			return object;
		}

		protected abstract T read(IOResource<InputStream> in) throws IOException;

	}

	private abstract class PersistentSecureCertObjectHolder<T> implements SecureCertObjectHolder<T> {

		private final UserCertStoreEntryId id;

		private final Path path;

		protected PersistentSecureCertObjectHolder(UserCertStoreEntryId id, Path path) {
			this.id = id;
			this.path = path;
		}

		@Override
		public Path path() {
			return this.path;
		}

		@Override
		public T get() throws IOException {
			throw new PasswordRequiredException(Check.notNull(this.id.getAlias()));
		}

		@Override
		public boolean isSecured() {
			return true;
		}

		@Override
		public T get(PasswordCallback password) throws IOException {
			T object;

			try (IOResource<InputStream> in = IOResource.newInputStream(Check.notNull(this.id.getAlias()), this.path,
					StandardOpenOption.READ)) {
				object = read(in, password);
			}
			return object;
		}

		protected abstract T read(IOResource<InputStream> in, PasswordCallback password) throws IOException;

	}

	private class PersistentCRTEntry extends PersistentCertObjectHolder<X509Certificate> {

		PersistentCRTEntry(UserCertStoreEntryId id) {
			super(id, entryPath(DIR_CRT, Check.notNull(id.getAlias()), EXTENSION_CRT));
		}

		PersistentCRTEntry(UserCertStoreEntryId id, X509Certificate crt, FileTime crtFileTime) {
			super(id, entryPath(DIR_CRT, Check.notNull(id.getAlias()), EXTENSION_CRT), crt, crtFileTime);
		}

		@Override
		protected X509Certificate read(IOResource<InputStream> in) throws IOException {
			return PEMCertReaderWriter.readCRTBinary(in);
		}

	}

	private class PersistentKeyEntry extends PersistentSecureCertObjectHolder<KeyPair> {

		PersistentKeyEntry(UserCertStoreEntryId id) {
			super(id, entryPath(DIR_KEY, Check.notNull(id.getAlias()), EXTENSION_KEY));
		}

		@Override
		protected KeyPair read(IOResource<InputStream> in, PasswordCallback password) throws IOException {
			return PEMCertReaderWriter.readKeyBinary(in, password);
		}

	}

	private class PersistentCSREntry extends PersistentCertObjectHolder<PKCS10CertificateRequest> {

		PersistentCSREntry(UserCertStoreEntryId id) {
			super(id, entryPath(DIR_CSR, Check.notNull(id.getAlias()), EXTENSION_CSR));
		}

		PersistentCSREntry(UserCertStoreEntryId id, PKCS10CertificateRequest csr, FileTime csrFileTime) {
			super(id, entryPath(DIR_CSR, Check.notNull(id.getAlias()), EXTENSION_CSR), csr, csrFileTime);
		}

		@Override
		protected PKCS10CertificateRequest read(IOResource<InputStream> in) throws IOException {
			return PEMCertReaderWriter.readCSRBinary(in);
		}

	}

	private class PersistentCRLEntry extends PersistentCertObjectHolder<X509CRL> {

		PersistentCRLEntry(UserCertStoreEntryId id) {
			super(id, entryPath(DIR_CRL, Check.notNull(id.getAlias()), EXTENSION_CRL));
		}

		PersistentCRLEntry(UserCertStoreEntryId id, X509CRL crl, FileTime crlFileTime) {
			super(id, entryPath(DIR_CRL, Check.notNull(id.getAlias()), EXTENSION_CRL), crl, crlFileTime);
		}

		@Override
		protected X509CRL read(IOResource<InputStream> in) throws IOException {
			return PEMCertReaderWriter.readCRLBinary(in);
		}

	}

}
