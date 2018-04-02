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
package de.carne.certmgr.jfx.dneditor;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

import de.carne.certmgr.certs.x500.X500Names;
import de.carne.certmgr.jfx.resources.Images;
import de.carne.check.Nullable;
import de.carne.jfx.scene.control.DialogController;
import de.carne.jfx.scene.control.ListViewEditor;
import de.carne.jfx.scene.control.Tooltips;
import de.carne.jfx.util.validation.ValidationAlerts;
import de.carne.jfx.util.validation.ValidationException;
import de.carne.util.Exceptions;
import de.carne.util.Strings;
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

	private final ListViewEditor<Rdn> rdnEntriesEditor = new ListViewEditor<Rdn>() {

		@Override
		@Nullable
		protected Rdn getInput() {
			return getRdnInput();
		}

		@Override
		protected void setInput(@Nullable Rdn input) {
			setRdnInput(input);
		}

	};

	@Nullable
	private X500Principal dnResult = null;

	@SuppressWarnings("null")
	@FXML
	ComboBox<String> ctlTypeInput;

	@SuppressWarnings("null")
	@FXML
	TextField ctlValueInput;

	@SuppressWarnings("null")
	@FXML
	Button cmdAddRdn;

	@SuppressWarnings("null")
	@FXML
	Button cmdApplyRdn;

	@SuppressWarnings("null")
	@FXML
	Button cmdDeleteRdn;

	@SuppressWarnings("null")
	@FXML
	Button cmdMoveRdnUp;

	@SuppressWarnings("null")
	@FXML
	Button cmdMoveRdnDown;

	@SuppressWarnings("null")
	@FXML
	ListView<Rdn> ctlRdnEntries;

	@FXML
	void onAddRdn(ActionEvent evt) {
		this.rdnEntriesEditor.onAddAction(evt);
	}

	@Nullable
	Rdn getRdnInput() {
		String typeInput = Strings.safeTrim(this.ctlTypeInput.getValue());
		String valueInput = Strings.safeTrim(this.ctlValueInput.getText());
		Rdn rdn = null;

		if (Strings.isEmpty(typeInput)) {
			Tooltips.show(this.ctlTypeInput, DNEditorI18N.formatSTR_MESSAGE_NO_TYPE(), Images.WARNING16);
		} else if (Strings.isEmpty(valueInput)) {
			Tooltips.show(this.ctlValueInput, DNEditorI18N.formatSTR_MESSAGE_NO_VALUE(), Images.WARNING16);
		} else {
			try {
				rdn = new Rdn(typeInput, valueInput);
			} catch (InvalidNameException e) {
				Tooltips.show(this.ctlValueInput, DNEditorI18N.formatSTR_MESSAGE_INVALID_RDN(e.getLocalizedMessage()),
						Images.WARNING16);
			}
		}
		return rdn;
	}

	void setRdnInput(@Nullable Rdn rdn) {
		if (rdn != null) {
			this.ctlTypeInput.setValue(rdn.getType());
			this.ctlValueInput.setText(rdn.getValue().toString());
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
		this.rdnEntriesEditor.init(this.ctlRdnEntries).setAddCommand(this.cmdAddRdn).setApplyCommand(this.cmdApplyRdn)
				.setDeleteCommand(this.cmdDeleteRdn).setMoveUpCommand(this.cmdMoveRdnUp)
				.setMoveDownCommand(this.cmdMoveRdnDown);
		this.ctlTypeInput.getItems().addAll(X500Names.rdnTypes());
		this.ctlTypeInput.getItems().sort((o1, o2) -> o1.compareTo(o2));
		this.ctlTypeInput.requestFocus();
		addButtonEventFilter(ButtonType.APPLY, this::onApply);
	}

	/**
	 * Initialize the DN editor's content.
	 *
	 * @param dnInput The current DN input.
	 * @return This controller.
	 */
	public DNEditorController init(String dnInput) {
		try {
			LdapName dn = new LdapName(dnInput);

			this.ctlRdnEntries.getItems().addAll(dn.getRdns());
		} catch (InvalidNameException e) {
			Exceptions.ignore(e);
		}
		return this;
	}

	private X500Principal validateAndGetDN() throws ValidationException {
		LdapName ldapDN = new LdapName(this.ctlRdnEntries.getItems());
		X500Principal x500DN;

		try {
			x500DN = X500Names.fromString(ldapDN.toString());
		} catch (IllegalArgumentException e) {
			throw new ValidationException(DNEditorI18N.formatSTR_MESSAGE_INVALID_DN(e.getLocalizedMessage()), e);
		}
		return x500DN;
	}

	@Override
	@Nullable
	public X500Principal call(@Nullable ButtonType param) {
		return this.dnResult;
	}

}
