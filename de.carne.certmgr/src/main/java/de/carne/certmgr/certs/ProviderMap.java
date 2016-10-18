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
package de.carne.certmgr.certs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import de.carne.certmgr.certs.spi.NamedProvider;
import de.carne.util.logging.Log;

/**
 * Map like class providing access to named service providers of a specific
 * type.
 *
 * @param <P> The service provider type hold by this class.
 */
public class ProviderMap<P extends NamedProvider> {

	private static final Log LOG = new Log();

	private final Map<String, P> providerMap = new HashMap<>();

	/**
	 * Construct {@code ProviderMap}.
	 *
	 * @param providerClass The service provider type to load.
	 */
	public ProviderMap(Class<P> providerClass) {
		assert providerClass != null;

		ServiceLoader<P> serviceLoader = ServiceLoader.load(providerClass);

		serviceLoader.forEach(p -> registerProvider(p));
	}

	private void registerProvider(P provider) {
		String providerName = provider.providerName();

		LOG.debug("Registering provider ''{0}'' -> {1}", providerName, provider.getClass().getName());

		P replacedProvider = this.providerMap.put(providerName, provider);

		if (replacedProvider != null) {
			LOG.warning("Multiple providers ({0}, {1}) defined for name ''{2}''", replacedProvider.getClass().getName(),
					provider.getClass().getName(), providerName);
		}
	}

	/**
	 * Get the names of the loaded service providers.
	 *
	 * @return The names of the loaded service providers.
	 */
	public Set<String> names() {
		return this.providerMap.keySet();
	}

	/**
	 * Get the loaded service providers.
	 *
	 * @return The loaded service providers.
	 */
	public Collection<P> providers() {
		return this.providerMap.values();
	}

	/**
	 * Look up the service provider for a specific name.
	 * 
	 * @param name The name to look up the service provider for.
	 * @return The found service provider or {@code null} if no service provider
	 *         is known for the given name.
	 */
	public P get(String name) {
		assert name != null;

		return this.providerMap.get(name);
	}

}
