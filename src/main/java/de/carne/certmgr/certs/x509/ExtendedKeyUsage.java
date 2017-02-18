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
package de.carne.certmgr.certs.x509;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * X.509 Extended Key Usage flags.
 */
public class ExtendedKeyUsage extends Enumeration<String> {

	private static final Map<String, ExtendedKeyUsage> instanceMap = new HashMap<>();

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

	private ExtendedKeyUsage(String name, String value) {
		super(name, value);
		registerInstance(this);
	}

	private static synchronized void registerInstance(ExtendedKeyUsage usage) {
		instanceMap.put(usage.value(), usage);
	}

	/**
	 * Get the known extended key usage instances.
	 * <p>
	 * This includes the statically defined ones in this class as well as any new ones encountered in a call to
	 * {@linkplain #fromValue(String )}.
	 *
	 * @return The known extended key usage instances.
	 */
	public static synchronized Set<ExtendedKeyUsage> instances() {
		return new HashSet<>(instanceMap.values());
	}

	/**
	 * Get the extended key usage instance for a specific value.
	 *
	 * @param value The value to get the instance for.
	 * @return The extended key usage instance corresponding to the submitted value.
	 */
	public static synchronized ExtendedKeyUsage fromValue(String value) {
		ExtendedKeyUsage usage = instanceMap.get(value);

		if (usage == null) {
			usage = new ExtendedKeyUsage(value, value);
		}
		return usage;
	}

}
