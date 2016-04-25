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
package de.carne.util;

import java.util.jar.Attributes;
import java.util.jar.JarFile;

import de.carne.ApplicationLoader;

/**
 * Class providing version information.
 */
public final class Version {

	/**
	 * The project id.
	 */
	public static final String PROJECT_ID;

	/**
	 * The project name.
	 */
	public static final String PROJECT_NAME;

	/**
	 * The build version.
	 */
	public static final String BUILD_VERSION;

	/**
	 * The build date.
	 */
	public static final String BUILD_DATE;

	static {
		Attributes attributes = null;

		try (JarFile codeJar = ApplicationLoader.getCodeJar()) {
			if (codeJar != null) {
				attributes = codeJar.getManifest().getMainAttributes();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		PROJECT_ID = getAttributeValue(attributes, "X-Version-ProjectId");
		PROJECT_NAME = getAttributeValue(attributes, "X-Version-ProjectName");
		BUILD_VERSION = getAttributeValue(attributes, "X-Version-ProjectVersion");
		BUILD_DATE = getAttributeValue(attributes, "X-Version-BuildDate");
	}

	private static String getAttributeValue(Attributes attributes, String name) {
		String value = (attributes != null ? attributes.getValue(name) : null);

		return (value != null ? value : "?");
	}

}
