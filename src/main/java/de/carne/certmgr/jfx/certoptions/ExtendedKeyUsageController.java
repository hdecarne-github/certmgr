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

import java.util.HashSet;
import java.util.Set;

import de.carne.boot.check.Nullable;
import de.carne.certmgr.certs.x509.ExtendedKeyUsage;
import de.carne.certmgr.certs.x509.ExtendedKeyUsageExtensionData;
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
 * Extended key usage dialog.
 */
public class ExtendedKeyUsageController extends DialogController<ExtendedKeyUsageExtensionData>
		implements Callback<ButtonType, ExtendedKeyUsageExtensionData> {

	@Nullable
	private ExtendedKeyUsageExtensionData extensionDataResult = null;

	@FXML
	CheckBox ctlCritical;

	@FXML
	CheckBox ctlAnyUsage;

	@FXML
	ListView<ExtendedKeyUsage> ctlUsages;

	@SuppressWarnings("unused")
	private void onApply(ActionEvent evt) {
		boolean critical = this.ctlCritical.isSelected();
		Set<ExtendedKeyUsage> usages = new HashSet<>();

		if (this.ctlAnyUsage.isSelected()) {
			usages.add(ExtendedKeyUsage.ANY);
		} else {
			for (ExtendedKeyUsage usage : this.ctlUsages.getSelectionModel().getSelectedItems()) {
				usages.add(usage);
			}
		}
		this.extensionDataResult = new ExtendedKeyUsageExtensionData(critical, usages);
	}

	@Override
	protected void setupDialog(Dialog<ExtendedKeyUsageExtensionData> dialog) {
		dialog.setTitle(ExtendedKeyUsageI18N.formatSTR_STAGE_TITLE());
		this.ctlUsages.disableProperty().bind(this.ctlAnyUsage.selectedProperty());
		addButtonEventFilter(ButtonType.APPLY, this::onApply);
	}

	/**
	 * Initialize the dialog.
	 *
	 * @param expertMode Whether to run in expert mode ({@code true}) or not ({@code false}).
	 * @return This controller.
	 */
	public ExtendedKeyUsageController init(boolean expertMode) {
		this.ctlCritical.setSelected(ExtendedKeyUsageExtensionData.CRITICAL_DEFAULT);

		ObservableList<ExtendedKeyUsage> usageItems = this.ctlUsages.getItems();

		for (ExtendedKeyUsage usage : ExtendedKeyUsage.instances()) {
			if (!ExtendedKeyUsage.ANY.equals(usage)) {
				usageItems.add(usage);
			}
		}
		usageItems.sort((o1, o2) -> o1.name().compareTo(o2.name()));
		this.ctlUsages.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.ctlAnyUsage.setSelected(false);
		return this;
	}

	/**
	 * Initialize the dialog with existing extension data.
	 *
	 * @param data The extension data to use.
	 * @param expertMode Whether to run in expert mode ({@code true}) or not ({@code false}).
	 * @return This controller.
	 */
	public ExtendedKeyUsageController init(ExtendedKeyUsageExtensionData data, boolean expertMode) {
		init(expertMode);
		this.ctlCritical.setSelected(data.getCritical());
		if (data.hasUsage(ExtendedKeyUsage.ANY)) {
			this.ctlAnyUsage.setSelected(true);
		} else {
			for (ExtendedKeyUsage usage : data) {
				this.ctlUsages.getSelectionModel().select(usage);
			}
		}
		return this;
	}

	@Override
	@Nullable
	public ExtendedKeyUsageExtensionData call(@Nullable ButtonType param) {
		return this.extensionDataResult;
	}

}
