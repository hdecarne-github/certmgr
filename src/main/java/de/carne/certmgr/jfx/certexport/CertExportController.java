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
package de.carne.certmgr.jfx.certexport;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import de.carne.certmgr.certs.CertObjectStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.io.CertWriters;
import de.carne.certmgr.certs.io.IOResource;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.certmgr.jfx.password.PasswordDialog;
import de.carne.jfx.application.PlatformHelper;
import de.carne.jfx.scene.control.Alerts;
import de.carne.jfx.stage.StageController;
import de.carne.jfx.util.FileChooserHelper;
import de.carne.jfx.util.validation.ValidationAlerts;
import de.carne.util.Strings;
import de.carne.util.prefs.PathPreference;
import de.carne.util.validation.InputValidator;
import de.carne.util.validation.PathValidator;
import de.carne.util.validation.ValidationException;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * Certificate export dialog.
 */
public class CertExportController extends StageController {

	private final Preferences preferences = Preferences.systemNodeForPackage(CertExportController.class);

	private final PathPreference preferenceInitalDirectory = new PathPreference(this.preferences, "initialDirectory",
			PathPreference.IS_DIRECTORY);

	private UserCertStoreEntry exportEntry;

	@FXML
	GridPane ctlControlPane;

	@FXML
	VBox ctlProgressOverlay;

	@FXML
	TextField ctlCertField;

	@FXML
	ChoiceBox<CertWriter> ctlFormatOption;

	@FXML
	RadioButton ctlFileDestinationOption;

	@FXML
	TextField ctlFileDestinationInput;

	@FXML
	Button cmdChooseFileDestinationButton;

	@FXML
	RadioButton ctlDirectoryDestinationOption;

	@FXML
	TextField ctlDirectoryDestinationInput;

	@FXML
	Button cmdChooseDirectoryDestinationButton;

	@FXML
	RadioButton ctlClipboardDestinationOption;

	@FXML
	CheckBox ctlEncryptOption;

	@FXML
	CheckBox ctlExportCertOption;

	@FXML
	CheckBox ctlExportChainOption;

	@FXML
	CheckBox ctlExportChainRootOption;

	@FXML
	CheckBox ctlExportKeyOption;

	@FXML
	CheckBox ctlExportCSROption;

	@FXML
	CheckBox ctlExportCRLOption;

	@FXML
	void onCmdChooseFileDestination(ActionEvent evt) {
		FileChooser chooser = new FileChooser();
		List<ExtensionFilter> extensionFilters = new ArrayList<>();
		CertWriter writer = this.ctlFormatOption.getValue();

		if (writer != null) {
			extensionFilters.add(new ExtensionFilter(writer.fileType(), writer.fileExtensionPatterns()));
		}
		extensionFilters.add(FileChooserHelper.filterFromString(CertExportI18N.formatSTR_FILTER_ALLFILES()));
		chooser.getExtensionFilters().addAll(extensionFilters);
		chooser.setSelectedExtensionFilter(extensionFilters.get(0));
		chooser.setInitialDirectory(this.preferenceInitalDirectory.getValueAsFile());

		File fileSource = chooser.showSaveDialog(getUI());

		if (fileSource != null) {
			this.ctlFileDestinationInput.setText(fileSource.getAbsolutePath());
			this.preferenceInitalDirectory.putValueFromFile(fileSource.getParentFile());
			syncPreferences();
		}
	}

	@FXML
	void onCmdChooseDirectoryDestination(ActionEvent evt) {
		DirectoryChooser chooser = new DirectoryChooser();

		chooser.setInitialDirectory(this.preferenceInitalDirectory.getValueAsFile());

		File directorySource = chooser.showDialog(getUI());

		if (directorySource != null) {
			this.ctlDirectoryDestinationInput.setText(directorySource.getAbsolutePath());
			this.preferenceInitalDirectory.putValueFromFile(directorySource);
			syncPreferences();
		}
	}

	@FXML
	void onCmdExport(ActionEvent evt) {
		try {
			CertWriter exportFormat = validateAndGetFormat();
			boolean encrypt = this.ctlEncryptOption.isSelected();
			boolean exportCert = this.ctlExportCertOption.isSelected();
			boolean exportChain = this.ctlExportChainOption.isSelected();
			boolean exportChainRoot = this.ctlExportChainRootOption.isSelected();
			boolean exportKey = this.ctlExportKeyOption.isSelected();
			boolean exportCSR = this.ctlExportCSROption.isSelected();
			boolean exportCRL = this.ctlExportCRLOption.isSelected();

			if (this.ctlFileDestinationOption.isSelected()) {
				Path exportFile = validateFileDestinationInput();

				getExecutorService().submit(new ExportTask<Path>(exportCert, exportChain, exportChainRoot, exportKey,
						exportCSR, exportCRL, exportFormat, exportFile, encrypt) {

					@Override
					protected void export(CertWriter format, Path param, CertObjectStore exportObjects,
							boolean encryptExport) throws IOException {
						exportToFile(format, param, exportObjects, encryptExport);
					}

				});
			} else if (this.ctlDirectoryDestinationOption.isSelected()) {
				Path exportDirectory = validateDirectoryDestinationInput();

				getExecutorService().submit(new ExportTask<Path>(exportCert, exportChain, exportChainRoot, exportKey,
						exportCSR, exportCRL, exportFormat, exportDirectory, encrypt) {

					@Override
					protected void export(CertWriter format, Path param, CertObjectStore exportObjects,
							boolean encryptExport) throws IOException {
						exportToDirectory(format, param, exportObjects, encryptExport);
					}

				});
			} else if (this.ctlClipboardDestinationOption.isSelected()) {
				getExecutorService().submit(new ExportTask<Void>(exportCert, exportChain, exportChainRoot, exportKey,
						exportCSR, exportCRL, exportFormat, null, encrypt) {

					@Override
					protected void export(CertWriter format, Void param, CertObjectStore exportObjects,
							boolean encryptExport) throws IOException {
						exportToClipboard(format, exportObjects, encryptExport);
					}

				});
			}
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
		}
	}

