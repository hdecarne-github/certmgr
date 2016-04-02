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
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

/**
 * CertObject implementation retrieving data from a file.
 */
abstract class StoredCertObject<T> extends CertObject<T> {

	private Path objectFile;
	private FileTime objectFileTime;
	private SoftReference<T> objectRef;

	StoredCertObject(String name, Path objectFile, FileTime objectFileTime, T object) {
		super(name);

		assert objectFile != null;
		assert objectFileTime != null;

		this.objectFile = objectFile;
		this.objectFileTime = objectFileTime;
		this.objectRef = new SoftReference<>(object);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.CertObject#getObject()
	 */
	@Override
	public T getObject() throws IOException {
		FileTime currentObjectFileTime = Files.getLastModifiedTime(this.objectFile);

		T object = (currentObjectFileTime.equals(this.objectFileTime) ? this.objectRef.get() : null);

		if (object == null) {
			object = readObject(this.objectFile);
			this.objectFileTime = currentObjectFileTime;

			assert object != null;

			this.objectRef = new SoftReference<>(object);
		}
		return object;
	}

	/**
	 * Read the object.
	 *
	 * @param objectFile The file to read from.
	 * @return The read object.
	 * @throws IOException if an I/O error occurs while reading the object.
	 */
	@SuppressWarnings("hiding")
	protected abstract T readObject(Path objectFile) throws IOException;

}
