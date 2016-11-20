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
package de.carne.certmgr.certs.io;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.ProviderMap;
import de.carne.certmgr.certs.spi.CertReader;

/**
 * Utility class providing {@link CertReader} related functions.
 */
public final class CertReaders {

	private CertReaders() {
		// Make sure this class is not instantiated from outside
	}

	/**
	 * The registered {@link CertReader}s.
	 */
	public static final ProviderMap<CertReader> REGISTERED = new ProviderMap<>(CertReader.class);

	/**
	 * Read all available certificate objects.
	 * <p>
	 * All registered {@link CertReader}s are considered for reading certificate
	 * object until one recognizes the input.
	 *
	 * @param input The input to read from.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The list of read certificate objects, or {@code null} if the
	 *         input is not recognized.
	 * @throws IOException if an I/O error occurs while reading.
	 */
	public static List<Object> read(CertReaderInput input, PasswordCallback password) throws IOException {
		Path inputFileName = input.fileName();
		Deque<CertReader> readers = new ArrayDeque<>();

		if (inputFileName != null) {
			for (CertReader reader : REGISTERED.providers()) {
				if (matchFileName(reader, inputFileName)) {
					readers.addFirst(reader);
				} else {
					readers.addLast(reader);
				}
			}
		} else {
			readers.addAll(REGISTERED.providers());
		}

		List<Object> certObjects = null;

		for (CertReader reader : readers) {
			certObjects = reader.read(input, password);
			if (certObjects != null) {
				break;
			}
		}
		return certObjects;
	}

	private static boolean matchFileName(CertReader reader, Path fileName) {
		boolean matches = false;

		for (String filterExtension : reader.fileExtensions()) {
			PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + filterExtension);

			if (matcher.matches(fileName)) {
				matches = true;
				break;
			}
		}
		return matches;
	}

}
