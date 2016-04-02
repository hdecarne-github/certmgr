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

/**
 * ASN.1 encoder interface.
 */
public interface ASN1Encoder {

	/**
	 * Encode boolean value.
	 *
	 * @param b The boolean value to encode.
	 */
	public void asn1EncodeBoolean(boolean b);

	/**
	 * Encode integer value.
	 *
	 * @param i The integer value to encode.
	 */
	public void asn1EncodeInteger(int i);

	/**
	 * Encode string object.
	 *
	 * @param s The string object to encode.
	 */
	public void asn1EncodeAsciiString(String s);

	/**
	 * Encode an OID.
	 *
	 * @param oid The OID to encode.
	 */
	public void asn1EncodeOID(String oid);

	/**
	 * Encode a bit string.
	 *
	 * @param bs The bit string to encode.
	 */
	public void asn1EncodeBitString(byte[] bs);

	/**
	 * Encode a tagged object.
	 *
	 * @param explicit The explicit flag.
	 * @param tagNo The tag number.
	 * @param object The object to encode.
	 */
	public void asn1EncodeTaggedObject(boolean explicit, int tagNo, ASN1Encodable object);

	/**
	 * Encode a object sequence.
	 *
	 * @param encodable The object responsible for encoding the sequence.
	 */
	public void asn1EncodeSequence(ASN1Encodable encodable);

}
