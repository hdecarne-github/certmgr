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

import java.security.SecureRandom;

/**
 * Parameter class for key generation.
 */
public class KeyParams {

	private String keyAlg;
	private int keySize;
	private String provider;
	private SecureRandom random;

	/**
	 * Construct KeyParams.
	 *
	 * @param keyAlg The key algorithm to use.
	 * @param keySize The key size to generate.
	 */
	public KeyParams(String keyAlg, int keySize) {
		this.keyAlg = keyAlg;
		this.keySize = keySize;
		this.provider = null;
		this.random = null;
	}

	/**
	 * Get the key algorithm to use.
	 *
	 * @return The key algorithm to use.
	 */
	public String getKeyAlg() {
		return this.keyAlg;
	}

	/**
	 * Get the key size to generate.
	 *
	 * @return The key size to generate.
	 */
	public int getKeySize() {
		return this.keySize;
	}

	/**
	 * Set the security provider to use for key generation.
	 *
	 * @param provider The security provider to set.
	 */
	public void setProvider(String provider) {
		this.provider = provider;
	}

	/**
	 * Get the security provider to use for key generation.
	 *
	 * @return The security provider to use for key generation (may be null)
	 */
	public String getProvider() {
		return this.provider;
	}

	/**
	 * Set the random source to use for key generation.
	 *
	 * @param random The random source to set.
	 */
	public void setRandom(SecureRandom random) {
		this.random = random;
	}

	/**
	 * Get the random source to use for key generation.
	 *
	 * @return The random source to use for key generation (may be null)
	 */
	public SecureRandom getRandom() {
		return this.random;
	}

}
