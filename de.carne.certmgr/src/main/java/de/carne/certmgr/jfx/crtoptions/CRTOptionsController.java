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
package de.carne.certmgr.jfx.crtoptions;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.security.auth.x500.X500Principal;

import de.carne.certmgr.jfx.CertStoreEntryOption;
import de.carne.certmgr.jfx.GeneralNameModel;
import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.jfx.InputValidator;
import de.carne.certmgr.jfx.InvalidInputException;
import de.carne.certmgr.jfx.NamedOption;
import de.carne.certmgr.jfx.StageController;
import de.carne.certmgr.jfx.StoreOptions;
import de.carne.certmgr.jfx.dneditor.DNEditorController;
import de.carne.certmgr.jfx.help.Help;
import de.carne.certmgr.jfx.help.HelpController;
import de.carne.certmgr.jfx.messagebox.MessageBoxStyle;
import de.carne.certmgr.jfx.passwordprompt.PasswordPromptCallback;
import de.carne.certmgr.store.CertStore;
import de.carne.certmgr.store.CertStoreEntry;
import de.carne.certmgr.store.PKCS10Object;
import de.carne.certmgr.store.PasswordCallback;
import de.carne.certmgr.store.x509.CertificateValidity;
import de.carne.certmgr.store.x509.DistributionPoint;
import de.carne.certmgr.store.x509.DistributionPointName;
import de.carne.certmgr.store.x509.EncodedX509Extension;
import de.carne.certmgr.store.x509.ExtendedKeyUsage;
import de.carne.certmgr.store.x509.GeneralName;
import de.carne.certmgr.store.x509.GeneralNameType;
import de.carne.certmgr.store.x509.KeyParams;
import de.carne.certmgr.store.x509.KeyUsage;
import de.carne.certmgr.store.x509.X509BasicConstraintsExtension;
import de.carne.certmgr.store.x509.X509CRLDistributionPointsExtension;
import de.carne.certmgr.store.x509.X509CertificateParams;
import de.carne.certmgr.store.x509.X509ExtendedKeyUsageExtension;
import de.carne.certmgr.store.x509.X509Extension;
import de.carne.certmgr.store.x509.X509KeyUsageExtension;
import de.carne.certmgr.store.x509.X509SubjectAlternativeNameExtension;
import de.carne.util.Strings;
import de.carne.util.logging.Log;

/**
 * Dialog controller for CRT/CSR generation.
 */
public class CRTOptionsController extends StageController {

	private static final Log LOG = new Log(CRTOptionsController.class);

	/**
	 * Controller's create callback interface.
	 */
	public interface Result {

		/**
		 * Called after an certificate store entry has been re-generated.
		 *
		 * @param entryParam The re-generated certificate store entry.
		 */
		public void onEntryGenerate(CertStoreEntry entryParam);

	}

	private Result result = null;
	private CertStore store = null;
	private CertStoreEntry storeEntry = null;
	private StoreOptions storeOptions = new StoreOptions();

	@FXML
	MenuButton ctlPresetSelection;

	@FXML
	Menu ctlTemplatePresetMenu;

	@FXML
	Menu ctlStorePresetMenu;

	@FXML
	RadioMenuItem ctlEmptyPresetMenuItem;

	@FXML
	Label ctlSubjectDNLabel;

	@FXML
	TextField ctlSubjectDNInput;

	@FXML
	Button ctlSubjectDNEditButton;

	@FXML
	Label ctlAliasLabel;

	@FXML
	TextField ctlAliasInput;

	@FXML
	Label ctlKeyAlgLabel;

	@FXML
	ComboBox<String> ctlKeyAlgSelection;

	@FXML
	Label ctlKeySizeLabel;

	@FXML
	ChoiceBox<Integer> ctlKeySizeSelection;

	@FXML
	ComboBox<String> ctlSigAlgSelection;

	@FXML
	Label ctlValidFromLabel;

	@FXML
	DatePicker ctlValidFromInput;

	@FXML
	Label ctlValidToLabel;

	@FXML
	DatePicker ctlValidToInput;

	@FXML
	Label ctlIssuerLabel;

	@FXML
	ComboBox<CertStoreEntryOption> ctlIssuerSelection;

	@FXML
	Accordion ctlExtensionsInput;

	@FXML
	ChoiceBox<ExtensionSelectionOption> ctlBasicConstraintsEnabled;

	@FXML
	CheckBox ctlBasicConstraintsCAFlag;

	@FXML
	ChoiceBox<NamedOption<Integer>> ctlBasicConstraintsPathLenSelection;

	@FXML
	ChoiceBox<ExtensionSelectionOption> ctlKeyUsageEnabled;

	@FXML
	CheckBox ctlKeyUsageAnySelection;

	@FXML
	ListView<KeyUsage> ctlKeyUsageSelection;

	@FXML
	ChoiceBox<ExtensionSelectionOption> ctlExtendedKeyUsageEnabled;

	@FXML
	CheckBox ctlExtendedKeyUsageAnySelection;

	@FXML
	ListView<ExtendedKeyUsage> ctlExtendedKeyUsageSelection;

	@FXML
	ChoiceBox<ExtensionSelectionOption> ctlSubjectAlternativeNameEnabled;

	@FXML
	TableView<GeneralNameModel> ctlSubjectAlternativeNameInput;

	@FXML
	TableColumn<GeneralNameModel, GeneralNameType> ctlSubjectAlternativeNameInputType;

	@FXML
	TableColumn<GeneralNameModel, String> ctlSubjectAlternativeNameInputName;

	@FXML
	ChoiceBox<ExtensionSelectionOption> ctlCRLDistributionPointsEnabled;

