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
package de.carne.certmgr.test.util;

import java.time.Period;
import java.util.Locale;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.carne.certmgr.util.Days;
import de.carne.certmgr.util.DaysI18N;

/**
 * Test {@link Days} class functionality.
 */
public class DaysTest {

	/**
	 * Tests expect the default {@link Locale} {@link Locale#US}.
	 */
	@BeforeClass
	public static void initDefaultLocale() {
		Locale.setDefault(Locale.US);
	}

	/**
	 * Test {@link Days} functions.
	 */
	@Test
	public void testDays() {
		DaysI18N.strDays(0);

		Days t0Days = new Days(0);
		Days t1Day = new Days(1);
		Days t30Days = new Days(30);
		Days t365Days = new Days(365);
		Days t396Days = new Days(396);

		t0Days.hashCode();
		t1Day.hashCode();
		t30Days.hashCode();
		t365Days.hashCode();
		t396Days.hashCode();
		Assert.assertEquals(t0Days.count(), 0);
		Assert.assertEquals(t1Day.count(), 1);
		Assert.assertEquals(t30Days.count(), 30);
		Assert.assertEquals(t365Days.count(), 365);
		Assert.assertEquals(t396Days.count(), 396);
		Assert.assertEquals(t0Days.period().getDays(), 0);
		Assert.assertEquals(t0Days.period().getMonths(), 0);
		Assert.assertEquals(t0Days.period().getYears(), 0);
		Assert.assertEquals(t1Day.period().getDays(), 1);
		Assert.assertEquals(t1Day.period().getMonths(), 0);
		Assert.assertEquals(t1Day.period().getYears(), 0);
		Assert.assertEquals(t30Days.period().getDays(), 0);
		Assert.assertEquals(t30Days.period().getMonths(), 1);
		Assert.assertEquals(t30Days.period().getYears(), 0);
		Assert.assertEquals(t365Days.period().getDays(), 0);
		Assert.assertEquals(t365Days.period().getMonths(), 0);
		Assert.assertEquals(t365Days.period().getYears(), 1);
		Assert.assertEquals(t396Days.period().getDays(), 1);
		Assert.assertEquals(t396Days.period().getMonths(), 1);
		Assert.assertEquals(t396Days.period().getYears(), 1);
		Assert.assertEquals(t0Days.toString(), "P0D");
		Assert.assertEquals(t1Day.toString(), "P1D");
		Assert.assertEquals(t30Days.toString(), "P1M");
		Assert.assertEquals(t365Days.toString(), "P1Y");
		Assert.assertEquals(t396Days.toString(), "P1Y1M1D");
		Assert.assertEquals(t0Days.toLocalizedString(), "0 day(s)");
		Assert.assertEquals(t1Day.toLocalizedString(), "1 day(s)");
		Assert.assertEquals(t30Days.toLocalizedString(), "1 month(s)");
		Assert.assertEquals(t365Days.toLocalizedString(), "1 year(s)");
		Assert.assertEquals(t396Days.toLocalizedString(), "1 year(s) 1 month(s) 1 day(s)");

		Days t1DPeriod = new Days(Period.parse("P1D"));
		Days t1MPeriod = new Days(Period.parse("P1M"));
		Days t1YPeriod = new Days(Period.parse("P1Y"));

		Assert.assertEquals(t1DPeriod, t1Day);
		Assert.assertEquals(t1MPeriod, t30Days);
		Assert.assertEquals(t1YPeriod, t365Days);

		Assert.assertFalse(t30Days.equals(this));
		Assert.assertFalse(t30Days.equals(t1Day));
		Assert.assertTrue(t30Days.equals(t30Days));
		Assert.assertFalse(t30Days.equals(t365Days));
		Assert.assertTrue(t30Days.compareTo(t1Day) > 0);
		Assert.assertTrue(t30Days.compareTo(t30Days) == 0);
		Assert.assertTrue(t30Days.compareTo(t365Days) < 0);
	}

}
