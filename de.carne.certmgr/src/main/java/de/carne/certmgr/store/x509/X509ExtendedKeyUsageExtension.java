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
import java.util.HashSet;
import java.util.Set;

import de.carne.certmgr.store.asn1.ASN1Decoder;
import de.carne.certmgr.store.asn1.ASN1Encodable;
import de.carne.certmgr.store.asn1.ASN1Encoder;
import de.carne.certmgr.store.asn1.OIDRegistry;

/**
 * X.509 Extended Key Usage certificate extension.
 */
public class X509ExtendedKeyUsageExtension extends X509Extension {

	/**
	 * The extension's OID.
	 */
	public static final String OID = OIDRegistry.register("2.5.29.37", "ExtendedKeyUsage");

	private Set<ExtendedKeyUsage> usageSet = new HashSet<>();

	/**
	 * Construct X509ExtendedKeyUsageExtension.
	 *
	 * @param critical The critical flag.
	 */
	public X509ExtendedKeyUsageExtension(boolean critical) {
		super(OID, critical);
	}

	/**
	 * Construct X509ExtendedKeyUsageExtension.
	 *
	 * @param critical The critical flag.
	 * @param usages The usages to set.
	 */
	public X509ExtendedKeyUsageExtension(boolean critical, ExtendedKeyUsage... usages) {
		super(OID, critical);
		addUsages(usages);
	}

	/**
	 * Add a usage.
	 *
	 * @param usage The usage to add.
	 * @return The updated extension object.
	 */
	public X509ExtendedKeyUsageExtension addUsage(ExtendedKeyUsage usage) {
		this.usageSet.add(usage);
		return this;
	}

	/**
	 * Add multiple usages.
	 *
	 * @param usages The usages to add.
	 * @return The updated extension object.
	 */
	public X509ExtendedKeyUsageExtension addUsages(ExtendedKeyUsage... usages) {
		for (ExtendedKeyUsage usage : usages) {
			this.usageSet.add(usage);
		}
		return this;
	}

	/**
	 * Get the set usage flags.
	 *
	 * @return The set usage flags.
	 */
	public ExtendedKeyUsage[] getUsages() {
		return this.usageSet.toArray(new ExtendedKeyUsage[this.usageSet.size()]);
	}

	/**
	 * Create extension instance from encoded data.
	 *
	 * @param critical The extension's critical flag.
	 * @param decoder The decoder providing access to the encoded data.
	 * @return The decoded instance.
	 * @throws IOException if the decoding fails.
	 */
	public static X509ExtendedKeyUsageExtension asn1Decode(boolean critical, ASN1Decoder decoder) throws IOException {
		X509ExtendedKeyUsageExtension decoded = new X509ExtendedKeyUsageExtension(critical);

		for (ASN1Decoder sequenceEntry : decoder.asn1DecodeSequence(-1, -1)) {
			decoded.addUsage(ExtendedKeyUsage.valueOf(sequenceEntry.asn1DecodeOID()));
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
		for (ExtendedKeyUsage usage : this.usageSet) {
			encoder.asn1EncodeOID(usage.value());
		}
	}

}
