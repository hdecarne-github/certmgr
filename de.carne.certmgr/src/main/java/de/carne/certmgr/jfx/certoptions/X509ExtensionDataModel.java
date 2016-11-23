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
package de.carne.certmgr.jfx.certoptions;

import de.carne.certmgr.certs.asn1.OIDs;
import de.carne.certmgr.certs.x509.X509ExtensionData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Model class for display and editing of {@link X509ExtensionData} objects.
 */
public final class X509ExtensionDataModel {

	private X509ExtensionData extensionData;

	private BooleanProperty criticalProperty;

	private ReadOnlyStringWrapper nameProperty;

	private ReadOnlyStringWrapper valueProperty;

	/**
	 * Construct {@code X509ExtensionDataModel}.
	 *
	 * @param extensionData The extension data represented by this model.
	 */
	public X509ExtensionDataModel(X509ExtensionData extensionData) {
		this.extensionData = extensionData;
		this.criticalProperty = new SimpleBooleanProperty(extensionData.getCritical());
		this.nameProperty = new ReadOnlyStringWrapper(OIDs.toString(this.extensionData.oid()));
		this.valueProperty = new ReadOnlyStringWrapper(this.extensionData.toValueString());
	}

	/**
	 * Set this model's extension data.
	 *
	 * @param extensionData The extension data to set.
	 */
	public void setExtensionData(X509ExtensionData extensionData) {
		this.extensionData = extensionData;
		this.criticalProperty.set(this.extensionData.getCritical());
		this.nameProperty.set(OIDs.toString(this.extensionData.oid()));
		this.valueProperty.set(this.extensionData.toValueString());
	}

	/**
	 * Get this model's extension data.
	 *
	 * @return This model's extension data.
	 */
	public X509ExtensionData getExtensionData() {
		this.extensionData.setCritical(this.criticalProperty.get());
		return this.extensionData;
	}

	/**
	 * Get the critical flag value.
	 *
	 * @return The critical flag value.
	 */
	public boolean getCritical() {
		return this.criticalProperty.get();
	}

	/**
	 * Set the critical flag value.
	 *
	 * @param critical The critical flag value to set.
	 */
	public void setCritical(boolean critical) {
		this.criticalProperty.set(critical);
	}

	/**
	 * Get the critical flag property.
	 * 
	 * @return The critical flag property.
	 */
	public BooleanProperty criticalProperty() {
		return this.criticalProperty;
	}

	/**
	 * Get the extension name.
	 * 
	 * @return The extension name.
	 */
	public String getName() {
		return this.nameProperty.get();
	}

	/**
	 * Get the extension name property.
	 * 
	 * @return The extension name property.
	 */
	public ReadOnlyStringProperty nameProperty() {
		return this.nameProperty.getReadOnlyProperty();
	}

	/**
	 * Get the extension value string.
	 * 
	 * @return The extension value string.
	 */
	public String getValue() {
		return this.valueProperty.get();
	}

	/**
	 * Get the extension value string property.
	 * 
	 * @return The extension value string property.
	 */
	public ReadOnlyStringProperty valueProperty() {
		return this.valueProperty.getReadOnlyProperty();
	}

}
