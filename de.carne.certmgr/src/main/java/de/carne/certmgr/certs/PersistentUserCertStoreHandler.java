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

/**
 * {@link UserCertStoreHandler} implementation providing file based storage.
 * <p>
 * Certificate objects are organized in the following directory structure:
 *
 * <pre>
 * ./*             (store directory)
 * ./certs/*.crt   (certificate files)
 * ./crl/*.crl     (certificate revocation lists)
 * ./csr/*.csr     (certificate signing requests)
 * ./private/*.key (encrypted key files)
 * </pre>
 *
 * A certificate object's file names are determined based upon the corresponding
 * entry id's alias attributes.
 */
class PersistentUserCertStoreHandler extends UserCertStoreHandler {

	PersistentUserCertStoreHandler(Path storeHome) {
		super(storeHome);
	}

	@Override
	public UserCertStoreEntryId createEntryId(String alias) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserCertStoreEntryId nextEntryId(String aliasHint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CRTEntry createCRTEntry(UserCertStoreEntryId id, X509Certificate crt) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KeyEntry createKeyEntry(UserCertStoreEntryId id, KeyPair key, PasswordCallback password) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CSREntry createCSREntry(UserCertStoreEntryId id, PKCS10CertificateRequest csr) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CRLEntry createCRLEntry(UserCertStoreEntryId id, X509CRL crl) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
