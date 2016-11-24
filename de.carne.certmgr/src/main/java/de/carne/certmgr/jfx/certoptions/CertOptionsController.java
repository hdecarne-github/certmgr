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

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;
import java.util.prefs.Preferences;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.UserCertStoreEntryId;
import de.carne.certmgr.certs.UserCertStorePreferences;
import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.certs.signer.CertSigners;
import de.carne.certmgr.certs.signer.Issuer;
import de.carne.certmgr.certs.spi.CertSigner;
import de.carne.certmgr.certs.x509.BasicConstraintsExtensionData;
import de.carne.certmgr.certs.x509.CRLDistributionPointsExtensionData;
import de.carne.certmgr.certs.x509.ExtendedKeyUsageExtensionData;
import de.carne.certmgr.certs.x509.KeyUsageExtensionData;
import de.carne.certmgr.certs.x509.SubjectAlternativeNameExtensionData;
import de.carne.certmgr.certs.x509.X509ExtensionData;
import de.carne.certmgr.jfx.resources.Images;
import de.carne.certmgr.util.DefaultSet;
import de.carne.jfx.application.PlatformHelper;
import de.carne.jfx.scene.control.Alerts;
import de.carne.jfx.stage.StageController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

/**
 * Certificate (CRT/CSR) creation dialog.
 */
public class CertOptionsController extends StageController {

	private final Preferences preferences = Preferences.systemNodeForPackage(CertOptionsController.class);

	private UserCertStore store = null;

	private UserCertStorePreferences storePreferences = null;

	private UserCertStoreEntry storeEntry = null;

	private boolean expertMode = false;

	private final ObjectProperty<BasicConstraintsExtensionData> basicConstraintsExtension = new SimpleObjectProperty<>(
			null);

	private final ObjectProperty<KeyUsageExtensionData> keyUsageExtension = new SimpleObjectProperty<>(null);

	private final ObjectProperty<ExtendedKeyUsageExtensionData> extendedKeyUsageExtension = new SimpleObjectProperty<>(
			null);

	private final ObjectProperty<SubjectAlternativeNameExtensionData> subjectAlternativeExtension = new SimpleObjectProperty<>(
			null);

	private final ObjectProperty<CRLDistributionPointsExtensionData> crlDistributionPointsExtension = new SimpleObjectProperty<>(
			null);

	@FXML
	Menu ctlStorePresetsMenu;

	@FXML
	Menu ctlTemplatePresetsMenu;

	@FXML
	TextField ctlAliasInput;

	@FXML
	TextField ctlDNInput;

	@FXML
	ComboBox<KeyPairAlgorithm> ctlKeyAlgOption;

	@FXML
	ComboBox<Integer> ctlKeySizeOption;

	@FXML
	ChoiceBox<CertSigner> ctlSignerOption;

	@FXML
	ComboBox<SignatureAlgorithm> ctlSigAlgOption;

	@FXML
	DatePicker ctlNotBeforeInput;

	@FXML
	DatePicker ctlNotAfterInput;

	@FXML
	ComboBox<Issuer> ctlIssuerInput;

	@FXML
	MenuItem cmdAddBasicConstraints;

	@FXML
	MenuItem cmdAddKeyUsage;

	@FXML
	MenuItem cmdAddExtendedKeyUsage;

	@FXML
	MenuItem cmdAddSubjectAlternativeName;

	@FXML
	MenuItem cmdAddCRLDistributionPoints;

	@FXML
	MenuItem cmdAddCustomExtension;

	@FXML
	Button cmdEditExtension;

	@FXML
	Button cmdDeleteExtension;

	@FXML
	TableView<ExtensionDataModel> ctlExtensionData;

	@FXML
	TableColumn<ExtensionDataModel, Boolean> ctlExtensionDataCritical;

	@FXML
	TableColumn<ExtensionDataModel, String> ctlExtensionDataName;

	@FXML
	TableColumn<ExtensionDataModel, String> ctlExtensionDataValue;

