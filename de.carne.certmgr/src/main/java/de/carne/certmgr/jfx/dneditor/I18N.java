/*
 * I18N resource strings
 *
 * Generated on 4/30/16 6:36 AM
 */
package de.carne.certmgr.jfx.dneditor;

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
	 * Resource key {@code STR_DN_EDITOR_TITLE}
	 * <p>
	 * Edit DN
	 * </p>
	 */
	static final String STR_DN_EDITOR_TITLE = "STR_DN_EDITOR_TITLE";

	/**
	 * Resource string {@code STR_DN_EDITOR_TITLE}
	 * <p>
	 * Edit DN
	 * </p>
	 */
	static String formatSTR_DN_EDITOR_TITLE(Object... arguments) {
		return format(STR_DN_EDITOR_TITLE, arguments);
	}

}
