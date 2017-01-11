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

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Callback interface used to query passwords on demand.
 */
public interface PasswordCallback {

	/**
	 * This function is called whenever a password is requested for the first
	 * time.
	 *
	 * @param resource The resource requiring the password.
	 * @return The provided password, or {@code null} if the password query was
	 *         cancelled.
	 */
	@Nullable
	char[] queryPassword(String resource);

	/**
	 * This function is called whenever a password is requested again after a
	 * previously provided password was rejected.
	 *
	 * @param resource The resource requiring the password.
	 * @param cause The optional cause why the previous password was rejected.
	 * @return The provided password or {@code null} if the password query was
	 *         cancelled.
	 */
	@Nullable
	char[] requeryPassword(String resource, Throwable cause);

}
