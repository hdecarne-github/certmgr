/*
 * I18N resource strings
 *
 * Generated on Apr 30, 2016 10:35:47 PM
 */
package de.carne.certmgr.jfx.storeoptions;

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
	 * Resource key {@code STR_NO_DEF_KEY_SIZE_MESSAGE}
	 * <p>
	 * Please select a default key size.
	 * </p>
	 */
	public static final String STR_NO_DEF_KEY_SIZE_MESSAGE = "STR_NO_DEF_KEY_SIZE_MESSAGE";

	/**
	 * Resource string {@code STR_NO_DEF_KEY_SIZE_MESSAGE}
	 * <p>
	 * Please select a default key size.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_DEF_KEY_SIZE_MESSAGE(Object... arguments) {
		return format(STR_NO_DEF_KEY_SIZE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_DEF_CRT_VALIDITY_MESSAGE}
	 * <p>
	 * Please select a default certificate validity.
	 * </p>
	 */
	public static final String STR_NO_DEF_CRT_VALIDITY_MESSAGE = "STR_NO_DEF_CRT_VALIDITY_MESSAGE";

	/**
	 * Resource string {@code STR_NO_DEF_CRT_VALIDITY_MESSAGE}
	 * <p>
	 * Please select a default certificate validity.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_DEF_CRT_VALIDITY_MESSAGE(Object... arguments) {
		return format(STR_NO_DEF_CRT_VALIDITY_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_DEF_SIG_ALG_MESSAGE}
	 * <p>
	 * Please select a default signature algorithm.
	 * </p>
	 */
	public static final String STR_NO_DEF_SIG_ALG_MESSAGE = "STR_NO_DEF_SIG_ALG_MESSAGE";

	/**
	 * Resource string {@code STR_NO_DEF_SIG_ALG_MESSAGE}
	 * <p>
	 * Please select a default signature algorithm.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_DEF_SIG_ALG_MESSAGE(Object... arguments) {
		return format(STR_NO_DEF_SIG_ALG_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_STORE_FOLDER_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid folder.
	 * </p>
	 */
	public static final String STR_INVALID_STORE_FOLDER_MESSAGE = "STR_INVALID_STORE_FOLDER_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_STORE_FOLDER_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid folder.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_INVALID_STORE_FOLDER_MESSAGE(Object... arguments) {
		return format(STR_INVALID_STORE_FOLDER_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_DEF_KEY_ALG_MESSAGE}
	 * <p>
	 * Please select a default key algorithm.
	 * </p>
	 */
	public static final String STR_NO_DEF_KEY_ALG_MESSAGE = "STR_NO_DEF_KEY_ALG_MESSAGE";

	/**
	 * Resource string {@code STR_NO_DEF_KEY_ALG_MESSAGE}
	 * <p>
	 * Please select a default key algorithm.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_DEF_KEY_ALG_MESSAGE(Object... arguments) {
		return format(STR_NO_DEF_KEY_ALG_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_CREATE_STORE_ERROR_MESSAGE}
	 * <p>
	 * An error occurred while creating the certificate store<br/>''{0}''.
	 * </p>
	 */
	public static final String STR_CREATE_STORE_ERROR_MESSAGE = "STR_CREATE_STORE_ERROR_MESSAGE";

	/**
	 * Resource string {@code STR_CREATE_STORE_ERROR_MESSAGE}
	 * <p>
	 * An error occurred while creating the certificate store<br/>''{0}''.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CREATE_STORE_ERROR_MESSAGE(Object... arguments) {
		return format(STR_CREATE_STORE_ERROR_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_WRITE_STORE_OPTIONS_ERROR_MESSAGE}
	 * <p>
	 * An error occurred while writing options for certificate store<br/>''{0}''.
	 * </p>
	 */
	public static final String STR_WRITE_STORE_OPTIONS_ERROR_MESSAGE = "STR_WRITE_STORE_OPTIONS_ERROR_MESSAGE";

	/**
	 * Resource string {@code STR_WRITE_STORE_OPTIONS_ERROR_MESSAGE}
	 * <p>
	 * An error occurred while writing options for certificate store<br/>''{0}''.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_WRITE_STORE_OPTIONS_ERROR_MESSAGE(Object... arguments) {
		return format(STR_WRITE_STORE_OPTIONS_ERROR_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_DEF_CRL_UPDATE_MESSAGE}
	 * <p>
	 * Please select a default CRL update.
	 * </p>
	 */
	public static final String STR_NO_DEF_CRL_UPDATE_MESSAGE = "STR_NO_DEF_CRL_UPDATE_MESSAGE";

	/**
	 * Resource string {@code STR_NO_DEF_CRL_UPDATE_MESSAGE}
	 * <p>
	 * Please select a default CRL update.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_DEF_CRL_UPDATE_MESSAGE(Object... arguments) {
		return format(STR_NO_DEF_CRL_UPDATE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_STORE_NAME_MESSAGE}
	 * <p>
	 * Please enter a certificate store name.
	 * </p>
	 */
	public static final String STR_NO_STORE_NAME_MESSAGE = "STR_NO_STORE_NAME_MESSAGE";

	/**
	 * Resource string {@code STR_NO_STORE_NAME_MESSAGE}
	 * <p>
	 * Please enter a certificate store name.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_STORE_NAME_MESSAGE(Object... arguments) {
		return format(STR_NO_STORE_NAME_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_STORE_FOLDER_MESSAGE}
	 * <p>
	 * Please enter&frasl;select a folder.
	 * </p>
	 */
	public static final String STR_NO_STORE_FOLDER_MESSAGE = "STR_NO_STORE_FOLDER_MESSAGE";

	/**
	 * Resource string {@code STR_NO_STORE_FOLDER_MESSAGE}
	 * <p>
	 * Please enter&frasl;select a folder.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_STORE_FOLDER_MESSAGE(Object... arguments) {
		return format(STR_NO_STORE_FOLDER_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_EDIT_STORE_OPTIONS_TITLE}
	 * <p>
	 * Edit Certificate Store Options
	 * </p>
	 */
	public static final String STR_EDIT_STORE_OPTIONS_TITLE = "STR_EDIT_STORE_OPTIONS_TITLE";

	/**
	 * Resource string {@code STR_EDIT_STORE_OPTIONS_TITLE}
	 * <p>
	 * Edit Certificate Store Options
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_EDIT_STORE_OPTIONS_TITLE(Object... arguments) {
		return format(STR_EDIT_STORE_OPTIONS_TITLE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_STORE_NAME_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid store name.
	 * </p>
	 */
	public static final String STR_INVALID_STORE_NAME_MESSAGE = "STR_INVALID_STORE_NAME_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_STORE_NAME_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid store name.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_INVALID_STORE_NAME_MESSAGE(Object... arguments) {
		return format(STR_INVALID_STORE_NAME_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_SAVE_BUTTON}
	 * <p>
	 * Save
	 * </p>
	 */
	public static final String STR_SAVE_BUTTON = "STR_SAVE_BUTTON";

	/**
	 * Resource string {@code STR_SAVE_BUTTON}
	 * <p>
	 * Save
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_SAVE_BUTTON(Object... arguments) {
		return format(STR_SAVE_BUTTON, arguments);
	}

	/**
	 * Resource key {@code STR_NEW_STORE_OPTIONS_TITLE}
	 * <p>
	 * New Certificate Store
	 * </p>
	 */
	public static final String STR_NEW_STORE_OPTIONS_TITLE = "STR_NEW_STORE_OPTIONS_TITLE";

	/**
	 * Resource string {@code STR_NEW_STORE_OPTIONS_TITLE}
	 * <p>
	 * New Certificate Store
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NEW_STORE_OPTIONS_TITLE(Object... arguments) {
		return format(STR_NEW_STORE_OPTIONS_TITLE, arguments);
	}

}
