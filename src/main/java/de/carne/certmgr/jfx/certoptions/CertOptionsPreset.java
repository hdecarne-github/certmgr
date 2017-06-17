/*
 * Copyright (c) 2015-2017 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.jfx.certoptions;

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.x500.X500Names;
import de.carne.certmgr.certs.x509.AuthorityKeyIdentifierExtensionData;
import de.carne.certmgr.certs.x509.KeyHelper;
import de.carne.certmgr.certs.x509.SubjectKeyIdentifierExtensionData;
import de.carne.certmgr.certs.x509.X509ExtensionData;
import de.carne.check.Nullable;
import de.carne.util.Exceptions;

class CertOptionsPreset {

	private static final Set<String> INVALID_PRESET_EXTENSIONS = new HashSet<>();

	static {
		INVALID_PRESET_EXTENSIONS.add(SubjectKeyIdentifierExtensionData.OID);
		INVALID_PRESET_EXTENSIONS.add(AuthorityKeyIdentifierExtensionData.OID);
	}

	private final String aliasInput;

	private final String dnInput;

	@Nullable
	private KeyPairAlgorithm keyAlg = null;

	@Nullable
	private Integer keySize = null;

	private final List<X509ExtensionData> extensions = new ArrayList<>();

	CertOptionsPreset(String aliasInput, String dnInput) {
		this.aliasInput = aliasInput;
		this.dnInput = dnInput;
	}

	CertOptionsPreset(String aliasInput, UserCertStoreEntry storeEntry) {
		this(aliasInput, X500Names.toString(storeEntry.dn()));
		try {
			if (storeEntry.hasCRT()) {
				X509Certificate crt = storeEntry.getCRT();
				PublicKey publicKey = crt.getPublicKey();

				this.keyAlg = KeyHelper.getKeyAlg(publicKey);
				this.keySize = KeyHelper.getKeySize(publicKey);

				Set<String> criticalExtensionOIDs = crt.getCriticalExtensionOIDs();

				if (criticalExtensionOIDs != null) {
					for (String criticalExtensionOID : criticalExtensionOIDs) {
						if (!INVALID_PRESET_EXTENSIONS.contains(criticalExtensionOID)) {
							X509ExtensionData criticalExtension = X509ExtensionData.decode(criticalExtensionOID, true,
									crt.getExtensionValue(criticalExtensionOID));

							this.extensions.add(criticalExtension);
						}
					}
				}

				Set<String> nonCriticalExtensionOIDs = crt.getNonCriticalExtensionOIDs();

				if (nonCriticalExtensionOIDs != null) {
					for (String nonCriticalExtensionOID : nonCriticalExtensionOIDs) {
						if (!INVALID_PRESET_EXTENSIONS.contains(nonCriticalExtensionOID)) {
							X509ExtensionData nonCriticalExtension = X509ExtensionData.decode(nonCriticalExtensionOID,
									false, crt.getExtensionValue(nonCriticalExtensionOID));

							this.extensions.add(nonCriticalExtension);
						}
					}
				}
			}
		} catch (IOException e) {
			Exceptions.warn(e);
		}
	}

	public String aliasInput() {
		return this.aliasInput;
	}

	public String dnInput() {
		return this.dnInput;
	}

	public void setKeyAlg(@Nullable KeyPairAlgorithm keyAlg) {
		this.keyAlg = keyAlg;
	}

	@Nullable
	public KeyPairAlgorithm getKeyAlg() {
		return this.keyAlg;
	}

	public void setKeySize(@Nullable Integer keySize) {
		this.keySize = keySize;
	}

	@Nullable
	public Integer getKeySize() {
		return this.keySize;
	}

	public void addExtension(X509ExtensionData extension) {
		this.extensions.add(extension);
	}

	public List<X509ExtensionData> getExtensions() {
		return Collections.unmodifiableList(this.extensions);
	}

}
