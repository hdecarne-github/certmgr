/*
 * Copyright (c) 2014-2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.store.x509;

import java.time.LocalDate;

/**
 * Parameter class containing the necessary parameters for CRL generation.
 */
public class X509CRLParams {

	private final String sigAlg;
	private final LocalDate lastUpdate;
	private final LocalDate nextUpdate;

	/**
	 * Construct X509CRLParams.
	 *
	 * @param sigAlg The signature algorithm for CRL signing.
	 * @param lastUpdate The CRL last update date to set.
	 * @param nextUpdate The CRL next update date to set.
	 */
	public X509CRLParams(String sigAlg, LocalDate lastUpdate, LocalDate nextUpdate) {
		assert sigAlg != null;
		assert lastUpdate != null;
		assert nextUpdate != null;

		this.sigAlg = sigAlg;
		this.lastUpdate = lastUpdate;
		this.nextUpdate = nextUpdate;
	}

	/**
	 * @return the sigAlg
	 */
	public String getSigAlg() {
		return this.sigAlg;
	}

	/**
	 * @return the lastUpdate
	 */
	public LocalDate getLastUpdate() {
		return this.lastUpdate;
	}

	/**
	 * @return the nextUpdate
	 */
	public LocalDate getNextUpdate() {
		return this.nextUpdate;
	}

}
