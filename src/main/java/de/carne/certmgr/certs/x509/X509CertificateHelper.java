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
package de.carne.certmgr.certs.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import de.carne.boot.Exceptions;
import de.carne.boot.logging.Log;
import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.certs.x500.X500Names;

/**
 * Utility class providing {@link X509Certificate} related functions.
 */
public final class X509CertificateHelper {

	private X509CertificateHelper() {
		// Make sure this class is not instantiated from outside
	}

	private static final Log LOG = new Log();

	/**
	 * Get a CRT object's {@code Attributes}.
	 *
	 * @param crt The CRT object to get the attributes for.
	 * @return The CRT object's attributes.
	 */
	public static Attributes toAttributes(X509Certificate crt) {
		Attributes crtAttributes = new Attributes(AttributesI18N.formatSTR_CRT());

		crtAttributes.add(AttributesI18N.formatSTR_CRT_VERSION(), Integer.toString(crt.getVersion()));
		crtAttributes.add(AttributesI18N.formatSTR_CRT_SERIALNUMBER(), crt.getSerialNumber().toString());
		crtAttributes.add(AttributesI18N.formatSTR_CRT_SIGALG(), crt.getSigAlgName());
		crtAttributes.add(AttributesI18N.formatSTR_CRT_ISSUERDN(), X500Names.toString(crt.getIssuerX500Principal()));
		crtAttributes.add(AttributesI18N.formatSTR_CRT_NOTBEFORE(), Attributes.printShortDate(crt.getNotBefore()));
		crtAttributes.add(AttributesI18N.formatSTR_CRT_NOTAFTER(), Attributes.printShortDate(crt.getNotAfter()));
		crtAttributes.add(AttributesI18N.formatSTR_CRT_SUBJECTDN(), X500Names.toString(crt.getSubjectX500Principal()));
		crtAttributes.add(AttributesI18N.formatSTR_CRT_PUBLICKEY(), KeyHelper.toString(crt.getPublicKey()));
		X509ExtensionHelper.addAttributes(crtAttributes, crt);
		return crtAttributes;
	}

	/**
	 * Check whether a certificate has been signed by specific key pair.
	 *
	 * @param crt The certificate to check.
	 * @param publicKey The public key of the key pair to check.
	 * @return {@code true} if the certificate has been signed by the public key's key pair.
	 * @throws IOException if a general security error occurs during the check.
	 */
	public static boolean isCRTSignedBy(X509Certificate crt, PublicKey publicKey) throws IOException {
		boolean isSignedBy = false;

		try {
			crt.verify(publicKey);
			isSignedBy = true;
		} catch (SignatureException | InvalidKeyException e) {
			Exceptions.ignore(e);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return isSignedBy;
	}

	/**
	 * Generate a CRT object.
	 *
	 * @param dn The CRT's Distinguished Name (DN).
	 * @param key The CRT's key pair
	 * @param serial The CRT's serial.
	 * @param notBefore The CRT's validity start.
	 * @param notAfter The CRT's validity end.
	 * @param extensions The CRT's extension objects.
	 * @param issuerDN The issuer's Distinguished Name (DN).
	 * @param issuerKey The issuer's key pair.
	 * @param signatureAlgorithm The signature algorithm to use.
	 * @return The generated CRT object.
	 * @throws IOException if an error occurs during generation.
	 */
	public static X509Certificate generateCRT(X500Principal dn, KeyPair key, BigInteger serial, Date notBefore,
			Date notAfter, List<X509ExtensionData> extensions, X500Principal issuerDN, KeyPair issuerKey,
			SignatureAlgorithm signatureAlgorithm) throws IOException {
		LOG.info("CRT generation ''{0}'' started...", dn);

		// Initialize CRT builder
		X509v3CertificateBuilder crtBuilder = new JcaX509v3CertificateBuilder(issuerDN, serial, notBefore, notAfter, dn,
				key.getPublic());

		// Add custom extension objects
		for (X509ExtensionData extensionData : extensions) {
			String oid = extensionData.oid();

			if (!oid.equals(Extension.subjectKeyIdentifier.getId())
					&& !oid.equals(Extension.authorityKeyIdentifier.getId())) {
				boolean critical = extensionData.getCritical();

				crtBuilder.addExtension(new ASN1ObjectIdentifier(oid), critical, extensionData.encode());
			} else {
				LOG.warning("Ignoring key identifier extension");
			}
		}

		X509Certificate crt;

		try {
			// Add standard extensions based upon the CRT's purpose
			JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();

			for (X509ExtensionData extensionData : extensions) {
				if (extensionData instanceof BasicConstraintsExtensionData) {
					BasicConstraintsExtensionData basicConstraintsExtension = (BasicConstraintsExtensionData) extensionData;

					if (basicConstraintsExtension.getCA()) {
						// CRT is CA --> record it's key's identifier
						crtBuilder.addExtension(Extension.subjectKeyIdentifier, false,
								extensionUtils.createSubjectKeyIdentifier(key.getPublic()));
					}
				}
			}
			if (!key.equals(issuerKey)) {
				// CRT is not self-signed --> record issuer key's identifier
				crtBuilder.addExtension(Extension.authorityKeyIdentifier, false,
						extensionUtils.createAuthorityKeyIdentifier(issuerKey.getPublic()));
			}

			// Sign CRT
			ContentSigner crtSigner = new JcaContentSignerBuilder(signatureAlgorithm.algorithm())
					.build(issuerKey.getPrivate());

			crt = new JcaX509CertificateConverter().getCertificate(crtBuilder.build(crtSigner));
		} catch (OperatorCreationException | GeneralSecurityException e) {
			throw new CertProviderException(e);
		}

		LOG.info("CRT generation ''{0}'' done", dn);

		return crt;
	}

}
