/*
 * Copyright (c) 2015-2020 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.jfx.certoptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.Exceptions;
import de.carne.boot.logging.Log;
import de.carne.boot.prefs.FilePreferencesFactory;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.x500.X500Names;
import de.carne.certmgr.certs.x509.AuthorityKeyIdentifierExtensionData;
import de.carne.certmgr.certs.x509.CRLDistributionPointsExtensionData;
import de.carne.certmgr.certs.x509.DirectoryName;
import de.carne.certmgr.certs.x509.DistributionPoint;
import de.carne.certmgr.certs.x509.DistributionPointName;
import de.carne.certmgr.certs.x509.GeneralName;
import de.carne.certmgr.certs.x509.GeneralNames;
import de.carne.certmgr.certs.x509.KeyHelper;
import de.carne.certmgr.certs.x509.StringName;
import de.carne.certmgr.certs.x509.SubjectAlternativeNameExtensionData;
import de.carne.certmgr.certs.x509.SubjectKeyIdentifierExtensionData;
import de.carne.certmgr.certs.x509.X509ExtensionData;
import de.carne.certmgr.util.BooleanPreference;
import de.carne.util.Strings;

/**
 * Class used for handling cert option defaults and templates.
 */
final class CertOptionsTemplates {

	private static final Log LOG = new Log();

	private static final Set<String> INVALID_PRESET_EXTENSIONS = new HashSet<>();

	static {
		INVALID_PRESET_EXTENSIONS.add(SubjectKeyIdentifierExtensionData.OID);
		INVALID_PRESET_EXTENSIONS.add(AuthorityKeyIdentifierExtensionData.OID);
	}

	private CertOptionsTemplates() {
		// Make sure this class is not instantiated from outside
	}

	private static final DateFormat DEFAULT_SERIAL_FORMAT = new SimpleDateFormat("yyyyMMddhhmm");

	public static String defaultSerial() {
		return DEFAULT_SERIAL_FORMAT.format(new Date());
	}

	public static String defaultDNInput(String aliasInput, String storeName, String serial) {
		return CertOptionsTemplatesI18N.strDefaultDn(aliasInput, storeName, serial);
	}

	private static final String DN_ALIAS_KEY = "CN";
	private static final String DN_STORE_KEY = "OU";
	private static final String DN_SERIAL_KEY = "SERIALNUMBER";

	static class MergeParams {

		private String[] aliasInput = new String[0];
		private String[] storeName = new String[0];
		private String[] serial = new String[0];

		public MergeParams aliasInput(String oldAliasInput, String newAliasInput) {
			this.aliasInput = new String[] { oldAliasInput, newAliasInput };
			return this;
		}

		public MergeParams storeName(String oldStoreName, String newStoreName) {
			this.storeName = new String[] { oldStoreName, newStoreName };
			return this;
		}

		public MergeParams storeName(String newStoreName) {
			this.storeName = new String[] { null, newStoreName };
			return this;
		}

		public MergeParams serial(String oldSerial, String newSerial) {
			this.serial = new String[] { oldSerial, newSerial };
			return this;
		}

		public MergeParams serial(String newSerial) {
			this.serial = new String[] { null, newSerial };
			return this;
		}

		public String applyToDN(String dn) {
			String mergedDNInput = dn;

			try {
				LdapName oldDN = new LdapName(Strings.safeTrim(dn));
				List<Rdn> oldRdns = oldDN.getRdns();
				List<Rdn> newRdns = new ArrayList<>(oldRdns.size());

				for (Rdn oldRdn : oldRdns) {
					if (this.aliasInput.length == 2 && DN_ALIAS_KEY.equals(oldRdn.getType())) {
						if (isRdnMergeable(this.aliasInput[0], oldRdn)) {
							newRdns.add(new Rdn(oldRdn.getType(), safeRdnValue(this.aliasInput[1])));
							continue;
						}
					}
					if (this.storeName.length == 2 && DN_STORE_KEY.equals(oldRdn.getType())) {
						if (this.storeName[0] == null) {
							this.storeName[0] = String.valueOf(oldRdn.getValue());
						}
						if (isRdnMergeable(this.storeName[0], oldRdn)) {
							newRdns.add(new Rdn(oldRdn.getType(), safeRdnValue(this.storeName[1])));
							continue;
						}
					}
					if (this.serial.length == 2 && DN_SERIAL_KEY.equals(oldRdn.getType())) {
						if (this.serial[0] == null) {
							this.serial[0] = String.valueOf(oldRdn.getValue());
						}
						if (isRdnMergeable(this.serial[0], oldRdn)) {
							newRdns.add(new Rdn(oldRdn.getType(), safeRdnValue(this.serial[1])));
							continue;
						}
					}
					newRdns.add(oldRdn);
				}

				LdapName newDN = new LdapName(newRdns);

				mergedDNInput = newDN.toString();
			} catch (InvalidNameException e) {
				Exceptions.ignore(e);
			}
			return mergedDNInput;
		}

