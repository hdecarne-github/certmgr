/*
 * Copyright (c) 2015-2019 Holger de Carne and contributors, All Rights Reserved.
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.Exceptions;
import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.x509.Attributes;
import de.carne.certmgr.certs.x509.PKCS10CertificateRequest;
import de.carne.certmgr.certs.x509.ReasonFlag;
import de.carne.certmgr.certs.x509.X509CRLHelper;
import de.carne.certmgr.certs.x509.X509CertificateHelper;
import de.carne.certmgr.jfx.certexport.CertExportController;
import de.carne.certmgr.jfx.certimport.CertImportController;
import de.carne.certmgr.jfx.certoptions.CertOptionsController;
import de.carne.certmgr.jfx.crloptions.CRLOptionsController;
import de.carne.certmgr.jfx.preferences.PreferencesController;
import de.carne.certmgr.jfx.preferences.PreferencesDialog;
import de.carne.certmgr.jfx.resources.Images;
import de.carne.certmgr.jfx.storepreferences.StorePreferencesController;
import de.carne.certmgr.jfx.storepreferences.StorePreferencesDialog;
import de.carne.certmgr.jfx.util.UserCertStoreTreeTableViewHelper;
import de.carne.certmgr.util.PathPreference;
import de.carne.jfx.application.PlatformHelper;
import de.carne.jfx.scene.control.Alerts;
import de.carne.jfx.scene.control.aboutinfo.AboutInfoController;
import de.carne.jfx.scene.control.aboutinfo.AboutInfoDialog;
import de.carne.jfx.stage.StageController;
import de.carne.jfx.stage.Windows;
import de.carne.jfx.stage.logview.LogViewController;
import de.carne.util.Debug;
import de.carne.util.Lazy;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Application main dialog for store management.
 */
public class StoreController extends StageController {

	private static final int DETAILS_VIEW_ATTRIBUTE_LIMIT = 100;

	private final Preferences preferences = Preferences.systemNodeForPackage(StoreController.class);

	private final PathPreference preferenceInitalDirectory = new PathPreference(this.preferences, "initialDirectory",
			PathPreference.IS_DIRECTORY);

	private final UserPreferences userPreferences = new UserPreferences();

	private final Lazy<UserCertStoreTreeTableViewHelper<StoreEntryModel>> storeEntryViewHelper = new Lazy<>(
			() -> new UserCertStoreTreeTableViewHelper<>(this.ctlStoreEntryView, (e) -> new StoreEntryModel(e)));

	private ObjectProperty<UserCertStore> storeProperty = new SimpleObjectProperty<>(null);

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdStorePreferences;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdCopyEntry;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdDeleteEntry;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdNewCert;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdRevokeCert;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdManageCRL;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdExportCert;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdImportCerts;

	@SuppressWarnings("null")
	@FXML
	CheckMenuItem cmdToggleLogView;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdContextCopyEntry;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdContextCopyEntryAttribute;

	@SuppressWarnings("null")
	@FXML
	MenuItem cmdContextDeleteEntry;

	@SuppressWarnings("null")
	@FXML
	Button cmdStorePreferencesButton;

	@SuppressWarnings("null")
	@FXML
	Button cmdCopyEntryButton;

	@SuppressWarnings("null")
	@FXML
	Button cmdDeleteEntryButton;

	@SuppressWarnings("null")
	@FXML
	Button cmdNewCertButton;

	@SuppressWarnings("null")
	@FXML
	Button cmdRevokeCertButton;

	@SuppressWarnings("null")
	@FXML
	Button cmdManageCRLButton;

	@SuppressWarnings("null")
	@FXML
	Button cmdExportCertButton;

	@SuppressWarnings("null")
	@FXML
	Button cmdImportCertsButton;

	@SuppressWarnings("null")
	@FXML
	TreeTableView<StoreEntryModel> ctlStoreEntryView;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<StoreEntryModel, String> ctlStoreEntryViewId;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<StoreEntryModel, String> ctlStoreEntryViewName;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<StoreEntryModel, BigInteger> ctlStoreEntryViewSerial;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<StoreEntryModel, Date> ctlStoreEntryViewExpires;

	@SuppressWarnings("null")
	@FXML
	Label ctlStoreEntryTooltipExternalCrt;

	@SuppressWarnings("null")
	@FXML
	Label ctlStoreEntryTooltipPublicCrt;

	@SuppressWarnings("null")
	@FXML
	Label ctlStoreEntryTooltipPrivateCrt;

