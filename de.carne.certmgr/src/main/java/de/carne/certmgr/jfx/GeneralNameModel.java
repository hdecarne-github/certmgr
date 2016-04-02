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
package de.carne.certmgr.jfx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import de.carne.certmgr.store.x509.GeneralName;
import de.carne.certmgr.store.x509.GeneralNameType;

/**
 * Model class for general name editing.
 */
public class GeneralNameModel {

	private final SimpleObjectProperty<GeneralNameType> type;
	private final SimpleStringProperty name;

	/**
	 * Construct GeneralNameModel.
	 */
	public GeneralNameModel() {
		this(null, "");
	}

	/**
	 * Construct GeneralNameModel.
	 *
	 * @param type The general name type.
	 * @param name The general name.
	 */
	public GeneralNameModel(GeneralNameType type, String name) {
		assert name != null;

		this.type = new SimpleObjectProperty<>(type);
		this.name = new SimpleStringProperty(name);
	}

	/**
	 * Construct GeneralNameModel.
	 *
	 * @param generalName The name.
	 */
	public GeneralNameModel(GeneralName generalName) {
		assert generalName != null;

		this.type = new SimpleObjectProperty<>(generalName.getType());
		this.name = new SimpleStringProperty(generalName.getNameString());
	}

	/**
	 * @return the type
	 */
	public GeneralNameType getType() {
		return this.type.get();
	}

	/**
	 * @param type the type to set
	 */
	public void setType(GeneralNameType type) {
		this.type.set(type);
	}

	/**
	 * @return the type property.
	 */
	public ObjectProperty<GeneralNameType> typeProperty() {
		return this.type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name.get();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name.set(name);
	}

	/**
	 * @return the name property
	 */
	public StringProperty nameProperty() {
		return this.name;
	}

}
