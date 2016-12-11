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
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;

import org.checkerframework.checker.nullness.qual.Nullable;

abstract class PersistentCertObjectHolder<T> implements CertObjectHolder<T> {

	private final Path path;

	private SoftReference<T> cached;
	private FileTime cachedFileTime;

	protected PersistentCertObjectHolder(Path path) {
		this.path = path;
	}

	protected PersistentCertObjectHolder(Path path, @Nullable T object, @Nullable FileTime fileTime) {
		this.path = path;
		this.cached = new SoftReference<>(object);
		this.cachedFileTime = (fileTime != null ? fileTime : FileTime.fromMillis(0));
	}

	@Override
	public Path path() {
		return this.path;
	}

	@Override
	public T get() throws IOException {
		T object = this.cached.get();
		FileTime pathFileTime = Files.getLastModifiedTime(this.path);

		if (object == null || !this.cachedFileTime.equals(pathFileTime)) {
			try (InputStream in = Files.newInputStream(this.path, StandardOpenOption.READ)) {
				object = read(in);
			}
			this.cached = new SoftReference<>(object);
			this.cachedFileTime = pathFileTime;
		}
		return object;
	}

	protected abstract T read(InputStream in) throws IOException;

}
