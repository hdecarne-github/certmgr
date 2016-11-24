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

import de.carne.certmgr.certs.x509.BasicConstraintsExtensionData;
import de.carne.jfx.scene.control.DialogController;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

/**
 * Basic constraints dialog.
 */
public class BasicConstraintsController extends DialogController<BasicConstraintsExtensionData>
		implements Callback<ButtonType, BasicConstraintsExtensionData> {

	@FXML
	CheckBox ctlCritical;

	@FXML
	CheckBox ctlCA;

	@FXML
	ComboBox<Integer> ctlPathLenConstraint;

	@Override
	protected void setupDialog(Dialog<BasicConstraintsExtensionData> dialog) {
		dialog.setTitle(BasicConstraintsI18N.formatSTR_STAGE_TITLE());
		this.ctlPathLenConstraint.disableProperty().bind(Bindings.not(this.ctlCA.selectedProperty()));
	}

	/**
	 * Initialize the dialog.
	 *
	 * @return This controller.
	 */
	public BasicConstraintsController init(boolean expertMode) {
		initExpertMode(expertMode);
		initPathLenConstraint();
		return this;
	}

	/**
	 * Initialize the dialog with existing extension data.
	 *
	 * @param data The extension data to use.
	 * @return This controller.
	 */
	public BasicConstraintsController init(BasicConstraintsExtensionData data, boolean expertMode) {
		init(expertMode);
		return this;
	}

	private void initExpertMode(boolean expertMode) {
		this.ctlPathLenConstraint.setEditable(expertMode);
	}

	private void initPathLenConstraint() {

	}

	@Override
	public BasicConstraintsExtensionData call(ButtonType param) {
		// TODO Auto-generated method stub
		return null;
	}

}
