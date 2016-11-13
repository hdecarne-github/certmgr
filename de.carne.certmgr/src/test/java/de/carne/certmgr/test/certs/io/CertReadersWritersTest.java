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
import java.security.Security;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.carne.certmgr.certs.StaticPassword;
import de.carne.certmgr.certs.io.CertReaders;
import de.carne.certmgr.certs.io.URLCertReaderInput;

/**
 * Test Certificate Readers and Writers.
 */
public class CertReadersWritersTest {

	private static final char[] TEST_PASSWORD = "password".toCharArray();

	private static final String PEM_NAME = "input.pem";

	private static final String KEYSTORE_NAME = "input.jks";

	private static final String PKCS12_NAME = "input.p12";

	/**
	 * Register BouncyCastle Provider.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Test PEM Reader and Writer.
	 */
	@Test
	public void testPEMReaderWriter() {
		try (URLCertReaderInput input = new URLCertReaderInput(getClass().getResource(PEM_NAME))) {
			List<Object> certObjects = CertReaders.read(input, StaticPassword.getInstance(TEST_PASSWORD));

			Assert.assertEquals(certObjects.size(), 2);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Test JKS Reader and Writer.
	 */
	@Test
	public void testJKSReaderWriter() {
		try (URLCertReaderInput input = new URLCertReaderInput(getClass().getResource(KEYSTORE_NAME))) {
			List<Object> certObjects = CertReaders.read(input, StaticPassword.getInstance(TEST_PASSWORD));

			Assert.assertEquals(certObjects.size(), 2);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Test PKCS#12 Reader and Writer.
	 */
	@Test
	public void testPKCS12ReaderWriter() {
		try (URLCertReaderInput input = new URLCertReaderInput(getClass().getResource(PKCS12_NAME))) {
			List<Object> certObjects = CertReaders.read(input, StaticPassword.getInstance(TEST_PASSWORD));

			Assert.assertEquals(certObjects.size(), 2);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

}
