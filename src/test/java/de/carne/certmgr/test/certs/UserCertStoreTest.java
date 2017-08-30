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
package de.carne.certmgr.test.certs;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Security;
import java.security.cert.X509Extension;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.carne.certmgr.certs.NoPassword;
import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.UserCertStoreEntryId;
import de.carne.certmgr.certs.UserCertStorePreferences;
import de.carne.certmgr.certs.net.SSLPeer.Protocol;
import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.security.PlatformKeyStore;
import de.carne.certmgr.certs.spi.CertGenerator;
import de.carne.certmgr.certs.x500.X500Names;
import de.carne.certmgr.certs.x509.Attributes;
import de.carne.certmgr.certs.x509.BasicConstraintsExtensionData;
import de.carne.certmgr.certs.x509.GenerateCertRequest;
import de.carne.certmgr.certs.x509.X509CRLHelper;
import de.carne.certmgr.certs.x509.X509CertificateHelper;
import de.carne.certmgr.certs.x509.X509ExtensionData;
import de.carne.certmgr.certs.x509.generator.CertGenerators;
import de.carne.certmgr.certs.x509.generator.Issuer;
import de.carne.check.Check;
import de.carne.io.IOHelper;
import de.carne.util.DefaultSet;
import de.carne.util.Exceptions;
import de.carne.util.Late;

/**
 * Test {@link UserCertStore} class functionality.
 */
public class UserCertStoreTest {

	private static final Late<Path> tempPath = new Late<>();

