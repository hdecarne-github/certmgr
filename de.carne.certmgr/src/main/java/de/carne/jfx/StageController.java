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
package de.carne.jfx;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.carne.jfx.messagebox.MessageBoxController;
import de.carne.jfx.messagebox.MessageBoxResult;
import de.carne.jfx.messagebox.MessageBoxStyle;
import de.carne.util.logging.Log;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Abstract base class for controlling JFX stages (including scene setup).
 */
public abstract class StageController {

	private static final Log LOG = new Log(StageController.class);

	private static final ExecutorService TASK_EXECUTOR = Executors.newSingleThreadScheduledExecutor();

	private static final Pattern CONTROLLER_PATTERN = Pattern.compile("^(.*)\\.(.+)Controller$");

	private static Stage primaryStage = null;

	private Stage stage = null;
	private ResourceBundle bundle = null;

	void setBundle(ResourceBundle bundle) {
		assert bundle != null;

		this.bundle = bundle;
	}

	/**
	 * Get the controller's resource bundle.
	 *
	 * @return The controller's resource bundle.
	 */
	protected ResourceBundle getBundle() {
		return this.bundle;
	}

	/**
	 * Get the controller's optional preferences object.
	 *
	 * @return The controller's preferences object or null if the controller
	 *         doesn't have one.
	 */
	protected Preferences getPreferences() {
		return null;
	}

	/**
	 * Synchronize the controller's preferences.
	 */
	protected void syncPreferences() {
		Preferences preferences = getPreferences();

		if (preferences != null) {
			try {
				preferences.sync();
			} catch (BackingStoreException e) {
				LOG.warning(e, I18N.BUNDLE, I18N.STR_PREF_SYNC_FAILED_MESSAGE);
			}
		}
	}

	/**
	 * Called during stage setup to get the stage's modality.
	 * <p>
	 * Derived classes can override this to change it's modality.
	 * </p>
	 *
	 * @return The modality to use for stage setup.
	 */
	protected Modality getModality() {
		return Modality.WINDOW_MODAL;
	}

	/**
	 * Called during stage setup to get the stage's resizable flag.
	 * <p>
	 * Derived classes can override this to change it's resizable behavior.
	 * </p>
	 *
	 * @return The resizable flag to use for stage setup.
	 */
	protected boolean getResizable() {
		return true;
	}

