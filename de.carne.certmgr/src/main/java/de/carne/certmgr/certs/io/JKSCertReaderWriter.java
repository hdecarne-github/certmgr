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
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.PasswordRequiredException;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.util.Exceptions;
import de.carne.util.Strings;
import de.carne.util.logging.Log;

/**
 * Java KeyStore I/O support.
 */
public class JKSCertReaderWriter implements CertReader, CertWriter {

	private static final Log LOG = new Log();

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
	public List<Object> read(CertReaderInput input, PasswordCallback password) throws IOException {
		List<Object> keyStoreObjects = null;
		KeyStore keyStore;

		try {
			keyStore = loadKeyStore(input, password);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		} catch (PasswordRequiredException e) {
			throw e;
		} catch (IOException e) {
			Exceptions.ignore(e);
			keyStore = null;
		}
		if (keyStore != null) {
			try {
				keyStoreObjects = new ArrayList<>();

				Enumeration<String> aliases = keyStore.aliases();

				while (aliases.hasMoreElements()) {
					String alias = aliases.nextElement();
					Key aliasKey = null;
					Certificate aliasCertificate = null;

					if (keyStore.isKeyEntry(alias)) {
						aliasKey = getAliasKey(keyStore, alias, password);
					}
					if (keyStore.isCertificateEntry(alias)) {
						aliasCertificate = keyStore.getCertificate(alias);
					}
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

	private KeyStore loadKeyStore(CertReaderInput input, PasswordCallback password)
			throws GeneralSecurityException, IOException {
		KeyStore keyStore = null;
		char[] passwordChars = null;
		Throwable passwordException = null;

		do {
			try (InputStream keyStoreStream = input.stream()) {
				if (keyStoreStream != null) {
					if (keyStore == null) {
						keyStore = getKeyStoreInstance();
					}
					keyStore.load(input.stream(), passwordChars);
				}
				passwordException = null;
			} catch (IOException e) {
				if (e.getCause() instanceof UnrecoverableKeyException) {
					passwordException = e.getCause();
				} else {
					throw e;
				}
			}
			if (passwordException != null) {
				passwordChars = password.requeryPassword(input.toString(), passwordException);
				if (passwordChars == null) {
					throw new PasswordRequiredException(input.toString(), passwordException);
				}
			}
		} while (passwordException != null);
		return keyStore;
	}

	private Key getAliasKey(KeyStore keyStore, String alias, PasswordCallback password)
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

	private KeyPair getKey(String alias, Key aliasKey, Certificate aliasCertificate) {
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

	private X509Certificate getCRT(String alias, Certificate aliasCertificate) {
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

	private static KeyStore getKeyStoreInstance() throws KeyStoreException {
		return KeyStore.getInstance("jks");
	}

}
