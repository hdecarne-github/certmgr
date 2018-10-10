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
package de.carne.certmgr.certs;

import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.certmgr.certs.x509.KeyHelper;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;

/**
 * Class used to collect/transfer individual certificate objects for reading and writing.
 */
public final class CertObjectStore implements Iterable<CertObjectStore.Entry> {

	/**
	 * This class represents a single certificate object.
	 */
	public final class Entry {

		private final String alias;
		private final CertObjectType type;
		private final Object object;
		private final byte[] encoded;

		Entry(Entry entry) {
			this.alias = entry.alias;
			this.type = entry.type;
			this.object = entry.object;
			this.encoded = entry.encoded;
		}

		Entry(String alias, CertObjectType type, Object object, byte[] encoded) {
			this.alias = alias;
			this.type = type;
			this.object = object;
			this.encoded = encoded;
		}

		/**
		 * Get this certificate object's alias.
		 *
		 * @return This certificate object's alias.
		 */
		public String alias() {
			return this.alias;
		}

		/**
		 * Get this certificate object's type.
		 *
		 * @return This certificate object's type.
		 * @see CertObjectType
		 */
		public CertObjectType type() {
			return this.type;
		}

		/**
		 * Get the CRT ({@link X509Certificate}) represented by this certificate object.
		 *
		 * @return The CRT represented by this certificate object.
		 * @throws ClassCastException if this certificate is not of type CRT.
		 * @see #type()
		 */
		public X509Certificate getCRT() throws ClassCastException {
			return (X509Certificate) this.object;
		}

		/**
		 * Get the Key ({@link KeyPair}) represented by this certificate object.
		 *
		 * @return The Key represented by this certificate object.
		 * @throws ClassCastException if this certificate is not of type Key.
		 * @see #type()
		 */
		public KeyPair getKey() throws ClassCastException {
			return (KeyPair) this.object;
		}

		/**
		 * Get the CSR ({@link PKCS10CertificateRequest}) represented by this certificate object.
		 *
		 * @return The CSR represented by this certificate object.
		 * @throws ClassCastException if this certificate is not of type CSR.
		 * @see #type()
		 */
		public PKCS10CertificateRequest getCSR() throws ClassCastException {
			return (PKCS10CertificateRequest) this.object;
		}

		/**
		 * Get the CRL ({@link X509CRL}) represented by this certificate object.
		 *
		 * @return The CRL represented by this certificate object.
		 * @throws ClassCastException if this certificate is not of type CRL.
		 * @see #type()
		 */
		public X509CRL getCRL() throws ClassCastException {
			return (X509CRL) this.object;
		}

		@Override
		public int hashCode() {
			return this.object.hashCode();
		}

		@Override
		public boolean equals(@Nullable Object obj) {
			boolean equals;

			if (this == obj) {
				equals = true;
			} else if (!(obj instanceof Entry)) {
				equals = false;
			} else {
				Entry entryObj = (Entry) obj;

				equals = this.type == entryObj.type && Arrays.equals(this.encoded, entryObj.encoded);
			}
			return equals;
		}

		@Override
		public String toString() {
			return this.alias + ":" + this.type;
		}

	}

	private final Set<Entry> entries = new LinkedHashSet<>();
	private int crtNumber = 1;
	private int keyNumber = 1;
	private int csrNumber = 1;
	private int crlNumber = 1;

	/**
	 * Wrap a single store entry into a store.
	 *
	 * @param entry The entry to wrap.
	 * @return The certificate store containing the submitted entry object.
	 */
	public static CertObjectStore wrap(Entry entry) {
		CertObjectStore store = new CertObjectStore();

		store.entries.add(store.new Entry(entry));
		return store;
	}

	/**
	 * Add a CRT object to the store.
	 *
	 * @param crt The CRT object to add.
	 * @throws IOException if an encoding error occurs.
	 */
	public void addCRT(X509Certificate crt) throws IOException {
		addCRT("crt" + this.crtNumber, crt);
		this.crtNumber++;
	}

	/**
	 * Add a CRT object to the store.
	 *
	 * @param alias The alias to use.
	 * @param crt The CRT object to add.
	 * @throws IOException if an encoding error occurs.
	 */
	public void addCRT(String alias, X509Certificate crt) throws IOException {
		try {
			this.entries.add(new Entry(alias, CertObjectType.CRT, crt, crt.getEncoded()));
		} catch (CertificateEncodingException e) {
			throw new IOException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Add a Key object to the store.
	 *
	 * @param key The Key object to add.
	 * @throws IOException if an encoding error occurs.
	 */
	public void addKey(KeyPair key) throws IOException {
		addKey("key" + this.keyNumber, key);
		this.keyNumber++;
	}

	/**
	 * Add a Key object to the store.
	 *
	 * @param alias The alias to use.
	 * @param key The Key object to add.
	 * @throws IOException if an encoding error occurs.
	 */
	public void addKey(String alias, KeyPair key) throws IOException {
		this.entries.add(new Entry(alias, CertObjectType.KEY, key, KeyHelper.encodePrivateKey(key.getPrivate())));
	}

	/**
	 * Add a CSR object to the store.
	 *
	 * @param csr The CSR object to add.
	 * @throws IOException if an encoding error occurs.
	 */
	public void addCSR(PKCS10CertificateRequest csr) throws IOException {
		addCSR("csr" + this.csrNumber, csr);
		this.csrNumber++;
	}

	/**
	 * Add a CSR object to the store.
	 *
	 * @param alias The alias to use.
	 * @param csr The CSR object to add.
	 * @throws IOException if an encoding error occurs.
	 */
	public void addCSR(String alias, PKCS10CertificateRequest csr) throws IOException {
		this.entries.add(new Entry(alias, CertObjectType.CSR, csr, csr.getEncoded()));
	}

	/**
	 * Add a CRL object to the store.
	 *
	 * @param crl The CRL object to add.
	 * @throws IOException if an encoding error occurs.
	 */
	public void addCRL(X509CRL crl) throws IOException {
		addCRL("crl" + this.crlNumber, crl);
		this.crlNumber++;
	}

	/**
	 * Add a CRL object to the store.
	 *
	 * @param alias The alias to use.
	 * @param crl The CRL object to add.
	 * @throws IOException if an encoding error occurs.
	 */
	public void addCRL(String alias, X509CRL crl) throws IOException {
		try {
			this.entries.add(new Entry(alias, CertObjectType.CRL, crl, crl.getEncoded()));
		} catch (CRLException e) {
			throw new IOException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Get the number of certificate objects in this store.
	 *
	 * @return The number of certificate objects in this store.
	 */
	public int size() {
		return this.entries.size();
	}

	@Override
	public Iterator<Entry> iterator() {
		return this.entries.iterator();
	}

}
