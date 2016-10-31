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
package de.carne.certmgr.certs.x509;

/**
 * X.509 Key Usage flags.
 */
public enum KeyUsage {

	/**
	 * ENCIPHER_ONLY
	 */
	ENCIPHER_ONLY(1 << 0),

	/**
	 * CRL_SIGN
	 */
	CRL_SIGN(1 << 1),

	/**
	 * KEY_CERT_SIGN
	 */
	KEY_CERT_SIGN(1 << 2),

	/**
	 * KEY_AGREEMENT
	 */
	KEY_AGREEMENT(1 << 3),

	/**
	 * DATA_ENCIPHERMENT
	 */
	DATA_ENCIPHERMENT(1 << 4),

	/**
	 * KEY_ENCIPHERMENT
	 */
	KEY_ENCIPHERMENT(1 << 5),

	/**
	 * NON_REPUDIATION
	 */
	NON_REPUDIATION(1 << 6),

	/**
	 * DIGITAL_SIGNATURE
	 */
	DIGITAL_SIGNATURE(1 << 7),

	/**
	 * DECIPHER_ONLY
	 */
	DECIPHER_ONLY(1 << 15),

	/**
	 * ANY
	 */
	ANY(-1 >>> 16);

	private final int value;

	private KeyUsage(int value) {
		this.value = value;
	}

	public final int value() {
		return this.value;
	}

}
