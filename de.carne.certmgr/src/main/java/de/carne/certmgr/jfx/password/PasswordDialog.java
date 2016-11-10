/*
 * Copyright (c) 2015-2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.jfx.password;

import java.io.IOException;
import java.util.Optional;

import de.carne.certmgr.certs.PasswordCallback;
import de.carne.jfx.application.PlatformHelper;
import de.carne.jfx.stage.StageController;
import de.carne.util.Exceptions;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

/**
 * This class implements the {@link PasswordCallback} interface by displaying
 * this package's "Enter password" or "Enter new password" dialogs.
 */
public final class PasswordDialog implements PasswordCallback {

	private final StageController owner;
	private final Class<? extends PasswordController> controllerClass;

	private PasswordDialog(StageController owner, boolean newPassword) {
		this.owner = owner;
		this.controllerClass = (newPassword ? EnterNewPasswordController.class : EnterPasswordController.class);
	}

	/**
	 * Create a {@link PasswordCallback} for enter an existing password.
	 *
	 * @param owner The owner to use for dialog display.
	 * @return The created {@link PasswordCallback}.
	 */
	public static PasswordDialog enterPassword(StageController owner) {
		return new PasswordDialog(owner, false);
	}

	/**
	 * Create a {@link PasswordCallback} for enter a new password.
	 *
	 * @param owner The owner to use for dialog display.
	 * @return The created {@link PasswordCallback}.
	 */
	public static PasswordDialog enterNewPassword(StageController owner) {
		return new PasswordDialog(owner, true);
	}

	@Override
	public char[] queryPassword(String resource) {
		return PlatformHelper.runLater(() -> queryPasswordDialog(resource));
	}

	@Override
	public char[] requeryPassword(String resource, Throwable cause) {
		return PlatformHelper.runLater(() -> requeryPasswordDialog(resource, cause));
	}

	private char[] queryPasswordDialog(String resource) {
		char[] password = null;

		try {
			PasswordController controller = this.owner.loadDialog((c) -> new PasswordInputDialog(c),
					this.controllerClass);

			controller.setResource(resource);

			Optional<String> dialogResult = controller.showAndWait();

			if (dialogResult.isPresent()) {
				password = dialogResult.get().toCharArray();
			}
		} catch (IOException e) {
			Exceptions.warn(e);
		}
		return password;
	}

	private char[] requeryPasswordDialog(String resource, Throwable cause) {
		char[] password = null;

		try {
			PasswordController controller = this.owner.loadDialog((c) -> new PasswordInputDialog(c),
					this.controllerClass);

			controller.setResource(resource);
			controller.setPasswordException(cause);

			Optional<String> dialogResult = controller.showAndWait();

			if (dialogResult.isPresent()) {
				password = dialogResult.get().toCharArray();
			}
		} catch (IOException e) {
			Exceptions.warn(e);
		}
		return password;
	}

	private class PasswordInputDialog extends Dialog<String> {

		private final PasswordController controller;

		PasswordInputDialog(PasswordController controller) {
			this.controller = controller;
			setResultConverter(new Callback<ButtonType, String>() {

				@Override
				public String call(ButtonType param) {
					return getPasswordInputResult(param);
				}

			});
		}

		String getPasswordInputResult(ButtonType button) {
			String passwordInput;

			if (ButtonType.NEXT.equals(button) || ButtonType.APPLY.equals(button)) {
				passwordInput = this.controller.getPasswordInput();
			} else {
				passwordInput = null;
			}
			return passwordInput;
		}

	}

}
