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
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.checkerframework.checker.nullness.qual.Nullable;

import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.NoPassword;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.PasswordRequiredException;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;
import de.carne.util.PropertiesHelper;
import de.carne.util.Strings;
import de.carne.util.logging.Log;

/**
 * PEM I/O support.
 */
public class PEMCertReaderWriter implements CertReader, CertWriter {

	private static final Log LOG = new Log();

	/**
	 * Provider name.
	 */
	public static final String PROVIDER_NAME = "PEM";

	private static final String PEM_ENCRYPTION = PropertiesHelper.get(PEMCertReaderWriter.class, "encryption",
			"AES-128-CBC");

	private static final JcePEMDecryptorProviderBuilder PEM_DECRYPTOR_BUILDER = new JcePEMDecryptorProviderBuilder();

	private static final JcePEMEncryptorBuilder PEM_ENCRYPTOR_BUILDER = new JcePEMEncryptorBuilder(PEM_ENCRYPTION);

	private static final JcaX509CertificateConverter CRT_CONVERTER = new JcaX509CertificateConverter();

	private static final JcaPEMKeyConverter KEY_CONVERTER = new JcaPEMKeyConverter();

	private static final JcaX509CRLConverter CRL_CONVERTER = new JcaX509CRLConverter();

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
	public @Nullable List<Object> readBinary(IOResource<InputStream> in, PasswordCallback password) throws IOException {
		assert in != null;

		return readObjectsBinary(in, password);
	}

	@Override
	public @Nullable List<Object> readString(IOResource<Reader> in, PasswordCallback password) throws IOException {
		assert in != null;

		return readObjectsString(in, password);
	}

	@Override
	public boolean isCharWriter() {
		return true;
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
		try (IOResource<Writer> outWriter = IOResource.streamWriter(out, StandardCharsets.US_ASCII)) {
			writeString(outWriter, certObjects);
		}
	}

	@Override
	public void writeEncryptedBinary(IOResource<OutputStream> out, List<Object> certObjects,
			PasswordCallback newPassword) throws IOException, UnsupportedOperationException {
		try (IOResource<Writer> outWriter = IOResource.streamWriter(out, StandardCharsets.US_ASCII)) {
			writeEncryptedString(outWriter, certObjects, newPassword);
		}
	}

	@Override
	public void writeString(IOResource<Writer> out, List<Object> certObjects)
			throws IOException, UnsupportedOperationException {
		try (JcaPEMWriter pemWriter = new JcaPEMWriter(out.io())) {
			for (Object certObject : certObjects) {
				writeObject(pemWriter, out.resource(), certObject);
			}
		}
	}

	@Override
	public void writeEncryptedString(IOResource<Writer> out, List<Object> certObjects, PasswordCallback newPassword)
			throws IOException, UnsupportedOperationException {
		try (JcaPEMWriter pemWriter = new JcaPEMWriter(out.io())) {
			for (Object certObject : certObjects) {
				if (certObject instanceof KeyPair) {
					writeEncryptedObject(pemWriter, out.resource(), certObject, newPassword);
				} else {
					writeObject(pemWriter, out.resource(), certObject);
				}
			}
		}
	}

	/**
	 * Read all available certificate objects from a PEM encoded
	 * {@link InputStream} resource.
	 *
	 * @param in The stream resource to read from.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The list of read certificate objects, or {@code null} if the
	 *         input is not recognized.
	 * @throws IOException if an I/O error occurs while reading.
	 */
	public static List<Object> readObjectsBinary(IOResource<InputStream> in, PasswordCallback password)
			throws IOException {
		assert in != null;

		List<Object> certObjects;

		try (IOResource<Reader> inReader = IOResource.streamReader(in, StandardCharsets.US_ASCII)) {
			certObjects = readObjectsString(inReader, password);
		}
		return certObjects;
	}

