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
package de.carne.certmgr.jfx.certimport;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Package localization resources.
 */
final class I18N {

	static final String TEXT_TITLE = "certimport.title";

	static final String TEXT_FILTERALL = "certimport.filterall";
	static final String TEXT_FILTERFORMAT = "certimport.filterformat";

	static final String TEXT_CLIPBOARDRESOURCE0 = "certimport.clipboardresource0";
	static final String TEXT_CLIPBOARDRESOURCE1 = "certimport.clipboardresource1";

	static final String MESSAGE_SETUPSOURCE = "certimport.setupsource";
	static final String MESSAGE_NOFILESOURCE = "certimport.nofilesource";
	static final String MESSAGE_INVALIDFILESOURCE = "certimport.invalidfilesource";
	static final String MESSAGE_NOFOLDERSOURCE = "certimport.nofoldersource";
	static final String MESSAGE_INVALIDFOLDERSOURCE = "certimport.invalidfoldersource";
	static final String MESSAGE_NOURLSOURCE = "certimport.nourlsource";
	static final String MESSAGE_INVALIDURLSOURCE = "certimport.invalidurlsource";
	static final String MESSAGE_INVALIDCLIPBOARDSOURCE = "certimport.invalidclipboardsource";
	static final String MESSAGE_NOIMPORTSELECTION = "certimport.noimportselection";

	static final String MESSAGE_LOADERROR = "certimport.loaderror";
	static final String MESSAGE_LOADEDENTRYCOUNT = "certimport.loadedentrycount";

	static final String MESSAGE_IMPORTFAILED = "certimport.importfailed";

	static ResourceBundle bundle() {
		return ResourceBundle.getBundle(I18N.class.getName());
	}

	static String format(String pattern, Object... arguments) {
		return MessageFormat.format(bundle().getString(pattern), arguments);
	}

}
