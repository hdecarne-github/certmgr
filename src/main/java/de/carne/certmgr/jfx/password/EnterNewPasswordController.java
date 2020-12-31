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

import java.util.Objects;

import de.carne.certmgr.jfx.resources.Images;
import de.carne.jfx.scene.control.Tooltips;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;

/**
 * Controller for entering a new password.
 */
public class EnterNewPasswordController extends PasswordController {

	@SuppressWarnings("null")
	@FXML
	PasswordField ctlPasswordInput1;

	@SuppressWarnings("null")
	@FXML
	PasswordField ctlPasswordInput2;

	private void onOk(ActionEvent evt) {
		String passwordInput1 = this.ctlPasswordInput1.getText();
		String passwordInput2 = this.ctlPasswordInput2.getText();

		if (!Objects.equals(passwordInput1, passwordInput2)) {
			Tooltips.show(this.ctlPasswordInput2, EnterNewPasswordI18N.strMessagePasswordMismatch(), Images.WARNING16);
			evt.consume();
		}
	}

	@Override
	protected void setupDialog(Dialog<PasswordResult> dialog) {
		dialog.setTitle(EnterNewPasswordI18N.strStageTitle());
		((Button) lookupButton(ButtonType.YES)).setText(EnterNewPasswordI18N.strTextOk());
		((Button) lookupButton(ButtonType.NO)).setText(EnterNewPasswordI18N.strTextCancel());
		((Button) lookupButton(ButtonType.CANCEL)).setText(EnterNewPasswordI18N.strTextCancelall());
		addButtonEventFilter(ButtonType.YES, this::onOk);
		this.ctlPasswordInput1.requestFocus();
	}

	@Override
	protected String getHeaderText(String resource) {
		return EnterNewPasswordI18N.strLabelEnterNewpasswordHeader(resource);
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
