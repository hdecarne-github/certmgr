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
package de.carne.util.prefs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * Property file based preferences implementation.
 */
public class PropertiesPreferencesFactory implements PreferencesFactory {

	private static final String PREFERENCES_DIR;

	static {
		String packageName = PropertiesPreferencesFactory.class.getPackage().getName();

		PREFERENCES_DIR = packageName.substring(0, packageName.length() - ".util.prefs".length());
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.prefs.PreferencesFactory#userRoot()
	 */
	@Override
	public Preferences systemRoot() {
		String userHome = System.getProperty("user.home", ".");
		String systemName;

		try {
			systemName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			systemName = "unknown";
		}

		Path propertiesPath = Paths.get(userHome, PREFERENCES_DIR, "system-" + systemName + ".properties");

		return new PropertiesPreferences(propertiesPath);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.prefs.PreferencesFactory#systemRoot()
	 */
	@Override
	public Preferences userRoot() {
		String userHome = System.getProperty("user.home", ".");
		Path propertiesPath = Paths.get(userHome, PREFERENCES_DIR, "user.properties");

		return new PropertiesPreferences(propertiesPath);
	}

	/**
	 * Create a Preferences object backed up by a given properties file.
	 *
	 * @param propertiesPath The properties file use for backing up the preferences.
	 * @return The created Preferences object.
	 */
	public static Preferences fromFile(Path propertiesPath) {
		return new PropertiesPreferences(propertiesPath);
	}

}
