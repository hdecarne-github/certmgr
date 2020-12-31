/*
 * Copyright (c) 2015-2021 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.certs.spi;

/**
 * Basic interface for all SPIs which provide file access.
 */
public interface FileAccessProvider {

	/**
	 * Get this provider's file type.
	 *
	 * @return This provider's file type.
	 */
	String fileType();

	/**
	 * Get the usual file name extensions used by this provider.
	 * <p>
	 * Each extension is a glob pattern and suitable for file name matching.
	 *
	 * @return The usual file name extensions used by this provider.
	 */
	String[] fileExtensionPatterns();

	/**
	 * Get the file extension for a specific certificate object type.
	 *
	 * @param cls The certificate object type to get the extension for.
	 * @return The extension suitable for the submitted certificate object type
	 *         or a default extension.
	 */
	String fileExtension(Class<?> cls);

}
