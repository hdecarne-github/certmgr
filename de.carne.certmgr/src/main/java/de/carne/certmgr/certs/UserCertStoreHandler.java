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
package de.carne.certmgr.certs;

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;

/**
 * {@code UserCertStoreHandler} derived classes provide the actual certificate
 * storage functions used by a certificate store.
 * <p>
 * Different handler implementations allow different kind of stores to be
 * provided via a single store interface ({@link UserCertStore}).
 */
abstract class UserCertStoreHandler {

	private final Path storeHome;

	UserCertStoreHandler(Path storeHome) {
		this.storeHome = storeHome;
	}

	public final Path storeHome() {
		return this.storeHome;
	}

	public abstract UserCertStoreEntryId nextEntryId(String aliasHint);

	public abstract CRTEntry createCRTEntry(UserCertStoreEntryId id, X509Certificate crt) throws IOException;

	public abstract KeyEntry createKeyEntry(UserCertStoreEntryId id, KeyPair key, PasswordCallback password)
			throws IOException;

	public abstract CSREntry createCSREntry(UserCertStoreEntryId id, PKCS10CertificateRequest csr) throws IOException;

	public abstract CRLEntry createCRLEntry(UserCertStoreEntryId id, X509CRL crl) throws IOException;

	public abstract void deleteEntry(UserCertStoreEntryId id) throws IOException;

}
