/*
 * I18N resource strings
 *
 * Generated on 07.05.2016 08:43:41
 */
package de.carne.certmgr.jfx.crloptions;

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
	 * Resource key {@code STR_NO_ISSUER_ENTRY_MESSAGE}
	 * <p>
	 * Please select a CRL issuer.
	 * </p>
	 */
	public static final String STR_NO_ISSUER_ENTRY_MESSAGE = "STR_NO_ISSUER_ENTRY_MESSAGE";

	/**
	 * Resource string {@code STR_NO_ISSUER_ENTRY_MESSAGE}
	 * <p>
	 * Please select a CRL issuer.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_ISSUER_ENTRY_MESSAGE(Object... arguments) {
		return format(STR_NO_ISSUER_ENTRY_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_LAST_UPDATE_MESSAGE}
	 * <p>
	 * Please select a last update date.
	 * </p>
	 */
	public static final String STR_NO_LAST_UPDATE_MESSAGE = "STR_NO_LAST_UPDATE_MESSAGE";

	/**
	 * Resource string {@code STR_NO_LAST_UPDATE_MESSAGE}
	 * <p>
	 * Please select a last update date.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_LAST_UPDATE_MESSAGE(Object... arguments) {
		return format(STR_NO_LAST_UPDATE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_NEXT_UPDATE_MESSAGE}
	 * <p>
	 * Please select a next update date.
	 * </p>
	 */
	public static final String STR_NO_NEXT_UPDATE_MESSAGE = "STR_NO_NEXT_UPDATE_MESSAGE";

	/**
	 * Resource string {@code STR_NO_NEXT_UPDATE_MESSAGE}
	 * <p>
	 * Please select a next update date.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_NEXT_UPDATE_MESSAGE(Object... arguments) {
		return format(STR_NO_NEXT_UPDATE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_SIG_ALG_MESSAGE}
	 * <p>
	 * Please select a signature algorithm.
	 * </p>
	 */
	public static final String STR_NO_SIG_ALG_MESSAGE = "STR_NO_SIG_ALG_MESSAGE";

	/**
	 * Resource string {@code STR_NO_SIG_ALG_MESSAGE}
	 * <p>
	 * Please select a signature algorithm.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_SIG_ALG_MESSAGE(Object... arguments) {
		return format(STR_NO_SIG_ALG_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_GENERATE_ERROR_MESSAGE}
	 * <p>
	 * Generation of Certificate Revocation List failed. See details for further information.
	 * </p>
	 */
	public static final String STR_GENERATE_ERROR_MESSAGE = "STR_GENERATE_ERROR_MESSAGE";

	/**
	 * Resource string {@code STR_GENERATE_ERROR_MESSAGE}
	 * <p>
	 * Generation of Certificate Revocation List failed. See details for further information.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_GENERATE_ERROR_MESSAGE(Object... arguments) {
		return format(STR_GENERATE_ERROR_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_CRL_OPTIONS_TITLE}
	 * <p>
	 * Create&frasl;Update Certificate Revocation List
	 * </p>
	 */
	public static final String STR_CRL_OPTIONS_TITLE = "STR_CRL_OPTIONS_TITLE";

	/**
	 * Resource string {@code STR_CRL_OPTIONS_TITLE}
	 * <p>
	 * Create&frasl;Update Certificate Revocation List
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CRL_OPTIONS_TITLE(Object... arguments) {
		return format(STR_CRL_OPTIONS_TITLE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_NEXT_UPDATE_MESSAGE}
	 * <p>
	 * Please select an update date after the last update date. 
	 * </p>
	 */
	public static final String STR_INVALID_NEXT_UPDATE_MESSAGE = "STR_INVALID_NEXT_UPDATE_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_NEXT_UPDATE_MESSAGE}
	 * <p>
	 * Please select an update date after the last update date. 
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_INVALID_NEXT_UPDATE_MESSAGE(Object... arguments) {
		return format(STR_INVALID_NEXT_UPDATE_MESSAGE, arguments);
	}

}
