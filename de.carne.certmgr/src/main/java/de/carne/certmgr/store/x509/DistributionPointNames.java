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
import de.carne.certmgr.store.asn1.ASN1EncodableSequence;

/**
 * Distribution point names object.
 */
public class DistributionPointNames extends ASN1EncodableSequence<DistributionPointName> {

	/**
	 * Decode distribution point names.
	 *
	 * @param decoder The decoder to use.
	 * @return The decoded object.
	 * @throws IOException if the decoding fails.
	 */
	public static DistributionPointNames asn1Decode(ASN1Decoder decoder) throws IOException {
		ASN1Decoder[] sequence = decoder.asn1DecodeSequence(-1, -1);
		DistributionPointNames names = new DistributionPointNames();

		for (ASN1Decoder sequenceEntry : sequence) {
			names.addElement(DistributionPointName.asn1Decode(sequenceEntry));
		}
		return names;
	}

}
