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

import de.carne.certmgr.store.asn1.ASN1BitString;
import de.carne.certmgr.store.asn1.ASN1Decoder;
import de.carne.certmgr.store.asn1.ASN1Encodable;
import de.carne.certmgr.store.asn1.ASN1Encoder;
import de.carne.certmgr.store.asn1.TaggedObject;

/**
 * Distribution point object.
 */
public class DistributionPoint implements ASN1Encodable {

	private static final int TAGNO_NAMES = 0;
	private static final int TAGNO_REASONS = 1;
	private static final int TAGNO_CRLISSUER = 2;

	private final DistributionPointNames names;
	private final ASN1BitString reasons;
	private final GeneralNames crlIssuers;

	private DistributionPoint(DistributionPointNames names, ASN1BitString reasons, GeneralNames crlIssuers) {
		this.names = (names != null ? names : new DistributionPointNames());
		this.reasons = (reasons != null ? reasons : new ASN1BitString());
		this.crlIssuers = (crlIssuers != null ? crlIssuers : new GeneralNames());
	}

	/**
	 * Construct DistributionPoint.
	 */
	public DistributionPoint() {
		this(null, null, null);
	}

	/**
	 * Add a distribution point name.
	 *
	 * @param name The distribution point name to add.
	 * @return The updated distribution point object.
	 */
	public DistributionPoint addName(DistributionPointName name) {
		this.names.addElement(name);
		return this;
	}

	/**
	 * Get the contained distribution point names.
	 *
	 * @return The contained distribution point names.
	 */
	public Collection<DistributionPointName> getNames() {
		return this.names.values();
	}

	/**
	 * Set the reason codes (previously set reason codes are overwritten).
	 *
	 * @param reasons The reason codes to set.
	 * @return The updated distribution point object.
	 */
	public DistributionPoint setReasons(RevokeReason... reasons) {
		int reasonValues = 0;

		for (RevokeReason reason : reasons) {
			reasonValues |= reason.value();
		}
		this.reasons.setBits(reasonValues);
		return this;
	}

	/**
	 * Get the contained reason codes.
	 *
	 * @return The contained reason codes.
	 */
	public RevokeReason[] getReasons() {
		return getReasonsFromBitString(this.reasons.getBitBytes());
	}

	/**
	 * Add an issuer name.
	 *
	 * @param issuer The issuer name to add.
	 * @return The updated distribution point object.
	 */
	public DistributionPoint addCRLIssuer(GeneralName issuer) {
		this.crlIssuers.addElement(issuer);
		return this;
	}

	/**
	 * Get the contained issuer names.
	 *
	 * @return The contained issuer names.
	 */
	public Collection<GeneralName> getCRLIssuers() {
		return this.crlIssuers.values();
	}

	/**
	 * Decode distribution point.
	 *
	 * @param decoder The decoder to use.
	 * @return The decoded object.
	 * @throws IOException if the decoding fails.
	 */
	public static DistributionPoint asn1Decode(ASN1Decoder decoder) throws IOException {
		ASN1Decoder[] sequence = decoder.asn1DecodeSequence(-1, -1);
		DistributionPointNames names = null;
		ASN1BitString reasons = null;
		GeneralNames crlIssuers = null;

		for (ASN1Decoder sequenceEntry : sequence) {
			TaggedObject<ASN1Decoder> taggedSequenceEntry = sequenceEntry.asn1EncodeTaggedObject(TAGNO_NAMES,
					TAGNO_REASONS, TAGNO_CRLISSUER);
			int tagNo = taggedSequenceEntry.getTagNo();

			switch (tagNo) {
			case TAGNO_NAMES:
				names = DistributionPointNames.asn1Decode(taggedSequenceEntry.getObject());
				break;
			case TAGNO_REASONS:
				reasons = ASN1BitString.asn1Decode(taggedSequenceEntry.getObject());
				break;
			case TAGNO_CRLISSUER:
				crlIssuers = GeneralNames.asn1Decode(taggedSequenceEntry.getObject());
				break;
			default:
				throw new IllegalStateException("Unexpedted tag no: " + tagNo);
			}
		}
		return new DistributionPoint(names, reasons, crlIssuers);
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
		if (this.names.size() > 0) {
			encoder.asn1EncodeTaggedObject(false, TAGNO_NAMES, this.names);
		}
		if (this.reasons.getBits() != 0) {
			encoder.asn1EncodeTaggedObject(false, TAGNO_REASONS, this.reasons);
		}
		if (this.crlIssuers.size() > 0) {
			encoder.asn1EncodeTaggedObject(false, TAGNO_CRLISSUER, this.crlIssuers);
		}
	}

	private static RevokeReason[] getReasonsFromBitString(byte[] bs) {
		ArrayList<RevokeReason> reasons = new ArrayList<>();

		if (bs.length > 0) {
			int reasonValue = 1;

			for (byte b : bs) {
				byte reasonBit = 1;

				for (int reasonBitsIndex = 0; reasonBitsIndex < 8; reasonBitsIndex++) {
					if ((b & reasonBit) == reasonBit) {
						reasons.add(RevokeReason.valueOf(reasonValue));
					}
					reasonBit <<= 1;
					reasonValue <<= 1;
				}
			}
		}
		return reasons.toArray(new RevokeReason[reasons.size()]);
	}

}
