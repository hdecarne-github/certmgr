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
package de.carne.certmgr.certs.io;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;

/**
 * Utility class providing the necessary functions for converting all kind of
 * objects to JCA compliant objects.
 */
abstract class JCAConversion {

	private static final JcaX509CertificateConverter CRT_CONVERTER = new JcaX509CertificateConverter();

	private static final JcaX509CRLConverter CRL_CONVERTER = new JcaX509CRLConverter();

	protected static X509Certificate convertCRT(X509CertificateHolder pemObject) throws IOException {
		X509Certificate crt;

		try {
			crt = CRT_CONVERTER.getCertificate(pemObject);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return crt;
	}

	protected static PKCS10CertificateRequest convertCSR(PKCS10CertificationRequest pemObject) throws IOException {
		return PKCS10CertificateRequest.fromPKCS10(pemObject);
	}

	protected static X509CRL convertCRL(X509CRLHolder pemObject) throws IOException {
		X509CRL crl;

		try {
			crl = CRL_CONVERTER.getCRL(pemObject);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return crl;
	}

}
