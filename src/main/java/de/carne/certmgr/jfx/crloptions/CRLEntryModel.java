/*
 * Copyright (c) 2015-2017 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.jfx.crloptions;

import java.math.BigInteger;
import java.util.Date;

import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.x509.ReasonFlag;
import de.carne.jfx.util.ShortDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for display and editing of CRL entries.
 */
public class CRLEntryModel implements Comparable<CRLEntryModel> {

	private final UserCertStoreEntry storeEntry;

	private final BooleanProperty revokedProperty;

	private final StringProperty nameProperty;

	private final ObjectProperty<BigInteger> serialProperty;

	private final ObjectProperty<ReasonFlag> reasonProperty;

	private final ObjectProperty<Date> dateProperty;

	CRLEntryModel(UserCertStoreEntry storeEntry, boolean revoked, BigInteger serial, ReasonFlag reason, Date date) {
		this.storeEntry = storeEntry;
		this.revokedProperty = new SimpleBooleanProperty(revoked);
		this.nameProperty = new SimpleStringProperty(storeEntry.getName());
		this.serialProperty = new SimpleObjectProperty<>(serial);
		this.reasonProperty = new SimpleObjectProperty<>(reason);
		this.dateProperty = new SimpleObjectProperty<>(date != null ? new ShortDate(date.getTime()) : null);
	}

	UserCertStoreEntry getStoreEntry() {
		return this.storeEntry;
	}

	/**
	 * Get the revoked flag value.
	 *
	 * @return The revoked flag value.
	 */
	public boolean getRevoked() {
		return this.revokedProperty.get();
	}

	/**
	 * Set the revoked flag value.
	 *
	 * @param revoked The revoked flag value to set.
	 */
	public void setRevoked(boolean revoked) {
		this.revokedProperty.set(revoked);
	}

	/**
	 * Get the revoked flag property.
	 *
	 * @return The revoked flag property.
	 */
	public BooleanProperty revokedProperty() {
		return this.revokedProperty;
	}

	/**
	 * Get the entry name.
	 *
	 * @return The entry name.
	 */
	public String getName() {
		return this.nameProperty.get();
	}

	/**
	 * Set the entry name.
	 *
	 * @param name The entry name to set.
	 */
	public void setName(String name) {
		this.nameProperty.set(name);
	}

	/**
	 * Get the entry name property.
	 *
	 * @return The entry name property.
	 */
	public StringProperty nameProperty() {
		return this.nameProperty;
	}

	/**
	 * Get the entry serial.
	 *
	 * @return The entry serial.
	 */
	public BigInteger getSerial() {
		return this.serialProperty.get();
	}

	/**
	 * Set the entry serial.
	 *
	 * @param serial The entry serial to set.
	 */
	public void setSerial(BigInteger serial) {
		this.serialProperty.set(serial);
	}

	/**
	 * Get the entry serial property.
	 *
	 * @return The entry serial property.
	 */
	public ObjectProperty<BigInteger> serialProperty() {
		return this.serialProperty;
	}

	/**
	 * Get the reason flag value.
	 *
	 * @return The reason flag value.
	 */
	public ReasonFlag getReason() {
		return this.reasonProperty.get();
	}

	/**
	 * Set the reason flag value.
	 *
	 * @param reason The reason flag value to set.
	 */
	public void setReason(ReasonFlag reason) {
		this.reasonProperty.set(reason);
	}

	/**
	 * Get the reason flag property.
	 *
	 * @return The reason flag property.
	 */
	public ObjectProperty<ReasonFlag> reasonProperty() {
		return this.reasonProperty;
	}

	/**
	 * Get the revocation date.
	 *
	 * @return The revocation date.
	 */
	public Date getDate() {
		return this.dateProperty.get();
	}

	/**
	 * Set the revocation date.
	 *
	 * @param date The revocation date to set.
	 */
	public void setDate(Date date) {
		this.dateProperty.set(date != null ? new ShortDate(date.getTime()) : null);
	}

	/**
	 * Get the revocation date property.
	 *
	 * @return The revocation date property.
	 */
	public ObjectProperty<Date> dateProperty() {
		return this.dateProperty;
	}

	@Override
	public int compareTo(CRLEntryModel o) {
		int comparison = this.nameProperty.get().compareTo(o.nameProperty.get());

		if (comparison == 0) {
			comparison = this.serialProperty.get().compareTo(o.serialProperty.get());
		}
		return comparison;
	}

}
