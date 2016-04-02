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
package de.carne.certmgr.store;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Interface defining the different export target types.
 */
public interface ExportTarget {

	/**
	 * Get the target's name.
	 *
	 * @return The target's name.
	 */
	public String getName();

	/**
	 * Byte data export target.
	 */
	public interface ByteData extends ExportTarget {

		/**
		 * Put byte data into the export target.
		 *
		 * @param byteData The byte data to put into the target.
		 * @throws IOException if an I/O error occurs while processing the byte data.
		 */
		public void putByteData(byte[] byteData) throws IOException;

	}

	/**
	 * String data export target.
	 */
	public interface StringData extends ExportTarget {

		/**
		 * Put string data into the export target.
		 *
		 * @param stringData The string data to put into the target.
		 * @throws IOException if an I/O error occurs while processing the string data.
		 */
		public void putStringData(String stringData) throws IOException;

	}

	/**
	 * String data map export target.
	 */
	public interface StringDataMap extends ExportTarget {

		/**
		 * Put string data map into the export target.
		 *
		 * @param stringDataMap The string data map to put into the target.
		 * @throws IOException if an I/O error occurs while processing the string data.
		 */
		public void putStringData(Map<String, String> stringDataMap) throws IOException;

	}

	/**
	 * File list export target.
	 */
	public interface FileList extends ExportTarget {

		/**
		 * Put a file list into the export target.
		 *
		 * @param fileList The file list to put into the target.
		 * @throws IOException if an I/O error occurs while processing the file list.
		 */
		public void putFileList(List<Path> fileList) throws IOException;

	}

}
