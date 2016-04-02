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

/**
 * General name types.
 */
public enum GeneralNameType {

	/**
	 * OTHER_NAME
	 */
	OTHER_NAME(0),

	/**
	 * RFC822_NAME
	 */
	RFC822_NAME(1),

	/**
	 * DNS_NAME
	 */
	DNS_NAME(2),

	/**
	 * X400_ADDRESS
	 */
	X400_ADDRESS(3),

	/**
	 * DIRECTORY_NAME
	 */
	DIRECTORY_NAME(4),

	/**
	 * EDI_PARTY_NAME
	 */
	EDI_PARTY_NAME(5),

	/**
	 * UNIFORM_RESOURCE_IDENTIFIER
	 */
	UNIFORM_RESOURCE_IDENTIFIER(6),

	/**
	 * IP_ADDRESS
	 */
	IP_ADDRESS(7),

	/**
	 * REGISTERED_ID
	 */
	REGISTERED_ID(8);

	private final int tagNo;

	private GeneralNameType(int tagNo) {
		this.tagNo = tagNo;
	}

	/**
	 * Get the enum's tag number.
	 *
	 * @return The enum's tag number.
	 */
	public int getTagNo() {
		return this.tagNo;
	}

	/**
	 * Get the known tag numbers.
	 * 
	 * @return The known tag numbers.
	 */
	public static int[] getTagNoValues() {
		GeneralNameType[] values = values();
		int tagNoValues[] = new int[values.length];
		int tagNoValueIndex = 0;

		for (GeneralNameType value : values) {
			tagNoValues[tagNoValueIndex] = value.getTagNo();
			tagNoValueIndex++;
		}
		return tagNoValues;
	}

	/**
	 * Map tag number to enum.
	 *
	 * @param tagNo The tag number to map.
	 * @return The found enum or null if the tag number is unknown.
	 */
	public static GeneralNameType fromTagNo(int tagNo) {
		GeneralNameType found = null;

		for (GeneralNameType value : values()) {
			if (value.tagNo == tagNo) {
				found = value;
				break;
			}
		}
		return found;
	}

	/**
	 * Get the values in sorted order.
	 *
	 * @return The values in sorted order.
	 */
	public static GeneralNameType[] sortedValues() {
		GeneralNameType[] unsorted = values();
		GeneralNameType[] sorted = Arrays.copyOf(unsorted, unsorted.length);

		Arrays.sort(sorted, new Comparator<GeneralNameType>() {

			@Override
			public int compare(GeneralNameType o1, GeneralNameType o2) {
				return o1.name().compareTo(o2.name());
			}

		});
		return sorted;
	}

}
