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
package de.carne.certmgr.test.util;

import org.junit.Assert;
import org.junit.Test;

import de.carne.util.DefaultSet;

/**
 * Test {@link DefaultSet} class functionality.
 */
public class DefaultSetTest {

	/**
	 * Test {@link DefaultSet} class functionality.
	 */
	@Test
	public void testDefaultSet() {
		DefaultSet<String> defaults1 = new DefaultSet<>();

		defaults1.add("A");
		Assert.assertEquals("A", defaults1.getDefault());
		defaults1.addDefault("B");
		Assert.assertEquals("B", defaults1.getDefault());
		defaults1.add("C");
		Assert.assertEquals("B", defaults1.getDefault());

		DefaultSet<String> defaults2 = new DefaultSet<>(defaults1);

		Assert.assertEquals(defaults2.size(), defaults1.size());

		defaults1.clear();

		Assert.assertNotEquals(defaults2.size(), defaults1.size());
	}

}
