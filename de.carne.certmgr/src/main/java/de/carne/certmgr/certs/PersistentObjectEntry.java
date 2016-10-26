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
package de.carne.certmgr.certs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;

abstract class PersistentObjectEntry<T> {

	private final Path entryPath;

	private SoftReference<T> cachedEntry;
	private FileTime cachedEntryFileTime;

	PersistentObjectEntry(Path entryPath) {
		this(entryPath, null, null);
	}

	PersistentObjectEntry(Path entryPath, T entry, FileTime entryFileTime) {
		this.entryPath = entryPath;
		this.cachedEntry = new SoftReference<>(entry);
		this.cachedEntryFileTime = entryFileTime;
	}

	protected synchronized T getEntry(PasswordCallback password) throws IOException {
		T entry = this.cachedEntry.get();
		FileTime entryFileTime = Files.getLastModifiedTime(this.entryPath);

		if (entry == null || !entryFileTime.equals(this.cachedEntryFileTime)) {
			try (InputStream entryInput = Files.newInputStream(this.entryPath)) {
				entry = decodeEntryInput(entryInput, password);
				this.cachedEntry = new SoftReference<>(entry);
				this.cachedEntryFileTime = entryFileTime;
			}
		}
		return entry;
	}

	protected abstract T decodeEntryInput(InputStream input, PasswordCallback password) throws IOException;

	protected static <T> T toEntryObject(Class<T> objectType, List<Object> certObjects) throws IOException {
		assert objectType != null;

		int certObjectCount = (certObjects != null ? certObjects.size() : 0);

		if (certObjectCount != 1) {
			throw new IOException(certObjectCount + " cert objects found");
		}

		assert certObjects != null;

		Object certObject = certObjects.get(0);

		if (!objectType.isInstance(certObject)) {
			throw new IOException("Unexpected object type '" + certObject.getClass().getName() + "' (expected '"
					+ objectType.getName() + "'");
		}
		return objectType.cast(certObject);
	}

}
