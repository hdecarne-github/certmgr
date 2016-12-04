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
package de.carne.certmgr.certs.x500;

/**
 * Relative Distinguished Name (RDN).
 */
public final class RDN {

	private final String type;

	private String value;

	/**
	 * Construct {@code RDN}.
	 *
	 * @param type The RDN type.
	 * @param value The RDN value.
	 */
	public RDN(String type, String value) {
		assert type != null;
		assert value != null;

		this.type = type;
		this.value = value;
	}

	/**
	 * Get the RDN type.
	 *
	 * @return The RDN type.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Get the RDN value.
	 *
	 * @return The RDN value.
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Set the RDN value.
	 *
	 * @param value The RDN value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.type + "=" + this.value;
	}

}
