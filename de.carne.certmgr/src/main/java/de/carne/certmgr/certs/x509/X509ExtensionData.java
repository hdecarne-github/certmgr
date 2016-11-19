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

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;

import de.carne.certmgr.certs.asn1.ASN1Data;
import de.carne.certmgr.certs.asn1.OIDs;

/**
 * Base class for X.509 extension object data.
 */
public abstract class X509ExtensionData extends ASN1Data implements AttributesProvider {

	private final String oid;

	private boolean critical;

	/**
	 * Construct {@code X509ExtensionData}.
	 *
	 * @param oid The extension OID.
	 * @param critical The extension's critical flag.
	 */
	protected X509ExtensionData(String oid, boolean critical) {
		assert oid != null;

		this.oid = oid;
		this.critical = critical;
	}

	/**
	 * Construct a {@code X509ExtensionData} from an encoded data stream.
	 *
	 * @param oid The extension OID.
	 * @param critical The extension's critical flag.
	 * @param data The encoded extension data.
	 * @return The decoded extension data object.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static X509ExtensionData decode(String oid, boolean critical, byte[] data) throws IOException {
		assert oid != null;

		ASN1Primitive primitive = JcaX509ExtensionUtils.parseExtensionValue(data);
		X509ExtensionData decoded;

		switch (oid) {
		case BasicConstraintsExtensionData.OID:
			decoded = BasicConstraintsExtensionData.decode(primitive, critical);
			break;
		case KeyUsageExtensionData.OID:
			decoded = KeyUsageExtensionData.decode(primitive, critical);
			break;
		case ExtendedKeyUsageExtensionData.OID:
			decoded = ExtendedKeyUsageExtensionData.decode(primitive, critical);
			break;
		case SubjectAlternativeNameExtensionData.OID:
			decoded = SubjectAlternativeNameExtensionData.decode(primitive, critical);
			break;
		case SubjectKeyIdentifierExtensionData.OID:
			decoded = SubjectKeyIdentifierExtensionData.decode(primitive, critical);
			break;
		case AuthorityKeyIdentifierExtensionData.OID:
			decoded = AuthorityKeyIdentifierExtensionData.decode(primitive, critical);
			break;
		case CRLNumberExtensionData.OID:
			decoded = CRLNumberExtensionData.decode(primitive, critical);
			break;
		case CRLDistributionPointsExtensionData.OID:
			decoded = CRLDistributionPointsExtensionData.decode(primitive, critical);
			break;
		default:
			decoded = new CustomExtensionData(oid, critical, data);
		}
		return decoded;
	}

	/**
	 * Get this extension's OID.
	 *
	 * @return This extension's OID.
	 */
	public String oid() {
		return this.oid;
	}

	/**
	 * Get this extension's critical flag.
	 *
	 * @return This extension's critical flag.
	 */
	public boolean getCritical() {
		return this.critical;
	}

	/**
	 * Set this extension's critical flag.
	 *
	 * @param critical The value to set.
	 */
	public void setCritical(boolean critical) {
		this.critical = critical;
	}

	@Override
	public Attributes toAttributes() {
		String extensionName = OIDs.toString(this.oid);

		return new Attributes(AttributesI18N.formatSTR_EXTENSION(extensionName), (this.critical
				? AttributesI18N.formatSTR_EXTENSION_CRITICAL() : AttributesI18N.formatSTR_EXTENSION_NONCRITICAL()));
	}

}
