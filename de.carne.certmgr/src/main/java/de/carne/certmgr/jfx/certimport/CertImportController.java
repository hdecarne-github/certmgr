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
package de.carne.certmgr.jfx.certimport;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.io.CertReaders;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.jfx.UserCertStoreTreeTableViewHelper;
import de.carne.certmgr.jfx.password.PasswordDialog;
import de.carne.certmgr.jfx.resources.Images;
import de.carne.certmgr.jfx.store.StoreController;
import de.carne.jfx.application.PlatformHelper;
import de.carne.jfx.scene.control.Alerts;
import de.carne.jfx.stage.StageController;
import de.carne.jfx.util.FileChooserHelper;
import de.carne.jfx.util.validation.ValidationAlerts;
import de.carne.util.Strings;
import de.carne.util.prefs.DirectoryPreference;
import de.carne.util.validation.InputValidator;
import de.carne.util.validation.PathValidator;
import de.carne.util.validation.ValidationException;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Certificate import dialog.
 */
public class CertImportController extends StageController {

	private static final Pattern SERVER_INPUT_PATTERN = Pattern.compile("(.+):(\\d+)");

	private final Preferences preferences = Preferences.systemNodeForPackage(StoreController.class);

	private final DirectoryPreference preferenceInitalDirectory = new DirectoryPreference(this.preferences,
			"initialDirectory", true);

	private UserCertStoreTreeTableViewHelper<ImportUserCertStoreEntryModel> sourceEntryInputHelper = null;

	private UserCertStore sourceStore = null;

	@FXML
	RadioButton ctlFileSourceOption;

	@FXML
	TextField ctlFileSourceInput;

	@FXML
	Button ctlChooseFileSourceButton;

	@FXML
	RadioButton ctlDirectorySourceOption;

	@FXML
	TextField ctlDirectorySourceInput;

	@FXML
	Button ctlChooseDirectorySourceButton;

	@FXML
	RadioButton ctlURLSourceOption;

	@FXML
	TextField ctlURLSourceInput;

	@FXML
	RadioButton ctlServerSourceOption;

	@FXML
	TextField ctlServerSourceInput;

	@FXML
	RadioButton ctlClipboardSourceOption;

	@FXML
	ImageView ctlStatusImage;

	@FXML
	Label ctlStatusMessage;

	@FXML
	TreeTableView<ImportUserCertStoreEntryModel> ctlSourceEntryInput;

	@FXML
	TreeTableColumn<ImportUserCertStoreEntryModel, Boolean> ctlSourceEntryInputSelected;

	@FXML
	TreeTableColumn<ImportUserCertStoreEntryModel, String> ctlSourceEntryInputDN;

	@FXML
	TreeTableColumn<ImportUserCertStoreEntryModel, Boolean> ctlSourceEntryInputCRT;

	@FXML
	TreeTableColumn<ImportUserCertStoreEntryModel, Boolean> ctlSourceEntryInputKey;

	@FXML
	TreeTableColumn<ImportUserCertStoreEntryModel, Boolean> ctlSourceEntryInputCSR;

	@FXML
	TreeTableColumn<ImportUserCertStoreEntryModel, Boolean> ctlSourceEntryInputCRL;

	@FXML
	void onCmdChooseFileSource(ActionEvent evt) {
		FileChooser chooser = new FileChooser();
		List<ExtensionFilter> extensionFilters = new ArrayList<>();

		extensionFilters.add(FileChooserHelper.filterFromString(CertImportI18N.formatSTR_FILTER_ALLFILES()));
		for (CertReader reader : CertReaders.REGISTERED.providers()) {
			extensionFilters.add(new ExtensionFilter(reader.fileType(), reader.fileExtensions()));
		}

		chooser.getExtensionFilters().addAll(extensionFilters);
		chooser.setSelectedExtensionFilter(extensionFilters.get(0));
		chooser.setInitialDirectory(this.preferenceInitalDirectory.getValueAsFile());

		File fileSource = chooser.showOpenDialog(getUI());

		if (fileSource != null) {
			this.ctlFileSourceInput.setText(fileSource.getAbsolutePath());
			this.preferenceInitalDirectory.putValueFromFile(fileSource.getParentFile());
			syncPreferences();
			onCmdFileReload();
		}
	}

	@FXML
	void onCmdChooseDirectorySource(ActionEvent evt) {
		DirectoryChooser chooser = new DirectoryChooser();

		chooser.setInitialDirectory(this.preferenceInitalDirectory.getValueAsFile());

		File directorySource = chooser.showDialog(getUI());

		if (directorySource != null) {
			this.ctlDirectorySourceInput.setText(directorySource.getAbsolutePath());
			this.preferenceInitalDirectory.putValueFromFile(directorySource);
			syncPreferences();
			onCmdDirectoryReload();
		}
	}