		private boolean isRdnMergeable(String oldValue, Rdn oldRdn) {
			return Strings.isEmpty(oldValue) || Objects.equals(oldValue, oldRdn.getValue());
		}

		private String safeRdnValue(String newValue) {
			return (Strings.notEmpty(newValue) ? newValue : "?");
		}

		public CertOptionsPreset applyToPreset(CertOptionsPreset preset) {
			String mergedAliasInput = (this.aliasInput.length == 2 ? this.aliasInput[1] : preset.aliasInput());
			String mergedDNInput = applyToDN(preset.dnInput());
			CertOptionsPreset mergedPreset = new CertOptionsPreset(mergedAliasInput, mergedDNInput);

			mergedPreset.setKeyAlg(preset.getKeyAlg());
			mergedPreset.setKeySize(preset.getKeySize());
			for (X509ExtensionData extension : preset.getExtensions()) {
				X509ExtensionData mergedExtension;

				if (SubjectAlternativeNameExtensionData.OID.equals(extension.oid())) {
					mergedExtension = applyToSubjectAlternativeNameExtension(
							(SubjectAlternativeNameExtensionData) extension);
				} else if (CRLDistributionPointsExtensionData.OID.equals(extension.oid())) {
					mergedExtension = applyToCRLDistributionPointsExtension(
							(CRLDistributionPointsExtensionData) extension);
				} else {
					mergedExtension = extension;
				}
				mergedPreset.addExtension(mergedExtension);
			}
			return mergedPreset;
		}

		private SubjectAlternativeNameExtensionData applyToSubjectAlternativeNameExtension(
				SubjectAlternativeNameExtensionData extension) {
			GeneralNames mergedNames = applyToGeneralNames(extension.getGeneralNames());

			return new SubjectAlternativeNameExtensionData(extension.getCritical(), mergedNames);
		}

		private CRLDistributionPointsExtensionData applyToCRLDistributionPointsExtension(
				CRLDistributionPointsExtensionData extension) {
			CRLDistributionPointsExtensionData mergedExtension = new CRLDistributionPointsExtensionData(
					extension.getCritical());

			for (DistributionPoint dp : extension) {
				DistributionPointName dpName = dp.getName();

				if (dpName != null) {
					GeneralNames dpFullName = dpName.getFullName();

					if (dpFullName != null) {
						GeneralNames mergedDPFullName = applyToGeneralNames(dpFullName);
						DistributionPointName mergedDPName = new DistributionPointName(mergedDPFullName);
						DistributionPoint mergedDP = new DistributionPoint(mergedDPName);

						mergedDP.setReasons(dp.getReasons());
						mergedExtension.addDistributionPoint(mergedDP);
					}

					X500Principal dpRelativeName = dpName.getRelativeName();

					if (dpRelativeName != null) {
						X500Principal mergedDPRelativeName = applyToX500Principal(dpRelativeName);
						DistributionPointName mergedDPName = new DistributionPointName(mergedDPRelativeName);
						DistributionPoint mergedDP = new DistributionPoint(mergedDPName);

						mergedDP.setReasons(dp.getReasons());
						mergedExtension.addDistributionPoint(mergedDP);
					}
				}

				GeneralNames dpCRLIssuer = dp.getCRLIssuer();

				if (dpCRLIssuer != null) {
					GeneralNames mergedDPCRLIssuer = applyToGeneralNames(dpCRLIssuer);

					DistributionPoint mergedDP = new DistributionPoint(mergedDPCRLIssuer);
					mergedDP.setReasons(dp.getReasons());
					mergedExtension.addDistributionPoint(mergedDP);
				}
			}
			return mergedExtension;
		}

		private GeneralNames applyToGeneralNames(GeneralNames names) {
			GeneralNames mergedNames = new GeneralNames();

			for (GeneralName name : names) {
				GeneralName mergedName;

				if (name instanceof StringName) {
					StringName stringName = (StringName) name;

					mergedName = new StringName(name.getType(),
							stringName.getNameString().replace(this.storeName[0], this.storeName[1]));
				} else if (name instanceof DirectoryName) {
					DirectoryName directoryName = (DirectoryName) name;

					mergedName = new DirectoryName(applyToX500Principal(directoryName.getDirectoryName()));
				} else {
					mergedName = name;
				}
				mergedNames.addName(mergedName);
			}
			return mergedNames;
		}

