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
import org.bouncycastle.asn1.ASN1TaggedObject;

import de.carne.certmgr.certs.asn1.ASN1Data;

/**
 * Distribution point name object.
 */
public class DistributionPointName extends ASN1Data implements AttributesContent {

	private final GeneralNames fullName;

	/**
	 * Construct {@code DistributionPointName}.
	 *
	 * @param fullName The names.
	 */
	public DistributionPointName(GeneralNames fullName) {
		assert fullName != null;

		this.fullName = fullName;
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

		switch (taggedObjectTag) {
		case 0:
			assert fullName == null;

			fullName = GeneralNames.decode(taggedObject.getObject());
			break;
		case 1:
			// TODO
			break;
		default:
			throw new IOException("Unsupported tag: " + taggedObjectTag);
		}
		return new DistributionPointName(fullName);
	}

	@Override
	public void addToAttributes(Attributes attributes) {
		if (this.fullName != null) {
			attributes.add(this.fullName);
		}
	}

}
