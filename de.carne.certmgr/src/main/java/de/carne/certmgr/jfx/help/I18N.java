/*
 * I18N resource strings
 *
 * Generated on Apr 30, 2016 10:35:47 PM
 */
package de.carne.certmgr.jfx.help;

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
	 * Resource key {@code STR_TOPIC_ENTRY_OPTIONS}
	 * <p>
	 * topic_entryoptions.html
	 * </p>
	 */
	public static final String STR_TOPIC_ENTRY_OPTIONS = "STR_TOPIC_ENTRY_OPTIONS";

	/**
	 * Resource string {@code STR_TOPIC_ENTRY_OPTIONS}
	 * <p>
	 * topic_entryoptions.html
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_TOPIC_ENTRY_OPTIONS(Object... arguments) {
		return format(STR_TOPIC_ENTRY_OPTIONS, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_CERT_EXPORT}
	 * <p>
	 * topic_certexport.html
	 * </p>
	 */
	public static final String STR_TOPIC_CERT_EXPORT = "STR_TOPIC_CERT_EXPORT";

	/**
	 * Resource string {@code STR_TOPIC_CERT_EXPORT}
	 * <p>
	 * topic_certexport.html
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_TOPIC_CERT_EXPORT(Object... arguments) {
		return format(STR_TOPIC_CERT_EXPORT, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_CRL_OPTIONS}
	 * <p>
	 * topic_crloptions.html
	 * </p>
	 */
	public static final String STR_TOPIC_CRL_OPTIONS = "STR_TOPIC_CRL_OPTIONS";

	/**
	 * Resource string {@code STR_TOPIC_CRL_OPTIONS}
	 * <p>
	 * topic_crloptions.html
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_TOPIC_CRL_OPTIONS(Object... arguments) {
		return format(STR_TOPIC_CRL_OPTIONS, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_PASSWORD_PROMPT}
	 * <p>
	 * topic_passwordprompt.html
	 * </p>
	 */
	public static final String STR_TOPIC_PASSWORD_PROMPT = "STR_TOPIC_PASSWORD_PROMPT";

	/**
	 * Resource string {@code STR_TOPIC_PASSWORD_PROMPT}
	 * <p>
	 * topic_passwordprompt.html
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_TOPIC_PASSWORD_PROMPT(Object... arguments) {
		return format(STR_TOPIC_PASSWORD_PROMPT, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_STORE_OPTIONS}
	 * <p>
	 * topic_storeoptions.html
	 * </p>
	 */
	public static final String STR_TOPIC_STORE_OPTIONS = "STR_TOPIC_STORE_OPTIONS";

	/**
	 * Resource string {@code STR_TOPIC_STORE_OPTIONS}
	 * <p>
	 * topic_storeoptions.html
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_TOPIC_STORE_OPTIONS(Object... arguments) {
		return format(STR_TOPIC_STORE_OPTIONS, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_CERT_IMPORT}
	 * <p>
	 * topic_certimport.html
	 * </p>
	 */
	public static final String STR_TOPIC_CERT_IMPORT = "STR_TOPIC_CERT_IMPORT";

	/**
	 * Resource string {@code STR_TOPIC_CERT_IMPORT}
	 * <p>
	 * topic_certimport.html
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_TOPIC_CERT_IMPORT(Object... arguments) {
		return format(STR_TOPIC_CERT_IMPORT, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_NEW_PASSWORD_PROMPT}
	 * <p>
	 * topic_newpasswordprompt.html
	 * </p>
	 */
	public static final String STR_TOPIC_NEW_PASSWORD_PROMPT = "STR_TOPIC_NEW_PASSWORD_PROMPT";

	/**
	 * Resource string {@code STR_TOPIC_NEW_PASSWORD_PROMPT}
	 * <p>
	 * topic_newpasswordprompt.html
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_TOPIC_NEW_PASSWORD_PROMPT(Object... arguments) {
		return format(STR_TOPIC_NEW_PASSWORD_PROMPT, arguments);
	}

	/**
	 * Resource key {@code STR_HELP_TITLE}
	 * <p>
	 * Help
	 * </p>
	 */
	public static final String STR_HELP_TITLE = "STR_HELP_TITLE";

	/**
	 * Resource string {@code STR_HELP_TITLE}
	 * <p>
	 * Help
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_HELP_TITLE(Object... arguments) {
		return format(STR_HELP_TITLE, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_STORE_MANAGER}
	 * <p>
	 * topic_storemanager.html
	 * </p>
	 */
	public static final String STR_TOPIC_STORE_MANAGER = "STR_TOPIC_STORE_MANAGER";

	/**
	 * Resource string {@code STR_TOPIC_STORE_MANAGER}
	 * <p>
	 * topic_storemanager.html
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_TOPIC_STORE_MANAGER(Object... arguments) {
		return format(STR_TOPIC_STORE_MANAGER, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_DN_EDITOR}
	 * <p>
	 * topic_dneditor.html
	 * </p>
	 */
	public static final String STR_TOPIC_DN_EDITOR = "STR_TOPIC_DN_EDITOR";

	/**
	 * Resource string {@code STR_TOPIC_DN_EDITOR}
	 * <p>
	 * topic_dneditor.html
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_TOPIC_DN_EDITOR(Object... arguments) {
		return format(STR_TOPIC_DN_EDITOR, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_CRT_OPTIONS}
	 * <p>
	 * topic_crtoptions.html
	 * </p>
	 */
	public static final String STR_TOPIC_CRT_OPTIONS = "STR_TOPIC_CRT_OPTIONS";

	/**
	 * Resource string {@code STR_TOPIC_CRT_OPTIONS}
	 * <p>
	 * topic_crtoptions.html
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_TOPIC_CRT_OPTIONS(Object... arguments) {
		return format(STR_TOPIC_CRT_OPTIONS, arguments);
	}

}
