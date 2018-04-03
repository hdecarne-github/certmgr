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
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Extension;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import de.carne.boot.logging.Log;
import de.carne.certmgr.certs.CertProviderException;
import de.carne.certmgr.certs.asn1.ASN1Data;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.certs.x500.X500Names;
import de.carne.check.Nullable;

/**
 * This class represents a PKCS#10 Certificate Signing Request (CSR) object.
 * <p>
 * As there is no official CSR class in the {@code java.security} package this application specific class is used
 * instead.
 * <p>
 * In the current implementation this class is wrapper around BouncyCastle's {@link PKCS10CertificationRequest} class.
 */
public class PKCS10CertificateRequest extends ASN1Data implements X509Extension, AttributesProvider {

	private static final Log LOG = new Log();

	private final JcaPKCS10CertificationRequest csr;
	private final X500Principal subject;
	private final PublicKey publicKey;
	private final Map<String, byte[]> criticalExtensions;
	private final Map<String, byte[]> nonCriticalExtensions;

	private PKCS10CertificateRequest(JcaPKCS10CertificationRequest csr, X500Principal subject, PublicKey publicKey,
			Map<String, byte[]> criticalExtensions, Map<String, byte[]> nonCriticalExtensions) {
		this.csr = csr;
		this.subject = subject;
		this.publicKey = publicKey;
		this.criticalExtensions = criticalExtensions;
		this.nonCriticalExtensions = nonCriticalExtensions;
	}

	/**
	 * Generate a CSR object.
	 *
	 * @param dn The CSR's Distinguished Name (DN).
	 * @param key The CSR's key pair
	 * @param extensions The CRT's extension objects.
	 * @param signatureAlgorithm The signature algorithm to use.
	 * @return The generated CSR object.
	 * @throws IOException if an error occurs during generation.
	 */
	public static PKCS10CertificateRequest generateCSR(X500Principal dn, KeyPair key,
			List<X509ExtensionData> extensions, SignatureAlgorithm signatureAlgorithm) throws IOException {

		LOG.info("CSR generation ''{0}'' started...", dn);

		// Initialize CSR builder
		PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(dn, key.getPublic());

		// Add custom extension objects
		ExtensionsGenerator extensionGenerator = new ExtensionsGenerator();

		for (X509ExtensionData extensionData : extensions) {
			extensionGenerator.addExtension(new ASN1ObjectIdentifier(extensionData.oid()), extensionData.getCritical(),
					extensionData.encode());
		}
		csrBuilder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extensionGenerator.generate());

		PKCS10CertificateRequest csr;

		try {
			// Sign CSR
			ContentSigner csrSigner;

			csrSigner = new JcaContentSignerBuilder(signatureAlgorithm.algorithm()).build(key.getPrivate());
			csr = fromPKCS10(csrBuilder.build(csrSigner));
		} catch (OperatorCreationException e) {
			throw new CertProviderException(e);
		}

		LOG.info("CSR generation ''{0}'' done", dn);

