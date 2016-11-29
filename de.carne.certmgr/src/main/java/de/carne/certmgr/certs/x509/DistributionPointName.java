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

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;

import de.carne.certmgr.certs.asn1.ASN1Data;
import de.carne.certmgr.certs.x500.X500Names;

/**
 * Distribution point name object.
 */
public class DistributionPointName extends ASN1Data implements AttributesContent {

	private final GeneralNames fullName;
	private final X500Principal nameRelativeToCRLIssuer;

	/**
	 * Construct {@code DistributionPointName}.
	 *
	 * @param fullName The full name.
	 * @param nameRelativeToCRLIssuer The relative name.
	 */
	public DistributionPointName(GeneralNames fullName, X500Principal nameRelativeToCRLIssuer) {
		assert fullName != null || nameRelativeToCRLIssuer != null;

		this.fullName = fullName;
		this.nameRelativeToCRLIssuer = nameRelativeToCRLIssuer;
	}

	/**
	 * Decode {@code DistributionPointName} object from an ASN.1 data object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @return The decoded distribution point name object.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static DistributionPointName decode(ASN1Primitive primitive) throws IOException {
		ASN1TaggedObject taggedObject = decodePrimitive(primitive, ASN1TaggedObject.class);
		int taggedObjectTag = taggedObject.getTagNo();
		GeneralNames fullName = null;
		X500Principal nameRelativeToCRLIssuer = null;

		switch (taggedObjectTag) {
		case 0:
			fullName = GeneralNames.decode(taggedObject.getObject());
			break;
		case 1:
			nameRelativeToCRLIssuer = new X500Principal(taggedObject.getObject().getEncoded());
			break;
		default:
			throw new IOException("Unsupported tag: " + taggedObjectTag);
		}
		return new DistributionPointName(fullName, nameRelativeToCRLIssuer);
	}

	@Override
	public ASN1Encodable encode() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addToAttributes(Attributes attributes) {
		if (this.fullName != null) {
			attributes.add(this.fullName);
		}
		if (this.nameRelativeToCRLIssuer != null) {
			attributes.add(AttributesI18N.formatSTR_DISTRIBUTIONPOINTNAME_NAMERELATIVETOCRLISSUER(),
					X500Names.toString(this.nameRelativeToCRLIssuer));
		}
	}

}
