/*
 * I18N resource strings
 *
 * Generated on 07.05.2016 08:43:41
 */
package de.carne.certmgr.jfx.entryoptions;

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
	 * Resource key {@code STR_NO_ALIAS_MESSAGE}
	 * <p>
	 * Please enter a new name for the certificate entry.
	 * </p>
	 */
	public static final String STR_NO_ALIAS_MESSAGE = "STR_NO_ALIAS_MESSAGE";

	/**
	 * Resource string {@code STR_NO_ALIAS_MESSAGE}
	 * <p>
	 * Please enter a new name for the certificate entry.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_ALIAS_MESSAGE(Object... arguments) {
		return format(STR_NO_ALIAS_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_APPLY_FAILED_MESSAGE}
	 * <p>
	 * Applying the options failed. See details for further information.
	 * </p>
	 */
	public static final String STR_APPLY_FAILED_MESSAGE = "STR_APPLY_FAILED_MESSAGE";

	/**
	 * Resource string {@code STR_APPLY_FAILED_MESSAGE}
	 * <p>
	 * Applying the options failed. See details for further information.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_APPLY_FAILED_MESSAGE(Object... arguments) {
		return format(STR_APPLY_FAILED_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_ALIAS_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid name.
	 * </p>
	 */
	public static final String STR_INVALID_ALIAS_MESSAGE = "STR_INVALID_ALIAS_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_ALIAS_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid name.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_INVALID_ALIAS_MESSAGE(Object... arguments) {
		return format(STR_INVALID_ALIAS_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_ENTRY_OPTIONS_TITLE}
	 * <p>
	 * Edit entry options
	 * </p>
	 */
	public static final String STR_ENTRY_OPTIONS_TITLE = "STR_ENTRY_OPTIONS_TITLE";

	/**
	 * Resource string {@code STR_ENTRY_OPTIONS_TITLE}
	 * <p>
	 * Edit entry options
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ENTRY_OPTIONS_TITLE(Object... arguments) {
		return format(STR_ENTRY_OPTIONS_TITLE, arguments);
	}

}