	/**
	 * Read all available certificate objects from a PEM encoded {@link Reader}
	 * resource.
	 *
	 * @param in The reader resource to read from.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The list of read certificate objects, or {@code null} if the
	 *         input is not recognized.
	 * @throws IOException if an I/O error occurs while reading.
	 */
	public static List<Object> readObjectsString(IOResource<Reader> in, PasswordCallback password) throws IOException {
		assert in != null;
		assert password != null;

		LOG.debug("Trying to read PEM objects from: ''{0}''...", in);

		List<Object> certObjects = null;

		try (PEMParser parser = new PEMParser(in.io())) {
			Object pemObject;

			try {
				pemObject = parser.readObject();
			} catch (IOException e) {
				LOG.info(e, "No PEM objects recognized in: ''{0}''", in);
				pemObject = null;
			}
			while (pemObject != null) {
				if (certObjects == null) {
					certObjects = new ArrayList<>();
				}

				LOG.info("Decoding PEM object of type {0}", pemObject.getClass().getName());

				if (pemObject instanceof X509CertificateHolder) {
					certObjects.add(convertCRT((X509CertificateHolder) pemObject));
				} else if (pemObject instanceof PEMKeyPair) {
					certObjects.add(convertKey((PEMKeyPair) pemObject));
				} else if (pemObject instanceof PEMEncryptedKeyPair) {
					certObjects.add(convertKey((PEMEncryptedKeyPair) pemObject, in.resource(), password));
				} else if (pemObject instanceof PKCS10CertificationRequest) {
					certObjects.add(convertCSR((PKCS10CertificationRequest) pemObject));
				} else if (pemObject instanceof X509CRLHolder) {
					certObjects.add(convertCRL((X509CRLHolder) pemObject));
				} else {
					LOG.warning("Ignoring unrecognized PEM object of type {0}", pemObject.getClass().getName());
				}
				pemObject = parser.readObject();
			}
		}
		return certObjects;
	}

	/**
	 * Read a single CRT object from a PEM encoded {@link InputStream} resource.
	 *
	 * @param in The stream resource to read from.
	 * @return The read CRT object.
	 * @throws IOException if no CRT object can be read.
	 */
	public static X509Certificate readCRTBinary(IOResource<InputStream> in) throws IOException {
		assert in != null;

		X509Certificate crtObject;

		try (IOResource<Reader> inReader = IOResource.streamReader(in, StandardCharsets.US_ASCII)) {
			crtObject = readCRTString(inReader);
		}
		return crtObject;
	}

	/**
	 * Read a single CRT object from a PEM encoded {@link Reader} resource.
	 *
	 * @param in The reader resource to read from.
	 * @return The read CRT object.
	 * @throws IOException if no CRT object can be read.
	 */
	public static X509Certificate readCRTString(IOResource<Reader> in) throws IOException {
		assert in != null;

		return readObjectString(X509Certificate.class, in, NoPassword.getInstance());
	}

	/**
	 * Read a single Key object from a PEM encoded {@link InputStream} resource.
	 *
	 * @param in The stream resource to read from.
	 * @param password The callback to use for querying the key password.
	 * @return The read Key object.
	 * @throws IOException if no Key object can be read.
	 */
	public static KeyPair readKeyBinary(IOResource<InputStream> in, PasswordCallback password) throws IOException {
		assert in != null;
		assert password != null;

		KeyPair keyObject;

		try (IOResource<Reader> inReader = IOResource.streamReader(in, StandardCharsets.US_ASCII)) {
			keyObject = readKeyString(inReader, password);
		}
		return keyObject;
	}

	/**
	 * Read a single Key object from a PEM encoded {@link Reader} resource.
	 *
	 * @param in The reader resource to read from.
	 * @param password The callback to use for querying the key password.
	 * @return The read Key object.
	 * @throws IOException if no Key object can be read.
	 */
	public static KeyPair readKeyString(IOResource<Reader> in, PasswordCallback password) throws IOException {
		assert in != null;
		assert password != null;

		return readObjectString(KeyPair.class, in, password);
	}

	/**
	 * Read a single CSR object from a PEM encoded {@link InputStream} resource.
	 *
	 * @param in The stream resource to read from.
	 * @return The read CSR object.
	 * @throws IOException if no CSR object can be read.
	 */
	public static PKCS10CertificateRequest readCSRBinary(IOResource<InputStream> in) throws IOException {
		assert in != null;

		PKCS10CertificateRequest csrObject;

		try (IOResource<Reader> inReader = IOResource.streamReader(in, StandardCharsets.US_ASCII)) {
			csrObject = readCSRString(inReader);
		}
		return csrObject;
	}

