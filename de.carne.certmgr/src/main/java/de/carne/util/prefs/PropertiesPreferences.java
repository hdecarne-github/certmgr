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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Properties;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

import de.carne.util.logging.Log;

/**
 * Property file based Preferences implementation.
 */
class PropertiesPreferences extends AbstractPreferences {

	private static Log LOG = new Log(PropertiesPreferences.class);

	private Path propertiesPath;
	private Properties properties;

	PropertiesPreferences(Path propertiesPath) {
		super(null, "");

		assert propertiesPath != null;

		this.propertiesPath = propertiesPath.toAbsolutePath();
		this.properties = new Properties();
		loadProperties();
	}

	private PropertiesPreferences(PropertiesPreferences parent, String name) {
		super(parent, name);
		this.propertiesPath = parent.propertiesPath;
		this.properties = parent.properties;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#putSpi(java.lang.String, java.lang.String)
	 */
	@Override
	protected void putSpi(String key, String value) {
		this.properties.put(absolutePath() + "/" + key, value);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#getSpi(java.lang.String)
	 */
	@Override
	protected String getSpi(String key) {
		return this.properties.getProperty(absolutePath() + "/" + key);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#removeSpi(java.lang.String)
	 */
	@Override
	protected void removeSpi(String key) {
		this.properties.remove(absolutePath() + "/" + key);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#removeNodeSpi()
	 */
	@Override
	protected void removeNodeSpi() throws BackingStoreException {
		String keyPrefix = absolutePath();

		for (Object keyObject : this.properties.keySet()) {
			String key = keyObject.toString();

			if (key.startsWith(keyPrefix)) {
				this.properties.remove(key);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#keysSpi()
	 */
	@Override
	protected String[] keysSpi() throws BackingStoreException {
		ArrayList<String> keys = new ArrayList<>(this.properties.size());
		String keyPrefix = absolutePath();

		for (Object keyObject : this.properties.keySet()) {
			String key = keyObject.toString();

			if (key.startsWith(keyPrefix) && key.indexOf("/", keyPrefix.length()) == -1) {
				keys.add(key.substring(keyPrefix.length()));
			}
		}
		return keys.toArray(new String[keys.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#childrenNamesSpi()
	 */
	@Override
	protected String[] childrenNamesSpi() throws BackingStoreException {
		ArrayList<String> childrenNames = new ArrayList<>(this.properties.size());
		String keyPrefix = absolutePath();

		for (Object keyObject : this.properties.keySet()) {
			String key = keyObject.toString();

			if (key.startsWith(keyPrefix) && key.indexOf("/", keyPrefix.length()) >= 0) {
				childrenNames.add(key.substring(keyPrefix.length()));
			}
		}
		return childrenNames.toArray(new String[childrenNames.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#childSpi(java.lang.String)
	 */
	@Override
	protected AbstractPreferences childSpi(String name) {
		return new PropertiesPreferences(this, name);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#sync()
	 */
	@Override
	public void sync() throws BackingStoreException {
		storeProperties();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#syncSpi()
	 */
	@Override
	protected void syncSpi() throws BackingStoreException {
		// Nothing to do here
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#flushSpi()
	 */
	@Override
	protected void flushSpi() throws BackingStoreException {
		// Nothing to do here
	}

	private void loadProperties() {
		if (Files.exists(this.propertiesPath)) {
			LOG.info(null, "Loading preferences from: ''{0}''", this.propertiesPath);
			try (InputStream propertiesStream = Files.newInputStream(this.propertiesPath, StandardOpenOption.READ)) {
				this.properties.load(propertiesStream);
			} catch (IOException e) {
				LOG.warning(e, null, e.getLocalizedMessage());
			}
		}
	}

	private void storeProperties() throws BackingStoreException {
		LOG.info(null, "Storing preferences to: ''{0}''", this.propertiesPath);
		try {
			Files.createDirectories(this.propertiesPath.getParent());
		} catch (IOException e) {
			throw new BackingStoreException(e);
		}
		try (OutputStream propertiesStream = Files.newOutputStream(this.propertiesPath, StandardOpenOption.CREATE,
				StandardOpenOption.WRITE)) {
			this.properties.store(propertiesStream, null);
		} catch (IOException e) {
			throw new BackingStoreException(e);
		}
	}

}
