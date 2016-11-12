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
package de.carne.certmgr.jfx.storepreferences;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStorePreferences;
import de.carne.certmgr.certs.security.CRLUpdatePeriod;
import de.carne.certmgr.certs.security.CRTValidityPeriod;
import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.util.Days;
import de.carne.certmgr.util.DefaultSet;
import de.carne.jfx.scene.control.Alerts;
import de.carne.jfx.scene.control.DialogController;
import de.carne.jfx.util.validation.ValidationAlerts;
import de.carne.util.Strings;
import de.carne.util.validation.InputValidator;
import de.carne.util.validation.PathValidator;
import de.carne.util.validation.ValidationException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

/**
 * Store options dialog.
 */
public class StorePreferencesController extends DialogController<UserCertStore>
		implements Callback<ButtonType, UserCertStore> {

	private UserCertStore store = null;

	private UserCertStorePreferences storePreferences = null;

	private boolean expertMode = false;

	@FXML
	TextField ctlNameInput;

	@FXML
	TextField ctlPathInput;

	@FXML
	Button cmdChoosePathButton;

	@FXML
	ComboBox<CRTValidityPeriod> ctlDefCRTValidityInput;

	@FXML
	ComboBox<CRLUpdatePeriod> ctlDefCRLUpdateInput;

	@FXML
	ComboBox<KeyPairAlgorithm> ctlDefKeyAlgOption;

	@FXML
	ComboBox<Integer> ctlDefKeySizeOption;

	@FXML
	ComboBox<SignatureAlgorithm> ctlDefSigAlgOption;

	@FXML
	void onCmdChoosePath(ActionEvent evt) {
		DirectoryChooser chooser = new DirectoryChooser();
		File path = chooser.showDialog(getWindow());

		if (path != null) {
			this.ctlPathInput.setText(path.toString());
		}
	}

	private void onDefKeyAlgChanged(KeyPairAlgorithm keyAlg) {
		Integer keySizeDefaultHint = null;
		String sigAlgDefaultHint = null;

		if (this.storePreferences != null
				&& keyAlg.algorithm().equals(this.storePreferences.defaultKeyPairAlgorithm.get())) {
			keySizeDefaultHint = this.storePreferences.defaultKeySize.get();
			sigAlgDefaultHint = this.storePreferences.defaultSignatureAlgorithm.get();
		}
		resetComboBoxOptions(this.ctlDefKeySizeOption, keyAlg.getStandardKeySizes(keySizeDefaultHint),
				(o1, o2) -> o1.compareTo(o2));
		resetComboBoxOptions(this.ctlDefSigAlgOption,
				SignatureAlgorithm.getDefaultSet(keyAlg.algorithm(), sigAlgDefaultHint, this.expertMode),
				(o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	private void onApply(ActionEvent evt) {
		if (this.store == null) {
			try {
				Path storeHome = validateStoreHomeInput();

				this.store = UserCertStore.createStore(storeHome);
				this.storePreferences = this.store.storePreferences();
			} catch (ValidationException e) {
				ValidationAlerts.error(e).showAndWait();
				evt.consume();
			} catch (Exception e) {
				Alerts.unexpected(e).showAndWait();
				evt.consume();
			}
		}
		if (this.storePreferences != null) {
			try {
				this.storePreferences.defaultCRTValidityPeriod
						.put(this.ctlDefCRTValidityInput.getSelectionModel().getSelectedItem().days().count());
				this.storePreferences.defaultCRLUpdatePeriod
						.put(this.ctlDefCRLUpdateInput.getSelectionModel().getSelectedItem().days().count());
				this.storePreferences.defaultKeyPairAlgorithm
						.put(this.ctlDefKeyAlgOption.getSelectionModel().getSelectedItem().algorithm());
				this.storePreferences.defaultKeySize
						.put(this.ctlDefKeySizeOption.getSelectionModel().getSelectedItem());
				this.storePreferences.defaultSignatureAlgorithm
						.put(this.ctlDefSigAlgOption.getSelectionModel().getSelectedItem().algorithm());
				this.storePreferences.sync();
			} catch (Exception e) {
				Alerts.unexpected(e).showAndWait();
				evt.consume();
			}
		}
	}

	@Override
	protected void setupDialog(Dialog<UserCertStore> dialog) {
		dialog.setTitle(StorePreferencesI18N.formatSTR_STAGE_TITLE());
		this.ctlDefKeyAlgOption.getSelectionModel().selectedItemProperty()
				.addListener((p, o, n) -> onDefKeyAlgChanged(n));
		addButtonEventFilter(ButtonType.APPLY, (evt) -> onApply(evt));
	}

	/**
	 * Initialize dialog for creating a new store.
	 *
	 * @param expertModeParam Whether to run in expert mode ({@code true}) or
	 *        not ({@code false}).
	 * @return This controller.
	 */
	public StorePreferencesController init(boolean expertModeParam) {
		this.store = null;
		this.expertMode = expertModeParam;
		this.ctlDefKeySizeOption.setEditable(this.expertMode);
		initCRTValidities();
		initCRLUpdatePeriods();
		initKeyAlgOptions();
		((Button) lookupButton(ButtonType.APPLY)).setText(StorePreferencesI18N.formatSTR_TEXT_CREATE());
		return this;
	}

	/**
	 * Initialize dialog for editing an existing store's preferences.
	 *
	 * @param storeParam The store to edit the preferences for.
	 * @param expertModeParam Whether to run in expert mode ({@code true}) or
	 *        not ({@code false}).
	 * @return This controller.
	 */
	public StorePreferencesController init(UserCertStore storeParam, boolean expertModeParam) {
		assert storeParam != null;

		this.store = storeParam;
		this.storePreferences = this.store.storePreferences();
		this.expertMode = expertModeParam;

		Path storeHome = this.store.storeHome();

		this.ctlNameInput.setText(storeHome.getFileName().toString());
		this.ctlNameInput.setDisable(true);
		this.ctlPathInput.setText(storeHome.getParent().toString());
		this.ctlPathInput.setDisable(true);
		this.cmdChoosePathButton.setDisable(true);
		this.ctlDefKeySizeOption.setEditable(this.expertMode);
		initCRTValidities();
		initCRLUpdatePeriods();
		initKeyAlgOptions();
		return this;
	}

	private void initCRTValidities() {
		this.ctlDefCRTValidityInput.setEditable(this.expertMode);

		Days defaultHint = null;

		if (this.storePreferences != null) {
			defaultHint = new Days(this.storePreferences.defaultCRTValidityPeriod.getInt(0));
		}
		resetComboBoxOptions(this.ctlDefCRTValidityInput, CRTValidityPeriod.getDefaultSet(defaultHint),
				(o1, o2) -> o1.days().compareTo(o2.days()));
	}

	private void initCRLUpdatePeriods() {
		this.ctlDefCRLUpdateInput.setEditable(this.expertMode);

		Days defaultHint = null;

		if (this.storePreferences != null) {
			defaultHint = new Days(this.storePreferences.defaultCRLUpdatePeriod.getInt(0));
		}
		resetComboBoxOptions(this.ctlDefCRLUpdateInput, CRLUpdatePeriod.getDefaultSet(defaultHint),
				(o1, o2) -> o1.days().compareTo(o2.days()));
	}

	private void initKeyAlgOptions() {
		String defaultHint = null;

		if (this.storePreferences != null) {
			defaultHint = this.storePreferences.defaultKeyPairAlgorithm.get();
		}
		resetComboBoxOptions(this.ctlDefKeyAlgOption, KeyPairAlgorithm.getDefaultSet(defaultHint, this.expertMode),
				(o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	@Override
	public UserCertStore call(ButtonType param) {
		return this.store;
	}

	private Path validateStoreHomeInput() throws ValidationException {
		String nameInput = InputValidator.notEmpty(Strings.safeTrim(this.ctlNameInput.getText()),
				(a) -> StorePreferencesI18N.formatSTR_MESSAGE_NO_NAME());
		String pathInput = InputValidator.notEmpty(Strings.safeTrim(this.ctlPathInput.getText()),
				(a) -> StorePreferencesI18N.formatSTR_MESSAGE_NO_PATH());
		Path path = PathValidator.isWritableDirectory(pathInput,
				(a) -> StorePreferencesI18N.formatSTR_MESSAGE_INVALID_PATH(pathInput));
		Path storeHome = PathValidator.isPath(path, nameInput,
				(a) -> StorePreferencesI18N.formatSTR_MESSAGE_INVALID_NAME(nameInput));

		InputValidator.isTrue(!Files.exists(storeHome),
				(a) -> StorePreferencesI18N.formatSTR_MESSAGE_STORE_HOME_EXISTS(storeHome));
		return storeHome;
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
