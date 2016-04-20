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
package de.carne.certmgr.jfx.storemanager;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Package localization resources.
 */
final class I18N {

	static final String TEXT_TITLE = "storemanager.title";

	static final String TEXT_CERT_STORE = "storemanager.certstore";
	static final String TEXT_CERT_ENTRY = "storemanager.certentry";
	static final String TEXT_CRT_OBJECT = "storemanager.crtobject";
	static final String TEXT_CSR_OBJECT = "storemanager.csrobject";
	static final String TEXT_CRL_OBJECT = "storemanager.crlobject";
	static final String TEXT_EXTENSION_OBJECT = "storemanager.extensionobject";

	static final String TEXT_ABOUTTITLE1 = "storemanager.abouttitle1";
	static final String TEXT_ABOUTINFO1 = "storemanager.aboutinfo1";
	static final String TEXT_ABOUTTITLE2 = "storemanager.abouttitle2";
	static final String TEXT_ABOUTINFO2 = "storemanager.aboutinfo2";
	static final String TEXT_ABOUTTITLE3 = "storemanager.abouttitle3";
	static final String TEXT_ABOUTINFO3 = "storemanager.aboutinfo3";

	static final String MESSAGE_CONFIRMDELETEENTRY = "storemanager.confirmdeleteentry";

	static final String MESSAGE_CERTENTRYERROR = "storemanager.certentryerror";

	static ResourceBundle bundle() {
		return ResourceBundle.getBundle(I18N.class.getName());
	}

	static String format(String pattern, Object... arguments) {
		return MessageFormat.format(bundle().getString(pattern), arguments);
	}

}
