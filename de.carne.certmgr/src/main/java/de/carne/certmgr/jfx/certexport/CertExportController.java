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
package de.carne.certmgr.jfx.certexport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.prefs.Preferences;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import de.carne.certmgr.jfx.CertFileFormats;
import de.carne.certmgr.jfx.CertStoreEntryOption;
import de.carne.certmgr.jfx.ClipboardExportTarget;
import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.jfx.InputValidator;
import de.carne.certmgr.jfx.InvalidInputException;
import de.carne.certmgr.jfx.StageController;
import de.carne.certmgr.jfx.help.Help;
import de.carne.certmgr.jfx.help.HelpController;
import de.carne.certmgr.jfx.messagebox.MessageBoxStyle;
import de.carne.certmgr.jfx.passwordprompt.PasswordPromptCallback;
import de.carne.certmgr.store.CertFileFormat;
import de.carne.certmgr.store.CertStore;
import de.carne.certmgr.store.CertStoreEntry;
import de.carne.certmgr.store.CertStoreEntryExporter;
import de.carne.certmgr.store.ExportTarget;
import de.carne.certmgr.store.FileExportTarget;
import de.carne.certmgr.store.FolderExportTarget;
import de.carne.certmgr.store.PasswordCallback;
import de.carne.certmgr.util.Strings;

/**
 * Dialog controller for esporting certificate data.
 */
public class CertExportController extends StageController {

	private static final Preferences PREFERENCES = Preferences.systemNodeForPackage(CertExportController.class);

	private static final String PREF_INITIAL_DIRECTORY = "initialDirectory";

	private CertStore store = null;

	@FXML
	ComboBox<CertStoreEntryOption> ctlExportCertSelection;

	@FXML
	ChoiceBox<CertFileFormat> ctlEncodingSelection;

	@FXML
	RadioButton ctlFileTargetOption;

	@FXML
	TextField ctlFileTargetInput;

	@FXML
	Button ctlChooseFileTargetButton;

	@FXML
	RadioButton ctlFolderTargetOption;

	@FXML
	TextField ctlFolderTargetInput;

	@FXML
	Button ctlChooseFolderTargetButton;

	@FXML
	RadioButton ctlClipboardTargetOption;

	@FXML
	CheckBox ctlEncryptExportOption;

	@FXML
	CheckBox ctlIncludeCRTOption;

	@FXML
	CheckBox ctlIncludeCRTChainOption;

	@FXML
	CheckBox ctlIncludeCRTAnchorOption;

	@FXML
	CheckBox ctlIncludeKeyOption;

	@FXML
	CheckBox ctlIncludeCSROption;

	@FXML
	CheckBox ctlIncludeCRLOption;

	@FXML
	Pane ctlProgressGroup;

	@FXML
	void onChooseFileTarget(ActionEvent evt) {
		FileChooser fileTargetChooser = new FileChooser();

		fileTargetChooser.getExtensionFilters().addAll(
				CertFileFormats.getFileChooserFilters(this.ctlEncodingSelection.getValue()));

		String fileTargetInput = Strings.safeTrim(this.ctlFileTargetInput.getText());
		File fileTargetFile;

		if (Strings.notEmpty(fileTargetInput)) {
			fileTargetFile = new File(fileTargetInput);
			if (fileTargetFile.isFile()) {
				fileTargetChooser.setInitialDirectory(fileTargetFile.getParentFile());
				fileTargetChooser.setInitialFileName(fileTargetFile.getPath());
			}
		}
		if (fileTargetChooser.getInitialDirectory() == null) {
			File initalDirectoryPreference = getInitialDirectoryPreference();

			if (initalDirectoryPreference != null) {
				fileTargetChooser.setInitialDirectory(initalDirectoryPreference);
			}
		}
		fileTargetFile = fileTargetChooser.showSaveDialog(getStage());
		if (fileTargetFile != null) {
			this.ctlFileTargetInput.setText(fileTargetFile.getPath());
			recordInitialDirectoryPreference(fileTargetFile.getParent());
		}
	}

