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
package de.carne.certmgr.jfx.crloptions;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509CRLEntry;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.carne.certmgr.jfx.CertStoreEntryOption;
import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.jfx.InputValidator;
import de.carne.certmgr.jfx.InvalidInputException;
import de.carne.certmgr.jfx.StoreOptions;
import de.carne.certmgr.jfx.help.Help;
import de.carne.certmgr.jfx.help.HelpController;
import de.carne.certmgr.jfx.passwordprompt.PasswordPromptCallback;
import de.carne.certmgr.store.CertStore;
import de.carne.certmgr.store.CertStoreEntry;
import de.carne.certmgr.store.x509.RevokeReason;
import de.carne.certmgr.store.x509.X509CRLParams;
import de.carne.jfx.StageController;
import de.carne.jfx.messagebox.MessageBoxStyle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Dialog controller for CRL editing.
 */
public class CRLOptionsController extends StageController {

	/**
	 * Controller's callback interface.
	 */
	public interface Result {

		/**
		 * Called when the user applies a CRL update and the selected
		 * certificate store entry was updated.
		 *
		 * @param entryParam The updated certificate store entry.
		 */
		public void onEntryUpdate(CertStoreEntry entryParam);

	}

	private Result result = null;
	private CertStore store = null;
	private StoreOptions storeOptions = new StoreOptions();

	@FXML
	ComboBox<CertStoreEntryOption> ctlIssuerEntrySelection;

	@FXML
	ComboBox<String> ctlSigAlgSelection;

	@FXML
	DatePicker ctlLastUpdateInput;

	@FXML
	DatePicker ctlNextUpdateInput;

	@FXML
	TableView<CRLEntryModel> ctlCRLEntriesInput;

	@FXML
	TableColumn<CRLEntryModel, Boolean> ctlCRLEntriesInputRevoked;

	@FXML
	TableColumn<CRLEntryModel, String> ctlCRLEntriesInputEntry;

	@FXML
	TableColumn<CRLEntryModel, RevokeReason> ctlCRLEntriesInputReason;

	@FXML
	void onSave(ActionEvent evt) {
		try {
			CertStoreEntry issuerEntry = validateAndGetIssuerEntry();
			X509CRLParams crlParams = validateAndGetCRLParams();
			Map<CertStoreEntry, RevokeReason> revokeList = validateAndGetRevokeList();
			PasswordPromptCallback password = PasswordPromptCallback.getPassword(this);

			this.store.generateAndSignCRL(issuerEntry, crlParams, revokeList, password, false);
			getStage().close();
			this.result.onEntryUpdate(issuerEntry);
		} catch (InvalidInputException e) {
			showMessageBox(e.getLocalizedMessage(), null, MessageBoxStyle.ICON_ERROR, MessageBoxStyle.BUTTON_OK);
		} catch (IOException | GeneralSecurityException e) {
			showMessageBox(I18N.formatSTR_GENERATE_ERROR_MESSAGE(), e, MessageBoxStyle.ICON_ERROR,
					MessageBoxStyle.BUTTON_OK);
		}
	}

	@FXML
	void onCancel(ActionEvent evt) {
		getStage().close();
	}

