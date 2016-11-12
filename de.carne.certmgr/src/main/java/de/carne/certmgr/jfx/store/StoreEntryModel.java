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
package de.carne.certmgr.jfx.store;

import java.io.IOException;
import java.util.Date;

import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.jfx.util.UserCertStoreEntryModel;
import de.carne.jfx.util.ShortDate;
import de.carne.util.Exceptions;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Model class used for managing {@link UserCertStoreEntry}.
 */
public class StoreEntryModel extends UserCertStoreEntryModel {

	private final ObjectProperty<ShortDate> expiresProperty;

	/**
	 * Construct {@code ManagedEntryModel}.
	 *
	 * @param entry The represented certificate store entry.
	 */
	public StoreEntryModel(UserCertStoreEntry entry) {
		super(entry);
		this.expiresProperty = new SimpleObjectProperty<>(getExpires(entry));
	}

	/**
	 * Get the Expires property value.
	 *
	 * @return The Expires property value.
	 */
	public final ShortDate getExpires() {
		return this.expiresProperty.getValue();
	}

	/**
	 * Set the Expires property value.
	 *
	 * @param expires The value to set.
	 */
	public final void setExpired(ShortDate expires) {
		this.expiresProperty.setValue(expires);
	}

	/**
	 * Get the Expired property.
	 *
	 * @return The Expires property.
	 */
	public final ObjectProperty<ShortDate> expiresProperty() {
		return this.expiresProperty;
	}

	private static ShortDate getExpires(UserCertStoreEntry entry) {
		Date expires = null;

		try {
			if (entry.hasCRT()) {
				expires = entry.getCRT().getNotAfter();
			}
		} catch (IOException e) {
			Exceptions.warn(e);
		}
		return (expires != null ? new ShortDate(expires.getTime()) : null);
	}

}
