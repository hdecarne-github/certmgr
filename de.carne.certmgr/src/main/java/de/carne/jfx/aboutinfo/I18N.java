/*
 * I18N resource strings
 *
 * Generated on 07.05.2016 08:43:41
 */
package de.carne.jfx.aboutinfo;

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
	 * Resource key {@code STR_ABOUTINFO_VERSION}
	 * <p>
	 * {0} - {1} - v{2} ({3})
	 * </p>
	 */
	public static final String STR_ABOUTINFO_VERSION = "STR_ABOUTINFO_VERSION";

	/**
	 * Resource string {@code STR_ABOUTINFO_VERSION}
	 * <p>
	 * {0} - {1} - v{2} ({3})
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ABOUTINFO_VERSION(Object... arguments) {
		return format(STR_ABOUTINFO_VERSION, arguments);
	}

	/**
	 * Resource key {@code STR_ABOUTINFO_TITLE}
	 * <p>
	 * About {0}
	 * </p>
	 */
	public static final String STR_ABOUTINFO_TITLE = "STR_ABOUTINFO_TITLE";

	/**
	 * Resource string {@code STR_ABOUTINFO_TITLE}
	 * <p>
	 * About {0}
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ABOUTINFO_TITLE(Object... arguments) {
		return format(STR_ABOUTINFO_TITLE, arguments);
	}

}
