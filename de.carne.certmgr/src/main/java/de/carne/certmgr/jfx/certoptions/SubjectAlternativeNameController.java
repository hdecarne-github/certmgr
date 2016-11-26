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
package de.carne.certmgr.jfx.certoptions;

import de.carne.certmgr.certs.x509.GeneralName;
import de.carne.certmgr.certs.x509.GeneralNameType;
import de.carne.certmgr.certs.x509.GeneralNames;
import de.carne.certmgr.certs.x509.SubjectAlternativeNameExtensionData;
import de.carne.certmgr.util.DefaultSet;
import de.carne.jfx.scene.control.DialogController;
import de.carne.jfx.util.validation.ValidationAlerts;
import de.carne.util.validation.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * Subject Alternative Name dialog.
 */
public class SubjectAlternativeNameController extends DialogController<SubjectAlternativeNameExtensionData>
		implements Callback<ButtonType, SubjectAlternativeNameExtensionData> {

	private SubjectAlternativeNameExtensionData extensionDataResult = null;

	@FXML
	CheckBox ctlCritical;

	@FXML
	ChoiceBox<GeneralNameType> ctlNameTypeOption;

	@FXML
	TextField ctlNameInput;

	@FXML
	Button cmdApplyName;

	@FXML
	Button cmdDeleteName;

	@FXML
	Button cmdMoveNameUp;

	@FXML
	Button cmdMoveNameDown;

	@FXML
	ListView<GeneralName> ctlNames;

	@FXML
	void onAddName(ActionEvent evt) {
		try {
			GeneralName name = GeneralNameFactory.toGeneralName(this.ctlNameTypeOption.getValue(),
					this.ctlNameInput.getText());

			this.ctlNames.getItems().add(name);
			this.ctlNames.getSelectionModel().select(name);
		} catch (IllegalArgumentException e) {

		}

	}

	@FXML
	void onApplyName(ActionEvent evt) {

	}

	@FXML
	void onDeleteName(ActionEvent evt) {

	}

	@FXML
	void onMoveNameUp(ActionEvent evt) {

	}

	@FXML
	void onMoveNameDown(ActionEvent evt) {

	}

	private void onNameSelectionChanged(GeneralName name) {
		if (name != null) {
			this.ctlNameTypeOption.setValue(name.getType());
			this.ctlNameInput.setText(name.toValueString());
		}
	}

	private void onApply(ActionEvent evt) {
		try {
			boolean critical = this.ctlCritical.isSelected();
			GeneralNames generalNames = validateAndGetGeneralNames();

			this.extensionDataResult = new SubjectAlternativeNameExtensionData(critical, generalNames);
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
			evt.consume();
		}
	}

	@Override
	protected void setupDialog(Dialog<SubjectAlternativeNameExtensionData> dialog) {
		dialog.setTitle(SubjectAlternativeNameI18N.formatSTR_STAGE_TITLE());
		this.cmdApplyName.disableProperty().bind(this.ctlNames.getSelectionModel().selectedItemProperty().isNull());
		this.cmdDeleteName.disableProperty().bind(this.ctlNames.getSelectionModel().selectedItemProperty().isNull());
		this.cmdMoveNameUp.disableProperty().bind(this.ctlNames.getSelectionModel().selectedItemProperty().isNull());
		this.cmdMoveNameDown.disableProperty().bind(this.ctlNames.getSelectionModel().selectedItemProperty().isNull());
		this.ctlNames.getSelectionModel().selectedItemProperty().addListener((p, o, n) -> onNameSelectionChanged(n));
		addButtonEventFilter(ButtonType.APPLY, (evt) -> onApply(evt));
	}

	/**
	 * Initialize the dialog.
	 *
	 * @param expertMode Whether to run in expert mode ({@code true}) or not
	 *        ({@code false}).
	 * @return This controller.
	 */
	public SubjectAlternativeNameController init(boolean expertMode) {
		this.ctlCritical.setSelected(SubjectAlternativeNameExtensionData.CRITICAL_DEFAULT);
		initNameTypeOptions();
		return this;
	}

	/**
	 * Initialize the dialog with existing extension data.
	 *
	 * @param data The extension data to use.
	 * @param expertMode Whether to run in expert mode ({@code true}) or not
	 *        ({@code false}).
	 * @return This controller.
	 */
	public SubjectAlternativeNameController init(SubjectAlternativeNameExtensionData data, boolean expertMode) {
		init(expertMode);
		this.ctlCritical.setSelected(data.getCritical());
		return this;
	}

	private void initNameTypeOptions() {
		DefaultSet<GeneralNameType> types = GeneralNameFactory.types();

		this.ctlNameTypeOption.getItems().addAll(types);
		this.ctlNameTypeOption.getItems().sort((o1, o2) -> o1.name().compareTo(o2.name()));
		this.ctlNameTypeOption.setValue(types.getDefault());
	}

	private GeneralNames validateAndGetGeneralNames() throws ValidationException {
		return null;
	}

	@Override
	public SubjectAlternativeNameExtensionData call(ButtonType param) {
		return this.extensionDataResult;
	}

}
