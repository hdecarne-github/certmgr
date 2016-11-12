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
package de.carne.certmgr.jfx.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

/**
 * Helper class for building up and updating the {@link TreeTableView} model for
 * a certificate store.
 *
 * @param <T> The model type.
 */
public final class UserCertStoreTreeTableViewHelper<T extends UserCertStoreEntryModel> {

	private class TreeItemComparator implements Comparator<TreeItem<T>> {

		TreeItemComparator() {
			// Just to make the construct visible to outer class
		}

		@Override
		public int compare(TreeItem<T> o1, TreeItem<T> o2) {
			return o1.getValue().compareTo(o2.getValue());
		}

	}

	private final TreeItemComparator comparator = new TreeItemComparator();
	private final TreeTableView<T> treeTableView;
	private final Function<UserCertStoreEntry, T> modelFactory;

	/**
	 * Construct {@code UserCertStoreTreeTableViewHelper}.
	 *
	 * @param treeTableView The {@link TreeTableView} to update the model for.
	 * @param modelFactory The factory for model cration.
	 */
	public UserCertStoreTreeTableViewHelper(TreeTableView<T> treeTableView,
			Function<UserCertStoreEntry, T> modelFactory) {
		assert treeTableView != null;
		assert modelFactory != null;

		this.treeTableView = treeTableView;
		this.modelFactory = modelFactory;
	}

	/**
	 * Update the {@link TreeTableView}'s model.
	 *
	 * @param store The certificate store providing the data to display.
	 */
	public void update(UserCertStore store) {
		if (store != null) {
			TreeItem<T> root = this.treeTableView.getRoot();

			if (root == null) {
				root = new TreeItem<>();
				this.treeTableView.setRoot(root);
			}
			updateHelper(root, store.getRootEntries());
		} else {
			this.treeTableView.setRoot(null);
		}
	}

	private void updateHelper(TreeItem<T> parent, Set<UserCertStoreEntry> entries) {
		ObservableList<TreeItem<T>> items = parent.getChildren();
		List<TreeItem<T>> itemsToRemove = new ArrayList<>(items.size());
		Map<UserCertStoreEntry, TreeItem<T>> itemsToUpdate = new HashMap<>(items.size());

		for (TreeItem<T> item : items) {
			if (entries.contains(item.getValue())) {
				// entry does still exist -> remember for update
				itemsToUpdate.put(item.getValue().getEntry(), item);
			} else {
				// entry no longer in store -> remember for remove
				itemsToRemove.add(item);
			}
		}
		items.removeAll(itemsToRemove);
		for (UserCertStoreEntry entry : entries) {
			TreeItem<T> entryItem = itemsToUpdate.get(entry);

			if (entryItem == null) {
				entryItem = new TreeItem<>(this.modelFactory.apply(entry));
				entryItem.graphicProperty().bind(entryItem.getValue().graphicProperty());
				items.add(entryItem);
			}
			updateHelper(entryItem, entry.issuedEntries());
		}
		items.sort(this.comparator);
	}

}
