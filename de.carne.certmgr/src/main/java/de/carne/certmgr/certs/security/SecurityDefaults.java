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
package de.carne.certmgr.certs.security;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import de.carne.util.PropertiesHelper;

class SecurityDefaults {

	private SecurityDefaults() {
		// Make sure this class is not instantiated from outside
	}

	private static final Properties DEFAULTS = PropertiesHelper.init(SecurityDefaults.class);

	private static final String KEY_KEY_ALGORITHM = "keyAlgorithm";
	private static final String KEY_KEY_SIZE = ".keySize";

	public static String getDefaultKeyAlgorithmName() {
		return getDefaultName(KEY_KEY_ALGORITHM);
	}

	public static Set<String> getKeyAlgorithmNames() {
		return getNames(KEY_KEY_ALGORITHM);
	}

	public static Set<Integer> getKeySizes(String algorithm) {
		Set<String> sizeStrings = getNames(algorithm + KEY_KEY_SIZE);
		Set<Integer> sizes = new HashSet<>(sizeStrings.size());

		for (String sizeString : sizeStrings) {
			sizes.add(Integer.valueOf(sizeString));
		}
		return sizes;
	}

	private static String getDefaultName(String key) {
		String defaultName = DEFAULTS.getProperty(key);

		if (defaultName == null) {
			throw new IllegalArgumentException("Unknown key: " + key);
		}
		return defaultName;
	}

	private static Set<String> getNames(String key) {
		String name;
		int keyNumber = 1;
		Set<String> names = new HashSet<>();

		while ((name = DEFAULTS.getProperty(key + "." + keyNumber)) != null) {
			names.add(name);
			keyNumber++;
		}
		return names;
	}

}
