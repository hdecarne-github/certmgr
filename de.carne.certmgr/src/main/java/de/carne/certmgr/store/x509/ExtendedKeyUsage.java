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
package de.carne.certmgr.store.x509;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;

/**
 * X.509 Extended Key Usage values.
 */
public final class ExtendedKeyUsage {

	private static final Hashtable<String, ExtendedKeyUsage> VALUES = new Hashtable<>();

	/**
	 * ANY
	 */
	public static final ExtendedKeyUsage ANY = new ExtendedKeyUsage("ANY", "2.5.29.37.0");

	/**
	 * SERVER_AUTH
	 */
	public static final ExtendedKeyUsage SERVER_AUTH = new ExtendedKeyUsage("SERVER_AUTH", "1.3.6.1.5.5.7.3.1");

	/**
	 * CLIENT_AUTH
	 */
	public static final ExtendedKeyUsage CLIENT_AUTH = new ExtendedKeyUsage("CLIENT_AUTH", "1.3.6.1.5.5.7.3.2");

	/**
	 * CODE_SIGNING
	 */
	public static final ExtendedKeyUsage CODE_SIGNING = new ExtendedKeyUsage("CODE_SIGNING", "1.3.6.1.5.5.7.3.3");

	/**
	 * EMAIL_PROTECTION
	 */
	public static final ExtendedKeyUsage EMAIL_PROTECTION = new ExtendedKeyUsage("EMAIL_PROTECTION",
			"1.3.6.1.5.5.7.3.4");

	/**
	 * IPSEC_END_SYSTEM
	 */
	public static final ExtendedKeyUsage IPSEC_END_SYSTEM = new ExtendedKeyUsage("IPSEC_END_SYSTEM",
			"1.3.6.1.5.5.7.3.5");

	/**
	 * IPSEC_TUNNEL
	 */
	public static final ExtendedKeyUsage IPSEC_TUNNEL = new ExtendedKeyUsage("IPSEC_TUNNEL", "1.3.6.1.5.5.7.3.6");

	/**
	 * IPSEC_USER
	 */
	public static final ExtendedKeyUsage IPSEC_USER = new ExtendedKeyUsage("IPSEC_USER", "1.3.6.1.5.5.7.3.7");

	/**
	 * TIME_STAMPING
	 */
	public static final ExtendedKeyUsage TIME_STAMPING = new ExtendedKeyUsage("TIME_STAMPING", "1.3.6.1.5.5.7.3.8");

	/**
	 * OCSP_SIGNING
	 */
	public static final ExtendedKeyUsage OCSP_SIGNING = new ExtendedKeyUsage("OCSP_SIGNING", "1.3.6.1.5.5.7.3.9");

	/**
	 * DVCS
	 */
	public static final ExtendedKeyUsage DVCS = new ExtendedKeyUsage("DVCS", "1.3.6.1.5.5.7.3.10");

	/**
	 * SBGP_CERT_AA_SERVER_AUTH
	 */
	public static final ExtendedKeyUsage SBGP_CERT_AA_SERVER_AUTH = new ExtendedKeyUsage("SBGP_CERT_AA_SERVER_AUTH",
			"1.3.6.1.5.5.7.3.11");

	/**
	 * SCVP_RESPONDER
	 */
	public static final ExtendedKeyUsage SCVP_RESPONDER = new ExtendedKeyUsage("SCVP_RESPONDER", "1.3.6.1.5.5.7.3.12");

	/**
	 * EAP_OVER_PPP
	 */
	public static final ExtendedKeyUsage EAP_OVER_PPP = new ExtendedKeyUsage("EAP_OVER_PPP", "1.3.6.1.5.5.7.3.13");

	/**
	 * EAP_OVER_LAN
	 */
	public static final ExtendedKeyUsage EAP_OVER_LAN = new ExtendedKeyUsage("EAP_OVER_LAN", "1.3.6.1.5.5.7.3.14");

