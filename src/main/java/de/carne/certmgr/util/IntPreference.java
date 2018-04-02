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

import de.carne.check.Nullable;
import de.carne.util.Exceptions;

/**
 * Utility class providing access to a {@link Integer} preference.
 */
public class IntPreference extends Preference<Integer> {

	/**
	 * Construct {@code IntPreference}.
	 *
	 * @param preferences The {@link Preferences} object storing this preference.
	 * @param key The preference key.
	 */
	public IntPreference(Preferences preferences, String key) {
		super(preferences, key);
	}

	/**
	 * Get the preference value.
	 *
	 * @param defaultValue The default preference value to return in case the preference is undefined.
	 * @return The preference value.
	 */
	public int getInt(int defaultValue) {
		return preferences().getInt(key(), defaultValue);
	}

	/**
	 * Set the preference value.
	 *
	 * @param value The value to set.
	 */
	public void putInt(int value) {
		preferences().putInt(key(), value);
	}

	@Override
	@Nullable
	protected Integer toValue(String valueString) {
		Integer value = null;

		try {
			value = Integer.valueOf(valueString);
		} catch (NumberFormatException e) {
			Exceptions.ignore(e);
		}
		return value;
	}

	@Override
	protected String fromValue(Integer value) {
		return value.toString();
	}

}
