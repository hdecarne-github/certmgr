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
package de.carne.certmgr.jfx.storemanager;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CRLReason;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.prefs.Preferences;

import de.carne.certmgr.jfx.ClipboardExportTarget;
import de.carne.certmgr.jfx.ImageViewTableCell;
import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.jfx.certexport.CertExportController;
import de.carne.certmgr.jfx.certimport.CertImportController;
import de.carne.certmgr.jfx.crloptions.CRLOptionsController;
import de.carne.certmgr.jfx.crtoptions.CRTOptionsController;
import de.carne.certmgr.jfx.entryoptions.EntryOptionsController;
import de.carne.certmgr.jfx.help.Help;
import de.carne.certmgr.jfx.help.HelpController;
import de.carne.certmgr.jfx.storeoptions.StoreOptionsController;
import de.carne.certmgr.store.CertStore;
import de.carne.certmgr.store.CertStoreEntry;
import de.carne.certmgr.store.PKCS10Object;
import de.carne.certmgr.store.asn1.OIDRegistry;
import de.carne.certmgr.store.x509.DistributionPoint;
import de.carne.certmgr.store.x509.DistributionPointName;
import de.carne.certmgr.store.x509.EncodedX509Extension;
import de.carne.certmgr.store.x509.ExtendedKeyUsage;
import de.carne.certmgr.store.x509.GeneralName;
import de.carne.certmgr.store.x509.KeyUsage;
import de.carne.certmgr.store.x509.RevokeReason;
import de.carne.certmgr.store.x509.X509BasicConstraintsExtension;
import de.carne.certmgr.store.x509.X509CRLDistributionPointsExtension;
import de.carne.certmgr.store.x509.X509ExtendedKeyUsageExtension;
import de.carne.certmgr.store.x509.X509Extension;
import de.carne.certmgr.store.x509.X509KeyUsageExtension;
import de.carne.certmgr.store.x509.X509SubjectAlternativeNameExtension;
import de.carne.jfx.StageController;
import de.carne.jfx.aboutinfo.AboutInfoController;
import de.carne.jfx.messagebox.MessageBoxResult;
import de.carne.jfx.messagebox.MessageBoxStyle;
import de.carne.util.Pair;
import de.carne.util.Strings;
import de.carne.util.logging.Log;
import de.carne.util.logging.LogBufferHandler;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Dialog controller for certificate store management dialog.
 */
public class StoreManagerController extends StageController {

	private static final Log LOG = new Log(StoreManagerController.class);

	private static final Preferences PREFERENCES = Preferences.systemNodeForPackage(StoreManagerController.class);

	private static final String PREF_INITIAL_DIRECTORY = "initialDirectory";

	private static final int MAX_CRL_REVOKE_ENTRY_COUNT = 5;

	private static final String OID_PKCS9_EXTENSION_REQUEST = "1.2.840.113549.1.9.14";

	private final Handler logHandler = new Handler() {

		@Override
		public void publish(LogRecord record) {
			onPublishLogRecord(record);
		}

		@Override
		public void flush() {
			// Nothing to do
		}

		@Override
		public void close() throws SecurityException {
			// Nothing to do
		}

	};

	private CertStore store = null;
	private HashMap<CertStoreEntry, TreeItem<StoreViewEntry>> storeViewItemMap = new HashMap<>();

	@FXML
	MenuItem ctlStoreOptionsMenuItem;

	@FXML
	Button ctlStoreOptionsButton;

	@FXML
	MenuItem ctlEntryOptionsMenuItem;

	@FXML
	Button ctlEntryOptionsButton;

	@FXML
	MenuItem ctlCopyEntryMenuItem;

	@FXML
	Button ctlCopyEntryButton;

	@FXML
	MenuItem ctlDeleteEntryMenuItem;

	@FXML
	Button ctlDeleteEntryButton;

	@FXML
	MenuItem ctlNewCRTMenuItem;

	@FXML
	Button ctlNewCRTButton;

	@FXML
	MenuItem ctlReSignCRTMenuItem;

	@FXML
	Button ctlReSignCRTButton;

	@FXML
	MenuItem ctlRevokeCRTMenuItem;

	@FXML
	Button ctlRevokeCRTButton;

	@FXML
	MenuItem ctlManageCRLMenuItem;

	@FXML
	Button ctlManageCRLButton;

	@FXML
	MenuItem ctlNewCSRMenuItem;

	@FXML
	Button ctlNewCSRButton;

	@FXML
	MenuItem ctlReSignCSRMenuItem;

	@FXML
	Button ctlReSignCSRButton;

	@FXML
	MenuItem ctlImportEntryMenuItem;

	@FXML
	Button ctlImportEntryButton;

	@FXML
	MenuItem ctlExportEntryMenuItem;

	@FXML
	Button ctlExportEntryButton;

	@FXML
	TreeView<StoreViewEntry> ctlStoreView;

	@FXML
	TreeTableView<StoreEntryAttributesModel> ctlEntryView;

	@FXML
	TreeTableColumn<StoreEntryAttributesModel, String> ctlEntryViewAttribute;

	@FXML
	TreeTableColumn<StoreEntryAttributesModel, String> ctlEntryViewValue;

	@FXML
	TableView<LogRecordModel> ctlLogView;

	@FXML
	TableColumn<LogRecordModel, Image> ctlLogViewLevel;

