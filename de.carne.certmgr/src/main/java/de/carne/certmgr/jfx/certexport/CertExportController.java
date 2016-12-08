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
package de.carne.certmgr.jfx.certexport;

import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.io.CertWriters;
import de.carne.certmgr.certs.spi.CertWriter;
import de.carne.jfx.stage.StageController;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Certificate export dialog.
 */
public class CertExportController extends StageController {

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

	}

	@FXML
	void onCmdChooseDirectoryDestination(ActionEvent evt) {

	}

	@FXML
	void onCmdExport(ActionEvent evt) {

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
		assert exportEntryParam != null;

		this.exportEntry = exportEntryParam;
		this.ctlCertField.setText(this.exportEntry.getName());
		this.ctlExportCertOption.setDisable(!this.exportEntry.hasCRT());
		this.ctlExportKeyOption.setDisable(!this.exportEntry.hasKey());
		this.ctlExportCSROption.setDisable(!this.exportEntry.hasCSR());
		this.ctlExportCRLOption.setDisable(!this.exportEntry.hasCRL());
		return this;
	}

}
