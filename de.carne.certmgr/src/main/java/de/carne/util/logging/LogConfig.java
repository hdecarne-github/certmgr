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
package de.carne.util.logging;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 * Logging configuration.
 */
public final class LogConfig {

	private static final String THIS_PACKAGE = LogConfig.class.getPackage().getName();

	private static String currentConfig = null;

	private static long currentConfigTime = 0;

	/**
	 * Default logging configuration.
	 */
	public static final String CONFIG_DEFAULT = "logging-default.properties";

	/**
	 * Verbose logging configuration..
	 */
	public static final String CONFIG_VERBOSE = "logging-verbose.properties";

	/**
	 * Debug logging configuration.
	 */
	public static final String CONFIG_DEBUG = "logging-debug.properties";

	/**
	 * Apply initial logging configuration.
	 * <p>
	 * To invoke this constructor during JDK logging initialization the
	 * {@code java.util.logging.config.class} system property must point to this
	 * class.
	 * </p>
	 */
	public LogConfig() {
		applyConfig(System.getProperty(THIS_PACKAGE, CONFIG_DEFAULT));
	}

	/**
	 * Apply a logging configuration.
	 * <p>
	 * The config parameter names the file or resource containing the logging
	 * configuration. First we try to read the config from a file based upon the
	 * current working directory. If this fails we try to read it as a resource.
	 * </p>
	 *
	 * @param config The configuration file or resource to apply.
	 * @return True, if the configuration was successfully applied.
	 */
	public static synchronized boolean applyConfig(String config) {
		assert config != null;

		boolean configApplied = currentConfig != null;

		if (currentConfig != null && !currentConfig.equals(config)) {
			LogManager.getLogManager().reset();
			configApplied = false;
		}
		if (!configApplied) {
			try (InputStream configIS = new FileInputStream(config)) {
				LogManager.getLogManager().readConfiguration(configIS);
				configApplied = true;
				currentConfig = config;
			} catch (FileNotFoundException e) {
				// Ignore, as we will try to read from a resource below
			} catch (Exception e) {
				System.err.println("An exception occured while reading the logging configuration file: " + config);
				e.printStackTrace();
			}
		}
		if (!configApplied) {
			try (InputStream configIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(config)) {
				if (configIS != null) {
					LogManager.getLogManager().readConfiguration(configIS);
					configApplied = true;
					currentConfig = config;
					currentConfigTime = System.currentTimeMillis();
				} else {
					System.err.println("Unable to open logging configuration: " + config);
				}
			} catch (Exception e) {
				System.err.println("An exception occured while reading the logging configuration resource: " + config);
				e.printStackTrace();
			}
		}
		return configApplied;
	}

	/**
	 * Get the timestamp the currently active logging configuration was applied.
	 *
	 * @return The timestamp the currently active logging configuration was
	 *         applied.
	 */
	public static long configTime() {
		return currentConfigTime;
	}

}
