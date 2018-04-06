/*
 * Copyright (c) 2015-2018 Holger de Carne and contributors, All Rights Reserved.
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
import de.carne.boot.check.Check;
import de.carne.boot.check.Nullable;
import de.carne.util.Strings;

/**
 * X.509 <a href="https://tools.ietf.org/html/rfc5280#section-4.2.1.1">Authority Key Identifier Extension</a> data.
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

	@Nullable
	private final byte[] keyIdentifier;

	@Nullable
	private final GeneralNames authorityCertIssuer;

	@Nullable
	private final BigInteger authorityCertSerialNumber;

	/**
	 * Construct {@code AuthorityKeyIdentifierExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 * @param keyIdentifier The issuer's key identifier bytes.
	 * @param authorityCertIssuer The issuer's certificate name.
	 * @param authorityCertSerialNumber The issuer's certificate serial number.
	 */
	public AuthorityKeyIdentifierExtensionData(boolean critical, @Nullable byte[] keyIdentifier,
			@Nullable GeneralNames authorityCertIssuer, @Nullable BigInteger authorityCertSerialNumber) {
		super(OID, critical);

		Check.assertTrue(keyIdentifier != null || (authorityCertIssuer != null && authorityCertSerialNumber != null));

		this.keyIdentifier = keyIdentifier;
		this.authorityCertIssuer = authorityCertIssuer;
		this.authorityCertSerialNumber = authorityCertSerialNumber;
	}

	/**
	 * Decode {@code AuthorityKeyIdentifierExtensionData} from an ASN.1 data object.
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
		if (keyIdentifier == null && (authorityCertIssuer == null || authorityCertSerialNumber == null)) {
			throw new IOException("Invalid or incomplete extension data");
		}
		return new AuthorityKeyIdentifierExtensionData(critical, keyIdentifier, authorityCertIssuer,
				authorityCertSerialNumber);
	}

	/**
	 * Get the issuer's key identifier.
	 *
	 * @return The issuer's key identifier.
	 */
	@Nullable
	public byte[] getKeyIdentifier() {
		return this.keyIdentifier;
	}

	/**
	 * Get the issuer's certificate name.
	 *
	 * @return The issuer's certificate name.
	 */
	@Nullable
	public GeneralNames getAuthorityCertIssuer() {
		return this.authorityCertIssuer;
	}

	/**
	 * Get the issuer's certificate serial number.
	 *
	 * @return The issuer's certificate serial number.
	 */
	@Nullable
	public BigInteger getAuthorityCertSerialNumber() {
		return this.authorityCertSerialNumber;
	}

	@Override
	public ASN1Encodable encode() throws IOException {
		ASN1EncodableVector sequence = new ASN1EncodableVector();
		byte[] checkedKeyIdentifier = this.keyIdentifier;

		if (checkedKeyIdentifier != null) {
			sequence.add(new DERTaggedObject(false, 0, new DEROctetString(checkedKeyIdentifier)));
		}

		GeneralNames checkedAuthorityCertIssuer = this.authorityCertIssuer;

		if (checkedAuthorityCertIssuer != null) {
			sequence.add(new DERTaggedObject(false, 1, checkedAuthorityCertIssuer.encode()));
		}

		BigInteger checkedAuthorityCertSerialNumber = this.authorityCertSerialNumber;

		if (checkedAuthorityCertSerialNumber != null) {
			sequence.add(new DERTaggedObject(false, 2, new ASN1Integer(checkedAuthorityCertSerialNumber)));
		}
		return new DERSequence(sequence);
	}

	@Override
	public String toValueString() {
		StringBuilder buffer = new StringBuilder();
		byte[] checkedKeyIdentifier = this.keyIdentifier;

		if (checkedKeyIdentifier != null) {
			buffer.append(Bytes.toString(checkedKeyIdentifier));
		}

		GeneralNames checkedAuthorityCertIssuer = this.authorityCertIssuer;

		if (checkedAuthorityCertIssuer != null) {
			buffer.append(Strings.join(checkedAuthorityCertIssuer, ", ", Attributes.FORMAT_LIMIT_LONG));
		}

		BigInteger checkedAuthorityCertSerialNumber = this.authorityCertSerialNumber;

		if (checkedAuthorityCertSerialNumber != null) {
			buffer.append(" (").append(checkedAuthorityCertSerialNumber.toString()).append(")");
		}
		return buffer.toString();
	}

	@Override
	public Attributes toAttributes() {
		Attributes extensionAttributes = super.toAttributes();
		byte[] checkedKeyIdentifier = this.keyIdentifier;

		if (checkedKeyIdentifier != null) {
			extensionAttributes.add(AttributesI18N.formatSTR_KEYIDENTIFIER(), Bytes.toString(checkedKeyIdentifier));
		}

		GeneralNames checkedAuthorityCertIssuer = this.authorityCertIssuer;

		if (checkedAuthorityCertIssuer != null) {
			extensionAttributes.add(AttributesI18N.formatSTR_AUTHORITY_CERT_ISSUER());
			checkedAuthorityCertIssuer.addToAttributes(extensionAttributes);
		}

		BigInteger checkedAuthorityCertSerialNumber = this.authorityCertSerialNumber;

		if (checkedAuthorityCertSerialNumber != null) {
			extensionAttributes.add(AttributesI18N.formatSTR_AUTHORITY_CERT_SERIAL_NUMBER(),
					checkedAuthorityCertSerialNumber.toString());
		}
		return extensionAttributes;
	}

}
