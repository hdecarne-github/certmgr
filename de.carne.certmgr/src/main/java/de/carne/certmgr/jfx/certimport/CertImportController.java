/*
 * Copyright (c) 2014-2016 Holger de Carne and contributors, All Rights Reserved.
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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import de.carne.certmgr.jfx.CertFileFormats;
import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.jfx.InputValidator;
import de.carne.certmgr.jfx.InvalidInputException;
import de.carne.certmgr.jfx.help.Help;
import de.carne.certmgr.jfx.help.HelpController;
import de.carne.certmgr.jfx.passwordprompt.PasswordPromptCallback;
import de.carne.certmgr.store.CertEntry;
import de.carne.certmgr.store.CertStore;
import de.carne.certmgr.store.CertStoreEntry;
import de.carne.certmgr.store.ImportSource;
import de.carne.certmgr.store.ImportStore;
import de.carne.certmgr.store.PasswordCallback;
import de.carne.jfx.StageController;
import de.carne.jfx.messagebox.MessageBoxStyle;
import de.carne.util.Strings;
import de.carne.util.logging.Log;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Dialog controller for importing certificate data.
 */
public class CertImportController extends StageController {

	private static final Log LOG = new Log(CertImportController.class);

	private static final Preferences PREFERENCES = Preferences.systemNodeForPackage(CertImportController.class);

	private static final String PREF_INITIAL_DIRECTORY = "initialDirectory";

	/**
	 * Controller's callback interface.
	 */
	public interface Result {

		/**
		 * Called when the user has performed an import.
		 *
		 * @param importEntriesParam The imported certificate store entries.
		 */
		public void onEntryImport(Collection<CertStoreEntry> importEntriesParam);

	}

	private Result result = null;
	private CertStore store = null;

	@FXML
	RadioButton ctlFileSourceOption;

	@FXML
	TextField ctlFileSourceInput;

	@FXML
	Button ctlChooseFileSourceButton;

	@FXML
	RadioButton ctlFolderSourceOption;

	@FXML
	TextField ctlFolderSourceInput;

	@FXML
	Button ctlChooseFolderSourceButton;

	@FXML
	RadioButton ctlURLSourceOption;

	@FXML
	TextField ctlURLSourceInput;

	@FXML
	RadioButton ctlClipboardSourceOption;

	@FXML
	Pane ctlProgressGroup;

	@FXML
	ImageView ctlImportStatusIcon;

	@FXML
	Label ctlImportStatusMessage;

	@FXML
	TreeTableView<ImportEntryModel> ctlImportSelection;

	@FXML
	TreeTableColumn<ImportEntryModel, Boolean> ctlImportSelectionSelected;

	@FXML
	TreeTableColumn<ImportEntryModel, String> ctlImportSelectionName;

	@FXML
	CheckBox ctlImportSelectionSelectedAll;

	@FXML
	TreeTableColumn<ImportEntryModel, Boolean> ctlImportSelectionHasKey;

	@FXML
	TreeTableColumn<ImportEntryModel, Boolean> ctlImportSelectionHasCRT;

	@FXML
	TreeTableColumn<ImportEntryModel, Boolean> ctlImportSelectionHasCSR;

	@FXML
	TreeTableColumn<ImportEntryModel, Boolean> ctlImportSelectionHasCRL;

	@FXML
	CheckBox ctlImportOverwriteOption;

