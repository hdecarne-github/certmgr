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
package de.carne.certmgr.jfx.crtoptions;

import java.util.Collection;

import de.carne.certmgr.store.CertStoreEntry;
import de.carne.certmgr.store.x509.X509BasicConstraintsExtension;
import de.carne.certmgr.store.x509.X509CRLDistributionPointsExtension;
import de.carne.certmgr.store.x509.X509ExtendedKeyUsageExtension;
import de.carne.certmgr.store.x509.X509Extension;
import de.carne.certmgr.store.x509.X509KeyUsageExtension;
import de.carne.certmgr.store.x509.X509SubjectAlternativeNameExtension;

/**
 * Base class for defining a preset of CRT options.
 */
abstract class Preset {

	private String name;
	private String subjectDN;
	private CertStoreEntry issuer;
	private X509BasicConstraintsExtension basicConstraints = null;
	private X509KeyUsageExtension keyUsage = null;
	private X509ExtendedKeyUsageExtension extendedKeyUsage = null;
	private X509SubjectAlternativeNameExtension subjectAlternativeName = null;
	private X509CRLDistributionPointsExtension crlDistributionPoints = null;

	/**
	 * Construct Preset.
	 *
	 * @param name The preset's name.
	 * @param subjectDN The preset's subject DN.
	 * @param issuer The preset's issuer.
	 * @param extensions The preset's extensions.
	 */
	protected Preset(String name, String subjectDN, CertStoreEntry issuer, Collection<X509Extension> extensions) {
		this.name = name;
		this.subjectDN = subjectDN;
		this.issuer = issuer;
		for (X509Extension extension : extensions) {
			if (extension instanceof X509BasicConstraintsExtension) {
				this.basicConstraints = (X509BasicConstraintsExtension) extension;
			} else if (extension instanceof X509KeyUsageExtension) {
				this.keyUsage = (X509KeyUsageExtension) extension;
			} else if (extension instanceof X509ExtendedKeyUsageExtension) {
				this.extendedKeyUsage = (X509ExtendedKeyUsageExtension) extension;
			} else if (extension instanceof X509SubjectAlternativeNameExtension) {
				this.subjectAlternativeName = (X509SubjectAlternativeNameExtension) extension;
			} else if (extension instanceof X509CRLDistributionPointsExtension) {
				this.crlDistributionPoints = (X509CRLDistributionPointsExtension) extension;
			}
		}
	}

	/**
	 * Get the preset's name.
	 *
	 * @return The preset's name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the preset's subject DN
	 *
	 * @param currentSubjectDN The current subject DN.
	 * @return The subject DN to use.
	 */
	public String getSubjectDN(String currentSubjectDN) {
		return this.subjectDN;
	}

	/**
	 * Get the preset's issuer.
	 *
	 * @param currentIssuer The current issuer.
	 * @return The issuer to use.
	 */
	public CertStoreEntry getIssuer(CertStoreEntry currentIssuer) {
		return this.issuer;
	}

	/**
	 * Get the preset's BasicConstraints extension.
	 *
	 * @param currentBasicConstraints The current extension.
	 * @return The extension to use.
	 */
	public X509BasicConstraintsExtension getBasicConstraints(X509BasicConstraintsExtension currentBasicConstraints) {
		return this.basicConstraints;
	}

	/**
	 * Get the preset's KeyUsage extension.
	 *
	 * @param currentKeyUsage The current extension.
	 * @return The extension to use.
	 */
	public X509KeyUsageExtension getKeyUsage(X509KeyUsageExtension currentKeyUsage) {
		return this.keyUsage;
	}

	/**
	 * Get the preset's ExtendedKeyUsage extension.
	 *
	 * @param currentExtendedKeyUsage The current extension.
	 * @return The extension to use.
	 */
	public X509ExtendedKeyUsageExtension getExtendedKeyUsage(X509ExtendedKeyUsageExtension currentExtendedKeyUsage) {
		return this.extendedKeyUsage;
	}

	/**
	 * Get the preset's SubjectAlternativeName extension.
	 *
	 * @param currentSubjectAlternativeName The current extension.
	 * @return The extension to use.
	 */
	public X509SubjectAlternativeNameExtension getSubjectAlternativeName(
			X509SubjectAlternativeNameExtension currentSubjectAlternativeName) {
		return this.subjectAlternativeName;
	}

	/**
	 * Get the preset's CRLDistributionPoints extension.
	 *
	 * @param currentCRLDistributionPoints The current extension.
	 * @return The extension to use.
	 */
	public X509CRLDistributionPointsExtension getCRLDistributionPoints(
			X509CRLDistributionPointsExtension currentCRLDistributionPoints) {
		return this.crlDistributionPoints;
	}

}
