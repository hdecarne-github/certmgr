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
package de.carne.certmgr.jfx;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Package localization resources.
 */
final class I18N {

	static final String MESSAGE_UNEXPECTED_EXCEPTION = "application.unexpectedexception";
	static final String MESSAGE_MESSAGEBOX_EXCEPTION = "application.messageboxexception";

	static final String MESSAGE_VM_INFO = "application.vminfo";
	static final String MESSAGE_PROVIDER_INFO = "application.providerinfo";
	static final String MESSAGE_PROVIDER_EXCEPTION = "application.providerexception";

	static final String MESSAGE_INVALID_PARAMETER = "application.invalidparameter";

	static final String MESSAGE_VERBOSE_ENABLED = "application.verboseenabled";
	static final String MESSAGE_DEBUG_ENABLED = "application.debugenabled";

	static final String MESSAGE_PREFSYNCFAILED = "stagecontroller.prefsyncfailed";

	static final String TEXT_TIMEPERIOD_DAYS = "timeperiod.days";
	static final String TEXT_TIMEPERIOD_MONTHS = "timeperiod.months";
	static final String TEXT_TIMEPERIOD_YEARS = "timeperiod.years";

	static final String TEXT_FILTERALL = "certfileformat.filterall";
	static final String TEXT_FILTERFORMAT = "certfileformat.filterformat";

	static final String TEXT_CLIPBOARDEXPORTTARGET = "exporttarget.clipboard";

	static ResourceBundle bundle() {
		return ResourceBundle.getBundle(I18N.class.getName());
	}

	static String format(String pattern, Object... arguments) {
		return MessageFormat.format(bundle().getString(pattern), arguments);
	}

}
