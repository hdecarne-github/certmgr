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
package de.carne.certmgr.jfx.resources;

import java.io.IOException;
import java.io.InputStream;

import javafx.scene.image.Image;

/**
 * Image resource.
 */
public final class Images {
	
	private Images() {
		// Make sure this class is not instantiated from outside
	}

	/**
	 * Store stage icon (16x16).
	 */
	public static final Image STORE16 = getImage("imageStore16.png");

	/**
	 * Store stage icon (32x32).
	 */
	public static final Image STORE32 = getImage("imageStore32.png");

	/**
	 * CertImport stage icon (16x16).
	 */
	public static final Image IMPORT16 = getImage("imageImport16.png");

	/**
	 * CertImport stage icon (32x32).
	 */
	public static final Image IMPORT32 = getImage("imageImport32.png");

	private static Image getImage(String resourceName) {
		Image image;

		try (InputStream imageStream = Images.class.getResourceAsStream(resourceName)) {
			if (imageStream == null) {
				throw new IOException("Unable to access resource: " + resourceName);
			}
			image = new Image(imageStream);
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		return image;
	}

}
