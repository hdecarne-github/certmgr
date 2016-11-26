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

import java.io.IOException;
import java.net.InetAddress;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;

/**
 * General name of type IP Address.
 */
public class IPAddressName extends GeneralName {

	private final InetAddress address;
	private final InetAddress netmask;

	/**
	 * Construct {@code IPAddressName}.
	 *
	 * @param address The name's address.
	 * @param netmask The name's network mask. May be {@code null}.
	 */
	public IPAddressName(InetAddress address, InetAddress netmask) {
		super(GeneralNameType.IP_ADDRESS);

		assert address != null;

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
		byte[] octets = decodePrimitive(primitive, ASN1OctetString.class).getOctets();
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
	public String toValueString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append(this.address.getHostAddress());
		if (this.netmask != null) {
			buffer.append('/').append(this.netmask.getHostAddress());
		}
		return buffer.toString();
	}

}
