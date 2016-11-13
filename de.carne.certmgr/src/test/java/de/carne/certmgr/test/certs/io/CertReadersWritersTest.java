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
package de.carne.certmgr.test.certs.io;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.carne.certmgr.certs.StaticPassword;
import de.carne.certmgr.certs.io.CertReaders;
import de.carne.certmgr.certs.io.URLCertReaderInput;

/**
 * Test Certificate Readers and Writers.
 */
public class CertReadersWritersTest {

	private static final char[] TEST_PASSWORD = "password".toCharArray();

	private static final String KEYSTORE_NAME = "keystore.jks";

	/**
	 * Test Certificate Readers and Writers.
	 */
	@Test
	public void testCertReadersWriters() {
		try (URLCertReaderInput input = new URLCertReaderInput(getClass().getResource(KEYSTORE_NAME))) {
			List<Object> certObjects = CertReaders.read(input, StaticPassword.getInstance(TEST_PASSWORD));

			Assert.assertEquals(certObjects.size(), 0);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

}
