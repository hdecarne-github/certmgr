/*
 * Copyright (c) 2015-2018 Holger de Carne and contributors, All Rights Reserved.
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

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.carne.certmgr.certs.security.CRTValidityPeriod;
import de.carne.certmgr.util.Days;
import de.carne.check.Check;
import de.carne.util.DefaultSet;

/**
 * Test {@link CRTValidityPeriod} class functionality.
 */
public class CRRValidityPeriodTest {

	/**
	 * Register BouncyCastle Provider.
	 */
	@BeforeClass
	public static void registerBouncyCastle() {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Test the CRT validity period provisioning.
	 */
	@Test
	public void testGetDefaultSet() {
		Days days42 = new Days(42);
		DefaultSet<CRTValidityPeriod> crtValidityPeriods = CRTValidityPeriod.getDefaultSet(days42);

		System.out.println("CRR Validity Periods:");
		for (CRTValidityPeriod crlValidityPeriod : crtValidityPeriods) {
			System.out.println(crlValidityPeriod);
		}
		Assert.assertEquals(days42, Check.nonNull(crtValidityPeriods.getDefault()).days());
	}

}
