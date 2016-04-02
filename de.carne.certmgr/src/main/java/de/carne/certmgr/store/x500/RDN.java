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

/**
 * Relative Distinguished Name (RDN).
 */
public final class RDN {

	String type;
	String value;

	RDN(String type, String value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * Get the element's type.
	 *
	 * @return The element's type.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Get the element's value.
	 *
	 * @return The element's value.
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Set the elemnt's value.
	 *
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
