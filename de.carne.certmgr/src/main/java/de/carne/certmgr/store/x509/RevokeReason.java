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
 * X.509 CRL Revoke Reason values.
 */
public final class RevokeReason {

	private static final Hashtable<Integer, RevokeReason> VALUES = new Hashtable<>();

	/**
	 * UNSPECIFIED
	 */
	public static final RevokeReason UNSPECIFIED = new RevokeReason("UNSPECIFIED", 0);

	/**
	 * KEY_COMPROMISE
	 */
	public static final RevokeReason KEY_COMPROMISE = new RevokeReason("KEY_COMPROMISE", 1);

	/**
	 * CA_COMPROMISE
	 */
	public static final RevokeReason CA_COMPROMISE = new RevokeReason("CA_COMPROMISE", 2);

	/**
	 * AFFILIATION_CHANGED
	 */
	public static final RevokeReason AFFILIATION_CHANGED = new RevokeReason("AFFILIATION_CHANGED", 3);

	/**
	 * SUPERSEDED
	 */
	public static final RevokeReason SUPERSEDED = new RevokeReason("SUPERSEDED", 4);

	/**
	 * CESSATION_OF_OPERATION
	 */
	public static final RevokeReason CESSATION_OF_OPERATION = new RevokeReason("CESSATION_OF_OPERATION", 5);

	/**
	 * CERTIFICATE_HOLD
	 */
	public static final RevokeReason CERTIFICATE_HOLD = new RevokeReason("CERTIFICATE_HOLD", 6);

	/**
	 * UNUSED
	 */
	public static final RevokeReason UNUSED = new RevokeReason("UNUSED", 7);

	/**
	 * REMOVE_FROM_CRL
	 */
	public static final RevokeReason REMOVE_FROM_CRL = new RevokeReason("REMOVE_FROM_CRL", 8);

	/**
	 * PRIVILEGE_WITHDRAWN
	 */
	public static final RevokeReason PRIVILEGE_WITHDRAWN = new RevokeReason("PRIVILEGE_WITHDRAWN", 9);

	/**
	 * AA_COMPROMISE
	 */
	public static final RevokeReason AA_COMPROMISE = new RevokeReason("AA_COMPROMISE", 10);

	private final String name;
	private final Integer value;

	private RevokeReason(String name, int value) {
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
	 * @return The known values.
	 */
	public static RevokeReason[] values() {
		return VALUES.values().toArray(new RevokeReason[VALUES.size()]);
	}

	/**
	 * Get the values in sorted order.
	 *
	 * @return The values in sorted order.
	 */
	public static RevokeReason[] sortedValues() {
		RevokeReason[] unsorted = values();
		RevokeReason[] sorted = Arrays.copyOf(unsorted, unsorted.length);

		Arrays.sort(sorted, new Comparator<RevokeReason>() {

			@Override
			public int compare(RevokeReason o1, RevokeReason o2) {
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
	public static RevokeReason fromName(String name) {
		RevokeReason found = null;

		for (RevokeReason value : VALUES.values()) {
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
	public static RevokeReason valueOf(int value) {
		Integer valueObject = value;
		RevokeReason found = VALUES.get(valueObject);

		if (found == null) {
			found = new RevokeReason(Integer.toUnsignedString(value), value);
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
