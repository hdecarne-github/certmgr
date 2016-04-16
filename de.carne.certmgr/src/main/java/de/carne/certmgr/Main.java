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
package de.carne.certmgr;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Program main class.
 */
public abstract class Main {

	private final static String THIS_PACKAGE = Main.class.getPackage().getName();

	private final static String[] INIT_SYSTEM_PROPERTIES = {

			"java.util.logging.config.class", "de.carne.util.logging.LogConfig",

			"java.util.prefs.PreferencesFactory", "de.carne.util.prefs.PropertiesPreferencesFactory"

	};

	private final static String JFX_MAIN_CLASS = THIS_PACKAGE + ".jfx.JFXMain";

	/**
	 * Perform basic initialization and then invoke the actual main class.
	 *
	 * @param args The program's command line.
	 */
	public static void main(String[] args) {
		int status = -1;

		try {
			init();
			status = Class.forName(JFX_MAIN_CLASS).asSubclass(Main.class).newInstance().run(args);
		} catch (Throwable e) {
			printUnhandledException(e);
		}
		System.exit(status);
	}

	/**
	 * Perform global initialization steps.
	 */
	public static void init() {
		for (int propertyIndex = 0; propertyIndex < INIT_SYSTEM_PROPERTIES.length; propertyIndex += 2) {
			System.setProperty(INIT_SYSTEM_PROPERTIES[propertyIndex], INIT_SYSTEM_PROPERTIES[propertyIndex + 1]);
		}
	}

	/**
	 * Run main class.
	 *
	 * @param args The program's command line.
	 * @return The program's exit status.
	 */
	protected abstract int run(String[] args);

	/**
	 * Print an unhandled exception to <code>System.err</code>.
	 *
	 * @param cause The unhandled exception to print.
	 * @see #printUnhandledException(PrintStream, Throwable)
	 */
	public static void printUnhandledException(Throwable cause) {
		printUnhandledException(System.err, cause);
	}

	/**
	 * Print unhandled exception to a specific <code>PrintStream</code>.
	 * <p>
	 * This function prints the actual stack trace of the causing exception as
	 * well as the current program state including running threads, runtime
	 * information and system properties.
	 * </p>
	 *
	 * @param ps The <code>PrintStream</code> to print to.
	 * @param cause The unhandled exception to print.
	 */
	public static void printUnhandledException(PrintStream ps, Throwable cause) {
		assert cause != null;

		printCause(ps, cause);
		printThreads(ps);
		printRuntime(ps);
		printSystemProperties(ps);
		ps.print("--- End ---");
	}

	private static void printCause(PrintStream ps, Throwable cause) {
		String exceptionName = cause.getClass().getName();
		String threadName = Thread.currentThread().toString();

		ps.println("--- Unhandled exception '" + exceptionName + "' caught in thread '" + threadName + "' ---");
		cause.printStackTrace(ps);

		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement catchedAt = null;

		for (StackTraceElement caller : stackTrace) {
			if (caller.getClass().equals(Main.class)) {
				break;
			}
			catchedAt = caller;
		}
		if (catchedAt != null) {
			ps.print("catched at ");
			ps.println(catchedAt);
		}
	}

	private static void printThreads(PrintStream ps) {
		Map<Thread, StackTraceElement[]> stackTraceMap = Thread.getAllStackTraces();

		for (Map.Entry<Thread, StackTraceElement[]> stackTraceEntry : stackTraceMap.entrySet()) {
			Thread thread = stackTraceEntry.getKey();

			if (!thread.equals(Thread.currentThread())) {
				String threadName = thread.toString();

				ps.println("--- Thread '" + threadName + "' running ---");

				StackTraceElement[] stackTrace = stackTraceEntry.getValue();

				for (StackTraceElement caller : stackTrace) {
					ps.println("\tat " + caller);
				}
			}
		}
	}

	private static void printRuntime(PrintStream ps) {
		ps.println("--- Runtime ---");

		Runtime rt = Runtime.getRuntime();

		ps.println("\tavailableProcessors = " + rt.availableProcessors());
		ps.println("\tfreeMemory = " + rt.freeMemory());
		ps.println("\ttotalMemory = " + rt.totalMemory());
		ps.println("\tmaxMemory = " + rt.maxMemory());
	}

	private static void printSystemProperties(PrintStream ps) {
		ps.println("--- System properties ---");

		Properties properties = System.getProperties();
		ArrayList<String> keys = new ArrayList<>(properties.stringPropertyNames());

		Collections.sort(keys);
		for (String key : keys) {
			String value = encodeString(properties.getProperty(key));

			ps.println("\t" + key + " = " + value);
		}
	}

	private static final String encodeString(String s) {
		StringBuilder encoded = new StringBuilder();

		for (int cIndex = 0; cIndex < s.length(); cIndex++) {
			char c = s.charAt(cIndex);

			switch (c) {
			case '\0':
				encoded.append("\\0");
				break;
			case '\t':
				encoded.append("\\t");
				break;
			case '\n':
				encoded.append("\\n");
				break;
			case '\r':
				encoded.append("\\r");
				break;
			case '\\':
				encoded.append("\\\\");
				break;
			default:
				encoded.append(c);
			}
		}
		return encoded.toString();
	}
}
