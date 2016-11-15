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
package de.carne.certmgr.test.certs;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Security;
import java.util.Set;
import java.util.prefs.BackingStoreException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.UserCertStorePreferences;
import de.carne.certmgr.certs.net.SSLPeer.Protocol;
import de.carne.io.IOHelper;

/**
 * Test {@link UserCertStore} class functionality.
 */
public class UserCertStoreTest {

	private static Path tempPath = null;

	/**
	 * Register BouncyCastle Provider.
	 */
	@BeforeClass
	public static void registerBouncyCastle() {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Setup temporary directory for all tests.
	 *
	 * @throws IOException
	 */
	@BeforeClass
	public static void setupTempPath() throws IOException {
		tempPath = Files.createTempDirectory(UserCertStoreTest.class.getSimpleName());
		System.out.println("Using temporary directory: " + tempPath);
	}

	/**
	 * Clean up temporary directory be deleting it including all content.
	 *
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void deleteTempPath() throws Exception {
		IOHelper.deleteDirectoryTree(tempPath);
	}

	private static final String NAME_STORE1 = "store1";

	/**
	 * Test create/open/access store operations.
	 */
	@Test
	public void testCreateAndOpenStore() {
		Path storeHome = tempPath.resolve(NAME_STORE1);

		try {
			UserCertStore createdStore = UserCertStore.createStore(storeHome);

			Assert.assertEquals(0, createdStore.size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		try {
			UserCertStore.createStore(storeHome);
			Assert.fail("Re-creating store succeeded, but should not");
		} catch (FileAlreadyExistsException e) {
			// Expected
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		try {
			UserCertStore openendStore = UserCertStore.openStore(storeHome);

			Assert.assertEquals(0, openendStore.size());

			UserCertStorePreferences initPreferences = openendStore.storePreferences();

			initPreferences.defaultCRTValidityPeriod.putInt(365);
			initPreferences.defaultCRLUpdatePeriod.putInt(30);
			initPreferences.defaultKeyPairAlgorithm.put("RSA");
			initPreferences.defaultKeySize.putInt(4096);
			initPreferences.defaultSignatureAlgorithm.put("SHA256WITHRSA");
			initPreferences.sync();

			UserCertStorePreferences loadPreferences = openendStore.storePreferences();

			Assert.assertEquals(Integer.valueOf(365), loadPreferences.defaultCRTValidityPeriod.get());
			Assert.assertEquals(Integer.valueOf(30), loadPreferences.defaultCRLUpdatePeriod.get());
			Assert.assertEquals("RSA", loadPreferences.defaultKeyPairAlgorithm.get());
			Assert.assertEquals(Integer.valueOf(4096), loadPreferences.defaultKeySize.get());
			Assert.assertEquals("SHA256WITHRSA", loadPreferences.defaultSignatureAlgorithm.get());
			Assert.assertEquals(NAME_STORE1, openendStore.storeName());
			Assert.assertEquals(0, openendStore.getEntries().size());
			Assert.assertEquals(0, traverStore(openendStore.getRootEntries()));
		} catch (IOException | BackingStoreException e) {
			Assert.fail(e.getMessage());
		}
	}

	private int traverStore(Set<UserCertStoreEntry> entries) {
		int entryCount = 0;

		for (UserCertStoreEntry entry : entries) {
			entryCount = 1 + traverStore(entry.issuedEntries());
		}
		return entryCount;
	}

	private final static String TEST_SSL_HOST = "google.com";
	private final static int TEST_SSL_PORT = 443;
	private final static String TEST_SMTP_HOST = "smtp.gmail.com";
	private final static int TEST_SMTP_PORT = 587;
	private final static String TEST_IMAP_HOST = "imap.1und1.de";
	private final static int TEST_IMAP_PORT = 143;

	/**
	 * Test store creation from server source.
	 */
	@Test
	public void testServerSourceStore() {
		try {
			UserCertStore importStore = UserCertStore.createFromServer(Protocol.SSL, TEST_SSL_HOST, TEST_SSL_PORT);

			Assert.assertNotNull(importStore);
			Assert.assertTrue(importStore.size() > 0);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		try {
			UserCertStore importStore = UserCertStore.createFromServer(Protocol.STARTTLS_SMTP, TEST_SMTP_HOST,
					TEST_SMTP_PORT);

			Assert.assertNotNull(importStore);
			Assert.assertTrue(importStore.size() > 0);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		try {
			UserCertStore importStore = UserCertStore.createFromServer(Protocol.STARTTLS_IMAP, TEST_IMAP_HOST,
					TEST_IMAP_PORT);

			Assert.assertNotNull(importStore);
			Assert.assertTrue(importStore.size() > 0);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		try {
			UserCertStore importStore = UserCertStore.createFromServer(Protocol.STARTTLS_SMTP, TEST_SSL_HOST,
					TEST_SSL_PORT);

			Assert.assertNull(importStore);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Test store creation from URL source.
	 */
	@Test
	public void testURLSourceStore() {
		try {
			UserCertStore importStore = UserCertStore.createFromURL(TestCerts.pkcs12InputURL(), TestCerts.password());

			Assert.assertNotNull(importStore);
			Assert.assertTrue(importStore.size() > 0);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

}
