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

import java.util.Stack;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

import de.carne.certmgr.store.asn1.ASN1Encodable;
import de.carne.certmgr.store.asn1.ASN1Encoder;

/**
 * BouncyCastle based ASN1.1 encoder.
 */
class BouncyCastleASN1Encoder implements ASN1Encoder, org.bouncycastle.asn1.ASN1Encodable {

	private ASN1Encodable encodable;
	private Stack<ASN1EncodableVector> sequenceStack = new Stack<>();
	private ASN1Primitive encodeResult;

	public BouncyCastleASN1Encoder(ASN1Encodable encodable) {
		this.encodable = encodable;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Encoder#asn1EncodeBoolean(boolean)
	 */
	@Override
	public void asn1EncodeBoolean(boolean b) {
		asn1Encode(ASN1Boolean.getInstance(b));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Encoder#asn1EncodeInteger(int)
	 */
	@Override
	public void asn1EncodeInteger(int i) {
		asn1Encode(new ASN1Integer(i));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Encoder#asn1EncodeAsciiString(java.lang.String)
	 */
	@Override
	public void asn1EncodeAsciiString(String s) {
		asn1Encode(new DERIA5String(s));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Encoder#asn1EncodeOID(java.lang.String)
	 */
	@Override
	public void asn1EncodeOID(String oid) {
		asn1Encode(new ASN1ObjectIdentifier(oid));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Encoder#asn1EncodeBitString(byte[])
	 */
	@Override
	public void asn1EncodeBitString(byte[] bs) {
		asn1Encode(new DERBitString(bs));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Encoder#asn1EncodeTaggedObject(boolean, int,
	 * de.carne.certmgr.store.asn1.ASN1Encodable)
	 */
	@Override
	public void asn1EncodeTaggedObject(boolean explicit, int tagNo, ASN1Encodable object) {
		BouncyCastleASN1Encoder nestedEncoder = new BouncyCastleASN1Encoder(object);

		asn1Encode(new DERTaggedObject(explicit, tagNo, nestedEncoder.toASN1Primitive()));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Encoder#asn1EncodeSequence(de.carne.certmgr.store.asn1.ASN1Encodable)
	 */
	@Override
	public void asn1EncodeSequence(ASN1Encodable encodable2) {
		this.sequenceStack.push(new ASN1EncodableVector());
		encodable2.asn1Encode(this);

		ASN1Primitive encoded = new DERSequence(this.sequenceStack.pop());

		asn1Encode(encoded);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bouncycastle.asn1.ASN1Encodable#toASN1Primitive()
	 */
	@Override
	public ASN1Primitive toASN1Primitive() {
		this.encodeResult = null;
		this.encodable.asn1Encode(this);
		return this.encodeResult;
	}

	private void asn1Encode(ASN1Primitive encoded) {
		if (this.sequenceStack.size() > 0) {
			this.sequenceStack.peek().add(encoded);
		} else if (this.encodeResult == null) {
			this.encodeResult = encoded;
		} else {
			throw new IllegalStateException();
		}
	}

}
