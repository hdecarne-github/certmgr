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
package de.carne.certmgr.jfx.dneditor;

import javax.security.auth.x500.X500Principal;

import org.checkerframework.checker.nullness.qual.Nullable;

import de.carne.certmgr.certs.x500.RDN;
import de.carne.certmgr.certs.x500.X500Names;
import de.carne.jfx.scene.control.DialogController;
import de.carne.jfx.scene.control.ListViewEditor;
import de.carne.jfx.util.validation.ValidationAlerts;
import de.carne.util.Strings;
import de.carne.util.validation.ValidationException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * DN editor dialog.
 */
public class DNEditorController extends DialogController<X500Principal> implements Callback<ButtonType, X500Principal> {

	private final ListViewEditor<RDN> rdnEntriesEditor = new ListViewEditor<RDN>() {

		@Override
		protected @Nullable RDN getInput() {
			return getRDNInput();
		}

		@Override
		protected void setInput(@Nullable RDN input) {
			setRDNInput(input);
		}

	};

	private X500Principal dnResult = null;

	@FXML
	ComboBox<String> ctlTypeInput;

	@FXML
	TextField ctlValueInput;

	@FXML
	Button cmdAddRDN;

	@FXML
	Button cmdApplyRDN;

	@FXML
	Button cmdDeleteRDN;

	@FXML
	Button cmdMoveRDNUp;

	@FXML
	Button cmdMoveRDNDown;

	@FXML
	ListView<RDN> ctlRDNEntries;

	@FXML
	void onAddRDN(ActionEvent evt) {
		this.rdnEntriesEditor.onAddAction(evt);
	}

	RDN getRDNInput() {
		String typeInput = Strings.safeTrim(Strings.safe(this.ctlTypeInput.getValue()));
		String valueInput = Strings.safeTrim(Strings.safe(this.ctlValueInput.getText()));
		RDN rdn;

		if (Strings.isEmpty(typeInput)) {
			rdn = null;
		} else if (Strings.isEmpty(valueInput)) {
			rdn = null;
		} else {
			rdn = new RDN(typeInput, valueInput);
		}
		return rdn;
	}

	void setRDNInput(RDN rdn) {
		if (rdn != null) {
			this.ctlTypeInput.setValue(rdn.getType());
			this.ctlValueInput.setText(rdn.getValue());
		}
	}

	private void onApply(ActionEvent evt) {
		try {
			this.dnResult = validateAndGetDN();
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
			evt.consume();
		}
	}

	@Override
	protected void setupDialog(Dialog<X500Principal> dialog) {
		dialog.setTitle(DNEditorI18N.formatSTR_STAGE_TITLE());
		this.rdnEntriesEditor.init(this.ctlRDNEntries).setAddCommand(this.cmdAddRDN).setApplyCommand(this.cmdApplyRDN)
				.setDeleteCommand(this.cmdDeleteRDN).setMoveUpCommand(this.cmdMoveRDNUp)
				.setMoveDownCommand(this.cmdMoveRDNDown);
		this.ctlTypeInput.getItems().addAll(X500Names.rdnTypes());
		this.ctlTypeInput.getItems().sort((o1, o2) -> o1.compareTo(o2));
		this.ctlTypeInput.requestFocus();
		addButtonEventFilter(ButtonType.APPLY, (evt) -> onApply(evt));
	}

	/**
	 * Initialize the DN editor's content.
	 *
	 * @param dnInput The current DN input.
	 * @return This controller.
	 */
	public DNEditorController init(String dnInput) {
		assert dnInput != null;

		ObservableList<RDN> rdnItems = this.ctlRDNEntries.getItems();

		for (RDN rdn : X500Names.decodeDN(dnInput, false)) {
			rdnItems.add(rdn);
		}
		return this;
	}

	private X500Principal validateAndGetDN() throws ValidationException {
		ObservableList<RDN> rdnItems = this.ctlRDNEntries.getItems();
		RDN[] rdns = rdnItems.toArray(new RDN[rdnItems.size()]);
		X500Principal dn;

		try {
			dn = X500Names.encodeDN(rdns);
		} catch (IllegalArgumentException e) {
			throw new ValidationException(DNEditorI18N.formatSTR_MESSAGE_INVALIDDN(e.getLocalizedMessage()), e);
		}
		return dn;
	}

	@Override
	public X500Principal call(ButtonType param) {
		return this.dnResult;
	}

}