	/**
	 * Read a single CSR object from a PEM encoded {@link Reader} resource.
	 *
	 * @param in The reader resource to read from.
	 * @return The read CSR object.
	 * @throws IOException if no CSR object can be read.
	 */
	public static PKCS10CertificateRequest readCSRString(IOResource<Reader> in) throws IOException {
		assert in != null;

		return readObjectString(PKCS10CertificateRequest.class, in, NoPassword.getInstance());
	}

	/**
	 * Read a single CRL object from a PEM encoded {@link InputStream} resource.
	 *
	 * @param in The stream resource to read from.
	 * @return The read CRL object.
	 * @throws IOException if no CRL object can be read.
	 */
	public static X509CRL readCRLBinary(IOResource<InputStream> in) throws IOException {
		assert in != null;

		X509CRL crlObject;

		try (IOResource<Reader> inReader = IOResource.streamReader(in, StandardCharsets.US_ASCII)) {
			crlObject = readCRLString(inReader);
		}
		return crlObject;
	}

	/**
	 * Read a single CRL object from a PEM encoded {@link Reader} resource.
	 *
	 * @param in The reader resource to read from.
	 * @return The read CRL object.
	 * @throws IOException if no CRL object can be read.
	 */
	public static X509CRL readCRLString(IOResource<Reader> in) throws IOException {
		assert in != null;

		return readObjectString(X509CRL.class, in, NoPassword.getInstance());
	}

	private static <T> T readObjectString(Class<T> type, IOResource<Reader> in, PasswordCallback password)
			throws IOException {
		List<Object> certObjects = readObjectsString(in, password);
		int certObjectsCount = (certObjects != null ? certObjects.size() : 0);

		if (certObjectsCount != 1) {
			throw new IOException(certObjectsCount + " objects read from '" + in.resource() + "' (expected 1)");
		}

		assert certObjects != null;

		Object certObject = certObjects.get(0);

		if (!type.isInstance(certObject)) {
			throw new IOException(certObject.getClass().getName() + " read (expected " + type.getName() + ")");
		}
		return type.cast(certObject);
	}

