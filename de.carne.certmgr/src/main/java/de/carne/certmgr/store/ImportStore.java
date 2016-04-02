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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import de.carne.certmgr.store.provider.StoreProvider;

/**
 * An import store provides access to certificate data from external sources.
 */
public final class ImportStore {

	static final StoreProvider PROVIDER = StoreProvider.getInstance();

	private CertEntryMap<Entry> storeEntries = new CertEntryMap<>();

	/**
	 * Open an import store and read external data into it.
	 * <p>
	 * While reading the import sources any data not recognized as known certificate data or not accessible due to
	 * missing/invalid password is simply ignored. Hence the resulting import store contains as much as possible data.
	 * If the import sources do not contain any known certificate data the resulting import store will be empty. However
	 * the import process will fail, if recognized certificate data structures cannot be processed due to data errors or
	 * the like.
	 * </p>
	 *
	 * @param sources The sources to read the certificate data from.
	 * @param password The password callback to use for password retrieval.
	 * @return The opened import store (may be empty).
	 * @throws IOException if an I/O error occurs while decoding already recognized data.
	 */
	public static ImportStore open(ImportSource[] sources, PasswordCallback password) throws IOException {
		assert sources != null;
		assert password != null;

		Collection<Object> decoded = decodeSources(sources, password);

		return new ImportStore(decoded, password);
	}

	private ImportStore(Collection<Object> decoded, PasswordCallback password) throws IOException {
		for (Object certObject : decoded) {
			Entry mergedEntry;

			if (certObject instanceof KeyPair) {
				mergedEntry = mergeKey((KeyPair) certObject);
			} else if (certObject instanceof X509Certificate) {
				mergedEntry = mergeCRT((X509Certificate) certObject);
			} else if (certObject instanceof PKCS10Object) {
				mergedEntry = mergeCSR((PKCS10Object) certObject);
			} else if (certObject instanceof X509CRL) {
				mergedEntry = mergeCRL((X509CRL) certObject);
			} else {
				throw new IOException("Unexpected certificate object type: " + certObject.getClass());
			}
			resolveIssuers(mergedEntry);
		}
	}

	/**
	 * Get the number of certificate entries in the store.
	 *
	 * @return The number of certificate entries in the store.
	 */
	public synchronized int getEntryCount() {
		int entryCount = 0;

		for (CertEntry storeEntry : this.storeEntries.values()) {
			if (storeEntry.hasCRT() || storeEntry.hasCSR()) {
				entryCount++;
			}
		}
		return entryCount;
	}

	/**
	 * Get the root certificate entries currently in the store.
	 *
	 * @return The root certificate entries currently in the store.
	 */
	public synchronized Collection<CertEntry> getRootEntries() {
		ArrayList<CertEntry> rootEntries = new ArrayList<>();

		for (CertEntry storeEntry : this.storeEntries.values()) {
			if (storeEntry.isRoot() && (storeEntry.hasCRT() || storeEntry.hasCSR())) {
				rootEntries.add(storeEntry);
			}
		}
		return rootEntries;
	}

	/**
	 * Get a certificate entry's issued certificate entries.
	 *
	 * @param entry The certificate entry to get the issued certificate entries for.
	 * @return The submitted certificate entry's issued certificate entries.
	 */
	public synchronized Collection<CertEntry> getIssuedEntries(CertEntry entry) {
		ArrayList<CertEntry> issuedEntries = new ArrayList<>();

		for (CertEntry storeEntry : this.storeEntries.values()) {
			if (!storeEntry.isRoot() && (storeEntry.hasCRT() || storeEntry.hasCSR())
					&& entry.equals(storeEntry.getIssuer())) {
				issuedEntries.add(storeEntry);
			}
		}
		return issuedEntries;
	}

