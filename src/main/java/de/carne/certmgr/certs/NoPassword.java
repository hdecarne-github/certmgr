/*
 * Copyright (c) 2015-2017 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.certs;

import de.carne.check.Nullable;

/**
 * {@link PasswordCallback} implementation providing no passwords.
 */
public class NoPassword implements PasswordCallback {

	private NoPassword() {
		// Make sure this class is not instantiated from outside
	}

	private static final NoPassword INSTANCE = new NoPassword();

	/**
	 * Get {@code NoPassword} instance.
	 *
	 * @return {@code NoPassword} instance.
	 */
	public static NoPassword getInstance() {
		return INSTANCE;
	}

	@Override
	@Nullable
	public char[] queryPassword(String resource) {
		return null;
	}

	@Override
	@Nullable
	public char[] requeryPassword(String resource, Throwable cause) {
		return null;
	}

}
