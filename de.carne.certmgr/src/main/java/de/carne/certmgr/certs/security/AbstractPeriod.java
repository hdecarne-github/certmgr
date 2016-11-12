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
package de.carne.certmgr.certs.security;

import de.carne.certmgr.util.Days;

abstract class AbstractPeriod {

	private final Days days;

	protected AbstractPeriod(Days validity) {
		this.days = validity;
	}

	/**
	 * Get this instance's period.
	 *
	 * @return This instance's period.
	 */
	public Days days() {
		return this.days;
	}

	@Override
	public int hashCode() {
		return this.days.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj instanceof AbstractPeriod && (this.days.equals(((AbstractPeriod) obj).days)));
	}

	@Override
	public String toString() {
		return this.days.toLocalizedString() + " [" + this.days.toString() + "]";
	}

}
