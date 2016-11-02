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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.io.IOHelper;

/**
 * Test {@link UserCertStore} class functionality.
 */
public class UserCertStoreTest {

	private static final String NAME_STORE1 = "store1";

	private static Path tempPath = null;

	/**
	 * Setup temporary directory for all tests.
	 *
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		tempPath = Files.createTempDirectory(UserCertStoreTest.class.getSimpleName());
		System.out.println("Using temporary directory: " + tempPath);
	}

	/**
	 * Clean up temporary directory be deleting it including all content.
	 *
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		IOHelper.deleteDirectoryTree(tempPath);
	}

	/**
	 * Test store create/open store operations.
	 */
	@Test
	public void testCreateAndOpenStore() {
		Path storeHome = tempPath.resolve(NAME_STORE1);

		try {
			UserCertStore createdStore = UserCertStore.createStore(storeHome);

			Assert.assertEquals(createdStore.size(), 0);
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

			Assert.assertEquals(openendStore.size(), 0);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

}
