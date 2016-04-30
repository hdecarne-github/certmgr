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

import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.jfx.InvalidInputException;
import de.carne.certmgr.jfx.help.Help;
import de.carne.certmgr.jfx.help.HelpController;
import de.carne.util.Strings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Dialog controller for password prompt display to ask for a new password.
 */
public class NewPasswordPromptController extends AbstractPasswordPromptController {

	@FXML
	Label ctlPromptText;

	@FXML
	TextField ctlPasswordInput1;

	@FXML
	TextField ctlPasswordInput2;

	@FXML
	ImageView ctlMessageIcon;

	@FXML
	Label ctlMessageText;

	@FXML
	void onOk(ActionEvent evt) {
		try {
			String passwordInput = validateAndGetPasswordInput();

			setPasswordInput(passwordInput);
			getStage().close();
		} catch (InvalidInputException e) {
			this.ctlMessageIcon.setImage(Images.IMAGE_WARNING16);
			this.ctlMessageText.setText(e.getLocalizedMessage());
		}
	}

	@FXML
	void onCancel(ActionEvent evt) {
		setPasswordInput(null);
		getStage().close();
	}

	@FXML
	void onHelp(ActionEvent evt) {
		try {
			HelpController.showHelp(this, Help.TOPIC_NEW_PASSWORD_PROMPT);
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.setTitle(I18N.formatSTR_NEW_PASSWORD_PROMPT_TITLE());
		controllerStage.getIcons().addAll(Images.IMAGE_PRIVATECRT16, Images.IMAGE_PRIVATECRT32);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.certmgr.jfx.passwordprompt.AbstractPasswordPromptController#
	 * beginPasswordPrompt(java.lang.String)
	 */
	@Override
	public void beginPasswordPrompt(String resource) {
		this.ctlPromptText.setText(I18N.formatSTR_NEW_PASSWORD_PROMPT_TITLE(resource));
		getStage().sizeToScene();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.certmgr.jfx.passwordprompt.AbstractPasswordPromptController#
	 * beginPasswordPrompt(java.lang.String, java.lang.Exception)
	 */
	@Override
	public void beginPasswordPrompt(String resource, Exception details) {
		this.ctlPromptText.setText(I18N.formatSTR_NEW_PASSWORD_PROMPT_TITLE(resource));
		this.ctlMessageIcon.setImage(Images.IMAGE_WARNING16);
		this.ctlMessageText.setText(I18N.formatSTR_WRONG_PASSWORD_MESSAGE());
		getStage().sizeToScene();
	}

	private String validateAndGetPasswordInput() throws InvalidInputException {
		String passwordInput1 = this.ctlPasswordInput1.getText();

		if (Strings.isEmpty(passwordInput1) || !passwordInput1.equals(passwordInput1.trim())) {
			throw new InvalidInputException(I18N.formatSTR_INVALID_PASSWORD_MESSAGE());
		}

		String passwordInput2 = this.ctlPasswordInput2.getText();

		if (!passwordInput1.equals(passwordInput2)) {
			throw new InvalidInputException(I18N.formatSTR_PASSWORD_MISMATCH_MESSAGE());
		}
		return passwordInput1;
	}

}
