/*
 * I18N resource strings
 *
 * Generated on 07.05.2016 08:43:41
 */
package de.carne.certmgr.jfx.passwordprompt;

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
	 * Resource key {@code STR_NO_PASSWORD_MESSAGE}
	 * <p>
	 * Please enter a password.
	 * </p>
	 */
	public static final String STR_NO_PASSWORD_MESSAGE = "STR_NO_PASSWORD_MESSAGE";

	/**
	 * Resource string {@code STR_NO_PASSWORD_MESSAGE}
	 * <p>
	 * Please enter a password.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_PASSWORD_MESSAGE(Object... arguments) {
		return format(STR_NO_PASSWORD_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NEW_PASSWORD_MESSAGE}
	 * <p>
	 * Please define the password for creating<br/>{0}.
	 * </p>
	 */
	public static final String STR_NEW_PASSWORD_MESSAGE = "STR_NEW_PASSWORD_MESSAGE";

	/**
	 * Resource string {@code STR_NEW_PASSWORD_MESSAGE}
	 * <p>
	 * Please define the password for creating<br/>{0}.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NEW_PASSWORD_MESSAGE(Object... arguments) {
		return format(STR_NEW_PASSWORD_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_PASSWORD_MISMATCH_MESSAGE}
	 * <p>
	 * Passwords do not match. Please try again.
	 * </p>
	 */
	public static final String STR_PASSWORD_MISMATCH_MESSAGE = "STR_PASSWORD_MISMATCH_MESSAGE";

	/**
	 * Resource string {@code STR_PASSWORD_MISMATCH_MESSAGE}
	 * <p>
	 * Passwords do not match. Please try again.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_PASSWORD_MISMATCH_MESSAGE(Object... arguments) {
		return format(STR_PASSWORD_MISMATCH_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NEW_PASSWORD_PROMPT_TITLE}
	 * <p>
	 * New password required
	 * </p>
	 */
	public static final String STR_NEW_PASSWORD_PROMPT_TITLE = "STR_NEW_PASSWORD_PROMPT_TITLE";

	/**
	 * Resource string {@code STR_NEW_PASSWORD_PROMPT_TITLE}
	 * <p>
	 * New password required
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NEW_PASSWORD_PROMPT_TITLE(Object... arguments) {
		return format(STR_NEW_PASSWORD_PROMPT_TITLE, arguments);
	}

	/**
	 * Resource key {@code STR_PASSWORD_PROMPT_TITLE}
	 * <p>
	 * Password required
	 * </p>
	 */
	public static final String STR_PASSWORD_PROMPT_TITLE = "STR_PASSWORD_PROMPT_TITLE";

	/**
	 * Resource string {@code STR_PASSWORD_PROMPT_TITLE}
	 * <p>
	 * Password required
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_PASSWORD_PROMPT_TITLE(Object... arguments) {
		return format(STR_PASSWORD_PROMPT_TITLE, arguments);
	}

	/**
	 * Resource key {@code STR_WRONG_PASSWORD_MESSAGE}
	 * <p>
	 * Password incorrect. Please try again.
	 * </p>
	 */
	public static final String STR_WRONG_PASSWORD_MESSAGE = "STR_WRONG_PASSWORD_MESSAGE";

	/**
	 * Resource string {@code STR_WRONG_PASSWORD_MESSAGE}
	 * <p>
	 * Password incorrect. Please try again.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_WRONG_PASSWORD_MESSAGE(Object... arguments) {
		return format(STR_WRONG_PASSWORD_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_PASSWORD_MESSAGE}
	 * <p>
	 * Password is empty or contains leading&frasl;trailing spaces.<br/>Please enter a different password.
	 * </p>
	 */
	public static final String STR_INVALID_PASSWORD_MESSAGE = "STR_INVALID_PASSWORD_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_PASSWORD_MESSAGE}
	 * <p>
	 * Password is empty or contains leading&frasl;trailing spaces.<br/>Please enter a different password.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_INVALID_PASSWORD_MESSAGE(Object... arguments) {
		return format(STR_INVALID_PASSWORD_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_PASSWORD_MESSAGE}
	 * <p>
	 * Please enter the password for accessing<br/>{0}.
	 * </p>
	 */
	public static final String STR_PASSWORD_MESSAGE = "STR_PASSWORD_MESSAGE";

	/**
	 * Resource string {@code STR_PASSWORD_MESSAGE}
	 * <p>
	 * Please enter the password for accessing<br/>{0}.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_PASSWORD_MESSAGE(Object... arguments) {
		return format(STR_PASSWORD_MESSAGE, arguments);
	}

}
