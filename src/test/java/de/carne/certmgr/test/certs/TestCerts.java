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
package de.carne.certmgr.test.certs;

import java.net.URL;

import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.StaticPassword;

/**
 * Certificate test data.
 */
public class TestCerts {

	private static final char[] TEST_PASSWORD = "password".toCharArray();

	private static final String SIMPLE_PEM_NAME = "simple.pem";

	private static final String SIMPLE_KEYSTORE_NAME = "simple.jks";

	private static final String SIMPLE_PKCS12_NAME = "simple.p12";

	private static final String TEST_STORE_ZIP_NAME = "test.domain.zip";

	/**
	 * @return The password callback for test data access.
	 */
	public static PasswordCallback password() {
		return StaticPassword.getInstance(TEST_PASSWORD);
	}

	/**
	 * @return Simple JKS data URL.
	 */
	public static URL simpleJKSURL() {
		return TestCerts.class.getResource(SIMPLE_KEYSTORE_NAME);
	}

	/**
	 * @return Simple PEM data URL.
	 */
	public static URL simplePEMURL() {
		return TestCerts.class.getResource(SIMPLE_PEM_NAME);
	}

	/**
	 * @return Simple PKCS#12 data URL.
	 */
	public static URL simplePKCS12URL() {
		return TestCerts.class.getResource(SIMPLE_PKCS12_NAME);
	}

	/**
	 * Test store name.
	 */
	public static final String TEST_STORE_NAME = "test.domain";

	/**
	 * @return Test store ZIP archive.
	 */
	public static URL testStoreZIPURL() {
		return TestCerts.class.getResource(TEST_STORE_ZIP_NAME);
	}

}
