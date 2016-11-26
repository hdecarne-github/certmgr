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
package de.carne.certmgr.jfx.certoptions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import de.carne.certmgr.certs.x509.GeneralName;
import de.carne.certmgr.certs.x509.GeneralNameType;
import de.carne.certmgr.certs.x509.StringName;
import de.carne.certmgr.util.DefaultSet;

final class GeneralNameFactory {

	private GeneralNameFactory() {
		// Make sure this class is not instantiated from outside
	}

	private static final Map<GeneralNameType, Function<String, GeneralName>> FACTORY_FUNCTIONS = new HashMap<>();

	static {
		FACTORY_FUNCTIONS.put(GeneralNameType.RFC822_NAME, (s) -> rfc822Name(s));
		FACTORY_FUNCTIONS.put(GeneralNameType.DNS_NAME, (s) -> dnsName(s));
		FACTORY_FUNCTIONS.put(GeneralNameType.DIRECTORY_NAME, (s) -> directoryName(s));
		FACTORY_FUNCTIONS.put(GeneralNameType.UNIFORM_RESOURCE_IDENTIFIER, (s) -> uriName(s));
		FACTORY_FUNCTIONS.put(GeneralNameType.IP_ADDRESS, (s) -> ipAddressName(s));
	}

	public static DefaultSet<GeneralNameType> types() {
		DefaultSet<GeneralNameType> types = new DefaultSet<>(FACTORY_FUNCTIONS.keySet());

		types.addDefault(GeneralNameType.DNS_NAME);
		return types;
	}

	public static GeneralName toGeneralName(GeneralNameType type, String name) throws IllegalArgumentException {
		Function<String, GeneralName> nameFunction = FACTORY_FUNCTIONS.get(type);

		if (nameFunction == null) {
			throw new IllegalArgumentException(GeneralNameFactoryI18N.formatSTR_MESSAGE_UNSUPPORTED_TYPE(type.name()));
		}
		return nameFunction.apply(name);
	}

	private static GeneralName rfc822Name(String name) throws IllegalArgumentException {
		String rfc822Name;

		rfc822Name = name;
		return new StringName(GeneralNameType.RFC822_NAME, rfc822Name);
	}

	private static GeneralName dnsName(String name) throws IllegalArgumentException {
		String dnsName;

		dnsName = name;
		return new StringName(GeneralNameType.DNS_NAME, dnsName);
	}

	private static GeneralName directoryName(String name) throws IllegalArgumentException {
		return null;
	}

	private static GeneralName uriName(String name) throws IllegalArgumentException {
		return null;
	}

	private static GeneralName ipAddressName(String name) throws IllegalArgumentException {
		return null;
	}

}
