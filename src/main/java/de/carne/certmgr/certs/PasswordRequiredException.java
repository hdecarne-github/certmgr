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

import java.io.IOException;

import de.carne.check.Nullable;

/**
 * Exception indicating that no valid password was given while accessing an encrypted resource.
 */
public class PasswordRequiredException extends IOException {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 8506291648565012681L;

	/**
	 * Construct {@code PasswordRequiredException}.
	 *
	 * @param resource To resource requiring the password.
	 */
	public PasswordRequiredException(String resource) {
		super(formatMessage(resource));
	}

	/**
	 * Construct {@code PasswordRequiredException}.
	 *
	 * @param resource To resource requiring the password.
	 * @param cause The cause while the password was rejected (may be {@code null}).
	 */
	public PasswordRequiredException(String resource, @Nullable Throwable cause) {
		super(formatMessage(resource), cause);
	}

	private static String formatMessage(String resource) {
		return UserCertStoreI18N.formatSTR_MESSAGE_PASSWORD_REQUIRED(resource);
	}

}
