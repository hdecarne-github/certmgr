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
package de.carne.certmgr.test.certs.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Security;
import java.util.Arrays;

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
		for (CertReader reader : CertReaders.REGISTERED.providers()) {
			for (int resourceIndex = 1;; resourceIndex++) {
				URL testResourceURL = getClass().getResource(reader.providerName() + "." + resourceIndex + ".dat");

				if (testResourceURL == null) {
					break;
				}

				Path testPath = Files.createTempFile(getClass().getSimpleName(), null);

				try {
					testReaderAndWriter(reader, testResourceURL, testPath);
				} finally {
					Files.delete(testPath);
				}
			}
		}
	}

	private void testReaderAndWriter(CertReader reader, URL testResourceURL, Path testPath) throws IOException {
		System.out.println("Testing I/O provider: " + reader.providerName());

		CertWriter writer = CertWriters.REGISTERED.get(reader.providerName());

		System.out.println(reader.fileType());
		System.out.println(Arrays.toString(reader.fileExtensionPatterns()));
		if (writer != null) {
			System.out.println("isCharWriter: " + writer.isCharWriter());
			System.out.println("isEncryptionRequired: " + writer.isEncryptionRequired());
		}

		CertObjectStore readCertObjects1 = CertReaders.readURL(testResourceURL, Tests.password());

		Assert.assertNotNull(readCertObjects1);

		for (CertObjectStore.Entry entry : readCertObjects1) {
			switch (entry.type()) {
			case CRT:
				reader.fileExtension(entry.getCRT().getClass());
				break;
			case KEY:
				reader.fileExtension(entry.getKey().getClass());
				break;
			case CSR:
				reader.fileExtension(entry.getCSR().getClass());
				break;
			case CRL:
				reader.fileExtension(entry.getCRL().getClass());
				break;
			}
			reader.fileExtension(getClass());
		}

		CertObjectStore readCertObjects2;

		try (IOResource<InputStream> in = new IOResource<>(testResourceURL.openStream(), reader.providerName())) {
			readCertObjects2 = reader.readBinary(in, Tests.password());

			Assert.assertNotNull(readCertObjects2);
			Assert.assertEquals(readCertObjects1.size(), readCertObjects2.size());
		}
		if (writer != null) {
			if (!writer.isEncryptionRequired()) {
				try (IOResource<OutputStream> out = IOResource.newOutputStream(writer.providerName(), testPath)) {
					writer.writeBinary(out, readCertObjects2);
				}
				verifyWriterOutput(readCertObjects2, reader, testPath);
				if (writer.isCharWriter()) {
					try (IOResource<Writer> out = new IOResource<>(Files.newBufferedWriter(testPath),
							writer.providerName())) {
						writer.writeString(out, readCertObjects2);
					}
					verifyWriterOutput(readCertObjects2, reader, testPath);
				}
			}
			try (IOResource<OutputStream> out = IOResource.newOutputStream(writer.providerName(), testPath)) {
				writer.writeEncryptedBinary(out, readCertObjects2, Tests.password());
			}
			if (writer.isCharWriter()) {
				try (IOResource<Writer> out = new IOResource<>(Files.newBufferedWriter(testPath),
						writer.providerName())) {
					writer.writeEncryptedString(out, readCertObjects2, Tests.password());
				}
				verifyWriterOutput(readCertObjects2, reader, testPath);
			}
			verifyWriterOutput(readCertObjects2, reader, testPath);
		}
		System.out.println();
	}

	private static void verifyWriterOutput(CertObjectStore readCertObjects, CertReader reader, Path testPath)
			throws IOException {
		try (IOResource<InputStream> in = IOResource.newInputStream(testPath.toString(), testPath)) {
			CertObjectStore readCertObjects2 = reader.readBinary(in, Tests.password());

			Assert.assertNotNull(readCertObjects2);
			Assert.assertEquals(readCertObjects.size(), readCertObjects2.size());
		}
	}

}
