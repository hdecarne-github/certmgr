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

import java.util.HashSet;
import java.util.Set;

import de.carne.certmgr.certs.x509.KeyUsage;
import de.carne.certmgr.certs.x509.KeyUsageExtensionData;
import de.carne.jfx.scene.control.DialogController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.util.Callback;

/**
 * Basic constraints dialog.
 */
public class KeyUsageController extends DialogController<KeyUsageExtensionData>
		implements Callback<ButtonType, KeyUsageExtensionData> {

	private KeyUsageExtensionData extensionDataResult = null;

	@FXML
	CheckBox ctlCritical;

	@FXML
	CheckBox ctlAnyUsage;

	@FXML
	ListView<KeyUsage> ctlUsages;

	private void onApply(ActionEvent evt) {
		boolean critical = this.ctlCritical.isSelected();
		Set<KeyUsage> usages = new HashSet<>();

		if (this.ctlAnyUsage.isSelected()) {
			usages.add(KeyUsage.ANY);
		} else {
			for (KeyUsage usage : this.ctlUsages.getSelectionModel().getSelectedItems()) {
				usages.add(usage);
			}
		}
		this.extensionDataResult = new KeyUsageExtensionData(critical, usages);
	}

	@Override
	protected void setupDialog(Dialog<KeyUsageExtensionData> dialog) {
		dialog.setTitle(BasicConstraintsI18N.formatSTR_STAGE_TITLE());
		this.ctlUsages.disableProperty().bind(this.ctlAnyUsage.selectedProperty());
		addButtonEventFilter(ButtonType.APPLY, (evt) -> onApply(evt));
	}

	/**
	 * Initialize the dialog.
	 *
	 * @param expertMode Whether to run in expert mode ({@code true}) or not
	 *        ({@code false}).
	 * @return This controller.
	 */
	public KeyUsageController init(boolean expertMode) {
		ObservableList<KeyUsage> usageItems = this.ctlUsages.getItems();

		for (KeyUsage usage : KeyUsage.instances()) {
			if (!KeyUsage.ANY.equals(usage)) {
				usageItems.add(usage);
			}
		}
		usageItems.sort((o1, o2) -> o1.value().compareTo(o2.value()));
		this.ctlUsages.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.ctlAnyUsage.setSelected(false);
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
	public KeyUsageController init(KeyUsageExtensionData data, boolean expertMode) {
		init(expertMode);
		this.ctlCritical.setSelected(data.getCritical());
		if (data.hasUsage(KeyUsage.ANY)) {
			this.ctlAnyUsage.setSelected(true);
		} else {
			for (KeyUsage usage : data) {
				this.ctlUsages.getSelectionModel().select(usage);
			}
		}
		return this;
	}

	@Override
	public KeyUsageExtensionData call(ButtonType param) {
		return this.extensionDataResult;
	}

}
