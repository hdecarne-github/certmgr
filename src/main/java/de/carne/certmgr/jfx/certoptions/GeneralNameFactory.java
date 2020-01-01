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
package de.carne.certmgr.jfx.certoptions;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.security.auth.x500.X500Principal;

import de.carne.certmgr.certs.x500.X500Names;
import de.carne.certmgr.certs.x509.DirectoryName;
import de.carne.certmgr.certs.x509.GeneralName;
import de.carne.certmgr.certs.x509.GeneralNameType;
import de.carne.certmgr.certs.x509.StringName;
import de.carne.jfx.util.DefaultSet;
import de.carne.util.Strings;

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
	}

	public static DefaultSet<GeneralNameType> alternateNameTypes() {
		DefaultSet<GeneralNameType> types = new DefaultSet<>(FACTORY_FUNCTIONS.keySet());

		types.addDefault(GeneralNameType.DNS_NAME);
		return types;
	}

	public static DefaultSet<GeneralNameType> locationTypes() {
		DefaultSet<GeneralNameType> types = new DefaultSet<>(FACTORY_FUNCTIONS.keySet());

		types.addDefault(GeneralNameType.UNIFORM_RESOURCE_IDENTIFIER);
		return types;
	}

	public static GeneralName toGeneralName(GeneralNameType type, String name) throws IllegalArgumentException {
		Function<String, GeneralName> nameFunction = FACTORY_FUNCTIONS.get(type);

		if (nameFunction == null) {
			throw new IllegalArgumentException(GeneralNameFactoryI18N.strMessageUnsupportedType(type.name()));
		}
		return nameFunction.apply(name);
	}

	private static GeneralName rfc822Name(String name) throws IllegalArgumentException {
		String rfc822Name = Strings.safe(name).trim();

		if (Strings.isEmpty(rfc822Name)) {
			throw new IllegalArgumentException(GeneralNameFactoryI18N.strMessageNoRfc822Name());
		}
		return new StringName(GeneralNameType.RFC822_NAME, rfc822Name);
	}

	private static GeneralName dnsName(String name) throws IllegalArgumentException {
		String dnsName = Strings.safe(name).trim();

		if (Strings.isEmpty(dnsName)) {
			throw new IllegalArgumentException(GeneralNameFactoryI18N.strMessageNoDnsName());
		}
		return new StringName(GeneralNameType.DNS_NAME, dnsName);
	}

	private static GeneralName directoryName(String name) throws IllegalArgumentException {
		String directoryNameString = Strings.safe(name).trim();

		if (Strings.isEmpty(directoryNameString)) {
			throw new IllegalArgumentException(GeneralNameFactoryI18N.strMessageNoDirectoryName());
		}

		X500Principal directoryNameX500;

		try {
			directoryNameX500 = X500Names.fromString(directoryNameString);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
					GeneralNameFactoryI18N.strMessageInvalidDirectoryName(directoryNameString, e.getLocalizedMessage()),
					e);
		}
		return new DirectoryName(directoryNameX500);
	}

	private static GeneralName uriName(String name) throws IllegalArgumentException {
		String uriName = Strings.safe(name).trim();

		if (Strings.isEmpty(uriName)) {
			throw new IllegalArgumentException(GeneralNameFactoryI18N.strMessageNoUriName());
		}
		try {
			new URI(uriName);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(
					GeneralNameFactoryI18N.strMessageInvalidUriName(uriName, e.getLocalizedMessage()), e);
		}
		return new StringName(GeneralNameType.UNIFORM_RESOURCE_IDENTIFIER, uriName);
	}

}
