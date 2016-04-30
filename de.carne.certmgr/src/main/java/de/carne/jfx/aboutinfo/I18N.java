/*
 * I18N resource strings
 *
 * Generated on 4/30/16 6:36 AM
 */
package de.carne.jfx.aboutinfo;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Package localization resources.
 */
final class I18N {

	static final ResourceBundle BUNDLE = ResourceBundle.getBundle(I18N.class.getName());

	static String format(String key, Object... arguments) {
		String pattern = BUNDLE.getString(key);

		return (arguments.length > 0 ? MessageFormat.format(pattern, arguments) : pattern);
	}

	/**
	 * Resource key {@code STR_ABOUTINFO_VERSION}
	 * <p>
	 * {0} - {1} - v{2} ({3})
	 * </p>
	 */
	static final String STR_ABOUTINFO_VERSION = "STR_ABOUTINFO_VERSION";

	/**
	 * Resource string {@code STR_ABOUTINFO_VERSION}
	 * <p>
	 * {0} - {1} - v{2} ({3})
	 * </p>
	 */
	static String formatSTR_ABOUTINFO_VERSION(Object... arguments) {
		return format(STR_ABOUTINFO_VERSION, arguments);
	}

	/**
	 * Resource key {@code STR_ABOUTINFO_TITLE}
	 * <p>
	 * About {0}
	 * </p>
	 */
	static final String STR_ABOUTINFO_TITLE = "STR_ABOUTINFO_TITLE";

	/**
	 * Resource string {@code STR_ABOUTINFO_TITLE}
	 * <p>
	 * About {0}
	 * </p>
	 */
	static String formatSTR_ABOUTINFO_TITLE(Object... arguments) {
		return format(STR_ABOUTINFO_TITLE, arguments);
	}

}
