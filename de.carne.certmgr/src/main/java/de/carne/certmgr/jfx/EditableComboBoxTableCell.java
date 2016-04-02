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

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.Callback;

/**
 * Simple wrapper for ComboBoxTableCell standard class setting the cell's comboboxEditable property to true.
 *
 * @param <S> The type of the elements contained within the Table.
 * @param <T> The type of the elements contained within the TableColumn.
 */
public class EditableComboBoxTableCell<S, T> extends TableCell<S, T> {

	private EditableComboBoxTableCell() {
		// Just to prevent instance creation
	}

	/**
	 * Create a ComboxBox cell factory for editable ComboBoxes.
	 *
	 * @param items The ComboBox items.
	 * @return The Callback responsbile for cell creation.
	 * @see ComboBoxTableCell#forTableColumn(T...)
	 */
	@SafeVarargs
	public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(T... items) {
		return new Callback<TableColumn<S, T>, TableCell<S, T>>() {
			private Callback<TableColumn<S, T>, TableCell<S, T>> callback = ComboBoxTableCell.forTableColumn(items);

			@Override
			public TableCell<S, T> call(TableColumn<S, T> param) {
				ComboBoxTableCell<S, T> cell = (ComboBoxTableCell<S, T>) this.callback.call(param);

				cell.setComboBoxEditable(true);
				return cell;
			}
		};
	}

}
