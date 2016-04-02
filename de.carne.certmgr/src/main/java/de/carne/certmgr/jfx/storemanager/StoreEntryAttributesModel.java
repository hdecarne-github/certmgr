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
package de.carne.certmgr.jfx.storemanager;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class used to display attribute/value pairs in a table view or tree table view.
 */
public final class StoreEntryAttributesModel {

	private final SimpleStringProperty attribute;
	private final SimpleStringProperty value;

	/**
	 * Construct StoreEntryAttributesModel
	 *
	 * @param attribute The initial attribute property.
	 */
	public StoreEntryAttributesModel(String attribute) {
		this(attribute, null);
	}

	/**
	 * Construct StoreEntryAttributesModel
	 *
	 * @param attribute The initial attribute property.
	 * @param value The initial value property.
	 */
	public StoreEntryAttributesModel(String attribute, String value) {
		this.attribute = new SimpleStringProperty(attribute);
		this.value = new SimpleStringProperty(value);
	}

	/**
	 * @return the attribute
	 */
	public String getAttribute() {
		return this.attribute.getValue();
	}

	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(String attribute) {
		this.attribute.setValue(attribute);
	}

	/**
	 * @return the attribute property
	 */
	public StringProperty attributeProperty() {
		return this.attribute;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value.getValue();
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value.setValue(value);
	}

	/**
	 * @return the value property
	 */
	public StringProperty valueProperty() {
		return this.value;
	}

}
