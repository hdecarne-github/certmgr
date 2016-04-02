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
package de.carne.certmgr.jfx.passwordprompt;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Package localization resources.
 */
final class I18N {

	static final String TEXT_PASSWORDTITLE = "passwordprompt.passwordtitle";
	static final String TEXT_NEWPASSWORDTITLE = "passwordprompt.newpasswordtitle";

	static final String MESSAGE_PASSWORDPROMPT = "passwordprompt.password";
	static final String MESSAGE_NEWPASSWORDPROMPT = "passwordprompt.newpassword";

	static final String MESSAGE_INVALIDPASSWORD = "passwordprompt.invalidpassword";
	static final String MESSAGE_PASSWORDMISMATCH = "passwordprompt.passwordmismatch";
	static final String MESSAGE_EMPTYORINCOMPATIBLEPASSWORD = "passwordprompt.emptyorincompatiblepassword";

	static ResourceBundle bundle() {
		return ResourceBundle.getBundle(I18N.class.getName());
	}

	static String format(String pattern, Object... arguments) {
		return MessageFormat.format(bundle().getString(pattern), arguments);
	}

}
