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

/**
 * Helper class to handle ASN.1 tagged objects in various contexts.
 *
 * @param <T> The context dependenig object type to handle.
 */
public class TaggedObject<T> {

	private final boolean explicit;
	private final int tagNo;
	private final T object;

	/**
	 * Construct TaggedObject.
	 *
	 * @param explicit The object's explicit flag.
	 * @param tagNo The object's tag number.
	 * @param object The actual object.
	 */
	public TaggedObject(boolean explicit, int tagNo, T object) {
		this.explicit = explicit;
		this.tagNo = tagNo;
		this.object = object;
	}

	/**
	 * @return the explicit
	 */
	public boolean isExplicit() {
		return this.explicit;
	}

	/**
	 * @return the tagNo
	 */
	public int getTagNo() {
		return this.tagNo;
	}

	/**
	 * @return the object
	 */
	public T getObject() {
		return this.object;
	}

}
