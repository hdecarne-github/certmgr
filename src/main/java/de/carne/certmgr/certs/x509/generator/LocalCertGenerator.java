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
package de.carne.certmgr.certs.x509.generator;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Objects;

import javax.security.auth.x500.X500Principal;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.Exceptions;
import de.carne.certmgr.certs.CertObjectStore;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.certs.spi.CertGenerator;
import de.carne.certmgr.certs.x509.GenerateCertRequest;
import de.carne.certmgr.certs.x509.KeyHelper;
import de.carne.certmgr.certs.x509.X509CertificateHelper;
import de.carne.jfx.util.DefaultSet;

/**
 * Generator service for self-sustaining CA management.
 */
public class LocalCertGenerator extends AbstractCertGenerator {

	/**
	 * Provider name.
	 */
	public static final String PROVIDER_NAME = "LOCAL";

	private final Issuer selfSignedIssuer = new LocalIssuer(CertGeneratorI18N.strSelfsignedName());

	/**
	 * Construct {@code LocalCertGenerator}.
	 */
	public LocalCertGenerator() {
		super();
	}

	@Override
	public String providerName() {
		return PROVIDER_NAME;
	}

	@Override
	public String getDescription() {
		return CertGeneratorI18N.strLocalDescription();
	}

	@Override
	public DefaultSet<Issuer> getIssuers(UserCertStore store, @Nullable UserCertStoreEntry defaultHint) {
		DefaultSet<Issuer> issuers = new DefaultSet<>();

		issuers.addDefault(this.selfSignedIssuer);
		if (store != null) {
			for (UserCertStoreEntry storeEntry : store.getEntries()) {
				if (storeEntry.canIssue()) {
					if (storeEntry.equals(defaultHint)) {
						issuers.addDefault(new LocalIssuer(storeEntry));
					} else {
						issuers.add(new LocalIssuer(storeEntry));
					}
				}
			}
		}
		return issuers;
	}

	@Override
	public DefaultSet<SignatureAlgorithm> getSignatureAlgorithms(@Nullable Issuer issuer,
			@Nullable KeyPairAlgorithm keyPairAlgorithm, @Nullable String defaultHint, boolean expertMode) {
		DefaultSet<SignatureAlgorithm> signatureAlgorithms = new DefaultSet<>();

		if (issuer != null && keyPairAlgorithm != null) {
			String keyPairAlgorithmName = null;

			if (this.selfSignedIssuer.equals(issuer)) {
				keyPairAlgorithmName = keyPairAlgorithm.algorithm();
			} else {
				try {
					keyPairAlgorithmName = Objects.requireNonNull(issuer.storeEntry()).getPublicKey().getAlgorithm();
				} catch (IOException e) {
					Exceptions.warn(e);
				}
			}
			if (keyPairAlgorithmName != null) {
				signatureAlgorithms = SignatureAlgorithm.getDefaultSet(keyPairAlgorithmName, defaultHint, expertMode);
			}
		}
		return signatureAlgorithms;
	}

	@Override
	public CertObjectStore generateCert(GenerateCertRequest request, PasswordCallback password) throws IOException {
		KeyPair key = KeyHelper.generateKey(request.keyPairAlgorithm(), request.keySize());
		Issuer issuer = requiredParameter(request.getIssuer(), "Issuer");
		BigInteger serial = BigInteger.ONE;
		X500Principal issuerDN = null;
		KeyPair issuerKey = null;
		X500Principal dn = request.dn();

		if (!this.selfSignedIssuer.equals(issuer)) {
			UserCertStoreEntry issuerEntry = Objects.requireNonNull(issuer.storeEntry());

			serial = getNextSerial(issuerEntry);
			issuerDN = issuerEntry.dn();
			issuerKey = issuerEntry.getKey(password);
		} else {
			issuerKey = key;
			issuerDN = dn;
		}

		Date notBefore = requiredParameter(request.getNotBefore(), "NotBefore");
		Date notAfter = requiredParameter(request.getNotAfter(), "NotAfter");
		SignatureAlgorithm signatureAlgorithm = requiredParameter(request.getSignatureAlgorithm(),
				"SignatureAlgorithm");
		X509Certificate crt = X509CertificateHelper.generateCRT(dn, key, serial, notBefore, notAfter,
				request.getExtensions(), issuerDN, issuerKey, signatureAlgorithm);
		CertObjectStore certObjects = new CertObjectStore();

		certObjects.addKey(key);
		certObjects.addCRT(crt);
		return certObjects;
	}

	private class LocalIssuer extends Issuer {

		LocalIssuer(UserCertStoreEntry storeEntry) {
			super(storeEntry);
		}

		LocalIssuer(String name) {
			super(name);
		}

		@Override
		public CertGenerator generator() {
			return LocalCertGenerator.this;
		}

	}

}
