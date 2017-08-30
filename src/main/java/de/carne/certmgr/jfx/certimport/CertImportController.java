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
package de.carne.certmgr.jfx.certimport;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.carne.certmgr.certs.PasswordCallback;
import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.io.CertReaders;
import de.carne.certmgr.certs.net.SSLPeer;
import de.carne.certmgr.certs.security.PlatformKeyStore;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.jfx.password.PasswordDialog;
import de.carne.certmgr.jfx.resources.Images;
import de.carne.certmgr.jfx.util.UserCertStoreTreeTableViewHelper;
import de.carne.check.Nullable;
import de.carne.io.IOHelper;
import de.carne.jfx.application.PlatformHelper;
import de.carne.jfx.scene.control.Alerts;
import de.carne.jfx.stage.StageController;
import de.carne.jfx.util.FileChooserHelper;
import de.carne.jfx.util.validation.ValidationAlerts;
import de.carne.util.DefaultSet;
import de.carne.util.Late;
import de.carne.util.Lazy;
import de.carne.util.Strings;
import de.carne.util.logging.LogLevel;
import de.carne.util.logging.LogMonitor;
import de.carne.util.prefs.PathPreference;
import de.carne.util.validation.InputValidator;
import de.carne.util.validation.PathValidator;
import de.carne.util.validation.ValidationException;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * Certificate import dialog.
 */
public class CertImportController extends StageController {

	private static final Pattern SERVER_INPUT_PATTERN = Pattern.compile("([^:]+)(?::(\\d+))?");

	private final Preferences preferences = Preferences.systemNodeForPackage(CertImportController.class);

	private final PathPreference preferenceInitalDirectory = new PathPreference(this.preferences, "initialDirectory",
			PathPreference.IS_DIRECTORY);

	private Lazy<UserCertStoreTreeTableViewHelper<ImportEntryModel>> lazyImportEntryViewHelper = new Lazy<>(
			() -> new UserCertStoreTreeTableViewHelper<>(this.ctlImportEntryView,
					(e) -> new ImportEntryModel(e, false)));

	private final Late<UserCertStore> importStoreParam = new Late<>();

	@Nullable
	private UserCertStore sourceStore = null;

	@SuppressWarnings("null")
	@FXML
	GridPane ctlControlPane;

	@SuppressWarnings("null")
	@FXML
	VBox ctlProgressOverlay;

	@SuppressWarnings("null")
	@FXML
	RadioButton ctlFileSourceOption;

	@SuppressWarnings("null")
	@FXML
	TextField ctlFileSourceInput;

	@SuppressWarnings("null")
	@FXML
	Button cmdChooseFileSourceButton;

	@SuppressWarnings("null")
	@FXML
	RadioButton ctlDirectorySourceOption;

	@SuppressWarnings("null")
	@FXML
	TextField ctlDirectorySourceInput;

	@SuppressWarnings("null")
	@FXML
	Button cmdChooseDirectorySourceButton;

	@SuppressWarnings("null")
	@FXML
	RadioButton ctlURLSourceOption;

	@SuppressWarnings("null")
	@FXML
	TextField ctlURLSourceInput;

	@SuppressWarnings("null")
	@FXML
	RadioButton ctlServerSourceOption;

	@SuppressWarnings("null")
	@FXML
	TextField ctlServerSourceInput;

	@SuppressWarnings("null")
	@FXML
	ChoiceBox<SSLPeer.Protocol> ctlServerSourceProtocolInput;

	@SuppressWarnings("null")
	@FXML
	RadioButton ctlPlatformSourceOption;

	@SuppressWarnings("null")
	@FXML
	ChoiceBox<PlatformKeyStore> ctlPlatformSourceInput;

	@SuppressWarnings("null")
	@FXML
	RadioButton ctlClipboardSourceOption;

	@SuppressWarnings("null")
	@FXML
	ImageView ctlStatusImage;