	@FXML
	void onHelp(ActionEvent evt) {
		try {
			HelpController.showHelp(this, Help.TOPIC_CRL_OPTIONS);
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	void onIssuerEntryChanged(CertStoreEntryOption issuerEntryOption) {
		CertStoreEntry issuerEntry = (issuerEntryOption != null ? issuerEntryOption.getEntry() : null);

		this.ctlSigAlgSelection.getItems().clear();
		this.ctlCRLEntriesInput.getItems().clear();

		if (issuerEntry != null) {
			try {
				String keyAlg = issuerEntry.getCRT().getObject().getPublicKey().getAlgorithm();

				this.ctlSigAlgSelection.getItems().addAll(CertStore.getSigAlgs(keyAlg));
				if (keyAlg.equals(this.storeOptions.getDefKeyAlg())) {
					this.ctlSigAlgSelection.getSelectionModel().select(this.storeOptions.getDefSigAlg());
				} else {
					this.ctlSigAlgSelection.getSelectionModel().select(CertStore.getDefaultSigAlg(keyAlg));
				}
				this.ctlCRLEntriesInput.getItems().addAll(getCRLEntries(issuerEntry));
			} catch (IOException e) {
				reportUnexpectedException(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.setTitle(I18N.formatSTR_CRL_OPTIONS_TITLE());
		controllerStage.getIcons().addAll(Images.IMAGE_NEWCRL16, Images.IMAGE_NEWCRL32);
		this.ctlCRLEntriesInputRevoked.setCellValueFactory(new PropertyValueFactory<>("revoked"));
		this.ctlCRLEntriesInputRevoked.setCellFactory(CheckBoxTableCell.forTableColumn(this.ctlCRLEntriesInputRevoked));
		this.ctlCRLEntriesInputEntry.setCellValueFactory(new PropertyValueFactory<>("entryOption"));
		this.ctlCRLEntriesInputReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
		this.ctlCRLEntriesInputReason.setCellFactory(ChoiceBoxTableCell.forTableColumn(RevokeReason.sortedValues()));
	}

	/**
	 * Begin CRL option editing.
	 *
	 * @param entryParam The certificate store entry to edit the CRL options
	 *        for.
	 * @param callback The callback to report the result of the user actions.
	 */
	public void beginCRLOptions(CertStoreEntry entryParam, Result callback) {
		assert entryParam != null;
		assert callback != null;

		this.result = callback;
		this.store = entryParam.getStore();
		this.storeOptions.load(this.store);
		this.ctlIssuerEntrySelection.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<CertStoreEntryOption>() {

					@Override
					public void changed(ObservableValue<? extends CertStoreEntryOption> property,
							CertStoreEntryOption oldValue, CertStoreEntryOption newValue) {
						onIssuerEntryChanged(newValue);
					}

				});

		Collection<CertStoreEntryOption> issuerEntries = CertStoreEntryOption.fromStoreWithPredicate(this.store,
				e -> e.hasKey());

		this.ctlIssuerEntrySelection.getItems().addAll(issuerEntries);
		this.ctlIssuerEntrySelection.getSelectionModel()
				.select(CertStoreEntryOption.findOption(issuerEntries, entryParam));

		LocalDate lastUpdate = LocalDate.now();
		LocalDate nextUpdate = this.storeOptions.getDefCRLUpdate().plusLocalData(lastUpdate);

		this.ctlLastUpdateInput.setValue(lastUpdate);
		this.ctlNextUpdateInput.setValue(nextUpdate);
		this.ctlCRLEntriesInput.setPlaceholder(new ImageView(Images.IMAGE_UNKNOWN32));
		getStage().sizeToScene();
	}

	/**
	 * Revoke a specific certificate store entry.
	 *
	 * @param entry The certificate store entry to revoke.
	 * @param reason The revoke reason to use.
	 */
	public void revokeCert(CertStoreEntry entry, RevokeReason reason) {
		assert entry != null;
		assert reason != null;

		for (CRLEntryModel crlEntry : this.ctlCRLEntriesInput.getItems()) {
			if (crlEntry.getEntryOption().getEntry().equals(entry)) {
				crlEntry.setRevoked(true);
				crlEntry.setReason(reason);
				break;
			}
		}
	}

	private Collection<CRLEntryModel> getCRLEntries(CertStoreEntry issuerEntry) throws IOException {
		Collection<CertStoreEntry> crlEntries0;

		if (issuerEntry.isRoot()) {
			Collection<CertStoreEntry> issuedEntries = this.store.getIssuedEntries(issuerEntry);

			crlEntries0 = new ArrayList<>(issuedEntries.size() + 1);
			crlEntries0.add(issuerEntry);
			crlEntries0.addAll(issuedEntries);
		} else {
			crlEntries0 = this.store.getIssuedEntries(issuerEntry);
		}

		ArrayList<CRLEntryModel> crlEntries = new ArrayList<>();

		for (CertStoreEntry crlEntry0 : crlEntries0) {
			if (crlEntry0.hasCRT()) {
				boolean revoked = false;
				RevokeReason reason = RevokeReason.UNSPECIFIED;

				if (issuerEntry.hasCRL()) {
					X509CRLEntry crlEntryInfo = issuerEntry.getCRL().getObject()
							.getRevokedCertificate(crlEntry0.getCRT().getObject());

					if (crlEntryInfo != null) {
						revoked = true;
						reason = RevokeReason.valueOf(crlEntryInfo.getRevocationReason().ordinal());
					}
				}
				crlEntries.add(new CRLEntryModel(revoked, crlEntry0, reason));
			}
		}
		return crlEntries;
	}

	private CertStoreEntry validateAndGetIssuerEntry() throws InvalidInputException {
		CertStoreEntryOption issuerEntryInput = InputValidator.notNull(I18N.BUNDLE, I18N.STR_NO_ISSUER_ENTRY_MESSAGE,
				this.ctlIssuerEntrySelection.getValue());

		return issuerEntryInput.getEntry();
	}

	private X509CRLParams validateAndGetCRLParams() throws InvalidInputException {
		String sigAlg = InputValidator.notNull(I18N.BUNDLE, I18N.STR_NO_SIG_ALG_MESSAGE,
				this.ctlSigAlgSelection.getValue());
		LocalDate lastUpdate = InputValidator.notNull(I18N.BUNDLE, I18N.STR_NO_LAST_UPDATE_MESSAGE,
				this.ctlLastUpdateInput.getValue());
		LocalDate nextUpdate = InputValidator.notNull(I18N.BUNDLE, I18N.STR_NO_NEXT_UPDATE_MESSAGE,
				this.ctlNextUpdateInput.getValue());

		InputValidator.isTrue(I18N.BUNDLE, I18N.STR_INVALID_NEXT_UPDATE_MESSAGE, nextUpdate.isAfter(lastUpdate),
				lastUpdate, nextUpdate);

		X509CRLParams crlParams = new X509CRLParams(sigAlg, lastUpdate, nextUpdate);

		return crlParams;
	}

	private Map<CertStoreEntry, RevokeReason> validateAndGetRevokeList() {
		HashMap<CertStoreEntry, RevokeReason> revokeList = new HashMap<>();

		for (CRLEntryModel crlEntry : this.ctlCRLEntriesInput.getItems()) {
			if (crlEntry.isRevoked()) {
				revokeList.put(crlEntry.getEntryOption().getEntry(), crlEntry.getReason());
			}
		}
		return revokeList;
	}

}
