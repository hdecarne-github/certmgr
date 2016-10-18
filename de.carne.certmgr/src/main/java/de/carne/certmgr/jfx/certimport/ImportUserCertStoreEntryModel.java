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
package de.carne.certmgr.jfx.certimport;

import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.jfx.UserCertStoreEntryModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 */
public class ImportUserCertStoreEntryModel extends UserCertStoreEntryModel {

	private final BooleanProperty selectedProperty;

	/**
	 *
	 */
	public ImportUserCertStoreEntryModel(UserCertStoreEntry entry, boolean selected) {
		super(entry);
		this.selectedProperty = new SimpleBooleanProperty(selected);
	}

	public final Boolean getSelected() {
		return this.selectedProperty.getValue();
	}

	public final void setSelected(Boolean selected) {
		this.selectedProperty.setValue(selected);
	}

	public final BooleanProperty selectedProperty() {
		return this.selectedProperty;
	}

}
