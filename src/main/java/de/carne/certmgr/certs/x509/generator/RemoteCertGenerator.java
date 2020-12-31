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
import java.security.KeyPair;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.certmgr.certs.CertObjectStore;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.certs.x509.GenerateCertRequest;
import de.carne.certmgr.certs.x509.KeyHelper;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;
import de.carne.jfx.util.DefaultSet;

/**
 * Generator service for CSR generation (for remote signing).
 */
public class RemoteCertGenerator extends AbstractCertGenerator {

	/**
	 * Provider name.
	 */
	public static final String PROVIDER_NAME = "REMOTE";

	/**
	 * Construct {@code RemoteCertGenerator}.
	 */
	public RemoteCertGenerator() {
		super(Feature.CUSTOM_SIGNATURE_ALGORITHM, Feature.CUSTOM_EXTENSIONS);
	}

	@Override
	public String providerName() {
		return PROVIDER_NAME;
	}

	@Override
	public String getDescription() {
		return CertGeneratorI18N.strRemoteDescription();
	}

	@Override
	public DefaultSet<Issuer> getIssuers(UserCertStore store, @Nullable UserCertStoreEntry defaultHint) {
		return new DefaultSet<>();
	}

	@Override
	public DefaultSet<SignatureAlgorithm> getSignatureAlgorithms(@Nullable Issuer issuer,
			@Nullable KeyPairAlgorithm keyPairAlgorithm, @Nullable String defaultHint, boolean expertMode) {
		DefaultSet<SignatureAlgorithm> signatureAlgorithms = new DefaultSet<>();

		if (keyPairAlgorithm != null) {
			signatureAlgorithms = SignatureAlgorithm.getDefaultSet(keyPairAlgorithm.algorithm(), defaultHint,
					expertMode);
		}
		return signatureAlgorithms;
	}

	@Override
	public CertObjectStore generateCert(GenerateCertRequest request, PasswordCallback password) throws IOException {
		KeyPair key = KeyHelper.generateKey(request.keyPairAlgorithm(), request.keySize());
		SignatureAlgorithm signatureAlgorithm = requiredParameter(request.getSignatureAlgorithm(),
				"SignatureAlgorithm");
		PKCS10CertificateRequest csr = PKCS10CertificateRequest.generateCSR(request.dn(), key, request.getExtensions(),
				signatureAlgorithm);
		CertObjectStore certObjects = new CertObjectStore();

		certObjects.addKey(key);
		certObjects.addCSR(csr);
		return certObjects;
	}

}
