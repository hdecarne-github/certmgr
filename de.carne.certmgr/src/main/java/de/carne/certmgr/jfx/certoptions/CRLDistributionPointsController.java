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

import de.carne.certmgr.certs.x509.CRLDistributionPointsExtensionData;
import de.carne.certmgr.certs.x509.DistributionPoint;
import de.carne.certmgr.certs.x509.DistributionPointName;
import de.carne.certmgr.certs.x509.GeneralName;
import de.carne.certmgr.certs.x509.GeneralNameType;
import de.carne.certmgr.certs.x509.GeneralNames;
import de.carne.certmgr.jfx.resources.Images;
import de.carne.jfx.scene.control.DialogController;
import de.carne.jfx.scene.control.Tooltips;
import de.carne.jfx.util.validation.ValidationAlerts;
import de.carne.util.DefaultSet;
import de.carne.util.validation.InputValidator;
import de.carne.util.validation.ValidationException;
import javafx.collections.ObservableList;
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
 * CRL Distribution Points dialog.
 */
public class CRLDistributionPointsController extends DialogController<CRLDistributionPointsExtensionData>
		implements Callback<ButtonType, CRLDistributionPointsExtensionData> {

	private CRLDistributionPointsExtensionData extensionDataResult = null;

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
		GeneralName name = getGeneralNameInput();

		if (name != null) {
			this.ctlNames.getItems().add(name);
			this.ctlNames.getSelectionModel().select(name);
		}
	}

	@FXML
	void onApplyName(ActionEvent evt) {
		int nameIndex = this.ctlNames.getSelectionModel().getSelectedIndex();

		if (nameIndex >= 0) {
			GeneralName name = getGeneralNameInput();

			if (name != null) {
				this.ctlNames.getItems().remove(nameIndex);
				this.ctlNames.getItems().add(nameIndex, name);
				this.ctlNames.getSelectionModel().select(name);
			}
		}
	}

	@FXML
	void onDeleteName(ActionEvent evt) {
		int nameIndex = this.ctlNames.getSelectionModel().getSelectedIndex();

		if (nameIndex >= 0) {
			this.ctlNames.getItems().remove(nameIndex);
		}
	}

	@FXML
	void onMoveNameUp(ActionEvent evt) {
		int nameIndex = this.ctlNames.getSelectionModel().getSelectedIndex();

		if (nameIndex > 0) {
			ObservableList<GeneralName> nameItems = this.ctlNames.getItems();
			GeneralName name = nameItems.remove(nameIndex);

			nameItems.add(nameIndex - 1, name);
			this.ctlNames.getSelectionModel().select(name);
		}
	}

	@FXML
	void onMoveNameDown(ActionEvent evt) {
		int nameIndex = this.ctlNames.getSelectionModel().getSelectedIndex();
		ObservableList<GeneralName> nameItems = this.ctlNames.getItems();

		if (nameIndex >= 0 && nameIndex + 1 < nameItems.size()) {
			GeneralName name = nameItems.remove(nameIndex);

			nameItems.add(nameIndex + 1, name);
			this.ctlNames.getSelectionModel().select(name);
		}
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
			DistributionPoint distributionPoint = validateAndGetDistributionPoint();

			this.extensionDataResult = new CRLDistributionPointsExtensionData(critical);
			this.extensionDataResult.addDistributionPoint(distributionPoint);
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
			evt.consume();
		}
	}

	@Override
	protected void setupDialog(Dialog<CRLDistributionPointsExtensionData> dialog) {
		dialog.setTitle(CRLDistributionPointsI18N.formatSTR_STAGE_TITLE());
		this.cmdApplyName.disableProperty().bind(this.ctlNames.getSelectionModel().selectedItemProperty().isNull());
		this.cmdDeleteName.disableProperty().bind(this.ctlNames.getSelectionModel().selectedItemProperty().isNull());
		this.cmdMoveNameUp.disableProperty().bind(this.ctlNames.getSelectionModel().selectedItemProperty().isNull());
		this.cmdMoveNameDown.disableProperty().bind(this.ctlNames.getSelectionModel().selectedItemProperty().isNull());
		this.ctlNames.getSelectionModel().selectedItemProperty().addListener((p, o, n) -> onNameSelectionChanged(n));
		addButtonEventFilter(ButtonType.APPLY, (evt) -> onApply(evt));
		this.ctlNameInput.requestFocus();
	}

	/**
	 * Initialize the dialog.
	 *
	 * @param expertMode Whether to run in expert mode ({@code true}) or not
	 *        ({@code false}).
	 * @return This controller.
	 */
	public CRLDistributionPointsController init(boolean expertMode) {
		this.ctlCritical.setSelected(CRLDistributionPointsExtensionData.CRITICAL_DEFAULT);
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
	public CRLDistributionPointsController init(CRLDistributionPointsExtensionData data, boolean expertMode) {
		init(expertMode);
		this.ctlCritical.setSelected(data.getCritical());

		ObservableList<GeneralName> nameItems = this.ctlNames.getItems();

		for (DistributionPoint distributionPoint : data) {
			DistributionPointName distributionPointName = distributionPoint.getName();

			if (distributionPointName != null) {
				for (GeneralName name : distributionPointName.getFullName()) {
					nameItems.add(name);
				}
				break;
			}
		}
		return this;
	}

	private void initNameTypeOptions() {
		DefaultSet<GeneralNameType> types = GeneralNameFactory.locationTypes();

		this.ctlNameTypeOption.getItems().addAll(types);
		this.ctlNameTypeOption.getItems().sort((o1, o2) -> o1.name().compareTo(o2.name()));
		this.ctlNameTypeOption.setValue(types.getDefault());
	}

	private GeneralName getGeneralNameInput() {
		GeneralName name = null;

		try {
			name = GeneralNameFactory.toGeneralName(this.ctlNameTypeOption.getValue(), this.ctlNameInput.getText());
		} catch (IllegalArgumentException e) {
			Tooltips.show(this.ctlNameInput, e.getLocalizedMessage(), Images.WARNING16);
		}
		return name;
	}

	private DistributionPoint validateAndGetDistributionPoint() throws ValidationException {
		GeneralNames names = new GeneralNames();
		int nameCount = 0;

		for (GeneralName name : this.ctlNames.getItems()) {
			names.addName(name);
			nameCount++;
		}
		InputValidator.isTrue(nameCount > 0, (a) -> CRLDistributionPointsI18N.formatSTR_MESSAGE_NO_NAMES());
		return new DistributionPoint(new DistributionPointName(names));
	}

	@Override
	public CRLDistributionPointsExtensionData call(ButtonType param) {
		return this.extensionDataResult;
	}

}
