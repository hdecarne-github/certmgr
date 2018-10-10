/*
 * Copyright (c) 2015-2018 Holger de Carne and contributors, All Rights Reserved.
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
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.Exceptions;
import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.x509.Attributes;
import de.carne.certmgr.jfx.util.UserCertStoreEntryModel;
import de.carne.jfx.util.FormattedBigInteger;
import de.carne.jfx.util.FormattedDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Model class used for managing {@link UserCertStoreEntry}.
 */
public class StoreEntryModel extends UserCertStoreEntryModel {

	private final ObjectProperty<Date> expiresProperty;
	private final ObjectProperty<BigInteger> serialProperty;

	/**
	 * Construct {@code ManagedEntryModel}.
	 *
	 * @param entry The represented certificate store entry.
	 */
	public StoreEntryModel(UserCertStoreEntry entry) {
		super(entry);

		Date expires = null;
		BigInteger serial = null;

		if (entry.hasCRT()) {
			try {
				X509Certificate crt = entry.getCRT();

				expires = crt.getNotAfter();
				serial = crt.getSerialNumber();
			} catch (IOException e) {
				Exceptions.warn(e);
			}
		}
		this.expiresProperty = new SimpleObjectProperty<>(
				expires != null ? FormattedDate.fromDate(Attributes.DATE_FORMAT, expires) : null);
		this.serialProperty = new SimpleObjectProperty<>(
				serial != null ? FormattedBigInteger.fromBigInteger(Attributes.SERIAL_FORMAT, serial) : null);
	}

	/**
	 * Get the Expires value.
	 *
	 * @return The Expires value.
	 */
	@Nullable
	public final Date getExpires() {
		return this.expiresProperty.getValue();
	}

	/**
	 * Set the Expires value.
	 *
	 * @param expires The Expires value to set.
	 */
	public final void setExpired(@Nullable Date expires) {
		this.expiresProperty.setValue(expires != null ? FormattedDate.fromDate(Attributes.DATE_FORMAT, expires) : null);
	}

	/**
	 * Get the Expires property.
	 *
	 * @return The Expires property.
	 */
	public final ObjectProperty<Date> expiresProperty() {
		return this.expiresProperty;
	}

	/**
	 * Get the Serial value.
	 *
	 * @return The Serial value.
	 */
	@Nullable
	public BigInteger getSerial() {
		return this.serialProperty.get();
	}

	/**
	 * Set the Serial value.
	 *
	 * @param serial The Serial value to set.
	 */
	public void setSerial(@Nullable BigInteger serial) {
		this.serialProperty
				.set(serial != null ? FormattedBigInteger.fromBigInteger(Attributes.SERIAL_FORMAT, serial) : null);
	}

	/**
	 * Get the Serial property.
	 *
	 * @return The Serial property.
	 */
	public ObjectProperty<BigInteger> serialProperty() {
		return this.serialProperty;
	}

}
