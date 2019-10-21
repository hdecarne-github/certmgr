/*
 * Copyright (c) 2015-2019 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.util;

import java.time.Period;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Utility class used to manage a period of days.
 * <p>
 * This class is actually a simple wrapper around the JDK's {@link Period} class. It adds a {@link Comparable}
 * implementation and some convenience functions.
 */
public class Days implements Comparable<Days> {

	private static final int DAYS_PER_MONTH = 30;
	private static final int DAYS_PER_YEAR = 365;

	private final Period period;

	/**
	 * Construct {@code Days}.
	 *
	 * @param period The period to use for initialization.
	 */
	public Days(Period period) {
		this.period = period.normalized();
	}

	/**
	 * Construct {@code Days}.
	 *
	 * @param days The number of days to user for initialization.
	 */
	public Days(int days) {
		int remainder = days;
		int years = days / DAYS_PER_YEAR;

		remainder -= years * DAYS_PER_YEAR;

		int months = remainder / DAYS_PER_MONTH;

		remainder -= months * DAYS_PER_MONTH;
		this.period = Period.of(years, months, remainder);
	}

	/**
	 * Get this instance's period object.
	 *
	 * @return This instance's period object.
	 */
	public Period period() {
		return this.period;
	}

	/**
	 * Get this instance's number of days.
	 *
	 * @return This instance's number of days.
	 */
	public int count() {
		return this.period.getYears() * DAYS_PER_YEAR + this.period.getMonths() * DAYS_PER_MONTH
				+ this.period.getDays();
	}

	@Override
	public int compareTo(@Nullable Days o) {
		return count() - Objects.requireNonNull(o).count();
	}

	@Override
	public int hashCode() {
		return this.period.hashCode();
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return this == obj || (obj instanceof Days && count() == ((Days) obj).count());
	}

	@Override
	public String toString() {
		return this.period.toString();
	}

	/**
	 * Get the localized string representation of this instance.
	 *
	 * @return The localized string representation of this instance.
	 */
	public String toLocalizedString() {
		StringBuilder buffer = new StringBuilder();

		int years = this.period.getYears();

		if (years != 0) {
			buffer.append(DaysI18N.strYears(years));
		}

		int months = this.period.getMonths();

		if (months != 0) {
			if (buffer.length() > 0) {
				buffer.append(' ');
			}
			buffer.append(DaysI18N.strMonths(months));
		}

		int days = this.period.getDays();

		if (days != 0 || buffer.length() == 0) {
			if (buffer.length() > 0) {
				buffer.append(' ');
			}
			buffer.append(DaysI18N.strDays(days));
		}
		return buffer.toString();
	}

}
