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
package de.carne.certmgr.util;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Utility class providing security key related functions.
 */
public final class Keys {

	private Keys() {
		// Make sure this class is not instantiated from outside
	}

	/**
	 * Get the public key's string representation.
	 * 
	 * @param publicKey The public key to format.
	 * @return The public key's string representation.
	 */
	public static String toString(PublicKey publicKey) {
		StringBuilder buffer = new StringBuilder();

		buffer.append(publicKey.getAlgorithm());
		if (publicKey instanceof RSAPublicKey) {
			RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

			buffer.append("/").append(rsaPublicKey.getModulus().bitLength());
		}
		return buffer.toString();
	}

}
