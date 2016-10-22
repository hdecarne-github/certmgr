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

import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.security.auth.x500.X500Principal;

/**
 * Certificate store entry.
 * <p>
 * A Certificate store entry consists of multiple (optional) certificate
 * objects:
 * <ul>
 * <li><strong>CRT</strong>: The actual certificate</li>
 * <li><strong>Key</strong>: The certificate's key pair</li>
 * <li><strong>CSR</strong>: The certificate's signing request</li>
 * <li><strong>CRL</strong>: The certificate's revocation list</li>
 * </ul>
 */
public abstract class UserCertStoreEntry {

	private final UserCertStoreEntryId id;

	private final X500Principal cn;

	UserCertStoreEntry(UserCertStoreEntryId id, X500Principal cn) {
		this.id = id;
		this.cn = cn;
	}

	/**
	 * Get this entry's display name.
	 * <p>
	 * The name is either the cn's name if available or the id's string
	 * representation.
	 *
	 * @return This entry's display name.
	 */
	public final String getName() {
		return (this.cn != null ? this.cn.getName() : this.id.toString());
	}

	/**
	 * Get this entry's {@link UserCertStore}.
	 *
	 * @return This entry's {@link UserCertStore}.
	 */
	public abstract UserCertStore store();

	/**
	 * Get this entry's id.
	 *
	 * @return This entry's id.
	 */
	public final UserCertStoreEntryId id() {
		return this.id;
	}

	/**
	 * Get this entry's common name (CN).
	 *
	 * @return This entry's common name (CN).
	 */
	public final X500Principal cn() {
		return this.cn;
	}

	/**
	 * Get this entry's issuing certificate store entry.
	 * <p>
	 * Self-signed certificates will return themselves here.
	 *
	 * @return This entry's issuing certificate store entry.
	 * @see #isSelfSigned()
	 */
	public abstract UserCertStoreEntry issuer();

	/**
	 * Get the user certificate store entries issued by this entry.
	 * <p>
	 * This gives the same result as calling
	 * <p>
	 * <code>entry.store().getIssuedEntries(this)</code>
	 *
	 * @return The user certificate store entries issued by this entry.
	 */
	public List<UserCertStoreEntry> issuedEntries() {
		return store().getIssuedEntries(this);
	}

	/**
	 * Check whether this entry is self-signed (is it's own issuer).
	 *
	 * @return {@code true} if this entry is it's own issuer.
	 * @see #issuer()
	 */
	public boolean isSelfSigned() {
		return equals(issuer());
	}

	/**
	 * Check whether this entry contains a CRT (Certificate) object.
	 *
	 * @return {@code true} if this entry contains a CRT (Certificate) object.
	 * @see #getCRT()
	 */
	public abstract boolean hasCRT();

	/**
	 * Get this entry's CRT (Certificate) object.
	 *
	 * @return This entry's CRT (Certificate) object (may be {@code null}).
	 * @throws IOException if an I/O error occurs while loading the CRT object.
	 * @see #hasCRT()
	 */
	public abstract X509Certificate getCRT() throws IOException;

	/**
	 * Check whether this entry contains a decrypted Key object.
	 *
	 * @return {@code true} if this entry contains a decrypted Key object.
	 * @see #getKey()
	 */
	public abstract boolean hasDecryptedKey();

	/**
	 * Check whether this entry contains a Key object.
	 *
	 * @return {@code true} if this entry contains a Key object.
	 * @see #getKey(PasswordCallback)
	 */
	public abstract boolean hasKey();

	/**
	 * Get this entry's Key object.
	 * <p>
	 * This function assumes that the Key object is decrypted and therefore no
	 * password is required to access it.
	 *
	 * @return This entry's Key object (may be {@code null}).
	 * @throws PasswordRequiredException if a password is required.
	 * @throws IOException if an I/O error occurs while loading the Key object.
	 * @see #hasDecryptedKey()
	 */
	public KeyPair getKey() throws IOException {
		return getKey(NoPassword.getInstance());
	}

	/**
	 * Get this entry's Key object.
	 *
	 * @param password The callback to use for querying passwords (if needed).
	 * @return This entry's Key object (may be {@code null}).
	 * @throws PasswordRequiredException if no valid password was given.
	 * @throws IOException if an I/O error occurs while loading the Key object.
	 * @see #hasKey()
	 */
	public abstract KeyPair getKey(PasswordCallback password) throws IOException;

	/**
	 * Check whether this entry contains a CSR (Certificate Signing Request)
	 * object.
	 *
	 * @return {@code true} if this entry contains a CSR (Certificate Signing
	 *         Request) object.
	 * @see #getCSR()
	 */
	public abstract boolean hasCSR();

	/**
	 * Get this entry's CSR (Certificate Signing Request) object.
	 *
	 * @return This entry's CSR (Certificate Signing Request) object (may be
	 *         {@code null}).
	 * @throws IOException if an I/O error occurs while loading the CSR object.
	 * @see #hasCSR()
	 */
	public abstract PKCS10CertificateRequest getCSR() throws IOException;

	/**
	 * Check whether this entry contains a CRL (Certificate Revocation List)
	 * object.
	 *
	 * @return {@code true} if this entry contains a CRL (Certificate Revocation
	 *         List) object.
	 * @see #getCRL()
	 */
	public abstract boolean hasCRL();

	/**
	 * Get this entry's CRL (Certificate Revocation List) object.
	 *
	 * @return This entry's CRL (Certificate Revocation List) object (may be
	 *         {@code null}).
	 * @throws IOException if an I/O error occurs while loading the CRL object.
	 * @see #hasCRL()
	 */
	public abstract X509CRL getCRL() throws IOException;

	/**
	 * Check whether this entry represents an external certificate (means
	 * contains no actual certificate objects).
	 * <p>
	 * External entries are used whenever an issuer reference cannot be resolved
	 * to another certificate store entry.
	 *
	 * @return {@code true} if this entry represents an external certificate.
	 */
	public final boolean isExternal() {
		return !hasCRT() && !hasKey() && !hasCSR() && !hasCRL();
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean equal = false;

		if (obj instanceof UserCertStoreEntry) {
			UserCertStoreEntry other = (UserCertStoreEntry) obj;

			equal = this.id.equals(other.id) && store().equals(other.store());
		}
		return equal;
	}

	@Override
	public String toString() {
		return getName();
	}

}
