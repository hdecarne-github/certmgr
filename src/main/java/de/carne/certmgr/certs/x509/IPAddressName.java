/*
 * Copyright (c) 2015-2018 Holger de Carne and contributors, All Rights Reserved.
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
import java.net.InetAddress;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;
import org.eclipse.jdt.annotation.Nullable;

/**
 * General name of type IP Address.
 */
public class IPAddressName extends GeneralName {

	private final InetAddress address;
	@Nullable
	private final InetAddress netmask;

	/**
	 * Construct {@code IPAddressName}.
	 *
	 * @param address The name's address.
	 * @param netmask The name's network mask (may be {@code null}).
	 */
	public IPAddressName(InetAddress address, @Nullable InetAddress netmask) {
		super(GeneralNameType.IP_ADDRESS);
		this.address = address;
		this.netmask = netmask;
	}

	/**
	 * Decode {@code IPAddressName} from an ASN.1 data object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @return The decoded IP address name object.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static IPAddressName decode(ASN1Primitive primitive) throws IOException {
		ASN1Primitive object = decodeTagged(primitive, GeneralNameType.IP_ADDRESS_TAG);
		byte[] octets = decodePrimitive(object, ASN1OctetString.class).getOctets();
		InetAddress address;
		InetAddress netmask;

		switch (octets.length) {
		case 4:
			address = InetAddress.getByAddress(octets);
			netmask = null;
			break;
		case 8:
			address = InetAddress.getByAddress(Arrays.copyOfRange(octets, 0, 4));
			netmask = InetAddress.getByAddress(Arrays.copyOfRange(octets, 4, 8));
			break;
		case 16:
			address = InetAddress.getByAddress(octets);
			netmask = null;
			break;
		case 32:
			address = InetAddress.getByAddress(Arrays.copyOfRange(octets, 0, 16));
			netmask = InetAddress.getByAddress(Arrays.copyOfRange(octets, 16, 32));
			break;
		default:
			throw new IOException("Unexpected data length: " + octets.length);
		}
		return new IPAddressName(address, netmask);
	}

	@Override
	public ASN1Encodable encode() throws IOException {
		byte[] addressBytes = this.address.getAddress();
		byte[] netmaskBytes = (this.netmask != null ? this.netmask.getAddress() : new byte[0]);
		byte[] encodedBytes = new byte[addressBytes.length + netmaskBytes.length];

		System.arraycopy(addressBytes, 0, encodedBytes, 0, addressBytes.length);
		System.arraycopy(netmaskBytes, 0, encodedBytes, addressBytes.length, netmaskBytes.length);
		return new DERTaggedObject(false, getType().value(), new DEROctetString(encodedBytes));
	}

	@Override
	public String toValueString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append(this.address.getHostAddress());

		InetAddress checkedNetmask = this.netmask;

		if (checkedNetmask != null) {
			buffer.append('/').append(checkedNetmask.getHostAddress());
		}
		return buffer.toString();
	}

	/**
	 * Get this name's address.
	 *
	 * @return This name's address.
	 */
	public InetAddress getAddress() {
		return this.address;
	}

	/**
	 * Get this name's network mask.
	 *
	 * @return This name's network mask (may be {@code null})..
	 */
	@Nullable
	public InetAddress getNetmask() {
		return this.netmask;
	}

}
