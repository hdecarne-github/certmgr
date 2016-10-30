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
package de.carne.certmgr.certs.asn1;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;

/**
 * Base class for all data objects that support ASN.1 decoding and encoding.
 */
public abstract class ASN1Data {

	/**
	 * Decode an ASN.1 sequence.
	 *
	 * @param primitive The ASN.1 data object to decode from.
	 * @param min The expected minimum sequence size.
	 * @param max The expected maximum sequence size.
	 * @return The decoded sequence object.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	protected static ASN1Sequence decodeSequence(ASN1Primitive primitive, int min, int max) throws IOException {
		ASN1Sequence sequence = decodePrimitive(primitive, ASN1Sequence.class);
		int sequenceSize = sequence.size();

		if (sequenceSize < min) {
			throw new IOException("Unxpected min sequence size " + sequenceSize + " (expected " + min + ")");
		}
		if (max < sequenceSize) {
			throw new IOException("Unxpected max sequence size " + sequenceSize + " (expected " + max + ")");
		}
		return sequence;
	}

	/**
	 * Decode an ASN.1 primitive of a specific type.
	 *
	 * @param primitive The raw primitive to decode.
	 * @param primitiveType The expected primitive type.
	 * @return The decoded primitive object.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	protected static <T extends ASN1Primitive> T decodePrimitive(ASN1Primitive primitive, Class<T> primitiveType)
			throws IOException {
		if (!primitiveType.isInstance(primitive)) {
			throw new IOException("Unexpected ASN.1 object type '" + primitive.getClass().getName() + "' (expected '"
					+ primitiveType.getName() + "'");
		}
		return primitiveType.cast(primitive);
	}

}