	@FXML
	void onCmdCancel(ActionEvent evt) {
		close(false);
	}

	@Override
	protected void setupStage(Stage stage) {
		stage.setTitle(CertExportI18N.formatSTR_STAGE_TITLE());
		this.ctlFileDestinationInput.disableProperty()
				.bind(Bindings.not(this.ctlFileDestinationOption.selectedProperty()));
		this.cmdChooseFileDestinationButton.disableProperty()
				.bind(Bindings.not(this.ctlFileDestinationOption.selectedProperty()));
		this.ctlDirectoryDestinationInput.disableProperty()
				.bind(Bindings.not(this.ctlDirectoryDestinationOption.selectedProperty()));
		this.cmdChooseDirectoryDestinationButton.disableProperty()
				.bind(Bindings.not(this.ctlDirectoryDestinationOption.selectedProperty()));
		this.ctlExportChainOption.disableProperty().bind(Bindings.not(this.ctlExportCertOption.selectedProperty()));
		this.ctlExportChainRootOption.disableProperty().bind(Bindings.not(Bindings
				.and(this.ctlExportCertOption.selectedProperty(), this.ctlExportChainOption.selectedProperty())));
		this.ctlFileDestinationOption.setSelected(true);
		this.ctlEncryptOption.setSelected(true);
		setupFormatOptions();
	}

	private void setupFormatOptions() {
		this.ctlFormatOption.getItems().addAll(CertWriters.REGISTERED.providers());
		this.ctlFormatOption.getItems().sort((o1, o2) -> o1.providerName().compareTo(o2.providerName()));
		this.ctlFormatOption.setValue(CertWriters.DEFAULT);
	}

