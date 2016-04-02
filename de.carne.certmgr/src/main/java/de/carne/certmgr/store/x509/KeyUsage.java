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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;

/**
 * X.509 Extended Key Usage values.
 */
public final class KeyUsage {

	private static final Hashtable<Integer, KeyUsage> VALUES = new Hashtable<>();

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

	private final String name;
	private final Integer value;

	private KeyUsage(String name, int value) {
		assert name != null;

		this.name = name;
		this.value = value;
		VALUES.put(this.value, this);
	}

	/**
	 * Get the key usage name.
	 *
	 * @return The key usage name.
	 */
	public String name() {
		return this.name;
	}

	/**
	 * Get the key usage value.
	 *
	 * @return The key usage value.
	 */
	public int value() {
		return this.value;
	}

	/**
	 * Get the known values.
	 *
	 * @param includeAny Whether to include the AYN value in the result or not.
	 * @return The known values.
	 */
	public static KeyUsage[] values(boolean includeAny) {
		KeyUsage[] values;

		if (includeAny) {
			values = new KeyUsage[VALUES.size()];
			VALUES.values().toArray(values);
		} else {
			values = new KeyUsage[VALUES.size() - 1];

			int valueIndex = 0;

			for (KeyUsage value : VALUES.values()) {
				if (!ANY.equals(value)) {
					values[valueIndex] = value;
					valueIndex++;
				}
			}
		}
		return values;
	}

	/**
	 * Get the values in sorted order.
	 *
	 * @param includeAny Whether to include the AYN value in the result or not.
	 * @return The values in sorted order.
	 */
	public static KeyUsage[] sortedValues(boolean includeAny) {
		KeyUsage[] unsorted = values(includeAny);
		KeyUsage[] sorted = Arrays.copyOf(unsorted, unsorted.length);

		Arrays.sort(sorted, new Comparator<KeyUsage>() {

			@Override
			public int compare(KeyUsage o1, KeyUsage o2) {
				return o1.name().compareTo(o2.name());
			}

		});
		return sorted;
	}

	/**
	 * Get key usage from name.
	 *
	 * @param name The name to get the key usage for.
	 * @return The found key usage or null if the name is unknown.
	 */
	public static KeyUsage fromName(String name) {
		KeyUsage found = null;

		for (KeyUsage value : VALUES.values()) {
			if (value.name().equals(name)) {
				found = value;
				break;
			}
		}
		return found;
	}

	/**
	 * Get key usage from value.
	 *
	 * @param value The value to get the key usage for.
	 * @return The corresponding key usage.
	 */
	public static KeyUsage valueOf(int value) {
		Integer valueObject = value;
		KeyUsage found = VALUES.get(valueObject);

		if (found == null) {
			found = new KeyUsage(Integer.toUnsignedString(value), value);
			VALUES.put(valueObject, found);
		}
		return found;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}

}
