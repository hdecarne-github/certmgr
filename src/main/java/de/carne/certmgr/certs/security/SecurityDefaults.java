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
import java.security.Security;
import java.time.Period;
import java.util.Properties;
import java.util.function.Function;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.carne.certmgr.util.Days;
import de.carne.util.DefaultSet;
import de.carne.util.PropertiesHelper;

class SecurityDefaults {

	private SecurityDefaults() {
		// Make sure this class is not instantiated from outside
	}

	private static final Properties DEFAULTS = PropertiesHelper.init(SecurityDefaults.class);

	private static final String KEY_KEY_ALGORITHM = "keyAlgorithm";
	private static final String KEY_KEY_SIZE = ".keySize";
	private static final String KEY_SIGNATURE_ALGORITHM = ".signatureAlgorithm";
	private static final String KEY_CRT_VALIDITY_PERIOD = "crtValidity";
	private static final String KEY_CRL_UPDATE_PERIOD = "crlUpdate";
	private static final String KEY_PLATFORM_KEY_STORE = "platformKeyStore";

	public static Provider[] getProviders(boolean expertMode) {
		return (expertMode ? Security.getProviders()
				: new Provider[] { Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) });
	}

	public static DefaultSet<String> getKeyAlgorithmNames() {
		return getValues(KEY_KEY_ALGORITHM, (s) -> s);
	}

	public static DefaultSet<Integer> getKeySizes(String algorithm) {
		return getValues(algorithm + KEY_KEY_SIZE, (s) -> Integer.valueOf(s));
	}

	public static DefaultSet<String> getSignatureAlgorithmNames(String keyPairAlgorithm) {
		return getValues(keyPairAlgorithm + KEY_SIGNATURE_ALGORITHM, (s) -> s);
	}

	public static DefaultSet<Days> getCRTValidityPeriods() {
		return getValues(KEY_CRT_VALIDITY_PERIOD, (s) -> new Days(Period.parse(s)));
	}

	public static DefaultSet<Days> getCRLUpdatedPeriods() {
		return getValues(KEY_CRL_UPDATE_PERIOD, (s) -> new Days(Period.parse(s)));
	}

	public static DefaultSet<String> getPlatformKeyStoreNames() {
		return getValues(KEY_PLATFORM_KEY_STORE, (s) -> s);
	}

	private static <T> DefaultSet<T> getValues(String key, Function<String, T> conversion) {
		DefaultSet<T> defaultValueSet = new DefaultSet<>();
		int keyIndex = 1;
		String valueString;

		while ((valueString = DEFAULTS.getProperty(key + "." + keyIndex)) != null) {
			defaultValueSet.add(conversion.apply(valueString));
			keyIndex++;
		}
		if (keyIndex > 1) {
			defaultValueSet.addDefault(conversion.apply(DEFAULTS.getProperty(key)));
		}
		return defaultValueSet;
	}

}
