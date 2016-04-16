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

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Simple wrapper for JDK's {@link Logger} class to have a minimum level of
 * abstraction and a clear level semantics.
 */
public final class Log {

	/**
	 * Log level for notice messages used to record fundamental application
	 * states and events.
	 */
	public static final Level LEVEL_NOTICE = Level.OFF;

	/**
	 * Log level for error messages.
	 */
	public static final Level LEVEL_ERROR = Level.SEVERE;

	/**
	 * Log level for warning messages.
	 */
	public static final Level LEVEL_WARNING = Level.WARNING;

	/**
	 * Log level for info messages.
	 */
	public static final Level LEVEL_INFO = Level.FINE;

	/**
	 * Log level for debug messages.
	 */
	public static final Level LEVEL_DEBUG = Level.FINER;

	private long loggerConfigTime;

	private Logger logger;

	/**
	 * Construct {@code Log}.
	 *
	 * @param clazz The class to derive the log name from.
	 */
	public Log(Class<?> clazz) {
		this.loggerConfigTime = LogConfig.configTime();
		this.logger = Logger.getLogger(clazz.getName());
	}

	/**
	 * Construct {@code Log}.
	 *
	 * @param clazz The class to derive the log name from.
	 * @param bundle The (optional) {@link ResourceBundle} to use for message
	 *        localization.
	 */
	public Log(Class<?> clazz, ResourceBundle bundle) {
		this.loggerConfigTime = LogConfig.configTime();
		this.logger = Logger.getLogger(clazz.getName(), bundle.getBaseBundleName());
	}

	/**
	 * Get the underlying JDK Logger.
	 *
	 * @return The underlying JDK Logger.
	 */
	public Logger getLogger() {
		long configTime = LogConfig.configTime();

		if (this.loggerConfigTime != configTime) {
			synchronized (this) {
				this.loggerConfigTime = configTime;
				this.logger = Logger.getLogger(this.logger.getName(), this.logger.getResourceBundleName());
			}
		}
		return this.logger;
	}

	/**
	 * Check whether a log level is currently logged.
	 *
	 * @param level The level to check.
	 * @return True, if the log level is currently logged.
	 */
	public boolean isLoggable(Level level) {
		return getLogger().isLoggable(level);
	}

	/**
	 * Log a message.
	 * <p>
	 * See {@link java.text.MessageFormat#format(String, Object...)} for format
	 * options.
	 * </p>
	 *
	 * @param level The log level.
	 * @param thrown The (optional) {@link Throwable} to log.
	 * @param bundle The (optional) {@link ResourceBundle} to use for message
	 *        localization.
	 * @param format The log message.
	 * @param args The (optional) log message parameters.
	 */
	public void log(Level level, Throwable thrown, ResourceBundle bundle, String format, Object... args) {
		Logger currentLogger = getLogger();

		if (currentLogger.isLoggable(level)) {
			LogRecord record = new LogRecord(level, format);

			record.setResourceBundle(bundle);
			record.setParameters(args);
			record.setThrown(thrown);
			record.setLoggerName(currentLogger.getName());
			currentLogger.log(record);
		}
	}

	/**
	 * Log a notice message.
	 * <p>
	 * See {@link java.text.MessageFormat#format(String, Object...)} for format
	 * options.
	 * </p>
	 *
	 * @param bundle The (optional) {@link ResourceBundle} to use for message
	 *        localization.
	 * @param format The log message.
	 * @param args The (optional) log message parameters.
	 */
	public void notice(ResourceBundle bundle, String format, Object... args) {
		log(LEVEL_NOTICE, null, bundle, format, args);
	}

	/**
	 * Log a notice message.
	 * <p>
	 * See {@link java.text.MessageFormat#format(String, Object...)
	 * MessageFormat.format} for format options.
	 * </p>
	 *
	 * @param thrown The (optional) {@link java.lang.Throwable Throwable} to
	 *        log.
	 * @param bundle The (optional) {@link ResourceBundle} to use for message
	 *        localization.
	 * @param format The log message.
	 * @param args The (optional) log message parameters.
	 */
	public void notice(Throwable thrown, ResourceBundle bundle, String format, Object... args) {
		log(LEVEL_NOTICE, thrown, bundle, format, args);
	}

	/**
	 * Log a error message.
	 * <p>
	 * See {@link java.text.MessageFormat#format(String, Object...)} for format
	 * options.
	 * </p>
	 *
	 * @param bundle The (optional) {@link ResourceBundle} to use for message
	 *        localization.
	 * @param format The log message.
	 * @param args The (optional) log message parameters.
	 */
	public void error(ResourceBundle bundle, String format, Object... args) {
		log(LEVEL_ERROR, null, bundle, format, args);
	}

