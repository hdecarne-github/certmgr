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
package de.carne.certmgr.jfx.dneditor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import de.carne.certmgr.store.x500.RDN;

/**
 * Model class for editing RDNs in a table.
 */
public class RDNModel {

	private final SimpleStringProperty type;
	private final SimpleStringProperty value;

	/**
	 * Construct RDNModel.
	 */
	public RDNModel() {
		this("", "");
	}

	/**
	 * Construct RDNModel.
	 *
	 * @param rdn RDN to use for model initialization.
	 */
	public RDNModel(RDN rdn) {
		this(rdn.getType(), rdn.getValue());
	}

	/**
	 * Construct RDNModel.
	 *
	 * @param type The type to use for model initialization.
	 * @param value The value to use for model initialization.
	 */
	public RDNModel(String type, String value) {
		this.type = new SimpleStringProperty(type);
		this.value = new SimpleStringProperty(value);
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type.get();
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type.set(type);
	}

	/**
	 * @return the type property
	 */
	public StringProperty typeProperty() {
		return this.type;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value.get();
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value.set(value);
	}

	/**
	 * @return the value property
	 */
	public StringProperty valueProperty() {
		return this.value;
	}

}
