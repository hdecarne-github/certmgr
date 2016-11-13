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
package de.carne.certmgr.jfx.store;

import java.io.File;
import java.io.IOException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.x509.Attributes;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;
import de.carne.certmgr.certs.x509.X509CRLHelper;
import de.carne.certmgr.certs.x509.X509CertificateHelper;
import de.carne.certmgr.jfx.certimport.CertImportController;
import de.carne.certmgr.jfx.certoptions.CertOptionsController;
import de.carne.certmgr.jfx.preferences.PreferencesController;
import de.carne.certmgr.jfx.preferences.PreferencesDialog;
import de.carne.certmgr.jfx.resources.Images;
import de.carne.certmgr.jfx.storepreferences.StorePreferencesController;
import de.carne.certmgr.jfx.storepreferences.StorePreferencesDialog;
import de.carne.certmgr.jfx.util.UserCertStoreTreeTableViewHelper;
import de.carne.jfx.application.PlatformHelper;
import de.carne.jfx.scene.control.Alerts;
import de.carne.jfx.scene.control.aboutinfo.AboutInfoController;
import de.carne.jfx.scene.control.aboutinfo.AboutInfoDialog;
import de.carne.jfx.stage.StageController;
import de.carne.jfx.stage.Windows;
import de.carne.jfx.stage.logview.LogViewController;
import de.carne.jfx.util.ShortDate;
import de.carne.text.MemUnitFormat;
import de.carne.util.Exceptions;
import de.carne.util.prefs.DirectoryPreference;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Application main dialog for store management.
 */
public class StoreController extends StageController {

	private final Preferences preferences = Preferences.systemNodeForPackage(StoreController.class);

	private final DirectoryPreference preferenceInitalDirectory = new DirectoryPreference(this.preferences,
			"initialDirectory", true);

	private final UserPreferences userPreferences = new UserPreferences();

	private UserCertStoreTreeTableViewHelper<StoreEntryModel> storeEntryViewHelper = null;

	private ObjectProperty<UserCertStore> storeProperty = new SimpleObjectProperty<>(null);

	@FXML
	MenuItem cmdStorePreferences;

	@FXML
	MenuItem cmdCopyEntry;

	@FXML
	MenuItem cmdDeleteEntry;

	@FXML
	MenuItem cmdNewCert;

	@FXML
	MenuItem cmdRevokeCert;

	@FXML
	MenuItem cmdEditCRL;

	@FXML
	MenuItem cmdExportCert;

	@FXML
	MenuItem cmdImportCerts;

	@FXML
	CheckMenuItem cmdToggleLogView;

	@FXML
	Button cmdStorePreferencesButton;

	@FXML
	Button cmdCopyEntryButton;

	@FXML
	Button cmdDeleteEntryButton;

	@FXML
	Button cmdNewCertButton;

	@FXML
	Button cmdRevokeCertButton;

	@FXML
	Button cmdEditCRLButton;

	@FXML
	Button cmdExportCertButton;

	@FXML
	Button cmdImportCertsButton;

	@FXML
	TreeTableView<StoreEntryModel> ctlStoreEntryView;

	@FXML
	TreeTableColumn<StoreEntryModel, String> ctlStoreEntryViewId;

	@FXML
	TreeTableColumn<StoreEntryModel, String> ctlStoreEntryViewDN;

	@FXML
	TreeTableColumn<StoreEntryModel, ShortDate> ctlStoreEntryViewExpires;

	@FXML
	TreeTableView<AttributeModel> ctlDetailsView;

	@FXML
	TreeTableColumn<AttributeModel, String> ctlDetailsViewName;

	@FXML
	TreeTableColumn<AttributeModel, String> ctlDetailsViewValue;

	@FXML
	Label ctlStoreStatusLabel;

	@FXML
	Label ctlHeapStatusLabel;

