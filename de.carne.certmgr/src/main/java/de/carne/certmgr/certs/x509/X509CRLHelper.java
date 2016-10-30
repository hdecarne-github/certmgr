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
package de.carne.certmgr.certs.x509;

import java.security.cert.X509CRL;

/**
 * Utility class providing {@link X509CRL} related functions.
 */
public final class X509CRLHelper {

	/**
	 * Get a CRL object's {@code Attributes}.
	 *
	 * @param crl The CRL object to get the attributes for.
	 * @return The CRL object's attributes.
	 */
	public static Attributes toAttributes(X509CRL crl) {
		Attributes crlAttributes = new Attributes(AttributesI18N.formatSTR_CRL(), null);

		crlAttributes.addExtension(crl);
		return crlAttributes;
	}

}
