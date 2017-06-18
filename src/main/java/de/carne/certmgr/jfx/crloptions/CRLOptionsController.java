/*
 * Copyright (c) 2015-2017 Holger de Carne and contributors, All Rights Reserved.
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
import java.security.cert.CRLReason;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.UserCertStorePreferences;
import de.carne.certmgr.certs.security.CRLUpdatePeriod;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.certs.x509.ReasonFlag;
import de.carne.certmgr.certs.x509.UpdateCRLRequest;
import de.carne.certmgr.jfx.password.PasswordDialog;
import de.carne.jfx.scene.control.Alerts;
import de.carne.jfx.scene.control.Controls;
import de.carne.jfx.stage.StageController;
import de.carne.jfx.util.validation.ValidationAlerts;
import de.carne.util.DefaultSet;
import de.carne.util.validation.InputValidator;
import de.carne.util.validation.ValidationException;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * CRL options dialog.
 */
public class CRLOptionsController extends StageController {

	private UserCertStoreEntry issuerEntry = null;

	@SuppressWarnings("null")
	@FXML
	GridPane ctlControlPane;

	@SuppressWarnings("null")
	@FXML
	VBox ctlProgressOverlay;

	@SuppressWarnings("null")
	@FXML
	TextField ctlIssuerField;

	@SuppressWarnings("null")
	@FXML
	ComboBox<SignatureAlgorithm> ctlSigAlgOption;

	@SuppressWarnings("null")
	@FXML
	DatePicker ctlLastUpdateInput;

	@SuppressWarnings("null")
	@FXML
	DatePicker ctlNextUpdateInput;

	@SuppressWarnings("null")
	@FXML
	TableView<CRLEntryModel> ctlEntryOptions;

	@SuppressWarnings("null")
	@FXML
	TableColumn<CRLEntryModel, Boolean> ctlEntryOptionRevoked;

	@SuppressWarnings("null")
	@FXML
	TableColumn<CRLEntryModel, String> ctlEntryOptionName;

	@SuppressWarnings("null")
	@FXML
	TableColumn<CRLEntryModel, BigInteger> ctlEntryOptionSerial;

	@SuppressWarnings("null")
	@FXML
	TableColumn<CRLEntryModel, ReasonFlag> ctlEntryOptionReason;

	@SuppressWarnings("null")
	@FXML
	TableColumn<CRLEntryModel, Date> ctlEntryOptionDate;

	@SuppressWarnings("unused")
	@FXML
	void onCmdUpdate(ActionEvent evt) {
		try {
			UpdateCRLRequest updateRequest = validateAndGetUpdateRequest();

			getExecutorService().submit(new UpdateCRLTask(updateRequest));
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
		}
	}

	@SuppressWarnings("unused")
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
	 * @param expertModeParam Whether to run in expert mode ({@code true}) or not ({@code false}).
	 * @return This controller.
	 * @throws IOException if an I/O error occurs during initialization.
	 */
	public CRLOptionsController init(UserCertStoreEntry issuerEntryParam, boolean expertModeParam) throws IOException {
		this.issuerEntry = issuerEntryParam;
		this.ctlIssuerField.setText(this.issuerEntry.getName());

		UserCertStorePreferences preferences = this.issuerEntry.store().storePreferences();

		initSigAlgOptions(preferences, expertModeParam);
		initUpdateOptions(preferences);
		initEntries();
		return this;
	}

	/**
	 * Mark a store entry as revoked.
	 *
	 * @param storeEntry The store entry to mark.
	 * @param reason The revoke reason to set.
	 */
	public void revokeStoreEntry(UserCertStoreEntry storeEntry, ReasonFlag reason) {
		for (CRLEntryModel entryItem : this.ctlEntryOptions.getItems()) {
			if (entryItem.getStoreEntry().equals(storeEntry)) {
				entryItem.setRevoked(true);
				entryItem.setReason(reason);
			}
		}
	}

