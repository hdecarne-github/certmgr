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

import de.carne.certmgr.Main;
import de.carne.jfx.messagebox.MessageBoxController;
import de.carne.jfx.messagebox.MessageBoxStyle;
import javafx.application.Application;

/**
 * Main class invoking the JFX application.
 */
public class JFXMain extends Main {

	static {
		MessageBoxController.registerImage(MessageBoxStyle.ICON_INFO, Images.IMAGE_INFO16);
		MessageBoxController.registerImage(MessageBoxStyle.ICON_INFO, Images.IMAGE_INFO32);
		MessageBoxController.registerImage(MessageBoxStyle.ICON_WARNING, Images.IMAGE_WARNING16);
		MessageBoxController.registerImage(MessageBoxStyle.ICON_WARNING, Images.IMAGE_WARNING32);
		MessageBoxController.registerImage(MessageBoxStyle.ICON_ERROR, Images.IMAGE_ERROR16);
		MessageBoxController.registerImage(MessageBoxStyle.ICON_ERROR, Images.IMAGE_ERROR32);
		MessageBoxController.registerImage(MessageBoxStyle.ICON_QUESTION, Images.IMAGE_QUESTION16);
		MessageBoxController.registerImage(MessageBoxStyle.ICON_QUESTION, Images.IMAGE_QUESTION32);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.Main#run(java.lang.String[])
	 */
	@Override
	protected int run(String[] args) {
		Application.launch(CertMgrApplication.class, args);
		return 0;
	}

}
