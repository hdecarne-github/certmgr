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
package de.carne.certmgr.certs;

import java.nio.file.Path;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import de.carne.util.prefs.IntPreference;
import de.carne.util.prefs.PropertiesPreferencesFactory;
import de.carne.util.prefs.StringPreference;

/**
 * Utility class providing access to store preferences.
 */
public final class UserCertStorePreferences {

	private static final String PREFERENCES_FILENAME = ".preferences.properties";

	private static final String STORE_NODE = "store";

	private static final String STORE_DEFAULT_CRT_VALIDITY = "defcrtvalidity";

	private static final String STORE_DEFAULT_CRL_UPDATE_PERIOD = "defcrlupdate";

	private static final String STORE_DEFAULT_KEY_PAIR_ALGORITHM = "defkeyalg";

	private static final String STORE_DEFAULT_KEY_SIZE = "defkeysize";

	private static final String STORE_DEFAULT_SIGNATURE_ALGORITHM = "defsigalg";

	private final Preferences preferences;

	/**
	 * Default CRT Validity (in days).
	 */
	public final IntPreference defaultCRTValidity;

	/**
	 * Default CRL update period (in days).
	 */
	public final IntPreference defaultCRLUpdatePeriod;

	/**
	 * Default Key Pair algorithm.
	 */
	public final StringPreference defaultKeyPairAlgorithm;

	/**
	 * Default Key size.
	 */
	public final IntPreference defaultKeySize;

	/**
	 * Default Signature algorithm.
	 */
	public final StringPreference defaultSignatureAlgorithm;

	UserCertStorePreferences(Path storeHome) {
		this.preferences = PropertiesPreferencesFactory.customRoot(storeHome.resolve(PREFERENCES_FILENAME));

		Preferences optionsNode = this.preferences.node(STORE_NODE);

		this.defaultCRTValidity = new IntPreference(optionsNode, STORE_DEFAULT_CRT_VALIDITY);
		this.defaultCRLUpdatePeriod = new IntPreference(optionsNode, STORE_DEFAULT_CRL_UPDATE_PERIOD);
		this.defaultKeyPairAlgorithm = new StringPreference(optionsNode, STORE_DEFAULT_KEY_PAIR_ALGORITHM);
		this.defaultKeySize = new IntPreference(optionsNode, STORE_DEFAULT_KEY_SIZE);
		this.defaultSignatureAlgorithm = new StringPreference(optionsNode, STORE_DEFAULT_SIGNATURE_ALGORITHM);
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
