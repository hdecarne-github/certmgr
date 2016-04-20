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

import de.carne.jfx.StageController;

/**
 * Base class for password prompt controllers defining their common functionality.
 */
public abstract class AbstractPasswordPromptController extends StageController {

	private String passwordInput = null;
	private boolean reusePasswordFlag = false;

	/**
	 * Begin password entry.
	 *
	 * @param resource The resource the password is required.
	 */
	public abstract void beginPasswordPrompt(String resource);

	/**
	 * Begin password entry after a previously failed entry.
	 *
	 * @param resource The resource the password is required.
	 * @param details The (optional) exception providing details of the previously failed entry.
	 */
	public abstract void beginPasswordPrompt(String resource, Exception details);

	/**
	 * Set the entered password.
	 * 
	 * @param passwordInput The entered password or null if password entry was cancelled.
	 */
	protected void setPasswordInput(String passwordInput) {
		this.passwordInput = passwordInput;
	}

	/**
	 * Get the entered password.
	 * 
	 * @return The entered password or null if password entry was cancelled.
	 */
	public String getPasswordInput() {
		return this.passwordInput;
	}

	/**
	 * Set the selected re-use password flag option.
	 * 
	 * @param reusePasswordFlag The selected re-use password flag option.
	 */
	protected void setReusePasswordFlag(boolean reusePasswordFlag) {
		this.reusePasswordFlag = reusePasswordFlag;
	}

	/**
	 * Get the selected re-use password flag option.
	 * 
	 * @return The selected re-use password flag option.
	 */
	public boolean getReusePasswordFlag() {
		return this.reusePasswordFlag;
	}

}
