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
package de.carne.certmgr.jfx.crtoptions;

import java.util.ArrayList;
import java.util.Collection;

import de.carne.certmgr.store.CertStoreEntry;
import de.carne.certmgr.store.x509.EncodedX509Extension;
import de.carne.certmgr.store.x509.X509Extension;

/**
 * A preset backed up by an existing store entry.
 */
class StorePreset extends Preset {

	/**
	 * Construct StorePreset.
	 *
	 * @param name The preset's name.
	 * @param subjectDN The preset's subject DN.
	 * @param issuer The preset's issuer.
	 * @param extensions The preset's extensions.
	 */
	public StorePreset(String name, String subjectDN, CertStoreEntry issuer, Collection<EncodedX509Extension> extensions) {
		super(name, subjectDN, issuer, decodeExtensions(extensions));
	}

	private static Collection<X509Extension> decodeExtensions(Collection<EncodedX509Extension> encoded) {
		ArrayList<X509Extension> decoded = new ArrayList<>(encoded.size());

		for (EncodedX509Extension extension : encoded) {
			decoded.add(extension.getDecoded());
		}
		return decoded;
	}

}
