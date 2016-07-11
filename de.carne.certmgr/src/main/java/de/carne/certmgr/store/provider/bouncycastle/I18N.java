/*
 * I18N resource strings
 *
 * Generated on 11.07.2016 11:39:09
 */
package de.carne.certmgr.store.provider.bouncycastle;

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
	 * Resource key {@code STR_GENERATE_CSR}
	 * <p>
	 * Generate and sign X.509 certificate signing request ''{0}''...
	 * </p>
	 */
	public static final String STR_GENERATE_CSR = "STR_GENERATE_CSR";

	/**
	 * Resource string {@code STR_GENERATE_CSR}
	 * <p>
	 * Generate and sign X.509 certificate signing request ''{0}''...
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_GENERATE_CSR(Object... arguments) {
		return format(STR_GENERATE_CSR, arguments);
	}

	/**
	 * Resource key {@code STR_GENERATE_CRT}
	 * <p>
	 * Generate and sign X.509 certificate ''{0}''...
	 * </p>
	 */
	public static final String STR_GENERATE_CRT = "STR_GENERATE_CRT";

	/**
	 * Resource string {@code STR_GENERATE_CRT}
	 * <p>
	 * Generate and sign X.509 certificate ''{0}''...
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_GENERATE_CRT(Object... arguments) {
		return format(STR_GENERATE_CRT, arguments);
	}

}
