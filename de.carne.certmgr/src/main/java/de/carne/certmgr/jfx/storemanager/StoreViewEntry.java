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

import java.io.IOException;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.store.CertStore;
import de.carne.certmgr.store.CertStoreEntry;

/**
 * Class representing a certificate store entry in a tree view.
 */
public final class StoreViewEntry implements Comparable<StoreViewEntry> {

	private CertStore store;
	private CertStoreEntry entry;
	private String string;
	private Image image;

	/**
	 * Construct StoreViewEntry for a store.
	 *
	 * @param store The store represented by this entry.
	 */
	public StoreViewEntry(CertStore store) {
		this(store, null);
	}

	/**
	 * Construct StoreViewEntry for a store entry.
	 *
	 * @param entry The store entry represented by this entry.
	 */
	public StoreViewEntry(CertStoreEntry entry) {
		this(entry.getStore(), entry);
	}

	private StoreViewEntry(CertStore store, CertStoreEntry entry) {
		this.store = store;
		this.entry = entry;
		this.string = getString();
		this.image = getImage();
	}

	/**
	 * Get this entry's store.
	 *
	 * @return This entry's store.
	 */
	public CertStore getStore() {
		return this.store;
	}

	/**
	 * Get this entry's certificate entry.
	 *
	 * @return This entry's certificate entry.
	 */
	public CertStoreEntry getEntry() {
		return this.entry;
	}

	/**
	 * Create a tree item for this entry.
	 *
	 * @return The tree item for this entry.
	 */
	public TreeItem<StoreViewEntry> toTreeItem() {
		return new TreeItem<>(this, new ImageView(this.image));
	}

	/**
	 * Update a tree item if anything changed till it's creation.
	 *
	 * @param item The tree item previously created via {@linkplain #toTreeItem()}.
	 * @return true, if an update has been performed.
	 */
	public boolean updateTreeItem(TreeItem<StoreViewEntry> item) {
		String updateString = getString();
		Image updateImage = getImage();
		boolean update = false;

		if (!this.string.equals(updateString)) {
			this.string = updateString;
			// First clear the value to make sure it gets invalidated
			item.setValue(null);
			item.setValue(this);
		}
		if (!this.image.equals(updateImage)) {
			this.image = updateImage;
			item.setGraphic(new ImageView(this.image));
		}
		return update;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(StoreViewEntry o) {
		return this.string.compareTo(o.string);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.string;
	}

	private String getString() {
		StringBuilder entryStringBuffer = new StringBuilder();

		if (this.entry != null) {
			entryStringBuffer.append(this.entry.getName());

			String alias = this.entry.getAlias();

			if (alias != null) {
				entryStringBuffer.append("\n").append("(").append(alias).append(")");
			}
		} else {
			entryStringBuffer.append(this.store.getHome());
		}
		return entryStringBuffer.toString();
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
					entryImage = Images.IMAGE_REVOKEDCRT32;
				} else if (this.entry.hasKey()) {
					entryImage = Images.IMAGE_PRIVATECRT32;
				} else {
					entryImage = Images.IMAGE_PUBLICCRT32;
				}
			} else if (this.entry.hasCSR()) {
				entryImage = Images.IMAGE_CSR32;
			} else {
				entryImage = Images.IMAGE_UNKNOWN32;
			}
		} else {
			entryImage = Images.IMAGE_STORE32;
		}
		return entryImage;
	}

}
