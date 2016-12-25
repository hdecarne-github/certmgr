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
package de.carne.certmgr.certs;

import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;

/**
 * This class represents a single certificate object and makes it accessible in
 * a common way independent from the actual object type.
 */
public final class CertObject {

	private final String alias;

	private final Object object;

	private CertObject(String alias, Object object) {
		this.alias = alias;
		this.object = object;
	}

	/**
	 * Wrap a raw certificate object.
	 *
	 * @param alias The alias name to use for the certificate object.
	 * @param object The certificate object to wrap.
	 * @return The wrapped certificate object.
	 * @throws IllegalArgumentException if the submitted object is no valid
	 *         certificate object type.
	 */
	public static CertObject wrap(String alias, Object object) throws IllegalArgumentException {
		assert alias != null;
		assert object != null;

		CertObject certObject;

		if (object instanceof X509Certificate || object instanceof KeyPair || object instanceof PKCS10CertificateRequest
				|| object instanceof X509CRL) {
			certObject = new CertObject(alias, object);
		} else {
			throw new IllegalArgumentException("Unexpected certificate object type: " + object.getClass().getName());
		}
		return certObject;
	}

	/**
	 * Wrap a raw certificate object.
	 *
	 * @param aliasIndex The alias index to use for alias name generation.
	 * @param object The certificate object to wrap.
	 * @return The wrapped certificate object.
	 * @throws IllegalArgumentException if the submitted object is no valid
	 *         certificate object type.
	 */
	public static CertObject wrap(int aliasIndex, Object object) throws IllegalArgumentException {
		assert object != null;

		CertObject certObject;

		if (object instanceof X509Certificate) {
			certObject = new CertObject("crt" + aliasIndex, object);
		} else if (object instanceof KeyPair) {
			certObject = new CertObject("key" + aliasIndex, object);
		} else if (object instanceof PKCS10CertificateRequest) {
			certObject = new CertObject("csr" + aliasIndex, object);
		} else if (object instanceof X509CRL) {
			certObject = new CertObject("crl" + aliasIndex, object);
		} else {
			throw new IllegalArgumentException("Unexpected certificate object type: " + object.getClass().getName());
		}
		return certObject;
	}

	/**
	 * Get this certificate object's alias.
	 *
	 * @return This certificate object's alias.
	 */
	public final String alias() {
		return this.alias;
	}

	/**
	 * Check whether this certificate object of type {@link X509Certificate}.
	 *
	 * @return {@code true} if this certificate object is of type
	 *         {@link X509Certificate}.
	 */
	public boolean isCRT() {
		return this.object instanceof X509Certificate;
	}

	/**
	 * Get the {@link X509Certificate} represented by this certificate object.
	 *
	 * @return The {@link X509Certificate} represented by this certificate
	 *         object.
	 * @throws ClassCastException if this certificate is not of type
	 *         {@link X509Certificate}.
	 * @see #isCRT()
	 */
	public X509Certificate getCRT() throws ClassCastException {
		return (X509Certificate) this.object;
	}

	/**
	 * Check whether this certificate object of type {@link KeyPair}.
	 *
	 * @return {@code true} if this certificate object is of type
	 *         {@link KeyPair}.
	 */
	public boolean isKey() {
		return this.object instanceof KeyPair;
	}

	/**
	 * Get the {@link KeyPair} represented by this certificate object.
	 *
	 * @return The {@link KeyPair} represented by this certificate object.
	 * @throws ClassCastException if this certificate is not of type
	 *         {@link KeyPair}.
	 * @see #isKey()
	 */
	public KeyPair getKey() throws ClassCastException {
		return (KeyPair) this.object;
	}

	/**
	 * Check whether this certificate object of type
	 * {@link PKCS10CertificateRequest}.
	 *
	 * @return {@code true} if this certificate object is of type
	 *         {@link PKCS10CertificateRequest}.
	 */
	public boolean isCSR() {
		return this.object instanceof PKCS10CertificateRequest;
	}

	/**
	 * Get the {@link PKCS10CertificateRequest} represented by this certificate
	 * object.
	 *
	 * @return The {@link PKCS10CertificateRequest} represented by this
	 *         certificate object.
	 * @throws ClassCastException if this certificate is not of type
	 *         {@link PKCS10CertificateRequest}.
	 * @see #isCSR()
	 */
	public PKCS10CertificateRequest getCSR() throws ClassCastException {
		return (PKCS10CertificateRequest) this.object;
	}

	/**
	 * Check whether this certificate object of type {@link X509CRL}.
	 *
	 * @return {@code true} if this certificate object is of type
	 *         {@link X509CRL}.
	 */
	public boolean isCRL() {
		return this.object instanceof X509CRL;
	}

	/**
	 * Get the {@link X509CRL} represented by this certificate object.
	 *
	 * @return The {@link X509CRL} represented by this certificate object.
	 * @throws ClassCastException if this certificate is not of type
	 *         {@link X509CRL}.
	 * @see #isCRL()
	 */
	public X509CRL getCRL() throws ClassCastException {
		return (X509CRL) this.object;
	}

}
