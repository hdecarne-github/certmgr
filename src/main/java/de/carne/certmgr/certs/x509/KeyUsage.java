/*
 * Copyright (c) 2015-2017 Holger de Carne and contributors, All Rights Reserved.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * X.509 Key Usage flags.
 */
public class KeyUsage extends Enumeration<Integer> {

	private static final Map<Integer, KeyUsage> instanceMap = new HashMap<>();

	/**
	 * ENCIPHER_ONLY
	 */
	public static final KeyUsage ENCIPHER_ONLY = new KeyUsage("ENCIPHER_ONLY", 1 << 0);

	/**
	 * CRL_SIGN
	 */
	public static final KeyUsage CRL_SIGN = new KeyUsage("CRL_SIGN", 1 << 1);

	/**
	 * KEY_CERT_SIGN
	 */
	public static final KeyUsage KEY_CERT_SIGN = new KeyUsage("KEY_CERT_SIGN", 1 << 2);

	/**
	 * KEY_AGREEMENT
	 */
	public static final KeyUsage KEY_AGREEMENT = new KeyUsage("KEY_AGREEMENT", 1 << 3);

	/**
	 * DATA_ENCIPHERMENT
	 */
	public static final KeyUsage DATA_ENCIPHERMENT = new KeyUsage("DATA_ENCIPHERMENT", 1 << 4);

	/**
	 * KEY_ENCIPHERMENT
	 */
	public static final KeyUsage KEY_ENCIPHERMENT = new KeyUsage("KEY_ENCIPHERMENT", 1 << 5);

	/**
	 * NON_REPUDIATION
	 */
	public static final KeyUsage NON_REPUDIATION = new KeyUsage("NON_REPUDIATION", 1 << 6);

	/**
	 * DIGITAL_SIGNATURE
	 */
	public static final KeyUsage DIGITAL_SIGNATURE = new KeyUsage("DIGITAL_SIGNATURE", 1 << 7);

	/**
	 * DECIPHER_ONLY
	 */
	public static final KeyUsage DECIPHER_ONLY = new KeyUsage("DECIPHER_ONLY", 1 << 15);

	/**
	 * ANY
	 */
	public static final KeyUsage ANY = new KeyUsage("ANY", -1 >>> 16);

	private KeyUsage(String name, Integer value) {
		super(name, value);
		registerInstance(this);
	}

	private static synchronized void registerInstance(KeyUsage usage) {
		instanceMap.put(usage.value(), usage);
	}

	/**
	 * Get the known key usage instances.
	 * <p>
	 * This includes the statically defined ones in this class as well as any
	 * new ones encountered in a call to {@linkplain #fromValue(int)}.
	 *
	 * @return The known key usage instances.
	 */
	public static synchronized Set<KeyUsage> instances() {
		return new HashSet<>(instanceMap.values());
	}

	/**
	 * Get the key usage instance for a specific value.
	 *
	 * @param value The value to get the instance for.
	 * @return The key usage instance corresponding to the submitted value.
	 */
	public static synchronized KeyUsage fromValue(int value) {
		KeyUsage usage = instanceMap.get(value);

		if (usage == null) {
			usage = new KeyUsage(String.format("0x%08x", value), value);
		}
		return usage;
	}

}