	private static final Late<Path> testStorePath = new Late<>();

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
		tempPath.init(Files.createTempDirectory(UserCertStoreTest.class.getSimpleName()));
		System.out.println("Using temporary directory: " + tempPath);
		testStorePath.init(IOHelper.createTempDirFromZIPResource(TestCerts.testStoreZIPURL(), tempPath.get(), null)
				.resolve(TestCerts.TEST_STORE_NAME));
	}

	/**
	 * Clean up temporary directory be deleting it including all content.
	 *
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void deleteTempPath() throws Exception {
		IOHelper.deleteDirectoryTree(tempPath.get());
	}

	private static final String NAME_STORE1 = "store1";

	/**
	 * Test create/open store operations.
	 */
	@Test
	public void testCreateAndOpenStore() {
		Path storeHome = tempPath.get().resolve(NAME_STORE1);

		try {
			UserCertStore createdStore = UserCertStore.createStore(storeHome);
			GenerateCertRequest request1 = generateRequest(createdStore, CertGenerators.DEFAULT, true);
			UserCertStoreEntry generated1 = createdStore.generateEntry(CertGenerators.DEFAULT, request1,
					TestCerts.password(), TestCerts.password(), "TestCert");
			DefaultSet<Issuer> issuers1 = CertGenerators.DEFAULT.getIssuers(createdStore, generated1);
			GenerateCertRequest request2 = generateRequest(createdStore, CertGenerators.DEFAULT, false);

			request2.setIssuer(issuers1.getDefault());
			createdStore.generateEntry(CertGenerators.DEFAULT, request1, TestCerts.password(), TestCerts.password(),
					"TestCert");

			Assert.assertEquals(2, createdStore.size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		try {
			UserCertStore.createStore(storeHome);
			Assert.fail("Re-creating store succeeded, but should not");
		} catch (FileAlreadyExistsException e) {
			Exceptions.ignore(e);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		try {
			UserCertStore openendStore = UserCertStore.openStore(storeHome);

			Assert.assertEquals(2, openendStore.size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	private GenerateCertRequest basicRequest() {
		KeyPairAlgorithm keyPairAlgorithm = Check.nonNull(KeyPairAlgorithm.getDefaultSet(null, false).getDefault());
		GenerateCertRequest request = new GenerateCertRequest(X500Names.fromString("CN=TestCert"), keyPairAlgorithm,
				Check.nonNull(keyPairAlgorithm.getStandardKeySizes(null).getDefault()));

		Date notBefore = new Date();
		Date notAfter = new Date(notBefore.getTime() + 1000 * 60 * 24);

		request.setNotBefore(notBefore);
		request.setNotAfter(notAfter);
		return request;
	}

	private GenerateCertRequest generateRequest(UserCertStore store, CertGenerator generator, boolean ca) {
		GenerateCertRequest request = basicRequest();

		if (generator.hasFeature(CertGenerator.Feature.CUSTOM_ISSUER)) {
			request.setIssuer(generator.getIssuers(store, null).getDefault());
		}
		if (generator.hasFeature(CertGenerator.Feature.CUSTOM_SIGNATURE_ALGORITHM)) {
			request.setSignatureAlgorithm(Check.nonNull(
					generator.getSignatureAlgorithms(request.getIssuer(), request.keyPairAlgorithm(), null, false)
							.getDefault()));
		}
		request.addExtension(new BasicConstraintsExtensionData(false, ca, null));
		return request;
	}

	/**
	 * Test access store operations.
	 */
	@Test
	public void testAccessStore() {
		try {
			UserCertStore store = UserCertStore.openStore(testStorePath.get());

			Assert.assertEquals(12, store.size());
			Assert.assertEquals(TestCerts.TEST_STORE_NAME, store.storeName());
			Assert.assertEquals(12, store.getEntries().size());
			Assert.assertEquals(1, traverseStore(store.getRootEntries()));

			// Check preferences access
			UserCertStorePreferences loadPreferences = Check.nonNull(store.storePreferences());

			Assert.assertEquals(Integer.valueOf(365), loadPreferences.defaultCRTValidityPeriod.get());
			Assert.assertEquals(Integer.valueOf(30), loadPreferences.defaultCRLUpdatePeriod.get());
			Assert.assertEquals("EC", loadPreferences.defaultKeyPairAlgorithm.get());
			Assert.assertEquals(Integer.valueOf(521), loadPreferences.defaultKeySize.get());
			Assert.assertEquals("SHA256WITHECDSA", loadPreferences.defaultSignatureAlgorithm.get());

			UserCertStorePreferences setPreferences = Check.nonNull(store.storePreferences());

			setPreferences.defaultCRTValidityPeriod.putInt(180);
			setPreferences.defaultCRLUpdatePeriod.putInt(7);
			setPreferences.defaultKeyPairAlgorithm.put("EC");
			setPreferences.defaultKeySize.putInt(521);
			setPreferences.defaultSignatureAlgorithm.put("SHA256WITHECDSA");
			setPreferences.sync();

			UserCertStorePreferences getPreferences = Check.nonNull(store.storePreferences());

			Assert.assertEquals(Integer.valueOf(180), getPreferences.defaultCRTValidityPeriod.get());
			Assert.assertEquals(Integer.valueOf(7), getPreferences.defaultCRLUpdatePeriod.get());
			Assert.assertEquals("EC", getPreferences.defaultKeyPairAlgorithm.get());
			Assert.assertEquals(Integer.valueOf(521), getPreferences.defaultKeySize.get());
			Assert.assertEquals("SHA256WITHECDSA", getPreferences.defaultSignatureAlgorithm.get());

			// Import access (with already existing entries)
			UserCertStore importStore = UserCertStore
					.createFromFiles(IOHelper.collectDirectoryFiles(testStorePath.get()), TestCerts.password());

			for (UserCertStoreEntry importStoreEntry : importStore.getEntries()) {
				store.importEntry(importStoreEntry, TestCerts.password(), "Imported");
			}
			Assert.assertEquals(12, store.size());

			// Delete access
			List<UserCertStoreEntryId> deleteIds = new ArrayList<>();

			for (UserCertStoreEntry storeEntry : store.getEntries()) {
				deleteIds.add(storeEntry.id());
			}
			for (UserCertStoreEntryId deleteId : deleteIds) {
				store.deleteEntry(deleteId);
			}
			Assert.assertEquals(0, store.size());

			// Import access (now with empty store)
			for (UserCertStoreEntry importStoreEntry : importStore.getEntries()) {
				store.importEntry(importStoreEntry, TestCerts.password(), "Imported");
			}
			Assert.assertEquals(12, store.size());
		} catch (IOException | BackingStoreException e) {
			Assert.fail(e.getMessage());
		}
	}

	private int traverseStore(Set<UserCertStoreEntry> entries) {
		int entryCount = 1;

		try {
			for (UserCertStoreEntry entry : entries) {
				Attributes.toAttributes(entry);
				if (entry.hasCRT()) {
					X509CertificateHelper.toAttributes(entry.getCRT());
					accessExtensionValues(entry.getCRT());
				}
				if (entry.hasCSR()) {
					entry.getCSR().toAttributes();
					accessExtensionValues(entry.getCSR());
				}
				if (entry.hasCRL()) {
					X509CRLHelper.toAttributes(entry.getCRL());
					accessExtensionValues(entry.getCRL());
				}
				entry.canIssue();
				entry.isValid();
				entry.isRevoked();
				entryCount = traverseStore(entry.issuedEntries());
			}
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		return entryCount;
	}

	private void accessExtensionValues(X509Extension extension) throws IOException {
		Set<String> criticalExtensionOIDs = extension.getCriticalExtensionOIDs();

		if (criticalExtensionOIDs != null) {
			for (String criticalExtensionOID : extension.getCriticalExtensionOIDs()) {
				X509ExtensionData extensionData = X509ExtensionData.decode(criticalExtensionOID, true,
						extension.getExtensionValue(criticalExtensionOID));

				extensionData.toValueString();
			}
		}

		Set<String> nonCriticalExtensionOIDs = extension.getNonCriticalExtensionOIDs();

		if (nonCriticalExtensionOIDs != null) {
			for (String nonCriticalExtensionOID : nonCriticalExtensionOIDs) {
				X509ExtensionData extensionData = X509ExtensionData.decode(nonCriticalExtensionOID, true,
						extension.getExtensionValue(nonCriticalExtensionOID));

				extensionData.toValueString();
			}
		}
	}

	/**
	 * Test store creation from platform key stores.
	 */
	@Test
	public void testPlatformKeyStoreSourceStore() {
		try {
			DefaultSet<PlatformKeyStore> platformKeyStores = PlatformKeyStore.getDefaultSet();

			for (PlatformKeyStore platformKeyStore : platformKeyStores) {
				UserCertStore importStore = UserCertStore.createFromPlatformKeyStore(platformKeyStore,
						NoPassword.getInstance());

				traverseStore(importStore.getEntries());
			}
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Test store creation from file list source.
	 */
	@Test
	public void testFilesSourceStore() {
		try {
			List<Path> files = IOHelper.collectDirectoryFiles(testStorePath.get());
			UserCertStore importStore = UserCertStore.createFromFiles(files, TestCerts.password());

			Assert.assertNotNull(importStore);
			Assert.assertTrue(importStore.size() > 0);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
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

			Assert.assertEquals(0, importStore.size());
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
			UserCertStore importStore = UserCertStore.createFromURL(TestCerts.simplePKCS12URL(), TestCerts.password());

			Assert.assertNotNull(importStore);
			Assert.assertTrue(importStore.size() > 0);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

}
