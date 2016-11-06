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

import java.util.prefs.Preferences;

import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.security.KeyPairAlgorithm;
import de.carne.certmgr.certs.signer.CertSigners;
import de.carne.certmgr.certs.spi.CertSigner;
import de.carne.certmgr.jfx.resources.Images;
import de.carne.jfx.application.PlatformHelper;
import de.carne.jfx.stage.StageController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Certificate (CRT/CSR) creation dialog.
 */
public class CertOptionsController extends StageController {

	private final Preferences preferences = Preferences.systemNodeForPackage(CertOptionsController.class);

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
	ComboBox<String> ctlSigAlgOption;

	@FXML
	DatePicker ctlNotBeforeInput;

	@FXML
	DatePicker ctlNotAfterInput;

	@FXML
	ComboBox<UserCertStoreEntry> ctlIssuerInput;

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
	void onCmdSubmit(ActionEvent evt) {

	}

	@FXML
	void onCmdCancel(ActionEvent evt) {
		close(false);
	}

	@Override
	protected void setupStage(Stage stage) {
		stage.getIcons().addAll(PlatformHelper.stageIcons(Images.NEWCERT32, Images.NEWCERT16));
		stage.setTitle(CertOptionsI18N.formatSTR_STAGE_TITLE());
		setupKeyAlgOptions();
		setupSignerOptions();
	}

	private void setupKeyAlgOptions() {
		ObservableList<KeyPairAlgorithm> keyAlgOptions = this.ctlKeyAlgOption.getItems();

		keyAlgOptions.clear();
		keyAlgOptions.addAll(KeyPairAlgorithm.getAll(true));
		keyAlgOptions.sort((o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	private void setupKeySizeOptions() {

	}

	private void setupSignerOptions() {
		ObservableList<CertSigner> signerOptions = this.ctlSignerOption.getItems();

		signerOptions.clear();
		signerOptions.addAll(CertSigners.REGISTERED.providers());
		signerOptions.sort((o1, o2) -> o1.toString().compareTo(o2.toString()));
	}

	@Override
	protected Preferences getPreferences() {
		return this.preferences;
	}

}
