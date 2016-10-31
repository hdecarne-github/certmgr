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

import org.bouncycastle.asn1.ASN1Primitive;

/**
 * X.509 <a href="https://tools.ietf.org/html/rfc5280#page-29">Key Usage
 * Extension</a> data.
 */
public class KeyUsageExtensionData extends X509ExtensionData {

	/**
	 * Extension OID.
	 */
	public static final String OID = "2.5.29.15";

	private final Set<KeyUsage> usages = new HashSet<>();

	/**
	 * Construct {@code KeyUsageExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 */
	public KeyUsageExtensionData(boolean critical) {
		super(OID, critical);
	}

	public static KeyUsageExtensionData decode(ASN1Primitive primitive, boolean critical) throws IOException {
		return new KeyUsageExtensionData(critical);
	}

}
