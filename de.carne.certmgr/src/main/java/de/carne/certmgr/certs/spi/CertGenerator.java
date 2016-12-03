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
package de.carne.certmgr.certs.spi;

import java.io.IOException;
import java.util.List;

import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.certs.x509.GenerateCertRequest;
import de.carne.certmgr.certs.x509.generator.Issuer;
import de.carne.util.DefaultSet;

/**
 * Service provider interface for certificate signing.
 */
public interface CertGenerator extends NamedProvider {

	/**
	 * Service features.
	 */
	public enum Feature {

		/**
		 * Service supports custom issuers.
		 */
		CUSTOM_ISSUER,

		/**
		 * Service supports custom signature algorithms.
		 */
		CUSTOM_SIGNATURE_ALGORITHM,

		/**
		 * Service supports custom validity.
		 */
		CUSTOM_VALIDITY,

		/**
		 * Service supports custom extensions.
		 */
		CUSTOM_EXTENSIONS

	}

	/**
	 * Get this provider's description.
	 *
	 * @return This provider's description.
	 */
	String getDescription();

	/**
	 * Check whether this service supports a specific feature.
	 *
	 * @param feature The feature to check.
	 * @return {@code true} if the submitted feature is supported.
	 */
	boolean hasFeature(Feature feature);

	/**
	 * Get the available issuers.
	 *
	 * @param store The store to use for issuer provisioning.
	 * @param defaultHint The default to return (may be {@code null}). If the
	 *        issuer representing this store entry is part of the default set,
	 *        it is also set as the default.
	 * @return The available issuers (may be an empty set).
	 */
	DefaultSet<Issuer> getIssuers(UserCertStore store, UserCertStoreEntry defaultHint);

	/**
	 * Get the available signature algorithms.
	 *
	 * @param issuer The selected issuer.
	 * @param keyPairAlgorithm The selected key pair algorithm.
	 * @param defaultHint The default to return (may be {@code null}). If this
	 *        algorithm is contained in the default set, it is also set as the
	 *        default.
	 * @param expertMode Whether only standard algorithms are considered
	 *        ({@code false}) or all algorithms available on the current
	 *        platform ({@code true}).
	 * @return The available signature algorithms
	 */
	DefaultSet<SignatureAlgorithm> getSignatureAlgorithms(Issuer issuer, KeyPairAlgorithm keyPairAlgorithm,
			String defaultHint, boolean expertMode);

	/**
	 * Generate certificate objects.
	 *
	 * @param request The parameters to use for generation.
	 * @param password The password callback to use for password querying.
	 * @return The generated certificate objects.
	 * @throws IOException if an error occurs during generation.
	 */
	List<Object> generateCert(GenerateCertRequest request, PasswordCallback password) throws IOException;

}
