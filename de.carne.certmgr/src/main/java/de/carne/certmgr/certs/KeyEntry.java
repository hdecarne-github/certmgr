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
package de.carne.certmgr.certs;

import java.io.IOException;
import java.security.KeyPair;

/**
 * Helper class used to provide Key access in a general manner.
 */
abstract class KeyEntry {

	/**
	 * Check whether the Key object is decrypted.
	 *
	 * @return {@code true} if the Key object is decrypted.
	 */
	public abstract boolean isDecrypted();

	/**
	 * Get the Key object.
	 *
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The Key object.
	 * @throws PasswordRequiredException if no valid password was given.
	 * @throws IOException if an I/O error occurs.
	 */
	public abstract KeyPair getKey(PasswordCallback password) throws IOException;

}
