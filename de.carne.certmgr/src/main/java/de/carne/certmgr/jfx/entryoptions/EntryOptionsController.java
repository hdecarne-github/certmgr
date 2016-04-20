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
package de.carne.certmgr.jfx.entryoptions;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.jfx.InputValidator;
import de.carne.certmgr.jfx.InvalidInputException;
import de.carne.certmgr.jfx.help.Help;
import de.carne.certmgr.jfx.help.HelpController;
import de.carne.certmgr.jfx.passwordprompt.PasswordPromptCallback;
import de.carne.certmgr.store.CertStoreEntry;
import de.carne.jfx.StageController;
import de.carne.jfx.messagebox.MessageBoxStyle;
import de.carne.util.Strings;

/**
 * Dialog controller for editing certificate store entry options.
 */
public class EntryOptionsController extends StageController {

	/**
	 * Controller's callback interface.
	 */
	public interface Result {

		/**
		 * Called when the user finishes editing and the selected certificate store entry was updated.
		 *
		 * @param entryParam The updated certificate store entry.
		 */
		public void onEntryUpdate(CertStoreEntry entryParam);

	}

	private Result result = null;
	private CertStoreEntry entry = null;

	@FXML
	TextField ctlSubjectDNInput;

	@FXML
	TextField ctlAliasInput;

	@FXML
	CheckBox ctlChangePasswordOption;

	@FXML
	void onApply(ActionEvent evt) {
		try {
			String alias = validateAndGetAlias();

			this.entry.getStore().renameEntry(this.entry, alias);
			if (this.ctlChangePasswordOption.isSelected()) {
				PasswordPromptCallback oldPassword = PasswordPromptCallback.getPassword(this);
				PasswordPromptCallback newPassword = PasswordPromptCallback.getNewPassword(this);

				this.entry.getStore().changePassword(this.entry, oldPassword, newPassword);
			}
			getStage().close();
			this.result.onEntryUpdate(this.entry);
		} catch (InvalidInputException e) {
			showMessageBox(e.getLocalizedMessage(), null, MessageBoxStyle.ICON_ERROR, MessageBoxStyle.BUTTON_OK);
		} catch (IOException e) {
			showMessageBox(I18N.format(I18N.MESSAGE_APPLYERROR), e, MessageBoxStyle.ICON_ERROR,
					MessageBoxStyle.BUTTON_OK);
		}
	}

	@FXML
	void onCancel(ActionEvent evt) {
		getStage().close();
	}

	@FXML
	void onHelp(ActionEvent evt) {
		try {
			HelpController.showHelp(this, Help.TOPIC_ENTRYOPTIONS);
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.setTitle(I18N.format(I18N.TEXT_TITLE));
		controllerStage.getIcons().addAll(Images.IMAGE_ENTRYOPTIONS16, Images.IMAGE_ENTRYOPTIONS32);
	}

	/**
	 * Begin certificate store entry option editing.
	 *
	 * @param entryParam The certificate store entry whose options should be edited.
	 * @param callback The callback to report the result of the user actions.
	 */
	public void beginStoreOptions(CertStoreEntry entryParam, Result callback) {
		assert entryParam != null;
		assert callback != null;

		this.result = callback;
		this.entry = entryParam;
		this.ctlSubjectDNInput.setText(this.entry.getName());
		this.ctlAliasInput.setText(this.entry.getAlias());
		this.ctlChangePasswordOption.setDisable(!this.entry.hasKey());
		getStage().sizeToScene();
	}

	private String validateAndGetAlias() throws InvalidInputException {
		String aliasInput = InputValidator.notEmpty(I18N.bundle(), I18N.MESSAGE_NOALIAS,
				Strings.safeTrim(this.ctlAliasInput.getText()));

		Path storeHome = this.entry.getStore().getHome();
		Path checkPath;

		try {
			checkPath = storeHome.resolve(aliasInput);
		} catch (InvalidPathException e) {
			throw new InvalidInputException(I18N.format(I18N.MESSAGE_INVALIDALIAS, aliasInput), e);
		}
		if (!storeHome.equals(checkPath.getParent())) {
			throw new InvalidInputException(I18N.format(I18N.MESSAGE_INVALIDALIAS, aliasInput));
		}
		return aliasInput;
	}

}
