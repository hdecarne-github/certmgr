/*
 * I18N resource strings
 *
 * Generated on 07.05.2016 08:43:41
 */
package de.carne.jfx.messagebox;

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
	 * Resource key {@code STR_OK_BUTTON}
	 * <p>
	 * Ok
	 * </p>
	 */
	public static final String STR_OK_BUTTON = "STR_OK_BUTTON";

	/**
	 * Resource string {@code STR_OK_BUTTON}
	 * <p>
	 * Ok
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_OK_BUTTON(Object... arguments) {
		return format(STR_OK_BUTTON, arguments);
	}

	/**
	 * Resource key {@code STR_NO_BUTTON}
	 * <p>
	 * No
	 * </p>
	 */
	public static final String STR_NO_BUTTON = "STR_NO_BUTTON";

	/**
	 * Resource string {@code STR_NO_BUTTON}
	 * <p>
	 * No
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_BUTTON(Object... arguments) {
		return format(STR_NO_BUTTON, arguments);
	}

	/**
	 * Resource key {@code STR_YES_BUTTON}
	 * <p>
	 * Yes
	 * </p>
	 */
	public static final String STR_YES_BUTTON = "STR_YES_BUTTON";

	/**
	 * Resource string {@code STR_YES_BUTTON}
	 * <p>
	 * Yes
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_YES_BUTTON(Object... arguments) {
		return format(STR_YES_BUTTON, arguments);
	}

	/**
	 * Resource key {@code STR_CANCEL_BUTTON}
	 * <p>
	 * Cancel
	 * </p>
	 */
	public static final String STR_CANCEL_BUTTON = "STR_CANCEL_BUTTON";

	/**
	 * Resource string {@code STR_CANCEL_BUTTON}
	 * <p>
	 * Cancel
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CANCEL_BUTTON(Object... arguments) {
		return format(STR_CANCEL_BUTTON, arguments);
	}

	/**
	 * Resource key {@code STR_DETAILS_COLLAPSED_BUTTON}
	 * <p>
	 * ▶
	 * </p>
	 */
	public static final String STR_DETAILS_COLLAPSED_BUTTON = "STR_DETAILS_COLLAPSED_BUTTON";

	/**
	 * Resource string {@code STR_DETAILS_COLLAPSED_BUTTON}
	 * <p>
	 * ▶
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_DETAILS_COLLAPSED_BUTTON(Object... arguments) {
		return format(STR_DETAILS_COLLAPSED_BUTTON, arguments);
	}

	/**
	 * Resource key {@code STR_DETAILS_EXPANDED_BUTTON}
	 * <p>
	 * ▼
	 * </p>
	 */
	public static final String STR_DETAILS_EXPANDED_BUTTON = "STR_DETAILS_EXPANDED_BUTTON";

	/**
	 * Resource string {@code STR_DETAILS_EXPANDED_BUTTON}
	 * <p>
	 * ▼
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_DETAILS_EXPANDED_BUTTON(Object... arguments) {
		return format(STR_DETAILS_EXPANDED_BUTTON, arguments);
	}

	/**
	 * Resource key {@code STR_MESSAGEBOX_TITLE}
	 * <p>
	 * Message
	 * </p>
	 */
	public static final String STR_MESSAGEBOX_TITLE = "STR_MESSAGEBOX_TITLE";

	/**
	 * Resource string {@code STR_MESSAGEBOX_TITLE}
	 * <p>
	 * Message
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_MESSAGEBOX_TITLE(Object... arguments) {
		return format(STR_MESSAGEBOX_TITLE, arguments);
	}

}
