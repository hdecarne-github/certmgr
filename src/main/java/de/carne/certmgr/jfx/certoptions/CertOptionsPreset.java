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
package de.carne.certmgr.jfx.certoptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.x509.X509ExtensionData;

class CertOptionsPreset {

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

	CertOptionsPreset(CertOptionsPreset preset) {
		this(preset.aliasInput, preset.dnInput);
		this.keyAlg = preset.keyAlg;
		this.keySize = preset.keySize;
		this.extensions.addAll(preset.extensions);
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
