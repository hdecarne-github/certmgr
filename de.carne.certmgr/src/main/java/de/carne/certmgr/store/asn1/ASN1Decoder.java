/*
 * Copyright (c) 2014-2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.store.asn1;

import java.io.IOException;
import java.math.BigInteger;

/**
 * ASN.1 decoder interface.
 */
public interface ASN1Decoder {

	/**
	 * Decode ASN.1 boolean value.
	 *
	 * @return The decoded boolean value.
	 * @throws IOException if the decoding fails.
	 */
	public boolean asn1DecodeBoolean() throws IOException;

	/**
	 * Decode ASN.1 int value.
	 *
	 * @return The decoded int value.
	 * @throws IOException if the decoding fails.
	 */
	public int asn1DecodeInteger() throws IOException;

	/**
	 * Decode ASN.1 BigInteger value.
	 *
	 * @return The decoded BigInteger value.
	 * @throws IOException if the decoding fails.
	 */
	public BigInteger asn1DecodeBigInteger() throws IOException;

	/**
	 * Decode ASN.1 ascii string.
	 *
	 * @return The decoded ascii string.
	 * @throws IOException if the decoding fails.
	 */
	public String asn1EncodeAsciiString() throws IOException;

	/**
	 * Decode ASN.1 OID.
	 *
	 * @return The decoded OID.
	 * @throws IOException if the decoding fails.
	 */
	public String asn1DecodeOID() throws IOException;

	/**
	 * Decode ASN.1 bit string.
	 *
	 * @return The decoded byte values.
	 * @throws IOException if the decoding fails.
	 */
	public byte[] asn1EncodeBitString() throws IOException;

	/**
	 * Decode ASN.1 tagged object.
	 *
	 * @param tagNos The expected tag numbers.
	 * @return The decoded object.
	 * @throws IOException if the decoding fails.
	 */
	public TaggedObject<ASN1Decoder> asn1EncodeTaggedObject(int... tagNos) throws IOException;

	/**
	 * Decode ASN.1 sequence.
	 *
	 * @param minSize The minimum expected sequence size or -1 if no minimum size is expected.
	 * @param maxSize The maximum expected sequence size of -1 if no maximum size is expected.
	 * @return The decoded sequence.
	 * @throws IOException if the decoding fails.
	 */
	public ASN1Decoder[] asn1DecodeSequence(int minSize, int maxSize) throws IOException;

	/**
	 * Get the encoded data.
	 *
	 * @return The encoded data.
	 * @throws IOException if an I/O error occurs.
	 */
	public byte[] getEncoded() throws IOException;

}