	private static Collection<Object> decodeSources(ImportSource[] sources, PasswordCallback password)
			throws IOException {
		ArrayList<Object> decoded = new ArrayList<>();

		for (ImportSource source : sources) {
			Collection<Object> decodedSource = decodeSource(source, password);

			if (decodedSource != null) {
				decoded.addAll(decodedSource);
			}
		}
		return decoded;
	}

	private static Collection<Object> decodeSource(ImportSource source, PasswordCallback password) throws IOException {
		Queue<CertFileFormat> formats = setupFormatQueue(source.getFormatHint());
		Collection<Object> decoded = null;
		CertFileFormat format;

		while (decoded == null && (format = formats.poll()) != null) {
			decoded = tryDecodeSource(source, format, password);
		}
		return decoded;
	}

	private static Queue<CertFileFormat> setupFormatQueue(CertFileFormat formatHint) {
		CertFileFormat[] formats = CertFileFormat.values();
		LinkedList<CertFileFormat> queue = new LinkedList<>();

		for (CertFileFormat format : formats) {
			if (format.equals(formatHint)) {
				queue.addFirst(format);
			} else {
				queue.addLast(format);
			}
		}
		return queue;
	}

	private static Collection<Object> tryDecodeSource(ImportSource source, CertFileFormat format,
			PasswordCallback password) throws IOException {
		Collection<Object> decoded = null;
		String stringData = null;
		byte[] byteData = null;

		switch (format) {
		case PEM:
			stringData = source.getStringData();
			if (stringData != null) {
				decoded = PROVIDER.tryDecodePEM(stringData, password, source.getResource());
			}
			break;
		case PKCS12:
			byteData = source.getByteData();
			if (byteData != null) {
				decoded = PROVIDER.tryDecodePKCS12(byteData, password, source.getResource());
			}
			break;
		default:
			throw new IllegalArgumentException("Unexpected format: " + format);
		}
		return decoded;
	}

	private Entry mergeKey(KeyPair key) throws IOException {
		Entry matchedEntry = this.storeEntries.matchKey(key);
		Entry mergedEntry;

		if (matchedEntry != null) {
			matchedEntry.mergeKey(key);
			mergedEntry = matchedEntry;
		} else {
			mergedEntry = new Entry(key);
			this.storeEntries.put(mergedEntry, mergedEntry);
		}
		return mergedEntry;
	}

	private Entry mergeCRT(X509Certificate crt) throws IOException {
		Entry matchedEntry = this.storeEntries.matchCRT(crt);
		Entry mergedEntry;

		if (matchedEntry != null) {
			matchedEntry.mergeCRT(crt);
			mergedEntry = matchedEntry;
		} else {
			mergedEntry = new Entry(crt);
			this.storeEntries.put(mergedEntry, mergedEntry);
		}
		return mergedEntry;
	}

	private Entry mergeCSR(PKCS10Object csr) throws IOException {
		Entry matchedEntry = this.storeEntries.matchCSR(csr);
		Entry mergedEntry;

		if (matchedEntry != null) {
			matchedEntry.mergeCSR(csr);
			mergedEntry = matchedEntry;
		} else {
			mergedEntry = new Entry(csr);
			this.storeEntries.put(mergedEntry, mergedEntry);
		}
		return mergedEntry;
	}

	private Entry mergeCRL(X509CRL crl) throws IOException {
		Entry matchedEntry = this.storeEntries.matchCRL(crl);
		Entry mergedEntry;

		if (matchedEntry != null) {
			matchedEntry.mergeCRL(crl);
			mergedEntry = matchedEntry;
		} else {
			mergedEntry = new Entry(crl);
			this.storeEntries.put(mergedEntry, mergedEntry);
		}
		return mergedEntry;
	}

	private void resolveIssuers(Entry mergedEntry) throws IOException {
		if (mergedEntry.hasCRT()) {
			for (Entry entry : this.storeEntries.values()) {
				if (mergedEntry.getIssuer() == null && mergedEntry.isIssuedBy(entry)) {
					mergedEntry.setIssuer(entry);
				}
				if (entry.getIssuer() == null && entry.isIssuedBy(mergedEntry)) {
					entry.setIssuer(mergedEntry);
				}
			}
		}
	}

