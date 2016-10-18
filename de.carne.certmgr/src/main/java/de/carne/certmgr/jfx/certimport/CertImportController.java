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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.io.CertReaders;
import de.carne.certmgr.certs.spi.CertReader;
import de.carne.certmgr.jfx.UserCertStoreTreeTableViewHelper;
import de.carne.certmgr.jfx.password.PasswordDialog;
import de.carne.certmgr.jfx.resources.Images;
import de.carne.certmgr.jfx.store.StoreController;
import de.carne.jfx.application.PlatformHelper;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * Certificate import dialog.
 */
public class CertImportController extends StageController {

	private final Preferences preferences = Preferences.systemNodeForPackage(StoreController.class);

	private final DirectoryPreference preferenceInitalDirectory = new DirectoryPreference(this.preferences,
			"initialDirectory", true);

	private UserCertStoreTreeTableViewHelper<ImportUserCertStoreEntryModel> sourceEntryInputHelper = null;

	private UserCertStore sourceStore = null;

	@FXML
	RadioButton ctlFileSourceOption;

	@FXML
	RadioButton ctlFolderSourceOption;

	@FXML
	RadioButton ctlClipboardSourceOption;

	@FXML
	RadioButton ctlURLSourceOption;

	@FXML
	RadioButton ctlServerSourceOption;

	@FXML
	TextField ctlFileSourceInput;

	@FXML
	Button ctlChooseFileSourceButton;

	@FXML
	TextField ctlFolderSourceInput;

	@FXML
	Button ctlChooseFolderSourceButton;

	@FXML
	TextField ctlURLSourceInput;

	@FXML
	TextField ctlServerSourceInput;

	@FXML
	TreeTableView<ImportUserCertStoreEntryModel> ctlSourceEntryInput;

	@FXML
	TreeTableColumn<ImportUserCertStoreEntryModel, Boolean> ctlSourceEntryInputSelected;

	@FXML
	TreeTableColumn<ImportUserCertStoreEntryModel, String> ctlSourceEntryInputCN;

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

		chooser.setInitialDirectory(this.preferenceInitalDirectory.getValueAsFile());

		List<ExtensionFilter> extensionFilters = new ArrayList<>();

		extensionFilters.add(FileChooserHelper.filterFromString(CertImportI18N.formatSTR_FILTER_ALLFILES()));
		for (CertReader reader : CertReaders.REGISTERED.providers()) {
			extensionFilters.add(new ExtensionFilter(reader.fileType(), reader.fileExtensions()));
		}

		chooser.getExtensionFilters().addAll(extensionFilters);
		chooser.setSelectedExtensionFilter(extensionFilters.get(0));

		File fileSource = chooser.showOpenDialog(getUI());

		if (fileSource != null) {
			this.ctlFileSourceInput.setText(fileSource.getAbsolutePath());
			this.preferenceInitalDirectory.putValueFromFile(fileSource.getParentFile());
			syncPreferences();
			onCmdFileReload();
		}
	}

	@FXML
	void onCmdChooseFolderSource(ActionEvent evt) {
		DirectoryChooser chooser = new DirectoryChooser();

		chooser.setInitialDirectory(this.preferenceInitalDirectory.getValueAsFile());

		File folderSource = chooser.showDialog(getUI());

		if (folderSource != null) {
			this.ctlFolderSourceInput.setText(folderSource.getAbsolutePath());
			this.preferenceInitalDirectory.putValueFromFile(folderSource);
			syncPreferences();
		}
	}

	@FXML
	void onCmdReload(ActionEvent evt) {
		if (this.ctlFileSourceOption.isSelected()) {
			onCmdFileReload();
		} else if (this.ctlFolderSourceOption.isSelected()) {

		} else if (this.ctlClipboardSourceOption.isSelected()) {

		} else if (this.ctlURLSourceOption.isSelected()) {

		} else if (this.ctlServerSourceOption.isSelected()) {

		}
	}

	@FXML
	void onCmdImport(ActionEvent evt) {

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
		this.ctlFolderSourceInput.disableProperty().bind(Bindings.not(this.ctlFolderSourceOption.selectedProperty()));
		this.ctlChooseFolderSourceButton.disableProperty()
				.bind(Bindings.not(this.ctlFolderSourceOption.selectedProperty()));
		this.ctlURLSourceInput.disableProperty().bind(Bindings.not(this.ctlURLSourceOption.selectedProperty()));
		this.ctlServerSourceInput.disableProperty().bind(Bindings.not(this.ctlServerSourceOption.selectedProperty()));
		this.ctlSourceEntryInputSelected
				.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(this.ctlSourceEntryInputSelected));
		this.ctlSourceEntryInputSelected.setCellValueFactory(new TreeItemPropertyValueFactory<>("selected"));
		this.ctlSourceEntryInputCN.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
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
		this.ctlSourceEntryInput.setTreeColumn(this.ctlSourceEntryInputCN);
		this.ctlFileSourceOption.setSelected(true);
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
			e.printStackTrace();
		}
	}

	private void updateSourceEntryInput() {
		if (this.sourceEntryInputHelper == null) {
			this.sourceEntryInputHelper = new UserCertStoreTreeTableViewHelper<>(this.ctlSourceEntryInput,
					(e) -> new ImportUserCertStoreEntryModel(e, false));
		}
		this.sourceEntryInputHelper.update(this.sourceStore);
	}

	private Path validateFileSourceInput() throws ValidationException {
		String fileSourceInput = InputValidator.notEmpty(Strings.safeTrim(this.ctlFileSourceInput.getText()),
				(a) -> CertImportI18N.formatSTR_MESSAGE_NO_FILE(a));

		return PathValidator.isReadableFile(fileSourceInput, (a) -> CertImportI18N.formatSTR_MESSAGE_INVALID_FILE(a));
	}

}
