/*
 * Copyright (c) 2015-2020 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.certs.security;

import java.security.Provider;
import java.security.Provider.Service;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.jfx.util.DefaultSet;

/**
 * Signature algorithm provisioning.
 */
public abstract class SignatureAlgorithm extends AbstractAlgorithm {

	private static final String SERVICE_TYPE_SIGNATURE = "Signature";

	SignatureAlgorithm(Service service) {
		super(service);
	}

	/**
	 * Get the available signature algorithms.
	 *
	 * @param keyPairAlgorithm The key pair algorithm to get the signature algorithms for.
	 * @param defaultHint The default to return (may be {@code null}). If this algorithm is contained in the default
	 * set, it is also set as the default.
	 * @param expertMode Whether only standard algorithms are considered ({@code false}) or all algorithms available on
	 * the current platform ({@code true}).
	 * @return The available signature algorithms
	 */
	public static DefaultSet<SignatureAlgorithm> getDefaultSet(String keyPairAlgorithm, @Nullable String defaultHint,
			boolean expertMode) {
		DefaultSet<SignatureAlgorithm> signatureAlgorithms = new DefaultSet<>();
		DefaultSet<String> defaultNames = SecurityDefaults.getSignatureAlgorithmNames(keyPairAlgorithm);
		@Nullable
		String defaultName = (defaultHint != null && defaultNames.contains(defaultHint) ? defaultHint
				: defaultNames.getDefault());

		if (defaultName != null) {
			defaultName = defaultName.toUpperCase();
		}
		for (Provider provider : SecurityDefaults.getProviders(expertMode)) {
			for (Provider.Service service : provider.getServices()) {
				if (!SERVICE_TYPE_SIGNATURE.equals(service.getType())) {
					continue;
				}

				String upperCaseAlgorithm = service.getAlgorithm().toUpperCase();

				if (!expertMode && !defaultNames.contains(upperCaseAlgorithm)) {
					continue;
				}

				SignatureAlgorithm signatureAlgorithm = (expertMode ? new ExpertKeyPairAlgorithm(service)
						: new StandardKeyPairAlgorithm(service));

				if (upperCaseAlgorithm.equals(defaultName)) {
					signatureAlgorithms.addDefault(signatureAlgorithm);
				} else {
					signatureAlgorithms.add(signatureAlgorithm);
				}
			}
		}
		return signatureAlgorithms;
	}

	private static class StandardKeyPairAlgorithm extends SignatureAlgorithm {

		public StandardKeyPairAlgorithm(Service service) {
			super(service);
		}

		@Override
		public int hashCode() {
			return algorithm().hashCode();
		}

		@Override
		public boolean equals(@Nullable Object obj) {
			return this == obj || (obj instanceof StandardKeyPairAlgorithm
					&& algorithm().equals(((StandardKeyPairAlgorithm) obj).algorithm()));
		}

		@Override
		public String toString() {
			return algorithm();
		}

	}

	private static class ExpertKeyPairAlgorithm extends SignatureAlgorithm {

		public ExpertKeyPairAlgorithm(Service service) {
			super(service);
		}

		@Override
		public int hashCode() {
			return service().hashCode();
		}

		@Override
		public boolean equals(@Nullable Object obj) {
			return this == obj || (obj instanceof ExpertKeyPairAlgorithm
					&& service().equals(((ExpertKeyPairAlgorithm) obj).service()));
		}

		@Override
		public String toString() {
			Service service = service();

			return service.getAlgorithm() + "/" + service.getProvider().getName();
		}

	}

}
