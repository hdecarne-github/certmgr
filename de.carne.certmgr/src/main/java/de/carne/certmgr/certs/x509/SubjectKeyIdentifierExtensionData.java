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

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;

/**
 * X.509 <a href="https://tools.ietf.org/html/rfc5280#section-4.2.1.2">Subject
 * Key Identifier Extension</a> data.
 */
public class SubjectKeyIdentifierExtensionData extends X509ExtensionData {

	/**
	 * Extension OID.
	 */
	public static final String OID = "2.5.29.14";

	private final byte[] keyIdentifier;

	/**
	 * Construct {@code SubjectKeyIdentifierExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 * @param keyIdentifier The key identifier bytes.
	 */
	public SubjectKeyIdentifierExtensionData(boolean critical, byte[] keyIdentifier) {
		super(OID, critical);

		assert keyIdentifier != null;

		this.keyIdentifier = new byte[keyIdentifier.length];
		System.arraycopy(keyIdentifier, 0, this.keyIdentifier, 0, this.keyIdentifier.length);
	}

	/**
	 * Decode {@code SubjectKeyIdentifierExtensionData} from an ASN.1 data
	 * object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @param critical The extension's critical flag.
	 * @return The decoded extension data.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static SubjectKeyIdentifierExtensionData decode(ASN1Primitive primitive, boolean critical)
			throws IOException {
		return new SubjectKeyIdentifierExtensionData(critical,
				decodePrimitive(primitive, ASN1OctetString.class).getOctets());
	}

	/**
	 * Get the key identifier.
	 *
	 * @return The key identifier.
	 */
	public byte[] getKeyIdentifier() {
		return this.keyIdentifier;
	}

	@Override
	public Attributes toAttributes() {
		Attributes extensionAttributes = super.toAttributes();

		extensionAttributes.addChild(AttributesI18N.formatSTR_KEYIDENTIFIER(),
				Attributes.printBytes(this.keyIdentifier));
		return extensionAttributes;
	}

}
