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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.x509.Attributes;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;
import de.carne.certmgr.certs.x509.X509CRLHelper;
import de.carne.certmgr.certs.x509.X509CertificateHelper;
import de.carne.certmgr.jfx.UserCertStoreTreeTableViewHelper;
import de.carne.certmgr.jfx.certimport.CertImportController;
import de.carne.certmgr.jfx.certoptions.CertOptionsController;
import de.carne.certmgr.jfx.resources.Images;
import de.carne.jfx.application.PlatformHelper;
import de.carne.jfx.scene.control.Alerts;
import de.carne.jfx.stage.StageController;
import de.carne.jfx.stage.Windows;
import de.carne.jfx.stage.logview.LogViewController;
import de.carne.jfx.util.ShortDate;
import de.carne.text.MemUnitFormat;
import de.carne.util.Exceptions;
import de.carne.util.prefs.DirectoryPreference;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

	private UserCertStoreTreeTableViewHelper<StoreEntryModel> storeEntryViewHelper = null;

	private UserCertStore store = null;

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
			CertOptionsController certOptions = loadStage(CertOptionsController.class);

			certOptions.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void onCmdRevokeCert(ActionEvent evt) {

	}

	@FXML
	void onCmdEditCRL(ActionEvent evt) {

	}

	@FXML
	void onCmdExportCerts(ActionEvent evt) {

	}

	@FXML
	void onCmdImportCerts(ActionEvent evt) {
		try {
			CertImportController importController = loadStage(CertImportController.class);

			importController.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void onCmdShowLog(ActionEvent evt) {
		try {
			loadStage(LogViewController.class).showAndWait();
		} catch (IOException e) {
			Alerts.unexpected(e);
		}
	}

	@FXML
	void onCmdAbout(ActionEvent evt) {

	}

	void onStoreViewSelectionChange(TreeItem<StoreEntryModel> selection) {
		updateDetailsView(selection);
	}

	void updateHeapStatus() {
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
		this.ctlStoreEntryViewId.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
		this.ctlStoreEntryViewDN.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		this.ctlStoreEntryViewExpires.setCellValueFactory(new TreeItemPropertyValueFactory<>("expires"));
		this.ctlDetailsViewName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		this.ctlDetailsViewValue.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
		this.ctlStoreEntryView.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<TreeItem<StoreEntryModel>>() {

					@Override
					public void changed(ObservableValue<? extends TreeItem<StoreEntryModel>> observable,
							TreeItem<StoreEntryModel> oldValue, TreeItem<StoreEntryModel> newValue) {
						onStoreViewSelectionChange(newValue);
					}

				});
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
			this.store = UserCertStore.openStore(storeHome.toPath());
			updateStoreEntryView();
			this.ctlStoreStatusLabel.setText(StoreI18N.formatSTR_STORE_STATUS(this.store.storeHome()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateStoreEntryView() {
		if (this.storeEntryViewHelper == null) {
			this.storeEntryViewHelper = new UserCertStoreTreeTableViewHelper<>(this.ctlStoreEntryView,
					(e) -> new StoreEntryModel(e));
		}
		this.storeEntryViewHelper.update(this.store);
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
					Exceptions.ignore(e);
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
