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
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Objects;

import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
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
import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.logging.Log;
import de.carne.certmgr.certs.CertObjectStore;
import de.carne.certmgr.certs.NoPassword;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.PasswordRequiredException;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;
import de.carne.util.Strings;
import de.carne.util.SystemProperties;

/**
 * PEM read/write support.
 */
public class PEMCertReaderWriter extends JCAConversion implements CertReader, CertWriter {

	private static final Log LOG = new Log();

	/**
	 * Provider name.
	 */
	public static final String PROVIDER_NAME = "PEM";

	private static final String PEM_ENCRYPTION = Objects.requireNonNull(
			SystemProperties.value(PEMCertReaderWriter.class.getPackage().getName() + ".encryption", "AES-128-CBC"));

	private static final JcePEMEncryptorBuilder PEM_ENCRYPTOR_BUILDER = new JcePEMEncryptorBuilder(PEM_ENCRYPTION);

	private static final JcePEMDecryptorProviderBuilder PEM_DECRYPTOR_PROVIDER_BUILDER = new JcePEMDecryptorProviderBuilder();

	private static final JcaPEMKeyConverter PEM_KEY_CONVERTER = new JcaPEMKeyConverter();

	@Override
	public String providerName() {
		return PROVIDER_NAME;
	}

	@Override
	public String fileType() {
		return CertIOI18N.strPemType();
	}

	@Override
	public String[] fileExtensionPatterns() {
		return Strings.split(CertIOI18N.strPemExtensionPatterns(), '|', true);
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
		return readObjectsBinary(in, password);
	}

	@Override
	@Nullable
	public CertObjectStore readString(IOResource<Reader> in, PasswordCallback password) throws IOException {
		return readObjectsString(in, password);
	}

	@Override
	public boolean isCharWriter() {
		return true;
	}

	@Override
	public boolean isEncryptionRequired() {
		return false;
	}

	@Override
	public void writeBinary(IOResource<OutputStream> out, CertObjectStore certObjects)
			throws IOException, UnsupportedOperationException {
		try (IOResource<Writer> outWriter = IOResource.streamWriter(out, StandardCharsets.US_ASCII)) {
			writeString(outWriter, certObjects);
		}
	}

	@Override
	public void writeEncryptedBinary(IOResource<OutputStream> out, CertObjectStore certObjects,
			PasswordCallback newPassword) throws IOException {
		try (IOResource<Writer> outWriter = IOResource.streamWriter(out, StandardCharsets.US_ASCII)) {
			writeEncryptedString(outWriter, certObjects, newPassword);
		}
	}

	@Override
	public void writeString(IOResource<Writer> out, CertObjectStore certObjects)
			throws IOException, UnsupportedOperationException {
		try (JcaPEMWriter pemWriter = new JcaPEMWriter(out.io())) {
			for (CertObjectStore.Entry certObject : certObjects) {
				writeCertObject(pemWriter, out.resource(), certObject);
			}
		}
	}

	@Override
	public void writeEncryptedString(IOResource<Writer> out, CertObjectStore certObjects, PasswordCallback newPassword)
			throws IOException, UnsupportedOperationException {
		try (JcaPEMWriter pemWriter = new JcaPEMWriter(out.io())) {
			for (CertObjectStore.Entry certObject : certObjects) {
				writeEncryptedCertObject(pemWriter, out.resource(), certObject, newPassword);
			}
		}
	}

	/**
	 * Read all available certificate objects from a PEM encoded {@link InputStream} resource.
	 *
	 * @param in The stream resource to read from.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The read certificate objects, or {@code null} if the input is not recognized.
	 * @throws IOException if an I/O error occurs while reading.
	 */
	@Nullable
	public static CertObjectStore readObjectsBinary(IOResource<InputStream> in, PasswordCallback password)
			throws IOException {
		CertObjectStore certObjects;

		try (IOResource<Reader> inReader = IOResource.streamReader(in, StandardCharsets.US_ASCII)) {
			certObjects = readObjectsString(inReader, password);
		}
		return certObjects;
	}

	/**
	 * Read all available certificate objects from a PEM encoded {@link Reader} resource.
	 *
	 * @param in The reader resource to read from.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The read certificate objects, or {@code null} if the input is not recognized.
	 * @throws IOException if an I/O error occurs while reading.
	 */
	@Nullable
	public static CertObjectStore readObjectsString(IOResource<Reader> in, PasswordCallback password)
			throws IOException {
		LOG.debug("Trying to read PEM objects from: ''{0}''...", in);

		CertObjectStore certObjects = null;

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
					certObjects = new CertObjectStore();
				}

				LOG.info("Decoding PEM object of type {0}", pemObject.getClass().getName());

				if (pemObject instanceof X509CertificateHolder) {
					certObjects.addCRT(convertCRT((X509CertificateHolder) pemObject));
				} else if (pemObject instanceof PEMKeyPair) {
					certObjects.addKey(convertKey((PEMKeyPair) pemObject));
				} else if (pemObject instanceof PEMEncryptedKeyPair) {
					certObjects.addKey(convertKey((PEMEncryptedKeyPair) pemObject, in.resource(), password));
				} else if (pemObject instanceof PKCS10CertificationRequest) {
					certObjects.addCSR(convertCSR((PKCS10CertificationRequest) pemObject));
				} else if (pemObject instanceof X509CRLHolder) {
					certObjects.addCRL(convertCRL((X509CRLHolder) pemObject));
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
		return readObjectString(in, NoPassword.getInstance()).getCRT();
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
		return readObjectString(in, password).getKey();
	}

