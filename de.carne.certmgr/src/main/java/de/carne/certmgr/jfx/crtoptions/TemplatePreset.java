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

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import de.carne.certmgr.store.CertStore;
import de.carne.certmgr.store.CertStoreEntry;
import de.carne.certmgr.store.x509.DistributionPoint;
import de.carne.certmgr.store.x509.DistributionPointName;
import de.carne.certmgr.store.x509.ExtendedKeyUsage;
import de.carne.certmgr.store.x509.GeneralName;
import de.carne.certmgr.store.x509.GeneralNameType;
import de.carne.certmgr.store.x509.KeyUsage;
import de.carne.certmgr.store.x509.X509BasicConstraintsExtension;
import de.carne.certmgr.store.x509.X509CRLDistributionPointsExtension;
import de.carne.certmgr.store.x509.X509ExtendedKeyUsageExtension;
import de.carne.certmgr.store.x509.X509Extension;
import de.carne.certmgr.store.x509.X509KeyUsageExtension;
import de.carne.certmgr.store.x509.X509SubjectAlternativeNameExtension;
import de.carne.util.Strings;
import de.carne.util.logging.Log;

/**
 * A preset backed up by property file data.
 */
class TemplatePreset extends Preset {

	private static final Log LOG = new Log(TemplatePreset.class);

	private static final DateFormat SERIAL_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");

	/**
	 * Get the available template presets.
	 *
	 * @param store The store to get the template presets for.
	 * @return The available template presets.
	 */
	public static Collection<TemplatePreset> getPresets(CertStore store) {
		ArrayList<TemplatePreset> presets = new ArrayList<>();
		String presetsResource = TemplatePreset.class.getSimpleName() + "s.properties";
		Properties presetsProperties = new Properties();

		try (InputStream presetsStream = TemplatePreset.class.getResourceAsStream(presetsResource)) {
			presetsProperties.load(presetsStream);
		} catch (IOException e) {
			LOG.warning(e, null, "Unable to read presets from resource ''{0}''", presetsResource);
		}

		int presetIndex = 0;
		String presetName;

		while ((presetName = presetsProperties.getProperty("preset." + presetIndex)) != null) {
			String subjectDN = getSubjectDN(presetsProperties, presetIndex, store);
			boolean rootEntry = getRootEntryFlag(presetsProperties, presetIndex);
			X509BasicConstraintsExtension basicConstraints = getBasicConstraints(presetsProperties, presetIndex);
			X509KeyUsageExtension keyUsage = getKeyUsage(presetsProperties, presetIndex);
			X509ExtendedKeyUsageExtension extendedKeyUsage = getExtendedKeyUsage(presetsProperties, presetIndex);
			X509SubjectAlternativeNameExtension subjectAlternativeName = getSubjectAlternativeName(presetsProperties,
					presetIndex);
			X509CRLDistributionPointsExtension crlDistributionPoints = getCRLDistributionPoints(presetsProperties,
					presetIndex);
			ArrayList<X509Extension> extensions = new ArrayList<>();

			if (basicConstraints != null) {
				extensions.add(basicConstraints);
			}
			if (keyUsage != null) {
				extensions.add(keyUsage);
			}
			if (extendedKeyUsage != null) {
				extensions.add(extendedKeyUsage);
			}
			if (subjectAlternativeName != null) {
				extensions.add(subjectAlternativeName);
			}
			if (crlDistributionPoints != null) {
				extensions.add(crlDistributionPoints);
			}
			presets.add(new TemplatePreset(presetName, subjectDN, rootEntry, extensions));
			presetIndex++;
		}
		return presets;
	}

	private boolean rootEntry;

