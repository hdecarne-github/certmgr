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
package de.carne.certmgr.jfx.crloptions;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import de.carne.certmgr.jfx.CertStoreEntryOption;
import de.carne.certmgr.store.CertStoreEntry;
import de.carne.certmgr.store.x509.RevokeReason;

/**
 * Model class for CRL entries.
 */
public class CRLEntryModel {

	private final SimpleBooleanProperty revoked;
	private final SimpleObjectProperty<CertStoreEntryOption> entryOption;
	private final SimpleObjectProperty<RevokeReason> reason;

	/**
	 * Construct CRLEntryModel.
	 *
	 * @param revoked Whether this entry is revoked or not.
	 * @param entry The certificate store entry.
	 * @param reason The revoke reason.
	 */
	public CRLEntryModel(boolean revoked, CertStoreEntry entry, RevokeReason reason) {
		this.revoked = new SimpleBooleanProperty(revoked);
		this.entryOption = new SimpleObjectProperty<>(CertStoreEntryOption.fromStoreEntry(entry));
		this.reason = new SimpleObjectProperty<>(reason);
	}

	/**
	 * @return the revoked
	 */
	public boolean isRevoked() {
		return this.revoked.get();
	}

	/**
	 * @param revoked the revoked to set
	 */
	public void setRevoked(boolean revoked) {
		this.revoked.set(revoked);
	}

	/**
	 * @return the revoked
	 */
	public BooleanProperty revokedProperty() {
		return this.revoked;
	}

	/**
	 * @return the entryOption
	 */
	public CertStoreEntryOption getEntryOption() {
		return this.entryOption.get();
	}

	/**
	 * @param entryOption the entryOption to set
	 */
	public void setEntryOption(CertStoreEntryOption entryOption) {
		this.entryOption.set(entryOption);
	}

	/**
	 * @return the entryOption
	 */
	public ObjectProperty<CertStoreEntryOption> entryOptionProperty() {
		return this.entryOption;
	}

	/**
	 * @return the reason
	 */
	public RevokeReason getReason() {
		return this.reason.get();
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(RevokeReason reason) {
		this.reason.set(reason);
	}

	/**
	 * @return the reason
	 */
	public ObjectProperty<RevokeReason> reasonProperty() {
		return this.reason;
	}

}