	/**
	 * Write a single CRT object to a {@link OutputStream} resource.
	 *
	 * @param out The stream resource to to write to.
	 * @param crt The CRT object to write.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public static void writeCRTBinary(IOResource<OutputStream> out, X509Certificate crt) throws IOException {
		assert out != null;
		assert crt != null;

		try (IOResource<Writer> outWriter = IOResource.streamWriter(out, StandardCharsets.US_ASCII)) {
			writeCRTString(outWriter, crt);
		}
	}

	/**
	 * Write a single CRT object to a {@link Writer} resource.
	 *
	 * @param out The writer resource to write to.
	 * @param crt The CRT object to write.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public static void writeCRTString(IOResource<Writer> out, X509Certificate crt) throws IOException {
		assert out != null;
		assert crt != null;

		try (JcaPEMWriter pemWriter = new JcaPEMWriter(out.io())) {
			writeObject(pemWriter, out.resource(), crt);
		}
	}

	/**
	 * Write a single Key object to a {@link OutputStream} resource.
	 *
	 * @param out The stream resource to to write to.
	 * @param key The Key object to write.
	 * @param newPassword The callback to use for querying the encryption
	 *        password.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public static void writeKeyBinary(IOResource<OutputStream> out, KeyPair key, PasswordCallback newPassword)
			throws IOException {
		assert out != null;
		assert key != null;
		assert newPassword != null;

		try (IOResource<Writer> outWriter = IOResource.streamWriter(out, StandardCharsets.US_ASCII)) {
			writeKeyString(outWriter, key, newPassword);
		}
	}

	/**
	 * Write a single Key object to a {@link Writer} resource.
	 *
	 * @param out The writer resource to to write to.
	 * @param key The Key object to write.
	 * @param newPassword The callback to use for querying the encryption
	 *        password.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public static void writeKeyString(IOResource<Writer> out, KeyPair key, PasswordCallback newPassword)
			throws IOException {
		assert out != null;
		assert key != null;
		assert newPassword != null;

		try (JcaPEMWriter pemWriter = new JcaPEMWriter(out.io())) {
			writeEncryptedObject(pemWriter, out.resource(), key, newPassword);
		}
	}

	/**
	 * Write a single CSR object to a {@link OutputStream} resource.
	 *
	 * @param out The stream resource to to write to.
	 * @param csr The CSR object to write.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public static void writeCSRBinary(IOResource<OutputStream> out, PKCS10CertificateRequest csr) throws IOException {
		assert out != null;
		assert csr != null;

		try (IOResource<Writer> outWriter = IOResource.streamWriter(out, StandardCharsets.US_ASCII)) {
			writeCSRString(outWriter, csr);
		}
	}

	/**
	 * Write a single CSR object to a {@link Writer} resource.
	 *
	 * @param out The writer resource to write to.
	 * @param csr The CSR object to write.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public static void writeCSRString(IOResource<Writer> out, PKCS10CertificateRequest csr) throws IOException {
		assert out != null;
		assert csr != null;

		try (JcaPEMWriter pemWriter = new JcaPEMWriter(out.io())) {
			writeObject(pemWriter, out.resource(), csr.toPKCS10());
		}
	}

	/**
	 * Write a single CRL object to a {@link OutputStream} resource.
	 *
	 * @param out The stream resource to to write to.
	 * @param crl The CRL object to write.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public static void writeCRLBinary(IOResource<OutputStream> out, X509CRL crl) throws IOException {
		assert out != null;
		assert crl != null;

		try (IOResource<Writer> outWriter = IOResource.streamWriter(out, StandardCharsets.US_ASCII)) {
			writeCRLString(outWriter, crl);
		}
	}

	/**
	 * Write a single CRL object to a {@link Writer} resource.
	 *
	 * @param out The writer resource to write to.
	 * @param crl The CRL object to write.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public static void writeCRLString(IOResource<Writer> out, X509CRL crl) throws IOException {
		assert out != null;
		assert crl != null;

		try (JcaPEMWriter pemWriter = new JcaPEMWriter(out.io())) {
			writeObject(pemWriter, out.resource(), crl);
		}
	}

	private static void writeObject(JcaPEMWriter writer, String resource, Object object) throws IOException {
		LOG.debug("Writing PEM object ''{0}'' to resource ''{1}''...", object.getClass().getName(), resource);

		writer.writeObject(object);
	}

	private static void writeEncryptedObject(JcaPEMWriter writer, String resource, Object object,
			PasswordCallback password) throws IOException {
		LOG.debug("Writing encrypted PEM object ''{0}'' to resource ''{1}''...", object.getClass().getName(), resource);

		char[] passwordChars = password.queryPassword(resource);

		if (passwordChars == null) {
			throw new PasswordRequiredException(resource);
		}
		writer.writeObject(object, PEM_ENCRYPTOR_BUILDER.build(passwordChars));
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

	private static KeyPair convertKey(PEMKeyPair pemObject) throws IOException {
		return KEY_CONVERTER.getKeyPair(pemObject);
	}

	private static KeyPair convertKey(PEMEncryptedKeyPair pemObject, String resource, PasswordCallback password)
			throws IOException {
		PEMKeyPair pemKeyPair = null;
		char[] passwordChars = password.queryPassword(resource);
		Throwable passwordException = null;

		while (pemKeyPair == null) {
			if (passwordChars == null) {
				throw new PasswordRequiredException(resource, passwordException);
			}

			PEMDecryptorProvider pemDecryptorProvider = PEM_DECRYPTOR_BUILDER.build(passwordChars);

			try {
				pemKeyPair = pemObject.decryptKeyPair(pemDecryptorProvider);
			} catch (EncryptionException e) {
				passwordException = e;
			}
			if (pemKeyPair == null) {
				passwordChars = password.requeryPassword(resource, passwordException);
			}
		}
		return convertKey(pemKeyPair);
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
