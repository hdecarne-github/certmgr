/*
 * Copyright (c) 2014-2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.store.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import de.carne.certmgr.store.PKCS10Object;
import de.carne.certmgr.store.PasswordCallback;
import de.carne.certmgr.store.PasswordRequiredException;
import de.carne.certmgr.store.x509.CertificateValidity;
import de.carne.certmgr.store.x509.EncodedX509Extension;
import de.carne.certmgr.store.x509.KeyParams;
import de.carne.certmgr.store.x509.RevokeReason;
import de.carne.certmgr.store.x509.X509CRLParams;
import de.carne.certmgr.store.x509.X509CertificateParams;
import de.carne.certmgr.util.logging.Log;

/**
 * Abstract base class for certificate store providers.
 */
public abstract class StoreProvider {

	private static final Log LOG = new Log(StoreProvider.class);

	private static final String THIS_PACKAGE = StoreProvider.class.getPackage().getName();

	private static final String KEY_PROVIDER_INFO = "info";

	private static final String KEY_KEYALG = "keyalg";

	private static final String KEY_KEYSIZE = "keysize";

	private static final String KEY_SIGALG = "sigalg";

	/**
	 * BouncyCastle provider.
	 */
	public static final String PROVIDER_BOUNCYCASTLE = THIS_PACKAGE + ".bouncycastle.BouncyCastleStoreProvider";

	/**
	 * Default provider (BouncyCastle).
	 */
	public static final String PROVIDER_DEFAULT = PROVIDER_BOUNCYCASTLE;

	/**
	 * Get an instance of the currently configured StoreProvider implementation.
	 *
	 * @return A StoreProvider instance.
	 */
	public static StoreProvider getInstance() {
		String providerName = System.getProperty(THIS_PACKAGE, PROVIDER_DEFAULT);

		return getInstance(providerName);
	}

	/**
	 * Get an instance of a specific StoreProvider implementation.
	 *
	 * @param providerName The provider name to get the instance for.
	 * @return A StoreProvider instance.
	 */
	public static StoreProvider getInstance(String providerName) {
		LOG.debug(null, "Loading store provider ''{0}'' ...", providerName);

		StoreProvider provider;

		try {
			Class<? extends StoreProvider> providerClass = Thread.currentThread().getContextClassLoader()
					.loadClass(providerName).asSubclass(StoreProvider.class);

			provider = providerClass.newInstance();
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			throw new IllegalStateException("", e);
		}
		LOG.debug(null, "Loading store provider ''{0}'' done", providerName);
		return provider;
	}

	/**
	 * Get provider properties.
	 *
	 * @return Provider properties.
	 */
	public abstract Properties getProperties();

	/**
	 * Get the provider's info string containing of name and version.
	 *
	 * @return The provider's info string.
	 */
	public String getInfo() {
		return getProperties().getProperty(KEY_PROVIDER_INFO);
	}

	/**
	 * Get the provider supported key algorithms.
	 *
	 * @return The provider supported key algorithms.
	 */
	public SortedSet<String> getKeyAlgs() {
		Properties providerProperties = getProperties();
		String baseKey = KEY_KEYALG + ".";
		TreeSet<String> keyAlgs = new TreeSet<>();

		for (int keyAlgIndex = 1;; keyAlgIndex++) {
			String keyAlgKey = baseKey + keyAlgIndex;
			String keyAlg = providerProperties.getProperty(keyAlgKey);

			if (keyAlg == null) {
				break;
			}
			keyAlgs.add(keyAlg);
		}
		return keyAlgs;
	}

	/**
	 * Get the provider's default key algorithm.
	 *
	 * @return The provider's default key algorithm.
	 */
	public String getDefaultKeyAlg() {
		return getProperties().getProperty(KEY_KEYALG);
	}

	/**
	 * Get the provider supported key sizes.
	 *
	 * @param keyAlg The key algorithm to get the key sizes for.
	 * @return The provider supported key sizes.
	 */
	public SortedSet<Integer> getKeySizes(String keyAlg) {
		Properties providerProperties = getProperties();
		String baseKey = keyAlg + "." + KEY_KEYSIZE + ".";
		TreeSet<Integer> keySizes = new TreeSet<>();

		for (int keySizeIndex = 1;; keySizeIndex++) {
			String keySizeKey = baseKey + keySizeIndex;
			String keySize = providerProperties.getProperty(keySizeKey);

			if (keySize == null) {
				break;
			}
			keySizes.add(Integer.valueOf(keySize));
		}
		return keySizes;
	}

