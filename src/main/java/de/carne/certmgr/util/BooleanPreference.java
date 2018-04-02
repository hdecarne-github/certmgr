/*
 * Copyright (c) 2016-2017 Holger de Carne and contributors, All Rights Reserved.
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
 * Utility class providing access to a {@link Boolean} preference.
 */
public class BooleanPreference extends Preference<Boolean> {

	/**
	 * Construct {@code BooleanPreference}.
	 *
	 * @param preferences The {@link Preferences} object storing this preference.
	 * @param key The preference key.
	 */
	public BooleanPreference(Preferences preferences, String key) {
		super(preferences, key);
	}

	/**
	 * Get the preference value.
	 *
	 * @param defaultValue The default preference value to return in case the preference is undefined.
	 * @return The preference value.
	 */
	public boolean getBoolean(boolean defaultValue) {
		return preferences().getBoolean(key(), defaultValue);
	}

	/**
	 * Set the preference value.
	 *
	 * @param value The value to set.
	 */
	public void putBoolean(boolean value) {
		preferences().putBoolean(key(), value);
	}

	@Override
	protected Boolean toValue(String valueString) {
		return Boolean.valueOf(valueString);
	}

	@Override
	protected String fromValue(Boolean value) {
		return value.toString();
	}

}
