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
package de.carne.certmgr.jfx.passwordprompt;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.jfx.help.Help;
import de.carne.certmgr.jfx.help.HelpController;

/**
 * Dialog controller for password prompt display to ask for an existing password.
 */
public class PasswordPromptController extends AbstractPasswordPromptController {

	@FXML
	Label ctlPromptText;

	@FXML
	TextField ctlPasswordInput1;

	@FXML
	CheckBox ctlReusePasswordOption;

	@FXML
	ImageView ctlMessageIcon;

	@FXML
	Label ctlMessageText;

	@FXML
	void onOk(ActionEvent evt) {
		String passwordInput = this.ctlPasswordInput1.getText();

		setPasswordInput(passwordInput);
		setReusePasswordFlag(this.ctlReusePasswordOption.isSelected());
		getStage().close();
	}

	@FXML
	void onCancel(ActionEvent evt) {
		setPasswordInput(null);
		setReusePasswordFlag(false);
		getStage().close();
	}

	@FXML
	void onHelp(ActionEvent evt) {
		try {
			HelpController.showHelp(this, Help.TOPIC_PASSWORDPROMPT);
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	@FXML
	void onCancelAll(ActionEvent evt) {
		setPasswordInput(null);
		setReusePasswordFlag(true);
		getStage().close();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.setTitle(getBundle().getString(I18N.TEXT_PASSWORDTITLE));
		controllerStage.getIcons().addAll(Images.IMAGE_PRIVATECRT16, Images.IMAGE_PRIVATECRT32);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.passwordprompt.AbstractPasswordPromptController#beginPasswordPrompt(java.lang.String)
	 */
	@Override
	public void beginPasswordPrompt(String resource) {
		this.ctlPromptText.setText(I18N.format(I18N.MESSAGE_PASSWORDPROMPT, resource));
		getStage().sizeToScene();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.passwordprompt.AbstractPasswordPromptController#beginPasswordPrompt(java.lang.String,
	 * java.lang.Exception)
	 */
	@Override
	public void beginPasswordPrompt(String resource, Exception details) {
		this.ctlPromptText.setText(I18N.format(I18N.MESSAGE_PASSWORDPROMPT, resource));
		this.ctlMessageIcon.setImage(Images.IMAGE_WARNING16);
		this.ctlMessageText.setText(I18N.format(I18N.MESSAGE_INVALIDPASSWORD));
		getStage().sizeToScene();
	}

}
