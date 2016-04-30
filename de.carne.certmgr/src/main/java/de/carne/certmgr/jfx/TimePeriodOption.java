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

import java.time.LocalDate;

/**
 * Class used to handle time periods (e.g. a validity period).
 */
public final class TimePeriodOption implements Comparable<TimePeriodOption> {

	/**
	 * Assumed days per month.
	 */
	public static final int DAYS_PER_MONTH = 30;

	/**
	 * Assumed days per year.
	 */
	public static final int DAYS_PER_YEAR = 365;

	private int days;

	/**
	 * Construct TimePeriodOption.
	 *
	 * @param days The amount of days the period represents.
	 */
	public TimePeriodOption(int days) {
		assert days >= 0;

		this.days = days;
	}

	/**
	 * Add a local date value to the time period and determine the resulting
	 * local date.
	 *
	 * @param date The date to add.
	 * @return The resulting date.
	 */
	public LocalDate plusLocalData(LocalDate date) {
		int daysToAdd = this.days;
		int yearsToAdd = daysToAdd / DAYS_PER_YEAR;

		daysToAdd -= yearsToAdd * DAYS_PER_YEAR;

		int monthToAdd = daysToAdd / DAYS_PER_MONTH;

		daysToAdd -= monthToAdd * DAYS_PER_MONTH;

		LocalDate result = date;

		if (yearsToAdd != 0) {
			result = result.plusYears(yearsToAdd);
		}
		if (monthToAdd != 0) {
			result = result.plusMonths(monthToAdd);
		}
		if (daysToAdd != 0) {
			result = result.plusDays(daysToAdd);
		}
		return result;
	}

	/**
	 * Get the amount of days represented by this time period.
	 *
	 * @return The amount of days represented by this time period.
	 */
	public int toDays() {
		return this.days;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.days;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof TimePeriodOption) && this.days == ((TimePeriodOption) obj).days;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String string;

		if (this.days < DAYS_PER_MONTH) {
			string = I18N.formatSTR_TIMEPERIOD_DAYS(this.days);
		} else if (this.days < DAYS_PER_YEAR) {
			string = I18N.formatSTR_TIMEPERIOD_MONTHS(this.days / DAYS_PER_MONTH);
		} else {
			string = I18N.formatSTR_TIMEPERIOD_YEARS(this.days / DAYS_PER_YEAR);
		}
		return string;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TimePeriodOption o) {
		return this.days - o.days;
	}

}
