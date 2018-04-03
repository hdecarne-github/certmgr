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
package de.carne.certmgr.certs.x500;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import de.carne.boot.logging.Log;
import de.carne.util.Exceptions;
import de.carne.util.Strings;
import de.carne.util.SystemProperties;

/**
 * Utility class providing X.500 names related functions.
 */
public final class X500Names {

	private X500Names() {
		// Make sure this class is not instantiated from outside
	}

	private static final Log LOG = new Log();

	private static final String OIDS_RESOURCE = X500Names.class.getSimpleName() + ".properties";

	private static final String OIDS_USER_FILE = SystemProperties.value(X500Names.class.getPackage().getName(), "");

	private static final Map<String, String> OIDS = new HashMap<>();

	private static final Map<String, String> NAMES = new HashMap<>();

	static {
		try (InputStream oidsStream = X500Names.class.getResourceAsStream(OIDS_RESOURCE)) {
			readOIDs(oidsStream);
		} catch (IOException e) {
			throw Exceptions.toRuntime(e);
		}
		if (Strings.notEmpty(OIDS_USER_FILE)) {
			try (FileInputStream oidsStream = new FileInputStream(OIDS_USER_FILE)) {
				readOIDs(oidsStream);
			} catch (IOException e) {
				LOG.warning(e, "An error occured while reading user defined X.500 names from ''{0}''", OIDS_USER_FILE);
			}
		}
	}

	private static void readOIDs(InputStream in) throws IOException {
		Properties oids = new Properties();

		oids.load(in);
		for (Map.Entry<Object, Object> oid : oids.entrySet()) {
			String oidID = oid.getKey().toString();
			String[] oidNames = Strings.split(oid.getValue().toString(), ',', true);

			OIDS.put(oidID, oidNames[0]);
			for (String oidName : oidNames) {
				NAMES.put(oidName.trim(), oidID);
			}
		}
	}

	/**
	 * Convert {@link X500Principal} to it's string representation.
	 * <p>
	 * This function uses the configured OID informations to resolve any known OID to a human readable string.
	 *
	 * @param principal The principal to convert.
	 * @return The principal's string representation.
	 */
	public static String toString(X500Principal principal) {
		return principal.getName(X500Principal.RFC2253, OIDS);
	}

	/**
	 * Create a {@link X500Principal} from it's string representation.
	 *
	 * @param name The principal name to parse.
	 * @return The parsed principal.
	 * @throws IllegalArgumentException if the parse operation fails.
	 */
	public static X500Principal fromString(String name) throws IllegalArgumentException {
		return new X500Principal(name, NAMES);
	}

	/**
	 * Get the collection of known RDN types.
	 *
	 * @return The collection of known RDN types.
	 */
	public static Set<String> rdnTypes() {
		return Collections.unmodifiableSet(NAMES.keySet());
	}

}
