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
package de.carne.certmgr.jfx.aboutinfo;

import java.io.IOException;

import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.jfx.StageController;
import de.carne.util.Version;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Dialog controller for about info display.
 */
public class AboutInfoController extends StageController {

	@FXML
	Label ctlInfoString;

	@FXML
	Accordion ctlCopyrights;

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#getResizable()
	 */
	@Override
	protected boolean getResizable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.setTitle(I18N.format(I18N.TEXT_TITLE));
		controllerStage.getIcons().addAll(Images.IMAGE_INFO16, Images.IMAGE_INFO32);
		this.ctlInfoString.setText(I18N.format(I18N.TEXT_INFO, Version.TITLE, Version.VERSION, Version.DATE));
		this.ctlCopyrights.setExpandedPane(this.ctlCopyrights.getPanes().get(0));
	}
}
