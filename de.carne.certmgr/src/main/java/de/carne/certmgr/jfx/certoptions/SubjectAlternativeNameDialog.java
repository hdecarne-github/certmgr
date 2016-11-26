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
package de.carne.certmgr.jfx.certoptions;

import java.io.IOException;

import de.carne.certmgr.certs.x509.SubjectAlternativeNameExtensionData;
import de.carne.jfx.stage.StageController;
import javafx.scene.control.Dialog;

/**
 * Subject Alternative Name dialog.
 */
public class SubjectAlternativeNameDialog extends Dialog<SubjectAlternativeNameExtensionData> {

	private SubjectAlternativeNameDialog(SubjectAlternativeNameController controller) {
		setResultConverter(controller);
	}

	/**
	 * Load the Subject Alternative Name dialog.
	 *
	 * @param owner The stage controller owning this dialog.
	 * @return The constructed controller which is bound to the newly created
	 *         dialog.
	 * @throws IOException if an I/O error occurs during dialog loading.
	 */
	public static SubjectAlternativeNameController load(StageController owner) throws IOException {
		assert owner != null;

		return owner.loadDialog((c) -> new SubjectAlternativeNameDialog(c), SubjectAlternativeNameController.class);
	}

}
