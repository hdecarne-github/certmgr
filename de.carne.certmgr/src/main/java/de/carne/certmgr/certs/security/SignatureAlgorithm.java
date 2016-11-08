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

import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.util.HashSet;
import java.util.Set;

/**
 * Signature algorithm provisioning.
 */
public abstract class SignatureAlgorithm {

	private static final String SERVICE_TYPE_SIGNATURE = "Signature";

	private final Service service;

	SignatureAlgorithm(Service service) {
		this.service = service;
	}

	/**
	 * Get the available signature algorithms.
	 * 
	 * @param keyPairAlgorithm The key pair algorithm to get the signature
	 *        algorithms for.
	 * @param expertMode Whether only standard algorithms are considered
	 *        ({@code false}) or all algorithms available on the current
	 *        platform ({@code true}).
	 * @return The available signature algorithms
	 */
	public static Set<SignatureAlgorithm> getAll(String keyPairAlgorithm, boolean expertMode) {
		Set<SignatureAlgorithm> signatureAlgorithms = new HashSet<>();
		Set<String> standardAlgorithms = SecurityDefaults.getSignatureAlgorithmNames(keyPairAlgorithm);

		for (Provider provider : Security.getProviders()) {
			for (Provider.Service service : provider.getServices()) {
				if (!SERVICE_TYPE_SIGNATURE.equals(service.getType())) {
					continue;
				}

				String upperCaseAlgorithm = service.getAlgorithm().toUpperCase();

				if (!expertMode && !standardAlgorithms.contains(upperCaseAlgorithm)) {
					continue;
				}
				if (expertMode) {
					signatureAlgorithms.add(new ExpertKeyPairAlgorithm(service));
				} else {
					signatureAlgorithms.add(new StandardKeyPairAlgorithm(service));
				}
			}
		}
		return signatureAlgorithms;
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

	private static class StandardKeyPairAlgorithm extends SignatureAlgorithm {

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

	private static class ExpertKeyPairAlgorithm extends SignatureAlgorithm {

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
