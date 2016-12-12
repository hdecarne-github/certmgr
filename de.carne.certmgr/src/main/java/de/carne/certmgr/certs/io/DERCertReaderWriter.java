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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.checkerframework.checker.nullness.qual.Nullable;

import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;
import de.carne.util.Exceptions;
import de.carne.util.Strings;
import de.carne.util.logging.Log;

/**
 * DER I/O support.
 */
public class DERCertReaderWriter implements CertReader, CertWriter {

	private static final Log LOG = new Log();

	private static final JcaX509CertificateConverter CRT_CONVERTER = new JcaX509CertificateConverter();

	private static final JcaX509CRLConverter CRL_CONVERTER = new JcaX509CRLConverter();

	/**
	 * Provider name.
	 */
	public static final String PROVIDER_NAME = "DER";

	@Override
	public String providerName() {
		return PROVIDER_NAME;
	}

	@Override
	public String fileType() {
		return CertIOI18N.formatSTR_DER_TYPE();
	}

	@Override
	public String[] fileExtensions() {
		return Strings.split(CertIOI18N.formatSTR_DER_EXTENSIONS(), "|");
	}

	@Override
	public @Nullable List<Object> readBinary(IOResource<InputStream> in, PasswordCallback password) throws IOException {
		assert in != null;
		assert password != null;

		LOG.debug("Trying to read DER objects from: ''{0}''...", in);

		List<Object> certObjects = null;

		try (ASN1InputStream asn1Stream = new ASN1InputStream(in.io())) {
			ASN1Primitive asn1Object;

			while ((asn1Object = asn1Stream.readObject()) != null) {
				if (certObjects == null) {
					certObjects = new ArrayList<>();
				}

				X509Certificate crt = decodeCRT(asn1Object);

				if (crt != null) {
					certObjects.add(crt);
					continue;
				}

				PKCS10CertificateRequest csr = decodeCSR(asn1Object);

				if (csr != null) {
					certObjects.add(csr);
					continue;
				}

				X509CRL crl = decodeCRL(asn1Object);

				if (crl != null) {
					certObjects.add(crl);
					continue;
				}
			}
		}
		return certObjects;
	}

	@Override
	public @Nullable List<Object> readString(IOResource<Reader> in, PasswordCallback password) throws IOException {
		return null;
	}

	@Override
	public boolean isCharWriter() {
		return false;
	}

	@Override
	public boolean isContainerWriter() {
		return true;
	}

	@Override
	public boolean isEncryptionRequired() {
		return false;
	}

	@Override
	public void writeBinary(IOResource<OutputStream> out, List<Object> certObjects)
			throws IOException, UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeEncryptedBinary(IOResource<OutputStream> out, List<Object> certObjects,
			PasswordCallback newPassword) throws IOException, UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeString(IOResource<Writer> out, List<Object> certObjects)
			throws IOException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeEncryptedString(IOResource<Writer> out, List<Object> certObjects, PasswordCallback newPassword)
			throws IOException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	private static X509Certificate decodeCRT(ASN1Primitive asn1Object) throws IOException {
		X509CertificateHolder crtObject = null;

		try {
			crtObject = new X509CertificateHolder(asn1Object.getEncoded());
		} catch (Exception e) {
			Exceptions.ignore(e);
		}
		return (crtObject != null ? convertCRT(crtObject) : null);
	}

	private static PKCS10CertificateRequest decodeCSR(ASN1Primitive asn1Object) throws IOException {
		PKCS10CertificationRequest csrObject = null;

		try {
			csrObject = new PKCS10CertificationRequest(asn1Object.getEncoded());
		} catch (Exception e) {
			Exceptions.ignore(e);
		}
		return (csrObject != null ? convertCSR(csrObject) : null);
	}

	private static X509CRL decodeCRL(ASN1Primitive asn1Object) throws IOException {
		X509CRLHolder crlObject = null;

		try {
			crlObject = new X509CRLHolder(asn1Object.getEncoded());
		} catch (Exception e) {
			Exceptions.ignore(e);
		}
		return (crlObject != null ? convertCRL(crlObject) : null);
	}

	private static X509Certificate convertCRT(X509CertificateHolder pemObject) throws IOException {
		X509Certificate crt;

		try {
			crt = CRT_CONVERTER.getCertificate(pemObject);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return crt;
	}

	private static PKCS10CertificateRequest convertCSR(PKCS10CertificationRequest pemObject) throws IOException {
		return PKCS10CertificateRequest.fromPKCS10(pemObject);
	}

	private static X509CRL convertCRL(X509CRLHolder pemObject) throws IOException {
		X509CRL crl;

		try {
			crl = CRL_CONVERTER.getCRL(pemObject);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return crl;
	}

	@Override
	public String toString() {
		return fileType();
	}

}
