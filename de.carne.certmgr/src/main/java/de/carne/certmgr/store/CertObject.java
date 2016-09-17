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
package de.carne.certmgr.store;

/**
 * Certificate object.
 */
public interface CertObject {

	/**
	 * Get the unique id of the certificate object.
	 *
	 * @return The unique id of the certificate object.
	 */
	String id();

	/**
	 * Get the common name (CN) of the certificate object.
	 * 
	 * @return The common name (CN) of the certificate object.
	 */
	String cn();

	/**
	 * Check whether the certificate object has a Key.
	 *
	 * @return {@code true} if the certificate object has a Key.
	 */
	boolean hasKey();

	/**
	 * Check whether the certificate object has a CRT (Certificate).
	 *
	 * @return {@code true} if the certificate object has a CRT (Certificate).
	 */
	boolean hasCRT();

	/**
	 * Check whether the certificate object has a CSR (Certificate Signing
	 * Request).
	 *
	 * @return {@code true} if the certificate object has a CSR (Certificate
	 *         Signing Request).
	 */
	boolean hasCSR();

	/**
	 * Check whether the certificate object has a CRL (Certificate Revocation
	 * List).
	 *
	 * @return {@code true} if the certificate object has a CRL (Certificate
	 *         Revocation List).
	 */
	boolean hasCRL();

}
