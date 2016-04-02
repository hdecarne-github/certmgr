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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

import de.carne.certmgr.util.logging.Log;

/**
 * Folder based export target implementation.
 */
public class FolderExportTarget implements ExportTarget.StringDataMap, ExportTarget.FileList {

	private static final Log LOG = new Log(FolderExportTarget.class);

	private Path folderPath;

	/**
	 * Construct FileExportTarget.
	 *
	 * @param filePath The file to export to.
	 */
	public FolderExportTarget(Path filePath) {
		assert filePath != null;

		this.folderPath = filePath;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.ExportTarget#getName()
	 */
	@Override
	public String getName() {
		return this.folderPath.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.ExportTarget.StringDataMap#putStringData(java.util.Map)
	 */
	@Override
	public void putStringData(Map<String, String> stringDataMap) throws IOException {
		assert stringDataMap != null;

		for (Map.Entry<String, String> entry : stringDataMap.entrySet()) {
			Path file = resolveUnique(entry.getKey());

			try (BufferedWriter fileWriter = Files.newBufferedWriter(file, StandardOpenOption.WRITE,
					StandardOpenOption.CREATE_NEW)) {
				fileWriter.write(entry.getValue());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.ExportTarget.FileList#putFileList(java.util.List)
	 */
	@Override
	public void putFileList(List<Path> fileList) throws IOException {
		assert fileList != null;

		for (Path srcFile : fileList) {
			Path dstFile = resolveUnique(srcFile.getFileName().toString());

			LOG.info(null, "Copying ''{0}'' -> ''{1}''...", srcFile, dstFile);
			Files.copy(srcFile, dstFile);
		}
	}

	private Path resolveUnique(String fileName) {
		Path uniqueFile = this.folderPath.resolve(fileName);
		int retryIndex = 1;

		while (Files.exists(uniqueFile)) {
			int extIndex = fileName.lastIndexOf('.');
			String retrySuffix = "(" + retryIndex + ")";

			retryIndex++;

			String retryFileName;

			if (extIndex > 0) {
				retryFileName = fileName.substring(0, extIndex) + retrySuffix + fileName.substring(extIndex);
			} else {
				retryFileName = fileName + retrySuffix;
			}
			uniqueFile = this.folderPath.resolve(retryFileName);
		}
		return uniqueFile;
	}

}