	@FXML
	TableView<GeneralNameModel> ctlCRLDistributionPointsInput;

	@FXML
	TableColumn<GeneralNameModel, GeneralNameType> ctlCRLDistributionPointsInputType;

	@FXML
	TableColumn<GeneralNameModel, String> ctlCRLDistributionPointsInputName;

	@FXML
	Pane ctlProgressGroup;

	@FXML
	Button ctlGenerateButton;

	@FXML
	void onApplyEmptyPreset(ActionEvent evt) {
		applyPreset(new EmptyPreset());
	}

	@FXML
	void onApplySelectedPreset(ActionEvent evt) {
		applyPreset((Preset) ((MenuItem) evt.getTarget()).getUserData());
	}

	@FXML
	void ontEditSubjectDN(ActionEvent evt) {
		try {
			DNEditorController dnEditorController = openStage(DNEditorController.class);
			String dnInput = Strings.safeTrim(this.ctlSubjectDNInput.getText());

			dnEditorController.beginDNEdit(dnInput, new DNEditorController.Result() {

				@Override
				public void onDNEdit(String dnInputParam) {
					onDNEditResult(dnInputParam);
				}

			});
			dnEditorController.getStage().show();
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	@FXML
	void onGeneralNameModelInputTypeCommit(CellEditEvent<GeneralNameModel, GeneralNameType> evt) {
		ObservableList<GeneralNameModel> tableItems = evt.getTableView().getItems();
		int tableInputIndex = evt.getTablePosition().getRow();
		GeneralNameModel inputModel = tableItems.get(tableInputIndex);
		GeneralNameType newValue = evt.getNewValue();

		inputModel.setType(newValue);
		if (tableInputIndex + 1 == tableItems.size()) {
			tableItems.add(new GeneralNameModel());
		}
	}

	@FXML
	void onGeneralNameModelInputNameCommit(CellEditEvent<GeneralNameModel, String> evt) {
		ObservableList<GeneralNameModel> tableItems = evt.getTableView().getItems();
		int tableInputIndex = evt.getTablePosition().getRow();
		GeneralNameModel inputModel = tableItems.get(tableInputIndex);
		String newValue = evt.getNewValue();

		inputModel.setName(newValue);
		if (inputModel.getType() == null) {
			inputModel.setType(GeneralNameType.OTHER_NAME);
		}
		if (tableInputIndex + 1 == tableItems.size()) {
			tableItems.add(new GeneralNameModel());
		}
	}

	@FXML
	void onGenerateCRT(ActionEvent evt) {
		try {
			if (this.storeEntry != null) {
				X509CertificateParams certificateParams = validateAndGetCertificateParams();
				CertificateValidity certificateValidity = validateAndGetValidity();
				PasswordPromptCallback issuerPassword = PasswordPromptCallback.getPassword(this);

				runTask(new GenerateTask() {
					private CertStore store2 = getStore();
					private CertStoreEntry storeEntry2 = getStoreEntry();
					private CertificateValidity certificateValidity2 = certificateValidity;
					private X509CertificateParams certificateParams2 = certificateParams;
					private PasswordCallback issuerPassword2 = issuerPassword;

					@Override
					protected CertStoreEntry call() throws Exception {
						return this.store2.generateAndSignCRT(this.storeEntry2, this.certificateParams2,
								this.certificateValidity2, this.issuerPassword2);
					}

				});
			} else {
				String alias = validateAndGetAlias();
				KeyParams keyParams = validateAndGetKeyParams();
				X509CertificateParams certificateParams = validateAndGetCertificateParams();
				CertificateValidity certificateValidity = validateAndGetValidity();
				PasswordPromptCallback password = PasswordPromptCallback.getNewPassword(this);
				CertStoreEntry issuerEntry = validateAndGetIssuer();
				PasswordPromptCallback issuerPassword = PasswordPromptCallback.getPassword(this);

				runTask(new GenerateTask() {
					private CertStore store2 = getStore();
					private String alias2 = alias;
					private KeyParams keyParams2 = keyParams;
					private CertificateValidity certificateValidity2 = certificateValidity;
					private X509CertificateParams certificateParams2 = certificateParams;
					private PasswordCallback password2 = password;
					private CertStoreEntry issuerEntry2 = issuerEntry;
					private PasswordCallback issuerPassword2 = issuerPassword;

					@Override
					protected CertStoreEntry call() throws Exception {
						return this.store2.generateAndSignCRT(this.alias2, this.keyParams2, this.certificateParams2,
								this.certificateValidity2, this.password2, this.issuerEntry2, this.issuerPassword2);
					}

				});
			}
		} catch (InvalidInputException e) {
			showMessageBox(e.getLocalizedMessage(), null, MessageBoxStyle.ICON_ERROR, MessageBoxStyle.BUTTON_OK);
		}
	}

	@FXML
	void onGenerateCSR(ActionEvent evt) {
		try {
			if (this.storeEntry != null) {
				X509CertificateParams certificateParams = validateAndGetCertificateParams();
				PasswordPromptCallback password = PasswordPromptCallback.getNewPassword(this);

				runTask(new GenerateTask() {
					private CertStore store2 = getStore();
					private CertStoreEntry storeEntry2 = getStoreEntry();
					private X509CertificateParams certificateParams2 = certificateParams;
					private PasswordCallback password2 = password;

					@Override
					protected CertStoreEntry call() throws Exception {
						return this.store2
								.generateAndSignCSR(this.storeEntry2, this.certificateParams2, this.password2);
					}

				});
			} else {
				String alias = validateAndGetAlias();
				KeyParams keyParams = validateAndGetKeyParams();
				X509CertificateParams certificateParams = validateAndGetCertificateParams();
				PasswordPromptCallback password = PasswordPromptCallback.getNewPassword(this);

				runTask(new GenerateTask() {
					private CertStore store2 = getStore();
					private String alias2 = alias;
					private KeyParams keyParams2 = keyParams;
					private X509CertificateParams certificateParams2 = certificateParams;
					private PasswordCallback password2 = password;

					@Override
					protected CertStoreEntry call() throws Exception {
						return this.store2.generateAndSignCSR(this.alias2, this.keyParams2, this.certificateParams2,
								this.password2);
					}

				});
			}
		} catch (InvalidInputException e) {
			showMessageBox(e.getLocalizedMessage(), null, MessageBoxStyle.ICON_ERROR, MessageBoxStyle.BUTTON_OK);
		}
	}

	@FXML
	void onCancel(ActionEvent evt) {
		getStage().close();
	}

	@FXML
	void onHelp(ActionEvent evt) {
		try {
			HelpController.showHelp(this, Help.TOPIC_CRTOPTIONS);
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}
	
	CertStore getStore() {
		return this.store;
	}
	
	CertStoreEntry getStoreEntry() {
		return this.storeEntry;
	}

	void onKeyAlgSelected(String keyAlg) {
		this.ctlKeySizeSelection.getItems().clear();
		this.ctlKeySizeSelection.getItems().addAll(CertStore.getKeySizes(keyAlg));
		if (keyAlg.equals(this.storeOptions.getDefKeyAlg())) {
			this.ctlKeySizeSelection.getSelectionModel().select(this.storeOptions.getDefKeySize());
		} else {
			this.ctlKeySizeSelection.getSelectionModel().select(CertStore.getDefaultKeySize(keyAlg));
		}
		this.ctlSigAlgSelection.getItems().clear();
		this.ctlSigAlgSelection.getItems().addAll(CertStore.getSigAlgs(keyAlg));
		if (keyAlg.equals(this.storeOptions.getDefKeyAlg())) {
			this.ctlSigAlgSelection.getSelectionModel().select(this.storeOptions.getDefSigAlg());
		} else {
			this.ctlSigAlgSelection.getSelectionModel().select(CertStore.getDefaultSigAlg(keyAlg));
		}
	}

	void onDNEditResult(String dnInputParam) {
		this.ctlSubjectDNInput.setText(dnInputParam);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		this.ctlProgressGroup.setVisible(false);
		this.ctlKeyAlgSelection.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> property, String oldValue, String newValue) {
				onKeyAlgSelected(newValue);
			}

		});
		this.ctlKeyUsageSelection.disableProperty().bind(this.ctlKeyUsageAnySelection.selectedProperty());
		this.ctlExtendedKeyUsageSelection.disableProperty().bind(
				this.ctlExtendedKeyUsageAnySelection.selectedProperty());
	}

