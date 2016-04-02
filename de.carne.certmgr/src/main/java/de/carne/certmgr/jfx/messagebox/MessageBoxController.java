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
package de.carne.certmgr.jfx.messagebox;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.jfx.StageController;

/**
 * Dialog controller for message box display.
 */
public class MessageBoxController extends StageController {

	private MessageBoxResult resultButton1 = MessageBoxResult.NONE;
	private MessageBoxResult resultButton2 = MessageBoxResult.NONE;
	private MessageBoxResult resultButton3 = MessageBoxResult.NONE;
	private MessageBoxResult result = MessageBoxResult.NONE;

	@FXML
	ImageView ctlIcon;

	@FXML
	Label ctlMessage;

	@FXML
	Label ctlDetailsLabel;

	@FXML
	TextArea ctlDetails;

	@FXML
	ToggleButton ctlButtonDetails;

	@FXML
	Button ctlButtonCmd1;

	@FXML
	Button ctlButtonCmd2;

	@FXML
	Button ctlButtonCmd3;

	@FXML
	void onToggleDetails(ActionEvent evt) {
		boolean toggledVisibleState = !this.ctlDetails.isVisible();

		this.ctlDetailsLabel.setVisible(toggledVisibleState);
		this.ctlDetails.setVisible(toggledVisibleState);
		getStage().sizeToScene();
	}

	@FXML
	void onButton1(ActionEvent evt) {
		this.result = this.resultButton1;
		getStage().close();
	}

	@FXML
	void onButton2(ActionEvent evt) {
		this.result = this.resultButton2;
		getStage().close();
	}

	@FXML
	void onButton3(ActionEvent evt) {
		this.result = this.resultButton3;
		getStage().close();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.setTitle(getBundle().getString(I18N.TEXT_TITLE));
		this.ctlDetailsLabel.managedProperty().bind(this.ctlDetailsLabel.visibleProperty());
		this.ctlDetails.managedProperty().bind(this.ctlDetails.visibleProperty());
	}

	/**
	 * Begin message display.
	 *
	 * @param message The message to display.
	 * @param details The (optional) exception causing the message.
	 * @param styles The message box style to use.
	 */
	public void beginMessageBox(String message, Throwable details, MessageBoxStyle... styles) {
		this.ctlMessage.setText(message);
		this.ctlDetailsLabel.setVisible(false);
		this.ctlDetails.setVisible(false);

		String detailsString = formatDetails(details);

		if (detailsString != null) {
			this.ctlDetails.setText(detailsString);
		} else {
			this.ctlButtonDetails.setVisible(false);
		}
		for (MessageBoxStyle style : styles) {
			switch (style) {
			case ICON_INFO:
				getStage().getIcons().addAll(Images.IMAGE_INFO32, Images.IMAGE_INFO16);
				this.ctlIcon.setImage(Images.IMAGE_INFO32);
				break;
			case ICON_WARNING:
				getStage().getIcons().addAll(Images.IMAGE_WARNING32, Images.IMAGE_WARNING16);
				this.ctlIcon.setImage(Images.IMAGE_WARNING32);
				break;
			case ICON_ERROR:
				getStage().getIcons().addAll(Images.IMAGE_ERROR32, Images.IMAGE_ERROR16);
				this.ctlIcon.setImage(Images.IMAGE_ERROR32);
				break;
			case ICON_QUESTION:
				getStage().getIcons().addAll(Images.IMAGE_QUESTION32, Images.IMAGE_QUESTION16);
				this.ctlIcon.setImage(Images.IMAGE_QUESTION32);
				break;
			case BUTTON_OK:
				this.ctlButtonCmd1.setText(I18N.format(I18N.TEXT_BUTTON_OK));
				this.ctlButtonCmd1.setDefaultButton(true);
				this.resultButton1 = MessageBoxResult.OK;
				this.ctlButtonCmd2.setVisible(false);
				this.ctlButtonCmd3.setVisible(false);
				break;
			case BUTTON_OK_CANCEL:
				this.ctlButtonCmd1.setText(I18N.format(I18N.TEXT_BUTTON_CANCEL));
				this.ctlButtonCmd1.setCancelButton(true);
				this.resultButton1 = MessageBoxResult.CANCEL;
				this.ctlButtonCmd2.setText(I18N.format(I18N.TEXT_BUTTON_OK));
				this.ctlButtonCmd2.setDefaultButton(true);
				this.resultButton2 = MessageBoxResult.OK;
				this.ctlButtonCmd3.setVisible(false);
				break;
			case BUTTON_YES_NO:
				this.ctlButtonCmd1.setText(I18N.format(I18N.TEXT_BUTTON_NO));
				this.ctlButtonCmd1.setCancelButton(true);
				this.resultButton1 = MessageBoxResult.NO;
				this.ctlButtonCmd2.setText(I18N.format(I18N.TEXT_BUTTON_YES));
				this.ctlButtonCmd2.setDefaultButton(true);
				this.resultButton2 = MessageBoxResult.YES;
				this.ctlButtonCmd3.setVisible(false);
				break;
			case BUTTON_YES_NO_CANCEL:
				this.ctlButtonCmd1.setText(I18N.format(I18N.TEXT_BUTTON_CANCEL));
				this.ctlButtonCmd1.setCancelButton(true);
				this.resultButton1 = MessageBoxResult.CANCEL;
				this.ctlButtonCmd2.setText(I18N.format(I18N.TEXT_BUTTON_NO));
				this.resultButton2 = MessageBoxResult.NO;
				this.ctlButtonCmd3.setText(I18N.format(I18N.TEXT_BUTTON_YES));
				this.resultButton3 = MessageBoxResult.YES;
				break;
			default:
				throw new RuntimeException("Unexpected style: " + style);
			}
		}
		getStage().sizeToScene();
	}

	/**
	 * Get the message box result.
	 *
	 * @return The message box result.
	 */
	public MessageBoxResult getResult() {
		return this.result;
	}

	private String formatDetails(Throwable details) {
		String detailsString = null;

		if (details != null) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);

			details.printStackTrace(printWriter);
			detailsString = stringWriter.toString();
		}
		return detailsString;
	}

}
