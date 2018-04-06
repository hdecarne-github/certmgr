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
package de.carne.certmgr.certs.x509;

import java.io.IOException;
import java.security.cert.X509Extension;
import java.util.Set;

import de.carne.boot.Exceptions;

/**
 * Utility class providing {@link X509Extension} related functions.
 */
public final class X509ExtensionHelper {

	private X509ExtensionHelper() {
		// Make sure this class is not instantiated from outside
	}

	/**
	 * Adds an Extension object's attributes to an {@code Attributes} object.
	 *
	 * @param attributes The {@link Attributes} object to add the attributes to.
	 * @param extension The Extension object to get the attributes from.
	 */
	public static void addAttributes(Attributes attributes, X509Extension extension) {
		Set<String> criticalExtensionOIDs = extension.getCriticalExtensionOIDs();

		if (criticalExtensionOIDs != null) {
			for (String criticalExtensionOID : criticalExtensionOIDs) {
				try {
					X509ExtensionData extensionData = X509ExtensionData.decode(criticalExtensionOID, true,
							extension.getExtensionValue(criticalExtensionOID));

					attributes.add(extensionData.toAttributes());
				} catch (IOException e) {
					Exceptions.warn(e);
				}
			}
		}

		Set<String> nonCriticalExtensionOIDs = extension.getNonCriticalExtensionOIDs();

		if (nonCriticalExtensionOIDs != null) {
			for (String nonCriticalExtensionOID : nonCriticalExtensionOIDs) {
				try {
					X509ExtensionData extensionData = X509ExtensionData.decode(nonCriticalExtensionOID, false,
							extension.getExtensionValue(nonCriticalExtensionOID));

					attributes.add(extensionData.toAttributes());
				} catch (IOException e) {
					Exceptions.warn(e);
				}
			}
		}
	}

}
