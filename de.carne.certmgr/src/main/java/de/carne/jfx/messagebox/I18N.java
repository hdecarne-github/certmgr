/*
 * Copyright (c) 2014-2016 Holger de Carne and contributors, All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.carne.jfx.messagebox;

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

	static String TEXT_TITLE(Object... arguments) {
		return format("messagebox.title", arguments);
	}

	static String TEXT_BUTTON_OK(Object... arguments) {
		return format("button.ok", arguments);
	}

	static String TEXT_BUTTON_CANCEL(Object... arguments) {
		return format("button.cancel", arguments);
	}

	static String TEXT_BUTTON_YES(Object... arguments) {
		return format("button.yes", arguments);
	}

	static String TEXT_BUTTON_NO(Object... arguments) {
		return format("button.no", arguments);
	}

}
