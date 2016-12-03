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
package de.carne.certmgr.certs.security;

import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.Provider.Service;

import de.carne.util.DefaultSet;

import java.security.Security;

/**
 * Key pair algorithm provisioning.
 */
public abstract class KeyPairAlgorithm extends AbstractAlgorithm {

	private static final String SERVICE_TYPE_KEY_PAIR_GENERATOR = "KeyPairGenerator";

	KeyPairAlgorithm(Service service) {
		super(service);
	}

	/**
	 * Get the available key pair algorithms.
	 *
	 * @param defaultHint The default to return (may be {@code null}). If this
	 *        algorithm is contained in the default set, it is also set as the
	 *        default.
	 * @param expertMode Whether only standard algorithms are considered
	 *        ({@code false}) or all algorithms available on the current
	 *        platform ({@code true}).
	 * @return The available key pair algorithms
	 */
	public static DefaultSet<KeyPairAlgorithm> getDefaultSet(String defaultHint, boolean expertMode) {
		DefaultSet<KeyPairAlgorithm> keyPairAlgorithms = new DefaultSet<>();
		DefaultSet<String> defaultNames = SecurityDefaults.getKeyAlgorithmNames();
		String defaultName = (defaultHint != null ? defaultHint : defaultNames.getDefault());

		for (Provider provider : Security.getProviders()) {
			for (Provider.Service service : provider.getServices()) {
				if (!SERVICE_TYPE_KEY_PAIR_GENERATOR.equals(service.getType())) {
					continue;
				}

				String algorithm = service.getAlgorithm();

				if (!expertMode && !defaultName.equals(algorithm) && !defaultNames.contains(algorithm)) {
					continue;
				}

				KeyPairAlgorithm keyPairAlgorithm = (expertMode ? new ExpertKeyPairAlgorithm(service)
						: new StandardKeyPairAlgorithm(service));

				if (algorithm.equals(defaultName)) {
					keyPairAlgorithms.addDefault(keyPairAlgorithm);
				} else {
					keyPairAlgorithms.add(keyPairAlgorithm);
				}
			}
		}
		return keyPairAlgorithms;
	}

	/**
	 * Get a {@link KeyPairGenerator} instance for this algorithm.
	 *
	 * @return A {@link KeyPairGenerator} instance for this algorithm.
	 * @throws GeneralSecurityException if no instance is available.
	 */
	public KeyPairGenerator getInstance() throws GeneralSecurityException {
		Service service = service();

		return KeyPairGenerator.getInstance(service.getAlgorithm(), service.getProvider());
	}

	/**
	 * Get this algorithm's standard key sizes.
	 *
	 * @param defaultHint The default to return (may be {@code null}). If not
	 *        yet part of the default set, this size is added to the set.
	 * @return This algorithm's standard key sizes.
	 */
	public DefaultSet<Integer> getStandardKeySizes(Integer defaultHint) {
		DefaultSet<Integer> standardKeySizes = SecurityDefaults.getKeySizes(algorithm());

		if (defaultHint != null) {
			standardKeySizes.addDefault(defaultHint);
		}
		return standardKeySizes;
	}

	private static class StandardKeyPairAlgorithm extends KeyPairAlgorithm {

		public StandardKeyPairAlgorithm(Service service) {
			super(service);
		}

		@Override
		public int hashCode() {
			return algorithm().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj || (obj instanceof StandardKeyPairAlgorithm
					&& algorithm().equals(((StandardKeyPairAlgorithm) obj).algorithm()));
		}

		@Override
		public String toString() {
			return algorithm();
		}

	}

	private static class ExpertKeyPairAlgorithm extends KeyPairAlgorithm {

		public ExpertKeyPairAlgorithm(Service service) {
			super(service);
		}

		@Override
		public int hashCode() {
			return service().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj || (obj instanceof ExpertKeyPairAlgorithm
					&& service().equals(((ExpertKeyPairAlgorithm) obj).service()));
		}

	}

}
