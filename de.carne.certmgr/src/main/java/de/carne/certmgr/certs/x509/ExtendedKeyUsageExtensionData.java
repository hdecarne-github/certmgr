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
import java.util.Set;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;

/**
 * X.509 <a href="https://tools.ietf.org/html/rfc5280#section-4.2.1.12">Extended
 * Key Usage Extension</a> data.
 */
public class ExtendedKeyUsageExtensionData extends X509ExtensionData {

	/**
	 * Extension OID.
	 */
	public static final String OID = "2.5.29.37";

	private final Set<ExtendedKeyUsage> usages;

	/**
	 * Construct {@code ExtendedKeyUsageExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 */
	public ExtendedKeyUsageExtensionData(boolean critical) {
		super(OID, critical);
		this.usages = new HashSet<>();
	}

	/**
	 * Construct {@code ExtendedKeyUsageExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 * @param usages The extension's usages.
	 */
	public ExtendedKeyUsageExtensionData(boolean critical, Set<ExtendedKeyUsage> usages) {
		super(OID, critical);

		assert usages != null;

		this.usages = new HashSet<>(usages);
	}

	/**
	 * Decode {@code ExtendedKeyUsageExtensionData} from an ASN.1 data object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @param critical The extension's critical flag.
	 * @return The decoded extension data.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static ExtendedKeyUsageExtensionData decode(ASN1Primitive primitive, boolean critical) throws IOException {
		ASN1Primitive[] sequence = decodeSequence(primitive, 0, Integer.MAX_VALUE);
		Set<ExtendedKeyUsage> usages = new HashSet<>();

		for (ASN1Primitive sequenceEntry : sequence) {
			usages.add(ExtendedKeyUsage.fromValue(decodePrimitive(sequenceEntry, ASN1ObjectIdentifier.class).getId()));
		}
		return new ExtendedKeyUsageExtensionData(critical, usages);
	}

	/**
	 * Add a usage flag to this extension.
	 *
	 * @param usage The usage flag to add.
	 */
	public void addUsage(ExtendedKeyUsage usage) {
		this.usages.add(usage);
	}

	/**
	 * Check whether a given usage flag is set for this extension.
	 *
	 * @param usage The usage flag to check.
	 * @return {@code true} if the usage is set.
	 */
	public boolean hasUsage(ExtendedKeyUsage usage) {
		return this.usages.contains(usage);
	}

	@Override
	public Attributes toAttributes() {
		Attributes extensionAttributes = super.toAttributes();

		for (ExtendedKeyUsage usage : this.usages) {
			extensionAttributes.addChild(usage.name(), null);
		}
		return extensionAttributes;
	}

}
