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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import de.carne.certmgr.certs.spi.CertReader;

/**
 * This class is used to provide data access for all kind of {@link CertReader}
 * implementations.
 */
public abstract class CertReaderInput implements Closeable {

	private final String resource;

	/**
	 * Construct {@code CertReaderInput}.
	 *
	 * @param resource The name of the resource accessed by this input.
	 */
	protected CertReaderInput(String resource) {
		assert resource != null;

		this.resource = resource;
	}

	/**
	 * Get byte access to the input data.
	 *
	 * @return {@link InputStream} access to the input data.
	 * @throws IOException if an I/O error occurs while accessing the data.
	 */
	public InputStream stream() throws IOException {
		return null;
	}

	/**
	 * Get character access to the input data.
	 *
	 * @param charset The {@link Charset} to use for byte to char conversion (if
	 *        needed).
	 * @return {@link Reader} access to the input data.
	 * @throws IOException if an I/O error occurs while accessing the data.
	 */
	public Reader reader(Charset charset) throws IOException {
		InputStream stream = stream();

		return (stream != null ? new InputStreamReader(stream, charset) : null);
	}

	@Override
	public void close() throws IOException {
		// Nothing to do here
	}

	@Override
	public String toString() {
		return this.resource;
	}

}
