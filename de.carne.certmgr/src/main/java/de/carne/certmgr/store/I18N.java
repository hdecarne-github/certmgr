/*
 * I18N resource strings
 *
 * Generated on 07.05.2016 08:43:41
 */
package de.carne.certmgr.store;

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
	 * Resource key {@code STR_SKIPPING_CRL_EXPORT}
	 * <p>
	 * Certificate entry<br/>''{0}''<br/>does not contain a certificate revocation list to export.
	 * </p>
	 */
	public static final String STR_SKIPPING_CRL_EXPORT = "STR_SKIPPING_CRL_EXPORT";

	/**
	 * Resource string {@code STR_SKIPPING_CRL_EXPORT}
	 * <p>
	 * Certificate entry<br/>''{0}''<br/>does not contain a certificate revocation list to export.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_SKIPPING_CRL_EXPORT(Object... arguments) {
		return format(STR_SKIPPING_CRL_EXPORT, arguments);
	}

	/**
	 * Resource key {@code STR_CREATE_STORE}
	 * <p>
	 * Creating store: ''{0}''
	 * </p>
	 */
	public static final String STR_CREATE_STORE = "STR_CREATE_STORE";

	/**
	 * Resource string {@code STR_CREATE_STORE}
	 * <p>
	 * Creating store: ''{0}''
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CREATE_STORE(Object... arguments) {
		return format(STR_CREATE_STORE, arguments);
	}

	/**
	 * Resource key {@code STR_INCOMPLETE_CERT_ENTRY}
	 * <p>
	 * Ignoring incomplete certificate entry ''{0}'' (key:''{1}'' crt:''{2}'' csr:''{3}'' crl:''{4}'') 
	 * </p>
	 */
	public static final String STR_INCOMPLETE_CERT_ENTRY = "STR_INCOMPLETE_CERT_ENTRY";

	/**
	 * Resource string {@code STR_INCOMPLETE_CERT_ENTRY}
	 * <p>
	 * Ignoring incomplete certificate entry ''{0}'' (key:''{1}'' crt:''{2}'' csr:''{3}'' crl:''{4}'') 
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_INCOMPLETE_CERT_ENTRY(Object... arguments) {
		return format(STR_INCOMPLETE_CERT_ENTRY, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_CERT_ENTRY}
	 * <p>
	 * Ignoring invalid certificate entry ''{0}'' (key:''{1}'' crt:''{2}'' csr:''{3}'' crl:''{4}'') 
	 * </p>
	 */
	public static final String STR_INVALID_CERT_ENTRY = "STR_INVALID_CERT_ENTRY";

	/**
	 * Resource string {@code STR_INVALID_CERT_ENTRY}
	 * <p>
	 * Ignoring invalid certificate entry ''{0}'' (key:''{1}'' crt:''{2}'' csr:''{3}'' crl:''{4}'') 
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_INVALID_CERT_ENTRY(Object... arguments) {
		return format(STR_INVALID_CERT_ENTRY, arguments);
	}

	/**
	 * Resource key {@code STR_OPEN_STORE}
	 * <p>
	 * Opening store: ''{0}'' 
	 * </p>
	 */
	public static final String STR_OPEN_STORE = "STR_OPEN_STORE";

	/**
	 * Resource string {@code STR_OPEN_STORE}
	 * <p>
	 * Opening store: ''{0}'' 
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_OPEN_STORE(Object... arguments) {
		return format(STR_OPEN_STORE, arguments);
	}

	/**
	 * Resource key {@code STR_SKIPPING_CSR_EXPORT}
	 * <p>
	 * Certificate entry<br/>''{0}''<br/>does not contain a certificate signing request to export.
	 * </p>
	 */
	public static final String STR_SKIPPING_CSR_EXPORT = "STR_SKIPPING_CSR_EXPORT";

	/**
	 * Resource string {@code STR_SKIPPING_CSR_EXPORT}
	 * <p>
	 * Certificate entry<br/>''{0}''<br/>does not contain a certificate signing request to export.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_SKIPPING_CSR_EXPORT(Object... arguments) {
		return format(STR_SKIPPING_CSR_EXPORT, arguments);
	}

	/**
	 * Resource key {@code STR_SKIPPING_KEY_EXPORT}
	 * <p>
	 * Certificate entry<br/>''{0}''<br/>does not contain a key to export.
	 * </p>
	 */
	public static final String STR_SKIPPING_KEY_EXPORT = "STR_SKIPPING_KEY_EXPORT";

	/**
	 * Resource string {@code STR_SKIPPING_KEY_EXPORT}
	 * <p>
	 * Certificate entry<br/>''{0}''<br/>does not contain a key to export.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_SKIPPING_KEY_EXPORT(Object... arguments) {
		return format(STR_SKIPPING_KEY_EXPORT, arguments);
	}

	/**
	 * Resource key {@code STR_SKIPPING_CRT_EXPORT}
	 * <p>
	 * Certificate entry<br/>''{0}''<br/>does not contain a certificate to export.
	 * </p>
	 */
	public static final String STR_SKIPPING_CRT_EXPORT = "STR_SKIPPING_CRT_EXPORT";

	/**
	 * Resource string {@code STR_SKIPPING_CRT_EXPORT}
	 * <p>
	 * Certificate entry<br/>''{0}''<br/>does not contain a certificate to export.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_SKIPPING_CRT_EXPORT(Object... arguments) {
		return format(STR_SKIPPING_CRT_EXPORT, arguments);
	}

	/**
	 * Resource key {@code STR_PASSWORD_REQUIRED}
	 * <p>
	 * A password is required to access ''{0}''.
	 * </p>
	 */
	public static final String STR_PASSWORD_REQUIRED = "STR_PASSWORD_REQUIRED";

	/**
	 * Resource string {@code STR_PASSWORD_REQUIRED}
	 * <p>
	 * A password is required to access ''{0}''.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_PASSWORD_REQUIRED(Object... arguments) {
		return format(STR_PASSWORD_REQUIRED, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_PASSWORD}
	 * <p>
	 * The password entered for ''{0}'' is invalid.
	 * </p>
	 */
	public static final String STR_INVALID_PASSWORD = "STR_INVALID_PASSWORD";

	/**
	 * Resource string {@code STR_INVALID_PASSWORD}
	 * <p>
	 * The password entered for ''{0}'' is invalid.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_INVALID_PASSWORD(Object... arguments) {
		return format(STR_INVALID_PASSWORD, arguments);
	}

	/**
	 * Resource key {@code STR_SKIPPING_CERT_IMPORT}
	 * <p>
	 * Certificate entry ''{0}'' is already in the store. Skipping import.
	 * </p>
	 */
	public static final String STR_SKIPPING_CERT_IMPORT = "STR_SKIPPING_CERT_IMPORT";

	/**
	 * Resource string {@code STR_SKIPPING_CERT_IMPORT}
	 * <p>
	 * Certificate entry ''{0}'' is already in the store. Skipping import.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_SKIPPING_CERT_IMPORT(Object... arguments) {
		return format(STR_SKIPPING_CERT_IMPORT, arguments);
	}

	/**
	 * Resource key {@code STR_CERT_ENTRY_ERROR}
	 * <p>
	 * An error occurred while processing certificate entry ''{0}'' (Cause: ''{1}'')
	 * </p>
	 */
	public static final String STR_CERT_ENTRY_ERROR = "STR_CERT_ENTRY_ERROR";

	/**
	 * Resource string {@code STR_CERT_ENTRY_ERROR}
	 * <p>
	 * An error occurred while processing certificate entry ''{0}'' (Cause: ''{1}'')
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CERT_ENTRY_ERROR(Object... arguments) {
		return format(STR_CERT_ENTRY_ERROR, arguments);
	}

}
