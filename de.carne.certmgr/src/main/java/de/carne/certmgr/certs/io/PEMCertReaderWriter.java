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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.PKCS10CertificateRequest;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.PasswordRequiredException;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.certs.spi.CertWriter;
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

	private final JcaX509CertificateConverter crtConverter = new JcaX509CertificateConverter();

	private final JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter();

	private final JcePEMDecryptorProviderBuilder pemDecryptorBuilder = new JcePEMDecryptorProviderBuilder();

	private final String PEM_ENCRYPTION = PropertiesHelper.get(PEMCertReaderWriter.class, "encryption", "AES-128-CBC");

	private final JcePEMEncryptorBuilder pemEncryptorBuilder = new JcePEMEncryptorBuilder(this.PEM_ENCRYPTION);

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
		assert input != null;

		return read(input.reader(StandardCharsets.US_ASCII), input.toString(), password);
	}

	/**
	 * Read all available certificate objects from a PEM encoded
	 * {@link InputStream}.
	 *
	 * @param input The input stream to read from.
	 * @param resource The resource name to use for querying passwords (if
	 *        needed).
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The list of read certificate objects or {@code null} if the input
	 *         is not recognized.
	 * @throws IOException if an I/O error occurs while reading.
	 */
	public List<Object> read(InputStream input, String resource, PasswordCallback password) throws IOException {
		assert input != null;

		return read(new InputStreamReader(input, StandardCharsets.US_ASCII), resource, password);
	}

	/**
	 * Read all available certificate objects from a PEM encoded {@link Reader}.
	 *
	 * @param reader The reader to use for accessing the data.
	 * @param resource The resource name to use for querying passwords (if
	 *        needed).
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The list of read certificate objects or {@code null} if the input
	 *         is not recognized.
	 * @throws IOException if an I/O error occurs while reading.
	 */
	public List<Object> read(Reader reader, String resource, PasswordCallback password) throws IOException {
		assert reader != null;
		assert resource != null;
		assert password != null;

		LOG.debug("Trying to read PEM objects from: ''{0}''...", resource);

		List<Object> pemObjects = null;

		try (PEMParser parser = new PEMParser(reader)) {
			Object pemObject;

			try {
				pemObject = parser.readObject();
				pemObjects = new ArrayList<>();
			} catch (IOException e) {
				LOG.info(e, "No PEM objects recognized in: ''{0}''", resource);
				pemObject = null;
			}
			while (pemObject != null) {
				assert pemObjects != null;

				LOG.info("Decoding PEM object of type {0}", pemObject.getClass().getName());
				if (pemObject instanceof X509CertificateHolder) {
					pemObjects.add(getCRT((X509CertificateHolder) pemObject));
				} else if (pemObject instanceof PEMKeyPair) {
					pemObjects.add(getKey((PEMKeyPair) pemObject));
				} else if (pemObject instanceof PEMEncryptedKeyPair) {
					pemObjects.add(getKey((PEMEncryptedKeyPair) pemObject, resource, password));
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

	/**
	 * Write CRT object.
	 *
	 * @param output The output stream to write to.
	 * @param crt The CRT object to write.
	 * @param resource The resource written to.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public void write(OutputStream output, X509Certificate crt, String resource) throws IOException {
		assert output != null;

		write(new OutputStreamWriter(output, StandardCharsets.US_ASCII), crt, resource);
	}

	/**
	 * Write CRT object.
	 *
	 * @param writer The writer to use.
	 * @param crt The CRT object to write.
	 * @param resource The resource written to.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public void write(Writer writer, X509Certificate crt, String resource) throws IOException {
		assert writer != null;
		assert crt != null;
		assert resource != null;

		writeObject(writer, crt, resource);
	}

	/**
	 * Write Key object.
	 *
	 * @param output The output stream to write to.
	 * @param key The Key object to write.
	 * @param resource The resource written to.
	 * @param password The callback to use for querying the needed password.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public void write(OutputStream output, KeyPair key, String resource, PasswordCallback password) throws IOException {
		assert output != null;

		write(new OutputStreamWriter(output, StandardCharsets.US_ASCII), key, resource, password);
	}

	/**
	 * Write Key object.
	 *
	 * @param writer The writer to use.
	 * @param key The Key object to write.
	 * @param resource The resource written to.
	 * @param password The callback to use for querying the needed password.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public void write(Writer writer, KeyPair key, String resource, PasswordCallback password) throws IOException {
		assert writer != null;
		assert key != null;
		assert resource != null;
		assert password != null;

		writeObject(writer, key, resource, password);
	}

	/**
	 * Write CSR object.
	 *
	 * @param output The output stream to write to.
	 * @param csr The CSR object to write.
	 * @param resource The resource written to.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public void write(OutputStream output, PKCS10CertificateRequest csr, String resource) throws IOException {
		assert output != null;

		write(new OutputStreamWriter(output, StandardCharsets.US_ASCII), csr, resource);
	}

	/**
	 * Write CSR object.
	 *
	 * @param writer The writer to use.
	 * @param csr The CSR object to write.
	 * @param resource The resource written to.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public void write(Writer writer, PKCS10CertificateRequest csr, String resource) throws IOException {
		assert writer != null;
		assert csr != null;
		assert resource != null;

		writeObject(writer, csr.toPKCS10(), resource);
	}

	/**
	 * Write CRL object.
	 *
	 * @param output The output stream to write to.
	 * @param crl The CRL object to write.
	 * @param resource The resource written to.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public void write(OutputStream output, X509CRL crl, String resource) throws IOException {
		assert output != null;

		write(new OutputStreamWriter(output, StandardCharsets.US_ASCII), crl, resource);
	}

	/**
	 * Write CRL object.
	 *
	 * @param writer The writer to use.
	 * @param crl The CRL object to write.
	 * @param resource The resource written to.
	 * @throws IOException if an I/O error occurs during encoding/writing.
	 */
	public void write(Writer writer, X509CRL crl, String resource) throws IOException {
		assert writer != null;
		assert crl != null;
		assert resource != null;

		writeObject(writer, crl, resource);
	}

	private void writeObject(Writer writer, Object object, String resource) throws IOException {
		LOG.debug("Writing PEM object ''{0}'' to: ''{1}''...", object.getClass().getName(), resource);

		try (JcaPEMWriter pemWriter = new JcaPEMWriter(writer)) {
			pemWriter.writeObject(object);
		}
	}

	private void writeObject(Writer writer, Object object, String resource, PasswordCallback password)
			throws IOException {
		LOG.debug("Writing encrypted PEM object ''{0}'' to: ''{1}''...", object.getClass().getName(), resource);

		char[] passwordChars = password.queryPassword(resource);

		if (passwordChars == null) {
			throw new PasswordRequiredException(resource);
		}
		try (JcaPEMWriter pemWriter = new JcaPEMWriter(writer)) {
			pemWriter.writeObject(object, this.pemEncryptorBuilder.build(passwordChars));
		}
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

			PEMDecryptorProvider pemDecryptorProvider = this.pemDecryptorBuilder.build(passwordChars);

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
