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
package de.carne.certmgr.jfx;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Map;

import de.carne.certmgr.jfx.storemanager.StoreManagerController;
import de.carne.certmgr.store.CertStore;
import de.carne.jfx.StageController;
import de.carne.util.logging.Log;
import de.carne.util.logging.LogConfig;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX application class responsible for starting up the GUI.
 */
public class CertMgrApplication extends Application {

	private static final Log LOG = new Log(CertMgrApplication.class);

	private static final String PARAMETER_VERBOSE = "--verbose";
	private static final String PARAMETER_DEBUG = "--debug";

	private static CertMgrApplication applicationInstance = null;

	void handleUncaughtException(Thread t, Throwable e, UncaughtExceptionHandler next) {
		LOG.error(e, I18N.BUNDLE, I18N.STR_UNEXPECTED_EXCEPTION_MESSAGE, e.getLocalizedMessage());
		if (next != null) {
			next.uncaughtException(t, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage stage) throws Exception {
		assert stage != null;

		Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			private UncaughtExceptionHandler next = Thread.currentThread().getUncaughtExceptionHandler();

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				handleUncaughtException(t, e, this.next);
			}

		});

		LOG.info(null, "Starting JavaFX GUI...");

		applicationInstance = this;

		String openStore = processParameters();

		StoreManagerController storeManager = StageController.setupPrimaryStage(stage, StoreManagerController.class);

		storeManager.getStage().show();
		logVMInfo();
		logLoaderInfo();
		logProviderInfo();
		if (openStore != null) {
			storeManager.openStore(openStore);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception {
		applicationInstance = null;
		LOG.info(null, "JavaFX GUI stopped");
	}

	/**
	 * Get the currently running application instance.
	 *
	 * @return The currently running application or null, if no application
	 *         running.
	 */
	public static CertMgrApplication getInstance() {
		return applicationInstance;
	}

	private String processParameters() {
		Parameters parameters = getParameters();

		for (Map.Entry<String, String> parameter : parameters.getNamed().entrySet()) {
			LOG.warning(I18N.BUNDLE, I18N.STR_INVALID_PARAMETER_MESSAGE,
					parameter.getKey() + "=" + parameter.getValue());
		}

		String openStore = null;

		for (String parameter : parameters.getUnnamed()) {
			if (PARAMETER_VERBOSE.equals(parameter)) {
				LogConfig.applyConfig(LogConfig.CONFIG_VERBOSE);
				LOG.notice(I18N.BUNDLE, I18N.STR_VERBOSE_ENABLED_MESSAGE);
			} else if (PARAMETER_DEBUG.equals(parameter)) {
				LogConfig.applyConfig(LogConfig.CONFIG_DEBUG);
				LOG.notice(I18N.BUNDLE, I18N.STR_DEBUG_ENABLED_MESSAGE);
			} else if (openStore == null && isValidStoreParameter(parameter)) {
				openStore = parameter;
			} else {
				LOG.warning(I18N.BUNDLE, I18N.STR_INVALID_PARAMETER_MESSAGE, parameter);
			}
		}
		return openStore;
	}

	private boolean isValidStoreParameter(String parameter) {
		File parameterFile = new File(parameter);

		return parameterFile.isDirectory();
	}

	private void logVMInfo() {
		String vmVersion = System.getProperty("java.version");
		String vmVendor = System.getProperty("java.vendor");

		LOG.notice(I18N.BUNDLE, I18N.STR_VM_INFO, vmVersion, vmVendor);
	}

	private void logLoaderInfo() {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();

		if (classloader instanceof URLClassLoader) {
			URL[] urls = ((URLClassLoader) classloader).getURLs();

			LOG.info(null, "Using ClassLoader {0} with URL path {1}", classloader.getClass().getName(),
					Arrays.toString(urls));
		}
	}

	private void logProviderInfo() {
		try {
			LOG.notice(I18N.BUNDLE, I18N.STR_PROVIDER_INFO, CertStore.getProviderInfo());
		} catch (Exception e) {
			LOG.error(e, I18N.BUNDLE, I18N.STR_PROVIDER_EXCEPTION_MESSAGE, e.getLocalizedMessage());
		}
	}

}
