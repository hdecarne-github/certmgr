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
package de.carne.util;

/**
 * Utility class used for managing 2-tuples.
 * 
 * @param <F> Type of first element.
 * @param <S> Type of second element.
 */
public final class Pair<F, S> {

	private final F first;
	private final S second;

	/**
	 * Construct Pair.
	 *
	 * @param first First value.
	 * @param second Second value.
	 */
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * @return the first
	 */
	public F getFirst() {
		return this.first;
	}

	/**
	 * @return the second
	 */
	public S getSecond() {
		return this.second;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.first + " = " + this.second;
	}

}