	/**
	 * Get the provider's default key size.
	 *
	 * @param keyAlg The key algorithm to get the default key size for.
	 * @return The provider's default key size.
	 */
	public Integer getDefaultKeySize(String keyAlg) {
		return Integer.valueOf(getProperties().getProperty(keyAlg + "." + KEY_KEYSIZE));
	}

	/**
	 * Get the provider supported signature algorithms.
	 *
	 * @param keyAlg The key algorithm to get the signature algorithms for.
	 * @return The provider supported signature algorithms.
	 */
	public SortedSet<String> getSigAlgs(String keyAlg) {
		Properties providerProperties = getProperties();
		String baseKey = keyAlg + "." + KEY_SIGALG + ".";
		TreeSet<String> sigAlgs = new TreeSet<>();

		for (int sigAlgIndex = 1;; sigAlgIndex++) {
			String sigAlgKey = baseKey + sigAlgIndex;
			String sigAlg = providerProperties.getProperty(sigAlgKey);

			if (sigAlg == null) {
				break;
			}
			sigAlgs.add(sigAlg);
		}
		return sigAlgs;
	}

	/**
	 * Get the provider's default signature algorithm.
	 *
	 * @param keyAlg The key algorithm to get the default signature algorithm
	 *        for.
	 * @return The provider's default signature algorithm.
	 */
	public String getDefaultSigAlg(String keyAlg) {
		return getProperties().getProperty(keyAlg + "." + KEY_SIGALG);
	}

	/**
	 * Get the default security provider to use.
	 *
	 * @return The default security provider to use (may be null).
	 */
	protected String getDefaultSecurityProvider() {
		return null;
	}

	/**
	 * Get the default random source to use.
	 *
	 * @return The default random source to use.
	 * @throws NoSuchAlgorithmException if no algorithm is available.
	 */
	protected SecureRandom getDefaultSecureRandom() throws NoSuchAlgorithmException {
		return SecureRandom.getInstanceStrong();
	}

	/**
	 * Generate key.
	 *
	 * @param keyParams The parameters to use for key generation.
	 * @return The generated key.
	 * @throws GeneralSecurityException if an security provider related error
	 *         occurs.
	 */
	public KeyPair generateKey(KeyParams keyParams) throws GeneralSecurityException {
		String keyAlg = keyParams.getKeyAlg();
		String provider = keyParams.getProvider();

		if (provider == null) {
			provider = getDefaultSecurityProvider();
		}

		KeyPairGenerator keyGenerator;

		if (provider != null) {
			keyGenerator = KeyPairGenerator.getInstance(keyAlg, provider);
		} else {
			keyGenerator = KeyPairGenerator.getInstance(keyAlg);
		}

		SecureRandom random = keyParams.getRandom();

		if (random == null) {
			random = getDefaultSecureRandom();
		}

		int keySize = keyParams.getKeySize();

		LOG.notice(I18N.bundle(), I18N.MESSAGE_GENERATEKEY, keyAlg, Integer.toString(keySize));

		keyGenerator.initialize(keySize, random);

		KeyPair key = keyGenerator.generateKeyPair();

		return key;
	}

	/**
	 * Generate and sign CRT.
	 *
	 * @param key The CRT's key.
	 * @param certificateParams The parameters to use for CRT generation.
	 * @param certificateValidity The validity range for the generated CRT.
	 * @param issuerKey The issuer key (null for self-signed CRTs).
	 * @param issuerCRT The issuer CRT (null for self-signed CRTs).
	 * @param serial The serial for the new CRT.
	 * @return The generated CRT.
	 * @throws IOException if an I/O error occurs during CRT generation.
	 * @throws GeneralSecurityException if an security provider related error
	 *         occurs.
	 */
	public abstract X509Certificate generateAndSignCRT(KeyPair key, X509CertificateParams certificateParams,
			CertificateValidity certificateValidity, KeyPair issuerKey, X509Certificate issuerCRT, BigInteger serial)
			throws IOException, GeneralSecurityException;