	@FXML
	void onCmdAddBasicConstraints(ActionEvent evt) {
		try {
			BasicConstraintsController extensionDialog = BasicConstraintsDialog.load(this);
			BasicConstraintsExtensionData extensionData = this.basicConstraintsExtension.get();

			if (extensionData != null) {
				extensionDialog.init(extensionData, this.expertMode);
			} else {
				extensionDialog.init(this.expertMode);
			}

			Optional<BasicConstraintsExtensionData> dialogResult = extensionDialog.showAndWait();

			if (dialogResult.isPresent()) {
				extensionData = dialogResult.get();
				setExtensionData(extensionData);
				this.basicConstraintsExtension.set(extensionData);
			}
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@FXML
	void onCmdAddKeyUsage(ActionEvent evt) {

	}

	@FXML
	void onCmdAddExtendedKeyUsage(ActionEvent evt) {

	}

	@FXML
	void onCmdAddSubjectAlternativeName(ActionEvent evt) {

	}

	@FXML
	void onCmdAddCRLDistributionPoints(ActionEvent evt) {

	}

	@FXML
	void onCmdAddCustomExtension(ActionEvent evt) {

	}

	@FXML
	void onCmdEditExtension(ActionEvent evt) {

	}

	@FXML
	void onCmdDeleteExtension(ActionEvent evt) {

	}

	@FXML
	void onCmdSubmit(ActionEvent evt) {

	}

	@FXML
	void onCmdCancel(ActionEvent evt) {
		close(false);
	}

	private void onKeyAlgChanged(KeyPairAlgorithm keyAlg) {
		DefaultSet<Integer> keySizes = null;

		if (keyAlg != null) {
			Integer defaultHint = null;

			if (keyAlg.algorithm().equals(this.storePreferences.defaultKeyPairAlgorithm.get())) {
				defaultHint = this.storePreferences.defaultKeySize.get();
			}
			keySizes = keyAlg.getStandardKeySizes(defaultHint);
		}
		resetComboBoxOptions(this.ctlKeySizeOption, keySizes, (o1, o2) -> o1.compareTo(o2));
		resetSigAlgOptions(keyAlg);
	}

	private void onSignerChanged(CertSigner newSigner) {
		DefaultSet<Issuer> issuers = (newSigner != null ? newSigner.getIssuers(this.store, this.storeEntry) : null);

		resetComboBoxOptions(this.ctlIssuerInput, issuers, (o1, o2) -> o1.compareTo(o2));
		resetSigAlgOptions(newSigner);
		resetValidityInput(newSigner);
	}

	private void onIssuerChanged(Issuer newIssuer) {
		resetSigAlgOptions(newIssuer);
	}

	@Override
	protected void setupStage(Stage stage) {
		stage.getIcons().addAll(PlatformHelper.stageIcons(Images.NEWCERT32, Images.NEWCERT16));
		stage.setTitle(CertOptionsI18N.formatSTR_STAGE_TITLE());
		this.ctlKeyAlgOption.valueProperty().addListener((p, o, n) -> onKeyAlgChanged(n));
		this.ctlKeySizeOption.setConverter(new IntegerStringConverter());
		this.ctlSignerOption.valueProperty().addListener((p, o, n) -> onSignerChanged(n));
		this.ctlIssuerInput.valueProperty().addListener((p, o, n) -> onIssuerChanged(n));
		this.cmdAddBasicConstraints.disableProperty().bind(this.basicConstraintsExtension.isNotNull());
		this.cmdAddKeyUsage.disableProperty().bind(this.keyUsageExtension.isNotNull());
		this.cmdAddExtendedKeyUsage.disableProperty().bind(this.extendedKeyUsageExtension.isNotNull());
		this.cmdAddSubjectAlternativeName.disableProperty().bind(this.subjectAlternativeExtension.isNotNull());
		this.cmdAddCRLDistributionPoints.disableProperty().bind(this.crlDistributionPointsExtension.isNotNull());
		this.cmdEditExtension.disableProperty()
				.bind(this.ctlExtensionData.getSelectionModel().selectedItemProperty().isNull());
		this.cmdDeleteExtension.disableProperty()
				.bind(this.ctlExtensionData.getSelectionModel().selectedItemProperty().isNull());
		this.ctlExtensionDataCritical.setCellFactory(CheckBoxTableCell.forTableColumn(this.ctlExtensionDataCritical));
		this.ctlExtensionDataCritical.setCellValueFactory(new PropertyValueFactory<>("critical"));
		this.ctlExtensionDataName.setCellValueFactory(new PropertyValueFactory<>("name"));
		this.ctlExtensionDataValue.setCellValueFactory(new PropertyValueFactory<>("value"));
	}

	/**
	 * Initialize dialog for certificate generation.
	 *
	 * @param storeParam The store to add the generated certificate to.
	 * @param storeEntryParam The (optional) store entry to use for certificate
	 *        signing.
	 * @param expertModeParam Whether to run in expert mode ({@code true}) or
	 *        not ({@code false}).
	 * @return This controller.
	 */
	public CertOptionsController init(UserCertStore storeParam, UserCertStoreEntry storeEntryParam,
			boolean expertModeParam) {
		this.store = storeParam;
		this.storePreferences = this.store.storePreferences();
		this.storeEntry = storeEntryParam;
		this.expertMode = expertModeParam;
		initExpertMode();
		initCertificateNames();
		initKeyAlgOptions();
		initSignerOptions();
		return this;
	}

	private void initExpertMode() {
		this.ctlKeySizeOption.setEditable(this.expertMode);
	}

	private void initCertificateNames() {
		UserCertStoreEntryId entryId = this.store.generateEntryId(CertOptionsI18N.formatSTR_TEXT_ALIASHINT());

		this.ctlAliasInput.setText(entryId.getAlias());
		this.ctlDNInput.setText(CertOptionsI18N.formatSTR_TEXT_DEFAULTDN(entryId.getAlias(), this.store.storeName()));
	}

	private void initKeyAlgOptions() {
		resetComboBoxOptions(this.ctlKeyAlgOption,
				KeyPairAlgorithm.getDefaultSet(this.storePreferences.defaultKeyPairAlgorithm.get(), this.expertMode),
				(o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	private void initSignerOptions() {
		ObservableList<CertSigner> signerOptions = this.ctlSignerOption.getItems();

		signerOptions.clear();
		signerOptions.addAll(CertSigners.REGISTERED.providers());
		signerOptions.sort((o1, o2) -> o1.toString().compareTo(o2.toString()));
		this.ctlSignerOption.setValue(CertSigners.DEFAULT);
	}

	private void resetSigAlgOptions(CertSigner signer) {
		KeyPairAlgorithm keyPairAlgorithm = this.ctlKeyAlgOption.getValue();
		Issuer issuer = this.ctlIssuerInput.getValue();

		resetSigAlgOptions(signer, keyPairAlgorithm, issuer);
	}

	private void resetSigAlgOptions(KeyPairAlgorithm keyPairAlgorithm) {
		CertSigner signer = this.ctlSignerOption.getValue();
		Issuer issuer = this.ctlIssuerInput.getValue();

		resetSigAlgOptions(signer, keyPairAlgorithm, issuer);
	}

	private void resetSigAlgOptions(Issuer issuer) {
		CertSigner signer = this.ctlSignerOption.getValue();
		KeyPairAlgorithm keyPairAlgorithm = this.ctlKeyAlgOption.getValue();

		resetSigAlgOptions(signer, keyPairAlgorithm, issuer);
	}

	private void resetSigAlgOptions(CertSigner signer, KeyPairAlgorithm keyPairAlgorithm, Issuer issuer) {
		DefaultSet<SignatureAlgorithm> sigAlgs = null;

		if (signer != null) {
			String keyPairAlgorithmName = null;
			String defaultHint = null;

			if (keyPairAlgorithm != null) {
				keyPairAlgorithmName = keyPairAlgorithm.algorithm();
				if (keyPairAlgorithmName.equals(this.storePreferences.defaultKeyPairAlgorithm.get())) {
					defaultHint = this.storePreferences.defaultSignatureAlgorithm.get();
				}
			}
			sigAlgs = signer.getSignatureAlgorithms(issuer, keyPairAlgorithmName, defaultHint, this.expertMode);
		}
		resetComboBoxOptions(this.ctlSigAlgOption, sigAlgs, (o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	private void resetValidityInput(CertSigner signer) {
		if (signer != null && signer.hasFeature(CertSigner.Feature.CUSTOM_VALIDITY)) {
			LocalDate notBeforeValue = LocalDate.now();
			LocalDate notAfterValue = notBeforeValue.plus(this.storePreferences.defaultCRTValidityPeriod.get(),
					ChronoUnit.DAYS);

			this.ctlNotBeforeInput.setValue(notBeforeValue);
			this.ctlNotBeforeInput.setDisable(false);
			this.ctlNotAfterInput.setValue(notAfterValue);
			this.ctlNotAfterInput.setDisable(false);
		} else {
			this.ctlNotBeforeInput.setValue(null);
			this.ctlNotBeforeInput.setDisable(true);
			this.ctlNotAfterInput.setValue(null);
			this.ctlNotAfterInput.setDisable(true);
		}
	}

	@Override
	protected Preferences getPreferences() {
		return this.preferences;
	}

	private void setExtensionData(X509ExtensionData extensionData) {
		ObservableList<ExtensionDataModel> extensionDataItems = this.ctlExtensionData.getItems();
		ExtensionDataModel extensionDataModel = null;

		for (ExtensionDataModel extensionDataItem : extensionDataItems) {
			if (extensionData.getClass().equals(extensionDataItem.getExtensionData().getClass())) {
				extensionDataModel = extensionDataItem;
				break;
			}
		}
		if (extensionDataModel != null) {
			extensionDataModel.setExtensionData(extensionData);
		} else {
			extensionDataModel = new ExtensionDataModel(extensionData);
			extensionDataItems.add(extensionDataModel);
			extensionDataItems.sort((o1, o2) -> o1.getExtensionData().oid().compareTo(o2.getExtensionData().oid()));
		}
	}

	private static <T> void resetComboBoxOptions(ComboBox<T> control, DefaultSet<T> defaultSet,
			Comparator<T> comparator) {
		ObservableList<T> options = control.getItems();

		options.clear();
		if (defaultSet != null && !defaultSet.isEmpty()) {
			options.addAll(defaultSet);
			options.sort(comparator);
			control.setValue(defaultSet.getDefault());
			control.setDisable(false);
		} else {
			control.setDisable(!control.isEditable());
		}
	}

}
