/*
 * Copyright (c) 2015-2017 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.certs.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

/**
 * Utility class wrapping an I/O resource with a name.
 *
 * @param <T> The I/O resource type.
 */
public final class IOResource<T extends AutoCloseable> implements AutoCloseable {

	private final T io;
	private final String name;

	/**
	 * Construct {@code IOResource}.
	 *
	 * @param io The I/O resource.
	 * @param name The resource's name.
	 */
	public IOResource(T io, String name) {
		assert io != null;
		assert name != null;

		this.io = io;
		this.name = name;
	}

	/**
	 * Get this instance's I/O resource.
	 *
	 * @return This instance's I/O resource.
	 */
	public T io() {
		return this.io;
	}

	/**
	 * Get this resource's name.
	 *
	 * @return This resource's name.
	 */
	public String resource() {
		return this.name;
	}

	@Override
	public void close() throws IOException {
		try {
			this.io.close();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String toString() {
		return resource();
	}

	/**
	 * Create a new {@link InputStream} based I/O resource.
	 *
	 * @param resource The resource name.
	 * @param path The path to open.
	 * @param options The open options.
	 * @return The created I/O resource.
	 * @throws IOException if an error occurs during creation.
	 * @see Files#newInputStream(Path, OpenOption...)
	 */
	public static IOResource<InputStream> newInputStream(String resource, Path path, OpenOption... options)
			throws IOException {
		assert resource != null;
		assert path != null;

		return new IOResource<>(Files.newInputStream(path, options), resource);
	}

	/**
	 * Create a new {@link OutputStream} based I/O resource.
	 *
	 * @param resource The resource name.
	 * @param path The path to open.
	 * @param options The open options.
	 * @return The created I/O resource.
	 * @throws IOException if an error occurs during creation.
	 * @see Files#newOutputStream(Path, OpenOption...)
	 */
	public static IOResource<OutputStream> newOutputStream(String resource, Path path, OpenOption... options)
			throws IOException {
		assert resource != null;
		assert path != null;

		return new IOResource<>(Files.newOutputStream(path, options), resource);
	}

	/**
	 * Wrap an {@link InputStream} based I/O resource in a {@link Reader} based
	 * resource.
	 *
	 * @param in The stream resource to wrap.
	 * @param charset The {@link Charset} to use for text to binary conversion.
	 * @return The wrapped resource.
	 */
	public static IOResource<Reader> streamReader(IOResource<? extends InputStream> in, Charset charset) {
		assert in != null;
		assert charset != null;

		return new IOResource<>(new InputStreamReader(in.io(), charset), in.resource());
	}

	/**
	 * Wrap an {@link OutputStream} based I/O resource in a {@link Writer} based
	 * resource.
	 *
	 * @param out The stream resource to wrap.
	 * @param charset The {@link Charset} to use for text to binary conversion.
	 * @return The wrapped resource.
	 */
	public static IOResource<Writer> streamWriter(IOResource<? extends OutputStream> out, Charset charset) {
		assert out != null;
		assert charset != null;

		return new IOResource<>(new OutputStreamWriter(out.io(), charset), out.resource());
	}

}
