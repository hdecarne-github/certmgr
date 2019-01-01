/*
 * Copyright (c) 2015-2019 Holger de Carne and contributors, All Rights Reserved.
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
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Strings;

/**
 * Generic class for {@link String} based general name objects.
 */
public class StringName extends GeneralName {

	private final String name;

	/**
	 * Construct {@code StringName}.
	 *
	 * @param type The name type.
	 * @param name The name.
	 */
	public StringName(GeneralNameType type, String name) {
		super(type);
		this.name = name;
	}

	/**
	 * Decode {@code StringName} from an ASN.1 data object.
	 *
	 * @param type The actual general name type.
	 * @param primitive The ASN.1 data object to decode.
	 * @return The decoded general name object.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static StringName decode(GeneralNameType type, ASN1Primitive primitive) throws IOException {
		ASN1Primitive object = decodeTagged(primitive, type.value());

		return new StringName(type, Strings.fromByteArray(decodePrimitive(object, ASN1OctetString.class).getOctets()));
	}

	@Override
	public ASN1Encodable encode() throws IOException {
		return new DERTaggedObject(false, getType().value(), new DEROctetString(Strings.toByteArray(this.name)));
	}

	@Override
	public String toValueString() {
		return this.name;
	}

	/**
	 * This name's {@link String} representation.
	 *
	 * @return This name's {@link String} representation.
	 */
	public String getNameString() {
		return this.name;
	}

}
