/*
 * I18N resource strings
 *
 * Generated on 4/30/16 6:36 AM
 */
package de.carne.certmgr.jfx.entryoptions;

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
	 * Resource key {@code STR_NO_ALIAS_MESSAGE}
	 * <p>
	 * Please enter a new name for the certificate entry.
	 * </p>
	 */
	static final String STR_NO_ALIAS_MESSAGE = "STR_NO_ALIAS_MESSAGE";

	/**
	 * Resource string {@code STR_NO_ALIAS_MESSAGE}
	 * <p>
	 * Please enter a new name for the certificate entry.
	 * </p>
	 */
	static String formatSTR_NO_ALIAS_MESSAGE(Object... arguments) {
		return format(STR_NO_ALIAS_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_APPLY_FAILED_MESSAGE}
	 * <p>
	 * Applying the options failed. See details for further information.
	 * </p>
	 */
	static final String STR_APPLY_FAILED_MESSAGE = "STR_APPLY_FAILED_MESSAGE";

	/**
	 * Resource string {@code STR_APPLY_FAILED_MESSAGE}
	 * <p>
	 * Applying the options failed. See details for further information.
	 * </p>
	 */
	static String formatSTR_APPLY_FAILED_MESSAGE(Object... arguments) {
		return format(STR_APPLY_FAILED_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_ALIAS_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid name.
	 * </p>
	 */
	static final String STR_INVALID_ALIAS_MESSAGE = "STR_INVALID_ALIAS_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_ALIAS_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid name.
	 * </p>
	 */
	static String formatSTR_INVALID_ALIAS_MESSAGE(Object... arguments) {
		return format(STR_INVALID_ALIAS_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_ENTRY_OPTIONS_TITLE}
	 * <p>
	 * Edit entry options
	 * </p>
	 */
	static final String STR_ENTRY_OPTIONS_TITLE = "STR_ENTRY_OPTIONS_TITLE";

	/**
	 * Resource string {@code STR_ENTRY_OPTIONS_TITLE}
	 * <p>
	 * Edit entry options
	 * </p>
	 */
	static String formatSTR_ENTRY_OPTIONS_TITLE(Object... arguments) {
		return format(STR_ENTRY_OPTIONS_TITLE, arguments);
	}

}
