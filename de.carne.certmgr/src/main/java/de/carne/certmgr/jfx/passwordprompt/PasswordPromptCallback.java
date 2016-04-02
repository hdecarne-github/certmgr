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

import javafx.application.Platform;
import de.carne.certmgr.jfx.StageController;
import de.carne.certmgr.store.PasswordCallback;

/**
 * Password callback implementation presenting a password prompt dialog to the user.
 */
public class PasswordPromptCallback implements PasswordCallback {

	private final StageController parent;
	private final Class<? extends AbstractPasswordPromptController> controllerClass;
	private volatile String passwordInput = null;
	private volatile boolean reusePasswordFlag = false;

	/**
	 * Create a password callback to ask the user for an existing password.
	 *
	 * @param parent The parent dialog.
	 * @return The created password callback.
	 */
	public static PasswordPromptCallback getPassword(StageController parent) {
		return new PasswordPromptCallback(parent, PasswordPromptController.class);
	}

	/**
	 * Create a password callback to ask the user for a new password.
	 *
	 * @param parent The parent dialog.
	 * @return The created password callback.
	 */
	public static PasswordPromptCallback getNewPassword(StageController parent) {
		return new PasswordPromptCallback(parent, NewPasswordPromptController.class);
	}

	private PasswordPromptCallback(StageController parent,
			Class<? extends AbstractPasswordPromptController> controllerClass) {
		assert parent != null;
		assert controllerClass != null;

		this.parent = parent;
		this.controllerClass = controllerClass;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.PasswordCallback#queryPassword(java.lang.String)
	 */
	@Override
	public String queryPassword(String resource) {
		String password = null;

		if (!this.reusePasswordFlag) {
			if (Platform.isFxApplicationThread()) {
				queryPassword0(resource);
				password = this.passwordInput;
			} else {
				QueryRunner queryRunner = new QueryRunner() {
					private String resource2 = resource;

					@Override
					public void run() {
						queryPassword0(this.resource2);
						setComplete();
					}
				};
				Platform.runLater(queryRunner);
				try {
					queryRunner.waitComplete();
					password = this.passwordInput;
				} catch (InterruptedException e) {
					// Nothing to do here
				}
			}
		} else {
			password = this.passwordInput;
		}
		return password;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.PasswordCallback#requeryPassword(java.lang.String, java.lang.Exception)
	 */
	@Override
	public String requeryPassword(String resource, Exception details) {
		String password = null;

		if (!this.reusePasswordFlag || this.passwordInput != null) {
			this.reusePasswordFlag = false;
			if (Platform.isFxApplicationThread()) {
				requeryPassword0(resource, details);
				password = this.passwordInput;
			} else {
				QueryRunner queryRunner = new QueryRunner() {
					private String resource2 = resource;
					private Exception details2 = details;

					@Override
					public void run() {
						requeryPassword0(this.resource2, this.details2);
						setComplete();
					}
				};

				Platform.runLater(queryRunner);
				try {
					queryRunner.waitComplete();
					password = this.passwordInput;
				} catch (InterruptedException e) {
					// nothing to do here
				}
			}
		}
		return password;
	}

	void queryPassword0(String resource) {
		try {
			AbstractPasswordPromptController controller = this.parent.openStage(this.controllerClass);

			controller.beginPasswordPrompt(resource);
			controller.getStage().showAndWait();
			this.passwordInput = controller.getPasswordInput();
			this.reusePasswordFlag = controller.getReusePasswordFlag();
		} catch (IOException e) {
			this.parent.reportUnexpectedException(e);
			this.passwordInput = null;
			this.reusePasswordFlag = false;
		}
	}

	void requeryPassword0(String resource, Exception details) {
		try {
			AbstractPasswordPromptController controller = this.parent.openStage(this.controllerClass);

			controller.beginPasswordPrompt(resource, details);
			controller.getStage().showAndWait();
			this.passwordInput = controller.getPasswordInput();
			this.reusePasswordFlag = controller.getReusePasswordFlag();
		} catch (IOException e) {
			this.parent.reportUnexpectedException(e);
			this.passwordInput = null;
			this.reusePasswordFlag = false;
		}
	}

	private abstract class QueryRunner implements Runnable {

		private boolean queryCompleteFlag = false;
		
		QueryRunner() {
			// Nothing to do here
		}

		public synchronized void waitComplete() throws InterruptedException {
			while (!this.queryCompleteFlag) {
				wait();
			}
		}

		public synchronized void setComplete() {
			this.queryCompleteFlag = true;
			notifyAll();
		}

	}

}
