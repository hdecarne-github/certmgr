/*
 * Copyright (c) 2015-2017 Holger de Carne and contributors, All Rights Reserved.
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
import java.util.Iterator;
import java.util.Set;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;

import de.carne.certmgr.certs.asn1.ASN1Data;

/**
 * CRL ReasonFlags object.
 */
public class ReasonFlags extends ASN1Data implements AttributesContent, Iterable<ReasonFlag> {

	private final Set<ReasonFlag> reasonFlags;

	/**
	 * Construct {@code ReasonFlags}.
	 *
	 * @param reasonFlags The reason flags.
	 */
	public ReasonFlags(Set<ReasonFlag> reasonFlags) {
		assert reasonFlags != null;

		this.reasonFlags = reasonFlags;
	}

	/**
	 * Decode {@code ReasonFlags} object from an ASN.1 data object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @return The decoded reason flags.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static ReasonFlags decode(ASN1Primitive primitive) throws IOException {
		byte[] bitBytes = decodePrimitive(primitive, ASN1BitString.class).getBytes();
		Set<ReasonFlag> reasonFlags = new HashSet<>();

		if (bitBytes.length != 0) {
			boolean unspecified = true;

			for (byte bitByte : bitBytes) {
				unspecified = unspecified && bitByte == 0;
			}
			if (unspecified) {
				reasonFlags.add(ReasonFlag.UNSPECIFIED);
			} else {
				int reasonFlagValue = 1;

				for (byte bitByte : bitBytes) {
					for (int bit = 1; bit < 256; bit <<= 1) {
						if ((bitByte & bit) == bit) {
							reasonFlags.add(ReasonFlag.fromValue(reasonFlagValue));
						}
						reasonFlagValue <<= 1;
					}
				}
			}
		}
		return new ReasonFlags(reasonFlags);
	}

	@Override
	public ASN1Encodable encode() throws IOException {
		int bits = 0;

		for (ReasonFlag reasonFlag : this.reasonFlags) {
			bits |= reasonFlag.value();
		}
		return new DERBitString(bits);
	}

	@Override
	public Iterator<ReasonFlag> iterator() {
		return this.reasonFlags.iterator();
	}

	@Override
	public void addToAttributes(Attributes attributes) {
		for (ReasonFlag reasonFlag : this.reasonFlags) {
			attributes.add(reasonFlag.name(), null);
		}
	}

}