	@FXML
	void onCmdNewStore(ActionEvent evt) {
		try {
			StorePreferencesController createStore = StorePreferencesDialog.load(this)
					.init(this.userPreferences.expertMode.getBoolean(false));
			Optional<UserCertStore> createStoreResult = createStore.showAndWait();

			if (createStoreResult.isPresent()) {
				UserCertStore store = createStoreResult.get();

				this.storeProperty.set(store);
				updateStoreEntryView();
				this.ctlStoreStatusLabel.setText(StoreI18N.formatSTR_STORE_STATUS(store.storeHome()));
			}
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@FXML
	void onCmdOpenStore(ActionEvent evt) {
		DirectoryChooser chooser = new DirectoryChooser();

		chooser.setInitialDirectory(this.preferenceInitalDirectory.getValueAsFile());

		File storeDirectory = chooser.showDialog(getUI());

		if (storeDirectory != null) {
			openStore(storeDirectory);
			this.preferenceInitalDirectory.putValueFromFile(storeDirectory);
			syncPreferences();
		}
	}

	@FXML
	void onCmdStorePreferences(ActionEvent evt) {
		try {
			StorePreferencesController storePreferences = StorePreferencesDialog.load(this)
					.init(this.storeProperty.get(), this.userPreferences.expertMode.getBoolean(false));

			storePreferences.showAndWait();
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@FXML
	void onCmdClose(ActionEvent evt) {
		close(true);
	}

	@FXML
	void onCmdCopyEntry(ActionEvent evt) {

	}

	@FXML
	void onCmdDeleteEntry(ActionEvent evt) {

	}

	@FXML
	void onCmdNewCert(ActionEvent evt) {
		try {
			CertOptionsController certOptions = loadStage(CertOptionsController.class).init(this.storeProperty.get(),
					getSelectedStoreEntry(), this.userPreferences.expertMode.getBoolean(false));

			certOptions.show();
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@FXML
	void onCmdRevokeCert(ActionEvent evt) {

	}

	@FXML
	void onCmdEditCRL(ActionEvent evt) {

	}

	@FXML
	void onCmdExportCert(ActionEvent evt) {

	}

	@FXML
	void onCmdImportCerts(ActionEvent evt) {
		try {
			CertImportController importController = loadStage(CertImportController.class);

			importController.show();
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@FXML
	void onCmdToggleLogView(ActionEvent evt) {
		if (this.cmdToggleLogView.isSelected()) {
			try {
				loadStage(LogViewController.class).setToggle(this.cmdToggleLogView.selectedProperty()).show();
			} catch (IOException e) {
				Alerts.unexpected(e).showAndWait();
			}
		}
	}

	@FXML
	void onCmdPreferences(ActionEvent evt) {
		try {
			PreferencesController editPreferences = PreferencesDialog.load(this).init(this.userPreferences);

			editPreferences.showAndWait();
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@FXML
	void onCmdAbout(ActionEvent evt) {
		try {
			AboutInfoController aboutInfo = AboutInfoDialog.load(this).setLogo(Images.STORE32);

			aboutInfo.addInfo(getClass().getResource("AboutInfo1.txt"));
			aboutInfo.addInfo(getClass().getResource("AboutInfo2.txt"));
			aboutInfo.addInfo(getClass().getResource("AboutInfo3.txt"));
			aboutInfo.showAndWait();
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	private void onStoreViewSelectionChange(TreeItem<StoreEntryModel> selection) {
		updateDetailsView(selection);
	}

	private void updateHeapStatus() {
		Runtime rt = Runtime.getRuntime();
		long usedMemory = rt.totalMemory() - rt.freeMemory();
		long maxMemory = rt.maxMemory();
		double usageRatio = (usedMemory * 1.0) / maxMemory;
		MemUnitFormat usedFormat = new MemUnitFormat(new DecimalFormat());
		NumberFormat usageFormat = NumberFormat.getPercentInstance();

		this.ctlHeapStatusLabel.setText(
				StoreI18N.formatSTR_HEAP_STATUS(usedFormat.format(usedMemory), usageFormat.format(usageRatio)));
	}

	@Override
	protected void setupStage(Stage stage) {
		stage.getIcons().addAll(Images.STORE32, Images.STORE16);
		stage.setTitle(StoreI18N.formatSTR_STAGE_TITLE());
		this.cmdStorePreferences.disableProperty().bind(this.storeProperty.isNull());
		this.cmdCopyEntry.disableProperty()
				.bind(this.ctlStoreEntryView.getSelectionModel().selectedItemProperty().isNull());
		this.cmdDeleteEntry.disableProperty()
				.bind(this.ctlStoreEntryView.getSelectionModel().selectedItemProperty().isNull());
		this.cmdNewCert.disableProperty().bind(this.storeProperty.isNull());
		this.cmdRevokeCert.disableProperty()
				.bind(this.ctlStoreEntryView.getSelectionModel().selectedItemProperty().isNull());
		this.cmdEditCRL.disableProperty()
				.bind(this.ctlStoreEntryView.getSelectionModel().selectedItemProperty().isNull());
		this.cmdExportCert.disableProperty()
				.bind(this.ctlStoreEntryView.getSelectionModel().selectedItemProperty().isNull());
		this.cmdImportCerts.disableProperty().bind(this.storeProperty.isNull());
		this.cmdStorePreferencesButton.disableProperty().bind(this.storeProperty.isNull());
		this.cmdCopyEntryButton.disableProperty().bind(this.cmdCopyEntry.disableProperty());
		this.cmdDeleteEntryButton.disableProperty().bind(this.cmdDeleteEntry.disableProperty());
		this.cmdNewCertButton.disableProperty().bind(this.cmdNewCert.disableProperty());
		this.cmdRevokeCertButton.disableProperty().bind(this.cmdRevokeCert.disableProperty());
		this.cmdEditCRLButton.disableProperty().bind(this.cmdEditCRL.disableProperty());
		this.cmdExportCertButton.disableProperty().bind(this.cmdExportCert.disableProperty());
		this.cmdImportCertsButton.disableProperty().bind(this.cmdImportCerts.disableProperty());
		this.ctlStoreEntryViewId.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
		this.ctlStoreEntryViewDN.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		this.ctlStoreEntryViewExpires.setCellValueFactory(new TreeItemPropertyValueFactory<>("expires"));
		this.ctlDetailsViewName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		this.ctlDetailsViewValue.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
		this.ctlStoreEntryView.getSelectionModel().selectedItemProperty()
				.addListener((p, o, n) -> onStoreViewSelectionChange(n));
		Windows.onHiding(stage, (ScheduledFuture<?> f) -> f.cancel(true), getExecutorService().scheduleAtFixedRate(
				PlatformHelper.runLaterRunnable(() -> updateHeapStatus()), 0, 500, TimeUnit.MILLISECONDS));
	}

	@Override
	protected Preferences getPreferences() {
		return this.preferences;
	}

	/**
	 * Open a certificate store.
	 *
	 * @param storeHome The home path of the store to open.
	 */
	public void openStore(File storeHome) {
		assert storeHome != null;

		try {
			this.storeProperty.set(UserCertStore.openStore(storeHome.toPath()));
			updateStoreEntryView();
			this.ctlStoreStatusLabel.setText(StoreI18N.formatSTR_STORE_STATUS(storeHome));
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	private UserCertStoreEntry getSelectedStoreEntry() {
		TreeItem<StoreEntryModel> selectedItem = this.ctlStoreEntryView.getSelectionModel().getSelectedItem();

		return (selectedItem != null ? selectedItem.getValue().getEntry() : null);
	}

	private void updateStoreEntryView() {
		if (this.storeEntryViewHelper == null) {
			this.storeEntryViewHelper = new UserCertStoreTreeTableViewHelper<>(this.ctlStoreEntryView,
					(e) -> new StoreEntryModel(e));
		}
		this.storeEntryViewHelper.update(this.storeProperty.get());
	}

	private void updateDetailsView(TreeItem<StoreEntryModel> selection) {
		TreeItem<AttributeModel> rootItem = null;

		if (selection != null) {
			rootItem = new TreeItem<>();
			rootItem.setExpanded(true);

			UserCertStoreEntry entry = selection.getValue().getEntry();

			updateDetailsViewHelper(rootItem, Attributes.toAttributes(entry), true);
			if (entry.hasCRT()) {
				try {
					X509Certificate crt = entry.getCRT();

					updateDetailsViewHelper(rootItem, X509CertificateHelper.toAttributes(crt), true);
				} catch (IOException e) {
					Exceptions.warn(e);
				}
			}
			if (entry.hasCSR()) {
				try {
					PKCS10CertificateRequest csr = entry.getCSR();

					updateDetailsViewHelper(rootItem, csr.toAttributes(), true);
				} catch (IOException e) {
					Exceptions.ignore(e);
				}
			}
			if (entry.hasCRL()) {
				try {
					X509CRL crl = entry.getCRL();

					updateDetailsViewHelper(rootItem, X509CRLHelper.toAttributes(crl), true);
				} catch (IOException e) {
					Exceptions.ignore(e);
				}
			}
		}
		this.ctlDetailsView.setRoot(rootItem);
	}

	private void updateDetailsViewHelper(TreeItem<AttributeModel> parentItem, Attributes attribute, boolean expand) {
		TreeItem<AttributeModel> attributeItem = new TreeItem<>(
				new AttributeModel(attribute.name(), attribute.value()));

		for (Attributes child : attribute.children()) {
			updateDetailsViewHelper(attributeItem, child, false);
		}
		parentItem.getChildren().add(attributeItem);
		attributeItem.setExpanded(expand);
	}

}
