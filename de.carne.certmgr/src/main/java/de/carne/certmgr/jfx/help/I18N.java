/*
 * I18N resource strings
 *
 * Generated on 4/30/16 6:36 AM
 */
package de.carne.certmgr.jfx.help;

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
	 * Resource key {@code STR_TOPIC_ENTRY_OPTIONS}
	 * <p>
	 * topic_entryoptions.html
	 * </p>
	 */
	static final String STR_TOPIC_ENTRY_OPTIONS = "STR_TOPIC_ENTRY_OPTIONS";

	/**
	 * Resource string {@code STR_TOPIC_ENTRY_OPTIONS}
	 * <p>
	 * topic_entryoptions.html
	 * </p>
	 */
	static String formatSTR_TOPIC_ENTRY_OPTIONS(Object... arguments) {
		return format(STR_TOPIC_ENTRY_OPTIONS, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_CERT_EXPORT}
	 * <p>
	 * topic_certexport.html
	 * </p>
	 */
	static final String STR_TOPIC_CERT_EXPORT = "STR_TOPIC_CERT_EXPORT";

	/**
	 * Resource string {@code STR_TOPIC_CERT_EXPORT}
	 * <p>
	 * topic_certexport.html
	 * </p>
	 */
	static String formatSTR_TOPIC_CERT_EXPORT(Object... arguments) {
		return format(STR_TOPIC_CERT_EXPORT, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_CRL_OPTIONS}
	 * <p>
	 * topic_crloptions.html
	 * </p>
	 */
	static final String STR_TOPIC_CRL_OPTIONS = "STR_TOPIC_CRL_OPTIONS";

	/**
	 * Resource string {@code STR_TOPIC_CRL_OPTIONS}
	 * <p>
	 * topic_crloptions.html
	 * </p>
	 */
	static String formatSTR_TOPIC_CRL_OPTIONS(Object... arguments) {
		return format(STR_TOPIC_CRL_OPTIONS, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_PASSWORD_PROMPT}
	 * <p>
	 * topic_passwordprompt.html
	 * </p>
	 */
	static final String STR_TOPIC_PASSWORD_PROMPT = "STR_TOPIC_PASSWORD_PROMPT";

	/**
	 * Resource string {@code STR_TOPIC_PASSWORD_PROMPT}
	 * <p>
	 * topic_passwordprompt.html
	 * </p>
	 */
	static String formatSTR_TOPIC_PASSWORD_PROMPT(Object... arguments) {
		return format(STR_TOPIC_PASSWORD_PROMPT, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_STORE_OPTIONS}
	 * <p>
	 * topic_storeoptions.html
	 * </p>
	 */
	static final String STR_TOPIC_STORE_OPTIONS = "STR_TOPIC_STORE_OPTIONS";

	/**
	 * Resource string {@code STR_TOPIC_STORE_OPTIONS}
	 * <p>
	 * topic_storeoptions.html
	 * </p>
	 */
	static String formatSTR_TOPIC_STORE_OPTIONS(Object... arguments) {
		return format(STR_TOPIC_STORE_OPTIONS, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_CERT_IMPORT}
	 * <p>
	 * topic_certimport.html
	 * </p>
	 */
	static final String STR_TOPIC_CERT_IMPORT = "STR_TOPIC_CERT_IMPORT";

	/**
	 * Resource string {@code STR_TOPIC_CERT_IMPORT}
	 * <p>
	 * topic_certimport.html
	 * </p>
	 */
	static String formatSTR_TOPIC_CERT_IMPORT(Object... arguments) {
		return format(STR_TOPIC_CERT_IMPORT, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_NEW_PASSWORD_PROMPT}
	 * <p>
	 * topic_newpasswordprompt.html
	 * </p>
	 */
	static final String STR_TOPIC_NEW_PASSWORD_PROMPT = "STR_TOPIC_NEW_PASSWORD_PROMPT";

	/**
	 * Resource string {@code STR_TOPIC_NEW_PASSWORD_PROMPT}
	 * <p>
	 * topic_newpasswordprompt.html
	 * </p>
	 */
	static String formatSTR_TOPIC_NEW_PASSWORD_PROMPT(Object... arguments) {
		return format(STR_TOPIC_NEW_PASSWORD_PROMPT, arguments);
	}

	/**
	 * Resource key {@code STR_HELP_TITLE}
	 * <p>
	 * Help
	 * </p>
	 */
	static final String STR_HELP_TITLE = "STR_HELP_TITLE";

	/**
	 * Resource string {@code STR_HELP_TITLE}
	 * <p>
	 * Help
	 * </p>
	 */
	static String formatSTR_HELP_TITLE(Object... arguments) {
		return format(STR_HELP_TITLE, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_STORE_MANAGER}
	 * <p>
	 * topic_storemanager.html
	 * </p>
	 */
	static final String STR_TOPIC_STORE_MANAGER = "STR_TOPIC_STORE_MANAGER";

	/**
	 * Resource string {@code STR_TOPIC_STORE_MANAGER}
	 * <p>
	 * topic_storemanager.html
	 * </p>
	 */
	static String formatSTR_TOPIC_STORE_MANAGER(Object... arguments) {
		return format(STR_TOPIC_STORE_MANAGER, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_DN_EDITOR}
	 * <p>
	 * topic_dneditor.html
	 * </p>
	 */
	static final String STR_TOPIC_DN_EDITOR = "STR_TOPIC_DN_EDITOR";

	/**
	 * Resource string {@code STR_TOPIC_DN_EDITOR}
	 * <p>
	 * topic_dneditor.html
	 * </p>
	 */
	static String formatSTR_TOPIC_DN_EDITOR(Object... arguments) {
		return format(STR_TOPIC_DN_EDITOR, arguments);
	}

	/**
	 * Resource key {@code STR_TOPIC_CRT_OPTIONS}
	 * <p>
	 * topic_crtoptions.html
	 * </p>
	 */
	static final String STR_TOPIC_CRT_OPTIONS = "STR_TOPIC_CRT_OPTIONS";

	/**
	 * Resource string {@code STR_TOPIC_CRT_OPTIONS}
	 * <p>
	 * topic_crtoptions.html
	 * </p>
	 */
	static String formatSTR_TOPIC_CRT_OPTIONS(Object... arguments) {
		return format(STR_TOPIC_CRT_OPTIONS, arguments);
	}

}
