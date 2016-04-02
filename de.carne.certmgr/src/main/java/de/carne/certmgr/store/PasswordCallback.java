/*
 * Copyright (c) 2014-2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.store;

/**
 * Callback interface used to provide password access.
 */
public interface PasswordCallback {

	/**
	 * This function is called whenever a password is requested for the first time.
	 *
	 * @param resource The resource requiring a password.
	 * @return The entered password or null if no password is available.
	 */
	public String queryPassword(String resource);

	/**
	 * This function is called whenever a password is requested after a previously given password failed.
	 *
	 * @param resource The resource requiring a password.
	 * @param details An optional exception providing details about the previous password failure.
	 * @return The entered password or null if no password is available.
	 */
	public String requeryPassword(String resource, Exception details);

}
