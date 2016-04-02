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

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

/**
 * Interface providing access to PKCS#10 based certificate signing requests (CSRs).
 * <p>
 * As the JDK does not provide a standard CSR class this is interface is used to wrap the provider specific CSR classes.
 * </p>
 */
public interface PKCS10Object {

	/**
	 * Get the CSR's signature algorithm name.
	 *
	 * @return The CSR's signature algorithm name.
	 */
	public String getSigAlgName();

	/**
	 * Get the CSR's subject.
	 *
	 * @return The CSR's subject.
	 */
	public X500Principal getSubjectX500Principal();

	/**
	 * Get the CSR's public key.
	 *
	 * @return The CSR's public key.
	 */
	public PublicKey getPublicKey();

	/**
	 * Verify whether this CSR was signed by the private key corresponding to the submitted public key.
	 *
	 * @param publicKey The public key to check.
	 * @throws NoSuchAlgorithmException if an unsupported signature algorithm is encountered.
	 * @throws NoSuchProviderException if no provider is available.
	 * @throws InvalidKeyException in case of an incorrect key.
	 * @throws SignatureException in case of an signature error.
	 */
	public void verify(PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchProviderException,
			InvalidKeyException, SignatureException;

	/**
	 * Get the CSR's attribute OIDs.
	 *
	 * @return The CSR's attribute OIDs.
	 */
	public Set<String> getAttributeOIDs();

	/**
	 * Get the attribute data.
	 *
	 * @param oid The OID of the attribute to retrieve.
	 * @return The attribute data.
	 * @throws IOException in case of an I/O error.
	 */
	public byte[][] getAttributeValues(String oid) throws IOException;

	/**
	 * Get the requested extension OIDs contained in the CSR.
	 * 
	 * @return The requested extension OIDs.
	 * @throws IOException in case of an I/O error.
	 */
	public Set<String> getExtensionOIDs() throws IOException;

	/**
	 * Get the requested extension data contained in the CSR.
	 * 
	 * @param oid The extension OID to retrieve the data for.
	 * @return The extension data or null if the OID is not part of the CSR.
	 * @throws IOException in case of an I/O error.
	 */
	public byte[] getExtensionValue(String oid) throws IOException;

	/**
	 * Get the provider specific CSR object.
	 *
	 * @return The provider specific CSR object.
	 */
	public Object getObject();

}
