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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.carne.certmgr.store.asn1.ASN1Decoder;
import de.carne.certmgr.store.asn1.ASN1Encodable;
import de.carne.certmgr.store.asn1.ASN1Encoder;
import de.carne.certmgr.store.asn1.OIDRegistry;

/**
 * X.509 CRL Distribution Points certificate extension.
 */
public class X509CRLDistributionPointsExtension extends X509Extension {

	/**
	 * The extension's OID.
	 */
	public static final String OID = OIDRegistry.register("2.5.29.31", "CRLDistributionPoints");

	private List<DistributionPoint> distributionPoints = new ArrayList<>();

	/**
	 * Construct X509CRLDistributionPointsExtension.
	 *
	 * @param critical The critical flag.
	 */
	public X509CRLDistributionPointsExtension(boolean critical) {
		super(OID, critical);
	}

	/**
	 * Add a CRL distribution point.
	 *
	 * @param distributionPoint The distribution point to add.
	 * @return The updated extension object.
	 */
	public X509CRLDistributionPointsExtension addDistributionPoint(DistributionPoint distributionPoint) {
		this.distributionPoints.add(distributionPoint);
		return this;
	}

	/**
	 * Get the contained distribution points.
	 *
	 * @return The contained distribution points.
	 */
	public Collection<DistributionPoint> getDistributionPoints() {
		return Collections.unmodifiableCollection(this.distributionPoints);
	}

	/**
	 * Create extension instance from encoded data.
	 *
	 * @param critical The extension's critical flag.
	 * @param decoder The decoder providing access to the encoded data.
	 * @return The decoded instance.
	 * @throws IOException if the decoding fails.
	 */
	public static X509CRLDistributionPointsExtension asn1Decode(boolean critical, ASN1Decoder decoder)
			throws IOException {
		ASN1Decoder[] sequence = decoder.asn1DecodeSequence(-1, -1);
		X509CRLDistributionPointsExtension decoded = new X509CRLDistributionPointsExtension(critical);

		for (ASN1Decoder sequenceEntry : sequence) {
			decoded.addDistributionPoint(DistributionPoint.asn1Decode(sequenceEntry));
		}
		return decoded;
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
		for (DistributionPoint distributionPoint : this.distributionPoints) {
			distributionPoint.asn1Encode(encoder);
		}
	}

}
