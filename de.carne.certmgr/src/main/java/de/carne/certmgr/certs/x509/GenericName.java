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

import org.bouncycastle.asn1.ASN1Primitive;

import de.carne.certmgr.util.Bytes;

/**
 * Generic class for any kind of general name objects.
 */
public class GenericName extends GeneralName {

	private final byte[] nameBytes;

	/**
	 * Construct {@code GenericName}.
	 *
	 * @param type The name type.
	 * @param nameBytes The name object's data.
	 */
	public GenericName(GeneralNameType type, byte[] nameBytes) {
		super(type);

		assert nameBytes != null;

		this.nameBytes = nameBytes;
	}

	/**
	 * Decode {@code GenericName} from an ASN.1 data object.
	 *
	 * @param type The name type.
	 * @param primitive The ASN.1 data object to decode.
	 * @return The decoded generic name object.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static GenericName decode(GeneralNameType type, ASN1Primitive primitive) throws IOException {
		return new GenericName(type, primitive.getEncoded());
	}

	@Override
	public String toValueString() {
		return Bytes.toString(this.nameBytes, Attributes.FORMAT_LIMIT_SHORT);
	}

}
