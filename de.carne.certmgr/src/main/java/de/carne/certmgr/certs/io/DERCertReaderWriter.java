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
package de.carne.certmgr.certs.io;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.NoSuchParserException;
import org.bouncycastle.x509.X509StreamParser;
import org.bouncycastle.x509.util.StreamParsingException;
import org.checkerframework.checker.nullness.qual.Nullable;

import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.util.Exceptions;
import de.carne.util.Strings;
import de.carne.util.logging.Log;

/**
 * DER I/O support.
 */
public class DERCertReaderWriter implements CertReader, CertWriter {

	private static final Log LOG = new Log();

	/**
	 * Provider name.
	 */
	public static final String PROVIDER_NAME = "DER";

	@Override
	public String providerName() {
		return PROVIDER_NAME;
	}

	@Override
	public String fileType() {
		return CertIOI18N.formatSTR_DER_TYPE();
	}

	@Override
	public String[] fileExtensions() {
		return Strings.split(CertIOI18N.formatSTR_DER_EXTENSIONS(), "|");
	}

	@Override
	@Nullable
	public List<Object> read(CertReaderInput input, PasswordCallback password) throws IOException {
		LOG.debug("Trying to read DER objects from: ''{0}''...", input);

		List<Object> certObjects = parseObjects(input, "CRL", BouncyCastleProvider.PROVIDER_NAME);

		if (certObjects == null) {
			certObjects = parseObjects(input, "CERTIFICATE", BouncyCastleProvider.PROVIDER_NAME);
		}
		return certObjects;
	}

	private List<Object> parseObjects(CertReaderInput input, String type, String provider) throws IOException {
		List<Object> certObjects = null;

		try (InputStream stream = input.stream()) {
			if (stream != null) {
				X509StreamParser parser = X509StreamParser.getInstance(type, provider);

				parser.init(stream);

				Collection<Object> parsedObjects = parser.readAll();

				if (parsedObjects != null && !parsedObjects.isEmpty()) {
					certObjects = new ArrayList<>(parsedObjects);
				}
			}
		} catch (StreamParsingException e) {
			Exceptions.ignore(e);
		} catch (GeneralSecurityException | NoSuchParserException e) {
			throw new CertProviderException(e);
		}
		return certObjects;
	}

}
