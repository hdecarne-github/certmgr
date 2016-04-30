/*
 * I18N resource strings
 *
 * Generated on 4/30/16 6:36 AM
 */
package de.carne.certmgr.store.provider.bouncycastle;

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
	 * Resource key {@code STR_GENERATE_CSR}
	 * <p>
	 * Generate and sign X.509 certificate signing request ''{0}''...
	 * </p>
	 */
	static final String STR_GENERATE_CSR = "STR_GENERATE_CSR";

	/**
	 * Resource string {@code STR_GENERATE_CSR}
	 * <p>
	 * Generate and sign X.509 certificate signing request ''{0}''...
	 * </p>
	 */
	static String formatSTR_GENERATE_CSR(Object... arguments) {
		return format(STR_GENERATE_CSR, arguments);
	}

	/**
	 * Resource key {@code STR_GENERATE_CRT}
	 * <p>
	 * Generate and sign X.509 certificate ''{0}''...
	 * </p>
	 */
	static final String STR_GENERATE_CRT = "STR_GENERATE_CRT";

	/**
	 * Resource string {@code STR_GENERATE_CRT}
	 * <p>
	 * Generate and sign X.509 certificate ''{0}''...
	 * </p>
	 */
	static String formatSTR_GENERATE_CRT(Object... arguments) {
		return format(STR_GENERATE_CRT, arguments);
	}

}
