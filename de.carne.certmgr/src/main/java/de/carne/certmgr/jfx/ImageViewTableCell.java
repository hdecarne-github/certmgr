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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * Table cell implementation used for displaying image properties.
 *
 * @param <T> Table model class.
 */
public class ImageViewTableCell<T> extends TableCell<T, Image> {

	private ImageView imageView = new ImageView();

	/**
	 * Construct ImageViewTableCell.
	 */
	public ImageViewTableCell() {
		setGraphic(this.imageView);
	}

	/**
	 * Create callback for image table cell.
	 *
	 * @return A callback for image table cell.
	 */
	public static <T> Callback<TableColumn<T, Image>, TableCell<T, Image>> forTableColumn() {
		return new Callback<TableColumn<T, Image>, TableCell<T, Image>>() {

			@Override
			public TableCell<T, Image> call(TableColumn<T, Image> col) {
				return new ImageViewTableCell<>();
			}

		};
	}

	/*
	 * (non-Javadoc)
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(Image image, boolean empty) {
		this.imageView.setImage(image);
	}

}
