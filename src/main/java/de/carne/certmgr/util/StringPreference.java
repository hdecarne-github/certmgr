/*
 * Copyright (c) 2015-2019 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.util;

import java.util.prefs.Preferences;

/**
 * Utility class providing access to a {@link String} preference.
 */
public class StringPreference extends Preference<String> {

	/**
	 * Construct {@code StringPreference}.
	 *
	 * @param preferences The {@link Preferences} object storing this preference.
	 * @param key The preference key.
	 */
	public StringPreference(Preferences preferences, String key) {
		super(preferences, key);
	}

	@Override
	protected String toValue(String valueString) {
		return valueString;
	}

	@Override
	protected String fromValue(String value) {
		return value;
	}

}
