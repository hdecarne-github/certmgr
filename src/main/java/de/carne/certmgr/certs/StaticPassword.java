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
package de.carne.certmgr.certs;

import de.carne.boot.check.Nullable;

/**
 * {@link PasswordCallback} implementation providing a static passwords.
 */
public class StaticPassword implements PasswordCallback {

	@Nullable
	private final char[] password;

	private StaticPassword(@Nullable char[] password) {
		this.password = password;
	}

	/**
	 * Get {@code StaticPassword} instance.
	 *
	 * @param password The password to return.
	 * @return {@code StaticPassword} instance.
	 */
	public static StaticPassword getInstance(@Nullable char[] password) {
		return new StaticPassword(password);
	}

	@Override
	@Nullable
	public char[] queryPassword(String resource) {
		return this.password;
	}

	@Override
	@Nullable
	public char[] requeryPassword(String resource, Throwable cause) {
		return null;
	}

}
