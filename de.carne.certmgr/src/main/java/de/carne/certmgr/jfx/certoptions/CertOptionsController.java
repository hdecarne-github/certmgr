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
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.prefs.Preferences;

import javax.security.auth.x500.X500Principal;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.UserCertStoreEntryId;
import de.carne.certmgr.certs.UserCertStorePreferences;
import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.security.SignatureAlgorithm;
import de.carne.certmgr.certs.spi.CertGenerator;
import de.carne.certmgr.certs.x500.X500Names;
import de.carne.certmgr.certs.x509.BasicConstraintsExtensionData;
import de.carne.certmgr.certs.x509.CRLDistributionPointsExtensionData;
import de.carne.certmgr.certs.x509.ExtendedKeyUsageExtensionData;
import de.carne.certmgr.certs.x509.GenerateCertRequest;
import de.carne.certmgr.certs.x509.KeyUsageExtensionData;
import de.carne.certmgr.certs.x509.SubjectAlternativeNameExtensionData;
import de.carne.certmgr.certs.x509.X509ExtensionData;
import de.carne.certmgr.certs.x509.generator.CertGenerators;
import de.carne.certmgr.certs.x509.generator.Issuer;
import de.carne.certmgr.jfx.password.PasswordDialog;
import de.carne.certmgr.jfx.resources.Images;
import de.carne.jfx.application.PlatformHelper;
import de.carne.jfx.scene.control.Alerts;
import de.carne.jfx.stage.StageController;
import de.carne.jfx.util.Controls;
import de.carne.jfx.util.validation.ValidationAlerts;
import de.carne.util.DefaultSet;
import de.carne.util.Strings;
import de.carne.util.validation.InputValidator;
import de.carne.util.validation.ValidationException;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
	GridPane ctlControlPane;

	@FXML
	VBox ctlProgressOverlay;

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
	ChoiceBox<CertGenerator> ctlGeneratorOption;

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
	void onCmdEditBasicConstraints(ActionEvent evt) {
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
	void onCmdEditKeyUsage(ActionEvent evt) {
		try {
			KeyUsageController extensionDialog = KeyUsageDialog.load(this);
			KeyUsageExtensionData extensionData = this.keyUsageExtension.get();

			if (extensionData != null) {
				extensionDialog.init(extensionData, this.expertMode);
			} else {
				extensionDialog.init(this.expertMode);
			}

			Optional<KeyUsageExtensionData> dialogResult = extensionDialog.showAndWait();

			if (dialogResult.isPresent()) {
				extensionData = dialogResult.get();
				setExtensionData(extensionData);
				this.keyUsageExtension.set(extensionData);
			}
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@FXML
	void onCmdEditExtendedKeyUsage(ActionEvent evt) {
		try {
			ExtendedKeyUsageController extensionDialog = ExtendedKeyUsageDialog.load(this);
			ExtendedKeyUsageExtensionData extensionData = this.extendedKeyUsageExtension.get();

			if (extensionData != null) {
				extensionDialog.init(extensionData, this.expertMode);
			} else {
				extensionDialog.init(this.expertMode);
			}

			Optional<ExtendedKeyUsageExtensionData> dialogResult = extensionDialog.showAndWait();

			if (dialogResult.isPresent()) {
				extensionData = dialogResult.get();
				setExtensionData(extensionData);
				this.extendedKeyUsageExtension.set(extensionData);
			}
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@FXML
	void onCmdEditSubjectAlternativeName(ActionEvent evt) {
		try {
			SubjectAlternativeNameController extensionDialog = SubjectAlternativeNameDialog.load(this);
			SubjectAlternativeNameExtensionData extensionData = this.subjectAlternativeExtension.get();

			if (extensionData != null) {
				extensionDialog.init(extensionData, this.expertMode);
			} else {
				extensionDialog.init(this.expertMode);
			}

			Optional<SubjectAlternativeNameExtensionData> dialogResult = extensionDialog.showAndWait();

			if (dialogResult.isPresent()) {
				extensionData = dialogResult.get();
				setExtensionData(extensionData);
				this.subjectAlternativeExtension.set(extensionData);
			}
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@FXML
	void onCmdEditCRLDistributionPoints(ActionEvent evt) {
		try {
			CRLDistributionPointsController extensionDialog = CRLDistributionPointsDialog.load(this);
			CRLDistributionPointsExtensionData extensionData = this.crlDistributionPointsExtension.get();

			if (extensionData != null) {
				extensionDialog.init(extensionData, this.expertMode);
			} else {
				extensionDialog.init(this.expertMode);
			}

			Optional<CRLDistributionPointsExtensionData> dialogResult = extensionDialog.showAndWait();

			if (dialogResult.isPresent()) {
				extensionData = dialogResult.get();
				setExtensionData(extensionData);
				this.crlDistributionPointsExtension.set(extensionData);
			}
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@FXML
	void onCmdAddCustomExtension(ActionEvent evt) {

	}

	@FXML
	void onCmdEditExtension(ActionEvent evt) {
		ExtensionDataModel extensionDataItem = this.ctlExtensionData.getSelectionModel().getSelectedItem();

		if (extensionDataItem != null) {
			X509ExtensionData extensionData = extensionDataItem.getExtensionData();

			if (extensionData instanceof BasicConstraintsExtensionData) {
				onCmdEditBasicConstraints(evt);
			} else if (extensionData instanceof KeyUsageExtensionData) {
				onCmdEditKeyUsage(evt);
			} else if (extensionData instanceof ExtendedKeyUsageExtensionData) {
				onCmdEditExtendedKeyUsage(evt);
			} else if (extensionData instanceof SubjectAlternativeNameExtensionData) {
				onCmdEditSubjectAlternativeName(evt);
			} else if (extensionData instanceof CRLDistributionPointsExtensionData) {
				onCmdEditCRLDistributionPoints(evt);
			}
		}
	}

	@FXML
	void onCmdDeleteExtension(ActionEvent evt) {
		ExtensionDataModel extensionDataItem = this.ctlExtensionData.getSelectionModel().getSelectedItem();

		if (extensionDataItem != null) {
			X509ExtensionData extensionData = extensionDataItem.getExtensionData();

			if (extensionData instanceof BasicConstraintsExtensionData) {
				this.basicConstraintsExtension.set(null);
			} else if (extensionData instanceof KeyUsageExtensionData) {
				this.keyUsageExtension.set(null);
			} else if (extensionData instanceof ExtendedKeyUsageExtensionData) {
				this.extendedKeyUsageExtension.set(null);
			} else if (extensionData instanceof SubjectAlternativeNameExtensionData) {
				this.subjectAlternativeExtension.set(null);
			} else if (extensionData instanceof CRLDistributionPointsExtensionData) {
				this.crlDistributionPointsExtension.set(null);
			}
			this.ctlExtensionData.getItems().remove(extensionDataItem);
		}
	}

	@FXML
	void onCmdGenerate(ActionEvent evt) {
		try {
			String alias = validateAndGetAlias();
			CertGenerator generator = validateAndGetGenerator();
			GenerateCertRequest generateRequest = validateAndGetGenerateRequest(generator);

			getExecutorService().submit(new GenerateEntryTask(generator, generateRequest, alias));
		} catch (ValidationException e) {
			ValidationAlerts.error(e).showAndWait();
		}
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
		Controls.resetComboBoxOptions(this.ctlKeySizeOption, keySizes, (o1, o2) -> o1.compareTo(o2));
		resetSigAlgOptions(keyAlg);
	}

	private void onGeneratorChanged(CertGenerator newGenerator) {
		DefaultSet<Issuer> issuers = (newGenerator != null ? newGenerator.getIssuers(this.store, this.storeEntry)
				: null);

		Controls.resetComboBoxOptions(this.ctlIssuerInput, issuers, (o1, o2) -> o1.compareTo(o2));
		resetSigAlgOptions(newGenerator);
		resetValidityInput(newGenerator);
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
		this.ctlGeneratorOption.valueProperty().addListener((p, o, n) -> onGeneratorChanged(n));
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
	 * @param issuerEntryParam The (optional) store entry to use for certificate
	 *        issuing.
	 * @param expertModeParam Whether to run in expert mode ({@code true}) or
	 *        not ({@code false}).
	 * @return This controller.
	 */
	public CertOptionsController init(UserCertStore storeParam, UserCertStoreEntry issuerEntryParam,
			boolean expertModeParam) {
		this.store = storeParam;
		this.storePreferences = this.store.storePreferences();
		this.storeEntry = issuerEntryParam;
		this.expertMode = expertModeParam;
		initExpertMode();
		initCertificateNames();
		initKeyAlgOptions();
		initGeneratorOptions();
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
		Controls.resetComboBoxOptions(this.ctlKeyAlgOption,
				KeyPairAlgorithm.getDefaultSet(this.storePreferences.defaultKeyPairAlgorithm.get(), this.expertMode),
				(o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	private void initGeneratorOptions() {
		ObservableList<CertGenerator> generatorOptions = this.ctlGeneratorOption.getItems();

		generatorOptions.clear();
		generatorOptions.addAll(CertGenerators.REGISTERED.providers());
		generatorOptions.sort((o1, o2) -> o1.toString().compareTo(o2.toString()));
		this.ctlGeneratorOption.setValue(CertGenerators.DEFAULT);
	}

	private void resetSigAlgOptions(CertGenerator generator) {
		KeyPairAlgorithm keyPairAlgorithm = this.ctlKeyAlgOption.getValue();
		Issuer issuer = this.ctlIssuerInput.getValue();

		resetSigAlgOptions(generator, keyPairAlgorithm, issuer);
	}

	private void resetSigAlgOptions(KeyPairAlgorithm keyPairAlgorithm) {
		CertGenerator generator = this.ctlGeneratorOption.getValue();
		Issuer issuer = this.ctlIssuerInput.getValue();

		resetSigAlgOptions(generator, keyPairAlgorithm, issuer);
	}

	private void resetSigAlgOptions(Issuer issuer) {
		CertGenerator generator = this.ctlGeneratorOption.getValue();
		KeyPairAlgorithm keyPairAlgorithm = this.ctlKeyAlgOption.getValue();

		resetSigAlgOptions(generator, keyPairAlgorithm, issuer);
	}

	private void resetSigAlgOptions(CertGenerator generator, KeyPairAlgorithm keyPairAlgorithm, Issuer issuer) {
		DefaultSet<SignatureAlgorithm> sigAlgs = null;

		if (generator != null) {
			String defaultHint = null;

			if (keyPairAlgorithm != null) {
				if (keyPairAlgorithm.algorithm().equals(this.storePreferences.defaultKeyPairAlgorithm.get())) {
					defaultHint = this.storePreferences.defaultSignatureAlgorithm.get();
				}
			}
			sigAlgs = generator.getSignatureAlgorithms(issuer, keyPairAlgorithm, defaultHint, this.expertMode);
		}
		Controls.resetComboBoxOptions(this.ctlSigAlgOption, sigAlgs,
				(o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	private void resetValidityInput(CertGenerator generator) {
		if (generator != null && generator.hasFeature(CertGenerator.Feature.CUSTOM_VALIDITY)) {
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

	@Override
	protected void setBlocked(boolean blocked) {
		this.ctlControlPane.setDisable(blocked);
		this.ctlProgressOverlay.setVisible(blocked);
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

	private GenerateCertRequest validateAndGetGenerateRequest(CertGenerator generator) throws ValidationException {
		X500Principal dn = validateAndGetDN();
		KeyPairAlgorithm keyAlg = validateAndGetKeyAlg();
		int keySize = validateAndGetKeySize();
		GenerateCertRequest generateRequest = new GenerateCertRequest(dn, keyAlg, keySize);

		if (generator.hasFeature(CertGenerator.Feature.CUSTOM_ISSUER)) {
			generateRequest.setIssuer(validateAndGetIssuer());
		}
		if (generator.hasFeature(CertGenerator.Feature.CUSTOM_SIGNATURE_ALGORITHM)) {
			generateRequest.setSignatureAlgorithm(validateAndGetSigAlg());
		}
		if (generator.hasFeature(CertGenerator.Feature.CUSTOM_VALIDITY)) {
			Date notBefore = validateAndGetNotBefore();
			Date notAfter = validateAndGetNotAfter(notBefore);

			generateRequest.setNotBefore(notBefore);
			generateRequest.setNotAfter(notAfter);
		}
		if (generator.hasFeature(CertGenerator.Feature.CUSTOM_EXTENSIONS)) {
			for (ExtensionDataModel extensionItem : this.ctlExtensionData.getItems()) {
				generateRequest.addExtension(extensionItem.getExtensionData());
			}
		}
		return generateRequest;
	}

	private String validateAndGetAlias() throws ValidationException {
		return InputValidator.notEmpty(Strings.safeTrim(this.ctlAliasInput.getText()),
				(a) -> CertOptionsI18N.formatSTR_MESSAGE_NO_ALIAS(a));
	}

	private X500Principal validateAndGetDN() throws ValidationException {
		String dnInput = InputValidator.notEmpty(Strings.safeTrim(this.ctlDNInput.getText()),
				(a) -> CertOptionsI18N.formatSTR_MESSAGE_NO_DN(a));
		X500Principal dn;

		try {
			dn = X500Names.fromString(dnInput);
		} catch (IllegalArgumentException e) {
			throw new ValidationException(CertOptionsI18N.formatSTR_MESSAGE_INVALID_DN(dnInput), e);
		}
		return dn;
	}

	private KeyPairAlgorithm validateAndGetKeyAlg() throws ValidationException {
		return InputValidator.notNull(this.ctlKeyAlgOption.getValue(),
				(a) -> CertOptionsI18N.formatSTR_MESSAGE_NO_KEYALG());
	}

	private int validateAndGetKeySize() throws ValidationException {
		return InputValidator
				.notNull(this.ctlKeySizeOption.getValue(), (a) -> CertOptionsI18N.formatSTR_MESSAGE_NO_KEYSIZE())
				.intValue();
	}

	private CertGenerator validateAndGetGenerator() throws ValidationException {
		return InputValidator.notNull(this.ctlGeneratorOption.getValue(),
				(a) -> CertOptionsI18N.formatSTR_MESSAGE_NO_GENERATOR());
	}

	private Issuer validateAndGetIssuer() throws ValidationException {
		return InputValidator.notNull(this.ctlIssuerInput.getValue(),
				(a) -> CertOptionsI18N.formatSTR_MESSAGE_NO_ISSUER());
	}

	private SignatureAlgorithm validateAndGetSigAlg() throws ValidationException {
		return InputValidator.notNull(this.ctlSigAlgOption.getValue(),
				(a) -> CertOptionsI18N.formatSTR_MESSAGE_NO_SIGALG());
	}

	private Date validateAndGetNotBefore() throws ValidationException {
		LocalDate localNotBefore = InputValidator.notNull(this.ctlNotBeforeInput.getValue(),
				(a) -> CertOptionsI18N.formatSTR_MESSAGE_NO_NOTBEFORE());

		return Date.from(localNotBefore.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	private Date validateAndGetNotAfter(Date notBefore) throws ValidationException {
		LocalDate localNotAfter = InputValidator.notNull(this.ctlNotAfterInput.getValue(),
				(a) -> CertOptionsI18N.formatSTR_MESSAGE_NO_NOTAFTER());
		Date notAfter = Date.from(localNotAfter.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		InputValidator.isTrue(notAfter.compareTo(notBefore) > 0,
				(a) -> CertOptionsI18N.formatSTR_MESSAGE_INVALID_VALIDITY(notBefore, notAfter));
		return notAfter;
	}

	void generateEntry(CertGenerator generator, GenerateCertRequest generateRequest, String alias) throws IOException {
		this.store.generateEntry(generator, generateRequest, PasswordDialog.enterPassword(this),
				PasswordDialog.enterNewPassword(this), alias);
	}

	private class GenerateEntryTask extends BackgroundTask<Void> {

		private final CertGenerator generator;
		private final GenerateCertRequest generateRequest;
		private final String alias;

		GenerateEntryTask(CertGenerator generator, GenerateCertRequest generateRequest, String alias) {
			this.generator = generator;
			this.generateRequest = generateRequest;
			this.alias = alias;
		}

		@Override
		protected Void call() throws Exception {
			generateEntry(this.generator, this.generateRequest, this.alias);
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
