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
import java.util.HashSet;
import java.util.Set;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Primitive;

import de.carne.certmgr.certs.asn1.ASN1Data;

/**
 * Distribution point object.
 */
public class ReasonFlags extends ASN1Data {

	private final Set<ReasonFlag> reasonFlags;

	/**
	 * Construct {@code DistributionPoint}.
	 *
	 * @param type The name type.
	 */
	public ReasonFlags(Set<ReasonFlag> reasonFlags) {
		assert reasonFlags != null;

		this.reasonFlags = reasonFlags;
	}

	/**
	 * Decode {@code ReasonFlags} object from an ASN.1 data object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @return The decoded flags.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static ReasonFlags decode(ASN1Primitive primitive) throws IOException {
		byte[] bitBytes = decodePrimitive(primitive, ASN1BitString.class).getBytes();
		Set<ReasonFlag> reasonFlags = new HashSet<>();

		return new ReasonFlags(reasonFlags);
	}

}
