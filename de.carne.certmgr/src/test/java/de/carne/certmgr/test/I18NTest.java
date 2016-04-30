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
package de.carne.certmgr.test;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Validate the I18N resources.
 */
public class I18NTest {

	private static final Locale DEFAULT_LOCALE = Locale.US;

	private static final HashSet<Locale> EXTRA_LOCALES = new HashSet<>();

	static {
		EXTRA_LOCALES.add(Locale.GERMANY);
	}

	private static final HashSet<String> BUNDLE_BASE_NAMES = new HashSet<>();

	static {
		BUNDLE_BASE_NAMES.add("de.carne.certmgr.jfx.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.certmgr.jfx.certexport.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.certmgr.jfx.certimport.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.certmgr.jfx.crloptions.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.certmgr.jfx.crtoptions.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.certmgr.jfx.dneditor.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.certmgr.jfx.entryoptions.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.certmgr.jfx.help.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.certmgr.jfx.passwordprompt.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.certmgr.jfx.storemanager.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.certmgr.jfx.storeoptions.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.certmgr.store.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.certmgr.store.provider.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.certmgr.store.provider.bouncycastle.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.jfx.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.jfx.aboutinfo.I18N");
		BUNDLE_BASE_NAMES.add("de.carne.jfx.messagebox.I18N");
	}

	private static final Hashtable<String, String> FXML_RESOURCES = new Hashtable<>();

	static {
		FXML_RESOURCES.put("/de/carne/certmgr/jfx/certexport/CertExport.fxml", "de.carne.certmgr.jfx.certexport.I18N");
		FXML_RESOURCES.put("/de/carne/certmgr/jfx/certimport/CertImport.fxml", "de.carne.certmgr.jfx.certimport.I18N");
		FXML_RESOURCES.put("/de/carne/certmgr/jfx/crloptions/CRLOptions.fxml", "de.carne.certmgr.jfx.crloptions.I18N");
		FXML_RESOURCES.put("/de/carne/certmgr/jfx/crtoptions/CRTOptions.fxml", "de.carne.certmgr.jfx.crtoptions.I18N");
		FXML_RESOURCES.put("/de/carne/certmgr/jfx/dneditor/DNEditor.fxml", "de.carne.certmgr.jfx.dneditor.I18N");
		FXML_RESOURCES.put("/de/carne/certmgr/jfx/entryoptions/EntryOptions.fxml",
				"de.carne.certmgr.jfx.entryoptions.I18N");
		FXML_RESOURCES.put("/de/carne/certmgr/jfx/help/Help.fxml", "de.carne.certmgr.jfx.help.I18N");
		FXML_RESOURCES.put("/de/carne/certmgr/jfx/passwordprompt/PasswordPrompt.fxml",
				"de.carne.certmgr.jfx.passwordprompt.I18N");
		FXML_RESOURCES.put("/de/carne/certmgr/jfx/passwordprompt/NewPasswordPrompt.fxml",
				"de.carne.certmgr.jfx.passwordprompt.I18N");
		FXML_RESOURCES.put("/de/carne/certmgr/jfx/storemanager/StoreManager.fxml",
				"de.carne.certmgr.jfx.storemanager.I18N");
		FXML_RESOURCES.put("/de/carne/certmgr/jfx/storeoptions/StoreOptions.fxml",
				"de.carne.certmgr.jfx.storeoptions.I18N");
		FXML_RESOURCES.put("/de/carne/jfx/aboutinfo/AboutInfo.fxml", "de.carne.jfx.aboutinfo.I18N");
		FXML_RESOURCES.put("/de/carne/jfx/messagebox/MessageBox.fxml", "de.carne.jfx.messagebox.I18N");
	}