		private X500Principal applyToX500Principal(X500Principal principal) {
			return new X500Principal(applyToDN(principal.getName()));
		}

	}

	public static MergeParams merge() {
		return new MergeParams();
	}

	private static final Preferences TEMPLATE_STORE = Preferences.systemNodeForPackage(CertOptionsTemplates.class);
	private static final BooleanPreference TEMPLATE_STORE_INITIALIZED = new BooleanPreference(TEMPLATE_STORE,
			"initialized");

	static class Template extends CertOptionsPreset {

		static final String KEY_NAME = "name";
		static final String KEY_ALIAS = "alias";
		static final String KEY_DN = "dn";
		static final String KEY_KEYALG = "keyAlg";
		static final String KEY_KEYSIZE = "keySize";
		static final String KEY_EXTENSION_OID = "oid";
		static final String KEY_EXTENSION_CRITICAL = "critical";
		static final String KEY_EXTENSION_DATA = "data";

		private String name;

		Template(String name, CertOptionsPreset preset) {
			super(preset);
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public CertOptionsPreset merge(String aliasInput, String storeName, String serial) {
			return CertOptionsTemplates.merge().aliasInput(aliasInput(), aliasInput).storeName(storeName).serial(serial)
					.applyToPreset(this);
		}

		@Override
		public String toString() {
			return this.name + " (" + dnInput() + ")";
		}

	}

	public static Template wrap(UserCertStoreEntry storeEntry) {
		String dnInput = X500Names.toString(storeEntry.dn());
		String aliasInput = dnInput;

		try {
			LdapName dn = new LdapName(aliasInput);
			for (Rdn rdn : dn.getRdns()) {
				if (DN_ALIAS_KEY.equals(rdn.getType())) {
					aliasInput = String.valueOf(rdn.getValue());
					break;
				}
			}
		} catch (InvalidNameException e) {
			Exceptions.ignore(e);
		}

		CertOptionsPreset preset = new CertOptionsPreset(aliasInput, dnInput);

		try {
			if (storeEntry.hasCRT()) {
				X509Certificate crt = storeEntry.getCRT();
				PublicKey publicKey = crt.getPublicKey();

				preset.setKeyAlg(KeyHelper.getKeyAlg(publicKey));
				preset.setKeySize(KeyHelper.getKeySize(publicKey));

				Set<String> criticalExtensionOIDs = crt.getCriticalExtensionOIDs();

				if (criticalExtensionOIDs != null) {
					for (String criticalExtensionOID : criticalExtensionOIDs) {
						if (!INVALID_PRESET_EXTENSIONS.contains(criticalExtensionOID)) {
							X509ExtensionData criticalExtension = X509ExtensionData.decode(criticalExtensionOID, true,
									crt.getExtensionValue(criticalExtensionOID));

							preset.addExtension(criticalExtension);
						}
					}
				}

				Set<String> nonCriticalExtensionOIDs = crt.getNonCriticalExtensionOIDs();

				if (nonCriticalExtensionOIDs != null) {
					for (String nonCriticalExtensionOID : nonCriticalExtensionOIDs) {
						if (!INVALID_PRESET_EXTENSIONS.contains(nonCriticalExtensionOID)) {
							X509ExtensionData nonCriticalExtension = X509ExtensionData.decode(nonCriticalExtensionOID,
									false, crt.getExtensionValue(nonCriticalExtensionOID));

							preset.addExtension(nonCriticalExtension);
						}
					}
				}
			}
		} catch (IOException e) {
			Exceptions.warn(e);
		}
		return new Template(preset.aliasInput(), preset);
	}

	public static List<Template> load() {
		List<Template> templates = new ArrayList<>();

		try {
			Preferences templateStore;

			if (TEMPLATE_STORE_INITIALIZED.getBoolean(false)) {
				templateStore = TEMPLATE_STORE;
			} else {
				try (InputStream standardTemplatesStream = getStandardTemplatesUrl().openStream()) {
					Properties standardTemplates = new Properties();

					standardTemplates.load(standardTemplatesStream);
					templateStore = FilePreferencesFactory.customRoot(standardTemplates);
				}
			}

			String[] templateNodeNames = templateStore.childrenNames();

			Arrays.sort(templateNodeNames);
			for (String templateNodeName : templateNodeNames) {
				Preferences templateNode = templateStore.node(templateNodeName);
				@Nullable Template template = loadTemplate(templateNode);

				if (template != null) {
					templates.add(template);
				}
			}
		} catch (IOException | BackingStoreException e) {
			Exceptions.warn(e);
		}
		return templates;
	}

