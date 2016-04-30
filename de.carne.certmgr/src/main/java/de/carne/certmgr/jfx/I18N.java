/*
 * I18N resource strings
 *
 * Generated on 4/30/16 6:36 AM
 */
package de.carne.certmgr.jfx;

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
	 * Resource key {@code STR_VERBOSE_ENABLED_MESSAGE}
	 * <p>
	 * Verbose logging enabled
	 * </p>
	 */
	static final String STR_VERBOSE_ENABLED_MESSAGE = "STR_VERBOSE_ENABLED_MESSAGE";

	/**
	 * Resource string {@code STR_VERBOSE_ENABLED_MESSAGE}
	 * <p>
	 * Verbose logging enabled
	 * </p>
	 */
	static String formatSTR_VERBOSE_ENABLED_MESSAGE(Object... arguments) {
		return format(STR_VERBOSE_ENABLED_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_VM_INFO}
	 * <p>
	 * Java VM Version: {0} Vendor: ''{1}''
	 * </p>
	 */
	static final String STR_VM_INFO = "STR_VM_INFO";

	/**
	 * Resource string {@code STR_VM_INFO}
	 * <p>
	 * Java VM Version: {0} Vendor: ''{1}''
	 * </p>
	 */
	static String formatSTR_VM_INFO(Object... arguments) {
		return format(STR_VM_INFO, arguments);
	}

	/**
	 * Resource key {@code STR_TIMEPERIOD_MONTHS}
	 * <p>
	 * {0} month(s)
	 * </p>
	 */
	static final String STR_TIMEPERIOD_MONTHS = "STR_TIMEPERIOD_MONTHS";

	/**
	 * Resource string {@code STR_TIMEPERIOD_MONTHS}
	 * <p>
	 * {0} month(s)
	 * </p>
	 */
	static String formatSTR_TIMEPERIOD_MONTHS(Object... arguments) {
		return format(STR_TIMEPERIOD_MONTHS, arguments);
	}

	/**
	 * Resource key {@code STR_DEBUG_ENABLED_MESSAGE}
	 * <p>
	 * Debug logging enabled
	 * </p>
	 */
	static final String STR_DEBUG_ENABLED_MESSAGE = "STR_DEBUG_ENABLED_MESSAGE";

	/**
	 * Resource string {@code STR_DEBUG_ENABLED_MESSAGE}
	 * <p>
	 * Debug logging enabled
	 * </p>
	 */
	static String formatSTR_DEBUG_ENABLED_MESSAGE(Object... arguments) {
		return format(STR_DEBUG_ENABLED_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_CERT_FILE_FILTER_FORMAT}
	 * <p>
	 * {0} files
	 * </p>
	 */
	static final String STR_CERT_FILE_FILTER_FORMAT = "STR_CERT_FILE_FILTER_FORMAT";

	/**
	 * Resource string {@code STR_CERT_FILE_FILTER_FORMAT}
	 * <p>
	 * {0} files
	 * </p>
	 */
	static String formatSTR_CERT_FILE_FILTER_FORMAT(Object... arguments) {
		return format(STR_CERT_FILE_FILTER_FORMAT, arguments);
	}

	/**
	 * Resource key {@code STR_EXPORT_TARGET_CLIPBOARD}
	 * <p>
	 * Clipboard
	 * </p>
	 */
	static final String STR_EXPORT_TARGET_CLIPBOARD = "STR_EXPORT_TARGET_CLIPBOARD";

	/**
	 * Resource string {@code STR_EXPORT_TARGET_CLIPBOARD}
	 * <p>
	 * Clipboard
	 * </p>
	 */
	static String formatSTR_EXPORT_TARGET_CLIPBOARD(Object... arguments) {
		return format(STR_EXPORT_TARGET_CLIPBOARD, arguments);
	}

	/**
	 * Resource key {@code STR_UNEXPECTED_EXCEPTION_MESSAGE}
	 * <p>
	 * An unexpected error has occurred: ''{0}''
	 * </p>
	 */
	static final String STR_UNEXPECTED_EXCEPTION_MESSAGE = "STR_UNEXPECTED_EXCEPTION_MESSAGE";

	/**
	 * Resource string {@code STR_UNEXPECTED_EXCEPTION_MESSAGE}
	 * <p>
	 * An unexpected error has occurred: ''{0}''
	 * </p>
	 */
	static String formatSTR_UNEXPECTED_EXCEPTION_MESSAGE(Object... arguments) {
		return format(STR_UNEXPECTED_EXCEPTION_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_PARAMETER_MESSAGE}
	 * <p>
	 * Invalid parameter: ''{0}''
	 * </p>
	 */
	static final String STR_INVALID_PARAMETER_MESSAGE = "STR_INVALID_PARAMETER_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_PARAMETER_MESSAGE}
	 * <p>
	 * Invalid parameter: ''{0}''
	 * </p>
	 */
	static String formatSTR_INVALID_PARAMETER_MESSAGE(Object... arguments) {
		return format(STR_INVALID_PARAMETER_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_TIMEPERIOD_DAYS}
	 * <p>
	 * {0} day(s)
	 * </p>
	 */
	static final String STR_TIMEPERIOD_DAYS = "STR_TIMEPERIOD_DAYS";

	/**
	 * Resource string {@code STR_TIMEPERIOD_DAYS}
	 * <p>
	 * {0} day(s)
	 * </p>
	 */
	static String formatSTR_TIMEPERIOD_DAYS(Object... arguments) {
		return format(STR_TIMEPERIOD_DAYS, arguments);
	}

	/**
	 * Resource key {@code STR_CERT_FILE_FILTER_ALL}
	 * <p>
	 * All files
	 * </p>
	 */
	static final String STR_CERT_FILE_FILTER_ALL = "STR_CERT_FILE_FILTER_ALL";

	/**
	 * Resource string {@code STR_CERT_FILE_FILTER_ALL}
	 * <p>
	 * All files
	 * </p>
	 */
	static String formatSTR_CERT_FILE_FILTER_ALL(Object... arguments) {
		return format(STR_CERT_FILE_FILTER_ALL, arguments);
	}

	/**
	 * Resource key {@code STR_PROVIDER_INFO}
	 * <p>
	 * Provider initialized: ''{0}''
	 * </p>
	 */
	static final String STR_PROVIDER_INFO = "STR_PROVIDER_INFO";

	/**
	 * Resource string {@code STR_PROVIDER_INFO}
	 * <p>
	 * Provider initialized: ''{0}''
	 * </p>
	 */
	static String formatSTR_PROVIDER_INFO(Object... arguments) {
		return format(STR_PROVIDER_INFO, arguments);
	}

	/**
	 * Resource key {@code STR_PROVIDER_EXCEPTION_MESSAGE}
	 * <p>
	 * Provider initialization failed.<br/>Cause: ''{0}'' 
	 * </p>
	 */
	static final String STR_PROVIDER_EXCEPTION_MESSAGE = "STR_PROVIDER_EXCEPTION_MESSAGE";

	/**
	 * Resource string {@code STR_PROVIDER_EXCEPTION_MESSAGE}
	 * <p>
	 * Provider initialization failed.<br/>Cause: ''{0}'' 
	 * </p>
	 */
	static String formatSTR_PROVIDER_EXCEPTION_MESSAGE(Object... arguments) {
		return format(STR_PROVIDER_EXCEPTION_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_TIMEPERIOD_YEARS}
	 * <p>
	 * {0} year(s)
	 * </p>
	 */
	static final String STR_TIMEPERIOD_YEARS = "STR_TIMEPERIOD_YEARS";

	/**
	 * Resource string {@code STR_TIMEPERIOD_YEARS}
	 * <p>
	 * {0} year(s)
	 * </p>
	 */
	static String formatSTR_TIMEPERIOD_YEARS(Object... arguments) {
		return format(STR_TIMEPERIOD_YEARS, arguments);
	}

}
