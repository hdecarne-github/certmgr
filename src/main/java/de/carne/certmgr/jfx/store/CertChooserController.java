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
import java.util.function.Predicate;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.jfx.util.UserCertStoreTreeTableViewHelper;
import de.carne.check.Nullable;
import de.carne.jfx.scene.control.DialogController;
import de.carne.util.Late;
import de.carne.util.Lazy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.Callback;

/**
 * Choose certificate dialog.
 */
public class CertChooserController extends DialogController<UserCertStoreEntry>
		implements Callback<ButtonType, UserCertStoreEntry> {

	private final Lazy<UserCertStoreTreeTableViewHelper<StoreEntryModel>> storeEntryViewHelper = new Lazy<>(
			() -> new UserCertStoreTreeTableViewHelper<>(this.ctlStoreEntryView, (e) -> new StoreEntryModel(e)));

	private final Late<Predicate<UserCertStoreEntry>> selectionFilterParam = new Late<>();

	@Nullable
	private UserCertStoreEntry result = null;

	@SuppressWarnings("null")
	@FXML
	private TreeTableView<StoreEntryModel> ctlStoreEntryView;

	@SuppressWarnings("null")
	@FXML
	private TreeTableColumn<StoreEntryModel, String> ctlStoreEntryViewId;

	@SuppressWarnings("null")
	@FXML
	private TreeTableColumn<StoreEntryModel, String> ctlStoreEntryViewName;

	@SuppressWarnings("null")
	@FXML
	private TreeTableColumn<StoreEntryModel, BigInteger> ctlStoreEntryViewSerial;

	@SuppressWarnings("null")
	@FXML
	private TreeTableColumn<StoreEntryModel, Date> ctlStoreEntryViewExpires;

	@FXML
	private void onCmdApply(ActionEvent evt) {
		TreeItem<StoreEntryModel> selectedItem = this.ctlStoreEntryView.getSelectionModel().getSelectedItem();

		if (selectedItem != null) {
			UserCertStoreEntry selectedEntry = selectedItem.getValue().getEntry();

			if (this.selectionFilterParam.get().test(selectedEntry)) {
				this.result = selectedEntry;
			}
		}
		if (this.result == null) {
			evt.consume();
		}
	}

	@Override
	protected void setupDialog(Dialog<UserCertStoreEntry> dialog) {
		dialog.setTitle(CertChooserI18N.formatSTR_STAGE_TITLE());
		this.ctlStoreEntryViewId.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
		this.ctlStoreEntryViewName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		this.ctlStoreEntryViewSerial.setCellValueFactory(new TreeItemPropertyValueFactory<>("serial"));
		this.ctlStoreEntryViewExpires.setCellValueFactory(new TreeItemPropertyValueFactory<>("expires"));
		addButtonEventFilter(ButtonType.APPLY, this::onCmdApply);
	}

	/**
	 * Initialize dialog for editing an existing store's preferences.
	 *
	 * @param store The store to select an entry from.
	 * @return This controller.
	 */
	public CertChooserController init(UserCertStore store) {
		return init(store, (entry) -> true);
	}

	/**
	 * Initialize dialog for editing an existing store's preferences.
	 *
	 * @param store The store to select an entry from.
	 * @param selectionFilter The predicate to determine whether a selection is valid.
	 * @return This controller.
	 */
	public CertChooserController init(UserCertStore store, Predicate<UserCertStoreEntry> selectionFilter) {
		this.storeEntryViewHelper.get().update(store);
		this.selectionFilterParam.init(selectionFilter);
		return this;
	}

	@Nullable
	@Override
	public UserCertStoreEntry call(@Nullable ButtonType param) {
		return this.result;
	}

}
