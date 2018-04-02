/*
 * Copyright (c) 2015-2018 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.jfx.store;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import de.carne.certmgr.util.BooleanPreference;

/**
 * Utility class providing access to store UI preferences.
 */
public final class UserPreferences {

	private final Preferences preferences = Preferences.userNodeForPackage(UserPreferences.class);

	/**
	 * Expert mode flag.
	 */
	public final BooleanPreference expertMode = new BooleanPreference(this.preferences, "expertMode");

	UserPreferences() {
		// Make sure this class is not instantiated from outside this package
	}

	/**
	 * Sync the preferences to the backing store.
	 *
	 * @throws BackingStoreException if an error occurs during syncing.
	 */
	public void sync() throws BackingStoreException {
		this.preferences.sync();
	}

}
