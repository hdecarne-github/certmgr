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

/**
 * Class combining the different objects by which a certificate is represented.
 * <p>
 * An actual certificate is represented by up to 4 different objects.
 * <ul>
 * <li>An key (respectively the key pair). This object is available for owned certificates and also is a prerequisite
 * for signing other objects</li>
 * <li>A CRT (X509 Certificate) object. This is the actual certificate.</li>
 * <li>A CSR (Certificate Signing Request) object. This object is available for externally signed certificates. As a key
 * is required for creating the CSR the presence of this object implies the presence of the corresponding key.</li>
 * <li>A CRL (Certificate Revocation List). Via this object previously signed certificates can be revoked afterwards. As
 * a key is required for creating the CRL the presence of this object implies the presence of the corresponding key.</li>
 * </ul>
 * </p>
 * The following combinations of available objects are possible:
 * <ul>
 * <li>None: For an unknown external certificates.</li>
 * <li>CRT only: For external certificates.</li>
 * <li>Key, CRT and optional CSR, CRL: For internal certificates.</li>
 * </ul>
 */
public abstract class CertEntry {

	/**
	 * Get the certificate entry's name.
	 *
	 * @return The certificate entry's name.
	 */
	public abstract String getName();

	/**
	 * Check whether the certificate entry has a key object.
	 *
	 * @return true, if the certificate entry has a key object (either password protected or not).
	 */
	public boolean hasKey() {
		return hasKey(true);
	}

	/**
	 * Check whether the certificate entry has a key object and if is accessible with our without password.
	 *
	 * @param havePassword Indicates whether a password is available or not.
	 * @return true, if the certificate entry has a key object and the entry is accessible with the indicated password
	 *         availability.
	 */
	public abstract boolean hasKey(boolean havePassword);

	/**
	 * Get the certificate entry's key object (without providing a password).
	 *
	 * @return The certificate entry's key object.
	 * @throws PasswordRequiredException if a password is required to access the key object.
	 * @throws IOException if an I/O error occurs while accessing the key object.
	 */
	public CertObject<KeyPair> getKey() throws PasswordRequiredException, IOException {
		return getKey(null);
	}

	/**
	 * Get the certificate entry's key object.
	 *
	 * @param password The password to use for accessing the key object.
	 * @return The certificate entry's key object.
	 * @throws PasswordRequiredException if a password is required to access the key object.
	 * @throws IOException if an I/O error occurs while accessing the key object.
	 */
	public abstract CertObject<KeyPair> getKey(PasswordCallback password) throws PasswordRequiredException, IOException;

	/**
	 * Check whether the certificate entry has a CRT object.
	 *
	 * @return true, if the certificate entry has a CRT object.
	 */
	public abstract boolean hasCRT();

	/**
	 * Get the certificate entry's CRT object.
	 *
	 * @return The certificate entry's CRT object.
	 * @throws IOException if an I/O error occurs while accessing the CRT object.
	 */
	public abstract CertObject<X509Certificate> getCRT() throws IOException;

	/**
	 * Check whether the certificate entry has a CSR object.
	 *
	 * @return true, if the certificate entry has a CSR object.
	 */
	public abstract boolean hasCSR();

	/**
	 * Get the certificate entry's CSR object.
	 *
	 * @return The certificate entry's CSR object.
	 * @throws IOException if an I/O error occurs while accessing the CSR object.
	 */
	public abstract CertObject<PKCS10Object> getCSR() throws IOException;

	/**
	 * Check whether the certificate entry has a CRL object.
	 *
	 * @return true, if the certificate entry has a CRL object.
	 */
	public abstract boolean hasCRL();

	/**
	 * Get the certificate entry's CRL object.
	 *
	 * @return The certificate entry's CRL object.
	 * @throws IOException if an I/O error occurs while accessing the CRL object.
	 */
	public abstract CertObject<X509CRL> getCRL() throws IOException;

	/**
	 * Get this certificate entry's issuer.
	 *
	 * @return This certificate entry's issuer.
	 */
	public abstract CertEntry getIssuer();

	/**
	 * Get this certificate entry's root issuer.
	 *
	 * @return This certificate entry's root issuer.
	 */
	public CertEntry getRootIssuer() {
		CertEntry rootIssuer = this;

		while (!rootIssuer.isRoot()) {
			rootIssuer = rootIssuer.getIssuer();
		}
		return rootIssuer;
	}

	/**
	 * Check whether this certificate entry is a root entry.
	 * <p>
	 * A certificate entry is a root entry if it is issued by itself.
	 * </p>
	 *
	 * @return true, if this certificate entry is a root entry.
	 */
	public boolean isRoot() {
		CertEntry issuer = getIssuer();

		return issuer == null || equals(issuer);
	}

	/**
	 * Check whether this certificate entry is issued by another certificate entry.
	 *
	 * @param issuerEntry The issuer entry to check against.
	 * @return true, if the certificate entry is issued by the submitted certificate entry.
	 * @throws IOException if an I/O error occurs while processing the certificate entry.
	 */
	public boolean isIssuedBy(CertEntry issuerEntry) throws IOException {
		assert issuerEntry != null;

		boolean isIssuedBy = issuerEntry.equals(getIssuer());

		if (!isIssuedBy && hasCRT() && issuerEntry.hasCRT()) {
			X509Certificate crt = getCRT().getObject();
			X509Certificate issuerCRT = issuerEntry.getCRT().getObject();

			if (crt.getIssuerX500Principal().equals(issuerCRT.getSubjectX500Principal())) {
				try {
					crt.verify(issuerCRT.getPublicKey());
					isIssuedBy = true;
				} catch (Exception e) {
					// Not issued by
				}
			}
		}
		return isIssuedBy;
	}

	/**
	 * Check whether this certificate entry has been revoked.
	 * <p>
	 * The check is performed against the entry's issuer's CRL. If no issuer exists or if the issuer does not have a
	 * CRL, the entry is considered valid (not revoked).
	 * </p>
	 *
	 * @return true, if the certificate has been revoked.
	 * @throws IOException if an I/O error occurs while processing the certificate entry.
	 */
	public boolean isRevoked() throws IOException {
		boolean isRevoked = false;

		if (hasCRT()) {
			CertEntry issuer = getIssuer();

			if (issuer != null && issuer.hasCRL()) {
				X509Certificate crt = getCRT().getObject();
				X509CRL crl = issuer.getCRL().getObject();

				isRevoked = crl.isRevoked(crt);
			}
		}
		return isRevoked;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

}
