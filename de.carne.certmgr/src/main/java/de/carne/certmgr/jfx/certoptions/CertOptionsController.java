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

import de.carne.certmgr.jfx.resources.Images;
import de.carne.jfx.application.PlatformHelper;
import de.carne.jfx.stage.StageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Certificate (CRT/CSR) creation dialog.
 */
public class CertOptionsController extends StageController {

	private final Preferences preferences = Preferences.systemNodeForPackage(CertOptionsController.class);

	@FXML
	Label ctlStatusMessage;

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
	}

	@Override
	protected Preferences getPreferences() {
		return this.preferences;
	}

}
