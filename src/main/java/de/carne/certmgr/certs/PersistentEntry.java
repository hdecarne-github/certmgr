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

import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;

final class PersistentEntry {

	private final CertObjectHolder<X509Certificate> crtHolder;
	private final SecureCertObjectHolder<KeyPair> keyHolder;
	private final CertObjectHolder<PKCS10CertificateRequest> csrHolder;
	private final CertObjectHolder<X509CRL> crlHolder;

	PersistentEntry(PersistentEntry base, CertObjectHolder<X509Certificate> crtHolder,
			SecureCertObjectHolder<KeyPair> keyHolder, CertObjectHolder<PKCS10CertificateRequest> csrHolder,
			CertObjectHolder<X509CRL> crlHolder) {
		this.crtHolder = (crtHolder != null ? crtHolder : (base != null ? base.crtHolder : null));
		this.keyHolder = (keyHolder != null ? keyHolder : (base != null ? base.keyHolder : null));
		this.csrHolder = (csrHolder != null ? csrHolder : (base != null ? base.csrHolder : null));
		this.crlHolder = (crlHolder != null ? crlHolder : (base != null ? base.crlHolder : null));
	}

	public CertObjectHolder<X509Certificate> crt() {
		return this.crtHolder;
	}

	public SecureCertObjectHolder<KeyPair> key() {
		return this.keyHolder;
	}

	public CertObjectHolder<PKCS10CertificateRequest> csr() {
		return this.csrHolder;
	}

	public CertObjectHolder<X509CRL> crl() {
		return this.crlHolder;
	}

}
