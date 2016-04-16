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
package de.carne.certmgr.store.provider.bouncycastle;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.EncryptionException;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;

import de.carne.certmgr.store.PKCS10Object;
import de.carne.certmgr.store.PasswordCallback;
import de.carne.certmgr.store.PasswordRequiredException;
import de.carne.certmgr.store.StoreProviderException;
import de.carne.certmgr.store.provider.StoreProvider;
import de.carne.certmgr.store.x509.CertificateValidity;
import de.carne.certmgr.store.x509.EncodedX509Extension;
import de.carne.certmgr.store.x509.RevokeReason;
import de.carne.certmgr.store.x509.X509CRLParams;
import de.carne.certmgr.store.x509.X509CertificateParams;
import de.carne.certmgr.store.x509.X509Extension;
import de.carne.util.logging.Log;

/**
 * BouncyCastle Provider.
 */
public class BouncyCastleStoreProvider extends StoreProvider {

	private static final Log LOG = new Log(BouncyCastleStoreProvider.class);

	private static final Charset PEM_CHARSET = Charset.forName("US-ASCII");

	private static final String PEM_ENCRYPTOR_ALGORTIHM = "AES-128-CBC";

	private static final Provider PROVIDER = new BouncyCastleProvider();

	private static final String PROPERTIES_RESOURCE = "BouncyCastle.properties";

	private static final Properties PROPERTIES = new Properties();

