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
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

import de.carne.certmgr.certs.PasswordCallback;

/**
 * Service provider interface for writing certificate objects to output
 * channels.
 */
public interface CertWriter extends NamedProvider, FileAccessProvider {

	/**
	 * Check whether this writer produces string based output (e.g. PEM).
	 *
	 * @return {@code true} if the writer produces string based output (e.g.
	 *         PEM).
	 * @see #writeString(Writer, List)
	 * @see #writeEncryptedString(Writer, List, PasswordCallback)
	 */
	boolean isCharWriter();

	/**
	 * Check whether this writer can write multiple certificate objects to a
	 * single output (e.g. PEM).
	 *
	 * @return {@code true} if the the writer can write multiple certificate
	 *         objects to a single output (e.g. PEM).
	 */
	boolean isContainerWriter();

	/**
	 * Check whether this writer enforces encryption of the generated output.
	 *
	 * @return {@code true} if the writer enforces encryption of the generated
	 *         output.
	 */
	boolean isEncryptionRequired();

	/**
	 * Write certificate objects to a (not encrypted) binary stream.
	 *
	 * @param out The stream to write to.
	 * @param certObjects The certificate objects to write. This is an unary
	 *        list if this instance is not a container writer.
	 * @throws IOException if an I/O error occurs while writing to the output.
	 * @throws UnsupportedOperationException if the operation is not supported.
	 * @see #isContainerWriter()
	 * @see #isEncryptionRequired()
	 */
	void writeBinary(OutputStream out, List<Object> certObjects) throws IOException, UnsupportedOperationException;

	/**
	 * Write certificate objects to an encrypted binary stream.
	 *
	 * @param out The stream to write to.
	 * @param certObjects The certificate objects to write. This is an unary
	 *        list if this instance is not a container writer.
	 * @param newPassword The callback to use for querying the encryption
	 *        password.
	 * @throws IOException if an I/O error occurs while writing to the output.
	 * @throws UnsupportedOperationException if the operation is not supported.
	 * @see #isContainerWriter()
	 */
	void writeEncryptedBinary(OutputStream out, List<Object> certObjects, PasswordCallback newPassword)
			throws IOException, UnsupportedOperationException;

	/**
	 * Write certificate objects to a (not encrypted) string writer.
	 *
	 * @param out The writer to write to.
	 * @param certObjects The certificate objects to write. This is an unary
	 *        list if this instance is not a container writer.
	 * @throws IOException if an I/O error occurs while writing to the output.
	 * @throws UnsupportedOperationException if the operation is not supported.
	 * @see #isCharWriter()
	 * @see #isContainerWriter()
	 * @see #isEncryptionRequired()
	 */
	void writeString(Writer out, List<Object> certObjects) throws IOException, UnsupportedOperationException;

	/**
	 * Write certificate objects to a (not encrypted) string writer.
	 *
	 * @param out The writer to write to.
	 * @param certObjects The certificate objects to write. This is an unary
	 *        list if this instance is not a container writer.
	 * @param newPassword The callback to use for querying the encryption
	 *        password.
	 * @throws IOException if an I/O error occurs while writing to the output.
	 * @throws UnsupportedOperationException if the operation is not supported.
	 * @see #isCharWriter()
	 * @see #isContainerWriter()
	 */
	void writeEncryptedString(Writer out, List<Object> certObjects, PasswordCallback newPassword)
			throws IOException, UnsupportedOperationException;

}
