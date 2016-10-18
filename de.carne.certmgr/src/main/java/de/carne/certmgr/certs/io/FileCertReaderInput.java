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
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * {@link CertReaderInput} implementation for accessing file based data.
 */
public class FileCertReaderInput extends CertReaderInput {

	private final Path file;

	/**
	 * Construct {@code FileCertReaderInput}.
	 * 
	 * @param file The file to access.
	 */
	public FileCertReaderInput(Path file) {
		super(file.toString());
		this.file = file;
	}

	@Override
	public InputStream stream() throws IOException {
		return Files.newInputStream(this.file);
	}

}
