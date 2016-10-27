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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model for attribute details display.
 */
public class AttributeModel {

	private final StringProperty nameProperty;
	private final ObjectProperty<Object> valueProperty;

	/**
	 * Construct {@code AttributeValue}.
	 *
	 * @param name The attribute name.
	 */
	public AttributeModel(String name) {
		this(name, null);
	}

	/**
	 * Construct {@code AttributeValue}.
	 *
	 * @param name The attribute name.
	 * @param value The attribute value.
	 */
	public AttributeModel(String name, Object value) {
		this.nameProperty = new SimpleStringProperty(name);
		this.valueProperty = new SimpleObjectProperty<>(value);
	}

	/**
	 * Get the Name property value.
	 *
	 * @return The Name property value.
	 */
	public final String getName() {
		return this.nameProperty.getValue();
	}

	/**
	 * Set the Name property value.
	 *
	 * @param name The value to set.
	 */
	public final void setName(String name) {
		this.nameProperty.setValue(name);
	}

	/**
	 * Get the Name property.
	 *
	 * @return The Name property.
	 */
	public final StringProperty nameProperty() {
		return this.nameProperty;
	}

	/**
	 * Get the Value property value.
	 *
	 * @return The Value property value.
	 */
	public final Object getValue() {
		return this.valueProperty.getValue();
	}

	/**
	 * Set the Value property value.
	 *
	 * @param value The value to set.
	 */
	public final void setValue(Object value) {
		this.valueProperty.setValue(value);
	}

	/**
	 * Get the Value property.
	 *
	 * @return The Value property.
	 */
	public final ObjectProperty<Object> valueProperty() {
		return this.valueProperty;
	}

}
