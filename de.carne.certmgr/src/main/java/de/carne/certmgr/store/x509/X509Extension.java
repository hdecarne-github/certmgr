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
package de.carne.certmgr.store.x509;

import de.carne.certmgr.store.asn1.ASN1Encodable;

/**
 * Base class for all kind of X.509 certificate extensions.
 */
public abstract class X509Extension implements ASN1Encodable {

	private String oid;
	private boolean critical;

	/**
	 * Construct CertificateExtension.
	 *
	 * @param oid The extension's oid.
	 * @param critical The extension's critical flag.
	 */
	public X509Extension(String oid, boolean critical) {
		assert oid != null;

		this.oid = oid;
		this.critical = critical;
	}

	/**
	 * Get the extension's oid.
	 *
	 * @return The extension's oid.
	 */
	public String getOID() {
		return this.oid;
	}

	/**
	 * Get the extension's critical flag.
	 *
	 * @return The extension's critical flag.
	 */
	public boolean isCritical() {
		return this.critical;
	}

}