	@FXML
	void onChooseFolderTarget(ActionEvent evt) {
		DirectoryChooser folderTargetChooser = new DirectoryChooser();
		String folderTargetInput = Strings.safeTrim(this.ctlFolderTargetInput.getText());
		File folderTargetFile;

		if (Strings.notEmpty(folderTargetInput)) {
			folderTargetFile = new File(folderTargetInput);
			if (folderTargetFile.isDirectory()) {
				folderTargetChooser.setInitialDirectory(folderTargetFile);
			}
		}
		if (folderTargetChooser.getInitialDirectory() == null) {
			File initalDirectoryPreference = getInitialDirectoryPreference();

			if (initalDirectoryPreference != null) {
				folderTargetChooser.setInitialDirectory(initalDirectoryPreference);
			}
		}
		folderTargetFile = folderTargetChooser.showDialog(getStage());
		if (folderTargetFile != null) {
			this.ctlFolderTargetInput.setText(folderTargetFile.getPath());
			recordInitialDirectoryPreference(folderTargetFile.getPath());
		}
	}

	@FXML
	void onExport(ActionEvent evt) {
		String exportEntryName = null;

		try {
			CertStoreEntry exportEntry = validateAndGetExportCertEntry();

			exportEntryName = exportEntry.getName();

			CertFileFormat encoding = validateAndGetEncoding();
			ExportTarget exportTarget;

			if (this.ctlFileTargetOption.isSelected()) {
				exportTarget = validateAndGetFileTarget();
			} else if (this.ctlFolderTargetOption.isSelected()) {
				exportTarget = validateAndGetFolderTarget();
			} else if (this.ctlClipboardTargetOption.isSelected()) {
				exportTarget = new ClipboardExportTarget();
			} else {
				throw new InvalidInputException(I18N.format(I18N.MESSAGE_NOEXPORTTARGET));
			}

			boolean includeKey = this.ctlIncludeKeyOption.isSelected();
			boolean includeCRT = this.ctlIncludeCRTOption.isSelected();
			boolean includeCRTChain = this.ctlIncludeCRTChainOption.isSelected();
			boolean includeCRTAnchor = this.ctlIncludeCRTAnchorOption.isSelected();
			boolean includeCSR = this.ctlIncludeCSROption.isSelected();
			boolean includeCRL = this.ctlIncludeCRLOption.isSelected();

			if (!includeKey && !includeCRT && !includeCSR && !includeCRL) {
				throw new InvalidInputException(I18N.format(I18N.MESSAGE_NOEXPORTOBJECT));
			}

			PasswordCallback exportEntryPassword = PasswordPromptCallback.getPassword(this);
			CertStoreEntryExporter exporter = CertStoreEntryExporter.forEntry(exportEntry, includeKey, includeCRT,
					includeCRTChain, includeCRTAnchor, includeCSR, includeCRL, exportEntryPassword);
			PasswordCallback exportPassword = (this.ctlEncryptExportOption.isSelected() ? PasswordPromptCallback
					.getNewPassword(this) : null);

			runTask(new ExportTask() {
				private CertStoreEntryExporter exporter2 = exporter;
				private CertFileFormat encoding2 = encoding;
				private ExportTarget exportTarget2 = exportTarget;
				private PasswordCallback exportPassword2 = exportPassword;

				@Override
				protected Void call() throws Exception {
					this.exporter2.export(this.encoding2, this.exportTarget2, this.exportPassword2);
					return null;
				}

			});
		} catch (InvalidInputException e) {
			showMessageBox(e.getLocalizedMessage(), null, MessageBoxStyle.ICON_ERROR, MessageBoxStyle.BUTTON_OK);
		} catch (IOException e) {
			showMessageBox(I18N.format(I18N.MESSAGE_EXPORTFAILED, exportEntryName), e, MessageBoxStyle.ICON_ERROR,
					MessageBoxStyle.BUTTON_OK);
		}
	}

	@FXML
	void onCancel(ActionEvent evt) {
		getStage().close();
	}

