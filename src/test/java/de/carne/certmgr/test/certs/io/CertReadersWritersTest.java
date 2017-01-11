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
package de.carne.certmgr.test.certs.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Security;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.carne.certmgr.certs.CertObjectStore;
import de.carne.certmgr.certs.io.CertReaders;
import de.carne.certmgr.certs.io.CertWriters;
import de.carne.certmgr.certs.io.IOResource;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.certmgr.test.Tests;

/**
 * Test Certificate Readers and Writers.
 */
public class CertReadersWritersTest {

	/**
	 * Register BouncyCastle Provider.
	 */
	@BeforeClass
	public static void registerBouncyCastle() {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Test all Readers and Writers for the different object types.
	 *
	 * @throws IOException
	 */
	@Test
	public void testReadersAndWriters() throws IOException {
		// List<String> testResources = Arrays.asList("test.crt", "test.key",
		// "test.csr", "test.crl", "test.pem");
		List<String> testResources = Arrays.asList("test.crt", "test.pem");

		for (String testResource : testResources) {
			URL testResourceURL = getClass().getResource(testResource);
			CertObjectStore certObjects = CertReaders.readURL(testResourceURL, Tests.password());

			Assert.assertNotNull(certObjects);
			Assert.assertTrue(certObjects.size() > 0);

			for (CertWriter writer : CertWriters.REGISTERED.providers()) {
				Path testPath = Files.createTempFile(getClass().getSimpleName(), null);

				try {
					testReaderAndWriter(writer, testPath, certObjects);
				} finally {
					Files.delete(testPath);
				}
			}
		}
	}

	private void testReaderAndWriter(CertWriter writer, Path testPath, CertObjectStore certObjects) throws IOException {
		System.out.println("Testing provider: " + writer.providerName());

		CertReader reader = CertReaders.REGISTERED.get(writer.providerName());

		System.out.println(reader != null ? "(Reading and Writing)" : "(Writing only)");
		System.out.println(writer.fileType());
		System.out.println(Arrays.toString(writer.fileExtensionPatterns()));
		System.out.println("isCharWriter: " + writer.isCharWriter());
		System.out.println("isEncryptionRequired: " + writer.isEncryptionRequired());

		boolean certObjectsWritten = false;

		try (IOResource<OutputStream> out = IOResource.newOutputStream(testPath.toString(), testPath)) {
			writer.writeBinary(out, certObjects);
		} catch (UnsupportedOperationException e) {
			Assert.assertTrue(writer.isEncryptionRequired());
		}
		try (IOResource<OutputStream> out = IOResource.newOutputStream(testPath.toString(), testPath)) {
			writer.writeEncryptedBinary(out, certObjects, Tests.password());
			certObjectsWritten = true;
		}

		if (reader != null && certObjectsWritten) {
			try (IOResource<InputStream> in = IOResource.newInputStream(testPath.toString(), testPath)) {
				CertObjectStore readCertObjects = reader.readBinary(in, Tests.password());

				// Assert.assertNotNull(readCertObjects);
				// Assert.assertEquals(certObjects.size(),
				// readCertObjects.size());
			}
		}
	}

}
