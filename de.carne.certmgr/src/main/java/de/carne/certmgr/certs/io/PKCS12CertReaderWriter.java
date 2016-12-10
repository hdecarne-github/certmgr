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
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.Attribute;
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
	@Nullable
	public List<Object> read(CertReaderInput input, PasswordCallback password) throws IOException {
		assert input != null;
		assert password != null;

		LOG.debug("Trying to read PKCS#12 objects from: ''{0}''...", input);

		List<Object> pkcs12Objects = null;
		PKCS12PfxPdu pkcs12 = readPKCS12(input);

		if (pkcs12 != null) {
			pkcs12Objects = new ArrayList<>();

			KeyPairResolver<ASN1Encodable> keyPairs = new KeyPairResolver<>();

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
						X509Certificate crt = getCRT((X509CertificateHolder) safeBagValue);

						resolveKey(keyPairs, safeBag.getAttributes(), crt.getPublicKey(), null);
						pkcs12Objects.add(crt);
					} else if (safeBagValue instanceof PKCS8EncryptedPrivateKeyInfo) {
						PrivateKey privateKey = getPrivateKey((PKCS8EncryptedPrivateKeyInfo) safeBagValue,
								input.toString(), password);

						resolveKey(keyPairs, safeBag.getAttributes(), null, privateKey);
					} else if (safeBagValue instanceof PrivateKeyInfo) {
						PrivateKey privateKey = getPrivateKey((PrivateKeyInfo) safeBagValue);

						resolveKey(keyPairs, safeBag.getAttributes(), null, privateKey);
					} else {
						LOG.warning("Ignoring unrecognized PKCS#12 object of type {0}",
								safeBagValue.getClass().getName());
					}
				}
			}
			keyPairs.resolve(pkcs12Objects);
		}
		return pkcs12Objects;
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
	public void writeBinary(OutputStream out, List<Object> certObjects, String resource)
			throws IOException, UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeEncryptedBinary(OutputStream out, List<Object> certObjects, String resource,
			PasswordCallback newPassword) throws IOException, UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeString(Writer out, List<Object> certObjects, String resource)
			throws IOException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeEncryptedString(Writer out, List<Object> certObjects, String resource,
			PasswordCallback newPassword) throws IOException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Nullable
	private PKCS12PfxPdu readPKCS12(CertReaderInput input) {
		PKCS12PfxPdu pkcs12 = null;

		try (InputStream stream = input.stream()) {
			if (stream != null) {
				pkcs12 = new PKCS12PfxPdu(IOHelper.readBytes(stream, CertReader.READ_LIMIT));
			}
		} catch (IOException e) {
			LOG.info(e, "No PKCS#12 objects recognized in: ''{0}''", input);
		}
		return pkcs12;
	}

	private InputDecryptorProvider buildInputDecryptorProvider(String resource, PasswordCallback password,
			@Nullable PKCSException decryptException) throws IOException {
		char[] passwordChars = (decryptException != null ? password.requeryPassword(resource, decryptException)
				: password.queryPassword(resource));

		if (passwordChars == null) {
			throw new PasswordRequiredException(resource, decryptException);
		}
		return this.pkcsPBInputDecrypterProviderBuilder.build(passwordChars);
	}

	private PKCS12SafeBagFactory getSafeBagFactory(ContentInfo contentInfo, String resource, PasswordCallback password)
			throws IOException {
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

	private PKCS12SafeBagFactory getSafeBagFactory(ContentInfo contentInfo) {
		return new PKCS12SafeBagFactory(contentInfo);
	}

	private X509Certificate getCRT(X509CertificateHolder safeBagValue) throws IOException {
		X509Certificate crt;

		try {
			crt = this.crtConverter.getCertificate(safeBagValue);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return crt;
	}

	private PrivateKey getPrivateKey(PKCS8EncryptedPrivateKeyInfo safeBagValue, String resource,
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
		return getPrivateKey(decryptedSafeBagValue);
	}

	private PrivateKey getPrivateKey(PrivateKeyInfo safeBagValue) throws IOException {
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

	private void resolveKey(KeyPairResolver<ASN1Encodable> keyPairs, Attribute[] safeBagAttributes,
			@Nullable PublicKey publicKey, @Nullable PrivateKey privateKey) {
		for (Attribute safeBagAttribute : safeBagAttributes) {
			if (safeBagAttribute.getAttrType().equals(PKCS12SafeBag.localKeyIdAttribute)) {
				ASN1Encodable keyId = safeBagAttribute.getAttributeValues()[0];

				if (publicKey != null) {
					keyPairs.addPublicKey(keyId, publicKey);
				}
				if (privateKey != null) {
					keyPairs.addPrivateKey(keyId, privateKey);
				}
			}
		}
	}

	@Override
	public String toString() {
		return fileType();
	}

}
