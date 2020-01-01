/*
 * Copyright (c) 2015-2020 Holger de Carne and contributors, All Rights Reserved.
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

import org.eclipse.jdt.annotation.Nullable;

abstract class Enumeration<T> {

	private final String name;

	private final T value;

	protected Enumeration(String name, T value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Get this instance's name.
	 *
	 * @return This instance's name.
	 */
	public String name() {
		return this.name;
	}

	/**
	 * Get this instance's value.
	 *
	 * @return This instance's value.
	 */
	public T value() {
		return this.value;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return this == obj || (obj instanceof KeyUsage && this.value.equals(((Enumeration<?>) obj).value));
	}

	@Override
	public String toString() {
		return this.name;
	}

}
