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
import java.util.Collection;

import de.carne.certmgr.store.asn1.ASN1Decoder;
import de.carne.certmgr.store.asn1.ASN1Encodable;
import de.carne.certmgr.store.asn1.ASN1Encoder;
import de.carne.certmgr.store.asn1.TaggedObject;

/**
 * Distribution point name object.
 */
public class DistributionPointName implements ASN1Encodable {

	private static final int TAGNO_NAMES = 0;

	private final GeneralNames names;

	private DistributionPointName(GeneralNames names) {
		this.names = names;
	}

	/**
	 * Construct DistributionPointName.
	 */
	public DistributionPointName() {
		this(new GeneralNames());
	}

	/**
	 * Add a name.
	 *
	 * @param name The name to add.
	 * @return The updated distribution point name.
	 */
	public DistributionPointName addName(GeneralName name) {
		this.names.addElement(name);
		return this;
	}

	/**
	 * Get the distribution point names.
	 *
	 * @return The distribution point names.
	 */
	public Collection<GeneralName> getNames() {
		return this.names.values();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Encodable#asn1Encode(de.carne.certmgr.store.asn1.ASN1Encoder)
	 */
	@Override
	public void asn1Encode(ASN1Encoder encoder) {
		if (this.names.size() > 0) {
			encoder.asn1EncodeTaggedObject(false, TAGNO_NAMES, this.names);
		}
	}

	/**
	 * Decode distribution point name.
	 *
	 * @param decoder The decoder to use.
	 * @return The decoded object.
	 * @throws IOException if the decoding fails.
	 */
	public static DistributionPointName asn1Decode(ASN1Decoder decoder) throws IOException {
		TaggedObject<ASN1Decoder> taggedObject = decoder.asn1EncodeTaggedObject(TAGNO_NAMES);

		return new DistributionPointName(GeneralNames.asn1Decode(taggedObject.getObject()));
	}

}