	/**
	 * SCVP_SERVER
	 */
	public static final ExtendedKeyUsage SCVP_SERVER = new ExtendedKeyUsage("SCVP_SERVER", "1.3.6.1.5.5.7.3.15");

	/**
	 * SCVP_CLIENT
	 */
	public static final ExtendedKeyUsage SCVP_CLIENT = new ExtendedKeyUsage("SCVP_CLIENT", "1.3.6.1.5.5.7.3.16");

	/**
	 * IPSEC_IKE
	 */
	public static final ExtendedKeyUsage IPSEC_IKE = new ExtendedKeyUsage("IPSEC_IKE", "1.3.6.1.5.5.7.3.17");

	/**
	 * CAPWAP_AC
	 */
	public static final ExtendedKeyUsage CAPWAP_AC = new ExtendedKeyUsage("CAPWAP_AC", "1.3.6.1.5.5.7.3.18");

	/**
	 * CAPWAP_WTP
	 */
	public static final ExtendedKeyUsage CAPWAP_WTP = new ExtendedKeyUsage("CAPWAP_WTP", "1.3.6.1.5.5.7.3.19");

	private final String name;
	private final String value;

	private ExtendedKeyUsage(String name, String value) {
		assert name != null;
		assert value != null;

		this.name = name;
		this.value = value;
		VALUES.put(this.value, this);
	}

	/**
	 * Get the key usage name.
	 *
	 * @return The key usage name.
	 */
	public String name() {
		return this.name;
	}

	/**
	 * Get the key usage value.
	 *
	 * @return The key usage value.
	 */
	public String value() {
		return this.value;
	}

	/**
	 * Get the known values.
	 *
	 * @param includeAny Whether to include the AYN value in the result or not.
	 * @return The known values.
	 */
	public static ExtendedKeyUsage[] values(boolean includeAny) {
		ExtendedKeyUsage[] values;

		if (includeAny) {
			values = new ExtendedKeyUsage[VALUES.size()];
			VALUES.values().toArray(values);
		} else {
			values = new ExtendedKeyUsage[VALUES.size() - 1];

			int valueIndex = 0;

			for (ExtendedKeyUsage value : VALUES.values()) {
				if (!ANY.equals(value)) {
					values[valueIndex] = value;
					valueIndex++;
				}
			}
		}
		return values;
	}

	/**
	 * Get the values in sorted order.
	 *
	 * @param includeAny Whether to include the AYN value in the result or not.
	 * @return The values in sorted order.
	 */
	public static ExtendedKeyUsage[] sortedValues(boolean includeAny) {
		ExtendedKeyUsage[] unsorted = values(includeAny);
		ExtendedKeyUsage[] sorted = Arrays.copyOf(unsorted, unsorted.length);

		Arrays.sort(sorted, new Comparator<ExtendedKeyUsage>() {

			@Override
			public int compare(ExtendedKeyUsage o1, ExtendedKeyUsage o2) {
				return o1.name().compareTo(o2.name());
			}

		});
		return sorted;
	}

	/**
	 * Get key usage from name.
	 *
	 * @param name The name to get the key usage for.
	 * @return The found key usage or null if the name is unknown.
	 */
	public static ExtendedKeyUsage fromName(String name) {
		ExtendedKeyUsage found = null;

		for (ExtendedKeyUsage value : VALUES.values()) {
			if (value.name().equals(name)) {
				found = value;
				break;
			}
		}
		return found;
	}

	/**
	 * Get key usage from value.
	 *
	 * @param value The value to get the key usage for.
	 * @return The corresponding key usage.
	 */
	public static ExtendedKeyUsage valueOf(String value) {
		ExtendedKeyUsage found = VALUES.get(value);

		if (found == null) {
			found = new ExtendedKeyUsage(value, value);
			VALUES.put(value, found);
		}
		return found;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}

}
