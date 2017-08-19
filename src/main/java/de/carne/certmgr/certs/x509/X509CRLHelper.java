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

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CRLReason;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v2CRLBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.certs.x500.X500Names;
import de.carne.check.Nullable;
import de.carne.util.Exceptions;
import de.carne.util.logging.Log;

/**
 * Utility class providing {@link X509CRL} related functions.
 */
public final class X509CRLHelper {

	private X509CRLHelper() {
		// Make sure this class is not instantiated from outside
	}

	private static final Log LOG = new Log();

	/**
	 * Get a CRL object's {@code Attributes}.
	 *
	 * @param crl The CRL object to get the attributes for.
	 * @return The CRL object's attributes.
	 */
	public static Attributes toAttributes(X509CRL crl) {
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
							ReasonFlag.fromCRLReason(revocationReason).name());
				}
				X509ExtensionHelper.addAttributes(crlEntryAttributes, crlEntry);
				entryIndex++;
			}
		}
		return crlAttributes;
	}

	/**
	 * Check whether a CRL object has been signed by specific key pair.
	 *
	 * @param crl The CRL object to check.
	 * @param publicKey The public key of the key pair to check.
	 * @return {@code true} if the CRL object has been signed by the public key's key pair.
	 * @throws IOException if a general security error occurs during the check.
	 */
	public static boolean isCRLSignedBy(X509CRL crl, PublicKey publicKey) throws IOException {
		boolean isSignedBy = false;

		try {
			crl.verify(publicKey);
			isSignedBy = true;
		} catch (SignatureException | InvalidKeyException e) {
			Exceptions.ignore(e);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return isSignedBy;
	}

	/**
	 * Generate a CRL object.
	 *
	 * @param currentCRL The current CRL object in case of an update (may be {@code null}).
	 * @param lastUpdate The last update timestamp to set.
	 * @param nextUpdate The next update timestamp to set (may be {@code null}).
	 * @param revokeEntries The revoked entries.
	 * @param issuerDN The CRL issuer's DN.
	 * @param issuerKey The CRL issuer's key pair.
	 * @param signatureAlgorithm The signature algorithm to use for signing.
	 * @return The generated CRL object.
	 * @throws IOException if an error occurs during generation.
	 */
	public static X509CRL generateCRL(@Nullable X509CRL currentCRL, Date lastUpdate, @Nullable Date nextUpdate,
			Map<BigInteger, ReasonFlag> revokeEntries, X500Principal issuerDN, KeyPair issuerKey,
			SignatureAlgorithm signatureAlgorithm) throws IOException {
		LOG.info("CRL generation ''{0}'' started...", issuerDN);

		// Initialize CRL builder
		JcaX509v2CRLBuilder crlBuilder = new JcaX509v2CRLBuilder(issuerDN, lastUpdate);

		if (nextUpdate != null) {
			crlBuilder.setNextUpdate(nextUpdate);
		}

		for (Map.Entry<BigInteger, ReasonFlag> revokeEntry : revokeEntries.entrySet()) {
			crlBuilder.addCRLEntry(revokeEntry.getKey(), lastUpdate, revokeEntry.getValue().value());
		}

		X509CRL crl;

		try {
			// Add extensions
			JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();

			crlBuilder.addExtension(Extension.authorityKeyIdentifier, false,
					extensionUtils.createAuthorityKeyIdentifier(issuerKey.getPublic()));

			BigInteger nextCRLNumber = getNextCRLNumber(currentCRL);

			crlBuilder.addExtension(Extension.cRLNumber, false, new CRLNumber(nextCRLNumber));

			// Sign and create CRL object
			ContentSigner crlSigner = new JcaContentSignerBuilder(signatureAlgorithm.algorithm())
					.build(issuerKey.getPrivate());

			crl = new JcaX509CRLConverter().getCRL(crlBuilder.build(crlSigner));
		} catch (GeneralSecurityException | OperatorCreationException e) {
			throw new CertProviderException(e);
		}

		LOG.info("CRT generation ''{0}'' done", issuerDN);

		return crl;
	}

	private static BigInteger getNextCRLNumber(X509CRL crl) throws IOException {
		BigInteger nextCRLNumber = BigInteger.ONE;

		if (crl != null) {
			byte[] encoded = crl.getExtensionValue(CRLNumberExtensionData.OID);

			if (encoded != null) {
				CRLNumberExtensionData crlNumberExtensionData = (CRLNumberExtensionData) X509ExtensionData
						.decode(CRLNumberExtensionData.OID, CRLNumberExtensionData.CRITICAL_DEFAULT, encoded);

				nextCRLNumber = nextCRLNumber.add(crlNumberExtensionData.getCRLNumber());
			}
		}
		return nextCRLNumber;
	}

}
