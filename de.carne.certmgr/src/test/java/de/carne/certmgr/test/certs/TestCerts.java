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
package de.carne.certmgr.test.certs;

import java.net.URL;

import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.StaticPassword;

/**
 * Certificate test data.
 */
public class TestCerts {

	private static final char[] TEST_PASSWORD = "password".toCharArray();

	private static final String PEM_NAME = "input.pem";

	private static final String KEYSTORE_NAME = "input.jks";

	private static final String PKCS12_NAME = "input.p12";

	/**
	 * @return The password callback for test data access.
	 */
	public static PasswordCallback password() {
		return StaticPassword.getInstance(TEST_PASSWORD);
	}

	/**
	 * @return JKS data URL.
	 */
	public static URL jksInputURL() {
		return TestCerts.class.getResource(KEYSTORE_NAME);
	}

	/**
	 * @return PEM data URL.
	 */
	public static URL pemInputURL() {
		return TestCerts.class.getResource(PEM_NAME);
	}

	/**
	 * @return PKCS#12 data URL.
	 */
	public static URL pkcs12InputURL() {
		return TestCerts.class.getResource(PKCS12_NAME);
	}

}
