/*
 * Copyright (c) 2015-2018 Holger de Carne and contributors, All Rights Reserved.
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

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

import de.carne.certmgr.certs.asn1.ASN1Data;
import de.carne.check.Check;
import de.carne.check.Nullable;

/**
 * Distribution point object.
 */
public class DistributionPoint extends ASN1Data implements AttributesContent {

	@Nullable
	private final DistributionPointName name;
	@Nullable
	private final GeneralNames crlIssuer;
	@Nullable
	private ReasonFlags reasons;

	/**
	 * Construct {@code DistributionPoint}.
	 *
	 * @param name The distribution point's name data.
	 */
	public DistributionPoint(DistributionPointName name) {
		this(name, null, null);
	}

	/**
	 * Construct {@code DistributionPoint}.
	 *
	 * @param crlIssuer The distribution point's CRL issuer.
	 */
	public DistributionPoint(GeneralNames crlIssuer) {
		this(null, crlIssuer, null);
	}

	private DistributionPoint(@Nullable DistributionPointName name, @Nullable GeneralNames crlIssuer,
			@Nullable ReasonFlags reasons) {
		this.name = name;
		this.crlIssuer = crlIssuer;
		this.reasons = reasons;
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
				name = DistributionPointName.decode(taggedObject.getObject());
				break;
			case 1:
				reasons = ReasonFlags.decode(taggedObject.getObject());
				break;
			case 2:
				crlIssuer = GeneralNames.decode(taggedObject.getObject());
				break;
			default:
				throw new IOException("Unsupported tag: " + taggedObjectTag);
			}
		}
		return new DistributionPoint(name, crlIssuer, reasons);
	}

	/**
	 * Get the defined distribution point name object.
	 *
	 * @return The defined distribution point name object or {@code null} if none has been defined.
	 */
	@Nullable
	public DistributionPointName getName() {
		return this.name;
	}

	/**
	 * Get the defined CRL issuer's names.
	 *
	 * @return The defined CRL issuer's names or {@code null} if none have been defined.
	 */
	@Nullable
	public GeneralNames getCRLIssuer() {
		return this.crlIssuer;
	}

	/**
	 * Set the reasons this distribution point is authoritative for.
	 *
	 * @param reasons The reasons this distribution point is authoritative for. May be {@code null} to use this
	 *        distribution point for all reasons.
	 */
	public void setReasons(@Nullable ReasonFlags reasons) {
		this.reasons = reasons;
	}

	/**
	 * Get the reasons this distribution point is authoritative for.
	 *
	 * @return The reasons this distribution point is authoritative for or {@code null} if this distribution point is
	 *         used for all reasons.
	 */
	@Nullable
	public ReasonFlags getReasons() {
		return this.reasons;
	}

	@Override
	public ASN1Encodable encode() throws IOException {
		ASN1EncodableVector sequence = new ASN1EncodableVector();

		if (this.name != null) {
			sequence.add(new DERTaggedObject(true, 0, this.name.encode()));
		}
		if (this.reasons != null) {
			sequence.add(new DERTaggedObject(false, 1, this.reasons.encode()));
		}
		if (this.crlIssuer != null) {
			sequence.add(new DERTaggedObject(false, 2, this.crlIssuer.encode()));
		}
		return new DERSequence(sequence);
	}

	@Override
	public void addToAttributes(Attributes attributes) {
		if (this.name != null) {
			attributes.add(this.name);
		}
		if (this.reasons != null) {
			attributes.add(AttributesI18N.formatSTR_DISTRIBUTIONPOINT_REASONS()).add(Check.notNull(this.reasons));
		}
		if (this.crlIssuer != null) {
			attributes.add(AttributesI18N.formatSTR_DISTRIBUTIONPOINT_CRLISSUER()).add(Check.notNull(this.crlIssuer));
		}
	}

}
