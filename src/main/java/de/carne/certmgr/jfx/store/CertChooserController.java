/*
 * Copyright (c) 2015-2017 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.jfx.store;

import java.math.BigInteger;
import java.util.Date;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.check.Nullable;
import de.carne.jfx.scene.control.DialogController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

/**
 * Store options dialog.
 */
public class CertChooserController extends DialogController<UserCertStore>
		implements Callback<ButtonType, UserCertStore> {

	@Nullable
	private UserCertStore store = null;

	@SuppressWarnings("null")
	@FXML
	TreeTableView<StoreEntryModel> ctlStoreEntryView;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<StoreEntryModel, String> ctlStoreEntryViewId;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<StoreEntryModel, String> ctlStoreEntryViewName;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<StoreEntryModel, BigInteger> ctlStoreEntryViewSerial;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<StoreEntryModel, Date> ctlStoreEntryViewExpires;

	@SuppressWarnings("unused")
	@FXML
	private void onCmdSelect(ActionEvent evt) {

	}

	@SuppressWarnings("unused")
	@FXML
	private void onCmdCancel(ActionEvent evt) {

	}

	@Override
	protected void setupDialog(Dialog<UserCertStore> dialog) {
		dialog.setTitle(CertChooserI18N.formatSTR_STAGE_TITLE());
		addButtonEventFilter(ButtonType.APPLY, (evt) -> onCmdSelect(evt));
	}

	/**
	 * Initialize dialog for editing an existing store's preferences.
	 *
	 * @param storeParam The store to edit the preferences for.
	 * @return This controller.
	 */
	public CertChooserController init(UserCertStore storeParam) {
		this.store = storeParam;
		return this;
	}

	@Override
	public UserCertStore call(@Nullable ButtonType param) {
		return this.store;
	}

}