	void onGenerationScheduled() {
		this.ctlProgressGroup.setVisible(true);
	}

	void onGenerationSucceeded(CertStoreEntry entry) {
		this.ctlProgressGroup.setVisible(false);
		getStage().close();
		this.result.onEntryGenerate(entry);
	}

	void onGenerationFailed(Throwable cause) {
		this.ctlProgressGroup.setVisible(false);
		showMessageBox(I18N.format(I18N.MESSAGE_GENERATEERROR), cause, MessageBoxStyle.ICON_ERROR,
				MessageBoxStyle.BUTTON_OK);
	}

	/**
	 * Begin CRT generation.
	 *
	 * @param storeParam The certificate store to create the CRT for.
	 * @param callback The callback to report the result of the user actions.
	 * @throws IOException if an I/O error occurs while generating the CRT.
	 */
	public void beginCRTOptions(CertStore storeParam, Result callback) throws IOException {
		getStage().getIcons().addAll(Images.IMAGE_NEWCRT16, Images.IMAGE_NEWCRT32);
		beginGeneralCRTOptions(storeParam, null, callback);
	}

	/**
	 * Begin CRT re-generation.
	 *
	 * @param entryParam The store entry to re-sign the CRT for.
	 * @param callback The callback to report the result of the user actions.
	 * @throws IOException if an I/O error occurs while generating the CRT.
	 */
	public void beginCRTOptions(CertStoreEntry entryParam, Result callback) throws IOException {
		getStage().getIcons().addAll(Images.IMAGE_RESIGNCRT16, Images.IMAGE_RESIGNCRT32);
		beginGeneralCRTOptions(entryParam.getStore(), entryParam, callback);
	}

