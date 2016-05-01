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
package de.carne.jfx.messagebox;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.carne.jfx.StageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Dialog controller for message box display.
 */
public class MessageBoxController extends StageController {

	private static final HashMap<MessageBoxStyle, List<Image>> IMAGE_REGISTRY = new HashMap<>();

	private static double getImageSpace(Image image) {
		return image.getWidth() * image.getHeight();
	}

	/**
	 * Register message box style images.
	 *
	 * @param style The style to register the image for.
	 * @param image The image to register.
	 */
	public static void registerImage(MessageBoxStyle style, Image image) {
		assert style != null;
		assert image != null;

		List<Image> styleImages = IMAGE_REGISTRY.get(style);

		if (styleImages == null) {
			styleImages = new LinkedList<>();
		}

		double imageSpace = getImageSpace(image);
		int imageIndex = 0;

		while (imageIndex < styleImages.size() && imageSpace < getImageSpace(styleImages.get(imageIndex))) {
			imageIndex++;
		}
		styleImages.add(imageIndex, image);
		IMAGE_REGISTRY.put(style, styleImages);
	}

	/**
	 * Get the images registered for a specific style.
	 *
	 * @param style The style to get the images for.
	 * @return The images registered for the submitted style or an empty list,
	 *         if no image has been registered yet.
	 */
	public static List<Image> getImages(MessageBoxStyle style) {
		assert style != null;

		List<Image> styleImages = IMAGE_REGISTRY.get(style);

		return (styleImages != null ? Collections.unmodifiableList(styleImages) : Collections.emptyList());
	}

	/**
	 * Get the image registered for a specific style.
	 *
	 * @param style The style to get the image for.
	 * @return The largest image registered for the submitted style of
	 *         {@code null} if no image has been registered yet.
	 */
	public static Image getImage(MessageBoxStyle style) {
		List<Image> styleImages = IMAGE_REGISTRY.get(style);

		return (styleImages != null ? styleImages.get(0) : null);
	}

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
		controllerStage.setTitle(I18N.formatSTR_MESSAGEBOX_TITLE());
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
				getStage().getIcons().addAll(getImages(MessageBoxStyle.ICON_INFO));
				this.ctlIcon.setImage(getImage(MessageBoxStyle.ICON_INFO));
				break;
			case ICON_WARNING:
				getStage().getIcons().addAll(getImages(MessageBoxStyle.ICON_WARNING));
				this.ctlIcon.setImage(getImage(MessageBoxStyle.ICON_WARNING));
				break;
			case ICON_ERROR:
				getStage().getIcons().addAll(getImages(MessageBoxStyle.ICON_ERROR));
				this.ctlIcon.setImage(getImage(MessageBoxStyle.ICON_ERROR));
				break;
			case ICON_QUESTION:
				getStage().getIcons().addAll(getImages(MessageBoxStyle.ICON_QUESTION));
				this.ctlIcon.setImage(getImage(MessageBoxStyle.ICON_QUESTION));
				break;
			case BUTTON_OK:
				this.ctlButtonCmd1.setText(I18N.formatSTR_OK_BUTTON());
				this.ctlButtonCmd1.setDefaultButton(true);
				this.resultButton1 = MessageBoxResult.OK;
				this.ctlButtonCmd2.setVisible(false);
				this.ctlButtonCmd3.setVisible(false);
				break;
			case BUTTON_OK_CANCEL:
				this.ctlButtonCmd1.setText(I18N.formatSTR_CANCEL_BUTTON());
				this.ctlButtonCmd1.setCancelButton(true);
				this.resultButton1 = MessageBoxResult.CANCEL;
				this.ctlButtonCmd2.setText(I18N.formatSTR_OK_BUTTON());
				this.ctlButtonCmd2.setDefaultButton(true);
				this.resultButton2 = MessageBoxResult.OK;
				this.ctlButtonCmd3.setVisible(false);
				break;
			case BUTTON_YES_NO:
				this.ctlButtonCmd1.setText(I18N.formatSTR_OK_BUTTON());
				this.ctlButtonCmd1.setCancelButton(true);
				this.resultButton1 = MessageBoxResult.NO;
				this.ctlButtonCmd2.setText(I18N.formatSTR_YES_BUTTON());
				this.ctlButtonCmd2.setDefaultButton(true);
				this.resultButton2 = MessageBoxResult.YES;
				this.ctlButtonCmd3.setVisible(false);
				break;
			case BUTTON_YES_NO_CANCEL:
				this.ctlButtonCmd1.setText(I18N.formatSTR_CANCEL_BUTTON());
				this.ctlButtonCmd1.setCancelButton(true);
				this.resultButton1 = MessageBoxResult.CANCEL;
				this.ctlButtonCmd2.setText(I18N.formatSTR_NO_BUTTON());
				this.resultButton2 = MessageBoxResult.NO;
				this.ctlButtonCmd3.setText(I18N.formatSTR_YES_BUTTON());
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
