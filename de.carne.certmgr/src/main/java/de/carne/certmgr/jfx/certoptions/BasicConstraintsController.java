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

import java.util.Arrays;

import de.carne.certmgr.certs.x509.BasicConstraintsExtensionData;
import de.carne.certmgr.util.DefaultSet;
import de.carne.jfx.scene.control.DialogController;
import de.carne.jfx.util.validation.ValidationAlerts;
import de.carne.util.validation.InputValidator;
import de.carne.util.validation.ValidationException;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

/**
 * Basic constraints dialog.
 */
public class BasicConstraintsController extends DialogController<BasicConstraintsExtensionData>
		implements Callback<ButtonType, BasicConstraintsExtensionData> {

	private static DefaultSet<Integer> DEFAULT_PATH_LENS = new DefaultSet<>(Arrays.asList(null, 0, 1, 2, 3, 4, 5));

	private BasicConstraintsExtensionData extensionDataResult = null;

	@FXML
	CheckBox ctlCritical;

	@FXML
	CheckBox ctlCA;

	@FXML
	ComboBox<Integer> ctlPathLenConstraint;

	private void onApply(ActionEvent evt) {
		try {
			boolean critical = this.ctlCritical.isSelected();
			boolean ca = this.ctlCA.isSelected();
			Integer pathLenConstraint = null;

			if (ca) {
				pathLenConstraint = valdiateAndGetPathLenConstraint();
			}
			this.extensionDataResult = new BasicConstraintsExtensionData(critical, ca, pathLenConstraint);
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
			evt.consume();
		}
	}

	@Override
	protected void setupDialog(Dialog<BasicConstraintsExtensionData> dialog) {
		dialog.setTitle(BasicConstraintsI18N.formatSTR_STAGE_TITLE());
		this.ctlPathLenConstraint.disableProperty().bind(Bindings.not(this.ctlCA.selectedProperty()));
		this.ctlPathLenConstraint.setConverter(new IntegerStringConverter());
		addButtonEventFilter(ButtonType.APPLY, (evt) -> onApply(evt));
	}

	/**
	 * Initialize the dialog.
	 *
	 * @param expertMode Whether to run in expert mode ({@code true}) or not
	 *        ({@code false}).
	 * @return This controller.
	 */
	public BasicConstraintsController init(boolean expertMode) {
		this.ctlCA.setSelected(BasicConstraintsExtensionData.CRITICAL_DEFAULT);
		initExpertMode(expertMode);
		initPathLenConstraint();
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
	public BasicConstraintsController init(BasicConstraintsExtensionData data, boolean expertMode) {
		init(expertMode);
		this.ctlCritical.setSelected(data.getCritical());
		this.ctlCA.setSelected(data.getCA());
		this.ctlPathLenConstraint.setValue(data.getPathLenConstraint());
		return this;
	}

	private void initExpertMode(boolean expertMode) {
		this.ctlPathLenConstraint.setEditable(expertMode);
	}

	private void initPathLenConstraint() {
		this.ctlPathLenConstraint.getItems().addAll(DEFAULT_PATH_LENS);
		this.ctlPathLenConstraint.setValue(DEFAULT_PATH_LENS.getDefault());
	}

	private Integer valdiateAndGetPathLenConstraint() throws ValidationException {
		Integer pathLenConstraint = this.ctlPathLenConstraint.getValue();

		if (pathLenConstraint != null) {
			InputValidator.isTrue(pathLenConstraint.intValue() >= 0,
					(a) -> BasicConstraintsI18N.formatSTR_MESSAGE_INVALID_PATH_LEN_CONSTRAINT());
		}
		return pathLenConstraint;
	}

	@Override
	public BasicConstraintsExtensionData call(ButtonType param) {
		return this.extensionDataResult;
	}

}
