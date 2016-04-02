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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * This class provides an abstract way to access all kind of data sources for filling up an import store or the like.
 */
public abstract class ImportSource {

	private static final Charset PEM_CHARSET = Charset.forName("US-ASCII");

	private String resource;
	private CertFileFormat formatHint;

	/**
	 * Create an import source from string data.
	 *
	 * @param data The data to access via the import source.
	 * @param resource The resource name associated with the import source data.
	 * @param formatHint An optional format hint for data decoding.
	 * @return The created import source.
	 */
	public static ImportSource fromData(String data, String resource, CertFileFormat formatHint) {
		assert data != null;
		assert resource != null;

		return new ImportSource(resource, formatHint) {

			private String stringData = data;

			@Override
			public String getStringData() throws IOException {
				return this.stringData;
			}

		};
	}

	/**
	 * Create an import source from byte data.
	 *
	 * @param data The data to access via the import source.
	 * @param resource The resource name associated with the import source data.
	 * @param formatHint An optional format hint for data decoding.
	 * @return The created import source.
	 */
	public static ImportSource fromData(byte[] data, String resource, CertFileFormat formatHint) {
		assert data != null;
		assert resource != null;

		return new ImportSource(resource, formatHint) {

			private byte[] byteData = data;

			@Override
			public byte[] getByteData() throws IOException {
				return this.byteData;
			}

		};
	}

	/**
	 * Create an import source from a file.
	 *
	 * @param filePath The file to access via the import source.
	 * @return The created import source.
	 */
	public static ImportSource fromFile(Path filePath) {
		assert filePath != null;

		return fromFile(filePath, filePath.toString());
	}

	/**
	 * Create an import source from a file.
	 *
	 * @param filePath The file to access via the import source.
	 * @param resource The (optional) resource name associated with the import source data. If null the file name is
	 *            used instead.
	 * @return The created import source.
	 */
	public static ImportSource fromFile(Path filePath, String resource) {
		assert filePath != null;

		CertFileFormat formatHint = guessFormatFromName(filePath.getFileName().toString());

		return new ImportSource((resource != null ? resource : filePath.toString()), formatHint) {
			private Path byteDataFilePath = filePath;
			private byte[] byteData = null;

			@Override
			public byte[] getByteData() throws IOException {
				if (this.byteData == null) {
					this.byteData = Files.readAllBytes(this.byteDataFilePath);
				}
				return this.byteData;
			}

		};
	}

	/**
	 * Create import source from multiple files.
	 *
	 * @param files The file to access via the import sources.
	 * @return The created import sources.
	 * @throws IOException if an I/O error occurs while reading the files' data.
	 */
	public static ImportSource[] fromFiles(Collection<File> files) throws IOException {
		return fromFiles(files, null);
	}

	/**
	 * Create import source from multiple files.
	 *
	 * @param files The files to access via the import sources.
	 * @param resourcePattern The (optional) format pattern to use for resource name creation. If null the file names
	 *            are used instead.
	 * @return The created import sources.
	 */
	public static ImportSource[] fromFiles(Collection<File> files, String resourcePattern) {
		assert files != null;

		ArrayList<ImportSource> sources = new ArrayList<>(files.size());

		for (File file : files) {
			sources.add(fromFile(Paths.get(file.getPath())));
		}
		return sources.toArray(new ImportSource[sources.size()]);
	}

	/**
	 * Create import source from multiple files.
	 *
	 * @param files The files to access via the import sources.
	 * @return The created import sources.
	 */
	public static ImportSource[] fromFiles(Stream<Path> files) {
		assert files != null;

		ArrayList<ImportSource> sources = new ArrayList<>();

		files.forEach(new Consumer<Path>() {
			private ArrayList<ImportSource> sources2 = sources;

			@Override
			public void accept(Path path) {
				if (Files.isRegularFile(path)) {
					this.sources2.add(ImportSource.fromFile(path));
				}
			}

		});
		return sources.toArray(new ImportSource[sources.size()]);
	}

	/**
	 * Create import source from a URL.
	 *
	 * @param url The URL to access via the import sources.
	 * @return The created import sources.
	 */
	public static ImportSource fromURL(URL url) {
		assert url != null;

		CertFileFormat formatHint = guessFormatFromName(url.toExternalForm());

		return new ImportSource(url.toExternalForm(), formatHint) {
			private URL byteDataURL = url;

			@Override
			public byte[] getByteData() throws IOException {
				byte[] byteData = new byte[0];
				byte[] buffer = new byte[4096];

				try (InputStream byteDataStream = this.byteDataURL.openStream()) {
					int read;

					while ((read = byteDataStream.read(buffer)) >= 0) {
						if (read > 0) {
							byte[] tmpByteData = new byte[byteData.length + read];

							System.arraycopy(byteData, 0, tmpByteData, 0, byteData.length);
							System.arraycopy(buffer, 0, tmpByteData, byteData.length, read);
							byteData = tmpByteData;
						}
					}
				}
				return byteData;
			}

		};
	}

	/**
	 * Construct ImportStoreSource.
	 *
	 * @param resource The resource name associated with the import source data.
	 * @param formatHint An optional format hint for data decoding.
	 */
	protected ImportSource(String resource, CertFileFormat formatHint) {
		assert resource != null;

		this.resource = resource;
		this.formatHint = formatHint;
	}

	/**
	 * Get the resource name associated with the import source's data.
	 *
	 * @return The resource name associated with the import source's data.
	 */
	public String getResource() {
		return this.resource;
	}

	/**
	 * Get the available format hint for data decoding.
	 *
	 * @return The available format hint for data decoding. May be null if no hint is available.
	 */
	public CertFileFormat getFormatHint() {
		return this.formatHint;
	}

	/**
	 * Get the import source's data as a string.
	 *
	 * @return The import source's string data or null, if the data is not available as a string.
	 * @throws IOException if an I/O error occurs while retrieving the data.
	 */
	public String getStringData() throws IOException {
		String stringData = null;
		byte[] byteData = getByteData();

		if (byteData != null) {
			try {
				stringData = PEM_CHARSET.newDecoder().decode(ByteBuffer.wrap(byteData)).toString();
			} catch (CharacterCodingException e) {
				// Nothing to do here
			}
		}
		return stringData;
	}

	/**
	 * Get the import source's data as a byte array.
	 *
	 * @return The import source's byte data or null, if the data is not available as a byte array.
	 * @throws IOException if an I/O error occurs while retrieving the data.
	 */
	public byte[] getByteData() throws IOException {
		return null;
	}

	private static CertFileFormat guessFormatFromName(String fileName) {
		String lowerFileName = fileName.toLowerCase();
		CertFileFormat guessed = null;

		for (CertFileFormat format : CertFileFormat.values()) {
			for (String formatExtension : format.getExtensions()) {
				if (lowerFileName.endsWith(formatExtension)) {
					guessed = format;
					break;
				}
			}
			if (guessed != null) {
				break;
			}
		}
		return guessed;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.resource;
	}

}