		return csr;
	}

	/**
	 * Construct {@code PKCS10CertificateRequest} from a PKCS#10 object.
	 *
	 * @param pkcs10 The PCKS#10 object.
	 * @return The constructed {@code PKCS10CertificateRequest}.
	 * @throws IOException if an I/O error occurs while accessing the PKCS#10 object.
	 */
	public static PKCS10CertificateRequest fromPKCS10(PKCS10CertificationRequest pkcs10) throws IOException {
		JcaPKCS10CertificationRequest csr;
		X500Principal subject;
		PublicKey publicKey;
		Map<String, byte[]> criticalExtensions = new HashMap<>();
		Map<String, byte[]> nonCriticalExtensions = new HashMap<>();

		try {
			if (pkcs10 instanceof JcaPKCS10CertificationRequest) {
				csr = (JcaPKCS10CertificationRequest) pkcs10;
			} else {
				csr = new JcaPKCS10CertificationRequest(pkcs10);
			}
			subject = new X500Principal(csr.getSubject().getEncoded());
			publicKey = csr.getPublicKey();

			Attribute[] extensionAttributes = csr.getAttributes(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest);

			if (extensionAttributes != null) {
				for (Attribute extensionAttribute : extensionAttributes) {
					ASN1Encodable[] values = extensionAttribute.getAttributeValues();

					if (values != null) {
						for (ASN1Encodable value : values) {
							ASN1Primitive[] extensionPrimitives = decodeSequence(value.toASN1Primitive(), 0,
									Integer.MAX_VALUE);

							for (ASN1Primitive extensionPrimitive : extensionPrimitives) {
								ASN1Primitive[] sequence = decodeSequence(extensionPrimitive, 2, 3);
								String extensionOID = decodePrimitive(sequence[0], ASN1ObjectIdentifier.class).getId();
								boolean criticalFlag = true;
								byte[] extensionData;

								if (sequence.length == 3) {
									criticalFlag = decodePrimitive(sequence[1], ASN1Boolean.class).isTrue();
									extensionData = sequence[2].getEncoded();
								} else {
									extensionData = sequence[1].getEncoded();
								}
								if (criticalFlag) {
									criticalExtensions.put(extensionOID, extensionData);
								} else {
									nonCriticalExtensions.put(extensionOID, extensionData);
								}
							}
						}
					}
				}
			}
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return new PKCS10CertificateRequest(csr, subject, publicKey, criticalExtensions, nonCriticalExtensions);
	}

	/**
	 * Convert this {@code PKCS10CertificateRequest} to a PKCS#10 object.
	 *
	 * @return The converted object.
	 * @throws IOException if an I/O error occurs during conversion.
	 */
	public PKCS10CertificationRequest toPKCS10() throws IOException {
		return this.csr;
	}

	/**
	 * Get the name of the signature algorithm used to sign the CSR.
	 *
	 * @return The name of the signature algorithm used to sign the CSR.
	 */
	public String getSigAlgName() {
		String sigAlgOID = this.csr.getSignatureAlgorithm().getAlgorithm().toString();
		String sigAlgPropertyKey = "Alg.Alias.Signature." + sigAlgOID;
		String sigAlgName = null;

		for (Provider provider : Security.getProviders()) {
			sigAlgName = provider.getProperty(sigAlgPropertyKey);
			if (sigAlgName != null) {
				break;
			}
		}
		return (sigAlgName != null ? sigAlgName : sigAlgOID);
	}

	/**
	 * Get this CSR's subject.
	 *
	 * @return This CSR's subject.
	 */
	public X500Principal getSubjectX500Principal() {
		return this.subject;
	}

	/**
	 * Get this CSR's public key.
	 *
	 * @return This CSR's public key.
	 */
	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	@Override
	public ASN1Encodable encode() throws IOException {
		return this.csr.toASN1Structure();
	}

	@Override
	public Attributes toAttributes() {
		Attributes csrAttributes = new Attributes(AttributesI18N.formatSTR_CSR());

		csrAttributes.add(AttributesI18N.formatSTR_CSR_SIGALG(), getSigAlgName());
		csrAttributes.add(AttributesI18N.formatSTR_CSR_SUBJECTDN(), X500Names.toString(getSubjectX500Principal()));
		csrAttributes.add(AttributesI18N.formatSTR_CSR_PUBLICKEY(), KeyHelper.toString(getPublicKey()));
		X509ExtensionHelper.addAttributes(csrAttributes, this);
		return csrAttributes;
	}

	@Override
	public boolean hasUnsupportedCriticalExtension() {
		return false;
	}

	@Override
	public Set<String> getCriticalExtensionOIDs() {
		return Collections.unmodifiableSet(this.criticalExtensions.keySet());
	}

	@Override
	public Set<String> getNonCriticalExtensionOIDs() {
		return Collections.unmodifiableSet(this.nonCriticalExtensions.keySet());
	}

	@Override
	public byte[] getExtensionValue(@Nullable String oid) {
		return this.criticalExtensions.getOrDefault(oid, this.nonCriticalExtensions.get(oid));
	}

	@Override
	public int hashCode() {
		return this.csr.hashCode();
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return obj instanceof PKCS10CertificateRequest && this.csr.equals(((PKCS10CertificateRequest) obj).csr);
	}

	@Override
	public String toString() {
		return this.csr.toString();
	}

}
