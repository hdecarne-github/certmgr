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

import java.util.Collection;
import java.util.HashMap;

import javax.security.auth.x500.X500Principal;

/**
 * Parameter class containing the necessary parameters for certificate generation.
 */
public class X509CertificateParams {

	private X500Principal subjectDN;
	private String sigAlg;
	private HashMap<String, X509Extension> extensions = new HashMap<>();

	/**
	 * Construct CertificateParams.
	 *
	 * @param subjectDN The certificate subject.
	 * @param sigAlg The certificate's signature algorithm.
	 */
	public X509CertificateParams(X500Principal subjectDN, String sigAlg) {
		assert subjectDN != null;
		assert sigAlg != null;

		this.subjectDN = subjectDN;
		this.sigAlg = sigAlg;
	}

	/**
	 * @return the subjectDN
	 */
	public X500Principal getSubjectDN() {
		return this.subjectDN;
	}

	/**
	 * @return the sigAlg
	 */
	public String getSigAlg() {
		return this.sigAlg;
	}

	/**
	 * @return the extensions
	 */
	public Collection<X509Extension> getExtensions() {
		return this.extensions.values();
	}

	/**
	 * @param extension the extensions to add
	 */
	public void addExtension(X509Extension extension) {
		this.extensions.put(extension.getOID(), extension);
	}

}
