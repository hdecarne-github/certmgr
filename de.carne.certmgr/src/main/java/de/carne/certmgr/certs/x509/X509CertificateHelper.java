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

import java.security.cert.X509Certificate;

import de.carne.certmgr.certs.x500.X500Names;

/**
 * Utility class providing {@link X509Certificate} related functions.
 */
public final class X509CertificateHelper {

	/**
	 * Get a CRT object's {@code Attributes}.
	 *
	 * @param crt The CRT object to get the attributes for.
	 * @return The CRT object's attributes.
	 */
	public static Attributes toAttributes(X509Certificate crt) {
		Attributes crtAttributes = new Attributes(AttributesI18N.formatSTR_CRT(), null);

		crtAttributes.addChild(AttributesI18N.formatSTR_CRT_VERSION(), Integer.toString(crt.getVersion()));
		crtAttributes.addChild(AttributesI18N.formatSTR_CRT_SERIALNUMBER(), crt.getSerialNumber().toString());
		crtAttributes.addChild(AttributesI18N.formatSTR_CRT_SIGALG(), crt.getSigAlgName());
		crtAttributes.addChild(AttributesI18N.formatSTR_CRT_ISSUERDN(),
				X500Names.toString(crt.getIssuerX500Principal()));
		crtAttributes.addChild(AttributesI18N.formatSTR_CRT_NOTBEFORE(), Attributes.printShortDate(crt.getNotBefore()));
		crtAttributes.addChild(AttributesI18N.formatSTR_CRT_NOTAFTER(), Attributes.printShortDate(crt.getNotAfter()));
		crtAttributes.addChild(AttributesI18N.formatSTR_CRT_SUBJECTDN(),
				X500Names.toString(crt.getSubjectX500Principal()));
		crtAttributes.addChild(AttributesI18N.formatSTR_CRT_PUBLICKEY(), Attributes.printPublicKey(crt.getPublicKey()));
		crtAttributes.addExtension(crt);
		return crtAttributes;
	}

}