	@FXML
	void onChooseFileSource(ActionEvent evt) {
		FileChooser fileSourceChooser = new FileChooser();

		fileSourceChooser.getExtensionFilters().addAll(CertFileFormats.getFileChooserFilters());

		String fileSourceInput = Strings.safeTrim(this.ctlFileSourceInput.getText());
		File fileSourceFile;

		if (Strings.notEmpty(fileSourceInput)) {
			fileSourceFile = new File(fileSourceInput);
			if (fileSourceFile.isFile()) {
				fileSourceChooser.setInitialDirectory(fileSourceFile.getParentFile());
				fileSourceChooser.setInitialFileName(fileSourceFile.getPath());
			}
		}
		if (fileSourceChooser.getInitialDirectory() == null) {
			File initalDirectoryPreference = getInitialDirectoryPreference();

			if (initalDirectoryPreference != null) {
				fileSourceChooser.setInitialDirectory(initalDirectoryPreference);
			}
		}
		fileSourceFile = fileSourceChooser.showOpenDialog(getStage());
		if (fileSourceFile != null) {
			this.ctlFileSourceInput.setText(fileSourceFile.getPath());
			recordInitialDirectoryPreference(fileSourceFile.getParentFile());
			onReloadSource(false);
		}
	}

	@FXML
	void onChooseFolderSource(ActionEvent evt) {
		DirectoryChooser folderSourceChooser = new DirectoryChooser();
		String folderSourceInput = Strings.safeTrim(this.ctlFolderSourceInput.getText());
		File folderSourceFile;

		if (Strings.notEmpty(folderSourceInput)) {
			folderSourceFile = new File(folderSourceInput);
			if (folderSourceFile.isDirectory()) {
				folderSourceChooser.setInitialDirectory(folderSourceFile);
			}
		}
		if (folderSourceChooser.getInitialDirectory() == null) {
			File initalDirectoryPreference = getInitialDirectoryPreference();

			if (initalDirectoryPreference != null) {
				folderSourceChooser.setInitialDirectory(initalDirectoryPreference);
			}
		}
		folderSourceFile = folderSourceChooser.showDialog(getStage());
		if (folderSourceFile != null) {
			this.ctlFolderSourceInput.setText(folderSourceFile.getPath());
			recordInitialDirectoryPreference(folderSourceFile);
			onReloadSource(false);
		}
	}

	@FXML
	void onReload(ActionEvent evt) {
		onReloadSource(true);
	}

	@FXML
	void onImport(ActionEvent evt) {
		ArrayList<CertStoreEntry> importedEntries = new ArrayList<>();
		String importEntryName = null;

		try {
			Collection<CertEntry> importSelection = validateAndGetImportSelection();
			boolean overwrite = validateAndGetOverwriteOption();
			PasswordCallback password = PasswordPromptCallback.getNewPassword(this);

			for (CertEntry importEntry : importSelection) {
				importEntryName = importEntry.getName();
				importedEntries.add(this.store.importEntry(importEntry, password, overwrite));
			}
			syncPreferences();
			getStage().close();
		} catch (InvalidInputException e) {
			showMessageBox(e.getLocalizedMessage(), null, MessageBoxStyle.ICON_ERROR, MessageBoxStyle.BUTTON_OK);
		} catch (IOException e) {
			showMessageBox(I18N.formatSTR_IMPORT_FAILED_MESSAGE(importEntryName), e, MessageBoxStyle.ICON_ERROR,
					MessageBoxStyle.BUTTON_OK);
		}
		this.result.onEntryImport(importedEntries);
	}

	@FXML
	void onCancel(ActionEvent evt) {
		getStage().close();
		this.result.onEntryImport(null);
	}

