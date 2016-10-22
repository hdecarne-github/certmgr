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
import de.carne.certmgr.jfx.resources.Images;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Basic model class for {@link UserCertStoreEntry} objects.
 */
public class UserCertStoreEntryModel implements Comparable<UserCertStoreEntryModel> {

	private final UserCertStoreEntry entry;
	private final StringProperty nameProperty;
	private final ObjectProperty<Node> graphicProperty;
	private final BooleanProperty hasCRTProperty;
	private final BooleanProperty hasKeyProperty;
	private final BooleanProperty hasCSRProperty;
	private final BooleanProperty hasCRLProperty;

	/**
	 * Construct {@code UserCertStoreEntryModel}.
	 *
	 * @param entry The represented certificate store entry.
	 */
	protected UserCertStoreEntryModel(UserCertStoreEntry entry) {
		assert entry != null;

		this.entry = entry;
		this.nameProperty = new SimpleStringProperty(this.entry.getName());
		this.graphicProperty = new SimpleObjectProperty<>(getEntryGraphic(this.entry));
		this.hasCRTProperty = new SimpleBooleanProperty(this.entry.hasCRT());
		this.hasKeyProperty = new SimpleBooleanProperty(this.entry.hasKey());
		this.hasCSRProperty = new SimpleBooleanProperty(this.entry.hasCSR());
		this.hasCRLProperty = new SimpleBooleanProperty(this.entry.hasCRL());
	}

	/**
	 * Get this model's entry.
	 *
	 * @return This model's entry.
	 */
	public final UserCertStoreEntry getEntry() {
		return this.entry;
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
	 * Get the Graphic property value.
	 *
	 * @return The Graphic property value.
	 */
	public final Node getGraphic() {
		return this.graphicProperty.getValue();
	}

	/**
	 * Set the Graphic property value.
	 *
	 * @param graphic The value to set.
	 */
	public final void setGraphic(Node graphic) {
		this.graphicProperty.setValue(graphic);
	}

	/**
	 * Get the Graphic property.
	 *
	 * @return The Graphic property.
	 */
	public final ObjectProperty<Node> graphicProperty() {
		return this.graphicProperty;
	}

	/**
	 * Get the HasCRT property value.
	 *
	 * @return The HasCRT property value.
	 */
	public final Boolean getHasCRT() {
		return this.hasCRTProperty.getValue();
	}

	/**
	 * Set the HasCRT property value.
	 *
	 * @param hasCRT The value to set.
	 */
	public final void setHasCRT(Boolean hasCRT) {
		this.hasCRTProperty.setValue(hasCRT);
	}

	/**
	 * Get the HasCRT property.
	 *
	 * @return The HasCRT property.
	 */
	public final BooleanProperty hasCRTProperty() {
		return this.hasCRTProperty;
	}

	/**
	 * Get the HasKey property value.
	 *
	 * @return The HasKey property value.
	 */
	public final Boolean getHasKey() {
		return this.hasKeyProperty.getValue();
	}

	/**
	 * Set the HasKey property value.
	 *
	 * @param hasKey The value to set.
	 */
	public final void setHashKey(Boolean hasKey) {
		this.hasKeyProperty.setValue(hasKey);
	}

	/**
	 * Get the HasKey property.
	 *
	 * @return The HasKey property.
	 */
	public final BooleanProperty hasKeyProperty() {
		return this.hasKeyProperty;
	}

	/**
	 * Get the HasCSR property value.
	 *
	 * @return The HasCSR property value.
	 */
	public final Boolean getHasCSR() {
		return this.hasCSRProperty.getValue();
	}

	/**
	 * Set the HasCSR property value.
	 *
	 * @param hasCSR The value to set.
	 */
	public final void setHashCSR(Boolean hasCSR) {
		this.hasCSRProperty.setValue(hasCSR);
	}

	/**
	 * Get the HasCSR property.
	 *
	 * @return The HasCSR property.
	 */
	public final BooleanProperty hasCSRProperty() {
		return this.hasCSRProperty;
	}

	/**
	 * Get the HasCRL property value.
	 *
	 * @return The HasCRL property value.
	 */
	public final Boolean getHasCRL() {
		return this.hasCRLProperty.get();
	}

	/**
	 * Set the HasCRL property value.
	 *
	 * @param hasCRL The value to set.
	 */
	public final void setHashCRL(Boolean hasCRL) {
		this.hasCRLProperty.setValue(hasCRL);
	}

	/**
	 * Get the HasCRL property.
	 *
	 * @return The HasCRL property.
	 */
	public final BooleanProperty hasCRLProperty() {
		return this.hasCRLProperty;
	}

	@Override
	public int compareTo(UserCertStoreEntryModel o) {
		return this.nameProperty.get().compareTo(o.nameProperty.get());
	}

	@Override
	public String toString() {
		return this.nameProperty.get();
	}

	private static Node getEntryGraphic(UserCertStoreEntry entry) {
		Image entryImage;

		if (entry.isExternal()) {
			entryImage = Images.EXTERNAL_CRT16;
		} else if (entry.hasCRT()) {
			entryImage = (entry.hasKey() ? Images.PRIVATE_CRT16 : Images.PUBLIC_CRT16);
		} else if (entry.hasCSR()) {
			entryImage = Images.CSR16;
		} else if (entry.hasCRL()) {
			entryImage = Images.CRL16;
		} else {
			entryImage = Images.KEY16;
		}
		return new ImageView(entryImage);
	}

}
