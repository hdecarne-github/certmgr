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
package de.carne.certmgr.store;

import java.io.IOException;

/**
 * Base class for all certificate store related exceptions.
 */
public abstract class StoreException extends IOException {

	/**
	 * Serialization support.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct StoreException.
	 *
	 * @param message The exception message.
	 */
	public StoreException(String message) {
		super(message);
	}

	/**
	 * Construct StoreException.
	 *
	 * @param message The exception message.
	 * @param cause The causing exception.
	 */
	public StoreException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Construct StoreException.
	 *
	 * @param cause The causing exception.
	 */
	public StoreException(Throwable cause) {
		super((cause != null ? cause.getMessage() : null), cause);
	}

}