	/**
	 * Initialize dialog for certificate generation.
	 *
	 * @param exportEntryParam The store entry to export.
	 * @return This controller.
	 */
	public CertExportController init(UserCertStoreEntry exportEntryParam) {
		this.exportEntry = exportEntryParam;
		this.ctlCertField.setText(this.exportEntry.getName());
		this.ctlExportCertOption.setDisable(!this.exportEntry.hasCRT());
		this.ctlExportCertOption.setSelected(this.exportEntry.hasCRT());
		this.ctlExportChainOption.setSelected(!this.exportEntry.isSelfSigned());
		this.ctlExportKeyOption.setDisable(!this.exportEntry.hasKey());
		this.ctlExportKeyOption.setSelected(this.exportEntry.hasKey());
		this.ctlExportCSROption.setDisable(!this.exportEntry.hasCSR());
		this.ctlExportCSROption.setSelected(this.exportEntry.hasCSR());
		this.ctlExportCRLOption.setDisable(!this.exportEntry.hasCRL());
		this.ctlExportCRLOption.setSelected(this.exportEntry.hasCRL());
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

	private CertWriter validateAndGetFormat() throws ValidationException {
		CertWriter writer = InputValidator.notNull(this.ctlFormatOption.getValue(),
				(a) -> CertExportI18N.formatSTR_MESSAGE_NO_FORMAT(a));

		InputValidator.isTrue(this.ctlEncryptOption.isSelected() || !writer.isEncryptionRequired(),
				(a) -> CertExportI18N.formatSTR_MESSAGE_ENCRYPTION_REQUIRED(writer.providerName()));
		InputValidator.isTrue(!this.ctlClipboardDestinationOption.isSelected() || writer.isCharWriter(),
				(a) -> CertExportI18N.formatSTR_MESSAGE_NO_CHARACTER_FORMAT(writer.providerName()));

		int exportObjectCount = 0;

		if (this.ctlExportCertOption.isSelected()) {
			exportObjectCount++;
		}
		if (this.ctlExportChainOption.isSelected()) {
			// This is not exact as the chain may be of length 0..n; however for
			// simplicity (regarding code and usability) we assume 1
			exportObjectCount++;
		}
		if (this.ctlExportKeyOption.isSelected()) {
			exportObjectCount++;
		}
		if (this.ctlExportCSROption.isSelected()) {
			exportObjectCount++;
		}
		if (this.ctlExportCRLOption.isSelected()) {
			exportObjectCount++;
		}
		InputValidator.isTrue(exportObjectCount > 0,
				(a) -> CertExportI18N.formatSTR_MESSAGE_NO_EXPORT(writer.providerName()));
		return writer;
	}

	private Path validateFileDestinationInput() throws ValidationException {
		String fileDestinationInput = InputValidator.notEmpty(Strings.safeTrim(this.ctlFileDestinationInput.getText()),
				(a) -> CertExportI18N.formatSTR_MESSAGE_NO_FILE(a));

		return PathValidator.isPath(fileDestinationInput, (a) -> CertExportI18N.formatSTR_MESSAGE_INVALID_FILE(a));
	}

	private Path validateDirectoryDestinationInput() throws ValidationException {
		String directoryDestinationInput = InputValidator.notEmpty(
				Strings.safeTrim(this.ctlDirectoryDestinationInput.getText()),
				(a) -> CertExportI18N.formatSTR_MESSAGE_NO_DIRECTORY(a));

		return PathValidator.isDirectoryPath(directoryDestinationInput,
				(a) -> CertExportI18N.formatSTR_MESSAGE_INVALID_DIRECTORY(a));
	}

	void exportToFile(CertWriter format, Path file, CertObjectStore exportObjects, boolean encryptExport)
			throws IOException {
		try (IOResource<OutputStream> out = IOResource.newOutputStream(file.toString(), file, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			if (encryptExport) {
				format.writeEncryptedBinary(out, exportObjects, PasswordDialog.enterNewPassword(this));
			} else {
				format.writeBinary(out, exportObjects);
			}
		}
	}

	void exportToDirectory(CertWriter format, Path directory, CertObjectStore exportObjects, boolean encryptExport)
			throws IOException {

	}

	void exportToClipboard(CertWriter format, CertObjectStore exportObjects, boolean encryptExport) throws IOException {
		StringWriter text = new StringWriter();

		try (IOResource<Writer> out = new IOResource<>(text, CertExportI18N.formatSTR_TEXT_CLIPBOARD())) {
			if (encryptExport) {
				format.writeEncryptedString(out, exportObjects, PasswordDialog.enterNewPassword(this));
			} else {
				format.writeString(out, exportObjects);
			}
		}

		String clipboardData = text.toString();

		PlatformHelper.runLater(() -> {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			ClipboardContent content = new ClipboardContent();

			content.putString(clipboardData);
			clipboard.setContent(content);
		});
	}

	CertObjectStore getExportObjectList(boolean exportCert, boolean exportChain, boolean exportChainRoot,
			boolean exportKey, boolean exportCSR, boolean exportCRL) throws IOException {
		CertObjectStore exportObjects = new CertObjectStore();
		String exportEntryAlias = this.exportEntry.id().getAlias();

		if (exportCert) {
			exportObjects.addCRT(exportEntryAlias, this.exportEntry.getCRT());
			if (exportChain && !this.exportEntry.isSelfSigned()) {
				UserCertStoreEntry issuer = this.exportEntry.issuer();

				while (!issuer.isSelfSigned()) {
					if (issuer.hasCRT()) {
						exportObjects.addCRT(issuer.id().getAlias(), issuer.getCRT());
					}
					issuer = issuer.issuer();
				}
				if (exportChainRoot && issuer.hasCRT()) {
					exportObjects.addCRT(issuer.id().getAlias(), issuer.getCRT());
				}
			}
		}
		if (exportKey) {
			exportObjects.addKey(exportEntryAlias, this.exportEntry.getKey(PasswordDialog.enterPassword(this)));
		}
		if (exportCSR) {
			exportObjects.addCSR(exportEntryAlias, this.exportEntry.getCSR());
		}
		if (exportCRL) {
			exportObjects.addCRL(exportEntryAlias, this.exportEntry.getCRL());
		}
		return exportObjects;
	}

	private abstract class ExportTask<P> extends BackgroundTask<Void> {

		private final boolean exportCert;
		private final boolean exportChain;
		private final boolean exportChainRoot;
		private final boolean exportKey;
		private final boolean exportCSR;
		private final boolean exportCRL;
		private final CertWriter exportFormat;
		private final P exportParam;
		private final boolean encrypt;

		ExportTask(boolean exportCert, boolean exportChain, boolean exportChainRoot, boolean exportKey,
				boolean exportCSR, boolean exportCRL, CertWriter exportFormat, P exportParam, boolean encrypt) {
			this.exportCert = exportCert;
			this.exportChain = exportChain;
			this.exportChainRoot = exportChainRoot;
			this.exportKey = exportKey;
			this.exportCSR = exportCSR;
			this.exportCRL = exportCRL;
			this.exportFormat = exportFormat;
			this.exportParam = exportParam;
			this.encrypt = encrypt;
		}

		protected abstract void export(CertWriter format, P param, CertObjectStore exportObjects, boolean encryptExport)
				throws IOException;

		@Override
		protected Void call() throws Exception {
			CertObjectStore exportObjects = getExportObjectList(this.exportCert, this.exportChain, this.exportChainRoot,
					this.exportKey, this.exportCSR, this.exportCRL);

			export(this.exportFormat, this.exportParam, exportObjects, this.encrypt);
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
