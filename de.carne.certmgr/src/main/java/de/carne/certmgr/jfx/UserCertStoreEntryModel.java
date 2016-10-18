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
package de.carne.certmgr.jfx;

import de.carne.certmgr.certs.UserCertStoreEntry;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 */
public class UserCertStoreEntryModel {

	private final UserCertStoreEntry entry;
	private final StringProperty nameProperty;
	private final BooleanProperty hasCRTProperty;
	private final BooleanProperty hasKeyProperty;
	private final BooleanProperty hasCSRProperty;
	private final BooleanProperty hasCRLProperty;

	/**
	 *
	 */
	protected UserCertStoreEntryModel(UserCertStoreEntry entry) {
		assert entry != null;

		this.entry = entry;
		this.nameProperty = new SimpleStringProperty(this.entry.cn().getName());
		this.hasCRTProperty = new SimpleBooleanProperty(this.entry.hasCRT());
		this.hasKeyProperty = new SimpleBooleanProperty(this.entry.hasKey());
		this.hasCSRProperty = new SimpleBooleanProperty(this.entry.hasCSR());
		this.hasCRLProperty = new SimpleBooleanProperty(this.entry.hasCRL());
	}

	public final UserCertStoreEntry getEntry() {
		return this.entry;
	}

	public final String getName() {
		return this.nameProperty.getValue();
	}

	public final void setName(String name) {
		this.nameProperty.setValue(name);
	}

	public final StringProperty nameProperty() {
		return this.nameProperty;
	}

	public final Boolean getHasCRT() {
		return this.hasCRTProperty.getValue();
	}

	public final void setHasCRT(Boolean hasCRT) {
		this.hasCRTProperty.setValue(hasCRT);
	}

	public final BooleanProperty hasCRTProperty() {
		return this.hasCRTProperty;
	}

	public final Boolean getHasKey() {
		return this.hasKeyProperty.getValue();
	}

	public final void setHashKey(Boolean hasKey) {
		this.hasKeyProperty.setValue(hasKey);
	}

	public final BooleanProperty hasKeyProperty() {
		return this.hasKeyProperty;
	}

	public final Boolean getHasCSR() {
		return this.hasCSRProperty.getValue();
	}

	public final void setHashCSR(Boolean hasCSR) {
		this.hasCSRProperty.setValue(hasCSR);
	}

	public final BooleanProperty hasCSRProperty() {
		return this.hasCSRProperty;
	}

	public final Boolean getHasCRL() {
		return this.hasCRLProperty.get();
	}

	public final void setHashCRL(Boolean hasCRL) {
		this.hasCRLProperty.setValue(hasCRL);
	}

	public final BooleanProperty hasCRLProperty() {
		return this.hasCRLProperty;
	}

}
