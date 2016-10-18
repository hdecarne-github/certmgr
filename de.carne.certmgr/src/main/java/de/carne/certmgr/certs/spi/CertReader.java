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
package de.carne.certmgr.certs.spi;

import java.io.IOException;
import java.util.List;

import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.io.CertReaderInput;

/**
 * Interface for reading certificate objects from input data.
 */
public interface CertReader extends NamedProvider, FileAccessProvider {

	/**
	 * Read all available certificate objects.
	 *
	 * @param input The input to read from.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The list of read certificate objects or {@code null} if the input
	 *         is not recognized.
	 * @throws IOException if an I/O error occurs while reading.
	 */
	List<Object> read(CertReaderInput input, PasswordCallback password) throws IOException;

}
