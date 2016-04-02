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

import de.carne.certmgr.store.asn1.ASN1Decoder;
import de.carne.certmgr.store.asn1.ASN1Encodable;
import de.carne.certmgr.store.asn1.ASN1Encoder;
import de.carne.certmgr.store.asn1.OIDRegistry;

/**
 * X.509 Basic Constraints certificate extension.
 */
public class X509BasicConstraintsExtension extends X509Extension {

	/**
	 * The extension's OID.
	 */
	public static final String OID = OIDRegistry.register("2.5.29.19", "BasicConstraints");

	private boolean ca;
	private int pathLenConstraint;

	/**
	 * Construct X509BasicConstraintsExtension.
	 *
	 * @param critical The critical flag.
	 * @param ca The certificate authority flag.
	 */
	public X509BasicConstraintsExtension(boolean critical, boolean ca) {
		this(critical, ca, -1);
	}

	/**
	 * Construct X509BasicConstraintsExtension.
	 *
	 * @param critical The critical flag.
	 * @param ca The certificate authority flag.
	 * @param pathLenConstraint The path lenght constraint.
	 */
	public X509BasicConstraintsExtension(boolean critical, boolean ca, int pathLenConstraint) {
		super(OID, critical);
		this.ca = ca;
		this.pathLenConstraint = pathLenConstraint;
	}

	/**
	 * Create extension instance from encoded data.
	 *
	 * @param critical The extension's critical flag.
	 * @param decoder The decoder providing access to the encoded data.
	 * @return The decoded instance.
	 * @throws IOException if the decoding fails.
	 */
	public static X509BasicConstraintsExtension asn1Decode(boolean critical, ASN1Decoder decoder) throws IOException {
		ASN1Decoder[] sequence = decoder.asn1DecodeSequence(0, 2);
		boolean ca = (sequence.length > 0 ? sequence[0].asn1DecodeBoolean() : false);
		int pathLenConstraint = (sequence.length > 1 ? sequence[1].asn1DecodeInteger() : -1);

		return new X509BasicConstraintsExtension(critical, ca, pathLenConstraint);
	}

	/**
	 * Get the CA flag.
	 *
	 * @return The CA flag.
	 */
	public boolean isCA() {
		return this.ca;
	}

	/**
	 * Get the path length constraint value.
	 *
	 * @return The path length constraint value.
	 */
	public int getPathLenConstraint() {
		return this.pathLenConstraint;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Encodable#asn1Encode(de.carne.certmgr.store.asn1.ASN1Encoder)
	 */
	@Override
	public void asn1Encode(ASN1Encoder encoder) {
		encoder.asn1EncodeSequence(new ASN1Encodable() {

			@Override
			public void asn1Encode(ASN1Encoder encoder2) {
				asn1Encode2(encoder2);
			}

		});
	}

	void asn1Encode2(ASN1Encoder encoder) {
		encoder.asn1EncodeBoolean(this.ca);
		if (this.pathLenConstraint >= 0) {
			encoder.asn1EncodeInteger(this.pathLenConstraint);
		}
	}

}
