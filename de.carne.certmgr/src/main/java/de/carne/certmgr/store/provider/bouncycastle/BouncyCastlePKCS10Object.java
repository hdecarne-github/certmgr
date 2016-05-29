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
package de.carne.certmgr.store.provider.bouncycastle;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;

import de.carne.certmgr.store.PKCS10Object;
import de.carne.certmgr.store.asn1.ASN1Decoder;

/**
 * BouncyCastle PKCS#10 CSR object wrapper.
 */
class BouncyCastlePKCS10Object implements PKCS10Object {

	private final JcaPKCS10CertificationRequest pkcs10Object;
	private final X500Principal pkcs10Subject;
	private final PublicKey pkcs10PublicKey;

	BouncyCastlePKCS10Object(PKCS10CertificationRequest pkcs10Object)
			throws IOException, NoSuchAlgorithmException, InvalidKeyException {
		if (pkcs10Object instanceof JcaPKCS10CertificationRequest) {
			this.pkcs10Object = (JcaPKCS10CertificationRequest) pkcs10Object;
		} else {
			this.pkcs10Object = new JcaPKCS10CertificationRequest(pkcs10Object);
		}
		this.pkcs10Subject = new X500Principal(this.pkcs10Object.getSubject().getEncoded());
		this.pkcs10PublicKey = this.pkcs10Object.getPublicKey();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.PKCS10Object#getSigAlgName()
	 */
	@Override
	public String getSigAlgName() {
		Provider bouncyCastleProvider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
		String sigAlgOID = this.pkcs10Object.getSignatureAlgorithm().getAlgorithm().toString();
		String sigAlgNameKey = "Alg.Alias.Signature." + sigAlgOID;
		String sigAlgName = null;

		if (bouncyCastleProvider != null) {
			sigAlgName = bouncyCastleProvider.getProperty(sigAlgNameKey);
		}
		if (sigAlgName == null) {
			for (Provider provider : Security.getProviders()) {
				if (provider.equals(bouncyCastleProvider)) {
					continue;
				}
				sigAlgName = provider.getProperty(sigAlgNameKey);
				if (sigAlgName != null) {
					break;
				}
			}
		}
		return (sigAlgName != null ? sigAlgName : sigAlgOID);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.PKCS10Object#getSubjectX500Principal()
	 */
	@Override
	public X500Principal getSubjectX500Principal() {
		return this.pkcs10Subject;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.PKCS10Object#getPublicKey()
	 */
	@Override
	public PublicKey getPublicKey() {
		return this.pkcs10PublicKey;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.PKCS10Object#verify(java.security.PublicKey)
	 */
	@Override
	public void verify(PublicKey publicKey)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
		JcaContentVerifierProviderBuilder verifierBuilder = new JcaContentVerifierProviderBuilder();
		boolean isSignatureValid = false;

		try {
			isSignatureValid = this.pkcs10Object.isSignatureValid(verifierBuilder.build(publicKey));
		} catch (OperatorCreationException e) {
			throw new NoSuchAlgorithmException(e.getLocalizedMessage(), e);
		} catch (PKCSException e) {
			throw new SignatureException(e.getLocalizedMessage(), e);
		}
		if (!isSignatureValid) {
			throw new SignatureException("Verification failed for: " + this.pkcs10Object);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.PKCS10Object#getAttributeOIDs()
	 */
	@Override
	public Set<String> getAttributeOIDs() {
		HashSet<String> oids = new HashSet<>();

		for (Attribute attribute : this.pkcs10Object.getAttributes()) {
			oids.add(attribute.getAttrType().getId());
		}
		return oids;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.certmgr.store.PKCS10Object#getAttributeValues(java.lang.String)
	 */
	@Override
	public byte[][] getAttributeValues(String oid) throws IOException {
		Attribute[] attributes = this.pkcs10Object.getAttributes(new ASN1ObjectIdentifier(oid));
		ArrayList<byte[]> values = null;

		if (attributes != null) {
			values = new ArrayList<>(attributes.length);
			for (Attribute attribute : attributes) {
				values.add(attribute.getEncoded());
			}
		}
		return (values != null ? values.toArray(new byte[values.size()][]) : null);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.PKCS10Object#getExtensionOIDs()
	 */
	@Override
	public Set<String> getExtensionOIDs() throws IOException {
		HashSet<String> oids = new HashSet<>();
		Attribute[] attributes = this.pkcs10Object.getAttributes(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest);

		if (attributes != null) {
			for (Attribute attribute : attributes) {
				ASN1Encodable[] values = attribute.getAttributeValues();

				if (values != null) {
					for (ASN1Encodable value : values) {
						ASN1Decoder decoder = new BouncyCastleASN1Decoder(value.toASN1Primitive());
						ASN1Decoder[] entries = decoder.asn1DecodeSequence(-1, -1);

						for (ASN1Decoder entry : entries) {
							ASN1Decoder[] extensionEntries = entry.asn1DecodeSequence(3, 3);
							String extensionOID = extensionEntries[0].asn1DecodeOID();

							oids.add(extensionOID);
						}
					}
				}
			}
		}
		return oids;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.certmgr.store.PKCS10Object#getExtensionValue(java.lang.String)
	 */
	@Override
	public byte[] getExtensionValue(String oid) throws IOException {
		byte[] extensionValue = null;
		Attribute[] attributes = this.pkcs10Object.getAttributes(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest);

		if (attributes != null) {
			for (Attribute attribute : attributes) {
				if (extensionValue != null) {
					break;
				}

				ASN1Encodable[] values = attribute.getAttributeValues();

				if (values != null) {
					for (ASN1Encodable value : values) {
						if (extensionValue != null) {
							break;
						}

						ASN1Decoder decoder = new BouncyCastleASN1Decoder(value.toASN1Primitive());
						ASN1Decoder[] entries = decoder.asn1DecodeSequence(-1, -1);

						for (ASN1Decoder entry : entries) {
							ASN1Decoder[] extensionEntries = entry.asn1DecodeSequence(3, 3);
							String extensionOID = extensionEntries[0].asn1DecodeOID();

							if (oid.equals(extensionOID)) {
								extensionValue = extensionEntries[2].getEncoded();
								break;
							}
						}
					}
				}
			}
		}
		return extensionValue;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.PKCS10Object#getObject()
	 */
	@Override
	public Object getObject() {
		return this.pkcs10Object;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof PKCS10Object && this.pkcs10Object.equals(((PKCS10Object) obj).getObject());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.pkcs10Object.hashCode();
	}

}
