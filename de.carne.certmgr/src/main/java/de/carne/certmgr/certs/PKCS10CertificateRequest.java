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
import java.security.GeneralSecurityException;
import java.security.PublicKey;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;

/**
 * This class represents a PKCS#10 Certificate Signing Request (CSR) object.
 * <p>
 * As there is no official CSR class in the {@code java.security} package this
 * application specific class is used instead.
 * <p>
 * In the current implementation this class is wrapper around BouncyCastle's
 * {@link PKCS10CertificationRequest} class.
 */
public class PKCS10CertificateRequest {

	private final JcaPKCS10CertificationRequest csr;

	private final X500Principal subject;

	private final PublicKey publicKey;

	private PKCS10CertificateRequest(JcaPKCS10CertificationRequest csr, X500Principal subject, PublicKey publicKey) {
		this.csr = csr;
		this.subject = subject;
		this.publicKey = publicKey;
	}

	/**
	 * Construct {@code PKCS10CertificateRequest} from a PKCS#10 object.
	 *
	 * @param pkcs10 The PCKS#10 object.
	 * @return The constructed {@code PKCS10CertificateRequest}.
	 * @throws IOException if an I/O error occurs while accessing the PKCS#10
	 *         object.
	 */
	public static PKCS10CertificateRequest fromPKCS10(PKCS10CertificationRequest pkcs10) throws IOException {
		JcaPKCS10CertificationRequest csr;
		X500Principal subject;
		PublicKey publicKey;

		try {
			if (pkcs10 instanceof JcaPKCS10CertificationRequest) {
				csr = (JcaPKCS10CertificationRequest) pkcs10;
			} else {
				csr = new JcaPKCS10CertificationRequest(pkcs10);
			}
			subject = new X500Principal(csr.getSubject().getEncoded());
			publicKey = csr.getPublicKey();
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return new PKCS10CertificateRequest(csr, subject, publicKey);
	}

	/**
	 * Get this CSR's subject.
	 *
	 * @return This CSR's subject.
	 */
	public X500Principal getSubjectX500Principal() {
		return this.subject;
	}

	/**
	 * Get this CSR's public key.
	 *
	 * @return This CSR's public key.
	 */
	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	@Override
	public int hashCode() {
		return this.csr.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PKCS10CertificateRequest && this.csr.equals(((PKCS10CertificateRequest) obj).csr);
	}

	@Override
	public String toString() {
		return this.csr.toString();
	}

}
