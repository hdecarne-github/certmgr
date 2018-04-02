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
package de.carne.certmgr.certs.security;

import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;

import de.carne.jfx.util.DefaultSet;

/**
 * Platform key store provisioning.
 */
public class PlatformKeyStore extends AbstractAlgorithm {

	private static final String SERVICE_TYPE_KEY_STORE = "KeyStore";

	private PlatformKeyStore(Service service) {
		super(service);
	}

	/**
	 * Get the available platform key stores.
	 *
	 * @return The available platform key stores.
	 */
	public static DefaultSet<PlatformKeyStore> getDefaultSet() {
		DefaultSet<PlatformKeyStore> platformKeyStores = new DefaultSet<>();
		DefaultSet<String> defaultNames = SecurityDefaults.getPlatformKeyStoreNames();
		String defaultName = defaultNames.getDefault();

		for (Provider provider : Security.getProviders()) {
			for (Provider.Service service : provider.getServices()) {
				if (!SERVICE_TYPE_KEY_STORE.equals(service.getType())) {
					continue;
				}

				String algorithm = service.getAlgorithm();

				if (!defaultNames.contains(algorithm)) {
					continue;
				}

				PlatformKeyStore platformKeyStore = new PlatformKeyStore(service);

				if (algorithm.equals(defaultName)) {
					platformKeyStores.addDefault(platformKeyStore);
				} else {
					platformKeyStores.add(platformKeyStore);
				}
			}
		}
		return platformKeyStores;
	}

}
