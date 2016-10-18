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

import java.util.List;
import java.util.function.Function;

import de.carne.certmgr.certs.UserCertStore;
import de.carne.certmgr.certs.UserCertStoreEntry;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

/**
 *
 */
public final class UserCertStoreTreeTableViewHelper<T extends UserCertStoreEntryModel> {

	private final TreeTableView<T> treeTableView;
	private final Function<UserCertStoreEntry, T> modelFactory;

	/**
	 *
	 */
	public UserCertStoreTreeTableViewHelper(TreeTableView<T> treeTableView,
			Function<UserCertStoreEntry, T> modelFactory) {
		assert treeTableView != null;
		assert modelFactory != null;

		this.treeTableView = treeTableView;
		this.modelFactory = modelFactory;
	}

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

	private void updateHelper(TreeItem<T> parent, List<UserCertStoreEntry> entries) {
		for (UserCertStoreEntry entry : entries) {
			TreeItem<T> entryItem = new TreeItem<>(this.modelFactory.apply(entry));

			parent.getChildren().add(entryItem);
			updateHelper(entryItem, entry.issuedEntries());
		}
	}

}
