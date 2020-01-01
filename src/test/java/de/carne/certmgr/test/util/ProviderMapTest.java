/*
 * Copyright (c) 2015-2020 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.test.util;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.certs.spi.CertGenerator;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.certmgr.certs.spi.NamedProvider;
import de.carne.certmgr.util.ProviderMap;

/**
 * Test SPI class access via {@link ProviderMap} class.
 */
public class ProviderMapTest {

	/**
	 * Test {@link CertReader} provider.
	 */
	@Test
	public void testCertReaderProviders() {
		testProvider(CertReader.class);
	}

	/**
	 * Test {@link CertWriter} provider.
	 */
	@Test
	public void testCertWriterProviders() {
		testProvider(CertWriter.class);
	}

	/**
	 * Test {@link CertGenerator} provider.
	 */
	@Test
	public void testCertSignerProviders() {
		testProvider(CertGenerator.class);
	}

	private <T extends NamedProvider> void testProvider(Class<T> cls) {
		ProviderMap<T> providers = new ProviderMap<>(cls);
		Collection<String> providerNames = new ArrayList<>();

		for (T provider : providers.providers()) {
			String providerName = provider.providerName();

			providerNames.add(providerName);
			Assert.assertEquals(provider, providers.get(providerName));
		}
		Assert.assertArrayEquals(providers.names().toArray(), providerNames.toArray());
	}

}
