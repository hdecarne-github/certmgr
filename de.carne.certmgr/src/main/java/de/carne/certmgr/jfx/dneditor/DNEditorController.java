/*
 * Copyright (c) 2014-2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.jfx.dneditor;

import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import de.carne.certmgr.jfx.EditableComboBoxTableCell;
import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.jfx.StageController;
import de.carne.certmgr.jfx.help.Help;
import de.carne.certmgr.jfx.help.HelpController;
import de.carne.certmgr.store.x500.RDN;
import de.carne.certmgr.store.x500.X500Names;
import de.carne.util.Strings;

/**
 * Dialog controller for DN editing.
 */
public class DNEditorController extends StageController {

	/**
	 * Controller's callback interface.
	 */
	public interface Result {

		/**
		 * Called after the user has edited and confirmed the DN.
		 *
		 * @param dnInputParam The edit DN input.
		 */
		public void onDNEdit(String dnInputParam);

	}

	private Result result = null;

	@FXML
	TableView<RDNModel> ctlRDNEntries;

	@FXML
	TableColumn<RDNModel, String> ctlRDNEntriesType;

	@FXML
	TableColumn<RDNModel, String> ctlRDNEntriesValue;

	@FXML
	void onOk(ActionEvent evt) {
		StringBuilder buffer = new StringBuilder();

		for (RDNModel rdn : this.ctlRDNEntries.getItems()) {
			String type = rdn.getType();
			String value = rdn.getValue();

			if (Strings.notEmpty(type) && Strings.notEmpty(value)) {
				X500Names.encodeDN(buffer, type, value);
			}
		}
		this.result.onDNEdit(buffer.toString());
		getStage().close();
	}

	@FXML
	void onCancel(ActionEvent evt) {
		getStage().close();
	}

	@FXML
	void onHelp(ActionEvent evt) {
		try {
			HelpController.showHelp(this, Help.TOPIC_DNEDITOR);
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	@FXML
	void onRDNTypeInputCommit(CellEditEvent<RDNModel, String> evt) {
		ObservableList<RDNModel> tableItems = evt.getTableView().getItems();
		int tableInputIndex = evt.getTablePosition().getRow();
		RDNModel inputModel = tableItems.get(tableInputIndex);
		String newValue = evt.getNewValue();

		inputModel.setType(newValue);
		if (tableInputIndex + 1 == tableItems.size()) {
			tableItems.add(new RDNModel());
		}
	}

	@FXML
	void onRDNValueInputCommit(CellEditEvent<RDNModel, String> evt) {
		ObservableList<RDNModel> tableItems = evt.getTableView().getItems();
		int tableInputIndex = evt.getTablePosition().getRow();
		RDNModel inputModel = tableItems.get(tableInputIndex);
		String newValue = evt.getNewValue();

		inputModel.setValue(newValue);
		if (tableInputIndex + 1 == tableItems.size()) {
			tableItems.add(new RDNModel());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.setTitle(getBundle().getString(I18N.TEXT_TITLE));
		controllerStage.getIcons().addAll(Images.IMAGE_DNEDIT16, Images.IMAGE_DNEDIT32);
		this.ctlRDNEntriesType.setCellValueFactory(new PropertyValueFactory<>("type"));
		this.ctlRDNEntriesType.setCellFactory(EditableComboBoxTableCell.forTableColumn(X500Names.getTypes()));
		this.ctlRDNEntriesValue.setCellValueFactory(new PropertyValueFactory<>("value"));
		this.ctlRDNEntriesValue.setCellFactory(TextFieldTableCell.forTableColumn());
	}

	/**
	 * Begin DN editing.
	 *
	 * @param dnInput The ND string to edit.
	 * @param callback The callback to report the result of the user actions.
	 */
	public void beginDNEdit(String dnInput, Result callback) {
		assert dnInput != null;
		assert callback != null;

		this.result = callback;

		ObservableList<RDNModel> rdnModels = this.ctlRDNEntries.getItems();

		rdnModels.clear();
		for (RDN rdn : X500Names.decodeDN(dnInput, false)) {
			rdnModels.add(new RDNModel(rdn));
		}
		rdnModels.add(new RDNModel());
		getStage().sizeToScene();
	}

}
