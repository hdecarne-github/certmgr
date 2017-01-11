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
 * X.509 General Name types.
 */
public class GeneralNameType extends Enumeration<Integer> {

	private static final Map<Integer, GeneralNameType> instanceMap = new HashMap<>();

	/**
	 * OTHER_NAME
	 */
	public static final int OTHER_NAME_TAG = 0;

	/**
	 * OTHER_NAME
	 */
	public static final GeneralNameType OTHER_NAME = new GeneralNameType("OTHER", OTHER_NAME_TAG);

	/**
	 * RFC822_NAME
	 */
	public static final int RFC822_NAME_TAG = 1;

	/**
	 * RFC822_NAME
	 */
	public static final GeneralNameType RFC822_NAME = new GeneralNameType("RFC822", RFC822_NAME_TAG);

	/**
	 * DNS_NAME
	 */
	public static final int DNS_NAME_TAG = 2;

	/**
	 * DNS_NAME
	 */
	public static final GeneralNameType DNS_NAME = new GeneralNameType("DNS", DNS_NAME_TAG);

	/**
	 * X400_ADDRESS
	 */
	public static final int X400_ADDRESS_TAG = 3;

	/**
	 * X400_ADDRESS
	 */
	public static final GeneralNameType X400_ADDRESS = new GeneralNameType("X400", X400_ADDRESS_TAG);

	/**
	 * DIRECTORY_NAME
	 */
	public static final int DIRECTORY_NAME_TAG = 4;

	/**
	 * DIRECTORY_NAME
	 */
	public static final GeneralNameType DIRECTORY_NAME = new GeneralNameType("DIRECTORY", DIRECTORY_NAME_TAG);

	/**
	 * EDI_PARTY_NAME
	 */
	public static final int EDI_PARTY_NAME_TAG = 5;

	/**
	 * EDI_PARTY_NAME
	 */
	public static final GeneralNameType EDI_PARTY_NAME = new GeneralNameType("EDI", EDI_PARTY_NAME_TAG);

	/**
	 * UNIFORM_RESOURCE_IDENTIFIER
	 */
	public static final int UNIFORM_RESOURCE_IDENTIFIER_TAG = 6;

	/**
	 * UNIFORM_RESOURCE_IDENTIFIER
	 */
	public static final GeneralNameType UNIFORM_RESOURCE_IDENTIFIER = new GeneralNameType("URI",
			UNIFORM_RESOURCE_IDENTIFIER_TAG);

	/**
	 * IP_ADDRESS
	 */
	public static final int IP_ADDRESS_TAG = 7;

	/**
	 * IP_ADDRESS
	 */
	public static final GeneralNameType IP_ADDRESS = new GeneralNameType("IP", IP_ADDRESS_TAG);

	/**
	 * REGISTERED_ID
	 */
	public static final int REGISTERED_ID_TAG = 8;

	/**
	 * REGISTERED_ID
	 */
	public static final GeneralNameType REGISTERED_ID = new GeneralNameType("OID", REGISTERED_ID_TAG);

	private GeneralNameType(String name, Integer value) {
		super(name, value);
		registerInstance(this);
	}

	private static synchronized void registerInstance(GeneralNameType usage) {
		instanceMap.put(usage.value(), usage);
	}

	/**
	 * Get the known general type name instances.
	 * <p>
	 * This includes the statically defined ones in this class as well as any
	 * new ones encountered in a call to {@linkplain #fromValue(int)}.
	 *
	 * @return The known general name type instances.
	 */
	public static synchronized Set<GeneralNameType> instances() {
		return new HashSet<>(instanceMap.values());
	}

	/**
	 * Get the general name type instance for a specific value.
	 *
	 * @param value The value to get the instance for.
	 * @return The general name type instance corresponding to the submitted
	 *         value.
	 */
	public static synchronized GeneralNameType fromValue(int value) {
		GeneralNameType usage = instanceMap.get(value);

		if (usage == null) {
			usage = new GeneralNameType(String.format("0x%02x", value), value);
		}
		return usage;
	}

}
