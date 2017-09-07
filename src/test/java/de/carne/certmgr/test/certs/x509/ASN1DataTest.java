/*
 * Copyright (c) 2015-2017 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.test.certs.x509;

import java.io.IOException;
import java.net.InetAddress;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.junit.Assert;
import org.junit.Test;

import de.carne.certmgr.certs.asn1.ASN1Data;
import de.carne.certmgr.certs.x509.DirectoryName;
import de.carne.certmgr.certs.x509.DistributionPoint;
import de.carne.certmgr.certs.x509.DistributionPointName;
import de.carne.certmgr.certs.x509.GeneralNameType;
import de.carne.certmgr.certs.x509.GeneralNames;
import de.carne.certmgr.certs.x509.IPAddressName;
import de.carne.certmgr.certs.x509.StringName;

/**
 * Test encoding & decoding of {@link ASN1Data} objects.
 */
public class ASN1DataTest {

	/**
	 * Test encoding & decoding of {@link DistributionPoint} object.
	 */
	@Test
	public void testGeneralNames() {
		try {
			GeneralNames in = new GeneralNames();
			StringName inNameA = new StringName(GeneralNameType.UNIFORM_RESOURCE_IDENTIFIER,
					"https://localhost/test.crl");
			DirectoryName inNameB = new DirectoryName(new X500Principal("CN=localhost"));
			IPAddressName inNameC = new IPAddressName(InetAddress.getByName("127.0.0.1"),
					InetAddress.getByName("255.255.255.255"));

			in.addName(inNameA);
			in.addName(inNameB);
			in.addName(inNameC);

			byte[] inEncoded = in.encode().toASN1Primitive().getEncoded();
			GeneralNames out = GeneralNames.decode(decodeBytes(inEncoded));
			byte[] outEncoded = out.encode().toASN1Primitive().getEncoded();

			Assert.assertArrayEquals(inEncoded, outEncoded);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	/**
	 * Test encoding & decoding of {@link DistributionPoint} object.
	 */
	@Test
	public void testDistributionPoint() {
		try {
			// DistributionPointName based
			GeneralNames in1FullName = new GeneralNames();
			StringName in1NameA = new StringName(GeneralNameType.UNIFORM_RESOURCE_IDENTIFIER,
					"https://localhost/test.crl");
			DirectoryName in1NameB = new DirectoryName(new X500Principal("CN=localhost"));

			in1FullName.addName(in1NameA);
			in1FullName.addName(in1NameB);

			DistributionPointName in1Name = new DistributionPointName(in1FullName);
			DistributionPoint in1 = new DistributionPoint(in1Name);
			byte[] in1Encoded = in1.encode().toASN1Primitive().getEncoded();
			DistributionPoint out1 = DistributionPoint.decode(decodeBytes(in1Encoded));
			byte[] out1Encoded = out1.encode().toASN1Primitive().getEncoded();

			Assert.assertArrayEquals(in1Encoded, out1Encoded);

			// GeneralName based
			GeneralNames in2CrlIssuers = new GeneralNames();
			StringName in2NameA = new StringName(GeneralNameType.UNIFORM_RESOURCE_IDENTIFIER,
					"https://localhost/test.crl");
			DirectoryName in2NameB = new DirectoryName(new X500Principal("CN=localhost"));

			in1FullName.addName(in2NameA);
			in1FullName.addName(in2NameB);

			DistributionPoint in2 = new DistributionPoint(in2CrlIssuers);
			byte[] in2Encoded = in2.encode().toASN1Primitive().getEncoded();
			DistributionPoint out2 = DistributionPoint.decode(decodeBytes(in2Encoded));
			byte[] out2Encoded = out2.encode().toASN1Primitive().getEncoded();

			Assert.assertArrayEquals(in2Encoded, out2Encoded);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	private ASN1Primitive decodeBytes(byte[] data) throws IOException {
		ASN1Primitive decoded;

		try (ASN1InputStream input = new ASN1InputStream(data)) {
			decoded = input.readObject();
		}
		return decoded;
	}

}
