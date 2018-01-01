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
package de.carne.certmgr.test;

import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.StaticPassword;

/**
 * Common test data and functions.
 */
public final class Tests {

	private static final char[] TEST_PASSWORD = "password".toCharArray();

	/**
	 * @return The password callback for test data access.
	 */
	public static PasswordCallback password() {
		return StaticPassword.getInstance(TEST_PASSWORD);
	}

}