	static {
		LOG.debug(null, "Adding BouncyCastle security provider...");
		Security.addProvider(PROVIDER);
		try (InputStream propertyStream = BouncyCastleStoreProvider.class.getResourceAsStream(PROPERTIES_RESOURCE)) {
			PROPERTIES.load(propertyStream);
		} catch (IOException e) {
			LOG.error(e, null, e.getLocalizedMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#getProperties()
	 */
	@Override
	public Properties getProperties() {
		return PROPERTIES;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#getInfo()
	 */
	@Override
	public String getInfo() {
		return PROVIDER.getInfo();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#getDefaultSecurityProvider()
	 */
	@Override
	protected String getDefaultSecurityProvider() {
		return BouncyCastleProvider.PROVIDER_NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#generateAndSignCRT(java.security.KeyPair,
	 * de.carne.certmgr.store.x509.X509CertificateParams, de.carne.certmgr.store.x509.CertificateValidity,
	 * java.security.KeyPair, java.security.cert.X509Certificate, java.math.BigInteger)
	 */
	@Override
	public X509Certificate generateAndSignCRT(KeyPair key, X509CertificateParams certificateParams,
			CertificateValidity certificateValidity, KeyPair issuerKey, X509Certificate issuerCRT, BigInteger serial)
					throws IOException, GeneralSecurityException {
		X500Principal issuerSubjectDN = (issuerCRT != null ? issuerCRT.getSubjectX500Principal() : certificateParams
				.getSubjectDN());
		Date validFrom = Date.from(certificateValidity.getValidFrom().atStartOfDay().atZone(ZoneId.systemDefault())
				.toInstant());
		Date validTo = Date.from(certificateValidity.getValidTo().atStartOfDay().atZone(ZoneId.systemDefault())
				.toInstant());
		X500Principal subjectDN = certificateParams.getSubjectDN();
		X509v3CertificateBuilder crtBuilder = new JcaX509v3CertificateBuilder(issuerSubjectDN, serial, validFrom,
				validTo, subjectDN, key.getPublic());

		addKeyIdentifierExtensions(crtBuilder, key.getPublic(),
				(issuerKey != null ? issuerKey.getPublic() : key.getPublic()));
		addCustomExtensions(crtBuilder, certificateParams);

		LOG.notice(I18N.bundle(), I18N.MESSAGE_GENERATECRT, subjectDN);

		ContentSigner crtSigner;

		try {
			crtSigner = new JcaContentSignerBuilder(certificateParams.getSigAlg()).build((issuerKey != null ? issuerKey
					.getPrivate() : key.getPrivate()));
		} catch (OperatorCreationException e) {
			throw new StoreProviderException(e.getMessage(), e);
		}
		return new JcaX509CertificateConverter().getCertificate(crtBuilder.build(crtSigner));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#generateAndSignCRT(java.security.cert.X509Certificate,
	 * de.carne.certmgr.store.x509.X509CertificateParams, de.carne.certmgr.store.x509.CertificateValidity,
	 * java.security.KeyPair)
	 */
	@Override
	public X509Certificate generateAndSignCRT(X509Certificate crt, X509CertificateParams certificateParams,
			CertificateValidity certificateValidity, KeyPair issuerKey) throws IOException, GeneralSecurityException {
		X500Principal issuerSubjectDN = crt.getIssuerX500Principal();
		BigInteger serial = crt.getSerialNumber();
		Date validFrom = Date.from(certificateValidity.getValidFrom().atStartOfDay().atZone(ZoneId.systemDefault())
				.toInstant());
		Date validTo = Date.from(certificateValidity.getValidTo().atStartOfDay().atZone(ZoneId.systemDefault())
				.toInstant());
		X500Principal subjectDN = crt.getSubjectX500Principal();
		X509v3CertificateBuilder crtBuilder = new JcaX509v3CertificateBuilder(issuerSubjectDN, serial, validFrom,
				validTo, subjectDN, crt.getPublicKey());

		addKeyIdentifierExtensions(crtBuilder, crt.getPublicKey(), issuerKey.getPublic());
		addCustomExtensions(crtBuilder, certificateParams);

		LOG.notice(I18N.bundle(), I18N.MESSAGE_GENERATECRT, subjectDN);

		ContentSigner crtSigner;

		try {
			crtSigner = new JcaContentSignerBuilder(certificateParams.getSigAlg()).build(issuerKey.getPrivate());
		} catch (OperatorCreationException e) {
			throw new StoreProviderException(e.getMessage(), e);
		}
		return new JcaX509CertificateConverter().getCertificate(crtBuilder.build(crtSigner));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#generateAndSignCSR(java.security.KeyPair,
	 * de.carne.certmgr.store.x509.X509CertificateParams)
	 */
	@Override
	public PKCS10Object generateAndSignCSR(KeyPair key, X509CertificateParams certificateParams) throws IOException,
	GeneralSecurityException {
		X500Principal subjectDN = certificateParams.getSubjectDN();
		PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(subjectDN,
				key.getPublic());

		addCustomExtensions(csrBuilder, certificateParams);

		LOG.notice(I18N.bundle(), I18N.MESSAGE_GENERATECSR, subjectDN);

		ContentSigner csrSigner;

		try {
			csrSigner = new JcaContentSignerBuilder(certificateParams.getSigAlg()).build(key.getPrivate());
		} catch (OperatorCreationException e) {
			throw new StoreProviderException(e.getMessage(), e);
		}
		return new BouncyCastlePKCS10Object(csrBuilder.build(csrSigner));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#generateAndSignCSR(de.carne.certmgr.store.PKCS10Object,
	 * java.security.KeyPair, de.carne.certmgr.store.x509.X509CertificateParams)
	 */
	@Override
	public PKCS10Object generateAndSignCSR(PKCS10Object csr, KeyPair key, X509CertificateParams certificateParams)
			throws IOException, GeneralSecurityException {
		X500Principal subjectDN = csr.getSubjectX500Principal();
		PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(subjectDN,
				key.getPublic());

		addCustomExtensions(csrBuilder, certificateParams);

		LOG.notice(I18N.bundle(), I18N.MESSAGE_GENERATECSR, subjectDN);

		ContentSigner csrSigner;

		try {
			csrSigner = new JcaContentSignerBuilder(certificateParams.getSigAlg()).build(key.getPrivate());
		} catch (OperatorCreationException e) {
			throw new StoreProviderException(e.getMessage(), e);
		}
		return new BouncyCastlePKCS10Object(csrBuilder.build(csrSigner));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#generateAndSignCRL(java.security.cert.X509CRL,
	 * de.carne.certmgr.store.x509.X509CRLParams, java.util.Map, java.security.KeyPair,
	 * java.security.cert.X509Certificate)
	 */
	@Override
	public X509CRL generateAndSignCRL(X509CRL currentCRL, X509CRLParams crlParams,
			Map<BigInteger, RevokeReason> revokeSerials, KeyPair issuerKey, X509Certificate issuerCRT)
			throws IOException, GeneralSecurityException {
		Date lastUpdate = Date
				.from(crlParams.getLastUpdate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		JcaX509v2CRLBuilder crlBuilder = new JcaX509v2CRLBuilder(issuerCRT.getSubjectX500Principal(), lastUpdate);
		LocalDate nextUpdateParam = crlParams.getNextUpdate();

		if (nextUpdateParam != null) {
			crlBuilder.setNextUpdate(Date.from(nextUpdateParam.atStartOfDay().atZone(ZoneId.systemDefault())
					.toInstant()));
		}

		CRLNumber crlNumber;

		if (currentCRL != null) {
			X509CRLHolder crlHolder = new X509CRLHolder(currentCRL.getEncoded());
			ASN1Integer currentSerial = (ASN1Integer) crlHolder.getExtension(Extension.cRLNumber).getParsedValue();

			crlNumber = new CRLNumber(currentSerial.getValue().add(BigInteger.ONE));
		} else {
			crlNumber = new CRLNumber(BigInteger.ONE);
		}
		for (Map.Entry<BigInteger, RevokeReason> revokeListEntry : revokeSerials.entrySet()) {
			crlBuilder.addCRLEntry(revokeListEntry.getKey(), lastUpdate, revokeListEntry.getValue().value());
		}

		JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();

		crlBuilder.addExtension(Extension.authorityKeyIdentifier, false,
				extensionUtils.createAuthorityKeyIdentifier(issuerCRT.getPublicKey()));
		crlBuilder.addExtension(Extension.cRLNumber, false, crlNumber);

		ContentSigner crlSigner;

		try {
			crlSigner = new JcaContentSignerBuilder(crlParams.getSigAlg()).build(issuerKey.getPrivate());
		} catch (OperatorCreationException e) {
			throw new StoreProviderException(e.getMessage(), e);
		}
		return new JcaX509CRLConverter().getCRL(crlBuilder.build(crlSigner));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#writeKey(java.security.KeyPair, java.nio.file.Path,
	 * de.carne.certmgr.store.PasswordCallback, java.lang.String)
	 */
	@Override
	public void writeKey(KeyPair key, Path keyFile, PasswordCallback password, String resource)
			throws PasswordRequiredException, IOException {
		writePEMObject(keyFile, key, password, resource);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#readKey(java.nio.file.Path,
	 * de.carne.certmgr.store.PasswordCallback, java.lang.String)
	 */
	@Override
	public KeyPair readKey(Path keyFile, PasswordCallback password, String resource) throws PasswordRequiredException,
			IOException {
		return keyFromPEMObject(readPEMObject(keyFile), password, resource);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#writeCRT(java.security.cert.X509Certificate,
	 * java.nio.file.Path)
	 */
	@Override
	public void writeCRT(X509Certificate crt, Path crtFile) throws IOException {
		writePEMObject(crtFile, crt);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#readCRT(java.nio.file.Path)
	 */
	@Override
	public X509Certificate readCRT(Path crtFile) throws IOException {
		return crtFromPEMObject(readPEMObject(crtFile));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#writeCSR(de.carne.certmgr.store.PKCS10Object,
	 * java.nio.file.Path)
	 */
	@Override
	public void writeCSR(PKCS10Object csr, Path csrFile) throws IOException {
		writePEMObject(csrFile, csr.getObject());
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#readCSR(java.nio.file.Path)
	 */
	@Override
	public PKCS10Object readCSR(Path csrFile) throws IOException {
		return csrFromPEMObject(readPEMObject(csrFile));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#writeCRL(java.security.cert.X509CRL, java.nio.file.Path)
	 */
	@Override
	public void writeCRL(X509CRL crl, Path crlFile) throws IOException {
		writePEMObject(crlFile, crl);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#readCRL(java.nio.file.Path)
	 */
	@Override
	public X509CRL readCRL(Path crlFile) throws IOException {
		return crlFromPEMObject(readPEMObject(crlFile));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#decodeExtension(java.lang.String, boolean, byte[])
	 */
	@Override
	public EncodedX509Extension decodeExtension(String oid, boolean critical, byte[] encoded) throws IOException {
		ASN1Primitive decoded = JcaX509ExtensionUtils.parseExtensionValue(encoded);

		return EncodedX509Extension.decode(oid, critical, new BouncyCastleASN1Decoder(decoded));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#tryDecodePEM(java.lang.String,
	 * de.carne.certmgr.store.PasswordCallback, java.lang.String)
	 */
	@Override
	public Collection<Object> tryDecodePEM(String pemData, PasswordCallback password, String resource)
			throws IOException {
		ArrayList<Object> decoded = null;

		try (StringReader reader = new StringReader(pemData); PEMParser pemParser = new PEMParser(reader)) {
			Object pemObject;

			try {
				pemObject = pemParser.readObject();
				decoded = new ArrayList<>();
			} catch (IOException e) {
				pemObject = null;
			}
			while (pemObject != null) {

				assert decoded != null;

				if (pemObject instanceof PEMKeyPair || pemObject instanceof PEMEncryptedKeyPair) {
					try {
						decoded.add(keyFromPEMObject(pemObject, password, resource));
					} catch (PasswordRequiredException e) {
						LOG.info(null, "Skipping key object from ''{0}'' due to missing/invalid password", resource);
					}
				} else if (pemObject instanceof X509CertificateHolder) {
					decoded.add(crtFromPEMObject(pemObject));
				} else if (pemObject instanceof PKCS10CertificationRequest) {
					decoded.add(csrFromPEMObject(pemObject));
				} else if (pemObject instanceof X509CRLHolder) {
					decoded.add(crlFromPEMObject(pemObject));
				} else {
					LOG.info(null, "Skipping unknown object type ''{1}'' from ''{0}''", resource, pemObject.getClass());
				}
				pemObject = pemParser.readObject();
			}
		}
		return decoded;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#tryDecodePKCS12(byte[],
	 * de.carne.certmgr.store.PasswordCallback, java.lang.String)
	 */
	@Override
	public Collection<Object> tryDecodePKCS12(byte[] pkcs12Data, PasswordCallback password, String resource)
			throws IOException {
		Collection<Object> decoded;

		try {
			PKCS12Decoder pkcs12Decoder = new PKCS12Decoder(new PKCS12PfxPdu(pkcs12Data), password, resource);

			decoded = pkcs12Decoder.decode();
		} catch (IOException e) {
			LOG.info(e, null, "Unable to decode PKCS#12 data from ''{0}''", resource);
			decoded = null;
		}
		return decoded;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#encodePEM(java.security.cert.X509Certificate[],
	 * java.security.KeyPair, de.carne.certmgr.store.PKCS10Object, java.security.cert.X509CRL,
	 * de.carne.certmgr.store.PasswordCallback, java.lang.String)
	 */
	@Override
	public String encodePEM(X509Certificate[] crtChain, KeyPair key, PKCS10Object csr, X509CRL crl,
			PasswordCallback password, String resource) throws IOException, PasswordRequiredException {
		String encoded;

		try (StringWriter stringWriter = new StringWriter(); JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
			if (key != null) {
				if (password != null) {
					String passwordInput = password.queryPassword(resource);

					if (passwordInput == null) {
						throw new PasswordRequiredException("Password input cancelled while writing key file");
					}

					JcePEMEncryptorBuilder encryptorBuilder = new JcePEMEncryptorBuilder(PEM_ENCRYPTOR_ALGORTIHM);

					pemWriter.writeObject(key, encryptorBuilder.build(passwordInput.toCharArray()));
				} else {
					pemWriter.writeObject(key);
				}
			}
			if (csr != null) {
				pemWriter.writeObject(csr.getObject());
			}
			if (crl != null) {
				pemWriter.writeObject(crl);
			}
			if (crtChain != null) {
				for (X509Certificate crt : crtChain) {
					pemWriter.writeObject(crt);
				}
			}
			pemWriter.flush();
			encoded = stringWriter.toString();
		}
		return encoded;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.provider.StoreProvider#encodePKCS12(java.security.cert.X509Certificate[],
	 * java.security.KeyPair, de.carne.certmgr.store.PKCS10Object, java.security.cert.X509CRL,
	 * de.carne.certmgr.store.PasswordCallback, java.lang.String)
	 */
	@Override
	public byte[] encodePKCS12(X509Certificate[] crtChain, KeyPair key, PKCS10Object csr, X509CRL crl,
			PasswordCallback password, String resource) throws IOException, PasswordRequiredException {
		String passwordInput = (password != null ? password.queryPassword(resource) : null);

		if (password != null && passwordInput == null) {
			throw new PasswordRequiredException("Password input cancelled while writing PKCS#12 file");
		}

		PKCS12SafeBagBuilder[] crtBagBuilders = new PKCS12SafeBagBuilder[crtChain != null ? crtChain.length : 0];
		DERBMPString crt0FriendlyName = null;
		SubjectKeyIdentifier subjectKeyIdentifier = null;

		if (crtChain != null) {
			int crtIndex = 0;

			for (X509Certificate crt : crtChain) {
				PKCS12SafeBagBuilder crtBagBuilder = crtBagBuilders[crtIndex] = new JcaPKCS12SafeBagBuilder(crt);
				DERBMPString crtFriendlyName = new DERBMPString(crt.getSubjectX500Principal().toString());

				crtBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, crtFriendlyName);
				if (crtIndex == 0) {
					crt0FriendlyName = crtFriendlyName;
					try {
						JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();

						subjectKeyIdentifier = extensionUtils.createSubjectKeyIdentifier(crt.getPublicKey());
					} catch (NoSuchAlgorithmException e) {
						throw new StoreProviderException(e);
					}
				}
				crtIndex++;
			}
		}

		PKCS12SafeBagBuilder keyBagBuilder = null;

		if (key != null) {
			if (passwordInput != null) {
				BcPKCS12PBEOutputEncryptorBuilder keyBagEncryptorBuilder = new BcPKCS12PBEOutputEncryptorBuilder(
						PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, new CBCBlockCipher(new DESedeEngine()));
				OutputEncryptor keyBagEncrypter = keyBagEncryptorBuilder.build(passwordInput.toCharArray());

				keyBagBuilder = new JcaPKCS12SafeBagBuilder(key.getPrivate(), keyBagEncrypter);
			} else {
				keyBagBuilder = new JcaPKCS12SafeBagBuilder(key.getPrivate());
			}
			if (crtBagBuilders.length > 0) {
				crtBagBuilders[0].addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, subjectKeyIdentifier);
				keyBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, subjectKeyIdentifier);
				keyBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, crt0FriendlyName);
			}
		}

		PKCS12SafeBag[] crtBags = new PKCS12SafeBag[crtBagBuilders.length];
		int crtBagIndex = 0;

		for (PKCS12SafeBagBuilder crtBagBuilder : crtBagBuilders) {
			crtBags[crtBagIndex] = crtBagBuilder.build();
			crtBagIndex++;
		}

		PKCS12PfxPduBuilder pkcs12Builder = new PKCS12PfxPduBuilder();

		if (passwordInput != null) {
			BcPKCS12PBEOutputEncryptorBuilder crtBagEncryptorBuilder = new BcPKCS12PBEOutputEncryptorBuilder(
					PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, new CBCBlockCipher(new RC2Engine()));
			OutputEncryptor crtBagEncryptor = crtBagEncryptorBuilder.build(passwordInput.toCharArray());

			pkcs12Builder.addEncryptedData(crtBagEncryptor, crtBags);
		} else {
			for (PKCS12SafeBag crtBag : crtBags) {
				pkcs12Builder.addData(crtBag);
			}
		}
		if (keyBagBuilder != null) {
			pkcs12Builder.addData(keyBagBuilder.build());
		}

		PKCS12PfxPdu pkcs12;

		try {
			if (passwordInput != null) {
				pkcs12 = pkcs12Builder.build(new BcPKCS12MacCalculatorBuilder(), passwordInput.toCharArray());
			} else {
				pkcs12 = pkcs12Builder.build(null, null);
			}
		} catch (PKCSException e) {
			throw new StoreProviderException(e);
		}
		return pkcs12.getEncoded();
	}

	private void addKeyIdentifierExtensions(X509v3CertificateBuilder crtBuilder, PublicKey publicKey,
			PublicKey issuerPublicKey) throws IOException, GeneralSecurityException {
		JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();

		crtBuilder.addExtension(Extension.subjectKeyIdentifier, false,
				extensionUtils.createSubjectKeyIdentifier(publicKey));
		if (!publicKey.equals(issuerPublicKey)) {
			crtBuilder.addExtension(Extension.authorityKeyIdentifier, false,
					extensionUtils.createAuthorityKeyIdentifier(issuerPublicKey));
		}
	}

	private void addCustomExtensions(X509v3CertificateBuilder crtBuilder, X509CertificateParams certificateParams)
			throws IOException {
		for (X509Extension extension : certificateParams.getExtensions()) {
			ASN1ObjectIdentifier extensionOID = new ASN1ObjectIdentifier(extension.getOID());

			crtBuilder.addExtension(extensionOID, extension.isCritical(), new BouncyCastleASN1Encoder(extension));
		}
	}

	private void addCustomExtensions(PKCS10CertificationRequestBuilder csrBuilder,
			X509CertificateParams certificateParams) throws IOException {
		ExtensionsGenerator extensionGenerator = new ExtensionsGenerator();

		for (X509Extension extension : certificateParams.getExtensions()) {
			ASN1ObjectIdentifier extensionOID = new ASN1ObjectIdentifier(extension.getOID());

			extensionGenerator.addExtension(extensionOID, extension.isCritical(),
					new BouncyCastleASN1Encoder(extension));
		}
		csrBuilder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extensionGenerator.generate());
	}

	private void writePEMObject(Path pemFile, Object object) throws IOException {
		writePEMObject(pemFile, object, null, null);
	}

	private void writePEMObject(Path pemFile, Object object, PasswordCallback password, String resource)
			throws PasswordRequiredException, IOException {
		String pemData;

		try (StringWriter stringWriter = new StringWriter(); JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
			if (password != null) {
				String passwordInput = password.queryPassword(resource);

				if (passwordInput == null) {
					throw new PasswordRequiredException("Password input cancelled while writing key file");
				}

				JcePEMEncryptorBuilder encryptorBuilder = new JcePEMEncryptorBuilder(PEM_ENCRYPTOR_ALGORTIHM);

				pemWriter.writeObject(object, encryptorBuilder.build(passwordInput.toCharArray()));
			} else {
				pemWriter.writeObject(object);
			}
			pemWriter.flush();
			pemData = stringWriter.toString();
		}
		try (Writer fileWriter = Files.newBufferedWriter(pemFile, PEM_CHARSET, StandardOpenOption.WRITE,
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			fileWriter.write(pemData);
		}
	}

	private Object readPEMObject(Path pemFile) throws IOException {
		Object object;

		try (Reader fileReader = Files.newBufferedReader(pemFile, PEM_CHARSET);
				PEMParser parser = new PEMParser(fileReader)) {
			object = parser.readObject();
		}
		return object;
	}

	private KeyPair keyFromPEMObject(Object pemObject, PasswordCallback password, String resource) throws IOException {
		PEMKeyPair keyPair = null;

		if (pemObject instanceof PEMEncryptedKeyPair) {
			PEMEncryptedKeyPair encryptedKeyPair = (PEMEncryptedKeyPair) pemObject;
			JcePEMDecryptorProviderBuilder decryptorBuilder = new JcePEMDecryptorProviderBuilder();
			String passwordInput = (password != null ? password.queryPassword(resource) : null);
			Exception invalidPasswordException = null;

			while (keyPair == null) {
				if (passwordInput == null) {
					throw new PasswordRequiredException("Password required for PEM object: '" + resource + "'",
							invalidPasswordException);
				}

				assert password != null;

				PEMDecryptorProvider decryptorProvider = decryptorBuilder.build(passwordInput.toCharArray());

				try {
					keyPair = encryptedKeyPair.decryptKeyPair(decryptorProvider);
				} catch (EncryptionException e) {
					invalidPasswordException = e;
					passwordInput = password.requeryPassword(resource, e);
				}
			}
		} else {
			keyPair = (PEMKeyPair) pemObject;
		}

		JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter();

		return keyConverter.getKeyPair(keyPair);
	}

	private X509Certificate crtFromPEMObject(Object pemObject) throws IOException {
		X509Certificate crt;

		try {
			X509CertificateHolder crtHolder = (X509CertificateHolder) pemObject;
			JcaX509CertificateConverter converter = new JcaX509CertificateConverter();

			crt = converter.getCertificate(crtHolder);
		} catch (Exception e) {
			throw new IOException(e.getLocalizedMessage(), e);
		}
		return crt;
	}

	private BouncyCastlePKCS10Object csrFromPEMObject(Object pemObject) throws IOException {
		JcaPKCS10CertificationRequest pkcs10Object;

		if (pemObject instanceof JcaPKCS10CertificationRequest) {
			pkcs10Object = (JcaPKCS10CertificationRequest) pemObject;
		} else {
			pkcs10Object = new JcaPKCS10CertificationRequest((PKCS10CertificationRequest) pemObject);
		}

		BouncyCastlePKCS10Object csr;

		try {
			csr = new BouncyCastlePKCS10Object(pkcs10Object);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e.getLocalizedMessage(), e);
		}
		return csr;
	}

	private X509CRL crlFromPEMObject(Object pemObject) throws IOException {
		X509CRL crl;

		try {
			X509CRLHolder crlHolder = (X509CRLHolder) pemObject;
			JcaX509CRLConverter converter = new JcaX509CRLConverter();

			crl = converter.getCRL(crlHolder);
		} catch (Exception e) {
			throw new IOException(e.getLocalizedMessage(), e);
		}
		return crl;
	}

}
