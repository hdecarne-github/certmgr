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
package de.carne.certmgr.jfx.crloptions;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.time.LocalDate;
import java.util.Date;

import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.UserCertStorePreferences;
import de.carne.certmgr.certs.security.CRLUpdatePeriod;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.certs.x509.ReasonFlag;
import de.carne.jfx.stage.StageController;
import de.carne.jfx.util.Controls;
import de.carne.util.DefaultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * CRL options dialog.
 */
public class CRLOptionsController extends StageController {

	private UserCertStoreEntry issuerEntry = null;

	@FXML
	TextField ctlIssuerField;

	@FXML
	ComboBox<SignatureAlgorithm> ctlSigAlgOption;

	@FXML
	DatePicker ctlLastUpdateInput;

	@FXML
	DatePicker ctlNextUpdateInput;

	@FXML
	TableView<CRLEntryModel> ctlEntryOptions;

	@FXML
	TableColumn<CRLEntryModel, Boolean> ctlEntryOptionRevoked;

	@FXML
	TableColumn<CRLEntryModel, String> ctlEntryOptionName;

	@FXML
	TableColumn<CRLEntryModel, BigInteger> ctlEntryOptionSerial;

	@FXML
	TableColumn<CRLEntryModel, ReasonFlag> ctlEntryOptionReason;

	@FXML
	TableColumn<CRLEntryModel, Date> ctlEntryOptionDate;

	@FXML
	void onCmdUpdate(ActionEvent evt) {

	}

	@FXML
	void onCmdCancel(ActionEvent evt) {
		close(false);
	}

	@Override
	protected void setupStage(Stage stage) {
		stage.setTitle(CRLOptionsI18N.formatSTR_STAGE_TITLE());
		this.ctlEntryOptionRevoked.setCellFactory(CheckBoxTableCell.forTableColumn(this.ctlEntryOptionRevoked));
		this.ctlEntryOptionRevoked.setCellValueFactory(new PropertyValueFactory<>("revoked"));
		this.ctlEntryOptionName.setCellValueFactory(new PropertyValueFactory<>("name"));
		this.ctlEntryOptionSerial.setCellValueFactory(new PropertyValueFactory<>("serial"));

		ObservableList<ReasonFlag> reasons = FXCollections.observableArrayList(ReasonFlag.instances());

		reasons.sort((o1, o2) -> o1.name().compareTo(o2.name()));
		this.ctlEntryOptionReason.setCellFactory(ChoiceBoxTableCell.forTableColumn(reasons));
		this.ctlEntryOptionReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
		this.ctlEntryOptionDate.setCellValueFactory(new PropertyValueFactory<>("date"));
	}

	/**
	 * Initialize the dialog for CRL options editing.
	 *
	 * @param issuerEntryParam The CRL issuer to edit the options for.
	 * @param expertModeParam Whether to run in expert mode ({@code true}) or
	 *        not ({@code false}).
	 * @return This controller.
	 * @throws IOException if an I/O error occurs during initialization.
	 */
	public CRLOptionsController init(UserCertStoreEntry issuerEntryParam, boolean expertModeParam) throws IOException {
		assert issuerEntryParam != null;

		this.issuerEntry = issuerEntryParam;
		this.ctlIssuerField.setText(this.issuerEntry.getName());

		UserCertStorePreferences preferences = this.issuerEntry.store().storePreferences();

		initSigAlgOptions(preferences, expertModeParam);
		initUpdateOptions(preferences);
		initEntries();
		return this;
	}

	private void initSigAlgOptions(UserCertStorePreferences preferences, boolean expertMode) throws IOException {
		String keyAlgName = this.issuerEntry.getCRT().getPublicKey().getAlgorithm();
		DefaultSet<SignatureAlgorithm> keyAlgs = SignatureAlgorithm.getDefaultSet(keyAlgName,
				preferences.defaultSignatureAlgorithm.get(), expertMode);

		Controls.resetComboBoxOptions(this.ctlSigAlgOption, keyAlgs,
				(o1, o2) -> o1.algorithm().compareTo(o2.algorithm()));
	}

	private void initUpdateOptions(UserCertStorePreferences preferences) {
		LocalDate lastUpdate = LocalDate.now();

		this.ctlLastUpdateInput.setValue(lastUpdate);

		CRLUpdatePeriod defaultUpdatePeriod = CRLUpdatePeriod.getDefaultSet(null).getDefault();
		LocalDate nextUpdate = lastUpdate
				.plusDays(preferences.defaultCRLUpdatePeriod.getInt(defaultUpdatePeriod.days().count()));

		this.ctlNextUpdateInput.setValue(nextUpdate);
	}

	private void initEntries() throws IOException {
		X509CRL crl = this.issuerEntry.getCRL();
		ObservableList<CRLEntryModel> entryItems = this.ctlEntryOptions.getItems();

		for (UserCertStoreEntry issuedEntry : this.issuerEntry.issuedEntries()) {
			BigInteger issuedSerial = issuedEntry.getCRT().getSerialNumber();
			boolean revoked = false;
			ReasonFlag reason = ReasonFlag.UNUSED;
			Date date = null;

			if (crl != null) {
				X509CRLEntry crlEntry = crl.getRevokedCertificate(issuedSerial);

				if (crlEntry != null) {
					revoked = true;
					reason = ReasonFlag.fromCRLReason(crlEntry.getRevocationReason());
					date = crlEntry.getRevocationDate();
				}
			}
			entryItems.add(new CRLEntryModel(issuedEntry, revoked, issuedSerial, reason, date));
		}
		entryItems.sort((o1, o2) -> o1.compareTo(o2));
	}

}
