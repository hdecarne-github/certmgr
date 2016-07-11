/*
 * I18N resource strings
 *
 * Generated on 11.07.2016 11:39:09
 */
package de.carne.certmgr.jfx.certimport;

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
	 * Resource key {@code STR_CLIPBOARD_RESOURCE_DATA}
	 * <p>
	 * Clipboard data
	 * </p>
	 */
	public static final String STR_CLIPBOARD_RESOURCE_DATA = "STR_CLIPBOARD_RESOURCE_DATA";

	/**
	 * Resource string {@code STR_CLIPBOARD_RESOURCE_DATA}
	 * <p>
	 * Clipboard data
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CLIPBOARD_RESOURCE_DATA(Object... arguments) {
		return format(STR_CLIPBOARD_RESOURCE_DATA, arguments);
	}

	/**
	 * Resource key {@code STR_FILE_FILTER_FORMAT}
	 * <p>
	 * {0} files
	 * </p>
	 */
	public static final String STR_FILE_FILTER_FORMAT = "STR_FILE_FILTER_FORMAT";

	/**
	 * Resource string {@code STR_FILE_FILTER_FORMAT}
	 * <p>
	 * {0} files
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_FILE_FILTER_FORMAT(Object... arguments) {
		return format(STR_FILE_FILTER_FORMAT, arguments);
	}

	/**
	 * Resource key {@code STR_IMPORT_FAILED_MESSAGE}
	 * <p>
	 * An error occurred while importing ''{0}''.<br/>The import has been stopped. 
	 * </p>
	 */
	public static final String STR_IMPORT_FAILED_MESSAGE = "STR_IMPORT_FAILED_MESSAGE";

	/**
	 * Resource string {@code STR_IMPORT_FAILED_MESSAGE}
	 * <p>
	 * An error occurred while importing ''{0}''.<br/>The import has been stopped. 
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_IMPORT_FAILED_MESSAGE(Object... arguments) {
		return format(STR_IMPORT_FAILED_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_FILE_SOURCE_MESSAGE}
	 * <p>
	 * Please enter&frasl;select a file to import.
	 * </p>
	 */
	public static final String STR_NO_FILE_SOURCE_MESSAGE = "STR_NO_FILE_SOURCE_MESSAGE";

	/**
	 * Resource string {@code STR_NO_FILE_SOURCE_MESSAGE}
	 * <p>
	 * Please enter&frasl;select a file to import.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_FILE_SOURCE_MESSAGE(Object... arguments) {
		return format(STR_NO_FILE_SOURCE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_URL_SOURCE_MESSAGE}
	 * <p>
	 * Please enter a URL to import.
	 * </p>
	 */
	public static final String STR_NO_URL_SOURCE_MESSAGE = "STR_NO_URL_SOURCE_MESSAGE";

	/**
	 * Resource string {@code STR_NO_URL_SOURCE_MESSAGE}
	 * <p>
	 * Please enter a URL to import.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_URL_SOURCE_MESSAGE(Object... arguments) {
		return format(STR_NO_URL_SOURCE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_LOAD_ERROR_MESSAGE}
	 * <p>
	 * An error occurred while loading the data: ''{0}''
	 * </p>
	 */
	public static final String STR_LOAD_ERROR_MESSAGE = "STR_LOAD_ERROR_MESSAGE";

	/**
	 * Resource string {@code STR_LOAD_ERROR_MESSAGE}
	 * <p>
	 * An error occurred while loading the data: ''{0}''
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_LOAD_ERROR_MESSAGE(Object... arguments) {
		return format(STR_LOAD_ERROR_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_FILE_SOURCE_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid file.
	 * </p>
	 */
	public static final String STR_INVALID_FILE_SOURCE_MESSAGE = "STR_INVALID_FILE_SOURCE_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_FILE_SOURCE_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid file.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_INVALID_FILE_SOURCE_MESSAGE(Object... arguments) {
		return format(STR_INVALID_FILE_SOURCE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_IMPORT_SOURCE_MESSAGE}
	 * <p>
	 * Please setup an import source.
	 * </p>
	 */
	public static final String STR_NO_IMPORT_SOURCE_MESSAGE = "STR_NO_IMPORT_SOURCE_MESSAGE";

	/**
	 * Resource string {@code STR_NO_IMPORT_SOURCE_MESSAGE}
	 * <p>
	 * Please setup an import source.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_IMPORT_SOURCE_MESSAGE(Object... arguments) {
		return format(STR_NO_IMPORT_SOURCE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_FOLDER_SOURCE_MESSAGE}
	 * <p>
	 * Please enter&frasl;select a folder to import.
	 * </p>
	 */
	public static final String STR_NO_FOLDER_SOURCE_MESSAGE = "STR_NO_FOLDER_SOURCE_MESSAGE";

	/**
	 * Resource string {@code STR_NO_FOLDER_SOURCE_MESSAGE}
	 * <p>
	 * Please enter&frasl;select a folder to import.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_FOLDER_SOURCE_MESSAGE(Object... arguments) {
		return format(STR_NO_FOLDER_SOURCE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_FILE_FILTER_ALL}
	 * <p>
	 * All files
	 * </p>
	 */
	public static final String STR_FILE_FILTER_ALL = "STR_FILE_FILTER_ALL";

	/**
	 * Resource string {@code STR_FILE_FILTER_ALL}
	 * <p>
	 * All files
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_FILE_FILTER_ALL(Object... arguments) {
		return format(STR_FILE_FILTER_ALL, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_URL_SOURCE_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid URL.
	 * </p>
	 */
	public static final String STR_INVALID_URL_SOURCE_MESSAGE = "STR_INVALID_URL_SOURCE_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_URL_SOURCE_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid URL.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_INVALID_URL_SOURCE_MESSAGE(Object... arguments) {
		return format(STR_INVALID_URL_SOURCE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_CLIPBOARD_SOURCE_MESSAGE}
	 * <p>
	 * The clipboard contains no suitable data.
	 * </p>
	 */
	public static final String STR_INVALID_CLIPBOARD_SOURCE_MESSAGE = "STR_INVALID_CLIPBOARD_SOURCE_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_CLIPBOARD_SOURCE_MESSAGE}
	 * <p>
	 * The clipboard contains no suitable data.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_INVALID_CLIPBOARD_SOURCE_MESSAGE(Object... arguments) {
		return format(STR_INVALID_CLIPBOARD_SOURCE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_IMPORT_SELECTION_MESSAGE}
	 * <p>
	 * No import entries selected.<br/>Please load and select the data to import first.
	 * </p>
	 */
	public static final String STR_NO_IMPORT_SELECTION_MESSAGE = "STR_NO_IMPORT_SELECTION_MESSAGE";

	/**
	 * Resource string {@code STR_NO_IMPORT_SELECTION_MESSAGE}
	 * <p>
	 * No import entries selected.<br/>Please load and select the data to import first.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_NO_IMPORT_SELECTION_MESSAGE(Object... arguments) {
		return format(STR_NO_IMPORT_SELECTION_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_FOLDER_SOURCE}
	 * <p>
	 * ''{0}'' is not a valid folder.
	 * </p>
	 */
	public static final String STR_INVALID_FOLDER_SOURCE = "STR_INVALID_FOLDER_SOURCE";

	/**
	 * Resource string {@code STR_INVALID_FOLDER_SOURCE}
	 * <p>
	 * ''{0}'' is not a valid folder.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_INVALID_FOLDER_SOURCE(Object... arguments) {
		return format(STR_INVALID_FOLDER_SOURCE, arguments);
	}

	/**
	 * Resource key {@code STR_ENTRIES_LOADED_MESSAGE}
	 * <p>
	 * {0} entries loaded
	 * </p>
	 */
	public static final String STR_ENTRIES_LOADED_MESSAGE = "STR_ENTRIES_LOADED_MESSAGE";

	/**
	 * Resource string {@code STR_ENTRIES_LOADED_MESSAGE}
	 * <p>
	 * {0} entries loaded
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ENTRIES_LOADED_MESSAGE(Object... arguments) {
		return format(STR_ENTRIES_LOADED_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_CERT_IMPORT_TITLE}
	 * <p>
	 * Import
	 * </p>
	 */
	public static final String STR_CERT_IMPORT_TITLE = "STR_CERT_IMPORT_TITLE";

	/**
	 * Resource string {@code STR_CERT_IMPORT_TITLE}
	 * <p>
	 * Import
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CERT_IMPORT_TITLE(Object... arguments) {
		return format(STR_CERT_IMPORT_TITLE, arguments);
	}

	/**
	 * Resource key {@code STR_CLIPBOARD_RESOURCE_FILE}
	 * <p>
	 * Clipboard file ''{0}''
	 * </p>
	 */
	public static final String STR_CLIPBOARD_RESOURCE_FILE = "STR_CLIPBOARD_RESOURCE_FILE";

	/**
	 * Resource string {@code STR_CLIPBOARD_RESOURCE_FILE}
	 * <p>
	 * Clipboard file ''{0}''
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CLIPBOARD_RESOURCE_FILE(Object... arguments) {
		return format(STR_CLIPBOARD_RESOURCE_FILE, arguments);
	}

}
