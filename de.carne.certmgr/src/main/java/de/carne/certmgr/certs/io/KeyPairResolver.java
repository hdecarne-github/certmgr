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
package de.carne.certmgr.certs.io;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class KeyPairResolver<T> {

	private static class KeyPairHolder {

		public PublicKey publicKey;

		public PrivateKey privateKey;

		KeyPairHolder(PublicKey publicKey, PrivateKey privateKey) {
			this.publicKey = publicKey;
			this.privateKey = privateKey;
		}

		public void setPublicKey(PublicKey publicKey) {
			this.publicKey = publicKey;
		}

		public void setPrivateKey(PrivateKey privateKey) {
			this.privateKey = privateKey;
		}

		public KeyPair resolve() {
			return (this.publicKey != null && this.privateKey != null ? new KeyPair(this.publicKey, this.privateKey)
					: null);
		}

	}

	private final Map<T, KeyPairHolder> keyPairHolderMap = new HashMap<>();

	public void addPublicKey(T id, PublicKey publicKey) {
		KeyPairHolder keyPairHolder = this.keyPairHolderMap.get(id);

		if (keyPairHolder == null) {
			keyPairHolder = new KeyPairHolder(publicKey, null);
			this.keyPairHolderMap.put(id, keyPairHolder);
		} else {
			keyPairHolder.setPublicKey(publicKey);
		}
	}

	public void addPrivateKey(T id, PrivateKey privateKey) {
		KeyPairHolder keyPairHolder = this.keyPairHolderMap.get(id);

		if (keyPairHolder == null) {
			keyPairHolder = new KeyPairHolder(null, privateKey);
			this.keyPairHolderMap.put(id, keyPairHolder);
		} else {
			keyPairHolder.setPrivateKey(privateKey);
		}
	}

	public List<Object> resolve() {
		List<Object> keyPairObjects = new ArrayList<>();

		for (KeyPairHolder keyPairHolder : this.keyPairHolderMap.values()) {
			KeyPair keyPair = keyPairHolder.resolve();

			if (keyPair != null) {
				keyPairObjects.add(keyPair);
			}
		}
		return keyPairObjects;
	}

}
