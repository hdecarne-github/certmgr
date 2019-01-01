/*
 * Copyright (c) 2015-2019 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.certs.x509;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import de.carne.certmgr.certs.security.KeyPairAlgorithm;

/**
 * Container for certificate content parameters.
 */
public class CertParams {

	private final X500Principal dn;
	private final KeyPairAlgorithm keyPairAlgorithm;
	private final int keySize;
	private List<X509ExtensionData> extensions = new ArrayList<>();

	/**
	 * Construct {@code CertParams}.
	 *
	 * @param dn The Distinguished Name (DN) to use for generation.
	 * @param keyPairAlgorithm The key pair algorithm to use for generation.
	 * @param keySize The key size to use for generation.
	 */
	public CertParams(X500Principal dn, KeyPairAlgorithm keyPairAlgorithm, int keySize) {
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
	 * Add an extension object.
	 *
	 * @param extension The extension object to add.
	 */
	public void addExtension(X509ExtensionData extension) {
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
