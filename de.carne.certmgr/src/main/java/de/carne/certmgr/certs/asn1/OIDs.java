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
package de.carne.certmgr.certs.asn1;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import de.carne.util.Exceptions;
import de.carne.util.PropertiesHelper;
import de.carne.util.logging.Log;

/**
 * Utility class providing ASN.1 Object Identifier (OID) related functions.
 */
public final class OIDs {

	private static final Log LOG = new Log();

	private static final String OIDS_RESOURCE = OIDs.class.getSimpleName() + ".properties";

	private static final String OIDS_USER_FILE = PropertiesHelper.get(OIDs.class, "", null);

	private static final Map<String, String> OIDS = new HashMap<>();

	static {
		try (InputStream oidsStream = OIDs.class.getResourceAsStream(OIDS_RESOURCE)) {
			readOIDs(oidsStream);
		} catch (IOException e) {
			Exceptions.toRuntime(e);
		}
		if (OIDS_USER_FILE != null) {
			try (FileInputStream oidsStream = new FileInputStream(OIDS_USER_FILE)) {
				readOIDs(oidsStream);
			} catch (IOException e) {
				LOG.warning(e, "An error occured while reading user defined Object Identifiers from ''{0}''",
						OIDS_USER_FILE);
			}
		}
	}

	private static void readOIDs(InputStream in) throws IOException {
		Properties oids = new Properties();

		oids.load(in);
		for (Map.Entry<Object, Object> oid : oids.entrySet()) {
			String oidID = oid.getKey().toString();
			String oidName = oid.getValue().toString();

			OIDS.put(oidID, oidName);
		}
	}

	private OIDs() {
		// Make sure this class is not instantiated from outside
	}

	/**
	 * Convert an OID string to it's string representation.
	 * <p>
	 * For known OID this is the defined OID name otherwise the unchanged OID
	 * string.
	 *
	 * @param oid The OID to convert.
	 * @return The OID's string representation.
	 */
	public static String toString(String oid) {
		return OIDS.getOrDefault(oid, oid);
	}

}
