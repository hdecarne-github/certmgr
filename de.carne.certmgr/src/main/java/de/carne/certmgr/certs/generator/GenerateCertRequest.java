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
package de.carne.certmgr.certs.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.certs.x509.X509ExtensionData;

/**
 * Parameter container for certificate generation.
 */
public class GenerateCertRequest {

	private final X500Principal dn;
	private final KeyPairAlgorithm keyPairAlgorithm;
	private final int keySize;
	private Issuer issuer = null;
	private SignatureAlgorithm signatureAlgorithm = null;
	private Date notBefore = null;
	private Date notAfter = null;
	private List<X509ExtensionData> extensions = new ArrayList<>();

	/**
	 * Construct {@code GenerateCertRequest}.
	 *
	 * @param dn The Distinguished Name (DN) to use for generation.
	 * @param keyPairAlgorithm The key pair algorithm to use for generation.
	 * @param keySize The key size to use for generation.
	 */
	public GenerateCertRequest(X500Principal dn, KeyPairAlgorithm keyPairAlgorithm, int keySize) {
		assert dn != null;
		assert keyPairAlgorithm != null;

		this.dn = dn;
		this.keyPairAlgorithm = keyPairAlgorithm;
		this.keySize = keySize;
	}

	/**
	 * Get the Distinguished Name (DN) to use for generation.
	 *
	 * @return The Distinguished Name (DN) to use for generation.
	 */
	public X500Principal dn() {
		return this.dn;
	}

	/**
	 * Get the key pair algorithm to use for generation.
	 *
	 * @return The key pair algorithm to use for generation.
	 */
	public KeyPairAlgorithm keyPairAlgorithm() {
		return this.keyPairAlgorithm;
	}

	/**
	 * Get the key size to use for generation.
	 *
	 * @return The key size to use for generation.
	 */
	public int keySize() {
		return this.keySize;
	}

	/**
	 * Set the issuer to use for generation.
	 *
	 * @param issuer The issuer to use for generation.
	 */
	public void setIssuer(Issuer issuer) {
		assert issuer != null;

		this.issuer = issuer;
	}

	/**
	 * Get the issuer to use for generation.
	 *
	 * @return The issuer to use for generation.
	 */
	public Issuer getIssuer() {
		return this.issuer;
	}

	/**
	 * Set the signature algorithm to use for generation.
	 *
	 * @param signatureAlgorithm The signature algorithm to use for generation.
	 */
	public void setSignatureAlgorithm(SignatureAlgorithm signatureAlgorithm) {
		assert signatureAlgorithm != null;

		this.signatureAlgorithm = signatureAlgorithm;
	}

	/**
	 * Get the signature algorithm to use for generation.
	 *
	 * @return The signature algorithm to use for generation.
	 */
	public SignatureAlgorithm getSignatureAlgorithm() {
		return this.signatureAlgorithm;
	}

	/**
	 * Set the validity begin date.
	 *
	 * @param notBefore The validity begin date.
	 */
	public void setNotBefore(Date notBefore) {
		assert notBefore != null;

		this.notBefore = notBefore;
	}

	/**
	 * Get the validity begin date.
	 *
	 * @return The validity begin date.
	 */
	public Date getNotBefore() {
		return this.notBefore;
	}

	/**
	 * Set the validity end date.
	 *
	 * @param notAfter The validity end date.
	 */
	public void setNotAfter(Date notAfter) {
		assert notAfter != null;

		this.notAfter = notAfter;
	}

	/**
	 * Get the validity end date.
	 *
	 * @return The validity end date.
	 */
	public Date getNotAfter() {
		return this.notAfter;
	}

	/**
	 * Add an extension object.
	 *
	 * @param extension The extension object to add.
	 */
	public void addExtension(X509ExtensionData extension) {
		assert extension != null;

		this.extensions.add(extension);
	}

	/**
	 * Get the extension objects.
	 * 
	 * @return The extension objects.
	 */
	public List<X509ExtensionData> getExtensions() {
		return Collections.unmodifiableList(this.extensions);
	}

}
