/*
 * I18N resource strings
 *
 * Generated on 4/30/16 6:36 AM
 */
package de.carne.certmgr.store.provider;

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
	 * Resource key {@code STR_GENERATE_KEY}
	 * <p>
	 * Generate {0}&frasl;{1} key...
	 * </p>
	 */
	static final String STR_GENERATE_KEY = "STR_GENERATE_KEY";

	/**
	 * Resource string {@code STR_GENERATE_KEY}
	 * <p>
	 * Generate {0}&frasl;{1} key...
	 * </p>
	 */
	static String formatSTR_GENERATE_KEY(Object... arguments) {
		return format(STR_GENERATE_KEY, arguments);
	}

}