	@SuppressWarnings("null")
	@FXML
	Label ctlStoreEntryTooltipCsr;

	@SuppressWarnings("null")
	@FXML
	Label ctlStoreEntryTooltipCrl;

	@SuppressWarnings("null")
	@FXML
	Label ctlStoreEntryTooltipKey;

	@SuppressWarnings("null")
	@FXML
	Label ctlStoreEntryTooltipRevokedCrt;

	@SuppressWarnings("null")
	@FXML
	Label ctlStoreEntryTooltipInvalidCrt;

	@SuppressWarnings("null")
	@FXML
	TreeTableView<AttributeModel> ctlDetailsView;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<AttributeModel, String> ctlDetailsViewName;

	@SuppressWarnings("null")
	@FXML
	TreeTableColumn<AttributeModel, String> ctlDetailsViewValue;

	@SuppressWarnings("null")
	@FXML
	Label ctlStoreStatusLabel;

	@SuppressWarnings("null")
	@FXML
	Label ctlHeapStatusLabel;

	@SuppressWarnings("unused")
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
				this.ctlStoreStatusLabel.setText(StoreI18N.formatSTR_TEXT_STORE_STATUS(store.storeHome()));
			}
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
	@FXML
	void onCmdClose(ActionEvent evt) {
		close(true);
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdCopyEntry(ActionEvent evt) {
		UserCertStoreEntry entry = getSelectedStoreEntry();

		if (entry != null) {
			List<Path> entryFilePaths = entry.getFilePaths();

			if (!entryFilePaths.isEmpty()) {
				List<File> entryFiles = entryFilePaths.stream().map((p) -> p.toFile()).collect(Collectors.toList());
				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent content = new ClipboardContent();

				content.putFiles(entryFiles);
				clipboard.setContent(content);
			}
		}
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdCopyEntryDN(ActionEvent evt) {
		TreeItem<StoreEntryModel> selectedItem = this.ctlStoreEntryView.getSelectionModel().getSelectedItem();

		if (selectedItem != null) {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			ClipboardContent content = new ClipboardContent();

			content.putString(selectedItem.getValue().getName());
			clipboard.setContent(content);
		}
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdCopyEntryAttribute(ActionEvent evt) {
		TreeItem<AttributeModel> selectedItem = this.ctlDetailsView.getSelectionModel().getSelectedItem();

		if (selectedItem != null) {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			ClipboardContent content = new ClipboardContent();

			content.putString(selectedItem.getValue().toString());
			clipboard.setContent(content);
		}
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdCopyEntryAttributes(ActionEvent evt) {
		TreeItem<AttributeModel> rootItem = this.ctlDetailsView.getRoot();

		if (rootItem != null) {
			StringWriter buffer = new StringWriter();
			PrintWriter writer = new PrintWriter(buffer);

			for (TreeItem<AttributeModel> attributeItem : rootItem.getChildren()) {
				copyEntryAttributesHelper(writer, attributeItem, "");
			}

			writer.flush();

			Clipboard clipboard = Clipboard.getSystemClipboard();
			ClipboardContent content = new ClipboardContent();

			content.putString(buffer.toString());
			clipboard.setContent(content);
		}
	}

	private void copyEntryAttributesHelper(PrintWriter writer, TreeItem<AttributeModel> item, String indent) {
		writer.print(indent);
		writer.println(item.getValue());

		String nextIndent = "  " + indent;

		for (TreeItem<AttributeModel> childItem : item.getChildren()) {
			copyEntryAttributesHelper(writer, childItem, nextIndent);
		}
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdDeleteEntry(ActionEvent evt) {
		UserCertStoreEntry entry = getSelectedStoreEntry();

		if (entry != null) {
			Optional<ButtonType> confirmation = Alerts
					.message(AlertType.CONFIRMATION, StoreI18N.formatSTR_MESSAGE_CONFIRM_DELETE(entry)).showAndWait();

			if (confirmation.isPresent() && confirmation.get().getButtonData() == ButtonData.OK_DONE) {
				try {
					this.storeProperty.get().deleteEntry(entry.id());
					if (entry.equals(getSelectedStoreEntry())) {
						this.ctlStoreEntryView.getSelectionModel().clearSelection();
					}
				} catch (IOException e) {
					Alerts.unexpected(e).showAndWait();
				}
			}
			updateStoreEntryView();
		}
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdNewCert(ActionEvent evt) {
		UserCertStore store = this.storeProperty.get();

		if (store != null) {
			try {
				CertOptionsController certOptions = loadStage(CertOptionsController.class).init(store,
						getSelectedStoreEntry(), this.userPreferences.expertMode.getBoolean(false));

				certOptions.showAndWait();
				updateStoreEntryView();
			} catch (IOException e) {
				Alerts.unexpected(e).showAndWait();
			}
		}
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdRevokeCert(ActionEvent evt) {
		UserCertStoreEntry entry = getSelectedStoreEntry();

		if (entry != null) {
			UserCertStoreEntry issuerEntry = entry.issuer();

			if (!entry.isSelfSigned() && issuerEntry.hasPublicKey() && issuerEntry.hasKey()) {
				try {
					CRLOptionsController crlOptionsController = loadStage(CRLOptionsController.class).init(issuerEntry,
							this.userPreferences.expertMode.getBoolean(false));

					crlOptionsController.revokeStoreEntry(entry, ReasonFlag.UNSPECIFIED);
					crlOptionsController.showAndWait();
					updateStoreEntryView();
				} catch (IOException e) {
					Alerts.unexpected(e).showAndWait();
				}
			} else {
				Alerts.message(AlertType.WARNING, StoreI18N.formatSTR_MESSAGE_CANNOT_REVOKE_CRT(issuerEntry),
						ButtonType.OK).showAndWait();
			}
		}
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdManageCRL(ActionEvent evt) {
		UserCertStoreEntry issuerEntry = getSelectedStoreEntry();

		if (issuerEntry != null) {
			if (issuerEntry.hasPublicKey() && issuerEntry.hasKey()) {
				try {
					CRLOptionsController crlOptionsController = loadStage(CRLOptionsController.class).init(issuerEntry,
							this.userPreferences.expertMode.getBoolean(false));

					crlOptionsController.showAndWait();
					updateStoreEntryView();
				} catch (IOException e) {
					Alerts.unexpected(e).showAndWait();
				}
			} else {
				Alerts.message(AlertType.WARNING, StoreI18N.formatSTR_MESSAGE_CANNOT_MANAGE_CRL(issuerEntry),
						ButtonType.OK).showAndWait();
			}
		}
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdExportCert(ActionEvent evt) {
		UserCertStoreEntry exportEntry = getSelectedStoreEntry();

		if (exportEntry != null) {
			try {
				CertExportController exportController = loadStage(CertExportController.class).init(exportEntry);

				exportController.showAndWait();
				updateStoreEntryView();
			} catch (IOException e) {
				Alerts.unexpected(e).showAndWait();
			}
		}
	}

	@SuppressWarnings("unused")
	@FXML
	void onCmdImportCerts(ActionEvent evt) {
		try {
			CertImportController importController = loadStage(CertImportController.class)
					.init(this.storeProperty.get());

			importController.showAndWait();
			updateStoreEntryView();
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
	@FXML
	void onCmdPreferences(ActionEvent evt) {
		try {
			PreferencesController editPreferences = PreferencesDialog.load(this).init(this.userPreferences);

			editPreferences.showAndWait();
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
	@FXML
	void onStoreViewItemMouseEntered(MouseEvent evt) {
		@SuppressWarnings("unchecked")
		TreeTableCell<StoreEntryModel, String> cell = (TreeTableCell<StoreEntryModel, String>) evt.getSource();
		UserCertStoreEntry entry = cell.getTreeTableRow().getItem().getEntry();

		this.ctlStoreEntryTooltipExternalCrt.setVisible(entry.isExternal());
		if (entry.hasCRT()) {
			if (entry.hasKey()) {
				this.ctlStoreEntryTooltipPublicCrt.setVisible(false);
				this.ctlStoreEntryTooltipPrivateCrt.setVisible(true);
				this.ctlStoreEntryTooltipCsr.setVisible(false);
				this.ctlStoreEntryTooltipCrl.setVisible(false);
				this.ctlStoreEntryTooltipKey.setVisible(false);
			} else {
				this.ctlStoreEntryTooltipPublicCrt.setVisible(true);
				this.ctlStoreEntryTooltipPrivateCrt.setVisible(false);
				this.ctlStoreEntryTooltipCsr.setVisible(false);
				this.ctlStoreEntryTooltipCrl.setVisible(false);
				this.ctlStoreEntryTooltipKey.setVisible(false);
			}
		} else if (entry.hasCSR()) {
			this.ctlStoreEntryTooltipPublicCrt.setVisible(false);
			this.ctlStoreEntryTooltipPrivateCrt.setVisible(false);
			this.ctlStoreEntryTooltipCsr.setVisible(true);
			this.ctlStoreEntryTooltipCrl.setVisible(false);
			this.ctlStoreEntryTooltipKey.setVisible(false);
		} else if (entry.hasCRL()) {
			this.ctlStoreEntryTooltipPublicCrt.setVisible(false);
			this.ctlStoreEntryTooltipPrivateCrt.setVisible(false);
			this.ctlStoreEntryTooltipCsr.setVisible(false);
			this.ctlStoreEntryTooltipCrl.setVisible(true);
			this.ctlStoreEntryTooltipKey.setVisible(false);
		} else if (entry.hasKey()) {
			this.ctlStoreEntryTooltipPublicCrt.setVisible(false);
			this.ctlStoreEntryTooltipPrivateCrt.setVisible(false);
			this.ctlStoreEntryTooltipCsr.setVisible(false);
			this.ctlStoreEntryTooltipCrl.setVisible(false);
			this.ctlStoreEntryTooltipKey.setVisible(true);
		}
		this.ctlStoreEntryTooltipRevokedCrt.setVisible(entry.isRevoked());
		this.ctlStoreEntryTooltipInvalidCrt.setVisible(!entry.isValid());
	}

	private void onStoreViewSelectionChanged(TreeItem<StoreEntryModel> selection) {
		updateDetailsView(selection);
	}

	private void onUpdateHeapStatus() {
		Runtime rt = Runtime.getRuntime();
		long usedMemory = rt.totalMemory() - rt.freeMemory();
		long maxMemory = rt.maxMemory();
		double usageRatio = (usedMemory * 1.0) / maxMemory;
		NumberFormat usageFormat = NumberFormat.getPercentInstance();

		this.ctlHeapStatusLabel.setText(
				StoreI18N.formatSTR_TEXT_HEAP_STATUS(Debug.formatUsedMemory(), usageFormat.format(usageRatio)));
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
		this.cmdManageCRL.disableProperty()
				.bind(this.ctlStoreEntryView.getSelectionModel().selectedItemProperty().isNull());
		this.cmdExportCert.disableProperty()
				.bind(this.ctlStoreEntryView.getSelectionModel().selectedItemProperty().isNull());
		this.cmdImportCerts.disableProperty().bind(this.storeProperty.isNull());
		this.cmdStorePreferencesButton.disableProperty().bind(this.storeProperty.isNull());
		this.cmdCopyEntryButton.disableProperty().bind(this.cmdCopyEntry.disableProperty());
		this.cmdDeleteEntryButton.disableProperty().bind(this.cmdDeleteEntry.disableProperty());
		this.cmdNewCertButton.disableProperty().bind(this.cmdNewCert.disableProperty());
		this.cmdRevokeCertButton.disableProperty().bind(this.cmdRevokeCert.disableProperty());
		this.cmdManageCRLButton.disableProperty().bind(this.cmdManageCRL.disableProperty());
		this.cmdExportCertButton.disableProperty().bind(this.cmdExportCert.disableProperty());
		this.cmdImportCertsButton.disableProperty().bind(this.cmdImportCerts.disableProperty());

		ContextMenu storeEntryViewMenu = this.ctlStoreEntryView.getContextMenu();

		this.ctlStoreEntryView.setContextMenu(null);
		this.ctlStoreEntryView.setRowFactory(param -> {
			ContextMenu menu = storeEntryViewMenu;

			return new TreeTableRow<StoreEntryModel>() {

				@Override
				protected void updateItem(@Nullable StoreEntryModel item, boolean empty) {
					super.updateItem(item, empty);
					if (!empty) {
						setContextMenu(menu);
					} else {
						setContextMenu(null);
					}
				}

			};
		});

		Tooltip storeEntryViewTooltip = this.ctlStoreEntryView.getTooltip();

		this.ctlStoreEntryTooltipExternalCrt.managedProperty()
				.bind(this.ctlStoreEntryTooltipExternalCrt.visibleProperty());
		this.ctlStoreEntryTooltipPublicCrt.managedProperty().bind(this.ctlStoreEntryTooltipPublicCrt.visibleProperty());
		this.ctlStoreEntryTooltipPrivateCrt.managedProperty()
				.bind(this.ctlStoreEntryTooltipPrivateCrt.visibleProperty());
		this.ctlStoreEntryTooltipCsr.managedProperty().bind(this.ctlStoreEntryTooltipCsr.visibleProperty());
		this.ctlStoreEntryTooltipCrl.managedProperty().bind(this.ctlStoreEntryTooltipCrl.visibleProperty());
		this.ctlStoreEntryTooltipKey.managedProperty().bind(this.ctlStoreEntryTooltipKey.visibleProperty());
		this.ctlStoreEntryTooltipRevokedCrt.managedProperty()
				.bind(this.ctlStoreEntryTooltipRevokedCrt.visibleProperty());
		this.ctlStoreEntryTooltipInvalidCrt.managedProperty()
				.bind(this.ctlStoreEntryTooltipInvalidCrt.visibleProperty());
		this.ctlStoreEntryView.setTooltip(null);
		this.ctlStoreEntryViewId.setCellFactory(param -> {

			return new TreeTableCell<StoreEntryModel, String>() {
				Tooltip tooltip = storeEntryViewTooltip;

				@Override
				protected void updateItem(@Nullable String item, boolean empty) {
					if (!empty) {
						setTooltip(this.tooltip);
						setOnMouseEntered(StoreController.this::onStoreViewItemMouseEntered);
						setText(item);
					} else {
						setTooltip(null);
						setOnMouseEntered(null);
						setText(null);
					}
				}

			};
		});
		this.ctlStoreEntryViewId.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
		this.ctlStoreEntryViewName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		this.ctlStoreEntryViewSerial.setCellValueFactory(new TreeItemPropertyValueFactory<>("serial"));
		this.ctlStoreEntryViewExpires.setCellValueFactory(new TreeItemPropertyValueFactory<>("expires"));

		ContextMenu detailsViewMenu = this.ctlDetailsView.getContextMenu();

		this.ctlDetailsView.setContextMenu(null);
		this.ctlDetailsView.setRowFactory(param -> {
			ContextMenu menu = detailsViewMenu;

			return new TreeTableRow<AttributeModel>() {

				@Override
				protected void updateItem(@Nullable AttributeModel item, boolean empty) {
					super.updateItem(item, empty);
					if (!empty) {
						setContextMenu(menu);
					} else {
						setContextMenu(null);
					}
				}

			};
		});
		this.ctlDetailsViewName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		this.ctlDetailsViewValue.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
		this.ctlStoreEntryView.getSelectionModel().selectedItemProperty()
				.addListener((p, o, n) -> onStoreViewSelectionChanged(n));
		Windows.onHiding(stage, (ScheduledFuture<?> f) -> f.cancel(true), getExecutorService().scheduleAtFixedRate(
				PlatformHelper.runLaterRunnable(() -> onUpdateHeapStatus()), 0, 500, TimeUnit.MILLISECONDS));
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
		try {
			this.storeProperty.set(UserCertStore.openStore(storeHome.toPath()));
			updateStoreEntryView();
			this.ctlStoreStatusLabel.setText(StoreI18N.formatSTR_TEXT_STORE_STATUS(storeHome));
		} catch (IOException e) {
			Alerts.unexpected(e).showAndWait();
		}
	}

	private @Nullable UserCertStoreEntry getSelectedStoreEntry() {
		TreeItem<StoreEntryModel> selectedItem = this.ctlStoreEntryView.getSelectionModel().getSelectedItem();

		return (selectedItem != null ? selectedItem.getValue().getEntry() : null);
	}

	private void updateStoreEntryView() {
		this.storeEntryViewHelper.get().update(this.storeProperty.get());
	}

	private void updateDetailsView(@Nullable TreeItem<StoreEntryModel> selection) {
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
		TreeItem<AttributeModel> attributeItem = new TreeItem<>(new AttributeModel(attribute));
		List<Attributes> attributeChildren = attribute.children();
		int childCount = attributeChildren.size();
		int childIndex = 0;

		for (Attributes child : attributeChildren) {
			if (childIndex >= DETAILS_VIEW_ATTRIBUTE_LIMIT) {
				attributeItem.getChildren().add(new TreeItem<>(
						new AttributeModel(StoreI18N.formatSTR_TEXT_DETAILS_OMITTED(childCount - childIndex))));
				break;
			}
			updateDetailsViewHelper(attributeItem, child, false);
			childIndex++;
		}
		parentItem.getChildren().add(attributeItem);
		attributeItem.setExpanded(expand);
	}

}
