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

import java.util.ArrayList;
import java.util.Collection;

import javafx.stage.FileChooser;
import de.carne.certmgr.store.CertFileFormat;

/**
 * Helper functions for handling the certificate file format enum.
 */
public final class CertFileFormats {

	/**
	 * Setup a FileChooser filter for a single format.
	 * <p>
	 * The created filter list contains a filter for the submitted format as well as a filter for all files.
	 * </p>
	 * 
	 * @param format The format to create the filter for.
	 * @return The created filter.
	 */
	public static Collection<FileChooser.ExtensionFilter> getFileChooserFilters(CertFileFormat format) {
		assert format != null;

		ArrayList<FileChooser.ExtensionFilter> filters = new ArrayList<>(2);
		String description = I18N.format(I18N.TEXT_FILTERFORMAT, format.name());
		String[] rawExtensions = format.getExtensions();
		ArrayList<String> filterExtensions = new ArrayList<>(rawExtensions.length);

		for (String rawExtension : rawExtensions) {
			filterExtensions.add("*" + rawExtension);
		}
		filters.add(new FileChooser.ExtensionFilter(description, filterExtensions));
		filters.add(new FileChooser.ExtensionFilter(I18N.format(I18N.TEXT_FILTERALL), "*"));
		return filters;
	}

	/**
	 * Setup a FileChooser filter for all formats.
	 * <p>
	 * The created filter list contains filters for the all formats as well as a filter for all files.
	 * </p>
	 * 
	 * @return The created filter.
	 */
	public static Collection<FileChooser.ExtensionFilter> getFileChooserFilters() {
		CertFileFormat[] formats = CertFileFormat.values();
		ArrayList<FileChooser.ExtensionFilter> filters = new ArrayList<>(formats.length + 1);

		filters.add(new FileChooser.ExtensionFilter(I18N.format(I18N.TEXT_FILTERALL), "*"));
		for (CertFileFormat format : formats) {
			String description = I18N.format(I18N.TEXT_FILTERFORMAT, format.name());
			String[] rawExtensions = format.getExtensions();
			ArrayList<String> filterExtensions = new ArrayList<>(rawExtensions.length);

			for (String rawExtension : rawExtensions) {
				filterExtensions.add("*" + rawExtension);
			}
			filters.add(new FileChooser.ExtensionFilter(description, filterExtensions));
		}
		return filters;
	}

}
