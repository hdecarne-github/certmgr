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

import java.util.Comparator;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.security.DefaultSet;
import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.jfx.scene.control.DialogController;
import javafx.collections.ObservableList;
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

	private boolean expertMode = false;

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
	ComboBox<KeyPairAlgorithm> ctlDefKeyAlgOption;

	@FXML
	ComboBox<Integer> ctlDefKeySizeOption;

	@FXML
	ComboBox<SignatureAlgorithm> ctlDefSigAlgOption;

	@FXML
	void onCmdChoosePath(ActionEvent evt) {

	}

	private void onDefKeyAlgChanged(KeyPairAlgorithm keyAlg) {
		resetComboBoxOptions(this.ctlDefKeySizeOption, keyAlg.getStandardKeySizes(), (o1, o2) -> o1.compareTo(o2));
		resetComboBoxOptions(this.ctlDefSigAlgOption, SignatureAlgorithm.getAll(keyAlg.algorithm(), this.expertMode),
				(o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	@Override
	protected void setupDialog(Dialog<UserCertStore> dialog) {
		dialog.setTitle(StoreOptionsI18N.formatSTR_STAGE_TITLE());
		this.ctlDefKeyAlgOption.getSelectionModel().selectedItemProperty()
				.addListener((p, o, n) -> onDefKeyAlgChanged(n));
	}

	public StoreOptionsController init(boolean expertModeParam) {
		this.store = null;
		this.expertMode = expertModeParam;
		initKeyAlgOptions();
		return this;
	}

	public StoreOptionsController init(UserCertStore storeParam, boolean expertModeParam) {
		assert storeParam != null;

		this.store = storeParam;
		this.expertMode = expertModeParam;
		initKeyAlgOptions();
		return this;
	}

	private void initKeyAlgOptions() {
		resetComboBoxOptions(this.ctlDefKeyAlgOption, KeyPairAlgorithm.getAll(this.expertMode),
				(o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	@Override
	public UserCertStore call(ButtonType param) {
		return null;
	}

	private static <T> void resetComboBoxOptions(ComboBox<T> control, DefaultSet<T> defaultSet,
			Comparator<T> comparator) {
		ObservableList<T> options = control.getItems();

		options.clear();
		if (defaultSet != null && !defaultSet.isEmpty()) {
			options.addAll(defaultSet);
			options.sort(comparator);
			control.getSelectionModel().select(defaultSet.getDefault());
			control.setDisable(false);
		} else {
			control.setDisable(!control.isEditable());
		}
	}

}