	private static URL getStandardTemplatesUrl() throws IOException {
		String standardTemplatesResourceName = CertOptionsTemplates.class.getSimpleName() + ".properties";
		URL standardTemplatesURL = CertOptionsTemplates.class.getResource(standardTemplatesResourceName);

		if (standardTemplatesURL == null) {
			throw new FileNotFoundException(
					"Unable to access standard template resource: " + standardTemplatesResourceName);
		}
		return standardTemplatesURL;
	}

	@Nullable
	private static Template loadTemplate(Preferences templateNode) {
		@Nullable Template template = null;

		@Nullable String name = templateNode.get(Template.KEY_NAME, null);
		@Nullable String aliasInput = templateNode.get(Template.KEY_ALIAS, null);
		@Nullable String dnInput = templateNode.get(Template.KEY_DN, null);

		if (Strings.notEmpty(name) && Strings.notEmpty(aliasInput) && Strings.notEmpty(dnInput)) {
			assert name != null;
			assert aliasInput != null;
			assert dnInput != null;

			template = new Template(name, new CertOptionsPreset(aliasInput, dnInput));

			@Nullable String keyAlg = templateNode.get(Template.KEY_KEYALG, null);

			if (keyAlg != null) {
				template.setKeyAlg(KeyHelper.getKeyAlg(keyAlg));
			}

			int keySize = templateNode.getInt(Template.KEY_KEYSIZE, 0);

			if (keySize != 0) {
				template.setKeySize(keySize);
			}

			try {
				String[] extensionNodeNames = templateNode.childrenNames();

				for (String extensionNodeName : extensionNodeNames) {
					Preferences extensionNode = templateNode.node(extensionNodeName);
					@Nullable String oid = extensionNode.get(Template.KEY_EXTENSION_OID, null);
					boolean criticial = extensionNode.getBoolean(Template.KEY_EXTENSION_CRITICAL, false);
					byte @Nullable [] data = extensionNode.getByteArray(Template.KEY_EXTENSION_DATA, null);

					if (Strings.notEmpty(oid) && data != null) {
						assert oid != null;

						template.addExtension(X509ExtensionData.decode(oid, criticial, data));
					} else {
						LOG.warning("Ignoring incomplete extension node ''{0}''", extensionNode.absolutePath());
					}
				}
			} catch (BackingStoreException | IOException e) {
				LOG.warning(e, "Ignoring inaccessible extension data for template node ''{0}''",
						templateNode.absolutePath());
			}
		} else {
			LOG.warning("Ignoring incomplete template node ''{0}''", templateNode.absolutePath());
		}
		return template;
	}

	public static void store(List<Template> templates) throws IOException, BackingStoreException {
		for (String templateNodeName : TEMPLATE_STORE.childrenNames()) {
			TEMPLATE_STORE.node(templateNodeName).removeNode();
		}
		// Flush to really remove nodes (an be able to re-create them below)
		TEMPLATE_STORE.sync();

		int templateIndex = 0;

		for (Template template : templates) {
			Preferences templateNode = TEMPLATE_STORE.node("template" + templateIndex);

			storeTemplate(templateNode, template);
			templateIndex++;
		}
		TEMPLATE_STORE_INITIALIZED.putBoolean(true);
		TEMPLATE_STORE.sync();
	}

	private static void storeTemplate(Preferences templateNode, Template template) throws IOException {
		templateNode.put(Template.KEY_NAME, template.getName());
		templateNode.put(Template.KEY_ALIAS, template.aliasInput());
		templateNode.put(Template.KEY_DN, template.dnInput());

		KeyPairAlgorithm keyAlg = template.getKeyAlg();

		if (keyAlg != null) {
			templateNode.put(Template.KEY_KEYALG, keyAlg.algorithm());
		}

		Integer keySize = template.getKeySize();

		if (keySize != null) {
			templateNode.putInt(Template.KEY_KEYSIZE, keySize);
		}

		int extensionIndex = 0;

		for (X509ExtensionData extension : template.getExtensions()) {
			Preferences extensionNode = templateNode.node("extension" + extensionIndex);

			extensionNode.put(Template.KEY_EXTENSION_OID, extension.oid());
			extensionNode.putBoolean(Template.KEY_EXTENSION_CRITICAL, extension.getCritical());
			extensionNode.putByteArray(Template.KEY_EXTENSION_DATA, extension.getEncoded());
			extensionIndex++;
		}
	}

}
