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
import java.security.Security;
import java.util.HashSet;
import java.util.Set;

import de.carne.util.Exceptions;

/**
 * Key pair algorithm class.
 */
public abstract class KeyPairAlgorithm {

	private static final String SERVICE_TYPE_KEY_PAIR_GENERATOR = "KeyPairGenerator";

	private final Service service;

	KeyPairAlgorithm(Service service) {
		this.service = service;
	}

	/**
	 * Get the available key pair algorithms.
	 *
	 * @param expertMode Whether only standard algorithms are considered
	 *        ({@code false}) or all algorithms available on the current
	 *        platform ({@code true}).
	 * @return The available key pair algorithms
	 */
	public static Set<KeyPairAlgorithm> getAll(boolean expertMode) {
		Set<KeyPairAlgorithm> keyPairAlgorithms = new HashSet<>();
		Set<String> standardAlgorithms = SecurityDefaults.getKeyAlgorithmNames();

		for (Provider provider : Security.getProviders()) {
			for (Provider.Service service : provider.getServices()) {
				if (!SERVICE_TYPE_KEY_PAIR_GENERATOR.equals(service.getType())) {
					continue;
				}
				if (!expertMode && !standardAlgorithms.contains(service.getAlgorithm())) {
					continue;
				}
				if (expertMode) {
					keyPairAlgorithms.add(new ExpertKeyPairAlgorithm(service));
				} else {
					keyPairAlgorithms.add(new StandardKeyPairAlgorithm(service));
				}
			}
		}
		return keyPairAlgorithms;
	}

	/**
	 * Get the default key pair algorithm.
	 *
	 * @param expertMode Whether only standard algorithms are considered
	 *        ({@code false}) or all algorithms available on the current
	 *        platform ({@code true}).
	 * @return The default key pair algorithm.
	 */
	public static KeyPairAlgorithm getDefault(boolean expertMode) {
		String defaultName = SecurityDefaults.getDefaultKeyAlgorithmName();
		Service service;

		try {
			service = KeyPairGenerator.getInstance(defaultName).getProvider()
					.getService(SERVICE_TYPE_KEY_PAIR_GENERATOR, defaultName);
		} catch (GeneralSecurityException e) {
			throw Exceptions.toRuntime(e);
		}
		return (expertMode ? new ExpertKeyPairAlgorithm(service) : new StandardKeyPairAlgorithm(service));
	}

	/**
	 * Get this algorithm's {@link Service} object.
	 *
	 * @return This algorithm's {@link Service} object.
	 */
	protected Service service() {
		return this.service;
	}

	/**
	 * Get this algorithm's name.
	 *
	 * @return This algorithm's name.
	 */
	public String algorithm() {
		return this.service.getAlgorithm();
	}

	/**
	 * Get a {@link KeyPairGenerator} instance for this algorithm.
	 *
	 * @return A {@link KeyPairGenerator} instance for this algorithm.
	 * @throws GeneralSecurityException if no instance is available.
	 */
	public KeyPairGenerator getInstance() throws GeneralSecurityException {
		return KeyPairGenerator.getInstance(this.service.getAlgorithm(), this.service.getProvider());
	}

	/**
	 * Get this algorithm's standard key sizes.
	 *
	 * @return This algorithm's standard key sizes.
	 */
	public Set<Integer> getStandardKeySizes() {
		return SecurityDefaults.getKeySizes(algorithm());
	}

	/**
	 * Get this algorithm's default key size.
	 *
	 * @return This algorithm's default key size.
	 */
	public Integer getDefaultKeySize() {
		return SecurityDefaults.getDefaultKeySize(algorithm());
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

		@Override
		public String toString() {
			Service service = service();

			return service.getAlgorithm() + "/" + service.getProvider().getName();
		}

	}

}
