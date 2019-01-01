/*
 * Copyright (c) 2015-2019 Holger de Carne and contributors, All Rights Reserved.
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
import java.security.PrivateKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfoBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder;
import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.Exceptions;
import de.carne.boot.logging.Log;
import de.carne.certmgr.certs.CertObjectStore;
import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.PasswordRequiredException;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.certmgr.certs.x509.KeyHelper;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;
import de.carne.util.Strings;

/**
 * DER read/write support.
 */
public class DERCertReaderWriter extends JCAConversion implements CertReader, CertWriter {

	private static final Log LOG = new Log(CertIOI18N.class.getName());

	private static final ASN1ObjectIdentifier OUTPUT_ENCRYPTOR_ALGORITHM = PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC;

	private static final JcePKCSPBEOutputEncryptorBuilder OUTPUT_ENCRYPTOR_BUILDER = new JcePKCSPBEOutputEncryptorBuilder(
			OUTPUT_ENCRYPTOR_ALGORITHM);

	private static final JcePKCSPBEInputDecryptorProviderBuilder INPUT_DECRYPTOR_BUILDER = new JcePKCSPBEInputDecryptorProviderBuilder();

	private static final JcaJceHelper JCA_JCE_HELPER = new DefaultJcaJceHelper();

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
		return Strings.split(CertIOI18N.formatSTR_DER_EXTENSION_PATTERNS(), '|', true);
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
	@Nullable
	public CertObjectStore readBinary(IOResource<InputStream> in, PasswordCallback password) throws IOException {
		LOG.debug("Trying to read DER objects from: ''{0}''...", in);

		CertObjectStore certObjects = null;

		try (ASN1InputStream derStream = new ASN1InputStream(in.io())) {
			ASN1Primitive derObject;

			while ((derObject = derStream.readObject()) != null) {
				X509Certificate crt = tryDecodeCRT(derObject);

				if (crt != null) {
					if (certObjects == null) {
						certObjects = new CertObjectStore();
					}
					certObjects.addCRT(crt);
					continue;
				}

				KeyPair key = tryDecodeKey(derObject, in.resource(), password);

				if (key != null) {
					if (certObjects == null) {
						certObjects = new CertObjectStore();
					}
					certObjects.addKey(key);
					continue;
				}

				PKCS10CertificateRequest csr = tryDecodeCSR(derObject);

				if (csr != null) {
					if (certObjects == null) {
						certObjects = new CertObjectStore();
					}
					certObjects.addCSR(csr);
					continue;
				}

				X509CRL crl = tryDecodeCRL(derObject);

				if (crl != null) {
					if (certObjects == null) {
						certObjects = new CertObjectStore();
					}
					certObjects.addCRL(crl);
					continue;
				}

				LOG.warning(CertIOI18N.STR_DER_UNKNOWN_OBJECT, derObject.getClass().getName());
			}
		} catch (ClassCastException e) {
			// ASN1InputStream may cause this in case of non-DER data; simply ignore it and consider
			// the file is not a DER stream
			throw new CertProviderException(e);
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
		for (CertObjectStore.Entry certObject : certObjects) {
			writeCertObject(out, certObject);
		}
	}

	@Override
	public void writeEncryptedBinary(IOResource<OutputStream> out, CertObjectStore certObjects,
			PasswordCallback newPassword) throws IOException {
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
				out.io().write(KeyHelper.encodePrivateKey(storeEntry.getKey().getPrivate()));
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
		try {
			switch (storeEntry.type()) {
			case CRT:
				out.io().write(storeEntry.getCRT().getEncoded());
				break;
			case KEY:
				out.io().write(encryptKey(storeEntry.getKey(), out.resource(), newPassword));
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

	@Nullable
	private static X509Certificate tryDecodeCRT(ASN1Primitive asn1Object) throws IOException {
		X509CertificateHolder crtObject = null;

		try {
			crtObject = new X509CertificateHolder(asn1Object.getEncoded());
		} catch (Exception e) {
			Exceptions.ignore(e);
		}
		return (crtObject != null ? convertCRT(crtObject) : null);
	}

	@Nullable
	private static KeyPair tryDecodeKey(ASN1Primitive asn1Object, String resource, PasswordCallback password)
			throws IOException {
		PKCS8EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = null;

		try {
			encryptedPrivateKeyInfo = new PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo.getInstance(asn1Object));
		} catch (Exception e) {
			Exceptions.ignore(e);
		}

		PrivateKeyInfo privateKeyInfo = null;

		if (encryptedPrivateKeyInfo != null) {
			Throwable passwordException = null;

			while (privateKeyInfo == null) {
				char[] passwordChars = password.queryPassword(resource);

				if (passwordChars == null) {
					throw new PasswordRequiredException(resource, passwordException);
				}

				InputDecryptorProvider inputDecryptorProvider = INPUT_DECRYPTOR_BUILDER.build(passwordChars);

				try {
					privateKeyInfo = encryptedPrivateKeyInfo.decryptPrivateKeyInfo(inputDecryptorProvider);
				} catch (PKCSException e) {
					passwordException = e;
				}
			}
		}

		try {
			privateKeyInfo = PrivateKeyInfo.getInstance(asn1Object);
		} catch (Exception e) {
			Exceptions.ignore(e);
		}

		KeyPair key = null;

		if (privateKeyInfo != null) {
			PrivateKey privateKey;

			try {
				String algorithmId = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm().getId();
				KeyFactory keyFactory = JCA_JCE_HELPER.createKeyFactory(algorithmId);
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyInfo.getEncoded());

				privateKey = keyFactory.generatePrivate(keySpec);
			} catch (GeneralSecurityException e) {
				throw new CertProviderException(e);
			}
			key = KeyHelper.rebuildKeyPair(privateKey);
		}
		return key;
	}

	@Nullable
	private static PKCS10CertificateRequest tryDecodeCSR(ASN1Primitive asn1Object) throws IOException {
		PKCS10CertificationRequest csrObject = null;

		try {
			csrObject = new PKCS10CertificationRequest(asn1Object.getEncoded());
		} catch (Exception e) {
			Exceptions.ignore(e);
		}
		return (csrObject != null ? convertCSR(csrObject) : null);
	}

	@Nullable
	private static X509CRL tryDecodeCRL(ASN1Primitive asn1Object) throws IOException {
		X509CRLHolder crlObject = null;

		try {
			crlObject = new X509CRLHolder(asn1Object.getEncoded());
		} catch (Exception e) {
			Exceptions.ignore(e);
		}
		return (crlObject != null ? convertCRL(crlObject) : null);
	}

	private static byte[] encryptKey(KeyPair key, String resource, PasswordCallback newPassword) throws IOException {
		char[] passwordChars = newPassword.queryPassword(resource);

		if (passwordChars == null) {
			throw new PasswordRequiredException(resource);
		}

		byte[] encoded;

		try {
			PKCS8EncryptedPrivateKeyInfoBuilder encryptedPrivateKeyInfoBuilder = new PKCS8EncryptedPrivateKeyInfoBuilder(
					KeyHelper.encodePrivateKey(key.getPrivate()));
			OutputEncryptor encryptor = OUTPUT_ENCRYPTOR_BUILDER.build(passwordChars);

			encoded = encryptedPrivateKeyInfoBuilder.build(encryptor).getEncoded();
		} catch (OperatorCreationException e) {
			throw new CertProviderException(e);
		}
		return encoded;
	}

	@Override
	public String toString() {
		return fileType();
	}

}
