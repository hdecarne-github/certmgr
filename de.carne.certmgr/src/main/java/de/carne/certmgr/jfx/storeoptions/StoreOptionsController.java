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
package de.carne.certmgr.jfx.storeoptions;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.jfx.scene.control.DialogController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * Store options dialog.
 */
public class StoreOptionsController extends DialogController<UserCertStore>
		implements Callback<ButtonType, UserCertStore> {

	private UserCertStore store = null;

	@FXML
	TextField ctlNameInput;

	@FXML
	TextField ctlPathInput;

	@FXML
	Button cmdChoosePathButton;

	@FXML
	ComboBox<?> ctlDefValidityInput;

	@FXML
	ComboBox<?> ctlDefCrlUpdateInput;

	@FXML
	ComboBox<?> ctlDefKeyAlgOption;

	@FXML
	ComboBox<?> ctlDefKeySizeOption;

	@FXML
	ComboBox<?> ctlDefSigAlgOption;

	@FXML
	void onCmdChoosePath(ActionEvent evt) {

	}

	@Override
	protected void setupDialog(Dialog<UserCertStore> dialog) {

	}

	public StoreOptionsController init(UserCertStore storeParam) {
		this.store = storeParam;
		return this;
	}

	@Override
	public UserCertStore call(ButtonType param) {
		return null;
	}

}
