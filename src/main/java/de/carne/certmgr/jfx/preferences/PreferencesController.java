/*
 * Copyright (c) 2015-2018 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.jfx.preferences;

import java.util.prefs.BackingStoreException;

import de.carne.certmgr.jfx.store.UserPreferences;
import de.carne.check.Nullable;
import de.carne.jfx.scene.control.Alerts;
import de.carne.jfx.scene.control.DialogController;
import de.carne.util.Late;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

/**
 * Preferences dialog.
 */
public class PreferencesController extends DialogController<UserPreferences>
		implements Callback<ButtonType, UserPreferences> {

	private final Late<UserPreferences> preferencesParam = new Late<>();

	@SuppressWarnings("null")
	@FXML
	CheckBox ctlExpertModeOption;

	@Override
	protected void setupDialog(Dialog<UserPreferences> dialog) {
		dialog.setTitle(PreferencesI18N.formatSTR_STAGE_TITLE());
	}

	/**
	 * Initialize the preferences edited by the dialog.
	 *
	 * @param preferences The initial preferences.
	 * @return This controller.
	 */
	public PreferencesController init(UserPreferences preferences) {
		this.preferencesParam.init(preferences);
		this.ctlExpertModeOption.setSelected(preferences.expertMode.getBoolean(false));
		return this;
	}

	@Override
	@Nullable
	public UserPreferences call(@Nullable ButtonType param) {
		UserPreferences dialogResult = null;

		if (param != null && ButtonType.APPLY.getButtonData() == param.getButtonData()) {
			this.preferencesParam.get().expertMode.put(this.ctlExpertModeOption.isSelected());
			try {
				this.preferencesParam.get().sync();
				dialogResult = this.preferencesParam.get();
			} catch (BackingStoreException e) {
				Alerts.unexpected(e).showAndWait();
			}
		}
		return dialogResult;
	}

}
