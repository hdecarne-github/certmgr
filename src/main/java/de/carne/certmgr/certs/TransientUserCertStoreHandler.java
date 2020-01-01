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
import de.carne.util.Strings;

/**
 * {@link UserCertStoreHandler} implementation providing simple heap based storage.
 */
class TransientUserCertStoreHandler extends UserCertStoreHandler {

	private int nextId = 1;

	@Override
	public UserCertStoreEntryId nextEntryId(@Nullable String aliasHint) {
		return new UserCertStoreEntryId(this.nextId++, Strings.safe(aliasHint));
	}

	@Override
	public CertObjectHolder<X509Certificate> createCRT(UserCertStoreEntryId id, X509Certificate crt)
			throws IOException {
		return new TransientCertObjectHolder<>(crt);
	}

	@Override
	public SecureCertObjectHolder<KeyPair> createKey(UserCertStoreEntryId id, KeyPair key, PasswordCallback password)
			throws IOException {
		return new TransientCertObjectHolder<>(key);
	}

	@Override
	public CertObjectHolder<PKCS10CertificateRequest> createCSR(UserCertStoreEntryId id, PKCS10CertificateRequest csr)
			throws IOException {
		return new TransientCertObjectHolder<>(csr);
	}

	@Override
	public CertObjectHolder<X509CRL> createCRL(UserCertStoreEntryId id, X509CRL crl) throws IOException {
		return new TransientCertObjectHolder<>(crl);
	}

	@Override
	public void deleteEntry(UserCertStoreEntryId id) throws IOException {
		// Nothing to do here
	}

	private static class TransientCertObjectHolder<T> implements SecureCertObjectHolder<T> {

		private final T object;

		TransientCertObjectHolder(T object) {
			this.object = object;
		}

		@Override
		@Nullable
		public Path path() {
			return null;
		}

		@Override
		public T get() throws IOException {
			return this.object;
		}

		@Override
		public boolean isSecured() {
			return false;
		}

		@Override
		public T get(PasswordCallback password) throws IOException {
			return get();
		}

	}

}
