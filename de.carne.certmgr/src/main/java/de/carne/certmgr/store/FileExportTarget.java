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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * File based export target implementation.
 */
public class FileExportTarget implements ExportTarget.ByteData, ExportTarget.StringData {

	private Path filePath;

	/**
	 * Construct FileExportTarget.
	 *
	 * @param filePath The file to export to.
	 */
	public FileExportTarget(Path filePath) {
		assert filePath != null;

		this.filePath = filePath;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.ExportTarget#getName()
	 */
	@Override
	public String getName() {
		return this.filePath.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.ExportTarget.ByteData#putByteData(byte[])
	 */
	@Override
	public void putByteData(byte[] byteData) throws IOException {
		try (OutputStream fileStream = Files.newOutputStream(this.filePath, StandardOpenOption.WRITE,
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			fileStream.write(byteData);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.ExportTarget.StringData#putStringData(java.lang.String)
	 */
	@Override
	public void putStringData(String stringData) throws IOException {
		try (BufferedWriter fileWriter = Files.newBufferedWriter(this.filePath, StandardOpenOption.WRITE,
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			fileWriter.write(stringData);
		}
	}

}
