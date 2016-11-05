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
import java.util.List;

import org.bouncycastle.asn1.ASN1Primitive;

/**
 * X.509 <a href="https://tools.ietf.org/html/rfc5280#section-4.2.1.6">Subject
 * Alternative Name Extension</a> data.
 */
public class SubjectAlternativeNameExtensionData extends X509ExtensionData {

	/**
	 * Extension OID.
	 */
	public static final String OID = "2.5.29.17";

	private final GeneralNames generalNames;

	/**
	 * Construct {@code SubjectAlternativeNameExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 */
	public SubjectAlternativeNameExtensionData(boolean critical) {
		super(OID, critical);
		this.generalNames = new GeneralNames();
	}

	/**
	 * Construct {@code SubjectAlternativeNameExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 * @param generalNames The extension's general names.
	 */
	public SubjectAlternativeNameExtensionData(boolean critical, GeneralNames generalNames) {
		super(OID, critical);
		this.generalNames = generalNames;
	}

	/**
	 * Decode {@code SubjectAlternativeNameExtensionData} from an ASN.1 data
	 * object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @param critical The extension's critical flag.
	 * @return The decoded extension data.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static SubjectAlternativeNameExtensionData decode(ASN1Primitive primitive, boolean critical)
			throws IOException {
		return new SubjectAlternativeNameExtensionData(critical, GeneralNames.decode(primitive));
	}

	/**
	 * Get the contained name objects.
	 *
	 * @return The contained name objects.
	 */
	public List<GeneralName> getNames() {
		return this.generalNames.getNames();
	}

	@Override
	public Attributes toAttributes() {
		Attributes extensionAttributes = super.toAttributes();
		int nameIndex = 0;

		for (GeneralName name : this.generalNames.getNames()) {
			extensionAttributes.addChild(AttributesI18N.formatSTR_GENERALNAME(nameIndex), name.toString());
			nameIndex++;
		}
		return extensionAttributes;
	}

}
