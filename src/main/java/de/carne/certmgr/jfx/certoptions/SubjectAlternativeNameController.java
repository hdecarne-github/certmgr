/*
 * Copyright (c) 2015-2018 Holger de Carne and contributors, All Rights Reserved.
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
import de.carne.certmgr.jfx.resources.Images;
import de.carne.check.Nullable;
import de.carne.jfx.scene.control.DialogController;
import de.carne.jfx.scene.control.ListViewEditor;
import de.carne.jfx.scene.control.Tooltips;
import de.carne.jfx.util.DefaultSet;
import de.carne.jfx.util.validation.InputValidator;
import de.carne.jfx.util.validation.ValidationAlerts;
import de.carne.jfx.util.validation.ValidationException;
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
 * Subject Alternative Name dialog.
 */
public class SubjectAlternativeNameController extends DialogController<SubjectAlternativeNameExtensionData>
		implements Callback<ButtonType, SubjectAlternativeNameExtensionData> {

	private final ListViewEditor<GeneralName> namesEditor = new ListViewEditor<GeneralName>() {

		@Override
		@Nullable
		protected GeneralName getInput() {
			return getGeneralNameInput();
		}

		@Override
		protected void setInput(@Nullable GeneralName input) {
			setGeneralNameInput(input);
		}

	};

	@Nullable
	private SubjectAlternativeNameExtensionData extensionDataResult = null;

	@SuppressWarnings("null")
	@FXML
	CheckBox ctlCritical;

	@SuppressWarnings("null")
	@FXML
	ChoiceBox<GeneralNameType> ctlNameTypeOption;

	@SuppressWarnings("null")
	@FXML
	TextField ctlNameInput;

	@SuppressWarnings("null")
	@FXML
	Button cmdAddName;

	@SuppressWarnings("null")
	@FXML
	Button cmdApplyName;

	@SuppressWarnings("null")
	@FXML
	Button cmdDeleteName;

	@SuppressWarnings("null")
	@FXML
	Button cmdMoveNameUp;

	@SuppressWarnings("null")
	@FXML
	Button cmdMoveNameDown;

	@SuppressWarnings("null")
	@FXML
	ListView<GeneralName> ctlNames;

	@FXML
	void onAddName(ActionEvent evt) {
		this.namesEditor.onAddAction(evt);
	}

	@Nullable
	GeneralName getGeneralNameInput() {
		GeneralName name = null;

		try {
			name = GeneralNameFactory.toGeneralName(this.ctlNameTypeOption.getValue(), this.ctlNameInput.getText());
		} catch (IllegalArgumentException e) {
			Tooltips.show(this.ctlNameInput, e.getLocalizedMessage(), Images.WARNING16);
		}
		return name;
	}

	void setGeneralNameInput(@Nullable GeneralName name) {
		if (name != null) {
			this.ctlNameTypeOption.setValue(name.getType());
			this.ctlNameInput.setText(name.toValueString());
		}
	}

	private void onApply(ActionEvent evt) {
		try {
			boolean critical = this.ctlCritical.isSelected();
			GeneralNames names = validateAndGetNames();

			this.extensionDataResult = new SubjectAlternativeNameExtensionData(critical, names);
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
			evt.consume();
		}
	}

	@Override
	protected void setupDialog(Dialog<SubjectAlternativeNameExtensionData> dialog) {
		dialog.setTitle(SubjectAlternativeNameI18N.formatSTR_STAGE_TITLE());
		this.namesEditor.init(this.ctlNames).setAddCommand(this.cmdAddName).setApplyCommand(this.cmdApplyName)
				.setDeleteCommand(this.cmdDeleteName).setMoveUpCommand(this.cmdMoveNameUp)
				.setMoveDownCommand(this.cmdMoveNameDown);
		addButtonEventFilter(ButtonType.APPLY, this::onApply);
		this.ctlNameInput.requestFocus();
	}

	/**
	 * Initialize the dialog.
	 *
	 * @param expertMode Whether to run in expert mode ({@code true}) or not ({@code false}).
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
	 * @param expertMode Whether to run in expert mode ({@code true}) or not ({@code false}).
	 * @return This controller.
	 */
	public SubjectAlternativeNameController init(SubjectAlternativeNameExtensionData data, boolean expertMode) {
		init(expertMode);
		this.ctlCritical.setSelected(data.getCritical());

		ObservableList<GeneralName> nameItems = this.ctlNames.getItems();

		for (GeneralName name : data.getGeneralNames()) {
			nameItems.add(name);
		}
		return this;
	}

	private void initNameTypeOptions() {
		DefaultSet<GeneralNameType> types = GeneralNameFactory.alternateNameTypes();

		this.ctlNameTypeOption.getItems().addAll(types);
		this.ctlNameTypeOption.getItems().sort((o1, o2) -> o1.name().compareTo(o2.name()));
		this.ctlNameTypeOption.setValue(types.getDefault());
	}

	private GeneralNames validateAndGetNames() throws ValidationException {
		GeneralNames names = new GeneralNames();
		int nameCount = 0;

		for (GeneralName name : this.ctlNames.getItems()) {
			names.addName(name);
			nameCount++;
		}
		InputValidator.isTrue(nameCount > 0, SubjectAlternativeNameI18N::formatSTR_MESSAGE_NO_NAMES);
		return names;
	}

	@Override
	@Nullable
	public SubjectAlternativeNameExtensionData call(@Nullable ButtonType param) {
		return this.extensionDataResult;
	}

}
