/*
 * Copyright (c) 2015-2018 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.certs.x509;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.boot.check.Nullable;

/**
 * Parameter container for CRL updating.
 */
public class UpdateCRLRequest {

	private final Map<BigInteger, ReasonFlag> revokeMap = new HashMap<>();
	private final Date lastUpdate;
	@Nullable
	private final Date nextUpdate;
	private final SignatureAlgorithm signatureAlgorithm;

	/**
	 * Construct {@code UpdateCRLRequest}.
	 *
	 * @param lastUpdate The CRL's last update date.
	 * @param nextUpdate The CRL's next update date (may be {@code null}).
	 * @param signatureAlgorithm The signature algorithm to use for CRL signing.
	 */
	public UpdateCRLRequest(Date lastUpdate, @Nullable Date nextUpdate, SignatureAlgorithm signatureAlgorithm) {
		this.lastUpdate = lastUpdate;
		this.nextUpdate = nextUpdate;
		this.signatureAlgorithm = signatureAlgorithm;
	}

	/**
	 * Add a CRL entry.
	 *
	 * @param serial The serial number of the revoked CRT.
	 * @param reason The revoke reason.
	 */
	public void addRevokeEntry(BigInteger serial, ReasonFlag reason) {
		this.revokeMap.put(serial, reason);
	}

	/**
	 * Get the CRL entries.
	 *
	 * @return The CRL entries.
	 */
	public Map<BigInteger, ReasonFlag> getRevokeEntries() {
		return Collections.unmodifiableMap(this.revokeMap);
	}

	/**
	 * Get the CRL's last update date.
	 *
	 * @return The CRL's last update date.
	 */
	public Date lastUpdate() {
		return this.lastUpdate;
	}

	/**
	 * Get the CRL's next update date.
	 *
	 * @return The CRL's next update date or {@code null} if undefined.
	 */
	@Nullable
	public Date nextUpdate() {
		return this.nextUpdate;
	}

	/**
	 * Get the signature algorithm to use for CRL signing.
	 *
	 * @return The signature algorithm to use for CRL signing.
	 */
	public SignatureAlgorithm signatureAlgorithm() {
		return this.signatureAlgorithm;
	}

}
