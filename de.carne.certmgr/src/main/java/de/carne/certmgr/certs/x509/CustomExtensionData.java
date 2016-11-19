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
package de.carne.certmgr.certs.x509;

/**
 * Custom Extension data.
 * <p>
 * This class allows the handling of unknown extension objects in a generic way.
 */
public class CustomExtensionData extends X509ExtensionData {

	private byte[] encoded;

	/**
	 * Construct {@code CustomExtensionData}.
	 *
	 * @param oid The extension OID.
	 * @param critical The extension's critical flag.
	 * @param encoded The encoded extension data.
	 */
	public CustomExtensionData(String oid, boolean critical, byte[] encoded) {
		super(oid, critical);

		assert encoded != null;

		this.encoded = encoded;
	}

	/**
	 * Get this extension's encoded data.
	 *
	 * @return This extension's encoded data.
	 */
	public byte[] getEncoded() {
		return this.encoded;
	}

	/**
	 * Set this extension's encoded data.
	 *
	 * @param encoded The data to set.
	 */
	public void setEncoded(byte[] encoded) {
		assert encoded != null;

		this.encoded = encoded;
	}

	@Override
	public Attributes toAttributes() {
		Attributes extensionAttributes = super.toAttributes();

		extensionAttributes.add(AttributesI18N.formatSTR_EXTENSION_DATA(this.encoded.length),
				Attributes.printBytes(this.encoded, 8));
		return extensionAttributes;
	}

}
