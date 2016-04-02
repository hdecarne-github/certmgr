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
package de.carne.certmgr.jfx;

/**
 * Utility class to pair object with individual names.
 *
 * @param <T> The option type.
 */
public class NamedOption<T extends Comparable<T>> implements Comparable<NamedOption<T>> {

	private T value;
	private String name;

	/**
	 * Construct NamedOption.
	 *
	 * @param value The option value.
	 */
	public NamedOption(T value) {
		this(value, null);
	}

	/**
	 * Construct NamedOption.
	 *
	 * @param name The option name.
	 */
	public NamedOption(String name) {
		this(null, name);
	}

	/**
	 * Construct NamedOption.
	 *
	 * @param value The option value.
	 * @param name The option name.
	 */
	public NamedOption(T value, String name) {
		this.value = value;
		this.name = (name != null ? name : value.toString());
	}

	/**
	 * Get the option value.
	 *
	 * @return The option value.
	 */
	public T getValue() {
		return this.value;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(NamedOption<T> o) {
		int compareResult;

		if (this.value != null && o.value != null) {
			compareResult = this.value.compareTo(o.value);
		} else if (this.value != null) {
			compareResult = 1;
		} else if (o.value != null) {
			compareResult = -1;
		} else {
			compareResult = 0;
		}
		return compareResult;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (this.value != null ? this.value.hashCode() : 0);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return this == obj
				|| (obj instanceof NamedOption<?> && (this.value != null ? this.value
						.equals(((NamedOption<?>) obj).value) : ((NamedOption<?>) obj).value == null));
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}

}
