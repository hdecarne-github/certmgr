/*
 * Copyright (c) 2015-2021 Holger de Carne and contributors, All Rights Reserved.
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
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBagFactory;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;
import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.logging.Log;
import de.carne.certmgr.certs.CertObjectStore;
import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.PasswordRequiredException;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.certmgr.certs.x509.KeyHelper;
import de.carne.io.IOUtil;
import de.carne.util.Strings;

/**
 * PKCS#12 read/write support.
 */
public class PKCS12CertReaderWriter implements CertReader, CertWriter {

	private static final Log LOG = new Log(CertIOI18N.class.getName());

	private static final JcePKCSPBEInputDecryptorProviderBuilder PKCS12_DECRYPTOR_PROVIDER_BUILDER = new JcePKCSPBEInputDecryptorProviderBuilder();

	private static final BcPKCS12PBEOutputEncryptorBuilder PKCS12_ENCRYPTOR_BUILDER = new BcPKCS12PBEOutputEncryptorBuilder(
			PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, new CBCBlockCipher(new DESedeEngine()));

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
		return CertIOI18N.strPkcs12Type();
	}

	@Override
	public String[] fileExtensionPatterns() {
		return Strings.split(CertIOI18N.strPkcs12ExtensionPatterns(), '|', true);
	}

	@Override
	public String fileExtension(Class<?> cls) {
		return fileExtensionPatterns()[0].replace("*", "");
	}

	@Override
	@Nullable
	public CertObjectStore readBinary(IOResource<InputStream> in, PasswordCallback password) throws IOException {
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
						PrivateKey privateKey = convertPrivateKey((PKCS8EncryptedPrivateKeyInfo) safeBagValue,
								in.resource(), password);
						try {
							certObjects.addKey(KeyHelper.rebuildKeyPair(privateKey));
						} catch (IOException e) {
							LOG.warning(e, "Unable to rebuild key pair for private key of type ''{1}''",
									privateKey.getClass().getName());
						}
					} else if (safeBagValue instanceof PrivateKeyInfo) {
						PrivateKey privateKey = convertPrivateKey((PrivateKeyInfo) safeBagValue);

						try {
							certObjects.addKey(KeyHelper.rebuildKeyPair(privateKey));
						} catch (IOException e) {
							LOG.warning(e, "Unable to rebuild key pair for private key of type ''{1}''",
									privateKey.getClass().getName());
						}
					} else {
						LOG.warning(CertIOI18N.STR_PKCS12_UNKNOWN_OBJECT, safeBagValue.getClass().getName());
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
	public boolean isEncryptionRequired() {
		return false;
	}

	@Override
	public void writeBinary(IOResource<OutputStream> out, CertObjectStore certObjects)
			throws IOException, UnsupportedOperationException {
		try {
			List<PKCS12SafeBagBuilder> safeBagBuilders = new ArrayList<>(certObjects.size());

			for (CertObjectStore.Entry certObject : certObjects) {
				switch (certObject.type()) {
				case CRT:
					safeBagBuilders.add(createCRTSafeBagBuilder(certObject.alias(), certObject.getCRT(),
							safeBagBuilders.isEmpty()));
					break;
				case KEY:
					safeBagBuilders.add(createKeySafeBagBuilder(certObject.alias(), certObject.getKey()));
					break;
				case CSR:
					break;
				case CRL:
					break;
				}
			}

			PKCS12PfxPduBuilder pkcs12Builder = new PKCS12PfxPduBuilder();

			for (PKCS12SafeBagBuilder safeBagBuilder : safeBagBuilders) {
				pkcs12Builder.addData(safeBagBuilder.build());
			}

			PKCS12PfxPdu pkcs12 = pkcs12Builder.build(null, null);

			out.io().write(pkcs12.getEncoded());
		} catch (GeneralSecurityException | PKCSException e) {
			throw new CertProviderException(e);
		}
	}

	@Override
	public void writeEncryptedBinary(IOResource<OutputStream> out, CertObjectStore certObjects,
			PasswordCallback newPassword) throws IOException {
		char[] passwordChars = newPassword.queryPassword(out.resource());

		if (passwordChars == null) {
			throw new PasswordRequiredException(out.resource());
		}
		try {
			List<PKCS12SafeBagBuilder> safeBagBuilders = new ArrayList<>(certObjects.size());

			for (CertObjectStore.Entry certObject : certObjects) {
				switch (certObject.type()) {
				case CRT:
					safeBagBuilders.add(createCRTSafeBagBuilder(certObject.alias(), certObject.getCRT(),
							safeBagBuilders.isEmpty()));
					break;
				case KEY:
					safeBagBuilders
							.add(createKeySafeBagBuilder(certObject.alias(), certObject.getKey(), passwordChars));
					break;
				case CSR:
					break;
				case CRL:
					break;
				}
			}

			PKCS12PfxPduBuilder pkcs12Builder = new PKCS12PfxPduBuilder();

			for (PKCS12SafeBagBuilder safeBagBuilder : safeBagBuilders) {
				pkcs12Builder.addData(safeBagBuilder.build());
			}

			PKCS12PfxPdu pkcs12 = pkcs12Builder.build(new BcPKCS12MacCalculatorBuilder(), passwordChars);

			out.io().write(pkcs12.getEncoded());
		} catch (GeneralSecurityException | PKCSException e) {
			throw new CertProviderException(e);
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

	private static PKCS12SafeBagBuilder createCRTSafeBagBuilder(String alias, X509Certificate crt, boolean addKeyId)
			throws IOException, GeneralSecurityException {
		PKCS12SafeBagBuilder safeBagBuilder = new JcaPKCS12SafeBagBuilder(crt);

		safeBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString(alias));
		if (addKeyId) {
			JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();
			SubjectKeyIdentifier subjectKeyIdentifier = extensionUtils.createSubjectKeyIdentifier(crt.getPublicKey());

			safeBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, subjectKeyIdentifier);
		}
		return safeBagBuilder;
	}

	private static PKCS12SafeBagBuilder createKeySafeBagBuilder(String alias, KeyPair key)
			throws GeneralSecurityException {
		PKCS12SafeBagBuilder safeBagBuilder = new JcaPKCS12SafeBagBuilder(key.getPrivate());

		safeBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString(alias));

		JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();
		SubjectKeyIdentifier subjectKeyIdentifier = extensionUtils.createSubjectKeyIdentifier(key.getPublic());

		safeBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, subjectKeyIdentifier);
		return safeBagBuilder;
	}

	private static PKCS12SafeBagBuilder createKeySafeBagBuilder(String alias, KeyPair key, char[] passwordChars)
			throws GeneralSecurityException {
		PKCS12SafeBagBuilder safeBagBuilder = new JcaPKCS12SafeBagBuilder(key.getPrivate(),
				PKCS12_ENCRYPTOR_BUILDER.build(passwordChars));

		safeBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString(alias));

		JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();
		SubjectKeyIdentifier subjectKeyIdentifier = extensionUtils.createSubjectKeyIdentifier(key.getPublic());

		safeBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, subjectKeyIdentifier);
		return safeBagBuilder;
	}

	@Nullable
	private static PKCS12PfxPdu readPKCS12(IOResource<InputStream> in) {
		PKCS12PfxPdu pkcs12 = null;

		try {
			byte[] bytes = IOUtil.readAllBytes(in.io(), CertReader.READ_LIMIT);

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
		return PKCS12_DECRYPTOR_PROVIDER_BUILDER.build(passwordChars);
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