	/**
	 * Validate whether all bundles have the same keys.
	 */
	@Test
	public void testI18NBundleKeys() {
		for (String baseName : BUNDLE_BASE_NAMES) {
			System.out.print("I18N: " + baseName + " > " + DEFAULT_LOCALE);

			ResourceBundle defaultBundle = getBundle(baseName, DEFAULT_LOCALE);
			Set<String> defaultKeys = defaultBundle.keySet();

			for (Locale extraLocale : EXTRA_LOCALES) {
				System.out.print(" + " + extraLocale);

				ResourceBundle extraBundle = getBundle(baseName, extraLocale);

				// Check whether all default bundle keys are also defined in the
				// current extra bundle
				for (String key : defaultKeys) {
					String defaultString = getString(defaultBundle, key);
					String extraString = getString(extraBundle, key);

					MessageFormat defaultFormat = new MessageFormat(defaultString);
					MessageFormat extraFormat = new MessageFormat(extraString);

					int defaultFormatCount = defaultFormat.getFormats().length;
					int extraFormatCount = extraFormat.getFormats().length;

					Assert.assertTrue("Format count mismatch " + defaultFormatCount + " != " + extraFormatCount
							+ " for key '" + key + "' in resource bundle '" + extraBundle.getBaseBundleName()
							+ "' for locale " + extraBundle.getLocale(), defaultFormatCount == extraFormatCount);
				}

				// Check whether there are no additional keys in the extra
				// bundle
				for (String key : extraBundle.keySet()) {
					Assert.assertTrue("Extra key '" + key + "' defined in resource bundle '"
							+ extraBundle.getBaseBundleName() + "' for locale " + extraBundle.getLocale(),
							defaultKeys.contains(key));
				}
			}
			System.out.println();
		}
	}

	/**
	 * Validate whether all FXML files are referencing valid resource keys.
	 */
	@Test
	public void testFXMLResourceKeys() {
		SAXParserFactory spf = SAXParserFactory.newInstance();

		for (Map.Entry<String, String> fxmlResource : FXML_RESOURCES.entrySet()) {
			String fxml = fxmlResource.getKey();
			String bundle = fxmlResource.getValue();

			System.out.println("I18N: " + fxml + " + " + bundle);
			try (InputStream fxmlStream = getClass().getResourceAsStream(fxml)) {
				SAXParser sp = spf.newSAXParser();

				sp.parse(fxmlStream, new FXMLDefaultHandler(fxml, bundle) {

					@Override
					public void startElement(String uri, String localName, String qName, Attributes attributes)
							throws SAXException {
						testFXMLResourcesKeysHelper(getFxml(), getBundle(), attributes);
					}

				});
			} catch (Exception e) {
				System.err.println("Processing error: " + fxml);
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		}
	}

	void testFXMLResourcesKeysHelper(String fxml, String bundleBaseName, Attributes attributes) {
		for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
			String name = attributes.getQName(attributeIndex);

			if ("text".equals(name)) {
				String value = attributes.getValue(attributeIndex);

				if (value.startsWith("%")) {
					String key = value.substring(1);
					ResourceBundle bundle = ResourceBundle.getBundle(bundleBaseName);

					Assert.assertTrue("Key '" + key + "' not defined in resource bundle '" + bundle.getBaseBundleName()
							+ "' for locale " + bundle.getLocale(), bundle.keySet().contains(key));
				}
			}
		}
	}

	private ResourceBundle getBundle(String baseName, Locale locale) {
		ResourceBundle bundle = null;

		try {
			bundle = ResourceBundle.getBundle(baseName, locale);
		} catch (MissingResourceException e) {
			Assert.fail("Resource bundle '" + baseName + "' for locale " + locale + " not found");
		}
		return bundle;
	}

	private String getString(ResourceBundle bundle, String key) {
		String string = null;

		try {
			string = bundle.getString(key);
		} catch (MissingResourceException e) {
			Assert.fail("Key '" + key + "' not defined in resource bundle '" + bundle.getBaseBundleName()
					+ "' for locale " + bundle.getLocale());
		}
		return string;
	}

	private class FXMLDefaultHandler extends DefaultHandler {

		private String fxml;

		private String bundle;

		FXMLDefaultHandler(String fxml, String bundle) {
			this.fxml = fxml;
			this.bundle = bundle;
		}

		public String getFxml() {
			return this.fxml;
		}

		public String getBundle() {
			return this.bundle;
		}

	}

}
