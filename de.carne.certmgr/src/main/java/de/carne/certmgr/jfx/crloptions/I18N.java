/*
 * I18N resource strings
 *
 * Generated on 4/30/16 6:36 AM
 */
package de.carne.certmgr.jfx.crloptions;

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
	 * Resource key {@code STR_NO_ISSUER_ENTRY_MESSAGE}
	 * <p>
	 * Please select a CRL issuer.
	 * </p>
	 */
	static final String STR_NO_ISSUER_ENTRY_MESSAGE = "STR_NO_ISSUER_ENTRY_MESSAGE";

	/**
	 * Resource string {@code STR_NO_ISSUER_ENTRY_MESSAGE}
	 * <p>
	 * Please select a CRL issuer.
	 * </p>
	 */
	static String formatSTR_NO_ISSUER_ENTRY_MESSAGE(Object... arguments) {
		return format(STR_NO_ISSUER_ENTRY_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_LAST_UPDATE_MESSAGE}
	 * <p>
	 * Please select a last update date.
	 * </p>
	 */
	static final String STR_NO_LAST_UPDATE_MESSAGE = "STR_NO_LAST_UPDATE_MESSAGE";

	/**
	 * Resource string {@code STR_NO_LAST_UPDATE_MESSAGE}
	 * <p>
	 * Please select a last update date.
	 * </p>
	 */
	static String formatSTR_NO_LAST_UPDATE_MESSAGE(Object... arguments) {
		return format(STR_NO_LAST_UPDATE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_NEXT_UPDATE_MESSAGE}
	 * <p>
	 * Please select a next update date.
	 * </p>
	 */
	static final String STR_NO_NEXT_UPDATE_MESSAGE = "STR_NO_NEXT_UPDATE_MESSAGE";

	/**
	 * Resource string {@code STR_NO_NEXT_UPDATE_MESSAGE}
	 * <p>
	 * Please select a next update date.
	 * </p>
	 */
	static String formatSTR_NO_NEXT_UPDATE_MESSAGE(Object... arguments) {
		return format(STR_NO_NEXT_UPDATE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_SIG_ALG_MESSAGE}
	 * <p>
	 * Please select a signature algorithm.
	 * </p>
	 */
	static final String STR_NO_SIG_ALG_MESSAGE = "STR_NO_SIG_ALG_MESSAGE";

	/**
	 * Resource string {@code STR_NO_SIG_ALG_MESSAGE}
	 * <p>
	 * Please select a signature algorithm.
	 * </p>
	 */
	static String formatSTR_NO_SIG_ALG_MESSAGE(Object... arguments) {
		return format(STR_NO_SIG_ALG_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_GENERATE_ERROR_MESSAGE}
	 * <p>
	 * Generation of Certificate Revocation List failed. See details for further information.
	 * </p>
	 */
	static final String STR_GENERATE_ERROR_MESSAGE = "STR_GENERATE_ERROR_MESSAGE";

	/**
	 * Resource string {@code STR_GENERATE_ERROR_MESSAGE}
	 * <p>
	 * Generation of Certificate Revocation List failed. See details for further information.
	 * </p>
	 */
	static String formatSTR_GENERATE_ERROR_MESSAGE(Object... arguments) {
		return format(STR_GENERATE_ERROR_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_CRL_OPTIONS_TITLE}
	 * <p>
	 * Create&frasl;Update Certificate Revocation List
	 * </p>
	 */
	static final String STR_CRL_OPTIONS_TITLE = "STR_CRL_OPTIONS_TITLE";

	/**
	 * Resource string {@code STR_CRL_OPTIONS_TITLE}
	 * <p>
	 * Create&frasl;Update Certificate Revocation List
	 * </p>
	 */
	static String formatSTR_CRL_OPTIONS_TITLE(Object... arguments) {
		return format(STR_CRL_OPTIONS_TITLE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_NEXT_UPDATE_MESSAGE}
	 * <p>
	 * Please select an update date after the last update date. 
	 * </p>
	 */
	static final String STR_INVALID_NEXT_UPDATE_MESSAGE = "STR_INVALID_NEXT_UPDATE_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_NEXT_UPDATE_MESSAGE}
	 * <p>
	 * Please select an update date after the last update date. 
	 * </p>
	 */
	static String formatSTR_INVALID_NEXT_UPDATE_MESSAGE(Object... arguments) {
		return format(STR_INVALID_NEXT_UPDATE_MESSAGE, arguments);
	}

}
