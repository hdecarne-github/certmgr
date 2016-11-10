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
package de.carne.certmgr.certs.signer;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.security.DefaultSet;
import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.certs.spi.CertSigner;

/**
 * Default (BouncyCastle based) service provider for certificate signing.
 */
public class DefaultCertSigner implements CertSigner {

	/**
	 * Provider name.
	 */
	public static final String PROVIDER_NAME = "BouncyCastle";

	private final Issuer selfSignedIssuer = new DefaultIssuer(CertSignerI18N.formatSTR_SELFSIGNED_NAME());

	@Override
	public String providerName() {
		return PROVIDER_NAME;
	}

	@Override
	public String getDescription() {
		return CertSignerI18N.formatSTR_DEFAULT_DESCRIPTION();
	}

	@Override
	public DefaultSet<Issuer> getIssuers(UserCertStore store) {
		DefaultSet<Issuer> issuers = new DefaultSet<>();

		issuers.addDefault(this.selfSignedIssuer);
		if (store != null) {
			for (UserCertStoreEntry storeEntry : store.getEntries()) {
				if (storeEntry.canIssue()) {
					issuers.add(new DefaultIssuer(storeEntry));
				}
			}
		}
		return issuers;
	}

	@Override
	public DefaultSet<SignatureAlgorithm> getSignatureAlgorithms(KeyPairAlgorithm keyPairAlgorithm, Issuer issuer,
			boolean expertMode) {
		DefaultSet<SignatureAlgorithm> signatureAlgorithms = new DefaultSet<>();

		if (keyPairAlgorithm != null && issuer != null) {
			String keyPairAlgorithmName;

			if (this.selfSignedIssuer.equals(issuer)) {
				keyPairAlgorithmName = keyPairAlgorithm.algorithm();
			} else {
				keyPairAlgorithmName = issuer.storeEntry().getKeyAlgorithm();
			}
			if (keyPairAlgorithmName != null) {
				signatureAlgorithms = SignatureAlgorithm.getAll(keyPairAlgorithmName, expertMode);
			}
		}
		return signatureAlgorithms;
	}

	@Override
	public String toString() {
		return getDescription();
	}

	private class DefaultIssuer extends Issuer {

		DefaultIssuer(UserCertStoreEntry storeEntry) {
			super(storeEntry);
		}

		DefaultIssuer(String name) {
			super(name);
		}

		@Override
		public CertSigner signer() {
			return DefaultCertSigner.this;
		}

	}

}