	@FXML
	void onHelp(ActionEvent evt) {
		try {
			HelpController.showHelp(this, Help.TOPIC_CERT_IMPORT);
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	void onReloadSource(boolean forced) {
		try {
			if (this.ctlFileSourceOption.isSelected()) {
				reloadFileSource();
			} else if (this.ctlFolderSourceOption.isSelected()) {
				reloadFolderSource();
			} else if (this.ctlURLSourceOption.isSelected()) {
				reloadURLSource();
			} else if (this.ctlClipboardSourceOption.isSelected()) {
				reloadClipboardSource();
			}
		} catch (InvalidInputException e) {
			this.ctlImportStatusIcon.setImage(forced ? Images.IMAGE_WARNING16 : Images.IMAGE_INFO16);
			this.ctlImportStatusMessage.setText(e.getLocalizedMessage());
		}
	}

	void onReloadScheduled() {
		this.ctlProgressGroup.setVisible(true);
	}

	void onReloadSucceeded(ImportStore importStore) {
		this.ctlProgressGroup.setVisible(false);

		int entryCount = importStore.getEntryCount();

		this.ctlImportStatusIcon.setImage(entryCount > 0 ? Images.IMAGE_INFO16 : Images.IMAGE_WARNING16);
		this.ctlImportStatusMessage.setText(I18N.formatSTR_ENTRIES_LOADED_MESSAGE(entryCount));
		updateImportSelection(importStore);
	}

	void onReloadFailed(Throwable cause) {
		this.ctlProgressGroup.setVisible(false);

		this.ctlImportStatusIcon.setImage(Images.IMAGE_WARNING16);
		this.ctlImportStatusMessage.setText(I18N.formatSTR_LOAD_ERROR_MESSAGE(cause.getLocalizedMessage()));
		LOG.warning(cause, null, this.ctlImportStatusMessage.getText());
	}

	void onImportSelectionSelectAll(Boolean selected) {
		TreeItem<ImportEntryModel> rootNode = this.ctlImportSelection.getRoot();

		if (rootNode != null) {
			setImportSelectionHelper(rootNode, selected);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.setTitle(I18N.formatSTR_CERT_IMPORT_TITLE());
		controllerStage.getIcons().addAll(Images.IMAGE_IMPORT16, Images.IMAGE_IMPORT32);
		this.ctlFileSourceInput.disableProperty().bind(Bindings.not(this.ctlFileSourceOption.selectedProperty()));
		this.ctlChooseFileSourceButton.disableProperty()
				.bind(Bindings.not(this.ctlFileSourceOption.selectedProperty()));
		this.ctlFolderSourceInput.disableProperty().bind(Bindings.not(this.ctlFolderSourceOption.selectedProperty()));
		this.ctlChooseFolderSourceButton.disableProperty()
				.bind(Bindings.not(this.ctlFolderSourceOption.selectedProperty()));
		this.ctlURLSourceInput.disableProperty().bind(Bindings.not(this.ctlURLSourceOption.selectedProperty()));
		this.ctlFileSourceOption.setSelected(true);
		this.ctlProgressGroup.setVisible(false);
		this.ctlImportSelection.setPlaceholder(new ImageView(Images.IMAGE_UNKNOWN32));
		this.ctlImportSelectionSelected.setCellValueFactory(new TreeItemPropertyValueFactory<>("selected"));
		this.ctlImportSelectionSelected
				.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlImportSelectionSelected));
		this.ctlImportSelectionName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		this.ctlImportSelectionHasKey.setCellValueFactory(new TreeItemPropertyValueFactory<>("hasKey"));
		this.ctlImportSelectionHasKey
				.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlImportSelectionHasKey));
		this.ctlImportSelectionHasCRT.setCellValueFactory(new TreeItemPropertyValueFactory<>("hasCRT"));
		this.ctlImportSelectionHasCRT
				.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlImportSelectionHasCRT));
		this.ctlImportSelectionHasCSR.setCellValueFactory(new TreeItemPropertyValueFactory<>("hasCSR"));
		this.ctlImportSelectionHasCSR
				.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlImportSelectionHasCSR));
		this.ctlImportSelectionHasCRL.setCellValueFactory(new TreeItemPropertyValueFactory<>("hasCRL"));
		this.ctlImportSelectionHasCRL
				.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlImportSelectionHasCRL));
		this.ctlImportSelection.setTreeColumn(this.ctlImportSelectionName);
		this.ctlFileSourceOption.getToggleGroup().selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> property, Toggle oldValue, Toggle newValue) {
				onReloadSource(false);
			}

		});
		this.ctlImportSelectionSelectedAll.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> property, Boolean oldValue, Boolean newValue) {
				onImportSelectionSelectAll(newValue);
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#getPreferences()
	 */
	@Override
	protected Preferences getPreferences() {
		return PREFERENCES;
	}

	/**
	 * Begin certificate import.
	 *
	 * @param storeParam The certificate store to import into.
	 * @param callback The callback to report the result of the user actions.
	 */
	public void beginCertImport(CertStore storeParam, Result callback) {
		assert storeParam != null;
		assert callback != null;

		this.result = callback;
		this.store = storeParam;
		getStage().sizeToScene();
	}

	private void reloadFileSource() throws InvalidInputException {
		Path fileSourcePath = validateAndGetFileSource();
		ImportSource[] sources = new ImportSource[] { ImportSource.fromFile(fileSourcePath) };

		getExecutorService().submit(new ReloadTask() {
			private ImportSource[] taskSources = sources;

			@Override
			protected ImportStore call() throws Exception {
				return reloadSource(this.taskSources);
			}

		});
	}

	private void reloadFolderSource() throws InvalidInputException {
		Path folderSourcePath = validateAndGetFolderSource();

		getExecutorService().submit(new ReloadTask() {
			private Path taskFolderPath = folderSourcePath;

			@Override
			protected ImportStore call() throws Exception {
				ImportStore importStore;

				try (Stream<Path> files = Files.walk(this.taskFolderPath)) {
					importStore = reloadSource(ImportSource.fromFiles(files));
				}
				return importStore;
			}

		});
	}

	private void reloadURLSource() throws InvalidInputException {
		URL urlSourceURL = validateAndGetURLSource();
		ImportSource[] sources = new ImportSource[] { ImportSource.fromURL(urlSourceURL) };

		getExecutorService().submit(new ReloadTask() {
			private ImportSource[] taskSources = sources;

			@Override
			protected ImportStore call() throws Exception {
				return reloadSource(this.taskSources);
			}

		});
	}

	private void reloadClipboardSource() throws InvalidInputException {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ImportSource[] sources;

		if (clipboard.hasString()) {
			sources = new ImportSource[] {
					ImportSource.fromData(clipboard.getString(), I18N.formatSTR_CLIPBOARD_RESOURCE_DATA(), null) };
		} else if (clipboard.hasFiles()) {
			sources = ImportSource.fromFiles(clipboard.getFiles(), I18N.formatSTR_CLIPBOARD_RESOURCE_FILE());
		} else {
			throw new InvalidInputException(I18N.formatSTR_INVALID_CLIPBOARD_SOURCE_MESSAGE());
		}
		getExecutorService().submit(new ReloadTask() {
			private ImportSource[] taskSources = sources;

			@Override
			protected ImportStore call() throws Exception {
				return reloadSource(this.taskSources);
			}

		});
	}

	ImportStore reloadSource(ImportSource[] sources) throws IOException {
		PasswordPromptCallback password = PasswordPromptCallback.getPassword(this);

		return ImportStore.open(sources, password);
	}

	private void updateImportSelection(ImportStore importStore) {
		ImportEntryModel rootItemValue = new ImportEntryModel();
		TreeItem<ImportEntryModel> rootItem = rootItemValue.toTreeItem();

		this.ctlImportSelection.setRoot(rootItem);
		updateImportSelectionHelper(rootItem, importStore, importStore.getRootEntries(),
				this.ctlImportSelectionSelectedAll.isSelected());
		rootItem.setExpanded(true);
	}

	private void updateImportSelectionHelper(TreeItem<ImportEntryModel> item, ImportStore importStore,
			Collection<CertEntry> entries, boolean selected) {
		for (CertEntry entry : entries) {
			ImportEntryModel entryItemValue = new ImportEntryModel(entry, selected);
			TreeItem<ImportEntryModel> entryItem = entryItemValue.toTreeItem();

			item.getChildren().add(entryItem);
			updateImportSelectionHelper(entryItem, importStore, importStore.getIssuedEntries(entry), selected);
			entryItem.setExpanded(true);
		}
	}

	private void setImportSelectionHelper(TreeItem<ImportEntryModel> node, Boolean selected) {
		ImportEntryModel value = node.getValue();

		if (value.getEntry() != null) {
			value.setSelected(selected);
		}
		for (TreeItem<ImportEntryModel> childNode : node.getChildren()) {
			setImportSelectionHelper(childNode, selected);
		}
	}

	private Path validateAndGetFileSource() throws InvalidInputException {
		String fileSourceInput = InputValidator.notEmpty(I18N.BUNDLE, I18N.STR_NO_FILE_SOURCE_MESSAGE,
				Strings.safeTrim(this.ctlFileSourceInput.getText()));

		return InputValidator.isRegularFile(I18N.BUNDLE, I18N.STR_INVALID_FILE_SOURCE_MESSAGE, fileSourceInput);
	}

	private Path validateAndGetFolderSource() throws InvalidInputException {
		String folderSourceInput = InputValidator.notEmpty(I18N.BUNDLE, I18N.STR_NO_FOLDER_SOURCE_MESSAGE,
				Strings.safeTrim(this.ctlFolderSourceInput.getText()));

		return InputValidator.isDirectory(I18N.BUNDLE, I18N.STR_INVALID_FOLDER_SOURCE, folderSourceInput);
	}

	private URL validateAndGetURLSource() throws InvalidInputException {
		String urlSourceInput = InputValidator.notEmpty(I18N.BUNDLE, I18N.STR_NO_URL_SOURCE_MESSAGE,
				Strings.safeTrim(this.ctlURLSourceInput.getText()));

		return InputValidator.isURL(I18N.BUNDLE, I18N.STR_INVALID_URL_SOURCE_MESSAGE, urlSourceInput);
	}

	private boolean validateAndGetOverwriteOption() {
		return this.ctlImportOverwriteOption.isSelected();
	}

	private Collection<CertEntry> validateAndGetImportSelection() throws InvalidInputException {
		ArrayList<CertEntry> selection = new ArrayList<>();
		TreeItem<ImportEntryModel> rootItem = this.ctlImportSelection.getRoot();

		if (rootItem != null) {
			collectImportSelectionHelper(selection, rootItem);
		}
		InputValidator.isTrue(I18N.BUNDLE, I18N.STR_NO_IMPORT_SELECTION_MESSAGE, !selection.isEmpty());
		return selection;
	}

	private void collectImportSelectionHelper(Collection<CertEntry> selection, TreeItem<ImportEntryModel> item) {
		ImportEntryModel itemValue = item.getValue();

		if (itemValue.getSelected().booleanValue()) {
			selection.add(itemValue.getEntry());
		}
		for (TreeItem<ImportEntryModel> childItem : item.getChildren()) {
			collectImportSelectionHelper(selection, childItem);
		}
	}

	private File getInitialDirectoryPreference() {
		File initialDirectory = null;
		String initialDirectoryPref = PREFERENCES.get(PREF_INITIAL_DIRECTORY, null);

		if (Strings.notEmpty(initialDirectoryPref)) {
			File initialDirectoryPrefFile = new File(initialDirectoryPref);

			if (initialDirectoryPrefFile.isDirectory()) {
				initialDirectory = initialDirectoryPrefFile;
			}
		}
		return initialDirectory;
	}

	private void recordInitialDirectoryPreference(File initialDirectory) {
		PREFERENCES.put(PREF_INITIAL_DIRECTORY, (initialDirectory != null ? initialDirectory.getPath() : ""));
	}

	private abstract class ReloadTask extends Task<ImportStore> {

		ReloadTask() {
			// Nothing to do here
		}

		/*
		 * (non-Javadoc)
		 * @see javafx.concurrent.Task#scheduled()
		 */
		@Override
		protected void scheduled() {
			onReloadScheduled();
		}

		@Override
		protected void failed() {
			onReloadFailed(getException());
		}

		@Override
		protected void succeeded() {
			onReloadSucceeded(getValue());
		}

	}

}
