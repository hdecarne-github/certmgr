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
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

/**
 * {@link CertReaderInput} implementation for accessing string based data.
 */
public class StringCertReaderInput extends CertReaderInput {

	private final String data;

	/**
	 * Construct {@code StringCertReaderInput}.
	 *
	 * @param data The string data to access.
	 * @param resource The name of the resource accessed by this input.
	 */
	public StringCertReaderInput(String data, String resource) {
		super(resource);

		assert data != null;

		this.data = data;
	}

	@Override
	public Reader reader(Charset charset) throws IOException {
		return new StringReader(this.data);
	}

}
