/*
 * Copyright (c) 2015-2017 Holger de Carne and contributors, All Rights Reserved.
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.spi.CertGenerator;

/**
 * Abstract base class for {@link CertGenerator} implementations which provides the commonly used generation functions.
 */
abstract class AbstractCertGenerator implements CertGenerator {

	private final Set<Feature> features;

	/**
	 * @param features
	 */
	protected AbstractCertGenerator(Feature... features) {
		this.features = new HashSet<>(Arrays.asList(features));
	}

	@Override
	public boolean hasFeature(Feature feature) {
		return (this.features.isEmpty() || this.features.contains(feature));
	}

	@Override
	public String toString() {
		return getDescription();
	}

	protected BigInteger getNextSerial(UserCertStoreEntry issuer) throws IOException {
		UserCertStoreEntry rootIssuer = issuer;

		while (!rootIssuer.isSelfSigned()) {
			rootIssuer = rootIssuer.issuer();
		}
		return getNextSerialHelper(rootIssuer, BigInteger.ONE).add(BigInteger.ONE);
	}

	private BigInteger getNextSerialHelper(UserCertStoreEntry issuer, BigInteger serial) throws IOException {
		BigInteger maxSerial = serial;

		if (!issuer.hasCRT()) {
			throw new IOException("Incomplete CA; unable to determine next serial");
		}
		maxSerial = maxSerial.max(issuer.getCRT().getSerialNumber());
		for (UserCertStoreEntry issuedEntry : issuer.issuedEntries()) {
			maxSerial = getNextSerialHelper(issuedEntry, maxSerial);
		}
		return maxSerial;
	}

	protected static <T> T requiredParameter(T parameter, String name) throws IllegalArgumentException {
		if (parameter == null) {
			throw new IllegalArgumentException("Required parameter " + name + " is not set");
		}
		return parameter;
	}

}
