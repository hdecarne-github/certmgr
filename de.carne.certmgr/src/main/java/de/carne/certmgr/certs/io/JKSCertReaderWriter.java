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
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.PasswordRequiredException;
import de.carne.certmgr.certs.security.PlatformKeyStore;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.util.Strings;
import de.carne.util.logging.Log;

/**
 * Java KeyStore I/O support.
 */
public class JKSCertReaderWriter implements CertReader, CertWriter {

	private static final Log LOG = new Log();

	private static final String INPUT_KEYSTORE_TYPE = "CaseExactJKS";

	/**
	 * Provider name.
	 */
	public static final String PROVIDER_NAME = "JKS";

	@Override
	public String providerName() {
		return PROVIDER_NAME;
	}

	@Override
	public String fileType() {
		return CertIOI18N.formatSTR_JKS_TYPE();
	}

	@Override
	public String[] fileExtensions() {
		return Strings.split(CertIOI18N.formatSTR_JKS_EXTENSIONS(), "|");
	}

	@Override
	@Nullable
	public List<Object> read(CertReaderInput input, PasswordCallback password) throws IOException {
		assert input != null;
		assert password != null;

		LOG.debug("Trying to read KeyStore objects from file: ''{0}''...", input);

		List<Object> keyStoreObjects = null;

		try (InputStream inputStream = input.stream()) {
			if (inputStream != null) {
				keyStoreObjects = readKeyStore(INPUT_KEYSTORE_TYPE, inputStream, input.toString(), password);
			}
		}
		return keyStoreObjects;
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
	public void writeBinary(OutputStream out, List<Object> certObjects)
			throws IOException, UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeEncryptedBinary(OutputStream out, List<Object> certObjects, PasswordCallback newPassword)
			throws IOException, UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeString(Writer out, List<Object> certObjects) throws IOException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeEncryptedString(Writer out, List<Object> certObjects, PasswordCallback newPassword)
			throws IOException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Read certificate object from a platform store.
	 *
	 * @param platformKeyStore The platform store to read from.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The list of read certificate objects, or {@code null} if the
	 *         input is not recognized.
	 * @throws IOException if an I/O error occurs while reading.
	 */
	@Nullable
	public static List<Object> readPlatformKeyStore(PlatformKeyStore platformKeyStore, PasswordCallback password)
			throws IOException {
		assert platformKeyStore != null;
		assert password != null;

		LOG.debug("Trying to read KeyStore objects from platform store: ''{0}''...", platformKeyStore);

		return readKeyStore(platformKeyStore.algorithm(), null, platformKeyStore.toString(), password);
	}

	@Nullable
	private static List<Object> readKeyStore(String keyStoreType, @Nullable InputStream inputStream, String resource,
			PasswordCallback password) throws IOException {
		List<Object> keyStoreObjects = null;
		KeyStore keyStore = null;

		try {
			keyStore = loadKeyStore(keyStoreType, inputStream, resource, password);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		} catch (PasswordRequiredException e) {
			throw e;
		} catch (IOException e) {
			LOG.info(e, "No KeyStore objects recognized in: ''{0}''", resource);
		}
		if (keyStore != null) {
			try {
				keyStoreObjects = new ArrayList<>();

				Enumeration<String> aliases = keyStore.aliases();

				while (aliases.hasMoreElements()) {
					String alias = aliases.nextElement();
					Key aliasKey = getAliasKey(keyStore, alias, password);
					Certificate aliasCertificate = keyStore.getCertificate(alias);

					if (aliasKey == null && aliasCertificate == null) {
						LOG.warning("Ignoring key store entry ''{0}'' due to missing data", alias);
					} else {
						KeyPair key = getKey(alias, aliasKey, aliasCertificate);

						if (key != null) {
							keyStoreObjects.add(key);
						}

						X509Certificate crt = getCRT(alias, aliasCertificate);

						if (crt != null) {
							keyStoreObjects.add(crt);
						}
					}
				}
			} catch (GeneralSecurityException e) {
				throw new CertProviderException(e);
			}
		}
		return keyStoreObjects;
	}

	private static KeyStore loadKeyStore(String keyStoreType, @Nullable InputStream inputStream, String resource,
			PasswordCallback password) throws GeneralSecurityException, IOException {
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		char[] passwordChars = null;
		Throwable passwordException = null;

		do {
			try {
				keyStore.load(inputStream, passwordChars);
				passwordException = null;
			} catch (IOException e) {
				if (e.getCause() instanceof UnrecoverableKeyException) {
					passwordException = e.getCause();
				} else {
					throw e;
				}
			}
			if (passwordException != null) {
				passwordChars = password.requeryPassword(resource, passwordException);
				if (passwordChars == null) {
					throw new PasswordRequiredException(resource, passwordException);
				}
			}
		} while (passwordException != null);
		return keyStore;
	}

	private static Key getAliasKey(KeyStore keyStore, String alias, PasswordCallback password)
			throws GeneralSecurityException {
		Key key = null;
		Throwable passwordException = null;

		while (key == null) {
			char[] passwordChars = (passwordException != null ? password.requeryPassword(alias, passwordException)
					: password.queryPassword(alias));

			if (passwordChars == null) {
				break;
			}
			try {
				key = keyStore.getKey(alias, passwordChars);
			} catch (UnrecoverableKeyException e) {
				passwordException = e;
			}
		}
		return key;
	}

	private static KeyPair getKey(String alias, Key aliasKey, Certificate aliasCertificate) {
		KeyPair keyPair = null;

		if (aliasKey != null) {
			if (!(aliasKey instanceof PrivateKey)) {
				LOG.info("Ignoring key entry ''{0}'' due to unsupported key type ''{1}''", alias,
						aliasKey.getClass().getName());
			} else if (aliasCertificate == null) {
				LOG.info("Ignoring key entry ''{0}'' due to missing public key", alias);
			} else {
				keyPair = new KeyPair(aliasCertificate.getPublicKey(), (PrivateKey) aliasKey);
			}
		}
		return keyPair;
	}

	private static X509Certificate getCRT(String alias, Certificate aliasCertificate) {
		X509Certificate crt = null;

		if (aliasCertificate != null) {
			if (!(aliasCertificate instanceof X509Certificate)) {
				LOG.info("Ignoring certificate entry ''{0}'' due to unsupported certificate type ''{1}''", alias,
						aliasCertificate.getClass().getName());
			} else {
				crt = (X509Certificate) aliasCertificate;
			}
		}
		return crt;
	}

	@Override
	public String toString() {
		return fileType();
	}

}
