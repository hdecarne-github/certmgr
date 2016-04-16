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
package de.carne.certmgr.store.x500;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.x500.X500Principal;

import de.carne.util.logging.Log;

/**
 * Utility class used to handle X.500 names.
 */
public final class X500Names {

	private static final Log LOG = new Log(X500Names.class);

	private static final Map<String, String> OID_TYPE_MAP = new HashMap<>();

	static {
		String defaultTypeResource = X500Names.class.getSimpleName() + ".properties";

		try (InputStream defaultTypeStream = X500Names.class.getResourceAsStream(defaultTypeResource)) {
			loadTypes(defaultTypeStream);
			LOG.info(null, "Default X.500 types loaded from resource ''{0}''", defaultTypeResource);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to read default X.500 types from resource '" + defaultTypeResource
					+ "'", e);
		}

		String extraTypeResource = System.getProperty(X500Names.class.getName());

		if (extraTypeResource != null) {
			try (InputStream extraTypeStream = new FileInputStream(extraTypeResource)) {
				loadTypes(extraTypeStream);
				LOG.info(null, "Extra X.500 types loaded from file ''{0}''", extraTypeResource);
			} catch (IOException e) {
				LOG.warning(e, null, "Unable to read extra X.500 types from file ''{0}''", extraTypeResource);
			}
		}
	}

	private static void loadTypes(InputStream typeStream) throws IOException {
		Properties types = new Properties();

		types.load(typeStream);
		for (Map.Entry<Object, Object> entry : types.entrySet()) {
			String type = entry.getKey().toString();
			String typeOID = entry.getValue().toString();

			OID_TYPE_MAP.put(typeOID, type);
		}
	}

	/**
	 * Get the list of known X.500 types.
	 *
	 * @return The list of known X.500 types.
	 */
	public static String[] getTypes() {
		ArrayList<String> sortedTypes = new ArrayList<>(OID_TYPE_MAP.values());

		sortedTypes.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}

		});
		return sortedTypes.toArray(new String[sortedTypes.size()]);
	}

	/**
	 * Format a X.500 name including known custom OID based types.
	 *
	 * @param principal The principal to format.
	 * @return The principal's string representation.
	 */
	public static String toString(X500Principal principal) {
		return principal.getName(X500Principal.RFC1779, OID_TYPE_MAP);
	}

	/**
	 * Create a X.500 principal from a distinguished name (DN) string.
	 *
	 * @param name The distinguished name to create the X.500 principal from.
	 * @return The created X.500 principal.
	 * @throws IllegalArgumentException If the submitted name is not a valid DN.
	 */
	public static X500Principal toPrincipal(String name) {
		return new X500Principal(name, OID_TYPE_MAP);
	}

	/**
	 * Parse a distinguished name (DN) string.
	 *
	 * @param name The distinguished name to parse.
	 * @param strict Whether to ignore invalid name parts (false) or throw an exception (true).
	 * @return The parsed a distinguished name.
	 * @throws IllegalArgumentException If the submitted name is not a valid DN.
	 */
	public static RDN[] decodeDN(String name, boolean strict) {
		DNDecoder parser = new DNDecoder(name, strict);

		return parser.decodeDN();
	}

	/**
	 * Encode a distinguished name (DN) to a string buffer.
	 *
	 * @param buffer The buffer to encode into.
	 * @param type The type to encode.
	 * @param value The value to encode.
	 * @return The update string buffer.
	 */
	public static StringBuilder encodeDN(StringBuilder buffer, String type, String value) {
		if (buffer.length() > 0) {
			buffer.append(',');
		}
		buffer.append(type);
		buffer.append('=');
		encode(buffer, value);
		return buffer;
	}

	private static StringBuilder encode(StringBuilder buffer, String value) {
		boolean quote = !(value.indexOf(',') < 0 && value.indexOf(';') < 0);
		String escapeChars;
		String escape0Chars;

		if (quote) {
			buffer.append('"');
			escapeChars = "\"\\";
			escape0Chars = escapeChars;
		} else {
			escapeChars = ",+\"\\<>;=";
			escape0Chars = " " + escapeChars;
		}

		int len = value.length();

		for (int pos = 0; pos < len; pos++) {
			char c = value.charAt(pos);

			if ((pos > 0 ? escapeChars : escape0Chars).indexOf(c) >= 0) {
				buffer.append('\\');
			}
			buffer.append(c);
		}
		if (quote) {
			buffer.append('"');
		}
		return buffer;
	}

}
