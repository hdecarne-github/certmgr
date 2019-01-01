/*
 * Copyright (c) 2015-2019 Holger de Carne and contributors, All Rights Reserved.
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
import de.carne.certmgr.jfx.util.UserCertStoreEntryModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Model class used for importing {@link UserCertStoreEntry}.
 */
public class ImportEntryModel extends UserCertStoreEntryModel {

	private final BooleanProperty selectedProperty;

	/**
	 * Construct {@code ImportUserCertStoreEntryModel}.
	 *
	 * @param entry The represented certificate store entry.
	 * @param selected Whether the entry is initially selected or not.
	 */
	public ImportEntryModel(UserCertStoreEntry entry, boolean selected) {
		super(entry);
		this.selectedProperty = new SimpleBooleanProperty(selected);
	}

	/**
	 * Get the Selected property value.
	 *
	 * @return The Selected property value.
	 */
	public final Boolean getSelected() {
		return this.selectedProperty.getValue();
	}

	/**
	 * Set the Selected property value.
	 *
	 * @param selected The value to set.
	 */
	public final void setSelected(Boolean selected) {
		this.selectedProperty.setValue(selected);
	}

	/**
	 * Get the Selected property.
	 *
	 * @return The Selected property.
	 */
	public final BooleanProperty selectedProperty() {
		return this.selectedProperty;
	}

}
