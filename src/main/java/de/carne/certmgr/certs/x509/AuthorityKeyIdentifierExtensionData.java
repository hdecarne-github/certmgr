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
import java.math.BigInteger;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

import de.carne.certmgr.util.Bytes;

/**
 * X.509 <a href="https://tools.ietf.org/html/rfc5280#section-4.2.1.1">Authority
 * Key Identifier Extension</a> data.
 */
public class AuthorityKeyIdentifierExtensionData extends X509ExtensionData {

	/**
	 * Extension OID.
	 */
	public static final String OID = "2.5.29.35";

	/**
	 * The default to use for this extension's critical flag.
	 */
	public static final boolean CRITICAL_DEFAULT = false;

	private final byte[] keyIdentifier;

	private final GeneralNames authorityCertIssuer;

	private final BigInteger authorityCertSerialNumber;

	/**
	 * Construct {@code AuthorityKeyIdentifierExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 * @param keyIdentifier The issuer's key identifier bytes.
	 * @param authorityCertIssuer The issuer's certificate name.
	 * @param authorityCertSerialNumber The issuer's certificate serial number.
	 */
	public AuthorityKeyIdentifierExtensionData(boolean critical, byte[] keyIdentifier, GeneralNames authorityCertIssuer,
			BigInteger authorityCertSerialNumber) {
		super(OID, critical);

		assert keyIdentifier != null;

		this.keyIdentifier = keyIdentifier;
		this.authorityCertIssuer = authorityCertIssuer;
		this.authorityCertSerialNumber = authorityCertSerialNumber;
	}

	/**
	 * Decode {@code AuthorityKeyIdentifierExtensionData} from an ASN.1 data
	 * object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @param critical The extension's critical flag.
	 * @return The decoded extension data.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static AuthorityKeyIdentifierExtensionData decode(ASN1Primitive primitive, boolean critical)
			throws IOException {
		ASN1Primitive[] sequence = decodeSequence(primitive, 0, Integer.MAX_VALUE);
		byte[] keyIdentifier = null;
		GeneralNames authorityCertIssuer = null;
		BigInteger authorityCertSerialNumber = null;

		for (ASN1Primitive sequenceEntry : sequence) {
			ASN1TaggedObject taggedObject = decodePrimitive(sequenceEntry, ASN1TaggedObject.class);
			int taggedObjectTag = taggedObject.getTagNo();

			switch (taggedObjectTag) {
			case 0:
				keyIdentifier = decodePrimitive(taggedObject.getObject(), ASN1OctetString.class).getOctets();
				break;
			case 1:
				authorityCertIssuer = GeneralNames.decode(taggedObject.getObject());
				break;
			case 2:
				authorityCertSerialNumber = decodePrimitive(taggedObject.getObject(), ASN1Integer.class).getValue();
				break;
			default:
				throw new IOException("Unsupported tag: " + taggedObjectTag);
			}
		}
		return new AuthorityKeyIdentifierExtensionData(critical, keyIdentifier, authorityCertIssuer,
				authorityCertSerialNumber);
	}

	/**
	 * Get the issuer's key identifier.
	 *
	 * @return The issuer's key identifier.
	 */
	public byte[] getKeyIdentifier() {
		return this.keyIdentifier;
	}

	/**
	 * Get the issuer's certificate name.
	 *
	 * @return The issuer's certificate name.
	 */
	public GeneralNames getAuthorityCertIssuer() {
		return this.authorityCertIssuer;
	}

	/**
	 * Get the issuer's certificate serial number.
	 *
	 * @return The issuer's certificate serial number.
	 */
	public BigInteger getAuthorityCertSerialNumber() {
		return this.authorityCertSerialNumber;
	}

	@Override
	public ASN1Encodable encode() throws IOException {
		ASN1EncodableVector sequence = new ASN1EncodableVector();

		if (this.keyIdentifier != null) {
			sequence.add(new DERTaggedObject(false, 0, new DEROctetString(this.keyIdentifier)));
		}
		if (this.authorityCertIssuer != null) {
			sequence.add(new DERTaggedObject(false, 1, this.authorityCertIssuer.encode()));
		}
		if (this.authorityCertSerialNumber != null) {
			sequence.add(new DERTaggedObject(false, 2, new ASN1Integer(this.authorityCertSerialNumber)));
		}
		return new DERSequence(sequence);
	}

	@Override
	public String toValueString() {
		return Bytes.toString(this.keyIdentifier);
	}

	@Override
	public Attributes toAttributes() {
		Attributes extensionAttributes = super.toAttributes();

		extensionAttributes.add(AttributesI18N.formatSTR_KEYIDENTIFIER(), toValueString());
		// TODO: Render other attributes
		return extensionAttributes;
	}

}