	@FXML
	TableColumn<LogRecordModel, String> ctlLogViewTime;

	@FXML
	TableColumn<LogRecordModel, String> ctlLogViewMessage;

	@FXML
	void onNewStoreAction(ActionEvent evt) {
		beginStoreOptions(true);
	}

	@FXML
	void onOpenStoreAction(ActionEvent evt) {
		DirectoryChooser storeChooser = new DirectoryChooser();
		File initalDirectoryPreference = getInitialDirectoryPreference();

		if (initalDirectoryPreference != null) {
			storeChooser.setInitialDirectory(initalDirectoryPreference);
		}

		File storeHomeFile = storeChooser.showDialog(getStage());

		if (storeHomeFile != null) {
			recordInitialDirectoryPreference(storeHomeFile.getParentFile());
			openStore(storeHomeFile.toString());
		}
	}

	@FXML
	void onStoreOptionsAction(ActionEvent evt) {
		beginStoreOptions(false);
	}

	@FXML
	void onEntryOptionsAction(ActionEvent evt) {
		StoreViewEntry selectedItemEntry = getSelectedItemEntry();

		if (selectedItemEntry != null && selectedItemEntry.getEntry() != null) {
			CertStoreEntry selectedEntry = selectedItemEntry.getEntry();

			try {
				EntryOptionsController entryOptionsController = openStage(EntryOptionsController.class);

				entryOptionsController.beginStoreOptions(selectedEntry, new EntryOptionsController.Result() {

					@Override
					public void onEntryUpdate(CertStoreEntry entryParam) {
						onCertUpdateResult(entryParam);
					}

				});
				entryOptionsController.getStage().show();
			} catch (IOException e) {
				reportUnexpectedException(e);
			}
		}
	}

	@FXML
	void onQuitAction(ActionEvent evt) {
		getStage().close();
	}

