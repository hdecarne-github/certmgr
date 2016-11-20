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

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

/**
 * Controller for entering a new password.
 */
public class EnterNewPasswordController extends PasswordController {

	@FXML
	PasswordField ctlPasswordInput1;

	@FXML
	PasswordField ctlPasswordInput2;

	@Override
	protected String getHeaderText(String resource) {
		return EnterNewPasswordI18N.formatSTR_LABEL_ENTER_NEWPASSWORD_HEADER(resource);
	}

	@Override
	protected String getPasswordInput() {
		return this.ctlPasswordInput1.getText();
	}

	@Override
	protected boolean getRememberPasswordOption() {
		return false;
	}

}
