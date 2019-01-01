/*
 * Copyright (c) 2015-2019 Holger de Carne and contributors, All Rights Reserved.
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
	 * Store stage image (16x16).
	 */
	public static final Image STORE16 = getImage("imageStore16.png");

	/**
	 * Store stage image (32x32).
	 */
	public static final Image STORE32 = getImage("imageStore32.png");

	/**
	 * CertImport stage image (16x16).
	 */
	public static final Image IMPORT16 = getImage("imageImport16.png");

	/**
	 * CertImport stage image (32x32).
	 */
	public static final Image IMPORT32 = getImage("imageImport32.png");

	/**
	 * CertOptions stage image (16x16).
	 */
	public static final Image NEWCERT16 = getImage("imageNewCert16.png");

	/**
	 * CertOptions stage image (32x32).
	 */
	public static final Image NEWCERT32 = getImage("imageNewCert32.png");

	/**
	 * Public CRT object image (16x16).
	 */
	public static final Image PUBLIC_CRT16 = getImage("imagePublicCRT16.png");

	/**
	 * Private CRT object image (16x16).
	 */
	public static final Image PRIVATE_CRT16 = getImage("imagePrivateCRT16.png");

	/**
	 * External CRT object image (16x16).
	 */
	public static final Image EXTERNAL_CRT16 = getImage("imageExternalCRT16.png");

	/**
	 * Key object image (16x16).
	 */
	public static final Image KEY16 = getImage("imageKey16.png");

	/**
	 * CSR object image (16x16).
	 */
	public static final Image CSR16 = getImage("imageCSR16.png");

	/**
	 * CRL object image (16x16).
	 */
	public static final Image CRL16 = getImage("imageCRL16.png");

	/**
	 * Invalid CRT overlay image (16x16).
	 */
	public static final Image INVALID_OVERLAY16 = getImage("imageInvalidOverlay16.png");

	/**
	 * Revoked CRT overlay image (16x16).
	 */
	public static final Image REVOKED_OVERLAY16 = getImage("imageRevokedOverlay16.png");

	/**
	 * Trace image (16x16).
	 */
	public static final Image TRACE16 = getImage("imageTrace16.png");

	/**
	 * Debug image (16x16).
	 */
	public static final Image DEBUG16 = getImage("imageDebug16.png");

	/**
	 * Info image (16x16).
	 */
	public static final Image INFO16 = getImage("imageInfo16.png");

	/**
	 * Warning image (16x16).
	 */
	public static final Image WARNING16 = getImage("imageWarning16.png");

	/**
	 * Error image (16x16).
	 */
	public static final Image ERROR16 = getImage("imageError16.png");

	/**
	 * Notice image (16x16).
	 */
	public static final Image NOTICE16 = getImage("imageNotice16.png");

	/**
	 * OK image (16x16).
	 */
	public static final Image OK16 = getImage("imageOK16.png");

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
