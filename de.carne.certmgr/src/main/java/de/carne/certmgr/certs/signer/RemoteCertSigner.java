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
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.certs.spi.CertSigner;
import de.carne.certmgr.util.DefaultSet;

/**
 * Signing service for CSR generation.
 */
public class RemoteCertSigner implements CertSigner {

	/**
	 * Provider name.
	 */
	public static final String PROVIDER_NAME = "REMOTE";

	@Override
	public String providerName() {
		return PROVIDER_NAME;
	}

	@Override
	public String getDescription() {
		return CertSignerI18N.formatSTR_REMOTE_DESCRIPTION();
	}

	@Override
	public boolean hasFeature(Feature feature) {
		return feature == Feature.CUSTOM_SIGNATURE_ALGORITHM || feature == Feature.CUSTOM_EXTENSIONS;
	}

	@Override
	public DefaultSet<Issuer> getIssuers(UserCertStore store, UserCertStoreEntry defaultHint) {
		return new DefaultSet<>();
	}

	@Override
	public DefaultSet<SignatureAlgorithm> getSignatureAlgorithms(Issuer issuer, String keyPairAlgorithm,
			String defaultHint, boolean expertMode) {
		DefaultSet<SignatureAlgorithm> signatureAlgorithms = new DefaultSet<>();

		if (keyPairAlgorithm != null) {
			signatureAlgorithms = SignatureAlgorithm.getDefaultSet(keyPairAlgorithm, defaultHint, expertMode);
		}
		return signatureAlgorithms;
	}

	@Override
	public String toString() {
		return getDescription();
	}

}
