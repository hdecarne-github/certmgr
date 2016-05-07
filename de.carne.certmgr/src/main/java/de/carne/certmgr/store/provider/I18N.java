/*
 * I18N resource strings
 *
 * Generated on 07.05.2016 08:43:41
 */
package de.carne.certmgr.store.provider;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Package localization resources.
 */
public final class I18N {

	/**
	 * The BUNDLE represented by this class.
	 */
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(I18N.class.getName());

	private static String format(String key, Object... arguments) {
		String pattern = BUNDLE.getString(key);

		return (arguments.length > 0 ? MessageFormat.format(pattern, arguments) : pattern);
	}

	/**
	 * Resource key {@code STR_GENERATE_KEY}
	 * <p>
	 * Generate {0}&frasl;{1} key...
	 * </p>
	 */
	public static final String STR_GENERATE_KEY = "STR_GENERATE_KEY";

	/**
	 * Resource string {@code STR_GENERATE_KEY}
	 * <p>
	 * Generate {0}&frasl;{1} key...
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_GENERATE_KEY(Object... arguments) {
		return format(STR_GENERATE_KEY, arguments);
	}

}
