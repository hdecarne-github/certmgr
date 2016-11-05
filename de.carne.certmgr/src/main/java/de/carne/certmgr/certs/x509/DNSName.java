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

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Strings;

/**
 * DNS_NAME type general name.
 */
public class DNSName extends GeneralName {

	private final String name;

	/**
	 * Construct {@code DNSName}.
	 *
	 * @param name The DNS name.
	 */
	public DNSName(String name) {
		super(GeneralNameType.DNS_NAME);

		assert name != null;

		this.name = name;
	}

	/**
	 * Decode {@code DNSName} from an ASN.1 data object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @return The decoded name object.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static DNSName decode(ASN1Primitive primitive) throws IOException {
		return new DNSName(Strings.fromByteArray(decodePrimitive(primitive, ASN1OctetString.class).getOctets()));
	}

	@Override
	public String toString() {
		return this.name;
	}

}
