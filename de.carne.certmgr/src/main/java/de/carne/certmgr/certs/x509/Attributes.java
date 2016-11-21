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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.x500.X500Names;
import de.carne.jfx.util.ShortDate;

/**
 * This class provides a generic way to access the content/attributes of all
 * kind of X.509 certificate objects.
 */
public class Attributes {

	private final String name;

	private final String value;

	private final List<Attributes> children = new ArrayList<>();

	Attributes(String name) {
		this(name, null);
	}

	Attributes(String name, String value) {
		this.name = name;
		this.value = value;
	}

	Attributes add(String childName) {
		return add(childName, null);
	}

	Attributes add(String childName, String childValue) {
		Attributes childAttributes = new Attributes(childName, childValue);

		this.children.add(childAttributes);
		return childAttributes;
	}

	Attributes add(Attributes childAttributes) {
		this.children.add(childAttributes);
		return childAttributes;
	}

	Attributes add(AttributesContent content) {
		content.addAttributes(this);
		return this;
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
	 * @return This attribute's value, or {@code null} if there is no value.
	 */
	public String value() {
		return this.value;
	}

	/**
	 * Get this attribute's children.
	 *
	 * @return This attribute's children, or {@code null} if there are no
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
		Attributes entryAttributes = new Attributes(AttributesI18N.formatSTR_ENTRY());

		entryAttributes.add(AttributesI18N.formatSTR_ENTRY_ID(), entry.id().toString());
		entryAttributes.add(AttributesI18N.formatSTR_ENTRY_DN(), X500Names.toString(entry.dn()));
		return entryAttributes;
	}

	static String printShortDate(Date date) {
		return ShortDate.FORMAT.format(date);
	}

	static String printSerial(BigInteger serial) {
		return serial.toString();
	}

}
