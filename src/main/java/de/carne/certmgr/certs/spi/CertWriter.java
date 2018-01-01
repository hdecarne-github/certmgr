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
package de.carne.certmgr.certs.spi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import de.carne.certmgr.certs.CertObjectStore;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.io.IOResource;

/**
 * Service provider interface for writing certificate objects to output channels.
 */
public interface CertWriter extends NamedProvider, FileAccessProvider {

	/**
	 * Check whether this writer produces string based output (e.g. PEM).
	 *
	 * @return {@code true} if the writer produces string based output (e.g. PEM).
	 * @see #writeString(IOResource, CertObjectStore)
	 * @see #writeEncryptedString(IOResource, CertObjectStore, PasswordCallback)
	 */
	boolean isCharWriter();

	/**
	 * Check whether this writer enforces encryption of the generated output.
	 *
	 * @return {@code true} if the writer enforces encryption of the generated output.
	 */
	boolean isEncryptionRequired();

	/**
	 * Write certificate objects to a (not encrypted) binary stream.
	 *
	 * @param out The stream resource to write to.
	 * @param certObjects The certificate objects to write.
	 * @throws IOException if an I/O error occurs while writing to the output.
	 * @throws UnsupportedOperationException if the operation is not supported.
	 * @see #isEncryptionRequired()
	 */
	void writeBinary(IOResource<OutputStream> out, CertObjectStore certObjects)
			throws IOException, UnsupportedOperationException;

	/**
	 * Write certificate objects to an encrypted binary stream.
	 *
	 * @param out The stream resource to write to.
	 * @param certObjects The certificate objects to write.
	 * @param newPassword The callback to use for querying the encryption password.
	 * @throws IOException if an I/O error occurs while writing to the output.
	 */
	void writeEncryptedBinary(IOResource<OutputStream> out, CertObjectStore certObjects, PasswordCallback newPassword)
			throws IOException;

	/**
	 * Write certificate objects to a (not encrypted) string writer.
	 *
	 * @param out The writer resource to write to.
	 * @param certObjects The certificate objects to write.
	 * @throws IOException if an I/O error occurs while writing to the output.
	 * @throws UnsupportedOperationException if the operation is not supported.
	 * @see #isCharWriter()
	 * @see #isEncryptionRequired()
	 */
	void writeString(IOResource<Writer> out, CertObjectStore certObjects)
			throws IOException, UnsupportedOperationException;

	/**
	 * Write certificate objects to a string writer.
	 *
	 * @param out The writer resource to write to.
	 * @param certObjects The certificate objects to write.
	 * @param newPassword The callback to use for querying the encryption password.
	 * @throws IOException if an I/O error occurs while writing to the output.
	 * @throws UnsupportedOperationException if the operation is not supported.
	 * @see #isCharWriter()
	 */
	void writeEncryptedString(IOResource<Writer> out, CertObjectStore certObjects, PasswordCallback newPassword)
			throws IOException, UnsupportedOperationException;

}
