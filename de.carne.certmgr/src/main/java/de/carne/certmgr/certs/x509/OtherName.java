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

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;

import de.carne.certmgr.certs.asn1.OIDs;
import de.carne.certmgr.util.Bytes;

/**
 * General name of type Other.
 */
public class OtherName extends GeneralName {

	private final String oid;
	private final byte[] nameBytes;

	/**
	 * Construct {@code OtherName}.
	 *
	 * @param oid The name object's OID.
	 * @param nameBytes The name object's data.
	 */
	public OtherName(String oid, byte[] nameBytes) {
		super(GeneralNameType.OTHER_NAME);

		assert oid != null;
		assert nameBytes != null;

		this.oid = oid;
		this.nameBytes = nameBytes;
	}

	/**
	 * Decode {@code OtherName} from an ASN.1 data object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @return The decoded other name object.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static OtherName decode(ASN1Primitive primitive) throws IOException {
		ASN1Primitive[] sequence = decodeSequence(primitive, 2, 2);
		String oid = decodePrimitive(sequence[0], ASN1ObjectIdentifier.class).getId();
		byte[] nameBytes = sequence[1].getEncoded();

		return new OtherName(oid, nameBytes);
	}

	@Override
	public ASN1Encodable encode() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toValueString() {
		return OIDs.toString(this.oid) + ":" + Bytes.toString(this.nameBytes, Attributes.FORMAT_LIMIT_SHORT);
	}

}
