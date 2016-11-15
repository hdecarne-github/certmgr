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
package de.carne.certmgr.test.certs.x500;

import javax.security.auth.x500.X500Principal;

import org.junit.Assert;
import org.junit.Test;

import de.carne.certmgr.certs.x500.X500Names;

/**
 * Test {@link X500Names} class functionality.
 */
public class X500NamesTest {

	private static final String DN_TEST1 = "CN=Test,OU=Test.Org,EMAILADDRESS=info@test.org,SERIALNUMBER=42";

	/**
	 * Test {@link X500Names} class functionality.
	 */
	@Test
	public void testX500Names() {
		System.getProperties().put("de.carne.certmgr.certs.x500", "./nofile.properties");

		X500Principal principal = new X500Principal(DN_TEST1);

		Assert.assertEquals(DN_TEST1, X500Names.toString(principal));
	}

}
