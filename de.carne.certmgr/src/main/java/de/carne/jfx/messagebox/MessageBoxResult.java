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

/**
 * Message box results.
 */
public enum MessageBoxResult {

	/**
	 * No button was selected (message box was closed via system menu or not show at all).
	 */
	NONE,

	/**
	 * "Ok" button was selected.
	 */
	OK,

	/**
	 * "Cancel" button was selected.
	 */
	CANCEL,

	/**
	 * "Yes" button was selected.
	 */
	YES,

	/**
	 * "No" button was selected.
	 */
	NO

}
