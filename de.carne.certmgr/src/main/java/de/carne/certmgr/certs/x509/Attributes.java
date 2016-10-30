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

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Extension;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.x500.X500Names;
import de.carne.jfx.util.ShortDate;
import de.carne.util.Exceptions;

/**
 * This class provides a generic way to display the content/attributes of all
 * kind of Certificate objects.
 */
public class Attributes {

	private final String name;

	private final String value;

	private final List<Attributes> children = new ArrayList<>();

	Attributes(String name, String value) {
		this.name = name;
		this.value = value;
	}

	Attributes addChild(String childName, String childValue) {
		Attributes childAttributes = new Attributes(childName, childValue);

		this.children.add(childAttributes);
		return childAttributes;
	}

	void addExtension(X509Extension extension) {
		Set<String> criticalExtensionOIDs = extension.getCriticalExtensionOIDs();

		if (criticalExtensionOIDs != null) {
			for (String criticalExtensionOID : criticalExtensionOIDs) {
				try {
					X509ExtensionData extensionData = X509ExtensionData.decode(criticalExtensionOID, true,
							extension.getExtensionValue(criticalExtensionOID));

					this.children.add(extensionData.toAttributes());
				} catch (IOException e) {
					Exceptions.ignore(e);
				}
			}
		}

		Set<String> nonCriticalExtensionOIDs = extension.getNonCriticalExtensionOIDs();

		if (nonCriticalExtensionOIDs != null) {
			for (String nonCriticalExtensionOID : nonCriticalExtensionOIDs) {
				try {
					X509ExtensionData extensionData = X509ExtensionData.decode(nonCriticalExtensionOID, false,
							extension.getExtensionValue(nonCriticalExtensionOID));

					this.children.add(extensionData.toAttributes());
				} catch (IOException e) {
					Exceptions.ignore(e);
				}
			}
		}
	}

	/**
	 * Get this attribute's name.
	 *
	 * @return This attribute's name.
	 */
	public String name() {
		return this.name;
	}

	/**
	 * Get this attribute's value.
	 *
	 * @return This attribute's value or {@code null} if there is no value.
	 */
	public String value() {
		return this.value;
	}

	/**
	 * Get this attribute's children.
	 *
	 * @return This attribute's children or {@code null} if there are no
	 *         children.
	 */
	public List<Attributes> children() {
		return Collections.unmodifiableList(this.children);
	}

	/**
	 * Get a store entry's {@code Attributes}.
	 *
	 * @param entry The store entry to get the attributes for.
	 * @return The store entry's attributes.
	 */
	public static Attributes toAttributes(UserCertStoreEntry entry) {
		Attributes entryAttributes = new Attributes(AttributesI18N.formatSTR_ENTRY(), null);

		entryAttributes.addChild(AttributesI18N.formatSTR_ENTRY_ID(), entry.id().toString());
		entryAttributes.addChild(AttributesI18N.formatSTR_ENTRY_DN(), X500Names.toString(entry.dn()));
		return entryAttributes;
	}

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

	static String printShortDate(Date date) {
		return ShortDate.FORMAT.format(date);
	}

	static String printPublicKey(PublicKey publicKey) {
		StringBuilder buffer = new StringBuilder();

		buffer.append(publicKey.getAlgorithm());
		if (publicKey instanceof RSAPublicKey) {
			RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

			buffer.append("/").append(rsaPublicKey.getModulus().bitLength());
		}
		return buffer.toString();
	}

}
