/*
 * Copyright (c) 2015-2021 Holger de Carne and contributors, All Rights Reserved.
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

import org.eclipse.jdt.annotation.Nullable;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;

/**
 * Controller for entering an existing password.
 */
public class EnterPasswordController extends PasswordController {

	@SuppressWarnings("null")
	@FXML
	PasswordField ctlPasswordInput;

	@SuppressWarnings("null")
	@FXML
	CheckBox ctlRememberPassword;

	@Override
	protected void setupDialog(Dialog<PasswordResult> dialog) {
		dialog.setTitle(EnterPasswordI18N.strStageTitle());
		((Button) lookupButton(ButtonType.YES)).setText(EnterPasswordI18N.strTextOk());
		((Button) lookupButton(ButtonType.NO)).setText(EnterPasswordI18N.strTextCancel());
		((Button) lookupButton(ButtonType.CANCEL)).setText(EnterPasswordI18N.strTextCancelall());
		this.ctlPasswordInput.requestFocus();
	}

	@Override
	public PasswordController init(String resource, boolean rememberPassword, @Nullable Throwable passwordException) {
		this.ctlRememberPassword.setSelected(rememberPassword);
		return super.init(resource, rememberPassword, passwordException);
	}

	@Override
	protected String getHeaderText(String resource) {
		return EnterPasswordI18N.strLabelEnterPasswordHeader(resource);
	}

	@Override
	protected String getPasswordInput() {
		return this.ctlPasswordInput.getText();
	}

	@Override
	protected boolean getRememberPasswordOption() {
		return this.ctlRememberPassword.isSelected();
	}

}
