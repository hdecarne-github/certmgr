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
package de.carne.certmgr.test;

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.carne.certmgr.store.CertStore;
import de.carne.certmgr.store.CertStoreEntry;
import de.carne.certmgr.store.provider.StoreProvider;
import de.carne.certmgr.store.x509.CertificateValidity;
import de.carne.certmgr.store.x509.KeyParams;
import de.carne.certmgr.store.x509.X509CertificateParams;

/**
 * Test store operations.
 */
public class StoreTest extends StoreTestBase {

	private static final String STORE_BOUNCYCASTLE = "BouncyCastleStore";

	private static final String ALIAS_ROOT1 = "Root1";
	private static final String ALIAS_INTERMEDIATE1 = "Intermediate1";
	private static final String ALIAS_INTERMEDIATE2 = "Intermediate2";

	private static Path testRoot = null;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testRoot = createTestRoot();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		StoreTest.testRoot = deleteTestRoot(StoreTest.testRoot);
	}

	/**
	 * Test store operations for BouncyCastle provider.
	 */
	@Test
	public void testBouncyCastleProvider() {
		testProvider(StoreProvider.getInstance(StoreProvider.PROVIDER_BOUNCYCASTLE), STORE_BOUNCYCASTLE);
	}

	private void testProvider(StoreProvider provider, String storeName) {
		try {
			CertStore store1 = CertStore.create(StoreTest.testRoot.resolve(storeName));

			CertStoreEntry root1Entry = testGenerateAndSignCRT(store1, ALIAS_ROOT1, null);
			CertStoreEntry intermediate1Entry = testGenerateAndSignCRT(store1, ALIAS_INTERMEDIATE1, root1Entry);
			CertStoreEntry intermediate2Entry = testGenerateAndSignCRT(store1, ALIAS_INTERMEDIATE2, root1Entry);

			Assert.assertEquals(getCRTSignature(root1Entry), getCRTParamsSignature(ALIAS_ROOT1));
			Assert.assertEquals(getCRTSignature(intermediate1Entry), getCRTParamsSignature(ALIAS_INTERMEDIATE1));
			Assert.assertEquals(getCRTSignature(intermediate2Entry), getCRTParamsSignature(ALIAS_INTERMEDIATE2));

			System.out.print(getStoreSignature(store1));

			CertStore store2 = CertStore.open(store1.getHome());

			Assert.assertEquals(getStoreSignature(store1), getStoreSignature(store2));
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	private CertStoreEntry testGenerateAndSignCRT(CertStore store, String alias, CertStoreEntry issuer)
			throws IOException, GeneralSecurityException {
		KeyParams keyParams = getKeyParams(alias);
		X509CertificateParams certificateParams = getCertificateParams(alias);
		CertificateValidity certificateValidity = getCertificateValidity(alias);

		return store.generateAndSignCRT(alias, keyParams, certificateParams, certificateValidity, TEST_PASSWORD, issuer,
				TEST_PASSWORD);
	}

}
