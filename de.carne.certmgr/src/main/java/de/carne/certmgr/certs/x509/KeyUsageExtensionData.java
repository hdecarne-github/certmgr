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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Primitive;

import de.carne.util.Strings;

/**
 * X.509 <a href="https://tools.ietf.org/html/rfc5280#section-4.2.1.3">Key Usage
 * Extension</a> data.
 */
public class KeyUsageExtensionData extends X509ExtensionData implements Iterable<KeyUsage> {

	/**
	 * Extension OID.
	 */
	public static final String OID = "2.5.29.15";

	/**
	 * The default to use for this extension's critical flag.
	 */
	public static final boolean CRITICAL_DEFAULT = true;

	private final Set<KeyUsage> usages;

	/**
	 * Construct {@code KeyUsageExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 */
	public KeyUsageExtensionData(boolean critical) {
		super(OID, critical);
		this.usages = new HashSet<>();
	}

	/**
	 * Construct {@code KeyUsageExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 * @param usages The extension's usages.
	 */
	public KeyUsageExtensionData(boolean critical, Set<KeyUsage> usages) {
		super(OID, critical);

		assert usages != null;

		this.usages = new HashSet<>(usages);
	}

	/**
	 * Decode {@code KeyUsageExtensionData} from an ASN.1 data object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @param critical The extension's critical flag.
	 * @return The decoded extension data.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static KeyUsageExtensionData decode(ASN1Primitive primitive, boolean critical) throws IOException {
		byte[] bitBytes = decodePrimitive(primitive, ASN1BitString.class).getBytes();
		Set<KeyUsage> usages = new HashSet<>();

		if (bitBytes.length != 0) {
			boolean anyUsage = true;

			for (byte bitByte : bitBytes) {
				anyUsage = anyUsage && bitByte == -1;
			}
			if (anyUsage) {
				usages.add(KeyUsage.ANY);
			} else {
				int usageValue = 1;

				for (byte bitByte : bitBytes) {
					anyUsage = anyUsage && bitByte == -1;
					for (int bit = 1; bit < 256; bit <<= 1) {
						if ((bitByte & bit) == bit) {
							usages.add(KeyUsage.fromValue(usageValue));
						}
						usageValue <<= 1;
					}
				}
			}
		}
		return new KeyUsageExtensionData(critical, usages);
	}

	/**
	 * Add a usage flag to this extension.
	 *
	 * @param usage The usage flag to add.
	 */
	public void addUsage(KeyUsage usage) {
		this.usages.add(usage);
	}

	/**
	 * Check whether a given usage flag is set for this extension.
	 *
	 * @param usage The usage flag to check.
	 * @return {@code true} if the usage is set.
	 */
	public boolean hasUsage(KeyUsage usage) {
		return this.usages.contains(usage);
	}

	@Override
	public Iterator<KeyUsage> iterator() {
		return this.usages.iterator();
	}

	@Override
	public String toValueString() {
		return Strings.join(this.usages, ", ");
	}

	@Override
	public Attributes toAttributes() {
		Attributes extensionAttributes = super.toAttributes();

		for (KeyUsage usage : this.usages) {
			extensionAttributes.add(usage.name(), null);
		}
		return extensionAttributes;
	}

}
