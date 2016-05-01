/*
 * I18N resource strings
 *
 * Generated on Apr 30, 2016 10:35:47 PM
 */
package de.carne.certmgr.jfx.dneditor;

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
	 * Resource key {@code STR_DN_EDITOR_TITLE}
	 * <p>
	 * Edit DN
	 * </p>
	 */
	public static final String STR_DN_EDITOR_TITLE = "STR_DN_EDITOR_TITLE";

	/**
	 * Resource string {@code STR_DN_EDITOR_TITLE}
	 * <p>
	 * Edit DN
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_DN_EDITOR_TITLE(Object... arguments) {
		return format(STR_DN_EDITOR_TITLE, arguments);
	}

}
