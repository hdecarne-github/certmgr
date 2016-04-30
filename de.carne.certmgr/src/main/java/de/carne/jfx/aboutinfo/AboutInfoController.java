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
package de.carne.jfx.aboutinfo;

import java.io.IOException;

import de.carne.jfx.StageController;
import de.carne.util.Version;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Dialog controller for about info display.
 */
public class AboutInfoController extends StageController {

	private int nextInfoIndex = 0;

	@FXML
	ImageView ctlInfoIcon;

	@FXML
	Label ctlInfoString;

	@FXML
	Accordion ctlInfos;

	@FXML
	TitledPane ctlMainInfoPane;

	@FXML
	TextArea ctlMainInfoText;

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
		controllerStage.setTitle(I18N.formatSTR_ABOUTINFO_TITLE(Version.PROJECT_ID));

		Window owner = controllerStage.getOwner();

		if (owner instanceof Stage) {
			controllerStage.getIcons().addAll(((Stage) owner).getIcons());
		}
		this.ctlInfoString.setText(I18N.formatSTR_ABOUTINFO_VERSION(Version.PROJECT_NAME, Version.PROJECT_ID,
				Version.BUILD_VERSION, Version.BUILD_DATE));
		this.ctlInfos.setExpandedPane(this.ctlMainInfoPane);
	}

	/**
	 * Set the info icon to display.
	 *
	 * @param image The info icon to display.
	 */
	public void setInfoIcon(Image image) {
		this.ctlInfoIcon.setImage(image);
	}

	/**
	 * Add a info text and title to the info display.
	 *
	 * @param title The title to add.
	 * @param info The info text to add.
	 */
	public void addInfo(String title, String info) {
		if (this.nextInfoIndex == 0) {
			this.ctlMainInfoPane.setText(title);
			this.ctlMainInfoText.setText(info);
		} else {
			TextArea infoText = new TextArea(info);
			TitledPane infoPane = new TitledPane(title, infoText);

			this.ctlInfos.getPanes().add(infoPane);
		}
		this.nextInfoIndex++;
	}

}