	@FXML
	void onCmdReload(ActionEvent evt) {
		if (this.ctlFileSourceOption.isSelected()) {
			onCmdFileReload();
		} else if (this.ctlDirectorySourceOption.isSelected()) {
			onCmdDirectoryReload();
		} else if (this.ctlURLSourceOption.isSelected()) {
			onCmdURLReload();
		} else if (this.ctlServerSourceOption.isSelected()) {
			onCmdServerReload();
		} else if (this.ctlClipboardSourceOption.isSelected()) {
			onCmdClipboardReload();
		}
	}

	@FXML
	void onCmdImport(ActionEvent evt) {
		close(true);
	}

	@FXML
	void onCmdCancel(ActionEvent evt) {
		close(false);
	}

	@Override
	protected void setupStage(Stage stage) {
		stage.getIcons().addAll(PlatformHelper.stageIcons(Images.IMPORT32, Images.IMPORT16));
		stage.setTitle(CertImportI18N.formatSTR_STAGE_TITLE());
		this.ctlFileSourceInput.disableProperty().bind(Bindings.not(this.ctlFileSourceOption.selectedProperty()));
		this.ctlChooseFileSourceButton.disableProperty()
				.bind(Bindings.not(this.ctlFileSourceOption.selectedProperty()));
		this.ctlDirectorySourceInput.disableProperty()
				.bind(Bindings.not(this.ctlDirectorySourceOption.selectedProperty()));
		this.ctlChooseDirectorySourceButton.disableProperty()
				.bind(Bindings.not(this.ctlDirectorySourceOption.selectedProperty()));
		this.ctlURLSourceInput.disableProperty().bind(Bindings.not(this.ctlURLSourceOption.selectedProperty()));
		this.ctlServerSourceInput.disableProperty().bind(Bindings.not(this.ctlServerSourceOption.selectedProperty()));
		this.ctlSourceEntryInputSelected
				.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlSourceEntryInputSelected));
		this.ctlSourceEntryInputSelected.setCellValueFactory(new TreeItemPropertyValueFactory<>("selected"));
		this.ctlSourceEntryInputDN.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		this.ctlSourceEntryInputCRT
				.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlSourceEntryInputCRT));
		this.ctlSourceEntryInputCRT.setCellValueFactory(new TreeItemPropertyValueFactory<>("hasCRT"));
		this.ctlSourceEntryInputKey
				.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlSourceEntryInputKey));
		this.ctlSourceEntryInputKey.setCellValueFactory(new TreeItemPropertyValueFactory<>("hasKey"));
		this.ctlSourceEntryInputCSR
				.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlSourceEntryInputCSR));
		this.ctlSourceEntryInputCSR.setCellValueFactory(new TreeItemPropertyValueFactory<>("hasCSR"));
		this.ctlSourceEntryInputCRL
				.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlSourceEntryInputCRL));
		this.ctlSourceEntryInputCRL.setCellValueFactory(new TreeItemPropertyValueFactory<>("hasCRL"));
		this.ctlSourceEntryInput.setTreeColumn(this.ctlSourceEntryInputDN);
		this.ctlFileSourceOption.setSelected(true);
	}

	@Override
	protected Preferences getPreferences() {
		return this.preferences;
	}

	private void onCmdFileReload() {
		try {
			Path fileSource = validateFileSourceInput();
			UserCertStore store = UserCertStore.createFromFile(fileSource, PasswordDialog.enterPassword(this));

			this.sourceStore = store;
			updateSourceEntryInput();
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
		} catch (IOException e) {
			Alerts.error(CertImportI18N.formatSTR_MESSAGE_CREATE_STORE_ERROR(), e);
		}
	}

	private void onCmdDirectoryReload() {
		try {
			Path directorySource = validateDirectorySourceInput();
			List<Path> files = Files.walk(directorySource).filter((p) -> Files.isRegularFile(p))
					.collect(Collectors.toList());
			UserCertStore store = UserCertStore.createFromFiles(files, PasswordDialog.enterPassword(this));

			this.sourceStore = store;
			updateSourceEntryInput();
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
		} catch (IOException e) {
			Alerts.error(CertImportI18N.formatSTR_MESSAGE_CREATE_STORE_ERROR(), e);
		}
	}

	private void onCmdURLReload() {
		try {
			URL urlSource = validateURLSourceInput();
			UserCertStore store = UserCertStore.createFromURL(urlSource, PasswordDialog.enterPassword(this));

			this.sourceStore = store;
			updateSourceEntryInput();
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
		} catch (IOException e) {
			Alerts.error(CertImportI18N.formatSTR_MESSAGE_CREATE_STORE_ERROR(), e);
		}
	}

	private void onCmdServerReload() {
		try {
			Pair<String, Integer> serverSource = validateServerSourceInput();
			UserCertStore store = UserCertStore.createFromServer(serverSource.getKey(), serverSource.getValue());

			this.sourceStore = store;
			updateSourceEntryInput();
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
		} catch (IOException e) {
			Alerts.error(CertImportI18N.formatSTR_MESSAGE_CREATE_STORE_ERROR(), e);
		}
	}

	private void onCmdClipboardReload() {
		try {
			Clipboard clipboard = Clipboard.getSystemClipboard();

			if (clipboard.hasFiles()) {
				List<Path> filesSource = clipboard.getFiles().stream().map((f) -> f.toPath())
						.collect(Collectors.toList());
				UserCertStore store = UserCertStore.createFromFiles(filesSource, PasswordDialog.enterPassword(this));

				this.sourceStore = store;
				updateSourceEntryInput();
			} else if (clipboard.hasUrl()) {
				URL urlSource = new URL(clipboard.getUrl());
				UserCertStore store = UserCertStore.createFromURL(urlSource, PasswordDialog.enterPassword(this));

				this.sourceStore = store;
				updateSourceEntryInput();
			} else if (clipboard.hasString()) {
				String stringSource = clipboard.getString();
				UserCertStore store = UserCertStore.createFromData(stringSource,
						CertImportI18N.formatSTR_TEXT_CLIPBOARD(), PasswordDialog.enterPassword(this));

				this.sourceStore = store;
				updateSourceEntryInput();
			}
		} catch (IOException e) {
			Alerts.error(CertImportI18N.formatSTR_MESSAGE_CREATE_STORE_ERROR(), e);
		}
	}

	private void updateSourceEntryInput() {
		if (this.sourceEntryInputHelper == null) {
			this.sourceEntryInputHelper = new UserCertStoreTreeTableViewHelper<>(this.ctlSourceEntryInput,
					(e) -> new ImportUserCertStoreEntryModel(e, false));
		}
		this.sourceEntryInputHelper.update(this.sourceStore);
		if (this.sourceStore != null) {
			this.ctlStatusImage.setImage(Images.OK16);
			this.ctlStatusMessage.setText(CertImportI18N.formatSTR_STATUS_NEW_STORE(this.sourceStore.size()));
		} else {
			this.ctlStatusImage.setImage(Images.WARNING16);
			this.ctlStatusMessage.setText(CertImportI18N.formatSTR_STATUS_NO_STORE());
		}
	}

	private Path validateFileSourceInput() throws ValidationException {
		String fileSourceInput = InputValidator.notEmpty(Strings.safeTrim(this.ctlFileSourceInput.getText()),
				(a) -> CertImportI18N.formatSTR_MESSAGE_NO_FILE(a));

		return PathValidator.isReadableFile(fileSourceInput, (a) -> CertImportI18N.formatSTR_MESSAGE_INVALID_FILE(a));
	}

	private Path validateDirectorySourceInput() throws ValidationException {
		String directorySourceInput = InputValidator.notEmpty(Strings.safeTrim(this.ctlDirectorySourceInput.getText()),
				(a) -> CertImportI18N.formatSTR_MESSAGE_NO_DIRECTORY(a));

		return PathValidator.isReadableDirectory(directorySourceInput,
				(a) -> CertImportI18N.formatSTR_MESSAGE_INVALID_DIRECTORY(a));
	}

	private URL validateURLSourceInput() throws ValidationException {
		String urlSourceInput = InputValidator.notEmpty(Strings.safeTrim(this.ctlURLSourceInput.getText()),
				(a) -> CertImportI18N.formatSTR_MESSAGE_NO_URL(a));
		URL urlSource;

		try {
			urlSource = new URL(urlSourceInput);
		} catch (MalformedURLException e) {
			throw new ValidationException(CertImportI18N.formatSTR_MESSAGE_INVALID_DIRECTORY(urlSourceInput), e);
		}
		return urlSource;
	}

	private Pair<String, Integer> validateServerSourceInput() throws ValidationException {
		String serverSourceInput = InputValidator.notEmpty(Strings.safeTrim(this.ctlServerSourceInput.getText()),
				(a) -> CertImportI18N.formatSTR_MESSAGE_NO_FILE(a));
		String[] serverSourceGroups = InputValidator.matches(serverSourceInput, SERVER_INPUT_PATTERN,
				(a) -> CertImportI18N.formatSTR_MESSAGE_INVALID_SERVER(a));
		String host = serverSourceGroups[0];
		int port;

		try {
			port = Integer.valueOf(serverSourceGroups[1]);
		} catch (NumberFormatException e) {
			throw new ValidationException(CertImportI18N.formatSTR_MESSAGE_INVALID_SERVER(serverSourceInput), e);
		}
		return new Pair<>(host, port);
	}

}
