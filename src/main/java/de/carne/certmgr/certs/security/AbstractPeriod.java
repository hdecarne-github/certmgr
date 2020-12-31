/*
 * Copyright (c) 2015-2021 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.certs.security;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.certmgr.util.Days;

/**
 * Abstract base class for {@link Days} based objects.
 */
public abstract class AbstractPeriod {

	private final Days period;

	/**
	 * Construct {@code AbstractPeriod}.
	 *
	 * @param period The period value to use for initialization.
	 */
	protected AbstractPeriod(Days period) {
		this.period = period;
	}

	/**
	 * Get this instance's period.
	 *
	 * @return This instance's period.
	 */
	public Days days() {
		return this.period;
	}

	@Override
	public int hashCode() {
		return this.period.hashCode();
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return this == obj || (obj instanceof AbstractPeriod && (this.period.equals(((AbstractPeriod) obj).period)));
	}

	@Override
	public String toString() {
		return this.period.toString();
	}

}
