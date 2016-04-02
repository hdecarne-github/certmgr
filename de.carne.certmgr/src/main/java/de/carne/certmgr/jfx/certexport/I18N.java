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
package de.carne.certmgr.jfx.certexport;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Package localization resources.
 */
final class I18N {

	static final String TEXT_TITLE = "certexport.title";

	static final String MESSAGE_NOEXPORTCERT = "certerxport.noexportcert";
	static final String MESSAGE_NOENCODING = "certerxport.noencoding";
	static final String MESSAGE_NOEXPORTOBJECT = "certexport.noexportobject";
	static final String MESSAGE_NOFILETARGET = "certexport.noexportfile";
	static final String MESSAGE_INVALIDFILETARGET = "certexport.invalidexportfile";
	static final String MESSAGE_NOFOLDERTARGET = "certexport.noexportfolder";
	static final String MESSAGE_INVALIDFOLDERTARGET = "certexport.invalidexportfolder";
	static final String MESSAGE_NOEXPORTTARGET = "certexport.noexporttarget";
	static final String MESSAGE_EXPORTFAILED = "certexport.exportfailed";

	static ResourceBundle bundle() {
		return ResourceBundle.getBundle(I18N.class.getName());
	}

	static String format(String pattern, Object... arguments) {
		return MessageFormat.format(bundle().getString(pattern), arguments);
	}

}