	private void beginGeneralCRTOptions(CertStore storeParam, CertStoreEntry entryParam, Result callback)
			throws IOException {
		String title = I18N.format(entryParam != null ? I18N.TEXT_REGENERATECRTTITLE : I18N.TEXT_GENERATECRTTITLE);

		getStage().setTitle(title);
		beginGeneralOptions(storeParam, entryParam, callback);

		LocalDate validFrom = LocalDate.now();
		LocalDate validTo = this.storeOptions.getDefCRTValidity().plusLocalData(validFrom);

		this.ctlValidFromInput.setValue(validFrom);
		this.ctlValidToInput.setValue(validTo);
		this.ctlIssuerSelection.getItems().addAll(
				CertStoreEntryOption.fromStoreWithPredicate(this.store, e -> e.hasKey()));
		if (this.storeEntry != null) {
			X509Certificate crt = this.storeEntry.getCRT().getObject();

			this.ctlPresetSelection.setDisable(true);
			this.ctlSubjectDNInput.setText(crt.getSubjectX500Principal().toString());
			this.ctlSubjectDNLabel.setDisable(true);
			this.ctlSubjectDNInput.setDisable(true);
			this.ctlSubjectDNEditButton.setDisable(true);
			this.ctlAliasInput.setText(this.storeEntry.getAlias());
			this.ctlAliasLabel.setDisable(true);
			this.ctlAliasInput.setDisable(true);
			this.ctlKeyAlgSelection.getSelectionModel().select(crt.getPublicKey().getAlgorithm());
			this.ctlKeyAlgLabel.setDisable(true);
			this.ctlKeyAlgSelection.setDisable(true);
			this.ctlKeySizeLabel.setDisable(true);
			this.ctlKeySizeSelection.setDisable(true);
			this.ctlIssuerSelection.getSelectionModel().select(
					CertStoreEntryOption.fromStoreEntry(this.store.getEntry(this.storeEntry.getIssuer())));
			this.ctlIssuerLabel.setDisable(true);
			this.ctlIssuerSelection.setDisable(true);

			Collection<EncodedX509Extension> extensions = CertStore.decodeCRTExtensions(crt);

			for (EncodedX509Extension extension : extensions) {
				X509Extension decoded = extension.getDecoded();

				if (decoded instanceof X509BasicConstraintsExtension) {
					applyBasicConstraints((X509BasicConstraintsExtension) decoded);
				} else if (decoded instanceof X509KeyUsageExtension) {
					applyKeyUsage((X509KeyUsageExtension) decoded);
				} else if (decoded instanceof X509ExtendedKeyUsageExtension) {
					applyExtendedKeyUsage((X509ExtendedKeyUsageExtension) decoded);
				} else if (decoded instanceof X509SubjectAlternativeNameExtension) {
					applySubjectAlternativeName((X509SubjectAlternativeNameExtension) decoded);
				} else if (decoded instanceof X509CRLDistributionPointsExtension) {
					applyCRLDistributionPoints((X509CRLDistributionPointsExtension) decoded);
				}
			}
		}
		this.ctlGenerateButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				onGenerateCRT(event);
			}

		});
		getStage().sizeToScene();
	}

	/**
	 * Begin CSR generation.
	 *
	 * @param storeParam The certificate store to create the CSR for.
	 * @param callback The callback to report the result of the user actions.
	 * @throws IOException if an I/O error occurs while generating the CSR.
	 */
	public void beginCSROptions(CertStore storeParam, Result callback) throws IOException {
		getStage().getIcons().addAll(Images.IMAGE_NEWCSR16, Images.IMAGE_NEWCSR32);
		beginGeneralCSROptions(storeParam, null, callback);
	}

	/**
	 * Begin CSR re-generation.
	 *
	 * @param entryParam The certificate store to re-sign the CSR for.
	 * @param callback The callback to report the result of the user actions.
	 * @throws IOException if an I/O error occurs while generating the CSR.
	 */
	public void beginCSROptions(CertStoreEntry entryParam, Result callback) throws IOException {
		getStage().getIcons().addAll(Images.IMAGE_RESIGNCSR16, Images.IMAGE_RESIGNCSR32);
		beginGeneralCSROptions(entryParam.getStore(), entryParam, callback);
	}

	private void beginGeneralCSROptions(CertStore storeParam, CertStoreEntry entryParam, Result callback)
			throws IOException {
		String title = I18N.format(entryParam != null ? I18N.TEXT_REGENERATECSRTITLE : I18N.TEXT_GENERATECSRTITLE);

		getStage().setTitle(title);
		beginGeneralOptions(storeParam, entryParam, callback);
		this.ctlValidFromLabel.setDisable(true);
		this.ctlValidFromInput.setDisable(true);
		this.ctlValidToLabel.setDisable(true);
		this.ctlValidToInput.setDisable(true);
		this.ctlIssuerLabel.setDisable(true);
		this.ctlIssuerSelection.setDisable(true);
		if (this.storeEntry != null) {
			PKCS10Object csr = this.storeEntry.getCSR().getObject();

			this.ctlPresetSelection.setDisable(true);
			this.ctlSubjectDNInput.setText(csr.getSubjectX500Principal().toString());
			this.ctlSubjectDNLabel.setDisable(true);
			this.ctlSubjectDNInput.setDisable(true);
			this.ctlSubjectDNEditButton.setDisable(true);
			this.ctlAliasInput.setText(this.storeEntry.getAlias());
			this.ctlAliasLabel.setDisable(true);
			this.ctlAliasInput.setDisable(true);
			this.ctlKeyAlgSelection.getSelectionModel().select(csr.getPublicKey().getAlgorithm());
			this.ctlKeyAlgLabel.setDisable(true);
			this.ctlKeyAlgSelection.setDisable(true);
			this.ctlKeySizeLabel.setDisable(true);
			this.ctlKeySizeSelection.setDisable(true);

			Collection<EncodedX509Extension> extensions = CertStore.decodeCSRExtensions(csr);

			for (EncodedX509Extension extension : extensions) {
				X509Extension decoded = extension.getDecoded();

				if (decoded instanceof X509BasicConstraintsExtension) {
					applyBasicConstraints((X509BasicConstraintsExtension) decoded);
				} else if (decoded instanceof X509KeyUsageExtension) {
					applyKeyUsage((X509KeyUsageExtension) decoded);
				} else if (decoded instanceof X509ExtendedKeyUsageExtension) {
					applyExtendedKeyUsage((X509ExtendedKeyUsageExtension) decoded);
				} else if (decoded instanceof X509SubjectAlternativeNameExtension) {
					applySubjectAlternativeName((X509SubjectAlternativeNameExtension) decoded);
				} else if (decoded instanceof X509CRLDistributionPointsExtension) {
					applyCRLDistributionPoints((X509CRLDistributionPointsExtension) decoded);
				}
			}
		}
		this.ctlGenerateButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				onGenerateCSR(event);
			}

		});
		getStage().sizeToScene();
	}

	private void beginGeneralOptions(CertStore storeParam, CertStoreEntry entryParam, Result callback) {
		assert storeParam != null;
		assert callback != null;

		this.result = callback;
		this.store = storeParam;
		this.storeEntry = entryParam;
		this.storeOptions.load(this.store);
		this.ctlEmptyPresetMenuItem.setSelected(true);
		populatePresetsMenu(this.ctlTemplatePresetMenu, TemplatePreset.getPresets(this.store));
		populatePresetsMenu(this.ctlStorePresetMenu, getStorePresets());
		this.ctlAliasInput.setText(this.store.createAlias());
		this.ctlKeyAlgSelection.getItems().addAll(CertStore.getKeyAlgs());
		this.ctlKeyAlgSelection.getSelectionModel().select(this.storeOptions.getDefKeyAlg());
		// Issuer
		this.ctlIssuerSelection.getItems().add(CertStoreEntryOption.defaultOption(I18N.format(I18N.TEXT_SELFSIGNED)));
		this.ctlIssuerSelection.getSelectionModel().select(0);
		// BasicConstraint extension
		this.ctlBasicConstraintsEnabled.getItems().addAll(ExtensionSelectionOption.VALUES);
		this.ctlBasicConstraintsEnabled.getSelectionModel().select(0);
		this.ctlBasicConstraintsCAFlag.setSelected(true);
		this.ctlBasicConstraintsPathLenSelection.getItems().addAll(getBasicConstriantsPathLens());
		this.ctlBasicConstraintsPathLenSelection.getSelectionModel().select(0);
		// Key Usage extension
		this.ctlKeyUsageEnabled.getItems().addAll(ExtensionSelectionOption.VALUES);
		this.ctlKeyUsageEnabled.getSelectionModel().select(0);
		this.ctlKeyUsageSelection.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.ctlKeyUsageSelection.getItems().addAll(KeyUsage.sortedValues(false));
		// Extended Key Usage extension
		this.ctlExtendedKeyUsageEnabled.getItems().addAll(ExtensionSelectionOption.VALUES);
		this.ctlExtendedKeyUsageEnabled.getSelectionModel().select(0);
		this.ctlExtendedKeyUsageSelection.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.ctlExtendedKeyUsageSelection.getItems().addAll(ExtendedKeyUsage.sortedValues(false));
		// Subject Alternative Name extension
		this.ctlSubjectAlternativeNameEnabled.getItems().addAll(ExtensionSelectionOption.VALUES);
		this.ctlSubjectAlternativeNameEnabled.getSelectionModel().select(0);
		this.ctlSubjectAlternativeNameInputType.setCellValueFactory(new PropertyValueFactory<>("type"));
		this.ctlSubjectAlternativeNameInputName.setCellValueFactory(new PropertyValueFactory<>("name"));
		this.ctlSubjectAlternativeNameInputType.setCellFactory(ChoiceBoxTableCell.forTableColumn(GeneralNameType
				.sortedValues()));
		this.ctlSubjectAlternativeNameInputName.setCellFactory(TextFieldTableCell.forTableColumn());
		this.ctlSubjectAlternativeNameInput.setItems(FXCollections.observableArrayList(new GeneralNameModel()));
		// CRL Distribution Points extension
		this.ctlCRLDistributionPointsEnabled.getItems().addAll(ExtensionSelectionOption.VALUES);
		this.ctlCRLDistributionPointsEnabled.getSelectionModel().select(0);
		this.ctlCRLDistributionPointsInputType.setCellValueFactory(new PropertyValueFactory<>("type"));
		this.ctlCRLDistributionPointsInputName.setCellValueFactory(new PropertyValueFactory<>("name"));
		this.ctlCRLDistributionPointsInputType.setCellFactory(ChoiceBoxTableCell.forTableColumn(GeneralNameType
				.sortedValues()));
		this.ctlCRLDistributionPointsInputName.setCellFactory(TextFieldTableCell.forTableColumn());
		this.ctlCRLDistributionPointsInput.setItems(FXCollections.observableArrayList(new GeneralNameModel()));
	}

	private void populatePresetsMenu(Menu menu, Collection<? extends Preset> presets) {
		for (Preset preset : presets) {
			RadioMenuItem templateMenuItem = new RadioMenuItem(preset.getName());

			templateMenuItem.setToggleGroup(this.ctlEmptyPresetMenuItem.getToggleGroup());
			templateMenuItem.setUserData(preset);
			templateMenuItem.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent evt) {
					onApplySelectedPreset(evt);
				}

			});
			menu.getItems().add(templateMenuItem);
		}
		menu.setDisable(presets.isEmpty());
	}

	private Collection<StorePreset> getStorePresets() {
		ArrayList<StorePreset> presets = new ArrayList<>();

		for (CertStoreEntryOption entryOption : CertStoreEntryOption.fromStoreWithPredicate(this.store, e -> e.hasCRT()
				|| e.hasCSR())) {
			CertStoreEntry entry = entryOption.getEntry();
			String subjectDN = entry.getName();
			CertStoreEntry issuer = (entry.isRoot() ? null : this.store.getEntry(entry.getIssuer()));
			Collection<EncodedX509Extension> extensions = null;

			try {
				subjectDN = entry.getName();
				if (entry.hasCRT()) {
					extensions = CertStore.decodeCRTExtensions(entry.getCRT().getObject());
				} else {
					extensions = CertStore.decodeCSRExtensions(entry.getCSR().getObject());
				}
			} catch (IOException e) {
				LOG.warning(e, I18N.bundle(), I18N.MESSAGE_CERTENTRYERROR, entry.getAlias(), e.getLocalizedMessage());
			}
			presets.add(new StorePreset(entryOption.toString(), subjectDN, issuer, extensions));
		}
		return presets;
	}

	private void applyPreset(Preset preset) {
		String subjectDN = Strings.safeTrim(this.ctlSubjectDNInput.getText());

		this.ctlSubjectDNInput.setText(Strings.safeTrim(preset.getSubjectDN(subjectDN)));

		CertStoreEntry issuer = validateAndGetIssuer();

		selectIssuer(preset.getIssuer(issuer));

		X509BasicConstraintsExtension basicConstraints = null;

		if (this.ctlBasicConstraintsEnabled.getSelectionModel().getSelectedItem().getValue() != null) {
			try {
				basicConstraints = validateAndGetBasicConstraintsExtension();
			} catch (InvalidInputException e) {
				// ignore exception and assume nothing
			}
		}
		applyBasicConstraints(preset.getBasicConstraints(basicConstraints));

		X509KeyUsageExtension keyUsage = null;

		if (this.ctlKeyUsageEnabled.getSelectionModel().getSelectedItem().isEnabled()) {
			try {
				keyUsage = validateAndGetKeyUsageExtension();
			} catch (InvalidInputException e) {
				// ignore exception and assume nothing
			}
		}
		applyKeyUsage(preset.getKeyUsage(keyUsage));

		X509ExtendedKeyUsageExtension extendedKeyUsage = null;

		if (this.ctlExtendedKeyUsageEnabled.getSelectionModel().getSelectedItem().isEnabled()) {
			try {
				extendedKeyUsage = validateAndGetExtendedKeyUsageExtension();
			} catch (InvalidInputException e) {
				// ignore exception and assume nothing
			}
		}
		applyExtendedKeyUsage(preset.getExtendedKeyUsage(extendedKeyUsage));

		X509SubjectAlternativeNameExtension subjectAlternativeName = null;

		if (this.ctlSubjectAlternativeNameEnabled.getSelectionModel().getSelectedItem().isEnabled()) {
			try {
				subjectAlternativeName = validateAndGetSubjectAlternativeName();
			} catch (InvalidInputException e) {
				// ignore exception and assume nothing
			}
		}
		applySubjectAlternativeName(preset.getSubjectAlternativeName(subjectAlternativeName));

		X509CRLDistributionPointsExtension crlDistributionPoints = null;

		if (this.ctlCRLDistributionPointsEnabled.getSelectionModel().getSelectedItem().isEnabled()) {
			try {
				crlDistributionPoints = validateAndGetCRLDistributionPointsExtension();
			} catch (InvalidInputException e) {
				// ignore exception and assume nothing
			}
		}
		applyCRLDistributionPoints(preset.getCRLDistributionPoints(crlDistributionPoints));
	}

	private void applyBasicConstraints(X509BasicConstraintsExtension basicConstraints) {
		if (basicConstraints != null) {
			this.ctlBasicConstraintsEnabled.getSelectionModel().select(
					ExtensionSelectionOption.fromCritical(basicConstraints.isCritical()));
			this.ctlBasicConstraintsCAFlag.setSelected(basicConstraints.isCA());
			this.ctlBasicConstraintsPathLenSelection.getSelectionModel().select(
					new NamedOption<>(basicConstraints.getPathLenConstraint()));
		} else {
			this.ctlBasicConstraintsEnabled.getSelectionModel().select(ExtensionSelectionOption.DISABLED);
		}
	}

	private void applyKeyUsage(X509KeyUsageExtension keyUsage) {
		if (keyUsage != null) {
			this.ctlKeyUsageEnabled.getSelectionModel().select(
					ExtensionSelectionOption.fromCritical(keyUsage.isCritical()));
			this.ctlKeyUsageSelection.getSelectionModel().clearSelection();

			boolean selectAny = false;

			for (KeyUsage usage : keyUsage.getUsages()) {
				if (KeyUsage.ANY.equals(usage)) {
					selectAny = true;
				} else {
					this.ctlKeyUsageSelection.getSelectionModel().select(usage);
				}
			}
			this.ctlKeyUsageAnySelection.setSelected(selectAny);
		} else {
			this.ctlKeyUsageEnabled.getSelectionModel().select(ExtensionSelectionOption.DISABLED);
		}
	}

	private void applyExtendedKeyUsage(X509ExtendedKeyUsageExtension extendedKeyUsage) {
		if (extendedKeyUsage != null) {
			this.ctlExtendedKeyUsageEnabled.getSelectionModel().select(
					ExtensionSelectionOption.fromCritical(extendedKeyUsage.isCritical()));
			this.ctlExtendedKeyUsageSelection.getSelectionModel().clearSelection();

			boolean selectAny = false;

			for (ExtendedKeyUsage usage : extendedKeyUsage.getUsages()) {
				if (ExtendedKeyUsage.ANY.equals(usage)) {
					selectAny = true;
				} else {
					this.ctlExtendedKeyUsageSelection.getSelectionModel().select(usage);
				}
			}
			this.ctlExtendedKeyUsageAnySelection.setSelected(selectAny);
		} else {
			this.ctlExtendedKeyUsageEnabled.getSelectionModel().select(ExtensionSelectionOption.DISABLED);
		}
	}

	private void applySubjectAlternativeName(X509SubjectAlternativeNameExtension subjectAlternativeName) {
		if (subjectAlternativeName != null) {
			this.ctlSubjectAlternativeNameEnabled.getSelectionModel().select(
					ExtensionSelectionOption.fromCritical(subjectAlternativeName.isCritical()));

			ObservableList<GeneralNameModel> items = this.ctlSubjectAlternativeNameInput.getItems();

			items.clear();
			for (GeneralName name : subjectAlternativeName.getNames()) {
				items.add(new GeneralNameModel(name));
			}
		} else {
			this.ctlSubjectAlternativeNameEnabled.getSelectionModel().select(ExtensionSelectionOption.DISABLED);
		}
	}

	private void applyCRLDistributionPoints(X509CRLDistributionPointsExtension crlDistributionPoints) {
		if (crlDistributionPoints != null) {
			this.ctlCRLDistributionPointsEnabled.getSelectionModel().select(
					ExtensionSelectionOption.fromCritical(crlDistributionPoints.isCritical()));

			ObservableList<GeneralNameModel> items = this.ctlCRLDistributionPointsInput.getItems();

			items.clear();
			for (DistributionPoint distributionPoint : crlDistributionPoints.getDistributionPoints()) {
				for (DistributionPointName distributionPointName : distributionPoint.getNames()) {
					for (GeneralName name : distributionPointName.getNames()) {
						items.add(new GeneralNameModel(name));
					}
				}
			}
		} else {
			this.ctlCRLDistributionPointsEnabled.getSelectionModel().select(ExtensionSelectionOption.DISABLED);
		}
	}

	/**
	 * Select the issuer for CRT generation.
	 *
	 * @param issuerEntry The entry to select.
	 */
	public void selectIssuer(CertStoreEntry issuerEntry) {
		CertStoreEntryOption issuerOption = CertStoreEntryOption.findOption(this.ctlIssuerSelection.getItems(),
				issuerEntry);

		if (issuerOption != null) {
			this.ctlIssuerSelection.getSelectionModel().select(issuerOption);
		}
	}

	private static List<NamedOption<Integer>> getBasicConstriantsPathLens() {
		ArrayList<NamedOption<Integer>> pathLens = new ArrayList<>();

		pathLens.add(new NamedOption<>(Integer.valueOf(-1), "\u221E"));
		for (int pathLen = 0; pathLen < 10; pathLen++) {
			pathLens.add(new NamedOption<>(Integer.valueOf(pathLen)));
		}
		return pathLens;
	}

	private String validateAndGetAlias() throws InvalidInputException {
		String aliasInput = InputValidator.notEmpty(I18N.bundle(), I18N.MESSAGE_NOALIAS,
				Strings.safeTrim(this.ctlAliasInput.getText()));

		Path storeHome = this.store.getHome();
		Path checkPath;

		try {
			checkPath = storeHome.resolve(aliasInput);
		} catch (InvalidPathException e) {
			throw new InvalidInputException(I18N.format(I18N.MESSAGE_INVALIDALIAS, aliasInput), e);
		}
		if (!storeHome.equals(checkPath.getParent())) {
			throw new InvalidInputException(I18N.format(I18N.MESSAGE_INVALIDALIAS, aliasInput));
		}
		return aliasInput;
	}

	private KeyParams validateAndGetKeyParams() throws InvalidInputException {
		String keyAlg = InputValidator
				.notNull(I18N.bundle(), I18N.MESSAGE_NOKEYALG, this.ctlKeyAlgSelection.getValue());
		int keySize = InputValidator
				.notNull(I18N.bundle(), I18N.MESSAGE_NOKEYSIZE, this.ctlKeySizeSelection.getValue()).intValue();

		return new KeyParams(keyAlg, keySize);
	}

	private CertificateValidity validateAndGetValidity() throws InvalidInputException {
		LocalDate validFrom = InputValidator.notNull(I18N.bundle(), I18N.MESSAGE_NOVALIDFROM,
				this.ctlValidFromInput.getValue());
		LocalDate validTo = InputValidator.notNull(I18N.bundle(), I18N.MESSAGE_NOVALIDTO,
				this.ctlValidToInput.getValue());

		InputValidator.isTrue(I18N.bundle(), I18N.MESSAGE_INVALIDVALIDITY, validFrom.isBefore(validTo));
		return new CertificateValidity(validFrom, validTo);
	}

	private CertStoreEntry validateAndGetIssuer() {
		CertStoreEntryOption issuerOption = this.ctlIssuerSelection.getValue();

		return (issuerOption != null ? issuerOption.getEntry() : null);
	}

	private X509CertificateParams validateAndGetCertificateParams() throws InvalidInputException {
		String subjectDNInput = InputValidator.notEmpty(I18N.bundle(), I18N.MESSAGE_NOSUBJECTDN,
				Strings.safeTrim(this.ctlSubjectDNInput.getText()));
		X500Principal subjectDN = InputValidator.isDN(I18N.bundle(), I18N.MESSAGE_INVALIDSUBJECTDN, subjectDNInput);
		String sigAlg = InputValidator.notEmpty(I18N.bundle(), I18N.MESSAGE_NOSUBJECTDN,
				this.ctlSigAlgSelection.getValue());

		X509CertificateParams certificateParams = new X509CertificateParams(subjectDN, sigAlg);

		if (this.ctlBasicConstraintsEnabled.getSelectionModel().getSelectedItem().getValue() != null) {
			certificateParams.addExtension(validateAndGetBasicConstraintsExtension());
		}
		if (this.ctlKeyUsageEnabled.getSelectionModel().getSelectedItem().isEnabled()) {
			certificateParams.addExtension(validateAndGetKeyUsageExtension());
		}
		if (this.ctlExtendedKeyUsageEnabled.getSelectionModel().getSelectedItem().isEnabled()) {
			certificateParams.addExtension(validateAndGetExtendedKeyUsageExtension());
		}
		if (this.ctlSubjectAlternativeNameEnabled.getSelectionModel().getSelectedItem().isEnabled()) {
			certificateParams.addExtension(validateAndGetSubjectAlternativeName());
		}
		if (this.ctlCRLDistributionPointsEnabled.getSelectionModel().getSelectedItem().isEnabled()) {
			certificateParams.addExtension(validateAndGetCRLDistributionPointsExtension());
		}
		return certificateParams;
	}

	private X509BasicConstraintsExtension validateAndGetBasicConstraintsExtension() throws InvalidInputException {
		boolean critical = this.ctlBasicConstraintsEnabled.getSelectionModel().getSelectedItem().isCritical();
		boolean isCA = this.ctlBasicConstraintsCAFlag.isSelected();
		int pathLenConstraint = InputValidator
				.notNull(I18N.bundle(), I18N.MESSAGE_NOPATHLENCONSTRAINT,
						this.ctlBasicConstraintsPathLenSelection.getValue()).getValue().intValue();

		return new X509BasicConstraintsExtension(critical, isCA, pathLenConstraint);
	}

	private X509KeyUsageExtension validateAndGetKeyUsageExtension() throws InvalidInputException {
		boolean critical = this.ctlKeyUsageEnabled.getSelectionModel().getSelectedItem().isCritical();
		X509KeyUsageExtension extension = new X509KeyUsageExtension(critical);
		int usageCount = 0;

		if (this.ctlKeyUsageAnySelection.isSelected()) {
			extension.addUsage(KeyUsage.ANY);
			usageCount++;
		} else {
			for (KeyUsage usage : this.ctlKeyUsageSelection.getSelectionModel().getSelectedItems()) {
				extension.addUsage(usage);
				usageCount++;
			}
		}
		InputValidator.isTrue(I18N.bundle(), I18N.MESSAGE_NOKEYUSAGE, usageCount > 0);
		return extension;
	}

	private X509ExtendedKeyUsageExtension validateAndGetExtendedKeyUsageExtension() throws InvalidInputException {
		boolean critical = this.ctlExtendedKeyUsageEnabled.getSelectionModel().getSelectedItem().isCritical();
		X509ExtendedKeyUsageExtension extension = new X509ExtendedKeyUsageExtension(critical);
		int usageCount = 0;

		if (this.ctlExtendedKeyUsageAnySelection.isSelected()) {
			extension.addUsage(ExtendedKeyUsage.ANY);
			usageCount++;
		} else {
			for (ExtendedKeyUsage usage : this.ctlExtendedKeyUsageSelection.getSelectionModel().getSelectedItems()) {
				extension.addUsage(usage);
				usageCount++;
			}
		}
		InputValidator.isTrue(I18N.bundle(), I18N.MESSAGE_NOEXTENDEDKEYUSAGE, usageCount > 0);
		return extension;
	}

	private X509SubjectAlternativeNameExtension validateAndGetSubjectAlternativeName() throws InvalidInputException {
		boolean critical = this.ctlSubjectAlternativeNameEnabled.getSelectionModel().getSelectedItem().isCritical();
		X509SubjectAlternativeNameExtension extension = new X509SubjectAlternativeNameExtension(critical);
		int nameCount = 0;

		for (GeneralNameModel generalName : this.ctlSubjectAlternativeNameInput.getItems()) {
			GeneralNameType nameType = generalName.getType();

			if (nameType != null) {
				String name = generalName.getName();

				try {
					extension.addName(new GeneralName(nameType, name));
					nameCount++;
				} catch (IllegalArgumentException e) {
					throw new InvalidInputException(I18N.format(I18N.MESSAGE_INVALIDSUBJECTALTERNATIVENAME, name), e);
				}
			}
		}
		InputValidator.isTrue(I18N.bundle(), I18N.MESSAGE_NOSUBJECTALTERNATIVENAME, nameCount > 0);
		return extension;
	}

	private X509CRLDistributionPointsExtension validateAndGetCRLDistributionPointsExtension()
			throws InvalidInputException {
		boolean critical = this.ctlCRLDistributionPointsEnabled.getSelectionModel().getSelectedItem().isCritical();
		X509CRLDistributionPointsExtension extension = new X509CRLDistributionPointsExtension(critical);
		DistributionPoint distributionPoint = new DistributionPoint();
		DistributionPointName distributionPointName = new DistributionPointName();
		int issuerCount = 0;

		for (GeneralNameModel generalName : this.ctlCRLDistributionPointsInput.getItems()) {
			GeneralNameType nameType = generalName.getType();

			if (nameType != null) {
				String name = generalName.getName();

				try {
					distributionPointName.addName(new GeneralName(nameType, name));
					issuerCount++;
				} catch (IllegalArgumentException e) {
					throw new InvalidInputException(I18N.format(I18N.MESSAGE_INVALIDSUBJECTALTERNATIVENAME, name), e);
				}
			}
		}
		InputValidator.isTrue(I18N.bundle(), I18N.MESSAGE_NOISSUERNAME, issuerCount > 0);
		extension.addDistributionPoint(distributionPoint);
		return extension;
	}

	private abstract class GenerateTask extends Task<CertStoreEntry> {

		GenerateTask() {
			// Nothing to do here
		}
		
		/*
		 * (non-Javadoc)
		 * @see javafx.concurrent.Task#scheduled()
		 */
		@Override
		protected void scheduled() {
			onGenerationScheduled();
		}

		@Override
		protected void failed() {
			onGenerationFailed(getException());
		}

		@Override
		protected void succeeded() {
			onGenerationSucceeded(getValue());
		}

	}

}
