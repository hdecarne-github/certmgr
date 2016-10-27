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
package de.carne.certmgr.certs;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.carne.certmgr.certs.x500.X500Names;

/**
 * This class provides a generic way to inspected and display the
 * content/attributes of all kind of Certificate objects.
 */
public class AttributesInspector {

	private final String name;

	private final String value;

	private final List<AttributesInspector> children = new ArrayList<>();

	private AttributesInspector(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Get this inspector's name.
	 *
	 * @return This inspector's name.
	 */
	public String name() {
		return this.name;
	}

	/**
	 * Get this inspector's value.
	 *
	 * @return This inspector's value or {@code null} if there is no value.
	 */
	public String value() {
		return this.value;
	}

	/**
	 * Get this inspector's children.
	 *
	 * @return This inspector's children or {@code null} if there are no
	 *         children.
	 */
	public List<AttributesInspector> children() {
		return Collections.unmodifiableList(this.children);
	}

	/**
	 * Create {@code AttributesInspector} for a store entry.
	 *
	 * @param entry The store entry to create the inspector for.
	 * @return The created inspector.
	 */
	public static AttributesInspector fromUserCertStoreEntry(UserCertStoreEntry entry) {
		AttributesInspector entryAttribute = new AttributesInspector(AttributesInspectorI18N.formatSTR_ENTRY(), null);

		entryAttribute.addChild(AttributesInspectorI18N.formatSTR_ENTRY_ID(), entry.id().toString());
		entryAttribute.addChild(AttributesInspectorI18N.formatSTR_ENTRY_DN(), X500Names.toString(entry.dn()));
		return entryAttribute;
	}

	/**
	 * Create {@code AttributesInspector} for CRT object.
	 *
	 * @param crt The CRT object to create the inspector for.
	 * @return The created inspector.
	 */
	public static AttributesInspector fromCRT(X509Certificate crt) {
		AttributesInspector crtAttribute = new AttributesInspector(AttributesInspectorI18N.formatSTR_CRT(), null);

		crtAttribute.addChild(AttributesInspectorI18N.formatSTR_CRT_TYPE(), crt.getType());
		crtAttribute.addChild(AttributesInspectorI18N.formatSTR_CRT_VERSION(), Integer.toString(crt.getVersion()));
		crtAttribute.addChild(AttributesInspectorI18N.formatSTR_CRT_SERIALNUMBER(), crt.getSerialNumber().toString());
		crtAttribute.addChild(AttributesInspectorI18N.formatSTR_CRT_SIGALG(), crt.getSigAlgName());
		crtAttribute.addChild(AttributesInspectorI18N.formatSTR_CRT_ISSUERDN(),
				X500Names.toString(crt.getIssuerX500Principal()));
		crtAttribute.addChild(AttributesInspectorI18N.formatSTR_CRT_NOTBEFORE(), formatDate(crt.getNotBefore()));
		crtAttribute.addChild(AttributesInspectorI18N.formatSTR_CRT_NOTAFTER(), formatDate(crt.getNotAfter()));
		crtAttribute.addChild(AttributesInspectorI18N.formatSTR_CRT_SUBJECTDN(),
				X500Names.toString(crt.getSubjectX500Principal()));
		crtAttribute.addChild(AttributesInspectorI18N.formatSTR_CRT_PUBLICKEY(), formatPublicKey(crt.getPublicKey()));
		return crtAttribute;
	}

	private AttributesInspector addChild(String childName, String childValue) {
		AttributesInspector childAttribute = new AttributesInspector(childName, childValue);

		this.children.add(childAttribute);
		return childAttribute;
	}

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();

	private static String formatDate(Date date) {
		return DATE_FORMAT.format(date);
	}

	private static String formatPublicKey(PublicKey publicKey) {
		StringBuilder buffer = new StringBuilder();

		buffer.append(publicKey.getAlgorithm());
		if (publicKey instanceof RSAPublicKey) {
			RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

			buffer.append("/").append(rsaPublicKey.getModulus().bitLength());
		}
		return buffer.toString();
	}

}
