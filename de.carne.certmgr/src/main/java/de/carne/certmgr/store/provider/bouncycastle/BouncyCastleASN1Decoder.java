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
package de.carne.certmgr.store.provider.bouncycastle;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Strings;

import de.carne.certmgr.store.asn1.ASN1Decoder;
import de.carne.certmgr.store.asn1.TaggedObject;

/**
 * TODO
 */
class BouncyCastleASN1Decoder implements ASN1Decoder {

	private ASN1Primitive encoded;

	public BouncyCastleASN1Decoder(ASN1Primitive encoded) {
		this.encoded = encoded;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Decoder#asn1DecodeBoolean()
	 */
	@Override
	public boolean asn1DecodeBoolean() throws IOException {
		ASN1Boolean booleanObject = ensureType(ASN1Boolean.class);

		return booleanObject.isTrue();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Decoder#asn1DecodeInteger()
	 */
	@Override
	public int asn1DecodeInteger() throws IOException {
		int intValue;

		try {
			intValue = asn1DecodeBigInteger().intValueExact();
		} catch (ArithmeticException e) {
			throw new IOException(e);
		}
		return intValue;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Decoder#asn1DecodeBigInteger()
	 */
	@Override
	public BigInteger asn1DecodeBigInteger() throws IOException {
		return ensureType(ASN1Integer.class).getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Decoder#asn1EncodeAsciiString()
	 */
	@Override
	public String asn1EncodeAsciiString() throws IOException {
		return Strings.fromByteArray(ensureType(ASN1OctetString.class).getOctets());
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Decoder#asn1DecodeOID()
	 */
	@Override
	public String asn1DecodeOID() throws IOException {
		return ensureType(ASN1ObjectIdentifier.class).getId();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Decoder#asn1EncodeBitString()
	 */
	@Override
	public byte[] asn1EncodeBitString() throws IOException {
		DERBitString bitString = ensureType(DERBitString.class);

		return bitString.getBytes();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Decoder#asn1EncodeTaggedObject(int[])
	 */
	@Override
	public TaggedObject<ASN1Decoder> asn1EncodeTaggedObject(int... validTagNos) throws IOException {
		DERTaggedObject taggedObject = ensureType(DERTaggedObject.class);
		boolean explicit = taggedObject.isExplicit();
		int tagNo = taggedObject.getTagNo();

		if (validTagNos.length > 0) {
			boolean tagNoInvalid = true;

			for (int validTagNo : validTagNos) {
				if (validTagNo == tagNo) {
					tagNoInvalid = false;
					break;
				}
			}
			if (tagNoInvalid) {
				throw new IOException("Unexpected tag no: " + tagNo + " expected one of: "
						+ Arrays.toString(validTagNos));
			}
		}

		ASN1Decoder objectDecoder = new BouncyCastleASN1Decoder(taggedObject.getObject().toASN1Primitive());

		return new TaggedObject<>(explicit, tagNo, objectDecoder);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Decoder#asn1DecodeSequence(int, int)
	 */
	@Override
	public ASN1Decoder[] asn1DecodeSequence(int minSize, int maxSize) throws IOException {
		ASN1Decoder[] sequence;

		if (isType(ASN1Sequence.class)) {
			ASN1Sequence sequenceObject = ensureType(ASN1Sequence.class);
			ArrayList<ASN1Decoder> sequenceDecoders = new ArrayList<>(sequenceObject.size());

			for (ASN1Encodable encodable : sequenceObject.toArray()) {
				sequenceDecoders.add(new BouncyCastleASN1Decoder(encodable.toASN1Primitive()));
			}
			sequence = sequenceDecoders.toArray(new ASN1Decoder[sequenceDecoders.size()]);
		} else {
			sequence = new ASN1Decoder[] { this };
		}

		int sequenceSize = sequence.length;

		if (minSize >= 0 && sequenceSize < minSize) {
			throw new IOException("Unexpected sequence size: " + sequenceSize + " expected minimum size: " + minSize);
		}
		if (maxSize >= 0 && sequenceSize > maxSize) {
			throw new IOException("Unexpected sequence size: " + sequenceSize + " expected maximum size: " + maxSize);
		}
		return sequence;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Decoder#getEncoded()
	 */
	@Override
	public byte[] getEncoded() throws IOException {
		return this.encoded.getEncoded();
	}

	private <T extends ASN1Primitive> boolean isType(Class<T> type) {
		return type.isInstance(this.encoded);
	}

	private <T extends ASN1Primitive> T ensureType(Class<T> type) throws IOException {
		if (!isType(type)) {
			throw new IOException("Unexpected ASN.1 primitive: '"
					+ (this.encoded != null ? this.encoded.getClass() : null) + "' expected: '" + type + "'");
		}
		return type.cast(this.encoded);
	}

}
