/*
 * Copyright (c) 2015-2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.jfx.password;

import de.carne.jfx.scene.control.DialogController;
import de.carne.jfx.scene.control.DialogHelper;

/**
 * Base class for all password controllers.
 */
abstract class PasswordController extends DialogController<PasswordResult> {

	public PasswordController init(String resource, boolean rememberPassword, Throwable passwordException) {
		getUI().setHeaderText(getHeaderText(resource));
		DialogHelper.setExceptionContent(getUI(), passwordException);
		return this;
	}

	protected abstract String getHeaderText(String resource);

	protected abstract String getPasswordInput();

	protected abstract boolean getRememberPasswordOption();

}