	/**
	 * Log a error message.
	 * <p>
	 * See {@link java.text.MessageFormat#format(String, Object...)
	 * MessageFormat.format} for format options.
	 * </p>
	 *
	 * @param thrown The (optional) {@link java.lang.Throwable Throwable} to
	 *        log.
	 * @param bundle The (optional) {@link ResourceBundle} to use for message
	 *        localization.
	 * @param format The log message.
	 * @param args The (optional) log message parameters.
	 */
	public void error(Throwable thrown, ResourceBundle bundle, String format, Object... args) {
		log(LEVEL_ERROR, thrown, bundle, format, args);
	}

	/**
	 * Log a warning message.
	 * <p>
	 * See {@link java.text.MessageFormat#format(String, Object...)} for format
	 * options.
	 * </p>
	 *
	 * @param bundle The (optional) {@link ResourceBundle} to use for message
	 *        localization.
	 * @param format The log message.
	 * @param args The (optional) log message parameters.
	 */
	public void warning(ResourceBundle bundle, String format, Object... args) {
		log(LEVEL_WARNING, null, bundle, format, args);
	}

	/**
	 * Log a warning message.
	 * <p>
	 * See {@link java.text.MessageFormat#format(String, Object...)
	 * MessageFormat.format} for format options.
	 * </p>
	 *
	 * @param thrown The (optional) {@link java.lang.Throwable Throwable} to
	 *        log.
	 * @param bundle The (optional) {@link ResourceBundle} to use for message
	 *        localization.
	 * @param format The log message.
	 * @param args The (optional) log message parameters.
	 */
	public void warning(Throwable thrown, ResourceBundle bundle, String format, Object... args) {
		log(LEVEL_WARNING, thrown, bundle, format, args);
	}

	/**
	 * Check whether info log level is currently logged.
	 *
	 * @return true, if info log level is currently logged.
	 */
	public boolean isInfoLoggable() {
		return isLoggable(LEVEL_INFO);
	}

	/**
	 * Log a info message.
	 * <p>
	 * See {@link java.text.MessageFormat#format(String, Object...)} for format
	 * options.
	 * </p>
	 *
	 * @param bundle The (optional) {@link ResourceBundle} to use for message
	 *        localization.
	 * @param format The log message.
	 * @param args The (optional) log message parameters.
	 */
	public void info(ResourceBundle bundle, String format, Object... args) {
		log(LEVEL_INFO, null, bundle, format, args);
	}

	/**
	 * Log a info message.
	 * <p>
	 * See {@link java.text.MessageFormat#format(String, Object...)
	 * MessageFormat.format} for format options.
	 * </p>
	 *
	 * @param thrown The (optional) {@link java.lang.Throwable Throwable} to
	 *        log.
	 * @param bundle The (optional) {@link ResourceBundle} to use for message
	 *        localization.
	 * @param format The log message.
	 * @param args The (optional) log message parameters.
	 */
	public void info(Throwable thrown, ResourceBundle bundle, String format, Object... args) {
		log(LEVEL_INFO, thrown, bundle, format, args);
	}

	/**
	 * Check whether debug log level is currently logged.
	 *
	 * @return true, if debug log level is currently logged.
	 */
	public boolean isDebugLoggable() {
		return isLoggable(LEVEL_DEBUG);
	}

	/**
	 * Log a debug message.
	 * <p>
	 * See {@link java.text.MessageFormat#format(String, Object...)} for format
	 * options.
	 * </p>
	 *
	 * @param bundle The (optional) {@link ResourceBundle} to use for message
	 *        localization.
	 * @param format The log message.
	 * @param args The (optional) log message parameters.
	 */
	public void debug(ResourceBundle bundle, String format, Object... args) {
		log(LEVEL_DEBUG, null, bundle, format, args);
	}

	/**
	 * Log a debug message.
	 * <p>
	 * See {@link java.text.MessageFormat#format(String, Object...)
	 * MessageFormat.format} for format options.
	 * </p>
	 *
	 * @param thrown The (optional) {@link java.lang.Throwable Throwable} to
	 *        log.
	 * @param bundle The (optional) {@link ResourceBundle} to use for message
	 *        localization.
	 * @param format The log message.
	 * @param args The (optional) log message parameters.
	 */
	public void debug(Throwable thrown, ResourceBundle bundle, String format, Object... args) {
		log(LEVEL_DEBUG, thrown, bundle, format, args);
	}

	static String formatLevel(Level level) {
		String levelText;

		if (LEVEL_NOTICE.equals(level)) {
			levelText = " NOTICE";
		} else if (LEVEL_ERROR.equals(level)) {
			levelText = "  ERROR";
		} else if (LEVEL_WARNING.equals(level)) {
			levelText = "WARNING";
		} else if (LEVEL_INFO.equals(level)) {
			levelText = "   INFO";
		} else if (LEVEL_DEBUG.equals(level)) {
			levelText = "  DEBUG";
		} else {
			levelText = String.valueOf(level);
		}
		return levelText;
	}

}
