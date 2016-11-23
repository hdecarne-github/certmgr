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
import org.checkerframework.checker.nullness.qual.Nullable;

import de.carne.certmgr.certs.asn1.ASN1Data;

/**
 * Distribution point object.
 */
public class DistributionPoint extends ASN1Data implements AttributesContent {

	private final DistributionPointName name;
	private final ReasonFlags reasons;
	private final GeneralNames crlIssuer;

	/**
	 * Construct {@code DistributionPoint}.
	 *
	 * @param name The (optional) distribution point's name data.
	 * @param reasons The (optional) distribution point's reason flags.
	 * @param crlIssuer The (optional) distribution point's CRL issuer.
	 */
	public DistributionPoint(DistributionPointName name, ReasonFlags reasons, GeneralNames crlIssuer) {
		this.name = name;
		this.reasons = reasons;
		this.crlIssuer = crlIssuer;
	}

	/**
	 * Decode {@code DistributionPoint} object from an ASN.1 data object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @return The decoded distribution point object.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static DistributionPoint decode(ASN1Primitive primitive) throws IOException {
		ASN1Primitive[] sequence = decodeSequence(primitive, 1, Integer.MAX_VALUE);
		DistributionPointName name = null;
		ReasonFlags reasons = null;
		GeneralNames crlIssuer = null;

		for (ASN1Primitive sequenceEntry : sequence) {
			ASN1TaggedObject taggedObject = decodePrimitive(sequenceEntry, ASN1TaggedObject.class);
			int taggedObjectTag = taggedObject.getTagNo();

			switch (taggedObjectTag) {
			case 0:
				assert name == null;

				name = DistributionPointName.decode(taggedObject.getObject());
				break;
			case 1:
				assert reasons == null;

				reasons = ReasonFlags.decode(taggedObject.getObject());
				break;
			case 2:
				assert crlIssuer == null;

				crlIssuer = GeneralNames.decode(taggedObject.getObject());
				break;
			default:
				throw new IOException("Unsupported tag: " + taggedObjectTag);
			}
		}
		return new DistributionPoint(name, reasons, crlIssuer);
	}

	/**
	 * Get the defined distribution point name object.
	 *
	 * @return The defined distribution point name object or {@code null} if
	 *         none has been defined.
	 */
	public @Nullable DistributionPointName getName() {
		return this.name;
	}

	/**
	 * Get the defined distribution point reasons.
	 *
	 * @return The defined distribution point reasons or {@code null} if none
	 *         have been defined.
	 */
	public @Nullable ReasonFlags getReasons() {
		return this.reasons;
	}

	/**
	 * Get the defined CRL issuer's names.
	 *
	 * @return The defined CRL issuer's names or {@code null} if none have been
	 *         defined.
	 */
	public @Nullable GeneralNames getCRLIssuer() {
		return this.crlIssuer;
	}

	@Override
	public void addAttributes(Attributes attributes) {
		if (this.name != null) {
			attributes.add(AttributesI18N.formatSTR_DISTRIBUTIONPOINT_NAME()).add(this.name);
		}
		if (this.reasons != null) {
			attributes.add(AttributesI18N.formatSTR_DISTRIBUTIONPOINT_REASONS()).add(this.reasons);
		}
		if (this.crlIssuer != null) {
			attributes.add(AttributesI18N.formatSTR_DISTRIBUTIONPOINT_CRLISSUER()).add(this.crlIssuer);
		}
	}

}
