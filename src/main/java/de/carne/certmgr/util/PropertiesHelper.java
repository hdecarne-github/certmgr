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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.carne.boot.logging.Log;
import de.carne.boot.check.Nullable;
import de.carne.boot.Exceptions;

/**
 * Utility class providing {@link Properties} related functions.
 */
public final class PropertiesHelper {

	private PropertiesHelper() {
		// Make sure this class is not instantiated from outside
	}

	private static final Log LOG = new Log();

	/**
	 * Initialize a {@link Properties} object for a specific class.
	 * <p>
	 * The function is identical to {@link #load(Class)} except for that it generates a {@link RuntimeException} if the
	 * load operation fails.
	 *
	 * @param cls The class to load the properties for.
	 * @return The loaded properties.
	 */
	public static Properties init(Class<?> cls) {
		Properties properties;

		try {
			properties = load(cls);
		} catch (IOException e) {
			throw Exceptions.toRuntime(e);
		}
		return properties;
	}

	/**
	 * Load a {@link Properties} object for a specific class.
	 * <p>
	 * This function assumes that the properties file is a resource named as the submitted class with the extension
	 * .properties.
	 *
	 * @param cls The class to load the properties for.
	 * @return The loaded properties.
	 * @throws IOException if an I/O error occurs during loading.
	 */
	public static Properties load(Class<?> cls) throws IOException {
		Properties properties;

		try (InputStream stream = cls.getResourceAsStream(cls.getSimpleName() + ".properties")) {
			if (stream == null) {
				throw new FileNotFoundException("Resource not found for class: " + cls.getName());
			}
			properties = new Properties();
			properties.load(stream);
		}
		return properties;
	}

	/**
	 * Get a {@link String} system property.
	 * <p>
	 * The system property key to retrieve is created by concatenating the package name of the submitted class with the
	 * submitted key.
	 * </p>
	 *
	 * @param cls The class to use for system property key creation.
	 * @param key The key to use for system property key creation.
	 * @param def The default value to use in case the system property is undefined.
	 * @return The system property value or the submitted default value if the system property is undefined.
	 */
	@Nullable
	public static String get(Class<?> cls, String key, @Nullable String def) {
		return System.getProperty(systemPropertyKey(cls, key), def);
	}

	/**
	 * Get a {@code int} system property.
	 * <p>
	 * The system property key to retrieve is created by concatenating the package name of the submitted class with the
	 * submitted key.
	 * </p>
	 *
	 * @param cls The class to use for system property key creation.
	 * @param key The key to use for system property key creation.
	 * @param def The default value to use in case the system property is undefined.
	 * @return The system property value or the submitted default value if the system property is undefined.
	 */
	public static int getInt(Class<?> cls, String key, int def) {
		return getInt(System.getProperties(), systemPropertyKey(cls, key), def);
	}

	/**
	 * Get a {@link String} property.
	 *
	 * @param properties The properties object to evaluate.
	 * @param key The property key to retrieve.
	 * @param def The default value to use in case the property is undefined.
	 * @return The property value or the submitted default value if the property is undefined.
	 */
	@Nullable
	public static String get(Properties properties, String key, String def) {
		return properties.getProperty(key, def);
	}

	/**
	 * Get a {@code int} property.
	 *
	 * @param properties The properties object to evaluate.
	 * @param key The property key to retrieve.
	 * @param def The default value to use in case the property is undefined.
	 * @return The property value or the submitted default value if the property is undefined.
	 */
	public static int getInt(Properties properties, String key, int def) {
		return toInt(properties.getProperty(key), key, def);
	}

	private static String systemPropertyKey(Class<?> cls, String key) {
		return cls.getPackage().getName() + key;
	}

	private static int toInt(@Nullable String property, String key, int def) {
		int propertyValue = def;

		if (property != null) {
			try {
				propertyValue = Integer.decode(property).intValue();
			} catch (NumberFormatException e) {
				LOG.warning(e, "Invalid integer property value ''{0}''=''{1}''; using default value", key, property);
			}
		}
		return propertyValue;
	}

}
