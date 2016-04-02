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

/**
 * ASN.1 bit string.
 */
public class ASN1BitString implements ASN1Encodable {

	private byte[] bitBytes;

	/**
	 * Construct ASN1BitString.
	 */
	public ASN1BitString() {
		this(new byte[0]);
	}

	/**
	 * Construct ASN1BitString.
	 *
	 * @param bits The bits to set.
	 */
	public ASN1BitString(int bits) {
		this(getBitBytes(bits));
	}

	/**
	 * Construct ASN1BitString.
	 *
	 * @param bitBytes The bit bytes to set.
	 */
	public ASN1BitString(byte[] bitBytes) {
		assert bitBytes != null;

		this.bitBytes = bitBytes;
	}

	/**
	 * Set the bit string's bits (existing bits are overwritten).
	 *
	 * @param bits The bits to set.
	 */
	public void setBits(int bits) {
		this.bitBytes = getBitBytes(bits);
	}

	/**
	 * Get the bit string's bits.
	 *
	 * @return The bit string's bits.
	 */
	public int getBits() {
		int bits = 0;
		int shift = 0;

		for (byte bitByte : this.bitBytes) {
			bits |= (bitByte & 0xff) << shift;
			shift += 8;
		}
		return bits;
	}

	/**
	 * Get the bit string bytes.
	 *
	 * @return The bit string bytes.
	 */
	public byte[] getBitBytes() {
		return this.bitBytes;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Encodable#asn1Encode(de.carne.certmgr.store.asn1.ASN1Encoder)
	 */
	@Override
	public void asn1Encode(ASN1Encoder encoder) {
		encoder.asn1EncodeBitString(getBitBytes());
	}

	/**
	 * Decode bit string.
	 *
	 * @param decoder The decoder to use.
	 * @return The decoded object.
	 * @throws IOException if the decoding fails.
	 */
	public static ASN1BitString asn1Decode(ASN1Decoder decoder) throws IOException {
		int bits = 0;
		int shift = 0;

		for (byte bitByte : decoder.asn1EncodeBitString()) {
			bits |= bitByte << shift;
			shift += 8;
		}
		return new ASN1BitString(bits);
	}

	private static byte[] getBitBytes(int bits) {
		byte[] bytes;

		if ((bits & 0xff000000) != 0) {
			bytes = new byte[4];
			bytes[0] = (byte) (bits & 0xff);
			bytes[1] = (byte) ((bits >>> 8) & 0xff);
			bytes[2] = (byte) ((bits >>> 16) & 0xff);
			bytes[3] = (byte) ((bits >>> 24) & 0xff);
		} else if ((bits & 0xff0000) != 0) {
			bytes = new byte[3];
			bytes[0] = (byte) (bits & 0xff);
			bytes[1] = (byte) ((bits >>> 8) & 0xff);
			bytes[2] = (byte) ((bits >>> 16) & 0xff);
		} else if ((bits & 0xff00) != 0) {
			bytes = new byte[2];
			bytes[0] = (byte) (bits & 0xff);
			bytes[1] = (byte) ((bits >>> 8) & 0xff);
		} else {
			bytes = new byte[1];
			bytes[0] = (byte) (bits & 0xff);
		}
		return bytes;
	}

}
