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
package de.carne.certmgr.jfx;

import java.io.IOException;
import java.io.InputStream;

import javafx.scene.image.Image;

/**
 * Utility class for accessing Image resources.
 */
public final class Images {

	/**
	 * Success icon (16x16)
	 */
	public static final Image IMAGE_SUCCESS16 = getImage(Images.class, "iconSuccess16.png");

	/**
	 * Success icon (32x32)
	 */
	public static final Image IMAGE_SUCCESS32 = getImage(Images.class, "iconSuccess32.png");

	/**
	 * Info icon (16x16)
	 */
	public static final Image IMAGE_INFO16 = getImage(Images.class, "iconInfo16.png");

	/**
	 * Info icon (32x32)
	 */
	public static final Image IMAGE_INFO32 = getImage(Images.class, "iconInfo32.png");

	/**
	 * Warning icon (16x16)
	 */
	public static final Image IMAGE_WARNING16 = getImage(Images.class, "iconWarning16.png");

	/**
	 * Warning icon (32x32)
	 */
	public static final Image IMAGE_WARNING32 = getImage(Images.class, "iconWarning32.png");

	/**
	 * Error icon (16x16)
	 */
	public static final Image IMAGE_ERROR16 = getImage(Images.class, "iconError16.png");

	/**
	 * Error icon (32x32)
	 */
	public static final Image IMAGE_ERROR32 = getImage(Images.class, "iconError32.png");

	/**
	 * Notice icon (16x16)
	 */
	public static final Image IMAGE_NOTICE16 = getImage(Images.class, "iconNotice16.png");

	/**
	 * Help icon (16x16)
	 */
	public static final Image IMAGE_HELP16 = getImage(Images.class, "iconHelp16.png");

	/**
	 * Help icon (32x32)
	 */
	public static final Image IMAGE_HELP32 = getImage(Images.class, "iconHelp32.png");

	/**
	 * Export icon (16x16)
	 */
	public static final Image IMAGE_EXPORT16 = getImage(Images.class, "iconExport16.png");

	/**
	 * Export icon (32x32)
	 */
	public static final Image IMAGE_EXPORT32 = getImage(Images.class, "iconExport32.png");

	/**
	 * Import icon (16x16)
	 */
	public static final Image IMAGE_IMPORT16 = getImage(Images.class, "iconImport16.png");

	/**
	 * Import icon (32x32)
	 */
	public static final Image IMAGE_IMPORT32 = getImage(Images.class, "iconImport32.png");

	/**
	 * Question icon (16x16)
	 */
	public static final Image IMAGE_QUESTION16 = getImage(Images.class, "iconQuestion16.png");

	/**
	 * Question icon (32x32)
	 */
	public static final Image IMAGE_QUESTION32 = getImage(Images.class, "iconQuestion32.png");

	/**
	 * New Store icon (16x16)
	 */
	public static final Image IMAGE_NEWSTORE16 = getImage(Images.class, "iconNewStore16.png");

	/**
	 * New Store icon (32x32)
	 */
	public static final Image IMAGE_NEWSTORE32 = getImage(Images.class, "iconNewStore32.png");

	/**
	 * Store icon (16x16)
	 */
	public static final Image IMAGE_STORE16 = getImage(Images.class, "iconStore16.png");

	/**
	 * Store icon (32x32)
	 */
	public static final Image IMAGE_STORE32 = getImage(Images.class, "iconStore32.png");

	/**
	 * Store Options icon (16x16)
	 */
	public static final Image IMAGE_STOREOPTIONS16 = getImage(Images.class, "iconStoreOptions16.png");

	/**
	 * Store Options icon (32x32)
	 */
	public static final Image IMAGE_STOREOPTIONS32 = getImage(Images.class, "iconStoreOptions32.png");

	/**
	 * Entry Options icon (16x16)
	 */
	public static final Image IMAGE_ENTRYOPTIONS16 = getImage(Images.class, "iconEntryOptions16.png");

	/**
	 * Entry Options icon (32x32)
	 */
	public static final Image IMAGE_ENTRYOPTIONS32 = getImage(Images.class, "iconEntryOptions32.png");

