/*
 * Copyright (c) 2015-2020 Holger de Carne and contributors, All Rights Reserved.
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
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.certmgr.certs.CertObjectStore;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.io.CertReaders;
import de.carne.certmgr.certs.io.IOResource;
import de.carne.util.SystemProperties;

/**
 * Service provider interface for reading certificate objects from input data.
 */
public interface CertReader extends NamedProvider, FileAccessProvider {

	/**
	 * The maximum number of bytes to read into memory during read operations.
	 * <p>
	 * This property is used to avoid out of memory conditions when we try to read huge files with readers that require
	 * all data to be read in advance.
	 */
	int READ_LIMIT = SystemProperties.intValue(CertReaders.class.getPackage().getName() + ".readLimit", 1 << 20);

	/**
	 * Read all available certificate objects.
	 *
	 * @param in The input resource to read from.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The read certificate objects, or {@code null} if the input is not recognized.
	 * @throws IOException if an I/O error occurs while reading.
	 */
	@Nullable
	CertObjectStore readBinary(IOResource<InputStream> in, PasswordCallback password) throws IOException;

	/**
	 * Read all available certificate objects.
	 *
	 * @param in The input resource to read from.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The read certificate objects, or {@code null} if the input is not recognized.
	 * @throws IOException if an I/O error occurs while reading.
	 */
	@Nullable
	CertObjectStore readString(IOResource<Reader> in, PasswordCallback password) throws IOException;

}
