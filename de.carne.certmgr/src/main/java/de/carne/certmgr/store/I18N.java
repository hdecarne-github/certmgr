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
package de.carne.certmgr.store;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Package localization resources.
 */
final class I18N {

	static final String MESSAGE_CREATESTORE = "store.create";
	static final String MESSAGE_OPENSTORE = "store.open";
	static final String MESSAGE_INCOMPLETECERTENTRY = "store.incompletecertentry";
	static final String MESSAGE_INVALIDCERTENTRY = "store.invalidcertentry";
	static final String MESSAGE_CERTENTRYERROR = "store.certentryerror";
	static final String MESSAGE_INVALIDPASSWORD = "store.invalidpassword";
	static final String MESSAGE_PASSWORDREQUIRED = "store.passwordrequired";
	static final String MESSAGE_NOTHINGTOIMPORT = "store.nothingtoimport";

	static final String MESSAGE_NOKEYTOEXPORT = "exporter.nokeytoexport";
	static final String MESSAGE_NOCRTTOEXPORT = "exporter.nocrttoexport";
	static final String MESSAGE_NOCSRTOEXPORT = "exporter.nocsrtoexport";
	static final String MESSAGE_NOCRLTOEXPORT = "exporter.nocrltoexport";

	static ResourceBundle bundle() {
		return ResourceBundle.getBundle(I18N.class.getName());
	}

	static String format(String pattern, Object... arguments) {
		return MessageFormat.format(bundle().getString(pattern), arguments);
	}

}