	/**
	 * Called during stage setup to perform the actual controller specific setup
	 * steps.
	 * <p>
	 * Derived classes overriding this function have to make sure to invoke the
	 * super's version prior to performing their setup steps.
	 * </p>
	 *
	 * @param controllerStage The stage to setup.
	 * @throws IOException If an I/O error occurs during setup.
	 * @see {@link #setupPrimaryStage(Stage, Class)}
	 */
	protected void setupStage(Stage controllerStage) throws IOException {
		this.stage = controllerStage;
		// install on close handler to avoid scene closing while stage is
		// disabled
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent evt) {
				onCloseRequest(evt);
			}

		});

	}

	private static <T extends StageController> T setupStage(Stage controllerStage, Class<T> controllerClass)
			throws IOException {
		assert controllerStage != null;
		assert controllerClass != null;

		String controllerName = controllerClass.getName();
		Matcher controllerNameMatcher = CONTROLLER_PATTERN.matcher(controllerName);

		if (!controllerNameMatcher.find()) {
			throw new IllegalArgumentException("Invalid controller class name: " + controllerName);
		}

		String baseName = controllerNameMatcher.group(1);
		String resourceName = controllerNameMatcher.group(2);
		String fxmlResourceName = resourceName + ".fxml";
		String bundleName = baseName + ".I18N";

		LOG.debug(null, "Setting up stage for controller: {0}", controllerName);
		LOG.debug(null, " Using FXML resource           : {0}", fxmlResourceName);
		LOG.debug(null, " Using resource bundle         : {0}", bundleName);

		FXMLLoader loader = new FXMLLoader();

		loader.setLocation(controllerClass.getResource(fxmlResourceName));

		ResourceBundle controllerBundle = ResourceBundle.getBundle(bundleName);

		loader.setResources(controllerBundle);
		controllerStage.setScene(new Scene(loader.load()));

		T controller = loader.getController();

		if (!controllerStage.equals(primaryStage)) {
			controllerStage.initModality(controller.getModality());
		}
		controllerStage.setResizable(controller.getResizable());
		controller.setBundle(controllerBundle);
		controller.setupStage(controllerStage);
		controllerStage.sizeToScene();
		return controller;
	}

	/**
	 * Setup a an existing stage with the JFX scene of the submitted controller
	 * class.
	 *
	 * @param controllerStage The primary stage to setup.
	 * @param controllerClass The controller class to derive the FXML resource
	 *        from.
	 * @return The loaded {@link Scene}'s controller instance.
	 * @throws IOException if an I/O error occurs while loading the FXML
	 *         resource.
	 */
	public static <T extends StageController> T setupPrimaryStage(Stage controllerStage, Class<T> controllerClass)
			throws IOException {
		assert primaryStage == null;
		assert controllerStage != null;

		primaryStage = controllerStage;
		return setupStage(primaryStage, controllerClass);
	}

	/**
	 * Get the applications primary stage.
	 *
	 * @return The primary stage.
	 */
	public static Stage getPrimaryStage() {
		assert primaryStage != null;

		return primaryStage;
	}

	/**
	 * Create a new stage owned by this controller's stage and set it up with
	 * the JFX scene of the submitted controller class.
	 *
	 * @param controllerClass The controller class to derive the FXML resource
	 *        from.
	 * @return The loaded {@link Scene}'s controller instance.
	 * @throws IOException if an I/O error occurs while loading the FXML
	 *         resource.
	 */
	public <T extends StageController> T openStage(Class<T> controllerClass) throws IOException {
		Stage controllerStage = new Stage();

		controllerStage.initOwner(getStage());
		return setupStage(controllerStage, controllerClass);
	}

	/**
	 * Get the controller's stage.
	 *
	 * @return The controller's stage.
	 */
	public Stage getStage() {
		return this.stage;
	}

	/**
	 * Show a message box.
	 *
	 * @param message The message to display.
	 * @param details The (optional) exception causing the message.
	 * @param styles The message box style.
	 * @return The message box result.
	 */
	public MessageBoxResult showMessageBox(String message, Throwable details, MessageBoxStyle... styles) {
		MessageBoxResult result = MessageBoxResult.NONE;

		try {
			MessageBoxController messageBoxController = openStage(MessageBoxController.class);

			messageBoxController.beginMessageBox(message, details, styles);
			messageBoxController.getStage().showAndWait();
			result = messageBoxController.getResult();
		} catch (IOException e) {
			LOG.error(e, I18N.BUNDLE, I18N.STR_MESSAGEBOX_EXCEPTION_MESSAGE, message, e.getLocalizedMessage());
		}
		return result;
	}

	/**
	 * Report an unexpected exception to the user.
	 *
	 * @param unexpected The exception to report.
	 */
	public void reportUnexpectedException(Throwable unexpected) {
		LOG.error(unexpected, I18N.BUNDLE, I18N.STR_UNEXPECTED_EXCEPTION_MESSAGE, unexpected.getLocalizedMessage());
	}

	/**
	 * Run a background task.
	 *
	 * @param task The task to run.
	 */
	public <T> void runTask(Task<T> task) {
		LOG.debug(null, "Executing task: ''{0}''", task);
		TASK_EXECUTOR.execute(new Runnable() {
			private Runnable task2 = task;

			@Override
			public void run() {
				beginTask();
				try {
					this.task2.run();
				} finally {
					endTask();
				}
			}

		});
	}

	void beginTask() {
		if (Platform.isFxApplicationThread()) {
			this.stage.getScene().getRoot().setDisable(true);
		} else {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					beginTask();
				}

			});
		}
	}

	void endTask() {
		if (Platform.isFxApplicationThread()) {
			this.stage.getScene().getRoot().setDisable(false);
		} else {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					endTask();
				}

			});
		}
	}

	void onCloseRequest(WindowEvent evt) {
		if (this.stage.getScene().getRoot().isDisabled()) {
			evt.consume();
		}
	}

}
