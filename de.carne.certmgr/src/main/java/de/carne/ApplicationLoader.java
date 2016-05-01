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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Launcher class used to install a class loader making the jar file and it's
 * contained jar files available for class loading.
 */
public final class ApplicationLoader extends URLClassLoader {

	private static final boolean DEBUG = System.getProperty(ApplicationLoader.class.getName() + ".DEBUG") != null;

	private static final String PROTOCOL_RESOURCE = "resource";

	static {
		ClassLoader.registerAsParallelCapable();
		URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {

			@Override
			public URLStreamHandler createURLStreamHandler(String protocol) {
				return ApplicationLoader.createURLStreamHandler(protocol);
			}

		});
	}

	private static Path CODE_PATH;

	static {
		Path codePath;

		try {
			URL codeURL = ApplicationLoader.class.getProtectionDomain().getCodeSource().getLocation();

			if (DEBUG) {
				System.out.println("Loader: Code source URL: " + codeURL);
			}

			URI codeURI = codeURL.toURI();

			if (DEBUG) {
				System.out.println("Loader: Code source URI: " + codeURI);
			}

			codePath = Paths.get(codeURI);

			if (DEBUG) {
				System.out.println("Loader: Code source path: " + codePath);
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		CODE_PATH = codePath;
	}

	/**
	 * Get the {@code JarFile} this application has been loaded from.
	 *
	 * @return The {@code JarFile} or {@code null} if the application was not
	 *         loaded from a release Jar.
	 * @throws IOException if an I/O error occurs while opening the Jar.
	 */
	public static JarFile getCodeJar() throws IOException {
		JarFile codeJar = null;

		if (CODE_PATH != null && Files.isRegularFile(CODE_PATH)) {
			codeJar = new JarFile(CODE_PATH.toFile());
		}
		return codeJar;
	}

	private static final URL[] RESOURCE_URLS;

	static {
		ArrayList<URL> libURLs = new ArrayList<>();

		try (JarFile codeJar = getCodeJar()) {
			if (codeJar != null) {
				libURLs.add(new URL(PROTOCOL_RESOURCE + ":/"));

				if (DEBUG) {
					System.out.println("Loader: Adding internal JARs to classpath...");
				}

				Iterator<JarEntry> jarEntries = codeJar.stream().filter(e -> e.getName().endsWith(".jar")).iterator();

				while (jarEntries.hasNext()) {
					String jarEntryName = jarEntries.next().getName();

					if (DEBUG) {
						System.out.println("Loader: Adding internal JAR: " + jarEntryName);
					}
					libURLs.add(new URL("jar:" + PROTOCOL_RESOURCE + ":/" + jarEntryName + "!/"));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		RESOURCE_URLS = libURLs.toArray(new URL[libURLs.size()]);
	}

	private static final String THIS_CLASS = ApplicationLoader.class.getName();

	// Prefix of class names that need to be loaded via system classloader (e.g.
	// log handlers).
	private static String[] SYSTEM_CLASSES = new String[] { THIS_CLASS, "de.carne.util.logging" };

	private ClassLoader systemClassloader = getSystemClassLoader();

	private ApplicationLoader() {
		super(RESOURCE_URLS, (RESOURCE_URLS.length > 1 ? null : getSystemClassLoader()));
	}

	private static final String MAIN_CLASS = ApplicationLoader.class.getPackage().getName() + ".MainLoader";

	/**
	 * Perform Classloader initialization and then invoke the actual main class.
	 *
	 * @param args The program's command line.
	 */
	public static void main(String[] args) {
		try (ApplicationLoader classLoader = new ApplicationLoader()) {
			Thread.currentThread().setContextClassLoader(classLoader);

			Class<?> mainClass = classLoader.loadClass(MAIN_CLASS);
			Method main = mainClass.getMethod("main", String[].class);

			main.invoke(null, (Object) args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static URLStreamHandler createURLStreamHandler(String protocol) {
		URLStreamHandler handler = null;

		if (PROTOCOL_RESOURCE.equals(protocol)) {
			handler = new URLStreamHandler() {

				@Override
				protected URLConnection openConnection(URL u) throws IOException {
					return ApplicationLoader.openConnection(u);
				}

			};
		}
		return handler;
	}

	static URLConnection openConnection(URL u) {
		return new URLConnection(u) {

			@Override
			public void connect() throws IOException {
				// Nothing to do here
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return ApplicationLoader.getInputStream(getURL());
			}

		};
	}

	static InputStream getInputStream(URL u) throws IOException {
		InputStream resourceStream = ApplicationLoader.class.getResourceAsStream(u.getFile());

		if (resourceStream == null) {
			throw new FileNotFoundException("Unknown resource: " + u);
		}
		return resourceStream;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		boolean useSystemClassLoader = false;

		for (String systemClassPrefix : SYSTEM_CLASSES) {
			if (name.startsWith(systemClassPrefix)) {
				useSystemClassLoader = true;
				break;
			}
		}
		return (useSystemClassLoader ? this.systemClassloader.loadClass(name) : super.loadClass(name));
	}

}
