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
import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import de.carne.util.Strings;

/**
 * {@link UserCertStoreHandler} implementation providing simple heap based
 * storage.
 */
class TransientUserCertStoreHandler extends UserCertStoreHandler {

	private int nextId = 1;

	TransientUserCertStoreHandler() {
		super(null);
	}

	@Override
	public UserCertStoreEntryId createEntryId(String alias) {
		return nextEntryId(alias);
	}

	@Override
	public UserCertStoreEntryId nextEntryId(String aliasHint) {
		return new UserCertStoreEntryId(this.nextId++, Strings.safe(aliasHint));
	}

	@Override
	public CRTEntry createCRTEntry(UserCertStoreEntryId id, X509Certificate crt) throws IOException {
		return new TransientCRTEntry(crt);
	}

	private static class TransientCRTEntry extends CRTEntry {

		private final X509Certificate crt;

		TransientCRTEntry(X509Certificate crt) {
			this.crt = crt;
		}

		@Override
		public X509Certificate getCRT() throws IOException {
			return this.crt;
		}

	}

	@Override
	public KeyEntry createKeyEntry(UserCertStoreEntryId id, KeyPair key, PasswordCallback password) throws IOException {
		return new TransientKeyEntry(key);
	}

	private static class TransientKeyEntry extends KeyEntry {

		private final KeyPair key;

		TransientKeyEntry(KeyPair key) {
			this.key = key;
		}

		@Override
		public boolean isDecrypted() {
			return true;
		}

		@Override
		public KeyPair getKey(PasswordCallback password) throws IOException {
			return this.key;
		}

	}

	@Override
	public CSREntry createCSREntry(UserCertStoreEntryId id, PKCS10CertificateRequest csr) throws IOException {
		return new TransientCSREntry(csr);
	}

	private static class TransientCSREntry extends CSREntry {

		private final PKCS10CertificateRequest csr;

		TransientCSREntry(PKCS10CertificateRequest csr) {
			this.csr = csr;
		}

		@Override
		public PKCS10CertificateRequest getCSR() throws IOException {
			return this.csr;
		}

	}

	@Override
	public CRLEntry createCRLEntry(UserCertStoreEntryId id, X509CRL crl) throws IOException {
		return new TransientCRLEntry(crl);
	}

	private static class TransientCRLEntry extends CRLEntry {

		private final X509CRL crl;

		TransientCRLEntry(X509CRL crl) {
			this.crl = crl;
		}

		@Override
		public X509CRL getCRL() throws IOException {
			return this.crl;
		}

	}

}
