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
package de.carne;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Generic program main class.
 */
public abstract class MainLoader implements Main {

	/**
	 * Perform low level initialization and then forward control to the actual
	 * application.
	 *
	 * @param args The application's command line.
	 */
	public static void main(String[] args) {
		int status = -1;

		try {
			status = init().newInstance().run(args);
		} catch (Throwable e) {
			printUncaughtException(e);
		}
		System.exit(status);
	}

	/**
	 * Perform global initialization steps and load the {@code Main} interface.
	 *
	 * @return The application's main class to create and invoke for execution.
	 */
	public static Class<? extends Main> init() {
		Class<? extends Main> mainClass;

		try (InputStream initStream = ApplicationLoader.class.getResourceAsStream("Main");
				BufferedReader initReader = new BufferedReader(
						new InputStreamReader(initStream, StandardCharsets.UTF_8))) {
			String mainClassName = initReader.readLine();
			String propertyLine;

			while ((propertyLine = initReader.readLine()) != null) {
				int splitIndex = propertyLine.indexOf('=');

				assert splitIndex > 0;

				String propertyKey = propertyLine.substring(0, splitIndex).trim();
				String propertyValue = propertyLine.substring(splitIndex + 1).trim();

				System.setProperty(propertyKey, propertyValue);
			}
			mainClass = Class.forName(mainClassName).asSubclass(Main.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return mainClass;
	}

	/**
	 * Print an uncaught exception to {@code System.err}.
	 *
	 * @param cause The uncaught exception to print.
	 * @see #printUncaughtException(PrintStream, Throwable)
	 */
	public static void printUncaughtException(Throwable cause) {
		printUncaughtException(System.err, cause);
	}

	/**
	 * Print uncaught exception to a specific {@code PrintStream}.
	 * <p>
	 * This function prints the actual stack trace of the causing exception as
	 * well as the current program state including running threads, runtime
	 * information and system properties.
	 * </p>
	 *
	 * @param ps The {@code PrintStream} to print to.
	 * @param cause The uncaught exception to print.
	 */
	public static void printUncaughtException(PrintStream ps, Throwable cause) {
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
			if (caller.getClass().equals(MainLoader.class)) {
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
