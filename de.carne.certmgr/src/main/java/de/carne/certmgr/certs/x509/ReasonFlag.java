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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * X.509 CRL Revoke Reason flags.
 */
public class ReasonFlag extends Enumeration<Integer> {

	private static final Map<Integer, ReasonFlag> instanceMap = new HashMap<>();

	/**
	 * UNSPECIFIED
	 */
	public static final ReasonFlag UNSPECIFIED = new ReasonFlag("UNSPECIFIED", 0);

	/**
	 * PRIVILEGE_WITHDRAWN
	 */
	public static final ReasonFlag PRIVILEGE_WITHDRAWN = new ReasonFlag("PRIVILEGE_WITHDRAWN", 1 << 0);

	/**
	 * CERTIFICATE_HOLD
	 */
	public static final ReasonFlag CERTIFICATE_HOLD = new ReasonFlag("CERTIFICATE_HOLD", 1 << 1);

	/**
	 * CESSATION_OF_OPERATION
	 */
	public static final ReasonFlag CESSATION_OF_OPERATION = new ReasonFlag("CESSATION_OF_OPERATION", 1 << 2);

	/**
	 * SUPERSEDED
	 */
	public static final ReasonFlag SUPERSEDED = new ReasonFlag("SUPERSEDED", 1 << 3);

	/**
	 * AFFILIATION_CHANGED
	 */
	public static final ReasonFlag AFFILIATION_CHANGED = new ReasonFlag("AFFILIATION_CHANGED", 1 << 4);

	/**
	 * CA_COMPROMISE
	 */
	public static final ReasonFlag CA_COMPROMISE = new ReasonFlag("CA_COMPROMISE", 1 << 5);

	/**
	 * KEY_COMPROMISE
	 */
	public static final ReasonFlag KEY_COMPROMISE = new ReasonFlag("KEY_COMPROMISE", 1 << 6);

	/**
	 * UNUSED
	 */
	public static final ReasonFlag UNUSED = new ReasonFlag("UNUSED", 1 << 7);

	/**
	 * REMOVE_FROM_CRL
	 */
	public static final ReasonFlag REMOVE_FROM_CRL = new ReasonFlag("REMOVE_FROM_CRL", 1 << 8);

	/**
	 * AA_COMPROMISE
	 */
	public static final ReasonFlag AA_COMPROMISE = new ReasonFlag("AA_COMPROMISE", 1 << 15);

	private ReasonFlag(String name, Integer value) {
		super(name, value);
		registerInstance(this);
	}

	private static synchronized void registerInstance(ReasonFlag usage) {
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
	public static synchronized Set<ReasonFlag> instances() {
		return new HashSet<>(instanceMap.values());
	}

	/**
	 * Get the key usage instance for a specific value.
	 *
	 * @param value The value to get the instance for.
	 * @return The key usage instance corresponding to the submitted value.
	 */
	public static synchronized ReasonFlag fromValue(int value) {
		ReasonFlag usage = instanceMap.get(value);

		if (usage == null) {
			usage = new ReasonFlag(String.format("0x%08x", value), value);
		}
		return usage;
	}

}
