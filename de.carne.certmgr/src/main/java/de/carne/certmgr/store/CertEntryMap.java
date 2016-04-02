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
import java.util.HashMap;
import java.util.function.Predicate;

/**
 * Certificate entry map providing extended support for certificate handling.
 */
class CertEntryMap<T extends CertEntry> extends HashMap<CertEntry, T> {

	/**
	 * Serialization support.
	 */
	private static final long serialVersionUID = 1L;

	public Collection<T> values(Predicate<T> predicate) {
		ArrayList<T> values = new ArrayList<>(size());

		for (T entry : values()) {
			if (predicate.test(entry)) {
				values.add(entry);
			}
		}
		return values;
	}

	public T matchKey(KeyPair key) throws IOException {
		T matchingEntry = null;

		for (T entry : values()) {
			if (matchKey(entry, key)) {
				matchingEntry = entry;
				break;
			}
		}
		return matchingEntry;
	}

	public T matchCRT(X509Certificate crt) throws IOException {
		T matchingEntry = null;

		for (T entry : values()) {
			if (matchCRT(entry, crt)) {
				matchingEntry = entry;
				break;
			}
		}
		return matchingEntry;
	}

	public T matchCSR(PKCS10Object csr) throws IOException {
		T matchingEntry = null;

		for (T entry : values()) {
			if (matchCSR(entry, csr)) {
				matchingEntry = entry;
				break;
			}
		}
		return matchingEntry;
	}

	public T matchCRL(X509CRL crl) throws IOException {
		T matchingEntry = null;

		for (T entry : values()) {
			if (matchCRL(entry, crl)) {
				matchingEntry = entry;
				break;
			}
		}
		return matchingEntry;
	}

	private boolean matchKey(CertEntry entry, KeyPair key) throws IOException {
		boolean match = false;

		if (entry.hasKey(false)) {
			match = entry.getKey().equals(key);
		} else if (entry.hasCSR()) {
			try {
				entry.getCSR().getObject().verify(key.getPublic());
				match = true;
			} catch (Exception e) {
				// No match
			}
		} else if (entry.hasCRL()) {
			try {
				entry.getCRL().getObject().verify(key.getPublic());
				match = true;
			} catch (Exception e) {
				// No match
			}
		} else if (entry.hasCRT()) {
			match = entry.getCRT().getObject().getPublicKey().equals(key.getPublic());
		}
		return match;
	}

	private static boolean matchCRT(CertEntry entry, X509Certificate crt) throws IOException {
		boolean match = false;

		if (entry.hasCRT()) {
			match = entry.getCRT().getObject().equals(crt);
		} else if (entry.hasCSR()) {
			try {
				entry.getCSR().getObject().verify(crt.getPublicKey());
				match = true;
			} catch (Exception e) {
				// No match
			}
		} else if (entry.hasCRL()) {
			try {
				entry.getCRL().getObject().verify(crt.getPublicKey());
				match = true;
			} catch (Exception e) {
				// No match
			}
		} else if (entry.hasKey(false)) {
			match = entry.getKey().getObject().getPublic().equals(crt.getPublicKey());
		}
		return match;
	}

	private static boolean matchCSR(CertEntry entry, PKCS10Object csr) throws IOException {
		boolean match = false;

		if (entry.hasCSR()) {
			match = entry.getCSR().getObject().equals(csr);
		} else if (entry.hasKey(false)) {
			try {
				csr.verify(entry.getKey().getObject().getPublic());
				match = true;
			} catch (Exception e) {
				// No match
			}
		} else if (entry.hasCRT()) {
			try {
				csr.verify(entry.getCRT().getObject().getPublicKey());
				match = true;
			} catch (Exception e) {
				// No match
			}
		} else if (entry.hasCRL()) {
			try {
				entry.getCRL().getObject().verify(csr.getPublicKey());
				match = true;
			} catch (Exception e) {
				// No match
			}
		}
		return match;
	}

	private static boolean matchCRL(CertEntry entry, X509CRL crl) throws IOException {
		boolean match = false;

		if (entry.hasCRL()) {
			match = entry.getCRL().getObject().equals(crl);
		} else if (entry.hasKey(false)) {
			try {
				crl.verify(entry.getKey().getObject().getPublic());
				match = true;
			} catch (Exception e) {
				// No match
			}
		} else if (entry.hasCRT()) {
			try {
				crl.verify(entry.getCRT().getObject().getPublicKey());
				match = true;
			} catch (Exception e) {
				// No match
			}
		} else if (entry.hasCSR()) {
			try {
				crl.verify(entry.getCSR().getObject().getPublicKey());
				match = true;
			} catch (Exception e) {
				// No match
			}
		}
		return match;
	}

}