	/**
	 * Re-generate and re-sign CRT.
	 *
	 * @param crt The CRT to re-generate and re-sign.
	 * @param certificateParams The parameters to use for CRT generation.
	 * @param certificateValidity The validity range for the generated CRT.
	 * @param issuerKey The issuer key.
	 * @return The generated CRT.
	 * @throws IOException if an I/O error occurs during CRT generation.
	 * @throws GeneralSecurityException if an security provider related error
	 *         occurs.
	 */
	public abstract X509Certificate generateAndSignCRT(X509Certificate crt, X509CertificateParams certificateParams,
			CertificateValidity certificateValidity, KeyPair issuerKey) throws IOException, GeneralSecurityException;

	/**
	 * Generate and sign CSR.
	 *
	 * @param key The CSR's key.
	 * @param certificateParams The parameters to use for CRT generation.
	 * @return The generated CSR.
	 * @throws IOException if an I/O error occurs during CSR generation.
	 * @throws GeneralSecurityException if an security provider related error
	 *         occurs.
	 */
	public abstract PKCS10Object generateAndSignCSR(KeyPair key, X509CertificateParams certificateParams)
			throws IOException, GeneralSecurityException;

	/**
	 * Re-generate and re-sign CSR.
	 *
	 * @param csr The CSR to re-generate and re-sign.
	 * @param key The CSR's key.
	 * @param certificateParams The parameters to use for CRT generation.
	 * @return The generated CSR.
	 * @throws IOException if an I/O error occurs during CSR generation.
	 * @throws GeneralSecurityException if an security provider related error
	 *         occurs.
	 */
	public abstract PKCS10Object generateAndSignCSR(PKCS10Object csr, KeyPair key,
			X509CertificateParams certificateParams) throws IOException, GeneralSecurityException;

	/**
	 * Generate and sign CRL.
	 *
	 * @param currentCRL The previously generated CRL to overwrite (may be
	 *        null).
	 * @param crlParams The parameters to use for CRL generation.
	 * @param revokeSerials The serial numbers to revoke.
	 * @param issuerKey The issuer key.
	 * @param issuerCRT The issuer CRT.
	 * @return The generated CRL.
	 * @throws IOException if an I/O error occurs during CRL generation.
	 * @throws GeneralSecurityException if an security provider related error
	 *         occurs.
	 */
	public abstract X509CRL generateAndSignCRL(X509CRL currentCRL, X509CRLParams crlParams,
			Map<BigInteger, RevokeReason> revokeSerials, KeyPair issuerKey, X509Certificate issuerCRT)
			throws IOException, GeneralSecurityException;

	/**
	 * Write key object to file.
	 *
	 * @param key The key object to write.
	 * @param keyFile The file to write to.
	 * @param password The password callback to use for key encryption (may be
	 *        null).
	 * @param resource The name of the resource to encode for password queries.
	 * @throws PasswordRequiredException if a password callback was submitted
	 *         but provided no password.
	 * @throws IOException if an I/O error occurs while writing the key object.
	 */
	public abstract void writeKey(KeyPair key, Path keyFile, PasswordCallback password, String resource)
			throws PasswordRequiredException, IOException;

	/**
	 * Read key object from file.
	 *
	 * @param keyFile The file to read from.
	 * @param password The password callback to use for key decryption (may be
	 *        null).
	 * @param resource The name of the resource the key belongs to.
	 * @return The read key object.
	 * @throws PasswordRequiredException if a password is required to access the
	 *         key object.
	 * @throws IOException if an I/O error occurs while reading the key object.
	 */
	public abstract KeyPair readKey(Path keyFile, PasswordCallback password, String resource)
			throws PasswordRequiredException, IOException;

	/**
	 * Write CRT object to file.
	 *
	 * @param crt The CRT object to write.
	 * @param crtFile The file to write to.
	 * @throws IOException if an I/O error occurs while writing the CRT object.
	 */
	public abstract void writeCRT(X509Certificate crt, Path crtFile) throws IOException;

	/**
	 * Read CRT object from file.
	 *
	 * @param crtFile The file to read from.
	 * @return The read CRT object.
	 * @throws IOException if an I/O error occurs while reading the CRT object.
	 */
	public abstract X509Certificate readCRT(Path crtFile) throws IOException;

