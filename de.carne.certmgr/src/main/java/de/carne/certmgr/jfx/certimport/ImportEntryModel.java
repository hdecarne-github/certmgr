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
package de.carne.certmgr.jfx.certimport;

import java.io.IOException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.store.CertEntry;

/**
 * Class representing a import store entry in a tree view.
 */
public final class ImportEntryModel {

	private CertEntry entry;
	private final SimpleBooleanProperty selected;
	private final SimpleStringProperty name;
	private final SimpleBooleanProperty hasKey;
	private final SimpleBooleanProperty hasCRT;
	private final SimpleBooleanProperty hasCSR;
	private final SimpleBooleanProperty hasCRL;

	/**
	 * Construct empty ImportEntryModel.
	 */
	public ImportEntryModel() {
		this(null, false);
	}

	/**
	 * Construct ImportEntryModel.
	 *
	 * @param entry The import store entry represented by this model.
	 * @param selected The init value for the selected property.
	 */
	public ImportEntryModel(CertEntry entry, boolean selected) {
		this.entry = entry;
		this.selected = new SimpleBooleanProperty(selected);
		this.name = new SimpleStringProperty(this.entry != null ? this.entry.getName() : "");
		this.hasKey = new SimpleBooleanProperty(this.entry != null && this.entry.hasKey());
		this.hasCRT = new SimpleBooleanProperty(this.entry != null && this.entry.hasCRT());
		this.hasCSR = new SimpleBooleanProperty(this.entry != null && this.entry.hasCSR());
		this.hasCRL = new SimpleBooleanProperty(this.entry != null && this.entry.hasCRL());
	}

	/**
	 * @return the entry
	 */
	public CertEntry getEntry() {
		return this.entry;
	}

	/**
	 * @return the selected
	 */
	public Boolean getSelected() {
		return this.selected.getValue();
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(Boolean selected) {
		this.selected.setValue(selected);
	}

	/**
	 * @return the selected property
	 */
	public BooleanProperty selectedProperty() {
		return this.selected;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name.getValue();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name.setValue(name);
	}

	/**
	 * @return the name property
	 */
	public StringProperty nameProperty() {
		return this.name;
	}

	/**
	 * @return the hasKey
	 */
	public Boolean getHasKey() {
		return this.hasKey.getValue();
	}

	/**
	 * @param hasKey the hasKey to set
	 */
	public void setHasKey(Boolean hasKey) {
		this.hasKey.setValue(hasKey);
	}

	/**
	 * @return the hasKey property
	 */
	public BooleanProperty hasKeyProperty() {
		return this.hasKey;
	}

	/**
	 * @return the hasCRT
	 */
	public Boolean getHasCRT() {
		return this.hasCRT.getValue();
	}

	/**
	 * @param hasCRT the hasCRT to set
	 */
	public void setHasCRT(Boolean hasCRT) {
		this.hasCRT.setValue(hasCRT);
	}

	/**
	 * @return the hasCRT property
	 */
	public BooleanProperty hasCRTProperty() {
		return this.hasCRT;
	}

	/**
	 * @return the hasCSR
	 */
	public Boolean getHasCSR() {
		return this.hasCSR.getValue();
	}

	/**
	 * @param hasCSR the hasCSR to set
	 */
	public void setHasCSR(Boolean hasCSR) {
		this.hasCSR.setValue(hasCSR);
	}

	/**
	 * @return the hasCSR property
	 */
	public BooleanProperty hasCSRProperty() {
		return this.hasCSR;
	}

	/**
	 * @return the hasCRL
	 */
	public Boolean getHasCRL() {
		return this.hasCRL.getValue();
	}

	/**
	 * @param hasCRL the hasCRL to set
	 */
	public void setHasCRL(Boolean hasCRL) {
		this.hasCRL.setValue(hasCRL);
	}

	/**
	 * @return the hasCRL property
	 */
	public BooleanProperty hasCRLProperty() {
		return this.hasCRL;
	}

	/**
	 * Create a tree item for this entry.
	 *
	 * @return The tree item for this entry.
	 */
	public TreeItem<ImportEntryModel> toTreeItem() {
		return new TreeItem<>(this, new ImageView(getImage()));
	}

	private Image getImage() {
		Image entryImage;

		if (this.entry != null) {
			if (this.entry.hasCRT()) {
				boolean isRevoked;

				try {
					isRevoked = this.entry.isRevoked();
				} catch (IOException e) {
					// Ignore
					isRevoked = false;
				}
				if (isRevoked) {
					entryImage = Images.IMAGE_REVOKEDCRT16;
				} else if (this.entry.hasKey()) {
					entryImage = Images.IMAGE_PRIVATECRT16;
				} else {
					entryImage = Images.IMAGE_PUBLICCRT16;
				}
			} else if (this.entry.hasCSR()) {
				entryImage = Images.IMAGE_CSR16;
			} else {
				entryImage = Images.IMAGE_UNKNOWN16;
			}
		} else {
			entryImage = Images.IMAGE_STORE16;
		}
		return entryImage;
	}

}
