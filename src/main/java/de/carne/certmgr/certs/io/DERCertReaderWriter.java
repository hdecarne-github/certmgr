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
package de.carne.certmgr.certs.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import de.carne.certmgr.certs.CertObjectStore;
import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.PasswordRequiredException;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;
import de.carne.util.Exceptions;
import de.carne.util.Strings;
import de.carne.util.logging.Log;

/**
 * DER read/write support.
 */
public class DERCertReaderWriter extends JCAConversion implements CertReader, CertWriter {

	private static final Log LOG = new Log(CertIOI18N.BUNDLE);

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
	public String[] fileExtensionPatterns() {
		return Strings.split(CertIOI18N.formatSTR_DER_EXTENSION_PATTERNS(), "|");
	}

	@Override
	public String fileExtension(Class<?> cls) {
		String extension;

		if (X509Certificate.class.isAssignableFrom(cls)) {
			extension = ".crt";
		} else if (KeyPair.class.isAssignableFrom(cls)) {
			extension = ".key";
		} else if (PKCS10CertificateRequest.class.isAssignableFrom(cls)) {
			extension = ".csr";
		} else if (X509CRL.class.isAssignableFrom(cls)) {
			extension = ".crl";
		} else {
			extension = fileExtensionPatterns()[0].replace("*", "");
		}
		return extension;
	}

	@Override
	public CertObjectStore readBinary(IOResource<InputStream> in, PasswordCallback password) throws IOException {
		assert in != null;
		assert password != null;

		LOG.debug("Trying to read DER objects from: ''{0}''...", in);

		CertObjectStore certObjects = null;

		try (ASN1InputStream derStream = new ASN1InputStream(in.io())) {
			ASN1Primitive derObject;

			while ((derObject = derStream.readObject()) != null) {
				if (certObjects == null) {
					certObjects = new CertObjectStore();
				}

				X509Certificate crt = decodeCRT(derObject);

				if (crt != null) {
					certObjects.addCRT(crt);
					continue;
				}

				PKCS10CertificateRequest csr = decodeCSR(derObject);

				if (csr != null) {
					certObjects.addCSR(csr);
					continue;
				}

				X509CRL crl = decodeCRL(derObject);

				if (crl != null) {
					certObjects.addCRL(crl);
					continue;
				}

				LOG.warning(CertIOI18N.STR_DER_UNKNOWN_OBJECT, derObject.getClass().getName());
			}
		}
		return certObjects;
	}

	@Override
	public CertObjectStore readString(IOResource<Reader> in, PasswordCallback password) throws IOException {
		return null;
	}

	@Override
	public boolean isCharWriter() {
		return false;
	}

	@Override
	public boolean isEncryptionRequired() {
		return false;
	}

	@Override
	public void writeBinary(IOResource<OutputStream> out, CertObjectStore certObjects)
			throws IOException, UnsupportedOperationException {
		for (CertObjectStore.Entry certObject : certObjects) {
			writeCertObject(out, certObject);
		}
	}

	@Override
	public void writeEncryptedBinary(IOResource<OutputStream> out, CertObjectStore certObjects,
			PasswordCallback newPassword) throws IOException, UnsupportedOperationException {
		for (CertObjectStore.Entry certObject : certObjects) {
			writeEncryptedCertObject(out, certObject, newPassword);
		}
	}

	@Override
	public void writeString(IOResource<Writer> out, CertObjectStore certObjects)
			throws IOException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeEncryptedString(IOResource<Writer> out, CertObjectStore certObjects, PasswordCallback newPassword)
			throws IOException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	private static void writeCertObject(IOResource<OutputStream> out, CertObjectStore.Entry storeEntry)
			throws IOException {
		LOG.debug("Writing DER object ''{0}'' to resource ''{1}''...", storeEntry, out);

		try {
			switch (storeEntry.type()) {
			case CRT:
				out.io().write(storeEntry.getCRT().getEncoded());
				break;
			case KEY:
				break;
			case CSR:
				out.io().write(storeEntry.getCSR().toPKCS10().getEncoded());
				break;
			case CRL:
				out.io().write(storeEntry.getCRL().getEncoded());
				break;
			}
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
	}

	private static void writeEncryptedCertObject(IOResource<OutputStream> out, CertObjectStore.Entry storeEntry,
			PasswordCallback newPassword) throws IOException {
		LOG.debug("Writing encrypted DER object ''{0}'' to resource ''{1}''...", storeEntry, out);

		char[] passwordChars = newPassword.queryPassword(out.resource());

		if (passwordChars == null) {
			throw new PasswordRequiredException(out.resource());
		}

		try {
			switch (storeEntry.type()) {
			case CRT:
				out.io().write(storeEntry.getCRT().getEncoded());
				break;
			case KEY:
				break;
			case CSR:
				out.io().write(storeEntry.getCSR().toPKCS10().getEncoded());
				break;
			case CRL:
				out.io().write(storeEntry.getCRL().getEncoded());
				break;
			}
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
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

	@Override
	public String toString() {
		return fileType();
	}

}
