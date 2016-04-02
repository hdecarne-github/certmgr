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
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.carne.certmgr.util.logging.Log;

/**
 * Helper class used to scan the files inside a certificate store.
 */
class CertStoreScanner implements FileVisitor<Path> {

	private static final Log LOG = new Log(CertStoreScanner.class);

	private PathMatcher keyMatcher;
	private PathMatcher crtMatcher;
	private PathMatcher csrMatcher;
	private PathMatcher crlMatcher;

	private Map<String, Path> keyFiles = new HashMap<>();
	private Map<String, Path> crtFiles = new HashMap<>();
	private Map<String, Path> csrFiles = new HashMap<>();
	private Map<String, Path> crlFiles = new HashMap<>();

	CertStoreScanner(PathMatcher keyMatcher, PathMatcher crtMatcher, PathMatcher csrMatcher, PathMatcher crlMatcher) {
		assert keyMatcher != null;
		assert crtMatcher != null;
		assert csrMatcher != null;
		assert crlMatcher != null;

		this.keyMatcher = keyMatcher;
		this.crtMatcher = crtMatcher;
		this.csrMatcher = csrMatcher;
		this.crlMatcher = crlMatcher;
	}

	public Collection<String> getFoundAliases() {
		HashSet<String> aliases = new HashSet<>();

		aliases.addAll(this.keyFiles.keySet());
		aliases.addAll(this.crtFiles.keySet());
		aliases.addAll(this.csrFiles.keySet());
		aliases.addAll(this.crlFiles.keySet());
		return aliases;
	}

	public Path getFoundKeyFile(String alias) {
		return this.keyFiles.get(alias);
	}

	public Path getFoundCRTFile(String alias) {
		return this.crtFiles.get(alias);
	}

	public Path getFoundCSRFile(String alias) {
		return this.csrFiles.get(alias);
	}

	public Path getFoundCRLFile(String alias) {
		return this.crlFiles.get(alias);
	}

	/*
	 * (non-Javadoc)
	 * @see java.nio.file.FileVisitor#preVisitDirectory(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	/*
	 * (non-Javadoc)
	 * @see java.nio.file.FileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		considerFile(file);
		return FileVisitResult.CONTINUE;
	}

	/*
	 * (non-Javadoc)
	 * @see java.nio.file.FileVisitor#visitFileFailed(java.lang.Object, java.io.IOException)
	 */
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	/*
	 * (non-Javadoc)
	 * @see java.nio.file.FileVisitor#postVisitDirectory(java.lang.Object, java.io.IOException)
	 */
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	private void considerFile(Path file) {
		if (this.keyMatcher.matches(file)) {
			LOG.info(null, "Found key file: ''{0}''", file);
			collectFile(this.keyFiles, file);
		} else if (this.crtMatcher.matches(file)) {
			LOG.info(null, "Found crt file: ''{0}''", file);
			collectFile(this.crtFiles, file);
		} else if (this.csrMatcher.matches(file)) {
			LOG.info(null, "Found csr file: ''{0}''", file);
			collectFile(this.csrFiles, file);
		} else if (this.crlMatcher.matches(file)) {
			LOG.info(null, "Found crl file: ''{0}''", file);
			collectFile(this.crlFiles, file);
		} else {
			LOG.debug(null, "Ignoring file: ''{0}''", file);
		}
	}

	private void collectFile(Map<String, Path> files, Path file) {
		String fileName = file.getFileName().toString();
		int extIndex = fileName.lastIndexOf('.');
		String alias = (extIndex > 1 ? fileName.substring(0, extIndex) : fileName);

		files.put(alias, file);
	}

}
