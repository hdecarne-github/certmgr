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
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagFactory;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;

import de.carne.certmgr.store.PasswordCallback;
import de.carne.certmgr.store.PasswordRequiredException;
import de.carne.util.Pair;
import de.carne.util.logging.Log;

/**
 * Decode PKCS#12 data.
 */
final class PKCS12Decoder {

	private static final Log LOG = new Log(PKCS12Decoder.class);

	private ArrayList<Object> decoded = new ArrayList<>();
	private PKCS12PfxPdu pkcs12;
	private PasswordCallback password;
	private String resource;
	private JcaX509CertificateConverter jcaConverter = new JcaX509CertificateConverter();
	private JcePKCSPBEInputDecryptorProviderBuilder inputDecrypterProviderBuilder = new JcePKCSPBEInputDecryptorProviderBuilder();
	private InputDecryptorProvider inputDecryptorProvider = null;
	private HashMap<ASN1Encodable, Pair<PublicKey, PrivateKey>> keyMap = new HashMap<>();

	PKCS12Decoder(PKCS12PfxPdu pkcs12, PasswordCallback password, String resource) {
		this.pkcs12 = pkcs12;
		this.password = password;
		this.resource = resource;
	}

	public Collection<Object> decode() throws IOException, PasswordRequiredException {
		for (ContentInfo contentInfo : this.pkcs12.getContentInfos()) {
			if (contentInfo.getContentType().equals(PKCSObjectIdentifiers.encryptedData)) {
				PKCS12SafeBagFactory bagFactory = null;
				PKCSException decryptException = null;

				while (bagFactory == null) {
					try {
						bagFactory = new PKCS12SafeBagFactory(contentInfo, getInputDecryptorProvider(decryptException));
					} catch (PKCSException e) {
						decryptException = e;
					}
				}
				decodeBags(bagFactory.getSafeBags());
			} else {
				PKCS12SafeBagFactory bagFactory = new PKCS12SafeBagFactory(contentInfo);

				decodeBags(bagFactory.getSafeBags());
			}
		}
		return this.decoded;
	}

	private void decodeBags(PKCS12SafeBag[] bags) throws PasswordRequiredException {
		for (PKCS12SafeBag bag : bags) {
			Object bagValue = bag.getBagValue();

			if (bagValue instanceof X509CertificateHolder) {
				decodeCRTBag((X509CertificateHolder) bagValue, bag.getAttributes());
			} else if (bagValue instanceof PKCS8EncryptedPrivateKeyInfo) {
				decodeKeyBag((PKCS8EncryptedPrivateKeyInfo) bagValue, bag.getAttributes());
			} else if (bagValue instanceof PrivateKeyInfo) {
				decodeKeyBag((PrivateKeyInfo) bagValue, bag.getAttributes());
			} else {
				LOG.info(null, "Ignoring unexpected bag type: {0}", bagValue.getClass());
			}
		}
	}

	public void decodeCRTBag(X509CertificateHolder bagValue, Attribute[] bagAttributes) {
		try {
			X509Certificate crt = this.jcaConverter.getCertificate(bagValue);

			this.decoded.add(crt);
			for (Attribute bagAttribute : bagAttributes) {
				if (bagAttribute.getAttrType().equals(PKCS12SafeBag.localKeyIdAttribute)) {
					decodeKey(bagAttribute.getAttributeValues()[0], crt.getPublicKey());
					break;
				}
			}
		} catch (Exception e) {
			LOG.info(e, null, "Unable to decode CRT data from PKCS#12 bag");
		}
	}

	public void decodeKeyBag(PKCS8EncryptedPrivateKeyInfo bagValue, Attribute[] bagAttributes)
			throws PasswordRequiredException {
		PrivateKeyInfo keyInfo = null;
		PKCSException decryptException = null;

		while (keyInfo == null) {
			try {
				keyInfo = bagValue.decryptPrivateKeyInfo(getInputDecryptorProvider(decryptException));
			} catch (PKCSException e) {
				decryptException = e;
			}
		}
		decodeKeyBag(keyInfo, bagAttributes);
	}

	public void decodeKeyBag(PrivateKeyInfo bagValue, Attribute[] bagAttributes) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(bagValue.getPrivateKeyAlgorithm().getAlgorithm().getId(),
					BouncyCastleProvider.PROVIDER_NAME);
			PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(bagValue.getEncoded()));

			for (Attribute bagAttribute : bagAttributes) {
				if (bagAttribute.getAttrType().equals(PKCS12SafeBag.localKeyIdAttribute)) {
					decodeKey(bagAttribute.getAttributeValues()[0], privateKey);
					break;
				}
			}
		} catch (Exception e) {
			LOG.info(e, null, "Unable to decode key data from PKCS#12 bag");
		}
	}

	private InputDecryptorProvider getInputDecryptorProvider(PKCSException decryptException)
			throws PasswordRequiredException {
		if (this.password == null) {
			throw new PasswordRequiredException("Password required for PKCS#12 decoding");
		}
		if (this.inputDecryptorProvider == null || decryptException != null) {
			String passwordInput;

			if (this.inputDecryptorProvider == null) {
				passwordInput = this.password.queryPassword(this.resource);
			} else {
				passwordInput = this.password.requeryPassword(this.resource, decryptException);
			}
			if (passwordInput == null) {
				throw new PasswordRequiredException("Password input cancelled while decoding PKCS#12 data");
			}
			this.inputDecryptorProvider = this.inputDecrypterProviderBuilder.build(passwordInput.toCharArray());
		}
		return this.inputDecryptorProvider;
	}

	private void decodeKey(ASN1Encodable keyId, PublicKey publicKey) {
		Pair<PublicKey, PrivateKey> key = this.keyMap.get(keyId);

		if (key == null) {
			this.keyMap.put(keyId, new Pair<>(publicKey, null));
		} else if (key.getFirst() == null) {
			this.keyMap.put(keyId, new Pair<>(publicKey, key.getSecond()));
			this.decoded.add(new KeyPair(publicKey, key.getSecond()));
		}
	}

	private void decodeKey(ASN1Encodable keyId, PrivateKey privateKey) {
		Pair<PublicKey, PrivateKey> key = this.keyMap.get(keyId);

		if (key == null) {
			this.keyMap.put(keyId, new Pair<>(null, privateKey));
		} else if (key.getSecond() == null) {
			this.keyMap.put(keyId, new Pair<>(key.getFirst(), privateKey));
			this.decoded.add(new KeyPair(key.getFirst(), privateKey));
		}
	}

}