	@FXML
	void onCopyEntryAction(ActionEvent evt) {
		try {
			StoreViewEntry selectedItemEntry = getSelectedItemEntry();

			if (selectedItemEntry != null && selectedItemEntry.getEntry() != null) {
				selectedItemEntry.getEntry().copy(new ClipboardExportTarget());
			}
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	@FXML
	void onDeleteEntryAction(ActionEvent evt) {
		StoreViewEntry selectedItemEntry = getSelectedItemEntry();

		if (selectedItemEntry != null && selectedItemEntry.getEntry() != null) {
			CertStoreEntry selectedEntry = selectedItemEntry.getEntry();
			MessageBoxResult result = showMessageBox(I18N.formatSTR_CONFIRM_DELETE_MESSAGE(selectedEntry.getName()),
					null, MessageBoxStyle.ICON_QUESTION, MessageBoxStyle.BUTTON_YES_NO);

			if (MessageBoxResult.YES.equals(result)) {
				try {
					this.store.deleteEntry(selectedEntry);
					updateStoreView(false);
				} catch (IOException e) {
					reportUnexpectedException(e);
				}
			}
		}
	}

	@FXML
	void onNewCRTAction(ActionEvent evt) {
		if (this.store != null) {
			try {
				CRTOptionsController crtOptionsController = openStage(CRTOptionsController.class);

				crtOptionsController.beginCRTOptions(this.store, new CRTOptionsController.Result() {

					@Override
					public void onEntryGenerate(CertStoreEntry entryParam) {
						onCertGenerateResult(entryParam);
					}

				});

				StoreViewEntry selectedItemEntry = getSelectedItemEntry();

				if (selectedItemEntry != null && selectedItemEntry.getEntry() != null) {
					CertStoreEntry selectedEntry = selectedItemEntry.getEntry();

					if (selectedEntry.hasCRT() && selectedEntry.hasKey()) {
						crtOptionsController.selectIssuer(selectedEntry);
					}
				}
				crtOptionsController.getStage().show();
			} catch (IOException e) {
				reportUnexpectedException(e);
			}
		}
	}

	@FXML
	void onReSignCRTAction(ActionEvent evt) {
		StoreViewEntry selectedItemEntry = getSelectedItemEntry();

		if (selectedItemEntry != null && selectedItemEntry.getEntry() != null) {
			CertStoreEntry selectedEntry = selectedItemEntry.getEntry();

			try {
				CRTOptionsController crtOptionsController = openStage(CRTOptionsController.class);

				crtOptionsController.beginCRTOptions(selectedEntry, new CRTOptionsController.Result() {

					@Override
					public void onEntryGenerate(CertStoreEntry entryParam) {
						onCertGenerateResult(entryParam);
					}

				});
				crtOptionsController.getStage().show();
			} catch (IOException e) {
				reportUnexpectedException(e);
			}
		}
	}

	@FXML
	void onRevokeCRTAction(ActionEvent evt) {
		StoreViewEntry selectedItemEntry = getSelectedItemEntry();

		if (selectedItemEntry != null && selectedItemEntry.getEntry() != null) {
			CertStoreEntry selectedEntry = selectedItemEntry.getEntry();
			CertStoreEntry issuerEntry = selectedEntry.getStore().getEntry(selectedEntry.getIssuer());

			if (issuerEntry != null && issuerEntry.hasKey()) {
				try {
					CRLOptionsController crlOptionsController = openStage(CRLOptionsController.class);

					crlOptionsController.beginCRLOptions(issuerEntry, new CRLOptionsController.Result() {

						@Override
						public void onEntryUpdate(CertStoreEntry entryParam) {
							onCertUpdateResult(entryParam);
						}

					});
					crlOptionsController.revokeCert(selectedEntry, RevokeReason.PRIVILEGE_WITHDRAWN);
					crlOptionsController.getStage().show();
				} catch (IOException e) {
					reportUnexpectedException(e);
				}
			}
		}
	}

	@FXML
	void onManageCRLAction(ActionEvent evt) {
		StoreViewEntry selectedItemEntry = getSelectedItemEntry();

		if (selectedItemEntry != null && selectedItemEntry.getEntry() != null) {
			CertStoreEntry selectedEntry = selectedItemEntry.getEntry();

			try {
				CRLOptionsController crlOptionsController = openStage(CRLOptionsController.class);

				crlOptionsController.beginCRLOptions(selectedEntry, new CRLOptionsController.Result() {

					@Override
					public void onEntryUpdate(CertStoreEntry entryParam) {
						onCertUpdateResult(entryParam);
					}

				});
				crlOptionsController.getStage().show();
			} catch (IOException e) {
				reportUnexpectedException(e);
			}
		}
	}

	@FXML
	void onNewCSRAction(ActionEvent evt) {
		if (this.store != null) {
			try {
				CRTOptionsController crtOptionsController = openStage(CRTOptionsController.class);

				crtOptionsController.beginCSROptions(this.store, new CRTOptionsController.Result() {

					@Override
					public void onEntryGenerate(CertStoreEntry entryParam) {
						onCertGenerateResult(entryParam);
					}

				});
				crtOptionsController.getStage().show();
			} catch (IOException e) {
				reportUnexpectedException(e);
			}
		}
	}

	@FXML
	void onReSignCSRAction(ActionEvent evt) {
		StoreViewEntry selectedItemEntry = getSelectedItemEntry();

		if (selectedItemEntry != null && selectedItemEntry.getEntry() != null) {
			CertStoreEntry selectedEntry = selectedItemEntry.getEntry();

			try {
				CRTOptionsController crtOptionsController = openStage(CRTOptionsController.class);

				crtOptionsController.beginCSROptions(selectedEntry, new CRTOptionsController.Result() {

					@Override
					public void onEntryGenerate(CertStoreEntry entryParam) {
						onCertGenerateResult(entryParam);
					}

				});
				crtOptionsController.getStage().show();
			} catch (IOException e) {
				reportUnexpectedException(e);
			}
		}
	}

	@FXML
	void onImportEntryAction(ActionEvent evt) {
		try {
			CertImportController certImportController = openStage(CertImportController.class);

			certImportController.beginCertImport(this.store, new CertImportController.Result() {

				@Override
				public void onEntryImport(Collection<CertStoreEntry> importEntriesParam) {
					onCertImportResult(importEntriesParam);
				}

			});
			certImportController.getStage().show();
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	@FXML
	void onExportEntryAction(ActionEvent evt) {
		try {
			StoreViewEntry selectedItemEntry = getSelectedItemEntry();

			if (selectedItemEntry != null && selectedItemEntry.getEntry() != null) {
				CertExportController certExportController = openStage(CertExportController.class);

				certExportController.beginCertExport(this.store, selectedItemEntry.getEntry());
				certExportController.getStage().show();
			}
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	@FXML
	void onHelpAction(ActionEvent evt) {
		try {
			HelpController.showHelp(this, Help.TOPIC_STORE_MANAGER);
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	@FXML
	void onAboutAction(ActionEvent evt) {
		try {
			AboutInfoController aboutInfoController = openStage(AboutInfoController.class);

			aboutInfoController.setInfoIcon(Images.IMAGE_STORE32);
			aboutInfoController.addInfo(I18N.formatSTR_ABOUT_TITLE11(), I18N.formatSTR_ABOUT_INFO1());
			aboutInfoController.addInfo(I18N.formatSTR_ABOUT_TITLE12(), I18N.formatSTR_ABOUT_INFO2());
			aboutInfoController.addInfo(I18N.formatSTR_ABOUT_TITLE13(), I18N.formatSTR_ABOUT_INFO3());
			aboutInfoController.getStage().showAndWait();
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	void onPublishLogRecord(LogRecord record) {
		if (Platform.isFxApplicationThread()) {
			TableViewSelectionModel<LogRecordModel> selectionModel = this.ctlLogView.getSelectionModel();
			LogRecordModel selection = selectionModel.getSelectedItem();
			ObservableList<LogRecordModel> items = this.ctlLogView.getItems();
			boolean updateSelection = (selection == null || selection.equals(items.get(items.size() - 1)));

			items.add(new LogRecordModel(record));
			while (items.size() > LogBufferHandler.BUFFER_SIZE) {
				items.remove(0);
			}
			if (updateSelection) {
				selectionModel.selectLast();
				this.ctlLogView.scrollTo(selectionModel.getSelectedItem());
			}
		} else {
			Platform.runLater(new Runnable() {
				private LogRecord record2 = record;

				@Override
				public void run() {
					onPublishLogRecord(this.record2);
				}
			});
		}
	}

	void onStoreOptionsResult(CertStore storeParam) {
		if (storeParam != null && !storeParam.equals(this.store)) {
			this.store = storeParam;
			updateStoreView(true);
		}
	}

	void onCertUpdateResult(CertStoreEntry entry) {
		updateStoreView(false);
		updateEntryView(getSelectedItem());
	}

	void onCertGenerateResult(CertStoreEntry entry) {
		updateStoreView(false);
	}

	void onCertImportResult(Collection<CertStoreEntry> importEntries) {
		updateStoreView(false);
	}

	void onStageOpening() {
		LogBufferHandler logBuffer = LogBufferHandler.getHandler(StoreManagerController.LOG.getLogger());

		if (logBuffer != null) {
			logBuffer.addHandler(this.logHandler);
		}
	}

	void onStageClosing() {
		syncPreferences();

		this.ctlLogView.getItems().clear();

		LogBufferHandler logBuffer = LogBufferHandler.getHandler(StoreManagerController.LOG.getLogger());

		if (logBuffer != null) {
			logBuffer.removeHandler(this.logHandler);
		}
	}

	void onStoreViewSelectionChanged(TreeItem<StoreViewEntry> selectedItem) {
		disableStoreViewCommands(selectedItem);
		updateEntryView(selectedItem);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.setTitle(I18N.formatSTR_STORE_MANAGER_TITLE());
		controllerStage.getIcons().addAll(Images.IMAGE_STORE16, Images.IMAGE_STORE32);
		this.ctlLogViewLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
		this.ctlLogViewLevel.setCellFactory(ImageViewTableCell.forTableColumn());
		this.ctlLogViewTime.setCellValueFactory(new PropertyValueFactory<>("time"));
		this.ctlLogViewMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
		controllerStage.showingProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> property, Boolean oldValue, Boolean newValue) {
				if (newValue.booleanValue()) {
					onStageOpening();
				} else {
					onStageClosing();
				}
			}
		});
		this.ctlStoreView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.ctlStoreView.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<TreeItem<StoreViewEntry>>() {
					@Override
					public void changed(ObservableValue<? extends TreeItem<StoreViewEntry>> property,
							TreeItem<StoreViewEntry> oldValue, TreeItem<StoreViewEntry> newValue) {
						onStoreViewSelectionChanged(newValue);
					}
				});
		this.ctlEntryView.setPlaceholder(new ImageView(Images.IMAGE_UNKNOWN32));
		this.ctlEntryViewAttribute.setCellValueFactory(new TreeItemPropertyValueFactory<>("attribute"));
		this.ctlEntryViewValue.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
		onStoreViewSelectionChanged(this.ctlStoreView.getSelectionModel().getSelectedItem());
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#getPreferences()
	 */
	@Override
	protected Preferences getPreferences() {
		return PREFERENCES;
	}

	/**
	 * Open a certificate store.
	 *
	 * @param storeHome The directory to open the store from.
	 */
	public void openStore(String storeHome) {
		assert storeHome != null;

		try {
			this.store = CertStore.open(storeHome);
			updateStoreView(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private TreeItem<StoreViewEntry> getSelectedItem() {
		return this.ctlStoreView.getSelectionModel().getSelectedItem();
	}

	private StoreViewEntry getSelectedItemEntry() {
		TreeItem<StoreViewEntry> selectedItem = getSelectedItem();

		return (selectedItem != null ? selectedItem.getValue() : null);
	}

	private void updateStoreView(boolean clearSelection) {
		if (this.store != null) {
			TreeItem<StoreViewEntry> rootItem = this.ctlStoreView.getRoot();
			boolean resetRootItem = false;

			// Reset root if it has not yet been set or if the store changed
			if (rootItem == null || !rootItem.getValue().getStore().equals(this.store)) {
				this.storeViewItemMap.clear();

				StoreViewEntry rootEntry = new StoreViewEntry(this.store);

				rootItem = rootEntry.toTreeItem();
				this.storeViewItemMap.put(null, rootItem);
				resetRootItem = true;
			}

			// Update all siblings and collect the processed entries
			Set<CertStoreEntry> processedEntries = updateStoreViewHelper(rootItem, this.store.getRootEntries());

			// Remove view entry no longer available in the store (copy first to
			// avoid concurrent modifcation)
			ArrayList<CertStoreEntry> mappedEntries = new ArrayList<>(this.storeViewItemMap.keySet());

			for (CertStoreEntry mappedEntry : mappedEntries) {
				if (mappedEntry != null && !processedEntries.contains(mappedEntry)) {
					TreeItem<StoreViewEntry> removeEntry = this.storeViewItemMap.remove(mappedEntry);
					ObservableList<TreeItem<StoreViewEntry>> removeEntryParentChildren = removeEntry.getParent()
							.getChildren();

					removeEntryParentChildren.addAll(removeEntry.getChildren());
					removeEntryParentChildren.remove(removeEntry);
				}
			}

			// Finish root reset
			if (resetRootItem) {
				this.ctlStoreView.setRoot(rootItem);
				if (rootItem != null) {
					rootItem.setExpanded(true);
				}
			}
		} else {
			this.storeViewItemMap.clear();
			this.ctlStoreView.setRoot(null);
		}
		if (clearSelection) {
			this.ctlStoreView.getSelectionModel().clearSelection();
			onStoreViewSelectionChanged(this.ctlStoreView.getSelectionModel().getSelectedItem());
		}
	}

	private Set<CertStoreEntry> updateStoreViewHelper(TreeItem<StoreViewEntry> item,
			Collection<CertStoreEntry> storeEntries) {
		HashSet<CertStoreEntry> processedEntries = new HashSet<>(storeEntries.size());
		ObservableList<TreeItem<StoreViewEntry>> itemChildren = item.getChildren();

		for (CertStoreEntry storeEntry : storeEntries) {
			TreeItem<StoreViewEntry> itemChild = this.storeViewItemMap.get(storeEntry);
			boolean insertItemChild = false;

			if (itemChild == null) {
				// A new entry, simply add it to tree
				StoreViewEntry viewEntry = new StoreViewEntry(storeEntry);

				itemChild = viewEntry.toTreeItem();
				insertItemChild = true;
				this.storeViewItemMap.put(storeEntry, itemChild);
			} else {
				// An existing entry, update entry and parent relationship if
				// needed
				itemChild.getValue().updateTreeItem(itemChild);
				if (!itemChild.getParent().equals(item)) {
					itemChild.getParent().getChildren().remove(itemChild);
					insertItemChild = true;
				}
			}
			// (Re-)insert the item according to item's sort order
			if (insertItemChild) {
				int insertIndex = 0;

				for (TreeItem<StoreViewEntry> itemChild2 : itemChildren) {
					if (itemChild.getValue().compareTo(itemChild2.getValue()) >= 0) {
						break;
					}
					insertIndex++;
				}
				itemChildren.add(insertIndex, itemChild);
			}
			processedEntries.add(storeEntry);
			processedEntries.addAll(updateStoreViewHelper(itemChild, this.store.getIssuedEntries(storeEntry)));
		}
		return processedEntries;
	}

	private void disableStoreViewCommands(TreeItem<StoreViewEntry> selectedItem) {
		boolean noStoreOpened = this.store == null;
		boolean noInternalEntrySelected = true;
		boolean noRevokableSelected = true;
		boolean noPrivateCertSelected = true;
		boolean noCSRSelected = true;

		if (selectedItem != null) {
			CertStoreEntry selectedEntry = selectedItem.getValue().getEntry();

			noInternalEntrySelected = selectedEntry == null || selectedEntry.isExternal();
			noRevokableSelected = selectedEntry == null || !selectedEntry.hasCRT()
					|| !selectedEntry.getIssuer().hasKey();
			noPrivateCertSelected = selectedEntry == null || !selectedEntry.hasKey() || !selectedEntry.hasCRT();
			noCSRSelected = selectedEntry == null || !selectedEntry.hasCSR();
		}
		this.ctlStoreOptionsMenuItem.setDisable(noStoreOpened);
		this.ctlStoreOptionsButton.setDisable(noStoreOpened);
		this.ctlEntryOptionsMenuItem.setDisable(noInternalEntrySelected);
		this.ctlEntryOptionsButton.setDisable(noInternalEntrySelected);
		this.ctlCopyEntryMenuItem.setDisable(noInternalEntrySelected);
		this.ctlCopyEntryButton.setDisable(noInternalEntrySelected);
		this.ctlDeleteEntryMenuItem.setDisable(noInternalEntrySelected);
		this.ctlDeleteEntryButton.setDisable(noInternalEntrySelected);
		this.ctlNewCRTMenuItem.setDisable(noStoreOpened);
		this.ctlNewCRTButton.setDisable(noStoreOpened);
		this.ctlReSignCRTMenuItem.setDisable(noRevokableSelected);
		this.ctlReSignCRTButton.setDisable(noRevokableSelected);
		this.ctlRevokeCRTMenuItem.setDisable(noRevokableSelected);
		this.ctlRevokeCRTButton.setDisable(noRevokableSelected);
		this.ctlManageCRLMenuItem.setDisable(noPrivateCertSelected);
		this.ctlManageCRLButton.setDisable(noPrivateCertSelected);
		this.ctlNewCSRMenuItem.setDisable(noStoreOpened);
		this.ctlNewCSRButton.setDisable(noStoreOpened);
		this.ctlReSignCSRMenuItem.setDisable(noCSRSelected);
		this.ctlReSignCSRButton.setDisable(noCSRSelected);
		this.ctlImportEntryMenuItem.setDisable(noStoreOpened);
		this.ctlImportEntryButton.setDisable(noStoreOpened);
		this.ctlExportEntryMenuItem.setDisable(noInternalEntrySelected);
		this.ctlExportEntryButton.setDisable(noInternalEntrySelected);
	}

	private void updateEntryView(TreeItem<StoreViewEntry> selectedItem) {
		TreeItem<StoreEntryAttributesModel> rootItem = new TreeItem<>(new StoreEntryAttributesModel(null));

		this.ctlEntryView.setRoot(rootItem);
		if (selectedItem != null) {
			StoreViewEntry selectedEntry = selectedItem.getValue();
			CertStoreEntry selectedStoreEntry = selectedEntry.getEntry();

			if (selectedStoreEntry != null) {
				TreeItem<StoreEntryAttributesModel> entryItem = addEntryViewNode(rootItem, I18N.formatSTR_CERT_ENTRY());

				addEntrViewValues(entryItem, getEntryInfo(new ArrayList<>(), selectedStoreEntry));
				try {
					if (selectedStoreEntry.hasCRT()) {
						TreeItem<StoreEntryAttributesModel> crtItem = addEntryViewNode(rootItem,
								I18N.formatSTR_CRT_OBJECT());
						X509Certificate crt = selectedStoreEntry.getCRT().getObject();

						addEntrViewValues(crtItem, getCRTInfo(new ArrayList<>(), crt));
						updateEntryViewExtensions(crtItem, CertStore.decodeCRTExtensions(crt));
					}
					if (selectedStoreEntry.hasCSR()) {
						TreeItem<StoreEntryAttributesModel> csrItem = addEntryViewNode(rootItem,
								I18N.formatSTR_CSR_OBJECT());
						PKCS10Object csr = selectedStoreEntry.getCSR().getObject();

						addEntrViewValues(csrItem, getCSRInfo(new ArrayList<>(), csr));
						updateEntryViewAttributes(csrItem, csr);
					}
					if (selectedStoreEntry.hasCRL()) {
						TreeItem<StoreEntryAttributesModel> crlItem = addEntryViewNode(rootItem,
								I18N.formatSTR_CRL_OBJECT());
						X509CRL crl = selectedStoreEntry.getCRL().getObject();

						addEntrViewValues(crlItem, getCRLInfo(new ArrayList<>(), crl));
						updateEntryViewExtensions(crlItem, CertStore.decodeCRLExtensions(crl));
					}
				} catch (IOException e) {
					LOG.warning(e, I18N.BUNDLE, I18N.STR_ENTRY_ERROR_MESSAGE, selectedStoreEntry.getAlias(),
							e.getLocalizedMessage());
				}
			} else {
				TreeItem<StoreEntryAttributesModel> storeItem = addEntryViewNode(rootItem, I18N.formatSTR_CERT_STORE());

				addEntrViewValues(storeItem, getStoreInfo(new ArrayList<>()));
			}
		}
	}

	private void updateEntryViewExtensions(TreeItem<StoreEntryAttributesModel> parentItem,
			Collection<EncodedX509Extension> extensions) {
		for (EncodedX509Extension extension : extensions) {
			String nodeAttribute = I18N.formatSTR_EXT_OBJECT(OIDRegistry.get(extension.getOID()));
			String nodeValue = (extension.isCritical() ? "critical" : "non-critical");
			TreeItem<StoreEntryAttributesModel> extensionItem = addEntryViewNode(parentItem, nodeAttribute, nodeValue);
			X509Extension decoded = extension.getDecoded();

			if (decoded instanceof X509BasicConstraintsExtension) {
				X509BasicConstraintsExtension decodedExtension = (X509BasicConstraintsExtension) decoded;

				addEntrViewValues(extensionItem, getBasicConstraintsExtensionInfo(new ArrayList<>(), decodedExtension));
			} else if (decoded instanceof X509KeyUsageExtension) {
				X509KeyUsageExtension decodedExtension = (X509KeyUsageExtension) decoded;

				addEntrViewValues(extensionItem, getKeyUsageExtensionInfo(new ArrayList<>(), decodedExtension));
			} else if (decoded instanceof X509ExtendedKeyUsageExtension) {
				X509ExtendedKeyUsageExtension decodedExtension = (X509ExtendedKeyUsageExtension) decoded;

				addEntrViewValues(extensionItem, getExtendedKeyUsageExtensionInfo(new ArrayList<>(), decodedExtension));
			} else if (decoded instanceof X509SubjectAlternativeNameExtension) {
				X509SubjectAlternativeNameExtension decodedExtension = (X509SubjectAlternativeNameExtension) decoded;

				addEntrViewValues(extensionItem,
						getSubjectAlternativeNameExtensionInfo(new ArrayList<>(), decodedExtension));
			} else if (decoded instanceof X509CRLDistributionPointsExtension) {
				X509CRLDistributionPointsExtension decodedExtension = (X509CRLDistributionPointsExtension) decoded;

				updateEntryViewCRLDistributionPointExtensions(extensionItem, decodedExtension);
			} else {
				addEntrViewValues(extensionItem, getEncodedDataInfo(new ArrayList<>(), extension.getEncoded()));
			}
		}
	}

	private void updateEntryViewCRLDistributionPointExtensions(TreeItem<StoreEntryAttributesModel> parentItem,
			X509CRLDistributionPointsExtension extension) {
		int distributionPointIndex = 0;

		for (DistributionPoint distributionPoint : extension.getDistributionPoints()) {
			TreeItem<StoreEntryAttributesModel> distributionPointItem = addEntryViewNode(parentItem,
					"DistributionPoint[" + distributionPointIndex + "]");
			int distributionPointNameIndex = 0;

			for (DistributionPointName distributionPointName : distributionPoint.getNames()) {
				TreeItem<StoreEntryAttributesModel> distributionPointNameItem = addEntryViewNode(distributionPointItem,
						"DistributionPointName[" + distributionPointNameIndex + "]");

				addEntrViewValues(distributionPointNameItem,
						getCRLDistributionPointNameInfo(new ArrayList<>(), distributionPointName));
				distributionPointNameIndex++;
			}

			ArrayList<Pair<String, String>> distributionPointInfo = new ArrayList<>();

			getCRLDistributionPointReasonInfo(distributionPointInfo, distributionPoint.getReasons());
			getDistributionPointCRLIssuerInfo(distributionPointInfo, distributionPoint.getCRLIssuers());
			addEntrViewValues(distributionPointItem, distributionPointInfo);
			distributionPointIndex++;
		}
	}

	private void updateEntryViewAttributes(TreeItem<StoreEntryAttributesModel> parentItem, PKCS10Object csr)
			throws IOException {
		Set<String> attributeOIDs = csr.getAttributeOIDs();

		for (String attributeOID : attributeOIDs) {
			TreeItem<StoreEntryAttributesModel> attributeItem = addEntryViewNode(parentItem,
					"Attribute " + OIDRegistry.get(attributeOID));

			if (OID_PKCS9_EXTENSION_REQUEST.equals(attributeOID)) {
				updateEntryViewExtensions(attributeItem, CertStore.decodeCSRExtensions(csr));
			} else {
				addEntrViewValues(attributeItem,
						getEncodedDataInfo(new ArrayList<>(), csr.getAttributeValues(attributeOID)));
			}
		}
	}

	private TreeItem<StoreEntryAttributesModel> addEntryViewNode(TreeItem<StoreEntryAttributesModel> parentItem,
			String name) {
		return addEntryViewNode(parentItem, name, null);
	}

	private TreeItem<StoreEntryAttributesModel> addEntryViewNode(TreeItem<StoreEntryAttributesModel> parentItem,
			String attribute, String value) {
		StoreEntryAttributesModel nodeModel = new StoreEntryAttributesModel(attribute, value);
		TreeItem<StoreEntryAttributesModel> nodeItem = new TreeItem<>(nodeModel);

		parentItem.getChildren().add(nodeItem);
		if (parentItem.getParent() == null) {
			nodeItem.setExpanded(true);
		}
		return nodeItem;
	}

	private void addEntrViewValues(TreeItem<StoreEntryAttributesModel> parentItem,
			Collection<Pair<String, String>> values) {
		for (Pair<String, String> value : values) {
			StoreEntryAttributesModel valueModel = new StoreEntryAttributesModel(value.getFirst(), value.getSecond());
			TreeItem<StoreEntryAttributesModel> valueItem = new TreeItem<>(valueModel);

			parentItem.getChildren().add(valueItem);
		}
	}

	private void beginStoreOptions(boolean newStore) {
		try {
			StoreOptionsController storeOptionsController = openStage(StoreOptionsController.class);

			storeOptionsController.beginStoreOptions((newStore ? null : this.store),
					new StoreOptionsController.Result() {

						@Override
						public void onStoreOptions(CertStore storeParam) {
							onStoreOptionsResult(storeParam);
						}

					});
			storeOptionsController.getStage().show();
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	private List<Pair<String, String>> getStoreInfo(List<Pair<String, String>> info) {
		info.add(new Pair<>("Home", this.store.getHome().toString()));
		info.add(new Pair<>("Entries", Integer.toString(this.store.getEntryCount())));
		return info;
	}

	private Collection<Pair<String, String>> getEntryInfo(Collection<Pair<String, String>> info, CertStoreEntry entry) {
		info.add(new Pair<>("Name", entry.getName()));
		info.add(new Pair<>("Alias", entry.getAlias()));
		return info;
	}

	private Collection<Pair<String, String>> getCRTInfo(Collection<Pair<String, String>> info, X509Certificate crt) {
		info.add(new Pair<>("Version", String.valueOf(crt.getVersion())));
		info.add(new Pair<>("Serial number", String.valueOf(crt.getSerialNumber())));
		info.add(new Pair<>("Signature algorithm", crt.getSigAlgName()));
		info.add(new Pair<>("Issuer", String.valueOf(crt.getIssuerX500Principal())));
		info.add(new Pair<>("Valid from", String.valueOf(crt.getNotBefore())));
		info.add(new Pair<>("Valid to", String.valueOf(crt.getNotAfter())));
		info.add(new Pair<>("Subject", String.valueOf(crt.getSubjectX500Principal())));
		getPublicKeyInfo(info, crt.getPublicKey());
		return info;
	}

	private Collection<Pair<String, String>> getCSRInfo(Collection<Pair<String, String>> info, PKCS10Object csr) {
		info.add(new Pair<>("Signature algorithm", csr.getSigAlgName()));
		info.add(new Pair<>("Subject", String.valueOf(csr.getSubjectX500Principal())));
		getPublicKeyInfo(info, csr.getPublicKey());
		return info;
	}

	private Collection<Pair<String, String>> getCRLInfo(Collection<Pair<String, String>> info, X509CRL crl) {
		info.add(new Pair<>("Effective date", String.valueOf(crl.getThisUpdate())));
		info.add(new Pair<>("Next update", String.valueOf(crl.getNextUpdate())));
		info.add(new Pair<>("Signature algorithm", crl.getSigAlgName()));

		Set<? extends X509CRLEntry> revokedEntries = crl.getRevokedCertificates();

		if (revokedEntries != null) {
			int revokedEntryCount = 0;

			for (X509CRLEntry revokedEntry : revokedEntries) {
				revokedEntryCount++;
				if (revokedEntryCount <= MAX_CRL_REVOKE_ENTRY_COUNT) {
					String key = "Revoke #" + revokedEntry.getSerialNumber();
					CRLReason crlReason = revokedEntry.getRevocationReason();
					String value;

					if (crlReason != null) {
						RevokeReason reason = RevokeReason.valueOf(crlReason.ordinal());

						value = revokedEntry.getRevocationDate() + " (" + reason + ")";
					} else {
						value = revokedEntry.getRevocationDate().toString();
					}
					info.add(new Pair<>(key, value));
				}
			}
			if (revokedEntryCount > MAX_CRL_REVOKE_ENTRY_COUNT) {
				info.add(new Pair<>("<" + (revokedEntryCount - MAX_CRL_REVOKE_ENTRY_COUNT) + " additional entries>",
						"..."));
			}
		}
		return info;
	}

	private static Collection<Pair<String, String>> getPublicKeyInfo(Collection<Pair<String, String>> info,
			PublicKey publicKey) {
		if (publicKey != null) {
			info.add(new Pair<>("Public key", publicKey.getAlgorithm()));
		}
		return info;
	}

	private static Collection<Pair<String, String>> getBasicConstraintsExtensionInfo(
			Collection<Pair<String, String>> info, X509BasicConstraintsExtension extension) {
		info.add(new Pair<>("CA", Boolean.valueOf(extension.isCA()).toString()));
		info.add(new Pair<>("pathLenConstraint", Integer.toString(extension.getPathLenConstraint())));
		return info;
	}

	private static Collection<Pair<String, String>> getKeyUsageExtensionInfo(Collection<Pair<String, String>> info,
			X509KeyUsageExtension extension) {
		int usageIndex = 0;

		for (KeyUsage usage : extension.getUsages()) {
			info.add(new Pair<>("KeyUsage[" + usageIndex + "]", usage.toString()));
			usageIndex++;
		}
		return info;
	}

	private static Collection<Pair<String, String>> getExtendedKeyUsageExtensionInfo(
			Collection<Pair<String, String>> info, X509ExtendedKeyUsageExtension extension) {
		int usageIndex = 0;

		for (ExtendedKeyUsage usage : extension.getUsages()) {
			info.add(new Pair<>("ExtendedKeyUsage[" + usageIndex + "]", usage.toString()));
			usageIndex++;
		}
		return info;
	}

	private static Collection<Pair<String, String>> getSubjectAlternativeNameExtensionInfo(
			Collection<Pair<String, String>> info, X509SubjectAlternativeNameExtension extension) {
		return getGeneralNamesInfo(info, extension.getNames());
	}

	private static Collection<Pair<String, String>> getCRLDistributionPointNameInfo(
			Collection<Pair<String, String>> info, DistributionPointName distributionPointName) {
		return getGeneralNamesInfo(info, distributionPointName.getNames());
	}

	private static Collection<Pair<String, String>> getCRLDistributionPointReasonInfo(
			Collection<Pair<String, String>> info, RevokeReason[] reasons) {
		int reasonIndex = 0;

		for (RevokeReason reason : reasons) {
			info.add(new Pair<>("RevokeReason[" + reasonIndex + "]", reason.toString()));
			reasonIndex++;
		}
		return info;
	}

	private static Collection<Pair<String, String>> getDistributionPointCRLIssuerInfo(
			Collection<Pair<String, String>> info, Collection<GeneralName> crlIssuers) {
		return getGeneralNamesInfo(info, crlIssuers, "CRLIssuer");
	}

	private static Collection<Pair<String, String>> getGeneralNamesInfo(Collection<Pair<String, String>> info,
			Collection<GeneralName> names) {
		return getGeneralNamesInfo(info, names, "Name");
	}

	private static Collection<Pair<String, String>> getGeneralNamesInfo(Collection<Pair<String, String>> info,
			Collection<GeneralName> names, String attributeName) {
		int nameIndex = 0;

		for (GeneralName name : names) {
			info.add(new Pair<>(attributeName + "[" + nameIndex + "]", name.toString()));
			nameIndex++;
		}
		return info;
	}

	private static Collection<Pair<String, String>> getEncodedDataInfo(Collection<Pair<String, String>> info,
			byte[] encoded) {
		info.add(new Pair<>("Encoded Data", byteValueString(encoded)));
		return info;
	}

	private static Collection<Pair<String, String>> getEncodedDataInfo(Collection<Pair<String, String>> info,
			byte[][] encodeds) {
		int encodedIndex = 0;

		for (byte[] encoded : encodeds) {
			info.add(new Pair<>("Encoded Data[" + encodedIndex + "]", byteValueString(encoded)));
			encodedIndex++;
		}
		return info;
	}

	private static String byteValueString(byte[] data) {
		StringBuilder stringBuffer = new StringBuilder();

		stringBuffer.append("[");

		int dataIndex = 0;

		for (byte b : data) {
			if (dataIndex > 64) {
				stringBuffer.append(" ...");
				break;
			} else if (dataIndex > 0) {
				stringBuffer.append(" ");
			}
			stringBuffer.append(String.format("%02X", b));
			dataIndex++;
		}
		stringBuffer.append("]");
		return stringBuffer.toString();
	}

	private File getInitialDirectoryPreference() {
		File initialDirectory = null;
		String initialDirectoryPref = PREFERENCES.get(PREF_INITIAL_DIRECTORY, null);

		if (Strings.notEmpty(initialDirectoryPref)) {
			File initialDirectoryPrefFile = new File(initialDirectoryPref);

			if (initialDirectoryPrefFile.isDirectory()) {
				initialDirectory = initialDirectoryPrefFile;
			}
		}
		return initialDirectory;
	}

	private void recordInitialDirectoryPreference(File initialDirectory) {
		PREFERENCES.put(PREF_INITIAL_DIRECTORY, (initialDirectory != null ? initialDirectory.getPath() : ""));
	}

}