	@FXML
	void onHelp(ActionEvent evt) {
		try {
			HelpController.showHelp(this, Help.TOPIC_CERTEXPORT);
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	void onEncodingSelected(CertFileFormat encoding) {
		CertStoreEntryOption selectedExportCertOption = this.ctlExportCertSelection.getValue();
		CertStoreEntry exportEntry = (selectedExportCertOption != null ? selectedExportCertOption.getEntry() : null);

		updateExportOptions(encoding, exportEntry);
	}

	void onExportCertSelected(CertStoreEntryOption exportCert) {
		updateExportOptions(this.ctlEncodingSelection.getValue(), exportCert.getEntry());
	}

	void onExportScheduled() {
		this.ctlProgressGroup.setVisible(true);
	}

	void onExportSucceeded() {
		this.ctlProgressGroup.setVisible(false);
		syncPreferences();
		getStage().close();
	}

	void onExportFailed(Throwable cause) {
		this.ctlProgressGroup.setVisible(false);

		CertStoreEntryOption selectedExportCertOption = this.ctlExportCertSelection.getValue();
		String exportEntryName = (selectedExportCertOption != null ? selectedExportCertOption.getEntry().getName()
				: null);

		showMessageBox(I18N.format(I18N.MESSAGE_EXPORTFAILED, exportEntryName), cause, MessageBoxStyle.ICON_ERROR,
				MessageBoxStyle.BUTTON_OK);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.setTitle(getBundle().getString(I18N.TEXT_TITLE));
		controllerStage.getIcons().addAll(Images.IMAGE_EXPORT16, Images.IMAGE_EXPORT32);

		this.ctlFileTargetInput.disableProperty().bind(Bindings.not(this.ctlFileTargetOption.selectedProperty()));
		this.ctlChooseFileTargetButton.disableProperty()
				.bind(Bindings.not(this.ctlFileTargetOption.selectedProperty()));
		this.ctlFolderTargetInput.disableProperty().bind(Bindings.not(this.ctlFolderTargetOption.selectedProperty()));
		this.ctlChooseFolderTargetButton.disableProperty().bind(
				Bindings.not(this.ctlFolderTargetOption.selectedProperty()));
		this.ctlFileTargetOption.setSelected(true);
		this.ctlEncryptExportOption.setSelected(true);
		this.ctlIncludeCRTChainOption.disableProperty().bind(Bindings.not(this.ctlIncludeCRTOption.selectedProperty()));
		this.ctlIncludeCRTAnchorOption.disableProperty().bind(
				Bindings.not(Bindings.and(this.ctlIncludeCRTOption.selectedProperty(),
						this.ctlIncludeCRTChainOption.selectedProperty())));
		this.ctlProgressGroup.setVisible(false);
		this.ctlEncodingSelection.getItems().addAll(CertFileFormat.values());
		this.ctlEncodingSelection.valueProperty().addListener(new ChangeListener<CertFileFormat>() {

			@Override
			public void changed(ObservableValue<? extends CertFileFormat> property, CertFileFormat oldValue,
					CertFileFormat newValue) {
				onEncodingSelected(newValue);
			}

		});
		this.ctlEncodingSelection.getSelectionModel().select(CertFileFormat.PEM);
		this.ctlExportCertSelection.valueProperty().addListener(new ChangeListener<CertStoreEntryOption>() {

			@Override
			public void changed(ObservableValue<? extends CertStoreEntryOption> property,
					CertStoreEntryOption oldValue, CertStoreEntryOption newValue) {
				onExportCertSelected(newValue);
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
	 * Begin certificate export.
	 *
	 * @param storeParam The certificate store to import into.
	 * @param exportEntryParam The certificate store entry to export.
	 */
	public void beginCertExport(CertStore storeParam, CertStoreEntry exportEntryParam) {
		assert storeParam != null;
		assert exportEntryParam != null;

		this.store = storeParam;

		List<CertStoreEntryOption> exportCertOptions = CertStoreEntryOption.fromStoreWithPredicate(this.store,
				e -> !e.isExternal());
		CertStoreEntryOption selectedExportCertOption = CertStoreEntryOption.findOption(exportCertOptions,
				exportEntryParam);

		this.ctlExportCertSelection.getItems().addAll(exportCertOptions);
		this.ctlExportCertSelection.getSelectionModel().select(selectedExportCertOption);
		getStage().sizeToScene();
	}

	private void updateExportOptions(CertFileFormat encoding, CertStoreEntry entry) {
		boolean noFolderTargetSupport = false;
		boolean noClipboardTargetSupport = false;
		boolean encryptionSelected = true;
		boolean noEncryptionSupport = false;
		boolean includeCRTSelected = entry != null && entry.hasCRT();
		boolean noIncludeCRTSupport = !includeCRTSelected;
		boolean includeKeySelected = entry != null && entry.hasKey();
		boolean noIncludeKeySupport = !includeKeySelected;
		boolean includeCSRSelected = entry != null && entry.hasCSR();
		boolean noIncludeCSRSupport = !includeCSRSelected;
		boolean includeCRLSelected = entry != null && entry.hasCRL();
		boolean noIncludeCRLSupport = !includeCRLSelected;

		if (encoding != null) {
			switch (encoding) {
			case PEM:
				// Nothing to do here
				break;
			case PKCS12:
				noFolderTargetSupport = true;
				noClipboardTargetSupport = true;
				includeCSRSelected = false;
				noIncludeCSRSupport = true;
				includeCRLSelected = false;
				noIncludeCRLSupport = true;
				break;
			default:
				throw new IllegalArgumentException("Unexpected encoding: " + encoding);
			}
		}
		this.ctlFolderTargetOption.setDisable(noFolderTargetSupport);
		if (noFolderTargetSupport && this.ctlFolderTargetOption.isSelected()) {
			this.ctlFolderTargetOption.getToggleGroup().selectToggle(this.ctlFileTargetOption);
		}
		this.ctlClipboardTargetOption.setDisable(noClipboardTargetSupport);
		if (noClipboardTargetSupport && this.ctlClipboardTargetOption.isSelected()) {
			this.ctlClipboardTargetOption.getToggleGroup().selectToggle(this.ctlFileTargetOption);
		}
		this.ctlEncryptExportOption.setDisable(noEncryptionSupport);
		this.ctlEncryptExportOption.setSelected(encryptionSelected);
		this.ctlIncludeCRTOption.setDisable(noIncludeCRTSupport);
		this.ctlIncludeCRTOption.setSelected(includeCRTSelected);
		this.ctlIncludeCRTChainOption.setSelected(includeCRTSelected);
		this.ctlIncludeCRTAnchorOption.setSelected(includeCRTSelected);
		this.ctlIncludeKeyOption.setDisable(noIncludeKeySupport);
		this.ctlIncludeKeyOption.setSelected(includeKeySelected);
		this.ctlIncludeCSROption.setDisable(noIncludeCSRSupport);
		this.ctlIncludeCSROption.setSelected(includeCSRSelected);
		this.ctlIncludeCRLOption.setDisable(noIncludeCRLSupport);
		this.ctlIncludeCRLOption.setSelected(includeCRLSelected);
	}

	private CertStoreEntry validateAndGetExportCertEntry() throws InvalidInputException {
		CertStoreEntryOption selectedExportCertOption = InputValidator.notNull(I18N.bundle(),
				I18N.MESSAGE_NOEXPORTCERT, this.ctlExportCertSelection.getValue());

		return InputValidator.notNull(I18N.bundle(), I18N.MESSAGE_NOEXPORTCERT, selectedExportCertOption.getEntry());
	}

	private CertFileFormat validateAndGetEncoding() throws InvalidInputException {
		return InputValidator.notNull(I18N.bundle(), I18N.MESSAGE_NOENCODING, this.ctlEncodingSelection.getValue());
	}

	private ExportTarget validateAndGetFileTarget() throws InvalidInputException {
		String fileTargetInput = InputValidator.notEmpty(I18N.bundle(), I18N.MESSAGE_NOFILETARGET,
				Strings.safeTrim(this.ctlFileTargetInput.getText()));
		Path fileTargetPath = InputValidator.isPath(I18N.bundle(), I18N.MESSAGE_INVALIDFILETARGET, fileTargetInput);

		return new FileExportTarget(fileTargetPath);
	}

	private ExportTarget validateAndGetFolderTarget() throws InvalidInputException {
		String folderTargetInput = InputValidator.notEmpty(I18N.bundle(), I18N.MESSAGE_NOFOLDERTARGET,
				Strings.safeTrim(this.ctlFolderTargetInput.getText()));
		Path folderTargetPath = InputValidator.isDirectory(I18N.bundle(), I18N.MESSAGE_INVALIDFOLDERTARGET,
				folderTargetInput);

		return new FolderExportTarget(folderTargetPath);
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

	private void recordInitialDirectoryPreference(String initialDirectory) {
		PREFERENCES.put(PREF_INITIAL_DIRECTORY, initialDirectory);
	}

	private abstract class ExportTask extends Task<Void> {
		
		ExportTask() {
			// Nothing to do here
		}

		/*
		 * (non-Javadoc)
		 * @see javafx.concurrent.Task#scheduled()
		 */
		@Override
		protected void scheduled() {
			onExportScheduled();
		}

		@Override
		protected void failed() {
			onExportFailed(getException());
		}

		@Override
		protected void succeeded() {
			onExportSucceeded();
		}

	}

}
