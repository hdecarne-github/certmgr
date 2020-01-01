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
package de.carne.certmgr.test.certs.security;

import java.security.Security;
import java.util.Objects;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.carne.certmgr.certs.security.CRLUpdatePeriod;
import de.carne.certmgr.util.Days;
import de.carne.jfx.util.DefaultSet;

/**
 * Test {@link CRLUpdatePeriod} class functionality.
 */
public class CRLUpdatePeriodTest {

	/**
	 * Register BouncyCastle Provider.
	 */
	@BeforeClass
	public static void registerBouncyCastle() {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Test the CRL update period provisioning.
	 */
	@Test
	public void testGetDefaultSet() {
		Days days42 = new Days(42);
		DefaultSet<CRLUpdatePeriod> crlUpdatePeriods = CRLUpdatePeriod.getDefaultSet(days42);

		System.out.println("CRL Update Periods:");
		for (CRLUpdatePeriod crlUpdatePeriod : crlUpdatePeriods) {
			System.out.println(crlUpdatePeriod);
		}
		Assert.assertEquals(days42, Objects.requireNonNull(crlUpdatePeriods.getDefault()).days());
	}

}
