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
package de.carne.certmgr.certs.x509;

import java.math.BigInteger;
import java.security.cert.CRLReason;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.Date;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import de.carne.certmgr.certs.x500.X500Names;

/**
 * Utility class providing {@link X509CRL} related functions.
 */
public final class X509CRLHelper {

	/**
	 * Get a CRL object's {@code Attributes}.
	 *
	 * @param crl The CRL object to get the attributes for.
	 * @return The CRL object's attributes.
	 */
	public static Attributes toAttributes(X509CRL crl) {
		assert crl != null;

		Attributes crlAttributes = new Attributes(AttributesI18N.formatSTR_CRL());

		crlAttributes.add(AttributesI18N.formatSTR_CRL_VERSION(), Integer.toString(crl.getVersion()));
		crlAttributes.add(AttributesI18N.formatSTR_CRL_THISUPDATE(), Attributes.printShortDate(crl.getThisUpdate()));
		crlAttributes.add(AttributesI18N.formatSTR_CRL_NEXTUPDATE(), Attributes.printShortDate(crl.getNextUpdate()));
		crlAttributes.add(AttributesI18N.formatSTR_CRL_SIGALG(), crl.getSigAlgName());
		crlAttributes.add(AttributesI18N.formatSTR_CRL_ISSUERDN(), X500Names.toString(crl.getIssuerX500Principal()));
		X509ExtensionHelper.addAttributes(crlAttributes, crl);

		Set<? extends X509CRLEntry> crlEntries = crl.getRevokedCertificates();

		if (crlEntries != null) {
			int entryIndex = 0;

			for (X509CRLEntry crlEntry : crlEntries) {
				BigInteger serial = crlEntry.getSerialNumber();
				X500Principal issuer = crlEntry.getCertificateIssuer();
				String entrySerial = (issuer != null
						? AttributesI18N.formatSTR_CRL_ENTRY_SERIAL_INDIRECT(Attributes.printSerial(serial), issuer)
						: AttributesI18N.formatSTR_CRL_ENTRY_SERIAL(Attributes.printSerial(serial)));
				Attributes crlEntryAttributes = crlAttributes.add(AttributesI18N.formatSTR_CRL_ENTRY(entryIndex),
						entrySerial);
				Date revocationDate = crlEntry.getRevocationDate();

				crlEntryAttributes.add(AttributesI18N.formatSTR_CRL_ENTRY_DATE(),
						Attributes.printShortDate(revocationDate));

				CRLReason revocationReason = crlEntry.getRevocationReason();

				if (revocationReason != null) {
					crlEntryAttributes.add(AttributesI18N.formatSTR_CRL_ENTRY_REASON(),
							ReasonFlag.fromValue(revocationReason.ordinal()).name());
				}
				X509ExtensionHelper.addAttributes(crlEntryAttributes, crlEntry);
				entryIndex++;
			}
		}
		return crlAttributes;
	}

}
