/*
 * Copyright (c) 2015-2021 Holger de Carne and contributors, All Rights Reserved.
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

import java.security.cert.CRLReason;
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
	 * KEY_COMPROMISE
	 */
	public static final ReasonFlag KEY_COMPROMISE = new ReasonFlag("KEY_COMPROMISE", 1);

	/**
	 * CA_COMPROMISE
	 */
	public static final ReasonFlag CA_COMPROMISE = new ReasonFlag("CA_COMPROMISE", 2);

	/**
	 * AFFILIATION_CHANGED
	 */
	public static final ReasonFlag AFFILIATION_CHANGED = new ReasonFlag("AFFILIATION_CHANGED", 3);

	/**
	 * SUPERSEDED
	 */
	public static final ReasonFlag SUPERSEDED = new ReasonFlag("SUPERSEDED", 4);

	/**
	 * CESSATION_OF_OPERATION
	 */
	public static final ReasonFlag CESSATION_OF_OPERATION = new ReasonFlag("CESSATION_OF_OPERATION", 5);

	/**
	 * CERTIFICATE_HOLD
	 */
	public static final ReasonFlag CERTIFICATE_HOLD = new ReasonFlag("CERTIFICATE_HOLD", 6);

	/**
	 * UNUSED
	 */
	public static final ReasonFlag UNUSED = new ReasonFlag("UNUSED", 7);

	/**
	 * REMOVE_FROM_CRL
	 */
	public static final ReasonFlag REMOVE_FROM_CRL = new ReasonFlag("REMOVE_FROM_CRL", 8);

	/**
	 * PRIVILEGE_WITHDRAWN
	 */
	public static final ReasonFlag PRIVILEGE_WITHDRAWN = new ReasonFlag("PRIVILEGE_WITHDRAWN", 9);

	/**
	 * AA_COMPROMISE
	 */
	public static final ReasonFlag AA_COMPROMISE = new ReasonFlag("AA_COMPROMISE", 10);

	private ReasonFlag(String name, Integer value) {
		super(name, value);
		registerInstance(this);
	}

	private static synchronized void registerInstance(ReasonFlag usage) {
		instanceMap.put(usage.value(), usage);
	}

	/**
	 * Get the known reason flag instances.
	 * <p>
	 * This includes the statically defined ones in this class as well as any new ones encountered in a call to
	 * {@linkplain #fromValue(int)}.
	 *
	 * @return The known reason flag instances.
	 */
	public static synchronized Set<ReasonFlag> instances() {
		return new HashSet<>(instanceMap.values());
	}

	/**
	 * Get the reason flag instance for a specific value.
	 *
	 * @param value The value to get the instance for.
	 * @return The reason flag instance corresponding to the submitted value.
	 */
	public static synchronized ReasonFlag fromValue(int value) {
		ReasonFlag usage = instanceMap.get(value);

		if (usage == null) {
			usage = new ReasonFlag(String.format("0x%08x", value), value);
		}
		return usage;
	}

	/**
	 * Get the reason flag instance for a specific {@link CRLReason}.
	 *
	 * @param reason The {@link CRLReason} to get the instance for.
	 * @return The reason flag instance corresponding to the submitted {@link CRLReason}.
	 */
	public static synchronized ReasonFlag fromCRLReason(CRLReason reason) {
		ReasonFlag reasonFlag = null;

		for (ReasonFlag instance : instanceMap.values()) {
			if (instance.name().equals(reason.name())) {
				reasonFlag = instance;
				break;
			}
		}
		if (reasonFlag == null) {
			throw new IllegalArgumentException("Unexpected CRL reason: " + reason);
		}
		return reasonFlag;
	}

}
