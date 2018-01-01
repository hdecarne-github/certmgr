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

import java.math.BigInteger;

import de.carne.certmgr.certs.x509.BasicConstraintsExtensionData;
import de.carne.check.Check;
import de.carne.check.Nullable;
import de.carne.jfx.scene.control.Controls;
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

/**
 * Basic Constraints dialog.
 */
public class BasicConstraintsController extends DialogController<BasicConstraintsExtensionData>
		implements Callback<ButtonType, BasicConstraintsExtensionData> {

	@Nullable
	private BasicConstraintsExtensionData extensionDataResult = null;

	@SuppressWarnings("null")
	@FXML
	CheckBox ctlCritical;

	@SuppressWarnings("null")
	@FXML
	CheckBox ctlCA;

	@SuppressWarnings("null")
	@FXML
	ComboBox<BasicConstraintsPathLen> ctlPathLenConstraint;

	private void onApply(ActionEvent evt) {
		try {
			boolean critical = this.ctlCritical.isSelected();
			boolean ca = this.ctlCA.isSelected();
			BigInteger pathLenConstraint = null;

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
		this.ctlPathLenConstraint.setConverter(BasicConstraintsPathLen.CONVERTER);
		addButtonEventFilter(ButtonType.APPLY, this::onApply);
	}

	/**
	 * Initialize the dialog.
	 *
	 * @param expertMode Whether to run in expert mode ({@code true}) or not ({@code false}).
	 * @return This controller.
	 */
	public BasicConstraintsController init(boolean expertMode) {
		this.ctlCritical.setSelected(BasicConstraintsExtensionData.CRITICAL_DEFAULT);
		initExpertMode(expertMode);
		initPathLenConstraint();
		return this;
	}

	/**
	 * Initialize the dialog with existing extension data.
	 *
	 * @param data The extension data to use.
	 * @param expertMode Whether to run in expert mode ({@code true}) or not ({@code false}).
	 * @return This controller.
	 */
	public BasicConstraintsController init(BasicConstraintsExtensionData data, boolean expertMode) {
		init(expertMode);
		this.ctlCritical.setSelected(data.getCritical());
		this.ctlCA.setSelected(data.getCA());
		this.ctlPathLenConstraint.setValue(BasicConstraintsPathLen.valueOf(data.getPathLenConstraint()));
		return this;
	}

	private void initExpertMode(boolean expertMode) {
		this.ctlPathLenConstraint.setEditable(expertMode);
	}

	private void initPathLenConstraint() {
		Controls.resetComboBoxOptions(this.ctlPathLenConstraint, BasicConstraintsPathLen.DEFAULT_SET,
				(o1, o2) -> o1.compareTo(o2));
	}

	private BigInteger valdiateAndGetPathLenConstraint() throws ValidationException {
		BasicConstraintsPathLen pathLenConstraint = InputValidator.notNull(this.ctlPathLenConstraint.getValue(),
				BasicConstraintsI18N::formatSTR_MESSAGE_NO_PATH_LEN_CONSTRAINT);
		BigInteger pathLenConstraintValue = pathLenConstraint.value();

		InputValidator.isTrue(pathLenConstraintValue == null || pathLenConstraintValue.compareTo(BigInteger.ZERO) >= 0,
				BasicConstraintsI18N::formatSTR_MESSAGE_INVALID_PATH_LEN_CONSTRAINT);
		return Check.nonNull(pathLenConstraintValue);
	}

	@Override
	@Nullable
	public BasicConstraintsExtensionData call(@Nullable ButtonType param) {
		return this.extensionDataResult;
	}

}
