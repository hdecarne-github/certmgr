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
package de.carne.certmgr.jfx.certimport;

import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.UserCertStoreEntry;

/**
 * Request object for importing a single certificate entry.
 */
public final class CertImportRequest {

	private final UserCertStoreEntry entry;

	private final PasswordCallback newPassword;

	CertImportRequest(UserCertStoreEntry entry, PasswordCallback newPassword) {
		this.entry = entry;
		this.newPassword = newPassword;
	}

	/**
	 * Get the certificate entry to import.
	 *
	 * @return The certificate entry to import.
	 */
	public UserCertStoreEntry entry() {
		return this.entry;
	}

	/**
	 * Get the password callback for new password queries.
	 * 
	 * @return The password callback for new password queries.
	 */
	public PasswordCallback newPassword() {
		return this.newPassword;
	}

}
