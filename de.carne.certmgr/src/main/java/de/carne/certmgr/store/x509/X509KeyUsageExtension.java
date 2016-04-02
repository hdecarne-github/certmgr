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

import de.carne.certmgr.store.asn1.ASN1BitString;
import de.carne.certmgr.store.asn1.ASN1Decoder;
import de.carne.certmgr.store.asn1.ASN1Encoder;
import de.carne.certmgr.store.asn1.OIDRegistry;

/**
 * X.509 KeyUsage certificate extension.
 */
public class X509KeyUsageExtension extends X509Extension {

	/**
	 * The extension's OID.
	 */
	public static final String OID = OIDRegistry.register("2.5.29.15", "KeyUsage");

	private ASN1BitString usageBitString = new ASN1BitString();

	/**
	 * Construct X509KeyUsageExtension.
	 *
	 * @param critical The critical flag.
	 */
	public X509KeyUsageExtension(boolean critical) {
		super(OID, critical);
	}

	/**
	 * Construct X509KeyUsageExtension.
	 *
	 * @param critical The critical flag.
	 * @param usages The usage flags to set.
	 */
	public X509KeyUsageExtension(boolean critical, KeyUsage... usages) {
		super(OID, critical);
		addUsages(usages);
	}

	/**
	 * Add key usage flag.
	 *
	 * @param usage The usage flag to add.
	 * @return The updated extension object.
	 */
	public X509KeyUsageExtension addUsage(KeyUsage usage) {
		this.usageBitString.setBits(this.usageBitString.getBits() | usage.value());
		return this;
	}

	/**
	 * Add multiple usage flags.
	 *
	 * @param usages The usage flags to add.
	 * @return The updated extension object.
	 */
	public X509KeyUsageExtension addUsages(KeyUsage... usages) {
		int usageValues = 0;

		for (KeyUsage usage : usages) {
			usageValues |= usage.value();
		}
		this.usageBitString.setBits(this.usageBitString.getBits() | usageValues);
		return this;
	}

	/**
	 * Get the set usage flags.
	 *
	 * @return The set usage flags.
	 */
	public KeyUsage[] getUsages() {
		return getUsagesFromBitString(this.usageBitString.getBitBytes());
	}

	/**
	 * Create extension instance from encoded data.
	 *
	 * @param critical The extension's critical flag.
	 * @param decoder The decoder providing access to the encoded data.
	 * @return The decoded instance.
	 * @throws IOException if the decoding fails.
	 */
	public static X509KeyUsageExtension asn1Decode(boolean critical, ASN1Decoder decoder) throws IOException {
		return new X509KeyUsageExtension(critical, getUsagesFromBitString(decoder.asn1EncodeBitString()));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Encodable#asn1Encode(de.carne.certmgr.store.asn1.ASN1Encoder)
	 */
	@Override
	public void asn1Encode(ASN1Encoder encoder) {
		this.usageBitString.asn1Encode(encoder);
	}

	private static KeyUsage[] getUsagesFromBitString(byte[] bs) {
		ArrayList<KeyUsage> usages = new ArrayList<>();

		if (bs.length > 0) {
			boolean anyUsage = true;

			for (byte b : bs) {
				if (b != -1) {
					anyUsage = false;
					break;
				}
			}
			if (anyUsage) {
				usages.add(KeyUsage.ANY);
			} else {
				int usageValue = 1;

				for (byte b : bs) {
					byte usageBit = 1;

					for (int usageBitsIndex = 0; usageBitsIndex < 8; usageBitsIndex++) {
						if ((b & usageBit) == usageBit) {
							usages.add(KeyUsage.valueOf(usageValue));
						}
						usageBit <<= 1;
						usageValue <<= 1;
					}
				}
			}
		}
		return usages.toArray(new KeyUsage[usages.size()]);
	}

}
