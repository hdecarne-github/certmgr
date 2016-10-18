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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.PasswordCallback;
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

	private final JcaX509CertificateConverter converter = new JcaX509CertificateConverter();

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
				if (pemObject instanceof PEMKeyPair || pemObject instanceof PEMEncryptedKeyPair) {

				} else if (pemObject instanceof X509CertificateHolder) {
					pemObjects.add(getCRT((X509CertificateHolder) pemObject));
				} else if (pemObject instanceof X509CRLHolder) {

				} else if (pemObject instanceof PKCS10CertificationRequest) {

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
			crt = this.converter.getCertificate(pemObject);
		} catch (CertificateException e) {
			throw new CertProviderException(e);
		}
		return crt;
	}

}
