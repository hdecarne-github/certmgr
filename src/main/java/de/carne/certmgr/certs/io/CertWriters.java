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
package de.carne.certmgr.certs.io;

import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.certmgr.util.ProviderMap;
import de.carne.boot.check.Check;

/**
 * Utility class providing {@link CertWriter} related functions.
 */
public final class CertWriters {

	private CertWriters() {
		// Make sure this class is not instantiated from outside
	}

	/**
	 * The registered {@link CertWriter}s.
	 */
	public static final ProviderMap<CertWriter> REGISTERED = new ProviderMap<>(CertWriter.class);

	/**
	 * The default {@link CertWriter}.
	 */
	public static final CertWriter DEFAULT = Check.notNull(REGISTERED.get(PEMCertReaderWriter.PROVIDER_NAME));

}
