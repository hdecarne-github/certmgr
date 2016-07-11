/*
 * I18N resource strings
 *
 * Generated on 11.07.2016 11:39:09
 */
package de.carne.jfx;

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

	/**
	 * Format a resource string.
	 * @param key The resource key.
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String format(String key, Object... arguments) {
		String pattern = BUNDLE.getString(key);

		return (arguments.length > 0 ? MessageFormat.format(pattern, arguments) : pattern);
	}

	/**
	 * Resource key {@code STR_UNEXPECTED_EXCEPTION_MESSAGE}
	 * <p>
	 * An unexpected error has occurred: ''{0}'' 
	 * </p>
	 */
	public static final String STR_UNEXPECTED_EXCEPTION_MESSAGE = "STR_UNEXPECTED_EXCEPTION_MESSAGE";

	/**
	 * Resource string {@code STR_UNEXPECTED_EXCEPTION_MESSAGE}
	 * <p>
	 * An unexpected error has occurred: ''{0}'' 
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_UNEXPECTED_EXCEPTION_MESSAGE(Object... arguments) {
		return format(STR_UNEXPECTED_EXCEPTION_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_PREF_SYNC_FAILED_MESSAGE}
	 * <p>
	 * Preference synchronization failed.
	 * </p>
	 */
	public static final String STR_PREF_SYNC_FAILED_MESSAGE = "STR_PREF_SYNC_FAILED_MESSAGE";

	/**
	 * Resource string {@code STR_PREF_SYNC_FAILED_MESSAGE}
	 * <p>
	 * Preference synchronization failed.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_PREF_SYNC_FAILED_MESSAGE(Object... arguments) {
		return format(STR_PREF_SYNC_FAILED_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_MESSAGEBOX_EXCEPTION_MESSAGE}
	 * <p>
	 * Unable to display message ''{0}''.<br/>Cause: ''{1}'' 
	 * </p>
	 */
	public static final String STR_MESSAGEBOX_EXCEPTION_MESSAGE = "STR_MESSAGEBOX_EXCEPTION_MESSAGE";

	/**
	 * Resource string {@code STR_MESSAGEBOX_EXCEPTION_MESSAGE}
	 * <p>
	 * Unable to display message ''{0}''.<br/>Cause: ''{1}'' 
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_MESSAGEBOX_EXCEPTION_MESSAGE(Object... arguments) {
		return format(STR_MESSAGEBOX_EXCEPTION_MESSAGE, arguments);
	}

}
