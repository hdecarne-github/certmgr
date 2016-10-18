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
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.EncryptionException;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.PKCS10CertificateRequest;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.PasswordRequiredException;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.util.Strings;
import de.carne.util.logging.Log;

/**
 * PEM file support.
 */
public class PEMCertReaderWriter implements CertReader {

	private static final Log LOG = new Log();

	/**
	 * Provider name.
	 */
	public static final String PROVIDER_NAME = "PEM";

	private final JcaX509CertificateConverter crtConverter = new JcaX509CertificateConverter();

	private final JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter();

	private final JcePEMDecryptorProviderBuilder pemCecryptorBuilder = new JcePEMDecryptorProviderBuilder();

	private final JcaX509CRLConverter crlConverter = new JcaX509CRLConverter();

	@Override
	public String providerName() {
		return PROVIDER_NAME;
	}

	@Override
	public String fileType() {
		return CertIOI18N.formatSTR_PEM_TYPE();
	}

	@Override
	public String[] fileExtensions() {
		return Strings.split(CertIOI18N.formatSTR_PEM_EXTENSIONS(), "|");
	}

	@Override
	public List<Object> read(CertReaderInput input, PasswordCallback password) throws IOException {
		List<Object> pemObjects = null;

		LOG.debug("Trying to read PEM objects from input ''{0}''...", input);
		try (PEMParser parser = new PEMParser(input.reader(StandardCharsets.US_ASCII))) {
			Object pemObject;

			try {
				pemObject = parser.readObject();
				pemObjects = new ArrayList<>();
			} catch (IOException e) {
				LOG.info(e, "No PEM objects recognized in input ''{0}''", input);
				pemObject = null;
			}
			while (pemObject != null) {
				assert pemObjects != null;

				LOG.debug("Decoding PEM object of type {0}", pemObject.getClass().getName());
				if (pemObject instanceof X509CertificateHolder) {
					pemObjects.add(getCRT((X509CertificateHolder) pemObject));
				} else if (pemObject instanceof PEMKeyPair) {
					pemObjects.add(getKey((PEMKeyPair) pemObject));
				} else if (pemObject instanceof PEMEncryptedKeyPair) {
					pemObjects.add(getKey((PEMEncryptedKeyPair) pemObject, input.toString(), password));
				} else if (pemObject instanceof PKCS10CertificationRequest) {
					pemObjects.add(getCSR((PKCS10CertificationRequest) pemObject));
				} else if (pemObject instanceof X509CRLHolder) {
					pemObjects.add(getCRL((X509CRLHolder) pemObject));
				} else {
					LOG.warning("Ignoring unrecognized PEM object of type {0}", pemObject.getClass().getName());
				}
				pemObject = parser.readObject();
			}
		}
		return pemObjects;
	}

	private X509Certificate getCRT(X509CertificateHolder pemObject) throws IOException {
		X509Certificate crt;

		try {
			crt = this.crtConverter.getCertificate(pemObject);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return crt;
	}

	private KeyPair getKey(PEMKeyPair pemObject) throws IOException {
		return this.keyConverter.getKeyPair(pemObject);
	}

	private KeyPair getKey(PEMEncryptedKeyPair pemObject, String resource, PasswordCallback password)
			throws IOException {
		PEMKeyPair pemKeyPair = null;
		char[] passwordChars = password.queryPassword(resource);
		Throwable passwordException = null;

		while (pemKeyPair == null) {
			if (passwordChars == null) {
				throw new PasswordRequiredException(resource, passwordException);
			}

			PEMDecryptorProvider pemDecryptorProvider = this.pemCecryptorBuilder.build(passwordChars);

			try {
				pemKeyPair = pemObject.decryptKeyPair(pemDecryptorProvider);
			} catch (EncryptionException e) {
				passwordException = e;
			}
			if (pemKeyPair == null) {
				passwordChars = password.requeryPassword(resource, passwordException);
			}
		}
		return getKey(pemKeyPair);
	}

	private PKCS10CertificateRequest getCSR(PKCS10CertificationRequest pemObject) throws IOException {
		return PKCS10CertificateRequest.fromPKCS10(pemObject);
	}

	private X509CRL getCRL(X509CRLHolder pemObject) throws IOException {
		X509CRL crl;

		try {
			crl = this.crlConverter.getCRL(pemObject);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return crl;
	}

}
