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

import de.carne.boot.check.Nullable;

/**
 * Generic base class for preference access.
 *
 * @param <T> The preference type.
 */
public abstract class Preference<T> {

	private final Preferences preferences;

	private final String key;

	/**
	 * Construct {@code Preference}.
	 *
	 * @param preferences The {@link Preferences} object storing this preference.
	 * @param key The preference key.
	 */
	protected Preference(Preferences preferences, String key) {
		this.preferences = preferences;
		this.key = key;
	}

	/**
	 * Get the {@code Preferences} object storing this preference.
	 *
	 * @return The {@code Preferences} object storing this preference.
	 */
	public Preferences preferences() {
		return this.preferences;
	}

	/**
	 * Get the preference key.
	 *
	 * @return The preference key.
	 */
	public String key() {
		return this.key;
	}

	/**
	 * Get the preference value.
	 *
	 * @return The found preference value, or {@code null} if the preference is undefined.
	 */
	@Nullable
	public T get() {
		String valueString = this.preferences.get(this.key, null);

		return (valueString != null ? toValue(valueString) : null);
	}

	/**
	 * Get the preference value.
	 *
	 * @param defaultValue The default preference value to return in case the preference is undefined.
	 * @return The found preference value.
	 */
	public T get(T defaultValue) {
		String valueString = this.preferences.get(this.key, null);
		T value = null;

		if (valueString != null) {
			value = toValue(valueString);
		}
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	/**
	 * Set the preference value.
	 *
	 * @param value The value to set. If {@code null} the preference is removed.
	 */
	public void put(@Nullable T value) {
		if (value != null) {
			this.preferences.put(this.key, fromValue(value));
		} else {
			remove();
		}
	}

	/**
	 * Remove the preference value.
	 */
	public void remove() {
		this.preferences.remove(this.key);
	}

	/**
	 * Convert the preference's string representation to it's value.
	 *
	 * @param valueString The string to convert.
	 * @return The converted preference value.
	 */
	@Nullable
	protected abstract T toValue(String valueString);

	/**
	 * Convert the preference value to it's string representation.
	 *
	 * @param value The value to convert.
	 * @return The converted preference value.
	 */
	protected abstract String fromValue(T value);

	@Override
	public String toString() {
		return this.key + " = " + get();
	}

}
