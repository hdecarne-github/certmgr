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
package de.carne.certmgr.store.x509;

import java.io.IOException;
import java.util.Collection;

import de.carne.certmgr.store.asn1.ASN1Decoder;
import de.carne.certmgr.store.asn1.ASN1Encoder;
import de.carne.certmgr.store.asn1.OIDRegistry;

/**
 * ASN.1 Subject Alternative Name certificate extension.
 */
public class X509SubjectAlternativeNameExtension extends X509Extension {

	/**
	 * The extension's OID.
	 */
	public static final String OID = OIDRegistry.register("2.5.29.17", "SubjectAlternativeName");

	private GeneralNames names = new GeneralNames();

	/**
	 * Construct X509SubjectAlternativeNameExtension.
	 *
	 * @param critical The critical flag.
	 */
	public X509SubjectAlternativeNameExtension(boolean critical) {
		super(OID, critical);
	}

	/**
	 * Add a name.
	 *
	 * @param name The name to add.
	 * @return The updated extension object.
	 */
	public X509SubjectAlternativeNameExtension addName(GeneralName name) {
		this.names.addElement(name);
		return this;
	}

	/**
	 * Get the contained names.
	 *
	 * @return The contained names.
	 */
	public Collection<GeneralName> getNames() {
		return this.names.values();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Encodable#asn1Encode(de.carne.certmgr.store.asn1.ASN1Encoder)
	 */
	@Override
	public void asn1Encode(ASN1Encoder encoder) {
		this.names.asn1Encode(encoder);
	}

	/**
	 * Create extension instance from encoded data.
	 *
	 * @param critical The extension's critical flag.
	 * @param decoder The decoder providing access to the encoded data.
	 * @return The decoded instance.
	 * @throws IOException if the decoding fails.
	 */
	public static X509SubjectAlternativeNameExtension asn1Decode(boolean critical, ASN1Decoder decoder)
			throws IOException {
		ASN1Decoder[] sequence = decoder.asn1DecodeSequence(-1, -1);
		X509SubjectAlternativeNameExtension decoded = new X509SubjectAlternativeNameExtension(critical);

		for (ASN1Decoder sequenceEntry : sequence) {
			decoded.addName(GeneralName.asn1Decode(sequenceEntry));
		}
		return decoded;
	}

}
