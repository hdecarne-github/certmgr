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

import java.io.IOException;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;

import de.carne.certmgr.certs.x500.X500Names;

/**
 * General name of type Directory.
 */
public class DirectoryName extends GeneralName {

	private final X500Principal name;

	/**
	 * Construct {@code DirectoryName}.
	 *
	 * @param name The X.500 name represented by this instance.
	 */
	public DirectoryName(X500Principal name) {
		super(GeneralNameType.DIRECTORY_NAME);
		this.name = name;
	}

	/**
	 * Decode {@code DirectoryName} from an ASN.1 data object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @return The decoded directory name object.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static DirectoryName decode(ASN1Primitive primitive) throws IOException {
		ASN1Primitive object = decodeTagged(primitive, GeneralNameType.DIRECTORY_NAME_TAG);

		return new DirectoryName(new X500Principal(object.getEncoded()));
	}

	@Override
	public ASN1Encodable encode() throws IOException {
		return new DERTaggedObject(true, getType().value(), new X500Name(this.name.getName()));
	}

	@Override
	public String toValueString() {
		return X500Names.toString(this.name);
	}

	/**
	 * Get the {@link X500Principal} represented by this name.
	 *
	 * @return The {@link X500Principal} represented by this name.
	 */
	public X500Principal getDirectoryName() {
		return this.name;
	}

}
