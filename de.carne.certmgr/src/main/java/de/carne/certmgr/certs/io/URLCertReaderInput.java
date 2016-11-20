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
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.checkerframework.checker.nullness.qual.Nullable;

import de.carne.util.Strings;

/**
 * {@link CertReaderInput} implementation for accessing URL based data.
 */
public class URLCertReaderInput extends CertReaderInput {

	private final URL url;

	/**
	 * Construct {@code URLCertReaderInput}.
	 *
	 * @param url The URL to access.
	 */
	public URLCertReaderInput(URL url) {
		super(url.toExternalForm());
		this.url = url;
	}

	@Override
	public @Nullable Path fileName() {
		String urlPath = this.url.getPath();
		int nameIndex = urlPath.lastIndexOf('/');
		String fileName = (nameIndex >= 0 ? urlPath.substring(nameIndex + 1) : urlPath);

		return (Strings.notEmpty(fileName) ? Paths.get(fileName) : null);
	}

	@Override
	public InputStream stream() throws IOException {
		return this.url.openStream();
	}

}