	/**
	 * Construct TemplatePreset.
	 *
	 * @param name The preset's name.
	 * @param subjectDN The preset's subject DN.
	 * @param issuer The preset's issuer.
	 * @param extensions The preset's extensions.
	 */
	TemplatePreset(String name, String subjectDN, boolean rootEntry, Collection<X509Extension> extensions) {
		super(name, subjectDN, null, extensions);
		this.rootEntry = rootEntry;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.crtoptions.Preset#getIssuer(de.carne.certmgr.store.CertStoreEntry)
	 */
	@Override
	public CertStoreEntry getIssuer(CertStoreEntry currentIssuer) {
		return (this.rootEntry ? null : currentIssuer);
	}

	private static String getSubjectDN(Properties presets, int presetIndex, CertStore store) {
		String subjectDNPattern = presets.getProperty(presetIndex + ".subjectDN");
		String serial = SERIAL_FORMAT.format(new Date());
		String storeName = store.getHome().getFileName().toString();

		return (subjectDNPattern != null ? MessageFormat.format(subjectDNPattern, serial, storeName) : null);
	}

	private static boolean getRootEntryFlag(Properties presets, int presetIndex) {
		String rootEntryString = presets.getProperty(presetIndex + ".rootEntry");

		return Boolean.valueOf(rootEntryString).booleanValue();
	}

	private static X509BasicConstraintsExtension getBasicConstraints(Properties presets, int presetIndex) {
		X509BasicConstraintsExtension basicConstraints = null;
		String tokenString = presets.getProperty(presetIndex + ".basicConstraints");

		if (Strings.notEmpty(tokenString)) {
			StringTokenizer tokens = new StringTokenizer(tokenString, ",");
			String criticalString = tokens.nextToken();
			String caString = tokens.nextToken();
			String pathLenConstraintString = tokens.nextToken();

			assert !tokens.hasMoreTokens();

			boolean critical = Boolean.valueOf(criticalString).booleanValue();
			boolean ca = Boolean.valueOf(caString).booleanValue();
			int pathLenConstraint = Integer.valueOf(pathLenConstraintString);

			basicConstraints = new X509BasicConstraintsExtension(critical, ca, pathLenConstraint);
		}
		return basicConstraints;
	}

	private static X509KeyUsageExtension getKeyUsage(Properties presets, int presetIndex) {
		X509KeyUsageExtension keyUsage = null;
		String tokenString = presets.getProperty(presetIndex + ".keyUsage");

		if (Strings.notEmpty(tokenString)) {

			StringTokenizer tokens = new StringTokenizer(tokenString, ",");
			String criticalString = tokens.nextToken();

			boolean critical = Boolean.valueOf(criticalString);

			keyUsage = new X509KeyUsageExtension(critical);
			while (tokens.hasMoreTokens()) {
				String usageString = tokens.nextToken();
				KeyUsage usage = KeyUsage.fromName(usageString);

				keyUsage.addUsage(usage);
			}
		}
		return keyUsage;
	}

	private static X509ExtendedKeyUsageExtension getExtendedKeyUsage(Properties presets, int presetIndex) {
		X509ExtendedKeyUsageExtension extendedKeyUsage = null;
		String tokenString = presets.getProperty(presetIndex + ".extendedKeyUsage");

		if (Strings.notEmpty(tokenString)) {
			StringTokenizer tokens = new StringTokenizer(tokenString, ",");
			String criticalString = tokens.nextToken();

			boolean critical = Boolean.valueOf(criticalString);

			extendedKeyUsage = new X509ExtendedKeyUsageExtension(critical);
			while (tokens.hasMoreTokens()) {
				String usageString = tokens.nextToken();
				ExtendedKeyUsage usage = ExtendedKeyUsage.fromName(usageString);

				extendedKeyUsage.addUsage(usage);
			}
		}
		return extendedKeyUsage;
	}

	private static X509SubjectAlternativeNameExtension getSubjectAlternativeName(Properties presets, int presetIndex) {
		X509SubjectAlternativeNameExtension subjectAlternativeName = null;
		int nameIndex = 0;
		String tokenString;

		while (Strings
				.notEmpty(tokenString = presets.getProperty(presetIndex + ".subjectAlternativeName." + nameIndex))) {
			if (subjectAlternativeName == null) {
				subjectAlternativeName = new X509SubjectAlternativeNameExtension(false);
			}

			int splitIndex = tokenString.indexOf(':');
			GeneralNameType nameType = GeneralNameType.valueOf(tokenString.substring(0, splitIndex));
			GeneralName name = new GeneralName(nameType, tokenString.substring(splitIndex + 1));

			subjectAlternativeName.addName(name);
			nameIndex++;
		}
		return subjectAlternativeName;
	}

	private static X509CRLDistributionPointsExtension getCRLDistributionPoints(Properties presets, int presetIndex) {
		X509CRLDistributionPointsExtension crlDistributionPoints = null;
		DistributionPointName distributionPointName = null;
		int nameIndex = 0;
		String tokenString;

		while (Strings.notEmpty(tokenString = presets.getProperty(presetIndex + ".crlDistributionPoints." + nameIndex))) {
			if (distributionPointName == null) {
				crlDistributionPoints = new X509CRLDistributionPointsExtension(false);

				DistributionPoint distributionPoint = new DistributionPoint();

				distributionPointName = new DistributionPointName();
				distributionPoint.addName(distributionPointName);
				crlDistributionPoints.addDistributionPoint(distributionPoint);
			}

			int splitIndex = tokenString.indexOf(':');
			GeneralNameType nameType = GeneralNameType.valueOf(tokenString.substring(0, splitIndex));
			GeneralName name = new GeneralName(nameType, tokenString.substring(splitIndex + 1));

			distributionPointName.addName(name);
			nameIndex++;
		}
		return crlDistributionPoints;
	}

}
