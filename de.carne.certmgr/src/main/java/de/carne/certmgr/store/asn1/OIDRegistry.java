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
package de.carne.certmgr.store.asn1;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import de.carne.util.logging.Log;

/**
 * Utility class used to register and format known object identifiers.
 */
public final class OIDRegistry {

	private static final Log LOG = new Log(OIDRegistry.class);

	private static final Map<String, String> OID_NAMES = new HashMap<>();

	static {
		String defaultOIDsResource = OIDRegistry.class.getSimpleName() + ".properties";

		try (InputStream defaultOIDsStream = OIDRegistry.class.getResourceAsStream(defaultOIDsResource)) {
			registerAll(defaultOIDsStream);
			defaultOIDsStream.close();
			LOG.info(null, "Default OIDs loaded from resource ''{0}''", defaultOIDsResource);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to read default OIDs from resource '" + defaultOIDsResource + "'",
					e);
		}

		String extraOIDsResource = System.getProperty(OIDRegistry.class.getName());

		if (extraOIDsResource != null) {
			try (InputStream extraOIDsStream = new FileInputStream(extraOIDsResource)) {
				registerAll(extraOIDsStream);
				LOG.info(null, "Extra OIDs loaded from file ''{0}''", extraOIDsResource);
			} catch (IOException e) {
				LOG.warning(e, null, "Unable to read extra OIDs from file ''{0}''", extraOIDsResource);
			}
		}
	}

	private static void registerAll(InputStream oidStream) throws IOException {
		Properties oids = new Properties();

		oids.load(oidStream);
		for (Map.Entry<Object, Object> entry : oids.entrySet()) {
			String oid = entry.getKey().toString();
			String name = entry.getValue().toString();

			OID_NAMES.put(oid, name);
		}
	}

	/**
	 * Register an object identifier.
	 *
	 * @param oid The object identifier to register.
	 * @param name The object identifier's name.
	 * @return The registered object identifier (for chaining)
	 */
	public static synchronized String register(String oid, String name) {
		OID_NAMES.put(oid, name);
		return oid;
	}

	/**
	 * Get a object identifier's name.
	 * <p>
	 * If no name has been registered for the submitted object identifier the object identifier itself is returned.
	 * </p>
	 *
	 * @param oid The object identifier to get the name for.
	 * @return The object identifier's name or the object identifier itself, if no name has been registered for it.
	 */
	public static synchronized String get(String oid) {
		return OID_NAMES.getOrDefault(oid, oid);
	}

}
