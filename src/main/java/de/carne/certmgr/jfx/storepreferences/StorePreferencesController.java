/*
 * Copyright (c) 2015-2019 Holger de Carne and contributors, All Rights Reserved.
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
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStorePreferences;
import de.carne.certmgr.certs.security.CRLUpdatePeriod;
import de.carne.certmgr.certs.security.CRTValidityPeriod;
import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.jfx.util.converter.CRLUpdatePeriodStringConverter;
import de.carne.certmgr.jfx.util.converter.CRTValidityPeriodStringConverter;
import de.carne.certmgr.util.Days;
import de.carne.jfx.scene.control.Alerts;
import de.carne.jfx.scene.control.Controls;
import de.carne.jfx.scene.control.DialogController;
import de.carne.jfx.util.validation.InputValidator;
import de.carne.jfx.util.validation.PathValidator;
import de.carne.jfx.util.validation.ValidationAlerts;
import de.carne.jfx.util.validation.ValidationException;
import de.carne.util.Late;
import de.carne.util.Strings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

/**
 * Store options dialog.
 */
public class StorePreferencesController extends DialogController<UserCertStore>
		implements Callback<ButtonType, UserCertStore> {

	private final Late<UserCertStore> storeParam = new Late<>();

	private final Late<UserCertStorePreferences> storePreferencesParam = new Late<>();

	private boolean expertModeParam = false;

	@SuppressWarnings("null")
	@FXML
	TextField ctlNameInput;

	@SuppressWarnings("null")
	@FXML
	TextField ctlPathInput;

	@SuppressWarnings("null")
	@FXML
	Button cmdChoosePathButton;

	@SuppressWarnings("null")
	@FXML
	ComboBox<CRTValidityPeriod> ctlDefCRTValidityInput;

	@SuppressWarnings("null")
	@FXML
	ComboBox<CRLUpdatePeriod> ctlDefCRLUpdateInput;

	@SuppressWarnings("null")
	@FXML
	ComboBox<KeyPairAlgorithm> ctlDefKeyAlgOption;

	@SuppressWarnings("null")
	@FXML
	ComboBox<Integer> ctlDefKeySizeOption;

	@SuppressWarnings("null")
	@FXML
	ComboBox<SignatureAlgorithm> ctlDefSigAlgOption;

	@SuppressWarnings("unused")
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

		if (this.storePreferencesParam.getOptional().isPresent()) {
			UserCertStorePreferences storePreferences = this.storePreferencesParam.get();

			if (keyAlg.algorithm().equals(storePreferences.defaultKeyPairAlgorithm.get())) {
				keySizeDefaultHint = storePreferences.defaultKeySize.get();
				sigAlgDefaultHint = storePreferences.defaultSignatureAlgorithm.get();
			}
		}
		Controls.resetComboBoxOptions(this.ctlDefKeySizeOption, keyAlg.getStandardKeySizes(keySizeDefaultHint),
				(o1, o2) -> o1.compareTo(o2));
		Controls.resetComboBoxOptions(this.ctlDefSigAlgOption,
				SignatureAlgorithm.getDefaultSet(keyAlg.algorithm(), sigAlgDefaultHint, this.expertModeParam),
				(o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	private void onApply(ActionEvent evt) {
		if (!this.storeParam.getOptional().isPresent()) {
			try {
				Path storeHome = validateStoreHomeInput();

				this.storeParam.set(UserCertStore.createStore(storeHome));
				this.storePreferencesParam.set(Objects.requireNonNull(this.storeParam.get().storePreferences()));
			} catch (ValidationException e) {
				ValidationAlerts.error(e).showAndWait();
				evt.consume();
			} catch (Exception e) {
				Alerts.unexpected(e).showAndWait();
				evt.consume();
			}
		}
		if (this.storePreferencesParam.getOptional().isPresent()) {
			try {
				UserCertStorePreferences storePreferences = this.storePreferencesParam.get();

				storePreferences.defaultCRTValidityPeriod.put(validateDefCRTValidityInput().days().count());
				storePreferences.defaultCRLUpdatePeriod.put(validateDefCRLUpdateInput().days().count());
				storePreferences.defaultKeyPairAlgorithm.put(validateDefKeyAlgInput().algorithm());
				storePreferences.defaultKeySize.put(validateDefKeySizeInput());
				storePreferences.defaultSignatureAlgorithm.put(validateDefSigAlgInput().algorithm());
				storePreferences.sync();
			} catch (ValidationException e) {
				ValidationAlerts.error(e).showAndWait();
				evt.consume();
			} catch (Exception e) {
				Alerts.unexpected(e).showAndWait();
				evt.consume();
			}
		}
	}

	@Override
	protected void setupDialog(Dialog<UserCertStore> dialog) {
		dialog.setTitle(StorePreferencesI18N.formatSTR_STAGE_TITLE());
		this.ctlDefKeyAlgOption.valueProperty().addListener((p, o, n) -> onDefKeyAlgChanged(n));
		this.ctlDefKeySizeOption.setConverter(new IntegerStringConverter());
		addButtonEventFilter(ButtonType.APPLY, this::onApply);
	}

	/**
	 * Initialize dialog for creating a new store.
	 *
	 * @param expertMode Whether to run in expert mode ({@code true}) or not ({@code false}).
	 * @return This controller.
	 */
	public StorePreferencesController init(boolean expertMode) {
		this.expertModeParam = expertMode;
		initExpertMode();
		initDefCRTValidityPeriods();
		initDefCRLUpdatePeriods();
		initDefKeyAlgOptions();
		((Button) lookupButton(ButtonType.APPLY)).setText(StorePreferencesI18N.formatSTR_TEXT_CREATE());
		return this;
	}

	/**
	 * Initialize dialog for editing an existing store's preferences.
	 *
	 * @param store The store to edit the preferences for.
	 * @param expertMode Whether to run in expert mode ({@code true}) or not ({@code false}).
	 * @return This controller.
	 */
	public StorePreferencesController init(UserCertStore store, boolean expertMode) {
		this.storeParam.set(store);
		this.storePreferencesParam.set(Objects.requireNonNull(store.storePreferences()));
		this.expertModeParam = expertMode;

		Path storeHome = Objects.requireNonNull(store.storeHome());

		this.ctlNameInput.setText(storeHome.getFileName().toString());
		this.ctlNameInput.setDisable(true);
		this.ctlPathInput.setText(storeHome.getParent().toString());
		this.ctlPathInput.setDisable(true);
		this.cmdChoosePathButton.setDisable(true);
		initExpertMode();
		initDefCRTValidityPeriods();
		initDefCRLUpdatePeriods();
		initDefKeyAlgOptions();
		return this;
	}

	private void initExpertMode() {
		this.ctlDefCRTValidityInput.setEditable(this.expertModeParam);

		CRTValidityPeriodStringConverter defCRTValidityConverter = new CRTValidityPeriodStringConverter();

		defCRTValidityConverter.attach(this.ctlDefCRTValidityInput);
		this.ctlDefCRLUpdateInput.setEditable(this.expertModeParam);

		CRLUpdatePeriodStringConverter defCRLUpdateConverter = new CRLUpdatePeriodStringConverter();

		defCRLUpdateConverter.attach(this.ctlDefCRLUpdateInput);
		this.ctlDefKeySizeOption.setEditable(this.expertModeParam);
	}

	private void initDefCRTValidityPeriods() {
		Days defaultHint = null;

		if (this.storePreferencesParam.getOptional().isPresent()) {
			defaultHint = new Days(this.storePreferencesParam.get().defaultCRTValidityPeriod.getInt(0));
		}
		Controls.resetComboBoxOptions(this.ctlDefCRTValidityInput, CRTValidityPeriod.getDefaultSet(defaultHint),
				(o1, o2) -> o1.days().compareTo(o2.days()));
	}

	private void initDefCRLUpdatePeriods() {
		Days defaultHint = null;

		if (this.storePreferencesParam.getOptional().isPresent()) {
			defaultHint = new Days(this.storePreferencesParam.get().defaultCRLUpdatePeriod.getInt(0));
		}
		Controls.resetComboBoxOptions(this.ctlDefCRLUpdateInput, CRLUpdatePeriod.getDefaultSet(defaultHint),
				(o1, o2) -> o1.days().compareTo(o2.days()));
	}

	private void initDefKeyAlgOptions() {
		String defaultHint = null;

		if (this.storePreferencesParam.getOptional().isPresent()) {
			defaultHint = this.storePreferencesParam.get().defaultKeyPairAlgorithm.get();
		}
		Controls.resetComboBoxOptions(this.ctlDefKeyAlgOption,
				KeyPairAlgorithm.getDefaultSet(defaultHint, this.expertModeParam),
				(o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	@Override
	@Nullable
	public UserCertStore call(@Nullable ButtonType param) {
		return this.storeParam.getOptional().orElse(null);
	}

	private Path validateStoreHomeInput() throws ValidationException {
		String nameInput = InputValidator.notEmpty(Strings.safeTrim(this.ctlNameInput.getText()),
				StorePreferencesI18N::formatSTR_MESSAGE_NO_NAME);
		String pathInput = InputValidator.notEmpty(Strings.safeTrim(this.ctlPathInput.getText()),
				StorePreferencesI18N::formatSTR_MESSAGE_NO_PATH);
		Path path = PathValidator.isDirectoryPath(pathInput, StorePreferencesI18N::formatSTR_MESSAGE_INVALID_PATH);
		Path storeHome = PathValidator.isPath(path, nameInput, StorePreferencesI18N::formatSTR_MESSAGE_INVALID_NAME);

		InputValidator.isTrue(!Files.exists(storeHome),
				(a) -> StorePreferencesI18N.formatSTR_MESSAGE_STORE_HOME_EXISTS(storeHome));
		return storeHome;
	}

	private CRTValidityPeriod validateDefCRTValidityInput() throws ValidationException {
		return InputValidator.notNull(this.ctlDefCRTValidityInput.getValue(),
				StorePreferencesI18N::formatSTR_MESSAGE_NO_DEFCRTVALIDITY);
	}

	private CRLUpdatePeriod validateDefCRLUpdateInput() throws ValidationException {
		return InputValidator.notNull(this.ctlDefCRLUpdateInput.getValue(),
				StorePreferencesI18N::formatSTR_MESSAGE_NO_DEFCRLUPDATE);
	}

	private KeyPairAlgorithm validateDefKeyAlgInput() throws ValidationException {
		return InputValidator.notNull(this.ctlDefKeyAlgOption.getValue(),
				StorePreferencesI18N::formatSTR_MESSAGE_NO_DEFKEYALG);
	}

	private Integer validateDefKeySizeInput() throws ValidationException {
		return InputValidator.notNull(this.ctlDefKeySizeOption.getValue(),
				StorePreferencesI18N::formatSTR_MESSAGE_NO_DEFKEYSIZE);
	}

	private SignatureAlgorithm validateDefSigAlgInput() throws ValidationException {
		return InputValidator.notNull(this.ctlDefSigAlgOption.getValue(),
				StorePreferencesI18N::formatSTR_MESSAGE_NO_DEFSIGALG);
	}

}