	@SuppressWarnings("null")
	@FXML
	Label ctlStatusMessage;

	@SuppressWarnings("null")
	@FXML
	CheckBox ctlSelectAllOption;

	@SuppressWarnings("null")
	@FXML
	TreeTableView<ImportEntryModel> ctlImportEntryView;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<ImportEntryModel, Boolean> ctlImportEntryViewSelected;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<ImportEntryModel, String> ctlImportEntryViewDN;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<ImportEntryModel, Boolean> ctlImportEntryViewCRT;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<ImportEntryModel, Boolean> ctlImportEntryViewKey;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<ImportEntryModel, Boolean> ctlImportEntryViewCSR;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<ImportEntryModel, Boolean> ctlImportEntryViewCRL;

	@SuppressWarnings("unused")
	@FXML
	void onCmdChooseFileSource(ActionEvent evt) {
		FileChooser chooser = new FileChooser();
		List<ExtensionFilter> extensionFilters = new ArrayList<>();

		extensionFilters.add(FileChooserHelper.filterFromString(CertImportI18N.formatSTR_FILTER_ALLFILES()));
		for (CertReader reader : CertReaders.REGISTERED.providers()) {
			extensionFilters.add(new ExtensionFilter(reader.fileType(), reader.fileExtensionPatterns()));
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

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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
		} else if (this.ctlPlatformSourceOption.isSelected()) {
			validateAndReloadPlatformSource();
		} else if (this.ctlClipboardSourceOption.isSelected()) {
			validateAndReloadClipboardSource();
		}
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdSelectAll(ActionEvent evt) {
		boolean select = this.ctlSelectAllOption.isSelected();

		forAllImportEntries(this.ctlImportEntryView.getRoot(), (i) -> {
			ImportEntryModel importEntry = i.getValue();

			if (importEntry != null) {
				importEntry.setSelected(select);
			}
		});
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdImport(ActionEvent evt) {
		try {
			Set<UserCertStoreEntry> importSelection = validateImportSelection();

			getExecutorService().submit(new ImportSelectionTask(importSelection));
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
		}
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdCancel(ActionEvent evt) {
		close(false);
	}

	void onReloadTaskSucceeded(ReloadTask<?> task, UserCertStore store) {
		this.sourceStore = store;
		updateImportEntryView();

		LogMonitor logMonitor = task.logMonitor();

		if (logMonitor.notEmpty()) {
			Alerts.logs(AlertType.WARNING, CertImportI18N.formatSTR_MESSAGE_CREATE_STORE_LOGS(),
					logMonitor.getRecords()).showAndWait();
		}
	}

	void onReloadTaskFailed(Throwable e) {
		Alerts.error(AlertType.ERROR, CertImportI18N.formatSTR_MESSAGE_CREATE_STORE_ERROR(), e).showAndWait();
	}

	@Override
	protected void setupStage(Stage stage) {
		stage.getIcons().addAll(PlatformHelper.stageIcons(Images.IMPORT32, Images.IMPORT16));
		stage.setTitle(CertImportI18N.formatSTR_STAGE_TITLE());
		this.ctlFileSourceInput.disableProperty().bind(Bindings.not(this.ctlFileSourceOption.selectedProperty()));
		this.cmdChooseFileSourceButton.disableProperty()
				.bind(Bindings.not(this.ctlFileSourceOption.selectedProperty()));
		this.ctlDirectorySourceInput.disableProperty()
				.bind(Bindings.not(this.ctlDirectorySourceOption.selectedProperty()));
		this.cmdChooseDirectorySourceButton.disableProperty()
				.bind(Bindings.not(this.ctlDirectorySourceOption.selectedProperty()));
		this.ctlURLSourceInput.disableProperty().bind(Bindings.not(this.ctlURLSourceOption.selectedProperty()));
		this.ctlServerSourceInput.disableProperty().bind(Bindings.not(this.ctlServerSourceOption.selectedProperty()));
		this.ctlServerSourceProtocolInput.disableProperty()
				.bind(Bindings.not(this.ctlServerSourceOption.selectedProperty()));
		this.ctlServerSourceProtocolInput.getItems().addAll(SSLPeer.Protocol.values());
		this.ctlPlatformSourceInput.disableProperty()
				.bind(Bindings.not(this.ctlPlatformSourceOption.selectedProperty()));
		setupPlatformSourceInput();
		this.ctlImportEntryViewSelected
				.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlImportEntryViewSelected));
		this.ctlImportEntryViewSelected.setCellValueFactory(new TreeItemPropertyValueFactory<>("selected"));
		this.ctlImportEntryViewDN.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		this.ctlImportEntryViewCRT.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlImportEntryViewCRT));
		this.ctlImportEntryViewCRT.setCellValueFactory(new TreeItemPropertyValueFactory<>("hasCRT"));
		this.ctlImportEntryViewKey.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlImportEntryViewKey));
		this.ctlImportEntryViewKey.setCellValueFactory(new TreeItemPropertyValueFactory<>("hasKey"));
		this.ctlImportEntryViewCSR.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlImportEntryViewCSR));
		this.ctlImportEntryViewCSR.setCellValueFactory(new TreeItemPropertyValueFactory<>("hasCSR"));
		this.ctlImportEntryViewCRL.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlImportEntryViewCRL));
		this.ctlImportEntryViewCRL.setCellValueFactory(new TreeItemPropertyValueFactory<>("hasCRL"));
		this.ctlImportEntryView.setTreeColumn(this.ctlImportEntryViewDN);
		this.ctlFileSourceOption.setSelected(true);
		this.ctlServerSourceProtocolInput.setValue(SSLPeer.Protocol.SSL);
	}

	private void setupPlatformSourceInput() {
		DefaultSet<PlatformKeyStore> platformSources = PlatformKeyStore.getDefaultSet();

		if (!platformSources.isEmpty()) {
			this.ctlPlatformSourceInput.getItems().addAll(platformSources);
			this.ctlPlatformSourceInput.setValue(platformSources.getDefault());
		} else {
			this.ctlPlatformSourceOption.setDisable(true);
		}
	}

	/**
	 * Initialize the dialog for certificate import.
	 *
	 * @param importStore The {@link UserCertStore} to import into.
	 * @return This controller.
	 */
	public CertImportController init(UserCertStore importStore) {
		this.importStoreParam.init(importStore);
		return this;
	}

	@Override
	protected Preferences getPreferences() {
		return this.preferences;
	}

	@Override
	protected void setBlocked(boolean blocked) {
		this.ctlControlPane.setDisable(blocked);
		this.ctlProgressOverlay.setVisible(blocked);
	}

	private void validateAndReloadFileSource() {
		try {
			Path fileSource = validateFileSourceInput();

			getExecutorService().submit(new ReloadTask<Path>(fileSource) {

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

			getExecutorService().submit(new ReloadTask<Path>(directorySource) {

				@Override
				protected UserCertStore createStore(Path params) throws IOException {
					List<Path> files = IOHelper.collectDirectoryFiles(params);

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

			getExecutorService().submit(new ReloadTask<URL>(urlSource) {

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

			getExecutorService().submit(new ReloadTask<ServerParams>(serverSource) {

				@Override
				protected UserCertStore createStore(ServerParams params) throws IOException {
					return UserCertStore.createFromServer(params.protocol(), params.host(), params.port());
				}

			});
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
		}
	}

	private void validateAndReloadPlatformSource() {
		try {
			PlatformKeyStore platformSource = validatePlatformSourceInput();

			getExecutorService().submit(new ReloadTask<PlatformKeyStore>(platformSource) {

				@Override
				protected UserCertStore createStore(PlatformKeyStore params) throws IOException {
					return UserCertStore.createFromPlatformKeyStore(params,
							PasswordDialog.enterPassword(CertImportController.this));
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

				getExecutorService().submit(new ReloadTask<List<Path>>(filesSource) {

					@Override
					protected UserCertStore createStore(List<Path> params) throws IOException {
						return UserCertStore.createFromFiles(params,
								PasswordDialog.enterPassword(CertImportController.this));
					}

				});
			} else if (clipboard.hasUrl()) {
				URL urlSource = new URL(clipboard.getUrl());

				getExecutorService().submit(new ReloadTask<URL>(urlSource) {

					@Override
					protected UserCertStore createStore(URL params) throws IOException {
						return UserCertStore.createFromURL(params,
								PasswordDialog.enterPassword(CertImportController.this));
					}

				});
			} else if (clipboard.hasString()) {

				String stringSource = clipboard.getString();

				getExecutorService().submit(new ReloadTask<String>(stringSource) {

					@Override
					protected UserCertStore createStore(String params) throws IOException {
						return UserCertStore.createFromData(params, CertImportI18N.formatSTR_TEXT_CLIPBOARD(),
								PasswordDialog.enterPassword(CertImportController.this));
					}

				});
			}
		} catch (

		IOException e) {
			Alerts.error(AlertType.ERROR, CertImportI18N.formatSTR_MESSAGE_CREATE_STORE_ERROR(), e);
		}
	}

	private void updateImportEntryView() {
		this.lazyImportEntryViewHelper.get().update(this.sourceStore);

		UserCertStore checkedSourceStore = this.sourceStore;

		if (checkedSourceStore != null) {
			this.ctlStatusMessage.setText(CertImportI18N.formatSTR_STATUS_NEW_STORE(checkedSourceStore.size()));
			this.ctlStatusImage.setImage(Images.OK16);
		} else {
			this.ctlStatusMessage.setText(CertImportI18N.formatSTR_STATUS_NO_STORE());
			this.ctlStatusImage.setImage(Images.WARNING16);
		}
		this.ctlSelectAllOption.setSelected(false);
	}

	private Path validateFileSourceInput() throws ValidationException {
		String fileSourceInput = InputValidator.notEmpty(Strings.safeSafeTrim(this.ctlFileSourceInput.getText()),
				CertImportI18N::formatSTR_MESSAGE_NO_FILE);

		return PathValidator.isRegularFilePath(fileSourceInput, CertImportI18N::formatSTR_MESSAGE_INVALID_FILE);
	}

	private Path validateDirectorySourceInput() throws ValidationException {
		String directorySourceInput = InputValidator.notEmpty(
				Strings.safeSafeTrim(this.ctlDirectorySourceInput.getText()),
				CertImportI18N::formatSTR_MESSAGE_NO_DIRECTORY);

		return PathValidator.isDirectoryPath(directorySourceInput, CertImportI18N::formatSTR_MESSAGE_INVALID_DIRECTORY);
	}

	private URL validateURLSourceInput() throws ValidationException {
		String urlSourceInput = InputValidator.notEmpty(Strings.safeSafeTrim(this.ctlURLSourceInput.getText()),
				CertImportI18N::formatSTR_MESSAGE_NO_URL);
		URL urlSource;

		try {
			urlSource = new URL(urlSourceInput);
		} catch (MalformedURLException e) {
			throw new ValidationException(CertImportI18N.formatSTR_MESSAGE_INVALID_DIRECTORY(urlSourceInput), e);
		}
		return urlSource;
	}

	private ServerParams validateServerSourceInput() throws ValidationException {
		SSLPeer.Protocol protocol = InputValidator.notNull(this.ctlServerSourceProtocolInput.getValue(),
				CertImportI18N::formatSTR_MESSAGE_NO_SERVERPROTOCOL);
		String serverSourceInput = InputValidator.notEmpty(Strings.safeSafeTrim(this.ctlServerSourceInput.getText()),
				CertImportI18N::formatSTR_MESSAGE_NO_SERVER);
		String[] serverSourceGroups = InputValidator.matches(serverSourceInput, SERVER_INPUT_PATTERN,
				CertImportI18N::formatSTR_MESSAGE_INVALID_SERVER);
		String host = serverSourceGroups[0];
		String portInput = serverSourceGroups[1];
		int port;

		if (portInput != null) {
			try {
				port = Integer.valueOf(portInput).intValue();
			} catch (NumberFormatException e) {
				throw new ValidationException(CertImportI18N.formatSTR_MESSAGE_INVALID_SERVER(serverSourceInput), e);
			}
		} else {
			port = protocol.defaultPort();
		}

		return new ServerParams(protocol, host, port);
	}

	private PlatformKeyStore validatePlatformSourceInput() throws ValidationException {
		return InputValidator.notNull(this.ctlPlatformSourceInput.getValue(),
				CertImportI18N::formatSTR_MESSAGE_NO_PLATFORMKEYSTORE);
	}

	private Set<UserCertStoreEntry> validateImportSelection() throws ValidationException {
		Set<UserCertStoreEntry> importSelection = new HashSet<>();

		forAllImportEntries(this.ctlImportEntryView.getRoot(), (i) -> {
			ImportEntryModel importEntry = i.getValue();

			if (importEntry != null && importEntry.getSelected().booleanValue()) {
				UserCertStoreEntry selectedEntry = importEntry.getEntry();

				if (!selectedEntry.isExternal()) {
					importSelection.add(selectedEntry);
				}
			}
		});
		InputValidator.isTrue(!importSelection.isEmpty(), CertImportI18N::formatSTR_MESSAGE_EMPTY_IMPORT_SELECTION);
		return importSelection;
	}

	private void forAllImportEntries(TreeItem<ImportEntryModel> importItem,
			Consumer<TreeItem<ImportEntryModel>> consumer) {
		consumer.accept(importItem);
		for (TreeItem<ImportEntryModel> importItemChild : importItem.getChildren()) {
			forAllImportEntries(importItemChild, consumer);
		}
	}

	private abstract class ReloadTask<P> extends BackgroundTask<UserCertStore> {

		private final LogMonitor logMonitor = new LogMonitor(LogLevel.LEVEL_WARNING);

		private final P reloadParam;

		ReloadTask(P reloadParam) {
			this.reloadParam = reloadParam;
		}

		public LogMonitor logMonitor() {
			return this.logMonitor;
		}

		@Override
		protected UserCertStore call() throws Exception {
			UserCertStore store;

			try (LogMonitor.Session session = this.logMonitor.start()
					.includePackage(UserCertStore.class.getPackage())) {
				store = createStore(this.reloadParam);
			}
			return store;
		}

		protected abstract UserCertStore createStore(P param) throws IOException;

		@Override
		protected void succeeded() {
			super.succeeded();
			onReloadTaskSucceeded(this, getValue());
			this.logMonitor.close();
		}

		@Override
		protected void failed() {
			super.failed();
			onReloadTaskFailed(getException());
			this.logMonitor.close();
		}

	}

	void importSelection(Set<UserCertStoreEntry> importSelection) throws IOException {
		PasswordCallback newPassword = PasswordDialog.enterNewPassword(this);

		UserCertStore importStore = this.importStoreParam.get();

		for (UserCertStoreEntry importEntry : importSelection) {
			importStore.importEntry(importEntry, newPassword, CertImportI18N.formatSTR_TEXT_ALIASHINT());
		}
	}

	private class ImportSelectionTask extends BackgroundTask<Void> {

		private final Set<UserCertStoreEntry> importSelection;

		ImportSelectionTask(Set<UserCertStoreEntry> importSelection) {
			this.importSelection = importSelection;
		}

		@Override
		@Nullable
		protected Void call() throws Exception {
			importSelection(this.importSelection);
			return null;
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			close(true);
		}

		@Override
		protected void failed() {
			super.failed();
			Alerts.unexpected(getException()).showAndWait();
		}

	}

}
