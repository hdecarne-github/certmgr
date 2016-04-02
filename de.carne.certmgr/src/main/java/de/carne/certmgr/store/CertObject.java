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
package de.carne.certmgr.store;

import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import de.carne.certmgr.store.x500.X500Names;

/**
 * Abstract base class for holding certificate objects.
 * <p>
 * This class is used to provide access to the different objects combined in a {@linkplain CertEntry} instance.
 * </p>
 *
 * @param <T> The object type to hold.
 */
public abstract class CertObject<T> {

	private String name;

	/**
	 * Construct CertObject.
	 *
	 * @param name The object's name.
	 */
	protected CertObject(String name) {
		assert name != null;

		this.name = name;
	}

	/**
	 * Get the key object's name.
	 *
	 * @param keypair The key object to get the name for.
	 * @return The key object's name.
	 */
	public static String getKeyName(KeyPair keypair) {
		assert keypair != null;

		return keypair.getPublic().getAlgorithm() + " key";
	}

	/**
	 * Get the CRT issuers object's name.
	 *
	 * @param crt The CRT object to get the issuer's name for.
	 * @return The CRT object issuer's name.
	 */
	public static String getCRTIssuerName(X509Certificate crt) {
		assert crt != null;

		return X500Names.toString(crt.getIssuerX500Principal());
	}

	/**
	 * Get the CRT object's name.
	 *
	 * @param crt The CRT object to get the name for.
	 * @return The CRT object's name.
	 */
	public static String getCRTName(X509Certificate crt) {
		assert crt != null;

		return X500Names.toString(crt.getSubjectX500Principal());
	}

	/**
	 * Get the CSR object's name.
	 *
	 * @param csr The CSR object to get the name for.
	 * @return The CSR object's name.
	 */
	public static String getCSRName(PKCS10Object csr) {
		assert csr != null;

		return X500Names.toString(csr.getSubjectX500Principal());
	}

	/**
	 * Get the CRL object's name.
	 *
	 * @param crl The CRL object to get the name for.
	 * @return The CRL object's name.
	 */
	public static String getCRLName(X509CRL crl) {
		assert crl != null;

		return X500Names.toString(crl.getIssuerX500Principal());
	}

	/**
	 * Get the object's name.
	 *
	 * @return The object's name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the object.
	 *
	 * @return The object.
	 * @throws IOException If an I/O error occurs while accessing the object's data.
	 */
	public abstract T getObject() throws IOException;

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

}