	/**
	 * New CRT icon (16x16)
	 */
	public static final Image IMAGE_NEWCRT16 = getImage(Images.class, "iconNewCRT16.png");

	/**
	 * New CRT icon (32x32)
	 */
	public static final Image IMAGE_NEWCRT32 = getImage(Images.class, "iconNewCRT32.png");

	/**
	 * Re-Sign CRT icon (16x16)
	 */
	public static final Image IMAGE_RESIGNCRT16 = getImage(Images.class, "iconReSignCRT16.png");

	/**
	 * Re-Sign CRT icon (32x32)
	 */
	public static final Image IMAGE_RESIGNCRT32 = getImage(Images.class, "iconReSignCRT32.png");

	/**
	 * Private CRT icon (16x16)
	 */
	public static final Image IMAGE_PRIVATECRT16 = getImage(Images.class, "iconPrivateCRT16.png");

	/**
	 * Private CRT icon (32x32)
	 */
	public static final Image IMAGE_PRIVATECRT32 = getImage(Images.class, "iconPrivateCRT32.png");

	/**
	 * Public CRT icon (16x16)
	 */
	public static final Image IMAGE_PUBLICCRT16 = getImage(Images.class, "iconPublicCRT16.png");

	/**
	 * Public CRT icon (32x32)
	 */
	public static final Image IMAGE_PUBLICCRT32 = getImage(Images.class, "iconPublicCRT32.png");

	/**
	 * Revoked CRT icon (16x16)
	 */
	public static final Image IMAGE_REVOKEDCRT16 = getImage(Images.class, "iconRevokedCRT16.png");

	/**
	 * Revoked CRT icon (32x32)
	 */
	public static final Image IMAGE_REVOKEDCRT32 = getImage(Images.class, "iconRevokedCRT32.png");

	/**
	 * CSR icon (16x16)
	 */
	public static final Image IMAGE_CSR16 = getImage(Images.class, "iconCSR16.png");

	/**
	 * New CSR icon (16x16)
	 */
	public static final Image IMAGE_NEWCSR16 = getImage(Images.class, "iconNewCSR16.png");

	/**
	 * New CSR icon (32x32)
	 */
	public static final Image IMAGE_NEWCSR32 = getImage(Images.class, "iconNewCSR32.png");

	/**
	 * Re-Sign CSR icon (16x16)
	 */
	public static final Image IMAGE_RESIGNCSR16 = getImage(Images.class, "iconReSignCSR16.png");

	/**
	 * Re-Sign CSR icon (32x32)
	 */
	public static final Image IMAGE_RESIGNCSR32 = getImage(Images.class, "iconReSignCSR32.png");

	/**
	 * CSR icon (32x32)
	 */
	public static final Image IMAGE_CSR32 = getImage(Images.class, "iconCSR32.png");

	/**
	 * New CRL icon (16x16)
	 */
	public static final Image IMAGE_NEWCRL16 = getImage(Images.class, "iconNewCRL16.png");

	/**
	 * New CRL icon (32x32)
	 */
	public static final Image IMAGE_NEWCRL32 = getImage(Images.class, "iconNewCRL32.png");

	/**
	 * CRL icon (16x16)
	 */
	public static final Image IMAGE_CRL16 = getImage(Images.class, "iconCRL16.png");

	/**
	 * CRL icon (32x32)
	 */
	public static final Image IMAGE_CRL32 = getImage(Images.class, "iconCRL32.png");

	/**
	 * Unknown certificate object icon (16x16)
	 */
	public static final Image IMAGE_UNKNOWN16 = getImage(Images.class, "iconUnknown16.png");

	/**
	 * Unknown certificate object icon (32x32)
	 */
	public static final Image IMAGE_UNKNOWN32 = getImage(Images.class, "iconUnknown32.png");

	/**
	 * DN edit icon (16x16)
	 */
	public static final Image IMAGE_DNEDIT16 = getImage(Images.class, "iconDNEdit16.png");

	/**
	 * DN edit icon (32x32)
	 */
	public static final Image IMAGE_DNEDIT32 = getImage(Images.class, "iconDNEdit32.png");

	private static Image getImage(Class<?> resourceClass, String resourceName) {
		Image image;

		try (InputStream imageStream = resourceClass.getResourceAsStream(resourceName)) {
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