	private class Entry extends CertEntry {

		private SimpleCertObject<KeyPair> keyObject;
		private SimpleCertObject<X509Certificate> crtObject;
		private SimpleCertObject<PKCS10Object> csrObject;
		private SimpleCertObject<X509CRL> crlObject;
		private CertEntry issuer;

		private Entry(SimpleCertObject<KeyPair> keyObject, SimpleCertObject<X509Certificate> crtObject,
				SimpleCertObject<PKCS10Object> csrObject, SimpleCertObject<X509CRL> crlObject, CertEntry issuer) {
			this.keyObject = keyObject;
			this.crtObject = crtObject;
			this.csrObject = csrObject;
			this.crlObject = crlObject;
			this.issuer = issuer;
		}

		Entry(KeyPair key) {
			this(new SimpleCertObject<>(CertObject.getKeyName(key), key), null, null, null, null);
		}

		Entry(X509Certificate crt) {
			this(null, new SimpleCertObject<>(CertObject.getCRTName(crt), crt), null, null, null);
		}

		Entry(PKCS10Object csr) {
			this(null, null, new SimpleCertObject<>(CertObject.getCSRName(csr), csr), null, null);
		}

		Entry(X509CRL crl) {
			this(null, null, null, new SimpleCertObject<>(CertObject.getCRLName(crl), crl), null);
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#getName()
		 */
		@Override
		public String getName() {
			String name;

			if (this.crtObject != null) {
				name = this.crtObject.getName();
			} else if (this.csrObject != null) {
				name = this.csrObject.getName();
			} else if (this.crlObject != null) {
				name = this.crlObject.getName();
			} else if (this.keyObject != null) {
				name = this.keyObject.getName();
			} else {
				throw new IllegalArgumentException("At least one argument must not be null");
			}
			return name;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#hasKey(boolean)
		 */
		@Override
		public boolean hasKey(boolean havePassword) {
			return this.keyObject != null;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#getKey(de.carne.certmgr.store.PasswordCallback)
		 */
		@Override
		public CertObject<KeyPair> getKey(PasswordCallback password) throws PasswordRequiredException, IOException {
			return this.keyObject;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#hasCRT()
		 */
		@Override
		public boolean hasCRT() {
			return this.crtObject != null;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#getCRT()
		 */
		@Override
		public CertObject<X509Certificate> getCRT() throws IOException {
			return this.crtObject;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#hasCSR()
		 */
		@Override
		public boolean hasCSR() {
			return this.csrObject != null;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#getCSR()
		 */
		@Override
		public CertObject<PKCS10Object> getCSR() throws IOException {
			return this.csrObject;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#hasCRL()
		 */
		@Override
		public boolean hasCRL() {
			return this.crlObject != null;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#getCRL()
		 */
		@Override
		public CertObject<X509CRL> getCRL() throws IOException {
			return this.crlObject;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.certmgr.store.CertEntry#getIssuer()
		 */
		@Override
		public CertEntry getIssuer() {
			return this.issuer;
		}

		void mergeKey(KeyPair key) {
			this.keyObject = new SimpleCertObject<>(CertObject.getKeyName(key), key);
		}

		void mergeCRT(X509Certificate crt) {
			this.crtObject = new SimpleCertObject<>(CertObject.getCRTName(crt), crt);
		}

		void mergeCSR(PKCS10Object csr) {
			this.csrObject = new SimpleCertObject<>(CertObject.getCSRName(csr), csr);
		}

		void mergeCRL(X509CRL crl) {
			this.crlObject = new SimpleCertObject<>(CertObject.getCRLName(crl), crl);
		}

		void setIssuer(CertEntry issuer) {
			this.issuer = issuer;
		}

	}

}
