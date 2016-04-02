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

/**
 * Supported file formats for certificate data exchange.
 */
public enum CertFileFormat {

	/**
	 * PEM file format.
	 */
	PEM(".pem", ".key", ".crt", ".csr", ".crl"),

	/**
	 * PKCSS#12 file format.
	 */
	PKCS12(".p12", ".pfx");

	private String[] extensions;

	private CertFileFormat(String... extensions) {
		this.extensions = extensions;
	}

	/**
	 * Get known file format extensions.
	 *
	 * @return Known file format extensions.
	 */
	public String[] getExtensions() {
		return this.extensions;
	}

	/**
	 * Get standard file format extension.
	 *
	 * @return Standard file format extension.
	 */
	public String getExtension() {
		return this.extensions[0];
	}

}
