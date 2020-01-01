/*
 * Copyright (c) 2015-2020 Holger de Carne and contributors, All Rights Reserved.
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

import org.eclipse.jdt.annotation.Nullable;

import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;

/**
 * {@code UserCertStoreHandler} derived classes provide the actual certificate storage functions used by a certificate
 * store.
 * <p>
 * Different handler implementations allow different kind of stores to be provided via a single store interface
 * ({@link UserCertStore}).
 */
abstract class UserCertStoreHandler {

	@Nullable
	public Path storeHome() {
		return null;
	}

	public abstract UserCertStoreEntryId nextEntryId(@Nullable String aliasHint);

	public abstract CertObjectHolder<X509Certificate> createCRT(UserCertStoreEntryId id, X509Certificate crt)
			throws IOException;

	public abstract SecureCertObjectHolder<KeyPair> createKey(UserCertStoreEntryId id, KeyPair key,
			PasswordCallback newPassword) throws IOException;

	public abstract CertObjectHolder<PKCS10CertificateRequest> createCSR(UserCertStoreEntryId id,
			PKCS10CertificateRequest csr) throws IOException;

	public abstract CertObjectHolder<X509CRL> createCRL(UserCertStoreEntryId id, X509CRL crl) throws IOException;

	public abstract void deleteEntry(UserCertStoreEntryId id) throws IOException;

}
