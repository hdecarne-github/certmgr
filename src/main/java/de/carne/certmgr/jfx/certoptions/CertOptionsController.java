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

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import de.carne.certmgr.jfx.dneditor.DNEditorController;
import de.carne.certmgr.jfx.dneditor.DNEditorDialog;
import de.carne.certmgr.jfx.password.PasswordDialog;
import de.carne.certmgr.jfx.resources.Images;
import de.carne.boot.check.Check;
import de.carne.boot.check.Nullable;
import de.carne.jfx.application.PlatformHelper;
import de.carne.jfx.scene.control.Alerts;
import de.carne.jfx.scene.control.Controls;
import de.carne.jfx.stage.StageController;
import de.carne.jfx.util.DefaultSet;
import de.carne.jfx.util.validation.InputValidator;
import de.carne.jfx.util.validation.ValidationAlerts;
import de.carne.jfx.util.validation.ValidationException;
import de.carne.util.Late;
import de.carne.util.Strings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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

	private final Late<UserCertStore> storeParam = new Late<>();

	private final Late<UserCertStorePreferences> storePreferencesParam = new Late<>();

	@Nullable
	private UserCertStoreEntry storeEntryParam = null;

	private boolean expertModeParam = false;

	private final Late<CertOptionsPreset> defaultPresetParam = new Late<>();

	private final ObjectProperty<BasicConstraintsExtensionData> basicConstraintsExtension = new SimpleObjectProperty<>(
			null);

	private final ObjectProperty<KeyUsageExtensionData> keyUsageExtension = new SimpleObjectProperty<>(null);

	private final ObjectProperty<ExtendedKeyUsageExtensionData> extendedKeyUsageExtension = new SimpleObjectProperty<>(
			null);

	private final ObjectProperty<SubjectAlternativeNameExtensionData> subjectAlternativeExtension = new SimpleObjectProperty<>(
			null);

	private final ObjectProperty<CRLDistributionPointsExtensionData> crlDistributionPointsExtension = new SimpleObjectProperty<>(
			null);

	@SuppressWarnings("null")
	@FXML
	GridPane ctlControlPane;

	@SuppressWarnings("null")
	@FXML
	VBox ctlProgressOverlay;

	@SuppressWarnings("null")
	@FXML
	Menu ctlStorePresetsMenu;

	@SuppressWarnings("null")
	@FXML
	Menu ctlStoreTemplatesMenu;

	@SuppressWarnings("null")
	@FXML
	Menu ctlTemplatePresetsMenu;

	@SuppressWarnings("null")
	@FXML
	TextField ctlAliasInput;

	@SuppressWarnings("null")
	@FXML
	TextField ctlDNInput;

	@SuppressWarnings("null")
	@FXML
	ComboBox<KeyPairAlgorithm> ctlKeyAlgOption;

	@SuppressWarnings("null")
	@FXML
	ComboBox<Integer> ctlKeySizeOption;

	@SuppressWarnings("null")
	@FXML
	ChoiceBox<CertGenerator> ctlGeneratorOption;

	@SuppressWarnings("null")
	@FXML
	ComboBox<SignatureAlgorithm> ctlSigAlgOption;

	@SuppressWarnings("null")
	@FXML
	DatePicker ctlNotBeforeInput;

	@SuppressWarnings("null")
	@FXML
	DatePicker ctlNotAfterInput;

	@SuppressWarnings("null")
	@FXML
	ComboBox<Issuer> ctlIssuerInput;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdAddBasicConstraints;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdAddKeyUsage;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdAddExtendedKeyUsage;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdAddSubjectAlternativeName;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdAddCRLDistributionPoints;

	@SuppressWarnings("null")
	@FXML
	Button cmdEditExtension;

	@SuppressWarnings("null")
	@FXML
	Button cmdDeleteExtension;

	@SuppressWarnings("null")
	@FXML
	TableView<ExtensionDataModel> ctlExtensionData;

	@SuppressWarnings("null")
	@FXML
	TableColumn<ExtensionDataModel, Boolean> ctlExtensionDataCritical;

	@SuppressWarnings("null")
	@FXML
	TableColumn<ExtensionDataModel, String> ctlExtensionDataName;

	@SuppressWarnings("null")
	@FXML
	TableColumn<ExtensionDataModel, String> ctlExtensionDataValue;

	@SuppressWarnings("unused")
	@FXML
	void onCmdEditDN(ActionEvent evt) {
		try {
			DNEditorController dnEditor = DNEditorDialog.load(this).init(this.ctlDNInput.getText());
			Optional<X500Principal> editResult = dnEditor.showAndWait();

			if (editResult.isPresent()) {
				this.ctlDNInput.setText(X500Names.toString(editResult.get()));
			}
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdApplyDefaultPreset(ActionEvent evt) {
		applyPreset(this.defaultPresetParam.get());
	}

	@FXML
	void onCmdApplyTemplate(ActionEvent evt) {
		CertOptionsTemplates.Template template = (CertOptionsTemplates.Template) ((MenuItem) evt.getSource())
				.getUserData();
		String aliasInput = Strings.safe(this.ctlAliasInput.getText());
		String serial = CertOptionsTemplates.defaultSerial();

		applyPreset(template.merge(aliasInput, Check.notNull(this.storeParam.get().storeName()), serial));
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdManagePresetTemplates(ActionEvent evt) {
		try {
			ManageTemplatesController manageTemplatesDialog = ManageTemplatesDialog.load(this).init(getCurrentPreset());

			manageTemplatesDialog.showAndWait();
			resetTemplatePresetsMenu();
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdEditBasicConstraints(ActionEvent evt) {
		try {
			BasicConstraintsController extensionDialog = BasicConstraintsDialog.load(this);
			BasicConstraintsExtensionData extensionData = this.basicConstraintsExtension.get();

			if (extensionData != null) {
				extensionDialog.init(extensionData, this.expertModeParam);
			} else {
				extensionDialog.init(this.expertModeParam);
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

	@SuppressWarnings("unused")
	@FXML
	void onCmdEditKeyUsage(ActionEvent evt) {
		try {
			KeyUsageController extensionDialog = KeyUsageDialog.load(this);
			KeyUsageExtensionData extensionData = this.keyUsageExtension.get();

			if (extensionData != null) {
				extensionDialog.init(extensionData, this.expertModeParam);
			} else {
				extensionDialog.init(this.expertModeParam);
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

	@SuppressWarnings("unused")
	@FXML
	void onCmdEditExtendedKeyUsage(ActionEvent evt) {
		try {
			ExtendedKeyUsageController extensionDialog = ExtendedKeyUsageDialog.load(this);
			ExtendedKeyUsageExtensionData extensionData = this.extendedKeyUsageExtension.get();

			if (extensionData != null) {
				extensionDialog.init(extensionData, this.expertModeParam);
			} else {
				extensionDialog.init(this.expertModeParam);
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

	@SuppressWarnings("unused")
	@FXML
	void onCmdEditSubjectAlternativeName(ActionEvent evt) {
		try {
			SubjectAlternativeNameController extensionDialog = SubjectAlternativeNameDialog.load(this);
			SubjectAlternativeNameExtensionData extensionData = this.subjectAlternativeExtension.get();

			if (extensionData != null) {
				extensionDialog.init(extensionData, this.expertModeParam);
			} else {
				extensionDialog.init(this.expertModeParam);
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

	@SuppressWarnings("unused")
	@FXML
	void onCmdEditCRLDistributionPoints(ActionEvent evt) {
		try {
			CRLDistributionPointsController extensionDialog = CRLDistributionPointsDialog.load(this);
			CRLDistributionPointsExtensionData extensionData = this.crlDistributionPointsExtension.get();

			if (extensionData != null) {
				extensionDialog.init(extensionData, this.expertModeParam);
			} else {
				extensionDialog.init(this.expertModeParam);
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
			} else {
				Alerts.message(AlertType.INFORMATION, CertOptionsI18N.formatSTR_MESSAGE_CANNOT_EDIT_EXTENSION(),
						ButtonType.OK).showAndWait();
			}
		}
	}

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
	@FXML
	void onCmdCancel(ActionEvent evt) {
		close(false);
	}

	private void onAliasChanged(@Nullable String oldAlias, @Nullable String newAlias) {
		String checkedOldAlias = Strings.safeTrim(oldAlias);
		String checkedNewAlias = Strings.safeTrim(newAlias);
		String oldDNInput = Strings.safeTrim(this.ctlDNInput.getText());
		String newDNInput = CertOptionsTemplates.merge().aliasInput(checkedOldAlias, checkedNewAlias)
				.applyToDN(oldDNInput);

		this.ctlDNInput.setText(newDNInput);
	}

	private void onKeyAlgChanged(@Nullable KeyPairAlgorithm keyAlg) {
		DefaultSet<Integer> keySizes = null;

		if (keyAlg != null) {
			UserCertStorePreferences storePreferences = this.storePreferencesParam.get();
			Integer defaultHint = null;

			if (keyAlg.algorithm().equals(storePreferences.defaultKeyPairAlgorithm.get())) {
				defaultHint = storePreferences.defaultKeySize.get();
			}
			keySizes = keyAlg.getStandardKeySizes(defaultHint);
		}
		Controls.resetComboBoxOptions(this.ctlKeySizeOption, keySizes, (o1, o2) -> o1.compareTo(o2));
		resetSigAlgOptions(keyAlg);
	}

	private void onGeneratorChanged(@Nullable CertGenerator generator) {
		DefaultSet<Issuer> issuers = (generator != null
				? generator.getIssuers(this.storeParam.get(), this.storeEntryParam)
				: null);

		Controls.resetComboBoxOptions(this.ctlIssuerInput, issuers, (o1, o2) -> o1.compareTo(o2));
		resetSigAlgOptions(generator);
		resetValidityInput(generator);
	}

	private void onIssuerChanged(Issuer issuer) {
		resetSigAlgOptions(issuer);
	}

	@Override
	protected void setupStage(Stage stage) {
		stage.getIcons().addAll(PlatformHelper.stageIcons(Images.NEWCERT32, Images.NEWCERT16));
		stage.setTitle(CertOptionsI18N.formatSTR_STAGE_TITLE());
		this.ctlAliasInput.textProperty().addListener((p, o, n) -> onAliasChanged(o, n));
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
	 * @param store The store to add the generated certificate to.
	 * @param issuerEntry The (optional) store entry to use for certificate issuing.
	 * @param expertMode Whether to run in expert mode ({@code true}) or not ({@code false}).
	 * @return This controller.
	 */
	public CertOptionsController init(UserCertStore store, @Nullable UserCertStoreEntry issuerEntry,
			boolean expertMode) {
		this.storeParam.set(store);
		this.storePreferencesParam.set(Check.notNull(store.storePreferences()));
		this.storeEntryParam = issuerEntry;
		this.expertModeParam = expertMode;
		initExpertMode();
		initCertificateNames();
		initKeyAlgOptions();
		initGeneratorOptions();
		this.defaultPresetParam.set(getCurrentPreset());
		resetStorePresetsMenu();
		resetTemplatePresetsMenu();
		return this;
	}

	private void initExpertMode() {
		this.ctlKeySizeOption.setEditable(this.expertModeParam);
	}

	private void initCertificateNames() {
		UserCertStore store = this.storeParam.get();
		UserCertStoreEntryId entryId = store.generateEntryId(CertOptionsI18N.formatSTR_TEXT_ALIASHINT());

		this.ctlAliasInput.setText(entryId.getAlias());

		String serial = CertOptionsTemplates.defaultSerial();

		this.ctlDNInput.setText(
				CertOptionsTemplates.defaultDNInput(entryId.getAlias(), Check.notNull(store.storeName()), serial));
	}

	private void initKeyAlgOptions() {
		UserCertStorePreferences storePreferences = this.storePreferencesParam.get();

		Controls.resetComboBoxOptions(this.ctlKeyAlgOption,
				KeyPairAlgorithm.getDefaultSet(storePreferences.defaultKeyPairAlgorithm.get(), this.expertModeParam),
				(o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	private void initGeneratorOptions() {
		ObservableList<CertGenerator> generatorOptions = this.ctlGeneratorOption.getItems();

		generatorOptions.clear();
		generatorOptions.addAll(CertGenerators.REGISTERED.providers());
		generatorOptions.sort((o1, o2) -> o1.toString().compareTo(o2.toString()));
		this.ctlGeneratorOption.setValue(CertGenerators.DEFAULT);
	}

	private void resetStorePresetsMenu() {
		ObservableList<MenuItem> menuItems = this.ctlStorePresetsMenu.getItems();

		menuItems.clear();
		resetStorePresetMenuHelper(menuItems, this.storeParam.get().getRootEntries(), "");
		if (menuItems.isEmpty()) {
			MenuItem noneMenuItem = new MenuItem(CertOptionsI18N.formatSTR_MENU_PRESETS_NONE());

			noneMenuItem.setDisable(true);
			menuItems.add(noneMenuItem);
		}
	}

	private void resetStorePresetMenuHelper(ObservableList<MenuItem> menuItems, Set<UserCertStoreEntry> storeEntries,
			String indent) {
		for (UserCertStoreEntry storeEntry : storeEntries) {
			if (storeEntry.hasCRT() || storeEntry.hasCSR()) {
				MenuItem storeEntryMenuItem = new MenuItem(CertOptionsI18N.formatSTR_MENU_STORE_PRESET(indent,
						storeEntry.id().getAlias(), X500Names.toString(storeEntry.dn())));

				storeEntryMenuItem.setUserData(CertOptionsTemplates.wrap(storeEntry));
				storeEntryMenuItem.setOnAction(this::onCmdApplyTemplate);
				menuItems.add(storeEntryMenuItem);

				String nextIndent = (Strings.notEmpty(indent) ? "  " + indent : " \u21b3");

				resetStorePresetMenuHelper(menuItems, storeEntry.issuedEntries(), nextIndent);
			}
		}
	}

	private void resetTemplatePresetsMenu() {
		ObservableList<MenuItem> menuItems = this.ctlTemplatePresetsMenu.getItems();

		menuItems.clear();

		List<CertOptionsTemplates.Template> templates = CertOptionsTemplates.load();

		for (CertOptionsTemplates.Template template : templates) {
			MenuItem templateMenuItem = new MenuItem(template.getName());

			templateMenuItem.setUserData(template);
			templateMenuItem.setOnAction(this::onCmdApplyTemplate);
			menuItems.add(templateMenuItem);
		}
		if (menuItems.isEmpty()) {
			MenuItem noneMenuItem = new MenuItem(CertOptionsI18N.formatSTR_MENU_PRESETS_NONE());

			noneMenuItem.setDisable(true);
			menuItems.add(noneMenuItem);
		}
	}

	private void resetSigAlgOptions(@Nullable CertGenerator generator) {
		KeyPairAlgorithm keyPairAlgorithm = this.ctlKeyAlgOption.getValue();
		Issuer issuer = this.ctlIssuerInput.getValue();

		resetSigAlgOptions(generator, keyPairAlgorithm, issuer);
	}

	private void resetSigAlgOptions(@Nullable KeyPairAlgorithm keyPairAlgorithm) {
		CertGenerator generator = this.ctlGeneratorOption.getValue();
		Issuer issuer = this.ctlIssuerInput.getValue();

		resetSigAlgOptions(generator, keyPairAlgorithm, issuer);
	}

	private void resetSigAlgOptions(@Nullable Issuer issuer) {
		CertGenerator generator = this.ctlGeneratorOption.getValue();
		KeyPairAlgorithm keyPairAlgorithm = this.ctlKeyAlgOption.getValue();

		resetSigAlgOptions(generator, keyPairAlgorithm, issuer);
	}

	private void resetSigAlgOptions(@Nullable CertGenerator generator, @Nullable KeyPairAlgorithm keyPairAlgorithm,
			@Nullable Issuer issuer) {
		DefaultSet<SignatureAlgorithm> sigAlgs = null;

		if (generator != null) {
			String defaultHint = null;

			if (keyPairAlgorithm != null) {
				UserCertStorePreferences storePreferences = this.storePreferencesParam.get();

				if (keyPairAlgorithm.algorithm().equals(storePreferences.defaultKeyPairAlgorithm.get())) {
					defaultHint = storePreferences.defaultSignatureAlgorithm.get();
				}
			}
			sigAlgs = generator.getSignatureAlgorithms(issuer, keyPairAlgorithm, defaultHint, this.expertModeParam);
		}
		Controls.resetComboBoxOptions(this.ctlSigAlgOption, sigAlgs,
				(o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	private void resetValidityInput(@Nullable CertGenerator generator) {
		if (generator != null && generator.hasFeature(CertGenerator.Feature.CUSTOM_VALIDITY)) {
			UserCertStorePreferences storePreferences = this.storePreferencesParam.get();
			LocalDate notBeforeValue = LocalDate.now();
			LocalDate notAfterValue = notBeforeValue.plus(storePreferences.defaultCRTValidityPeriod.getInt(0),
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

	private CertOptionsPreset getCurrentPreset() {
		String aliasInput = Strings.safe(this.ctlAliasInput.getText());
		String dnInput = Strings.safe(this.ctlDNInput.getText());
		CertOptionsPreset preset = new CertOptionsPreset(aliasInput, dnInput);

		preset.setKeyAlg(this.ctlKeyAlgOption.getValue());
		preset.setKeySize(this.ctlKeySizeOption.getValue());
		for (ExtensionDataModel extensionData : this.ctlExtensionData.getItems()) {
			preset.addExtension(extensionData.getExtensionData());
		}
		return preset;
	}

	private void applyPreset(CertOptionsPreset preset) {
		this.ctlAliasInput.setText(preset.aliasInput());
		this.ctlDNInput.setText(preset.dnInput());

		KeyPairAlgorithm keyAlg = preset.getKeyAlg();

		if (keyAlg != null) {
			this.ctlKeyAlgOption.setValue(preset.getKeyAlg());
		}

		Integer keySize = preset.getKeySize();

		if (keySize != null) {
			this.ctlKeySizeOption.setValue(preset.getKeySize());
		}
		this.basicConstraintsExtension.set(null);
		this.keyUsageExtension.set(null);
		this.extendedKeyUsageExtension.set(null);
		this.subjectAlternativeExtension.set(null);
		this.crlDistributionPointsExtension.set(null);
		this.ctlExtensionData.getItems().clear();
		for (X509ExtensionData extensionData : preset.getExtensions()) {
			if (extensionData instanceof BasicConstraintsExtensionData) {
				this.basicConstraintsExtension.set((BasicConstraintsExtensionData) extensionData);
			} else if (extensionData instanceof KeyUsageExtensionData) {
				this.keyUsageExtension.set((KeyUsageExtensionData) extensionData);
			} else if (extensionData instanceof ExtendedKeyUsageExtensionData) {
				this.extendedKeyUsageExtension.set((ExtendedKeyUsageExtensionData) extensionData);
			} else if (extensionData instanceof SubjectAlternativeNameExtensionData) {
				this.subjectAlternativeExtension.set((SubjectAlternativeNameExtensionData) extensionData);
			} else if (extensionData instanceof CRLDistributionPointsExtensionData) {
				this.crlDistributionPointsExtension.set((CRLDistributionPointsExtensionData) extensionData);
			}
			this.ctlExtensionData.getItems().add(new ExtensionDataModel(extensionData));
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
				CertOptionsI18N::formatSTR_MESSAGE_NO_ALIAS);
	}

	private X500Principal validateAndGetDN() throws ValidationException {
		String dnInput = InputValidator.notEmpty(Strings.safeTrim(this.ctlDNInput.getText()),
				CertOptionsI18N::formatSTR_MESSAGE_NO_DN);
		X500Principal dn;

		try {
			dn = X500Names.fromString(dnInput);
		} catch (IllegalArgumentException e) {
			throw new ValidationException(CertOptionsI18N.formatSTR_MESSAGE_INVALID_DN(dnInput), e);
		}
		return dn;
	}

	private KeyPairAlgorithm validateAndGetKeyAlg() throws ValidationException {
		return InputValidator.notNull(this.ctlKeyAlgOption.getValue(), CertOptionsI18N::formatSTR_MESSAGE_NO_KEYALG);
	}

	private int validateAndGetKeySize() throws ValidationException {
		return InputValidator.notNull(this.ctlKeySizeOption.getValue(), CertOptionsI18N::formatSTR_MESSAGE_NO_KEYSIZE)
				.intValue();
	}

	private CertGenerator validateAndGetGenerator() throws ValidationException {
		return InputValidator.notNull(this.ctlGeneratorOption.getValue(),
				CertOptionsI18N::formatSTR_MESSAGE_NO_GENERATOR);
	}

	private Issuer validateAndGetIssuer() throws ValidationException {
		return InputValidator.notNull(this.ctlIssuerInput.getValue(), CertOptionsI18N::formatSTR_MESSAGE_NO_ISSUER);
	}

	private SignatureAlgorithm validateAndGetSigAlg() throws ValidationException {
		return InputValidator.notNull(this.ctlSigAlgOption.getValue(), CertOptionsI18N::formatSTR_MESSAGE_NO_SIGALG);
	}

	private Date validateAndGetNotBefore() throws ValidationException {
		LocalDate localNotBefore = InputValidator.notNull(this.ctlNotBeforeInput.getValue(),
				CertOptionsI18N::formatSTR_MESSAGE_NO_NOTBEFORE);

		return Date.from(localNotBefore.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	private Date validateAndGetNotAfter(Date notBefore) throws ValidationException {
		LocalDate localNotAfter = InputValidator.notNull(this.ctlNotAfterInput.getValue(),
				CertOptionsI18N::formatSTR_MESSAGE_NO_NOTAFTER);
		Date notAfter = Date.from(localNotAfter.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		InputValidator.isTrue(notAfter.compareTo(notBefore) > 0,
				(a) -> CertOptionsI18N.formatSTR_MESSAGE_INVALID_VALIDITY(notBefore, notAfter));
		return notAfter;
	}

	void generateEntry(CertGenerator generator, GenerateCertRequest generateRequest, String alias) throws IOException {
		UserCertStore store = this.storeParam.get();

		store.generateEntry(generator, generateRequest, PasswordDialog.enterPassword(this),
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
		@Nullable
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
