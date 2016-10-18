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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.jfx.certimport.CertImportController;
import de.carne.certmgr.jfx.resources.Images;
import de.carne.jfx.application.PlatformHelper;
import de.carne.jfx.stage.StageController;
import de.carne.jfx.stage.Windows;
import de.carne.text.MemUnitFormat;
import de.carne.util.prefs.DirectoryPreference;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Application main dialog for store management.
 */
public class StoreController extends StageController {

	private final Preferences preferences = Preferences.systemNodeForPackage(StoreController.class);

	private final DirectoryPreference preferenceInitalDirectory = new DirectoryPreference(this.preferences,
			"initialDirectory", true);

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
	void onCmdAbout(ActionEvent evt) {

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
		Windows.onHiding(stage, (ScheduledFuture<?> f) -> f.cancel(true), getExecutorService().scheduleAtFixedRate(
				PlatformHelper.runLaterRunnable(() -> updateHeapStatus()), 0, 500, TimeUnit.MILLISECONDS));
	}

	@Override
	protected Preferences getPreferences() {
		return this.preferences;
	}

	public void openStore(File storeHome) {
		assert storeHome != null;

		try {
			UserCertStore store = UserCertStore.openStore(storeHome.toPath());

			this.ctlStoreStatusLabel.setText(StoreI18N.formatSTR_STORE_STATUS(store.storeHome()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