	private void initSigAlgOptions(UserCertStorePreferences preferences, boolean expertMode) throws IOException {
		String keyAlgName = this.issuerEntry.getPublicKey().getAlgorithm();
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
		ObservableList<CRLEntryModel> entryItems = this.ctlEntryOptions.getItems();

		for (UserCertStoreEntry issuedEntry : this.issuerEntry.issuedEntries()) {
			BigInteger issuedSerial = issuedEntry.getCRT().getSerialNumber();
			boolean revoked = false;
			ReasonFlag reason = ReasonFlag.UNSPECIFIED;
			Date date = null;

			if (this.issuerEntry.hasCRL()) {
				X509CRL crl = this.issuerEntry.getCRL();
				X509CRLEntry crlEntry = crl.getRevokedCertificate(issuedSerial);

				if (crlEntry != null) {
					revoked = true;

					CRLReason crlEntryReason = crlEntry.getRevocationReason();

					if (crlEntryReason != null) {
						reason = ReasonFlag.fromCRLReason(crlEntryReason);
					}
					date = crlEntry.getRevocationDate();
				}
			}
			entryItems.add(new CRLEntryModel(issuedEntry, revoked, issuedSerial, reason, date));
		}
		entryItems.sort((o1, o2) -> o1.compareTo(o2));
	}

	@Override
	protected void setBlocked(boolean blocked) {
		this.ctlControlPane.setDisable(blocked);
		this.ctlProgressOverlay.setVisible(blocked);
	}

	private UpdateCRLRequest validateAndGetUpdateRequest() throws ValidationException {
		Date lastUpdate = validateAndGetLastUpdate();
		Date nextUpdate = validateAndGetNextUpdate(lastUpdate);
		SignatureAlgorithm sigAlg = validateAndGetSigAlg();
		UpdateCRLRequest updateRequest = new UpdateCRLRequest(lastUpdate, nextUpdate, sigAlg);

		for (CRLEntryModel entryItem : this.ctlEntryOptions.getItems()) {
			if (entryItem.getRevoked()) {
				updateRequest.addRevokeEntry(entryItem.getSerial(), entryItem.getReason());
			}
		}
		return updateRequest;
	}

	private Date validateAndGetLastUpdate() throws ValidationException {
		LocalDate localLastUpdate = InputValidator.notNull(this.ctlLastUpdateInput.getValue(),
				CRLOptionsI18N::formatSTR_MESSAGE_NO_LASTUPDATE);

		return Date.from(localLastUpdate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	private Date validateAndGetNextUpdate(Date lastUpdate) throws ValidationException {
		LocalDate localNextUpdate = this.ctlNextUpdateInput.getValue();
		Date nextUpdate = (localNextUpdate != null
				? Date.from(localNextUpdate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) : null);

		if (nextUpdate != null) {
			InputValidator.isTrue(nextUpdate.compareTo(lastUpdate) > 0,
					(a) -> CRLOptionsI18N.formatSTR_MESSAGE_INVALID_UPDATEDATES(lastUpdate, nextUpdate));
		}
		return nextUpdate;
	}

	private SignatureAlgorithm validateAndGetSigAlg() throws ValidationException {
		return InputValidator.notNull(this.ctlSigAlgOption.getValue(), CRLOptionsI18N::formatSTR_MESSAGE_NO_SIGALG);
	}

	void updateCRL(UpdateCRLRequest updateRequest) throws IOException {
		this.issuerEntry.updateCRL(updateRequest, PasswordDialog.enterPassword(this));
	}

	private class UpdateCRLTask extends BackgroundTask<Void> {

		private final UpdateCRLRequest updateRequest;

		UpdateCRLTask(UpdateCRLRequest updateRequest) {
			this.updateRequest = updateRequest;
		}

		@Override
		protected Void call() throws Exception {
			updateCRL(this.updateRequest);
			return null;
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			close(true);
		}

		@Override
		protected void failed() {
			super.failed();
			Alerts.unexpected(getException()).showAndWait();
		}

	}

}
