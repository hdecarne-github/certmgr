/*
 * Copyright (c) 2015-2018 Holger de Carne and contributors, All Rights Reserved.
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
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.logging.Log;
import de.carne.certmgr.certs.CertObjectStore;
import de.carne.certmgr.certs.CertObjectType;
import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.PasswordRequiredException;
import de.carne.certmgr.certs.security.PlatformKeyStore;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.certmgr.certs.x509.KeyHelper;
import de.carne.util.Strings;

/**
 * Java KeyStore read/write support.
 */
public class JKSCertReaderWriter implements CertReader, CertWriter {

	private static final Log LOG = new Log(CertIOI18N.class.getName());

	private static final String KEYSTORE_TYPE = "JKS";

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
	public String[] fileExtensionPatterns() {
		return Strings.split(CertIOI18N.formatSTR_JKS_EXTENSION_PATTERNS(), '|', true);
	}

	@Override
	public String fileExtension(Class<?> cls) {
		return fileExtensionPatterns()[0].replace("*", "");
	}

	@Override
	@Nullable
	public CertObjectStore readBinary(IOResource<InputStream> in, PasswordCallback password) throws IOException {
		LOG.debug("Trying to read KeyStore objects from file: ''{0}''...", in);

		return readKeyStore(KEYSTORE_TYPE, in.io(), in.resource(), password);
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
		return true;
	}

	@Override
	public void writeBinary(IOResource<OutputStream> out, CertObjectStore certObjects)
			throws IOException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeEncryptedBinary(IOResource<OutputStream> out, CertObjectStore certObjects,
			PasswordCallback newPassword) throws IOException {
		char[] passwordChars = newPassword.queryPassword(out.resource());

		if (passwordChars == null) {
			throw new PasswordRequiredException(out.resource());
		}
		try {
			KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);

			keyStore.load(null, null);

			List<X509Certificate> crtChain = new ArrayList<>(certObjects.size());

			for (CertObjectStore.Entry certObject : certObjects) {
				if (certObject.type() == CertObjectType.CRT) {
					keyStore.setCertificateEntry(certObject.alias(), certObject.getCRT());
					crtChain.add(0, certObject.getCRT());
				}
			}
			for (CertObjectStore.Entry certObject : certObjects) {
				if (certObject.type() == CertObjectType.KEY) {
					keyStore.setKeyEntry(certObject.alias(), certObject.getKey().getPrivate(), passwordChars,
							crtChain.toArray(new X509Certificate[crtChain.size()]));
					crtChain.clear();
				}
			}
			keyStore.store(out.io(), passwordChars);
		} catch (GeneralSecurityException e) {
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

	/**
	 * Read certificate object from a platform store.
	 *
	 * @param platformKeyStore The platform store to read from.
	 * @param password The callback to use for querying passwords (if needed).
	 * @return The list of read certificate objects, or {@code null} if the input is not recognized.
	 * @throws IOException if an I/O error occurs while reading.
	 */
	@Nullable
	public static CertObjectStore readPlatformKeyStore(PlatformKeyStore platformKeyStore, PasswordCallback password)
			throws IOException {
		LOG.debug("Trying to read KeyStore objects from platform store: ''{0}''...", platformKeyStore);

		return readKeyStore(platformKeyStore.algorithm(), null, platformKeyStore.algorithm(), password);
	}

	@Nullable
	private static CertObjectStore readKeyStore(String keyStoreType, @Nullable InputStream inputStream, String resource,
			PasswordCallback password) throws IOException {
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

		CertObjectStore certObjects = null;

		if (keyStore != null) {
			try {
				certObjects = new CertObjectStore();

				Enumeration<String> aliases = keyStore.aliases();

				while (aliases.hasMoreElements()) {
					String alias = aliases.nextElement();
					Certificate aliasCertificate = keyStore.getCertificate(alias);

					if (aliasCertificate != null) {
						if (aliasCertificate instanceof X509Certificate) {
							certObjects.addCRT((X509Certificate) aliasCertificate);
						} else {
							LOG.warning(
									"Ignoring certificate of key store entry ''{0}'' due to unsupported type ''{1}''",
									alias, aliasCertificate.getClass().getName());
						}
					}

					Key aliasKey = getAliasKey(keyStore, alias, password);

					if (aliasKey != null) {
						if (aliasKey instanceof PrivateKey) {
							try {
								certObjects.addKey(KeyHelper.rebuildKeyPair((PrivateKey) aliasKey));
							} catch (IOException e) {
								LOG.warning(e, "Unable to rebuild key pair for private key ''{0}'' of type ''{1}''",
										alias, aliasKey.getClass().getName());
							}
						} else {
							LOG.warning("Ignoring key of key store entry ''{0}'' due to unsupported type ''{1}''",
									alias, aliasKey.getClass().getName());
						}
					}

					Certificate[] aliasChain = keyStore.getCertificateChain(alias);

					if (aliasChain != null) {
						for (Certificate aliasChainEntry : aliasChain) {
							if (aliasChainEntry instanceof X509Certificate) {
								certObjects.addCRT((X509Certificate) aliasChainEntry);
							} else {
								LOG.warning(
										"Ignoring chain certificate of key store entry ''{0}'' due to unsupported type ''{1}''",
										alias, aliasChainEntry.getClass().getName());
							}
						}
					}
				}
			} catch (GeneralSecurityException e) {
				throw new CertProviderException(e);
			}
		}
		return certObjects;
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

	@Nullable
	private static Key getAliasKey(KeyStore keyStore, String alias, PasswordCallback password)
			throws GeneralSecurityException {
		Key key = null;
		Throwable passwordException = null;

		do {
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
		} while (key == null && passwordException != null);
		return key;
	}

	@Override
	public String toString() {
		return fileType();
	}

}
