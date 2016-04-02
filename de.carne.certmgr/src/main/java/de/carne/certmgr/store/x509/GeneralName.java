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
package de.carne.certmgr.store.x509;

import java.io.IOException;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import de.carne.certmgr.store.asn1.ASN1Decoder;
import de.carne.certmgr.store.asn1.ASN1Encodable;
import de.carne.certmgr.store.asn1.ASN1Encoder;
import de.carne.certmgr.store.asn1.TaggedObject;

/**
 * General name object.
 */
public class GeneralName implements ASN1Encodable {

	private GeneralNameType type;
	private String nameString;
	private Object nameObject;

	/**
	 * Construct GeneralName.
	 *
	 * @param type The name type.
	 * @param name The name's string representation.
	 * @throws IllegalArgumentException if the type/name combination is not valid.
	 */
	public GeneralName(GeneralNameType type, String name) throws IllegalArgumentException {
		this.type = type;
		this.nameString = name;
		this.nameObject = decodeName(this.type, this.nameString);
	}

	/**
	 * Get the name type.
	 *
	 * @return The name type.
	 */
	public GeneralNameType getType() {
		return this.type;
	}

	/**
	 * Get the name.
	 *
	 * @return The name.
	 */
	public Object getName() {
		return this.nameObject;
	}

	/**
	 * Get the name's string representation.
	 *
	 * @return The name's string representation.
	 */
	public String getNameString() {
		return this.nameString;
	}

	private static Object decodeName(GeneralNameType type, String nameString) throws IllegalArgumentException {
		Object decoded;

		switch (type) {
		case OTHER_NAME:
			throw new IllegalArgumentException("Unsupported name type: " + type);
			// break;
		case RFC822_NAME:
		case DNS_NAME:
		case UNIFORM_RESOURCE_IDENTIFIER:
			decoded = nameString;
			break;
		case X400_ADDRESS:
			throw new IllegalArgumentException("Unsupported name type: " + type);
			// break;
		case DIRECTORY_NAME:
			try {
				decoded = new LdapName(nameString);
			} catch (InvalidNameException e) {
				throw new IllegalArgumentException("Unable to decode type/name: " + type + " " + nameString, e);
			}
		case EDI_PARTY_NAME:
			throw new IllegalArgumentException("Unsupported name type: " + type);
			// break;
		case IP_ADDRESS:
			throw new IllegalArgumentException("Unsupported name type: " + type);
			// break;
		case REGISTERED_ID:
			decoded = nameString;
			break;
		default:
			throw new IllegalArgumentException("Unsupported name type: " + type);
		}
		return decoded;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Encodable#asn1Encode(de.carne.certmgr.store.asn1.ASN1Encoder)
	 */
	@Override
	public void asn1Encode(ASN1Encoder encoder) {
		switch (this.type) {
		case OTHER_NAME:
			break;
		case RFC822_NAME:
		case DNS_NAME:
		case UNIFORM_RESOURCE_IDENTIFIER:
			encoder.asn1EncodeTaggedObject(false, this.type.getTagNo(), new ASN1Encodable() {

				@Override
				public void asn1Encode(ASN1Encoder encoder2) {
					asn1EncodeAsciiName(encoder2);
				}

			});
			break;
		case X400_ADDRESS:
			break;
		case DIRECTORY_NAME:
			break;
		case EDI_PARTY_NAME:
			break;
		case IP_ADDRESS:
			break;
		case REGISTERED_ID:
			encoder.asn1EncodeTaggedObject(false, this.type.getTagNo(), new ASN1Encodable() {

				@Override
				public void asn1Encode(ASN1Encoder encoder2) {
					asn1EncodeOID(encoder2);
				}

			});
			break;
		default:
			throw new IllegalStateException("Unsupported name type: " + this.type);
		}
	}

	void asn1EncodeAsciiName(ASN1Encoder encoder) {
		encoder.asn1EncodeAsciiString((String) this.nameObject);
	}

	void asn1EncodeOID(ASN1Encoder encoder) {
		encoder.asn1EncodeOID((String) this.nameObject);
	}

	/**
	 * Decode general name.
	 *
	 * @param decoder The decoder to use.
	 * @return The decoded object.
	 * @throws IOException if the decoding fails.
	 */
	public static GeneralName asn1Decode(ASN1Decoder decoder) throws IOException {
		TaggedObject<ASN1Decoder> taggedObject = decoder.asn1EncodeTaggedObject(GeneralNameType.getTagNoValues());
		int tagNo = taggedObject.getTagNo();
		ASN1Decoder object = taggedObject.getObject();
		GeneralNameType type = GeneralNameType.fromTagNo(tagNo);

		if (type == null) {
			throw new IllegalStateException("Unexpected tag no: " + tagNo);
		}

		String name;

		switch (type) {
		case OTHER_NAME:
			throw new IOException("Unsupported GeneralNameType: " + type);
			// break;
		case RFC822_NAME:
		case DNS_NAME:
		case UNIFORM_RESOURCE_IDENTIFIER:
			name = object.asn1EncodeAsciiString();
			break;
		case X400_ADDRESS:
			throw new IOException("Unsupported GeneralNameType: " + type);
			// break;
		case DIRECTORY_NAME:
		case EDI_PARTY_NAME:
			throw new IOException("Unsupported GeneralNameType: " + type);
			// break;
		case IP_ADDRESS:
			throw new IOException("Unsupported GeneralNameType: " + type);
			// break;
		case REGISTERED_ID:
			name = object.asn1DecodeOID();
			break;
		default:
			throw new IOException("Unsupported GeneralNameType: " + type);
		}
		return new GeneralName(type, name);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.type + "=" + this.nameObject;
	}

}
