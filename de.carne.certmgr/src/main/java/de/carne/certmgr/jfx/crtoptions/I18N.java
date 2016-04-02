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
package de.carne.certmgr.jfx.crtoptions;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Package localization resources.
 */
final class I18N {

	static final String TEXT_GENERATECRTTITLE = "crtoptions.generatecrttitle";
	static final String TEXT_REGENERATECRTTITLE = "crtoptions.regeneratecrttitle";
	static final String TEXT_GENERATECSRTITLE = "crtoptions.generatecsrtitle";
	static final String TEXT_REGENERATECSRTITLE = "crtoptions.regeneratecsrtitle";

	static final String TEXT_SELFSIGNED = "crtoptions.selfsigned";

	static final String TEXT_EXTENSION_UNUSED = "crtoptions.extensionunused";
	static final String TEXT_EXTENSION_CRITICAL = "crtoptions.extensioncritical";
	static final String TEXT_EXTENSION_NONCRITICAL = "crtoptions.extensionnoncritical";

	static final String MESSAGE_NOSUBJECTDN = "crtoptions.nosubjectdn";
	static final String MESSAGE_INVALIDSUBJECTDN = "crtoptions.invalidsubjectdn";
	static final String MESSAGE_NOALIAS = "crtoptions.noalias";
	static final String MESSAGE_INVALIDALIAS = "crtoptions.invalidalias";
	static final String MESSAGE_NOKEYALG = "crtoptions.nokeyalg";
	static final String MESSAGE_NOKEYSIZE = "crtoptions.nokeysize";
	static final String MESSAGE_NOVALIDFROM = "crtoptions.novalidfrom";
	static final String MESSAGE_NOVALIDTO = "crtoptions.novalidto";
	static final String MESSAGE_INVALIDVALIDITY = "crtoptions.invalidvalidity";
	static final String MESSAGE_NOSIGALG = "crtoptions.nosigalg";
	static final String MESSAGE_NOPATHLENCONSTRAINT = "crtoptions.nopathlenconstraint";
	static final String MESSAGE_NOKEYUSAGE = "crtoptions.nokeyusage";
	static final String MESSAGE_NOEXTENDEDKEYUSAGE = "crtoptions.noextendedkeyusage";
	static final String MESSAGE_NOSUBJECTALTERNATIVENAME = "crtoptions.nosubjectalternativename";
	static final String MESSAGE_INVALIDSUBJECTALTERNATIVENAME = "crtoptions.invalidsubjectalternativename";
	static final String MESSAGE_NOISSUERNAME = "crtoptions.noissuername";
	static final String MESSAGE_INVALIDISSUERNAME = "crtoptions.invalidissuername";
	static final String MESSAGE_GENERATEERROR = "crtoptions.generateerror";
	static final String MESSAGE_CERTENTRYERROR = "crtoptions.certentryerror";

	static ResourceBundle bundle() {
		return ResourceBundle.getBundle(I18N.class.getName());
	}

	static String format(String pattern, Object... arguments) {
		return MessageFormat.format(bundle().getString(pattern), arguments);
	}

}
