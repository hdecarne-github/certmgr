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
			validateAndReloadFileSource();
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
			validateAndReloadDirectorySource();
		}
	}

	@FXML
	void onCmdReload(ActionEvent evt) {
		if (this.ctlFileSourceOption.isSelected()) {
			validateAndReloadFileSource();
		} else if (this.ctlDirectorySourceOption.isSelected()) {
			validateAndReloadDirectorySource();
		} else if (this.ctlURLSourceOption.isSelected()) {
			validateAndReloadURLSource();
		} else if (this.ctlServerSourceOption.isSelected()) {
			validateAndReloadServerSource();
		} else if (this.ctlClipboardSourceOption.isSelected()) {
			validateAndReloadClipboardSource();
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

	void onReloadTaskSucceeded(UserCertStore store) {
		this.sourceStore = store;
		updateSourceEntryInput();
	}

	void onReloadTaskFailed(Throwable e) {
		Alerts.error(CertImportI18N.formatSTR_MESSAGE_CREATE_STORE_ERROR(), e);
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

	private void validateAndReloadFileSource() {
		try {
			Path fileSource = validateFileSourceInput();

			getExecutorService().submit(new CreateStoreTask<Path>(fileSource) {

				@Override
				protected UserCertStore createStore(Path params) throws IOException {
					return UserCertStore.createFromFile(params,
							PasswordDialog.enterPassword(CertImportController.this));
				}

			});
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
		}
	}

	private void validateAndReloadDirectorySource() {
		try {
			Path directorySource = validateDirectorySourceInput();

			getExecutorService().submit(new CreateStoreTask<Path>(directorySource) {

				@Override
				protected UserCertStore createStore(Path params) throws IOException {
					List<Path> files = Files.walk(params).filter((p) -> Files.isRegularFile(p))
							.collect(Collectors.toList());

					return UserCertStore.createFromFiles(files,
							PasswordDialog.enterPassword(CertImportController.this));
				}

			});
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
		}
	}

	private void validateAndReloadURLSource() {
		try {
			URL urlSource = validateURLSourceInput();

			getExecutorService().submit(new CreateStoreTask<URL>(urlSource) {

				@Override
				protected UserCertStore createStore(URL params) throws IOException {
					return UserCertStore.createFromURL(params, PasswordDialog.enterPassword(CertImportController.this));
				}

			});
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
		}
	}

	private void validateAndReloadServerSource() {
		try {
			ServerParams serverSource = validateServerSourceInput();

			getExecutorService().submit(new CreateStoreTask<ServerParams>(serverSource) {

				@Override
				protected UserCertStore createStore(ServerParams params) throws IOException {
					return UserCertStore.createFromServer(params.host(), params.port());
				}

			});
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
		}
	}

	private void validateAndReloadClipboardSource() {
		try {
			Clipboard clipboard = Clipboard.getSystemClipboard();

			if (clipboard.hasFiles()) {
				List<Path> filesSource = clipboard.getFiles().stream().map((f) -> f.toPath())
						.collect(Collectors.toList());

				getExecutorService().submit(new CreateStoreTask<List<Path>>(filesSource) {

					@Override
					protected UserCertStore createStore(List<Path> params) throws IOException {
						return UserCertStore.createFromFiles(params,
								PasswordDialog.enterPassword(CertImportController.this));
					}

				});
			} else if (clipboard.hasUrl()) {
				URL urlSource = new URL(clipboard.getUrl());

				getExecutorService().submit(new CreateStoreTask<URL>(urlSource) {

					@Override
					protected UserCertStore createStore(URL params) throws IOException {
						return UserCertStore.createFromURL(params,
								PasswordDialog.enterPassword(CertImportController.this));
					}

				});
			} else if (clipboard.hasString()) {
				String stringSource = clipboard.getString();

				getExecutorService().submit(new CreateStoreTask<String>(stringSource) {

					@Override
					protected UserCertStore createStore(String params) throws IOException {
						return UserCertStore.createFromData(params, CertImportI18N.formatSTR_TEXT_CLIPBOARD(),
								PasswordDialog.enterPassword(CertImportController.this));
					}

				});
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

	private ServerParams validateServerSourceInput() throws ValidationException {
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
		return new ServerParams(host, port);
	}

	private abstract class CreateStoreTask<P> extends BackgroundTask<UserCertStore> {

		private final P createParams;

		CreateStoreTask(P createParams) {
			this.createParams = createParams;
		}

		@Override
		protected UserCertStore call() throws Exception {
			return createStore(this.createParams);
		}

		protected abstract UserCertStore createStore(P param) throws IOException;

		@Override
		protected void succeeded() {
			onReloadTaskSucceeded(getValue());
			super.succeeded();
		}

		@Override
		protected void failed() {
			onReloadTaskFailed(getException());
			super.failed();
		}

	}

}
