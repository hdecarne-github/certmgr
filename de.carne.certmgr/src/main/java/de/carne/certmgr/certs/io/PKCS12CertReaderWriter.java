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
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagFactory;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;

import de.carne.certmgr.certs.CertObjectStore;
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

	private static final JcePKCSPBEInputDecryptorProviderBuilder PKCS_DECRYPTOR_PROVIDER_BUILDER = new JcePKCSPBEInputDecryptorProviderBuilder();

	private static final JcaX509CertificateConverter CRT_CONVERTER = new JcaX509CertificateConverter();

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
	public String[] fileExtensionPatterns() {
		return Strings.split(CertIOI18N.formatSTR_PKCS12_EXTENSION_PATTERNS(), "|");
	}

	@Override
	public String fileExtension(Class<?> cls) {
		return fileExtensionPatterns()[0].replace("*", "");
	}

	@Override
	@Nullable
	public CertObjectStore readBinary(IOResource<InputStream> in, PasswordCallback password) throws IOException {
		assert in != null;
		assert password != null;

		LOG.debug("Trying to read PKCS#12 objects from: ''{0}''...", in);

		CertObjectStore certObjects = null;
		PKCS12PfxPdu pkcs12 = readPKCS12(in);

		if (pkcs12 != null) {
			certObjects = new CertObjectStore();
			for (ContentInfo contentInfo : pkcs12.getContentInfos()) {
				ASN1ObjectIdentifier contentType = contentInfo.getContentType();
				PKCS12SafeBagFactory safeBagFactory;

				if (contentType.equals(PKCSObjectIdentifiers.encryptedData)) {
					safeBagFactory = getSafeBagFactory(contentInfo, in.resource(), password);
				} else {
					safeBagFactory = getSafeBagFactory(contentInfo);
				}
				for (PKCS12SafeBag safeBag : safeBagFactory.getSafeBags()) {
					Object safeBagValue = safeBag.getBagValue();

					if (safeBagValue instanceof X509CertificateHolder) {
						certObjects.addCRT(convertCRT((X509CertificateHolder) safeBagValue));
					} else if (safeBagValue instanceof PKCS8EncryptedPrivateKeyInfo) {
						certObjects.addPrivateKey(convertPrivateKey((PKCS8EncryptedPrivateKeyInfo) safeBagValue,
								in.resource(), password));
					} else if (safeBagValue instanceof PrivateKeyInfo) {
						certObjects.addPrivateKey(convertPrivateKey((PrivateKeyInfo) safeBagValue));
					} else {
						LOG.warning("Ignoring unrecognized PKCS#12 object of type {0}",
								safeBagValue.getClass().getName());
					}
				}
			}
		}
		return certObjects;
	}

	@Override
	@Nullable
	public CertObjectStore readString(IOResource<Reader> in, PasswordCallback password) throws IOException {
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
		return true;
	}

	@Override
	public void writeBinary(IOResource<OutputStream> out, CertObjectStore certObjects)
			throws IOException, UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeEncryptedBinary(IOResource<OutputStream> out, CertObjectStore certObjects,
			PasswordCallback newPassword) throws IOException, UnsupportedOperationException {
		// TODO Auto-generated method stub

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

	private static @Nullable PKCS12PfxPdu readPKCS12(IOResource<InputStream> in) {
		PKCS12PfxPdu pkcs12 = null;

		try {
			byte[] bytes = IOHelper.readBytes(in.io(), CertReader.READ_LIMIT);

			if (bytes.length > 0) {
				pkcs12 = new PKCS12PfxPdu(bytes);
			} else {
				LOG.info("Ignoring empty resource: ''{0}''", in);
			}
		} catch (IOException e) {
			LOG.info(e, "No PKCS#12 objects recognized in: ''{0}''", in);
		}
		return pkcs12;
	}

	private static InputDecryptorProvider buildInputDecryptorProvider(String resource, PasswordCallback password,
			@Nullable PKCSException decryptException) throws IOException {
		char[] passwordChars = (decryptException != null ? password.requeryPassword(resource, decryptException)
				: password.queryPassword(resource));

		if (passwordChars == null) {
			throw new PasswordRequiredException(resource, decryptException);
		}
		return PKCS_DECRYPTOR_PROVIDER_BUILDER.build(passwordChars);
	}

	private static PKCS12SafeBagFactory getSafeBagFactory(ContentInfo contentInfo, String resource,
			PasswordCallback password) throws IOException {
		PKCS12SafeBagFactory safeBagFactory = null;
		PKCSException decryptException = null;

		while (safeBagFactory == null) {
			try {
				safeBagFactory = new PKCS12SafeBagFactory(contentInfo,
						buildInputDecryptorProvider(resource, password, decryptException));
			} catch (PKCSException e) {
				decryptException = e;
			}
		}
		return safeBagFactory;
	}

	private static PKCS12SafeBagFactory getSafeBagFactory(ContentInfo contentInfo) {
		return new PKCS12SafeBagFactory(contentInfo);
	}

	private static X509Certificate convertCRT(X509CertificateHolder safeBagValue) throws IOException {
		X509Certificate crt;

		try {
			crt = CRT_CONVERTER.getCertificate(safeBagValue);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return crt;
	}

	private static PrivateKey convertPrivateKey(PKCS8EncryptedPrivateKeyInfo safeBagValue, String resource,
			PasswordCallback password) throws IOException {
		PrivateKeyInfo decryptedSafeBagValue = null;
		PKCSException decryptException = null;

		while (decryptedSafeBagValue == null) {
			try {
				decryptedSafeBagValue = safeBagValue
						.decryptPrivateKeyInfo(buildInputDecryptorProvider(resource, password, decryptException));
			} catch (PKCSException e) {
				decryptException = e;
			}
		}
		return convertPrivateKey(decryptedSafeBagValue);
	}

	private static PrivateKey convertPrivateKey(PrivateKeyInfo safeBagValue) throws IOException {
		PrivateKey privateKey;

		try {
			KeyFactory keyFactory = KeyFactory.getInstance(safeBagValue.getPrivateKeyAlgorithm().getAlgorithm().getId(),
					BouncyCastleProvider.PROVIDER_NAME);

			privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(safeBagValue.getEncoded()));
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
			throw new CertProviderException(e);
		}
		return privateKey;
	}

	@Override
	public String toString() {
		return fileType();
	}

}
