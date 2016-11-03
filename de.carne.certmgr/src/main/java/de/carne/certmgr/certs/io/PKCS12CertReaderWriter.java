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
import java.io.InterruptedIOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagFactory;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;

import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.PasswordRequiredException;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.io.IOHelper;
import de.carne.util.Strings;
import de.carne.util.logging.Log;

/**
 * PKCS#12 I/O support.
 */
public class PKCS12CertReaderWriter implements CertReader, CertWriter {

	private static final Log LOG = new Log();

	private JcePKCSPBEInputDecryptorProviderBuilder pkcsPBInputDecrypterProviderBuilder = new JcePKCSPBEInputDecryptorProviderBuilder();

	private final JcaX509CertificateConverter crtConverter = new JcaX509CertificateConverter();

	private final JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter();

	private final JcaX509CRLConverter crlConverter = new JcaX509CRLConverter();

	/**
	 * Provider name.
	 */
	public static final String PROVIDER_NAME = "PKCS12";

	@Override
	public String providerName() {
		return PROVIDER_NAME;
	}

	@Override
	public String fileType() {
		return CertIOI18N.formatSTR_PKCS12_TYPE();
	}

	@Override
	public String[] fileExtensions() {
		return Strings.split(CertIOI18N.formatSTR_PKCS12_EXTENSIONS(), "|");
	}

	@Override
	public List<Object> read(CertReaderInput input, PasswordCallback password) throws IOException {
		List<Object> pkcs12Objects = null;
		PKCS12PfxPdu pkcs12 = readPKCS12(input);

		if (pkcs12 != null) {
			pkcs12Objects = new ArrayList<>();
			for (ContentInfo contentInfo : pkcs12.getContentInfos()) {
				ASN1ObjectIdentifier contentType = contentInfo.getContentType();
				PKCS12SafeBagFactory safeBagFactory;

				if (contentType.equals(PKCSObjectIdentifiers.encryptedData)) {
					safeBagFactory = getSafeBagFactory(contentInfo, input.toString(), password);
				} else {
					safeBagFactory = getSafeBagFactory(contentInfo);
				}
				for (PKCS12SafeBag safeBag : safeBagFactory.getSafeBags()) {
					Object safeBagValue = safeBag.getBagValue();

					if (safeBagValue instanceof X509CertificateHolder) {
						pkcs12Objects.add(getCRT((X509CertificateHolder) safeBagValue));
					} else if (safeBagValue instanceof PKCS8EncryptedPrivateKeyInfo) {

					} else if (safeBagValue instanceof PrivateKeyInfo) {

					} else {
						LOG.warning("Ignoring unrecognized PKCS#12 object of type {0}",
								safeBagValue.getClass().getName());
					}
				}
			}
		}
		return pkcs12Objects;
	}

	private PKCS12PfxPdu readPKCS12(CertReaderInput input) throws IOException {
		PKCS12PfxPdu pkcs12 = null;

		try (InputStream stream = input.stream()) {
			pkcs12 = new PKCS12PfxPdu(IOHelper.readBytes(stream, CertReader.READ_LIMIT));
		} catch (InterruptedIOException e) {
			LOG.notice(CertIOI18N.formatSTR_MESSAGE_READ_LIMIT_REACHED(PROVIDER_NAME, input,
					NumberFormat.getInstance().format(CertReader.READ_LIMIT)));
		}
		return pkcs12;
	}

	private PKCS12SafeBagFactory getSafeBagFactory(ContentInfo contentInfo, String resource, PasswordCallback password)
			throws IOException {
		PKCS12SafeBagFactory safeBagFactory = null;
		PKCSException decryptException = null;

		while (safeBagFactory == null) {
			char[] passwordChars = (decryptException != null ? password.queryPassword(resource)
					: password.requeryPassword(resource, decryptException));

			if (passwordChars == null) {
				throw new PasswordRequiredException(resource, decryptException);
			}

			InputDecryptorProvider inputDecryptorProvider = this.pkcsPBInputDecrypterProviderBuilder
					.build(passwordChars);

			try {
				safeBagFactory = new PKCS12SafeBagFactory(contentInfo, inputDecryptorProvider);
			} catch (PKCSException e) {
				decryptException = e;
			}
		}
		return safeBagFactory;
	}

	private PKCS12SafeBagFactory getSafeBagFactory(ContentInfo contentInfo) {
		return new PKCS12SafeBagFactory(contentInfo);
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

}
