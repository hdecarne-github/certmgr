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
import java.math.BigInteger;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;

/**
 * X.509 <a href="https://tools.ietf.org/html/rfc5280#section-5.2.3">CRL Number
 * Extension</a> data.
 */
public class CRLNumberExtensionData extends X509ExtensionData {

	/**
	 * Extension OID.
	 */
	public static final String OID = "2.5.29.20";

	private final BigInteger crlNumber;

	/**
	 * Construct {@code CRLNumberExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 * @param crlNumber The CRL number.
	 */
	public CRLNumberExtensionData(boolean critical, BigInteger crlNumber) {
		super(OID, critical);

		assert crlNumber != null;

		this.crlNumber = crlNumber;
	}

	/**
	 * Decode {@code CRLNumberExtensionData} from an ASN.1 data object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @param critical The extension's critical flag.
	 * @return The decoded extension data.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static CRLNumberExtensionData decode(ASN1Primitive primitive, boolean critical) throws IOException {
		return new CRLNumberExtensionData(critical, decodePrimitive(primitive, ASN1Integer.class).getValue());
	}

	/**
	 * Get the CRL number.
	 *
	 * @return The CRL number.
	 */
	public BigInteger getCRLNumber() {
		return this.crlNumber;
	}

	@Override
	public Attributes toAttributes() {
		Attributes extensionAttributes = super.toAttributes();

		extensionAttributes.addChild(AttributesI18N.formatSTR_CRLNUMBER(), this.crlNumber.toString());
		return extensionAttributes;
	}

}
