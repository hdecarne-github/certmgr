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
package de.carne.certmgr.certs;

import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;

/**
 * The certificate object types.
 */
public enum CertObjectType {

	/**
	 * Certificate object of type CRT ({@link X509Certificate}).
	 */
	CRT,

	/**
	 * Certificate object of type Key ({@link KeyPair}).
	 */
	KEY,

	/**
	 * Certificate object of type CSR ({@link PKCS10CertificateRequest}).
	 */
	CSR,

	/**
	 * Certificate object of type CRL ({@link X509CRL}).
	 */
	CRL

}
