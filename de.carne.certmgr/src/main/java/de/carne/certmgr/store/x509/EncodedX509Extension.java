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

/**
 * Class representing an encoded X.509 extension.
 */
public final class EncodedX509Extension {

	private String oid;
	private boolean critical;
	private byte[] encoded;
	private X509Extension decoded;

	private EncodedX509Extension(String oid, boolean critical, byte[] encoded, X509Extension decoded) {
		this.oid = oid;
		this.critical = critical;
		this.encoded = encoded;
		this.decoded = decoded;
	}

	/**
	 * Decode a X.509 extension.
	 *
	 * @param oid The extension's oid.
	 * @param critical The extension's critical flag.
	 * @param decoder The decoder to decode from.
	 * @return The decoded extension.
	 * @throws IOException if the decoding fails.
	 */
	public static EncodedX509Extension decode(String oid, boolean critical, ASN1Decoder decoder) throws IOException {
		X509Extension decoded;

		if (X509BasicConstraintsExtension.OID.equals(oid)) {
			decoded = X509BasicConstraintsExtension.asn1Decode(critical, decoder);
		} else if (X509KeyUsageExtension.OID.equals(oid)) {
			decoded = X509KeyUsageExtension.asn1Decode(critical, decoder);
		} else if (X509ExtendedKeyUsageExtension.OID.equals(oid)) {
			decoded = X509ExtendedKeyUsageExtension.asn1Decode(critical, decoder);
		} else if (X509SubjectAlternativeNameExtension.OID.equals(oid)) {
			decoded = X509SubjectAlternativeNameExtension.asn1Decode(critical, decoder);
		} else if (X509CRLDistributionPointsExtension.OID.equals(oid)) {
			decoded = X509CRLDistributionPointsExtension.asn1Decode(critical, decoder);
		} else {
			decoded = null;
		}
		return new EncodedX509Extension(oid, critical, decoder.getEncoded(), decoded);
	}

	/**
	 * Get the extension's oid.
	 *
	 * @return The extension's oid.
	 */
	public String getOID() {
		return this.oid;
	}

	/**
	 * Get the extension's critical flag.
	 *
	 * @return The extension's critical flag.
	 */
	public boolean isCritical() {
		return this.critical;
	}

	/**
	 * Get the encoded extension data.
	 *
	 * @return The encoded extension data.
	 */
	public byte[] getEncoded() {
		return this.encoded;
	}

	/**
	 * Get the decoded extension object. The function returns null if the extension type is unknown.
	 *
	 * @return The decoded extension object or null if the extension type is unknown.
	 */
	public X509Extension getDecoded() {
		return this.decoded;
	}

}