	/**
	 * Write CSR object to file.
	 *
	 * @param csr The CSR object to write.
	 * @param csrFile The file to write to.
	 * @throws IOException if an I/O error occurs while writing the CSR object.
	 */
	public abstract void writeCSR(PKCS10Object csr, Path csrFile) throws IOException;

	/**
	 * Read CSR object from file.
	 *
	 * @param csrFile The file to read from.
	 * @return The read CSR object.
	 * @throws IOException if an I/O error occurs while reading the CSR object.
	 */
	public abstract PKCS10Object readCSR(Path csrFile) throws IOException;

	/**
	 * Write CRL object to file.
	 *
	 * @param crl The CRL object to write.
	 * @param crlFile The file to write to.
	 * @throws IOException if an I/O error occurs while writing the CRL object.
	 */
	public abstract void writeCRL(X509CRL crl, Path crlFile) throws IOException;

	/**
	 * Read CRL object from file.
	 *
	 * @param crlFile The file to read from.
	 * @return The read CRL object.
	 * @throws IOException if an I/O error occurs while reading the CRL object.
	 */
	public abstract X509CRL readCRL(Path crlFile) throws IOException;

	/**
	 * Decode an extension.
	 *
	 * @param oid The extension's oid.
	 * @param critical The extension's critical flag.
	 * @param encoded The encoded extension data.
	 * @return The decoded extension.
	 * @throws IOException
	 */
	public abstract EncodedX509Extension decodeExtension(String oid, boolean critical, byte[] encoded)
			throws IOException;

	/**
	 * Try to decode certificate objects from PEM encoded data.
	 *
	 * @param pemData The PEM data to decode.
	 * @param password The password callback to use for key decryption (may be
	 *        null).
	 * @param resource The name of the resource to decode for password queries.
	 * @return The list of decoded objects or null if data is not PEM encoded.
	 * @throws IOException if an I/O error occurs during PEM decoding.
	 */
	public abstract Collection<Object> tryDecodePEM(String pemData, PasswordCallback password, String resource)
			throws IOException;

	/**
	 * Try to decode certificate objects from PKCS#12 encoded data.
	 *
	 * @param pkcs12Data The PKCS#12 data to decode.
	 * @param password The password callback to use for key decryption (may be
	 *        null).
	 * @param resource The name of the resource to decode for password queries.
	 * @return The list of decoded objects or null if data is not PKCS#12
	 *         encoded.
	 * @throws IOException if an I/O error occurs during PKCS#12 decoding.
	 */
	public abstract Collection<Object> tryDecodePKCS12(byte[] pkcs12Data, PasswordCallback password, String resource)
			throws IOException;

	/**
	 * Encode certificate objects into a PEM data stream.
	 *
	 * @param crtChain The CRT chain to encode starting with the issued ones and
	 *        (optionally) continuing with the issuers (may be null).
	 * @param key The key pair to encode (may be null).
	 * @param csr The CSR to encode (may be null).
	 * @param crl The CRL to encode (may be null).
	 * @param password The password callback to use for key encryption (may be
	 *        null).
	 * @param resource The name of the resource to encode for password queries.
	 * @return The encoded PEM data.
	 * @throws IOException if an I/O error occurs during encoding.
	 * @throws PasswordRequiredException if no password was provided during
	 *         encoding.
	 */
	public abstract String encodePEM(X509Certificate[] crtChain, KeyPair key, PKCS10Object csr, X509CRL crl,
			PasswordCallback password, String resource) throws IOException, PasswordRequiredException;

	/**
	 * Encode certificate objects into a PKCS#12 data stream.
	 *
	 * @param crtChain The CRT chain to encode starting with the issued ones and
	 *        (optionally) continuing with the issuers (may be null).
	 * @param key The key pair to encode (may be null).
	 * @param csr The CSR to encode (may be null).
	 * @param crl The CRL to encode (may be null).
	 * @param password The password callback to use for key encryption (may be
	 *        null).
	 * @param resource The name of the resource to encode for password queries.
	 * @return The encoded PKCS#12 data.
	 * @throws IOException if an I/O error occurs during encoding.
	 * @throws PasswordRequiredException if no password was provided during
	 *         encoding.
	 */
	public abstract byte[] encodePKCS12(X509Certificate[] crtChain, KeyPair key, PKCS10Object csr, X509CRL crl,
			PasswordCallback password, String resource) throws IOException, PasswordRequiredException;

}