	/**
	 * Read a single CSR object from a PEM encoded {@link InputStream} resource.
	 *
	 * @param in The stream resource to read from.
	 * @return The read CSR object.
	 * @throws IOException if no CSR object can be read.
	 */
	public static PKCS10CertificateRequest readCSRBinary(IOResource<InputStream> in) throws IOException {
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
		return readObjectString(in, NoPassword.getInstance()).getCSR();
	}

	/**
	 * Read a single CRL object from a PEM encoded {@link InputStream} resource.
	 *
	 * @param in The stream resource to read from.
	 * @return The read CRL object.
	 * @throws IOException if no CRL object can be read.
	 */
	public static X509CRL readCRLBinary(IOResource<InputStream> in) throws IOException {
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
		return readObjectString(in, NoPassword.getInstance()).getCRL();
	}

	private static CertObjectStore.Entry readObjectString(IOResource<Reader> in, PasswordCallback password)
			throws IOException {
		CertObjectStore certObjects = readObjectsString(in, password);

		if (certObjects == null) {
			throw new IOException("No objects read from '" + in.resource() + "'");
		}

		int certObjectsCount = certObjects.size();

		if (certObjectsCount != 1) {
			throw new IOException(certObjectsCount + " objects read from '" + in.resource() + "' (expected 1)");
		}
		return certObjects.iterator().next();
	}

	/**
	 * Write a single CRT object to a {@link OutputStream} resource.
	 *
	 * @param out The stream resource to to write to.
	 * @param crt The CRT object to write.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public static void writeCRTBinary(IOResource<OutputStream> out, X509Certificate crt) throws IOException {
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
		try (JcaPEMWriter pemWriter = new JcaPEMWriter(out.io())) {
			writeObject(pemWriter, out.resource(), crt);
		}
	}

	/**
	 * Write a single Key object to a {@link OutputStream} resource.
	 *
	 * @param out The stream resource to to write to.
	 * @param key The Key object to write.
	 * @param newPassword The callback to use for querying the encryption password.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public static void writeKeyBinary(IOResource<OutputStream> out, KeyPair key, PasswordCallback newPassword)
			throws IOException {
		try (IOResource<Writer> outWriter = IOResource.streamWriter(out, StandardCharsets.US_ASCII)) {
			writeKeyString(outWriter, key, newPassword);
		}
	}

	/**
	 * Write a single Key object to a {@link Writer} resource.
	 *
	 * @param out The writer resource to to write to.
	 * @param key The Key object to write.
	 * @param newPassword The callback to use for querying the encryption password.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public static void writeKeyString(IOResource<Writer> out, KeyPair key, PasswordCallback newPassword)
			throws IOException {
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
		try (JcaPEMWriter pemWriter = new JcaPEMWriter(out.io())) {
			writeObject(pemWriter, out.resource(), crl);
		}
	}

	private static void writeCertObject(JcaPEMWriter writer, String resource, CertObjectStore.Entry storeEntry)
			throws IOException {
		switch (storeEntry.type()) {
		case CRT:
			writeObject(writer, resource, storeEntry.getCRT());
			break;
		case KEY:
			writeObject(writer, resource, storeEntry.getKey());
			break;
		case CSR:
			writeObject(writer, resource, storeEntry.getCSR().toPKCS10());
			break;
		case CRL:
			writeObject(writer, resource, storeEntry.getCRL());
			break;
		}
	}

	private static void writeObject(JcaPEMWriter writer, String resource, Object object) throws IOException {
		LOG.debug("Writing PEM object ''{0}'' to resource ''{1}''...", object.getClass().getName(), resource);

		writer.writeObject(object);
	}

	private static void writeEncryptedCertObject(JcaPEMWriter writer, String resource, CertObjectStore.Entry storeEntry,
			PasswordCallback newPassword) throws IOException {
		switch (storeEntry.type()) {
		case CRT:
			writeObject(writer, resource, storeEntry.getCRT());
			break;
		case KEY:
			writeEncryptedObject(writer, resource, storeEntry.getKey(), newPassword);
			break;
		case CSR:
			writeObject(writer, resource, storeEntry.getCSR().toPKCS10());
			break;
		case CRL:
			writeObject(writer, resource, storeEntry.getCRL());
			break;
		}
	}

	private static void writeEncryptedObject(JcaPEMWriter writer, String resource, Object object,
			PasswordCallback newPassword) throws IOException {
		LOG.debug("Writing encrypted PEM object ''{0}'' to resource ''{1}''...", object.getClass().getName(), resource);

		char[] passwordChars = newPassword.queryPassword(resource);

		if (passwordChars == null) {
			throw new PasswordRequiredException(resource);
		}
		writer.writeObject(object, PEM_ENCRYPTOR_BUILDER.build(passwordChars));
	}

	private static KeyPair convertKey(PEMEncryptedKeyPair pemObject, String resource, PasswordCallback password)
			throws IOException {
		PEMKeyPair pemKeyPair = null;
		Throwable passwordException = null;

		while (pemKeyPair == null) {
			char[] passwordChars = (passwordException == null ? password.queryPassword(resource)
					: password.requeryPassword(resource, passwordException));

			if (passwordChars == null) {
				throw new PasswordRequiredException(resource, passwordException);
			}

			PEMDecryptorProvider pemDecryptorProvider = PEM_DECRYPTOR_PROVIDER_BUILDER.build(passwordChars);

			try {
				pemKeyPair = pemObject.decryptKeyPair(pemDecryptorProvider);
			} catch (EncryptionException e) {
				passwordException = e;
			}
		}
		return convertKey(pemKeyPair);
	}

	private static KeyPair convertKey(PEMKeyPair pemObject) throws IOException {
		return PEM_KEY_CONVERTER.getKeyPair(pemObject);
	}

	@Override
	public String toString() {
		return fileType();
	}

}
