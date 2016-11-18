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
package de.carne.certmgr.certs.x509;

import java.io.IOException;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Primitive;

import de.carne.certmgr.certs.x500.X500Names;

/**
 * Class for directory names.
 */
public class DirectoryName extends GeneralName {

	private final X500Principal name;

	/**
	 * Construct {@code DirectoryName}.
	 *
	 * @param name The X.500 name represented by this instance.
	 */
	public DirectoryName(X500Principal name) {
		super(GeneralNameType.DIRECTORY_NAME);

		assert name != null;

		this.name = name;
	}

	@Override
	public String toString() {
		return getType().name() + ":" + X500Names.toString(this.name);
	}

	/**
	 * Decode {@code DirectoryName} from an ASN.1 data object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @return The decoded directory name object.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static DirectoryName decode(ASN1Primitive primitive) throws IOException {
		return new DirectoryName(new X500Principal(primitive.getEncoded()));
	}

}
