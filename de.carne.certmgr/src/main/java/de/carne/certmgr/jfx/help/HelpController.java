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
package de.carne.certmgr.jfx.help;

import java.io.IOException;
import java.util.MissingResourceException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import de.carne.certmgr.jfx.CertMgrApplication;
import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.jfx.help.html.Htmls;
import de.carne.jfx.StageController;

/**
 * Dialog controller for help display.
 */
public class HelpController extends StageController {

	private static HelpController helpController = null;

	@FXML
	WebView ctlHelpView;

	void onHidden() {
		helpController = null;
	}

	void onDocumentLoaded() {
		Document document = this.ctlHelpView.getEngine().getDocument();
		NodeList nodeList = document.getElementsByTagName("a");

		for (int itemIndex = 0; itemIndex < nodeList.getLength(); itemIndex++) {
			HTMLAnchorElement anchor = (HTMLAnchorElement) nodeList.item(itemIndex);
			String href = anchor.getHref();

			if (href != null && href.startsWith("http")) {
				EventTarget eventTarget = (EventTarget) anchor;

				eventTarget.addEventListener("click", new EventListener() {

					@Override
					public void handleEvent(Event evt) {
						onExternalAnchorClick((HTMLAnchorElement) evt.getTarget());
						evt.preventDefault();
					}

				}, false);
			}
		}
	}

	void onExternalAnchorClick(HTMLAnchorElement anchor) {
		CertMgrApplication.getInstance().getHostServices().showDocument(anchor.getHref());
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.setTitle(getBundle().getString(I18N.TEXT_TITLE));
		controllerStage.getIcons().addAll(Images.IMAGE_HELP16, Images.IMAGE_HELP32);
		controllerStage.sizeToScene();
		controllerStage.setOnHidden(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				onHidden();
			}

		});
		this.ctlHelpView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {

			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if (Worker.State.SUCCEEDED.equals(newValue)) {
					onDocumentLoaded();
				}
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#getModality()
	 */
	@Override
	protected Modality getModality() {
		return Modality.NONE;
	}

	private void align(StageController parentController) {
		Stage parentStage = parentController.getStage();
		Stage stage = getStage();

		stage.setX(Math.max(0, parentStage.getX() + parentStage.getWidth() - stage.getWidth()));
		stage.setY(parentStage.getY());
		stage.setHeight(Math.max(stage.getHeight(), parentStage.getHeight()));
	}

	private void open(String url) {
		this.ctlHelpView.getEngine().load(url);
	}

	/**
	 * Show help window with a specific topic.
	 * <p>
	 * Only one help window will be opened at a time. If a previous call to this function already opened a help window,
	 * it will be reused.
	 * </p>
	 *
	 * @param parentController The controller requesting the help.
	 * @param topic The topic to display ({@linkplain Help})
	 * @return The help controller.
	 * @throws IOException if an I/O error occurs.
	 */
	public static HelpController showHelp(StageController parentController, String topic) throws IOException {
		if (helpController == null) {
			helpController = parentController.openStage(HelpController.class);
			helpController.getStage().show();
			helpController.align(parentController);
		} else {
			helpController.getStage().requestFocus();
		}

		String htmlName;

		try {
			htmlName = I18N.bundle().getString(topic);
		} catch (MissingResourceException e) {
			throw new IOException("Unknown help topic '" + topic + "'");
		}

		String htmlURL = Htmls.getURL(htmlName);

		helpController.open(htmlURL);
		return helpController;
	}

}
