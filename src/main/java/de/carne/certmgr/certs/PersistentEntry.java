/*
 * Copyright (c) 2015-2019 Holger de Carne and contributors, All Rights Reserved.
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

import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;

final class PersistentEntry {

	@Nullable
	private final CertObjectHolder<X509Certificate> crtHolder;
	@Nullable
	private final SecureCertObjectHolder<KeyPair> keyHolder;
	@Nullable
	private final CertObjectHolder<PKCS10CertificateRequest> csrHolder;
	@Nullable
	private final CertObjectHolder<X509CRL> crlHolder;

	PersistentEntry(@Nullable PersistentEntry base, @Nullable CertObjectHolder<X509Certificate> crtHolder,
			@Nullable SecureCertObjectHolder<KeyPair> keyHolder,
			@Nullable CertObjectHolder<PKCS10CertificateRequest> csrHolder,
			@Nullable CertObjectHolder<X509CRL> crlHolder) {
		this.crtHolder = (crtHolder != null ? crtHolder : (base != null ? base.crtHolder : null));
		this.keyHolder = (keyHolder != null ? keyHolder : (base != null ? base.keyHolder : null));
		this.csrHolder = (csrHolder != null ? csrHolder : (base != null ? base.csrHolder : null));
		this.crlHolder = (crlHolder != null ? crlHolder : (base != null ? base.crlHolder : null));
	}

	@Nullable
	public CertObjectHolder<X509Certificate> crt() {
		return this.crtHolder;
	}

	@Nullable
	public SecureCertObjectHolder<KeyPair> key() {
		return this.keyHolder;
	}

	@Nullable
	public CertObjectHolder<PKCS10CertificateRequest> csr() {
		return this.csrHolder;
	}

	@Nullable
	public CertObjectHolder<X509CRL> crl() {
		return this.crlHolder;
	}

}
