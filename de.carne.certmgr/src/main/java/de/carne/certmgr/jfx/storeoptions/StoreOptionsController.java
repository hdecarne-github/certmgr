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
package de.carne.certmgr.jfx.storeoptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import de.carne.certmgr.jfx.Images;
import de.carne.certmgr.jfx.InputValidator;
import de.carne.certmgr.jfx.InvalidInputException;
import de.carne.certmgr.jfx.StageController;
import de.carne.certmgr.jfx.StoreOptions;
import de.carne.certmgr.jfx.TimePeriodOption;
import de.carne.certmgr.jfx.help.Help;
import de.carne.certmgr.jfx.help.HelpController;
import de.carne.certmgr.jfx.messagebox.MessageBoxStyle;
import de.carne.certmgr.store.CertStore;
import de.carne.util.Strings;

/**
 * Dialog controller for editing the store options of a new or an existing certificate store.
 */
public class StoreOptionsController extends StageController {

	/**
	 * Controller's callback interface.
	 */
	public interface Result {

		/**
		 * Called when the user finishes editing by create or save action.
		 *
		 * @param storeParam The created or updated certificate store.
		 */
		public void onStoreOptions(CertStore storeParam);

	}

	private Result result = null;
	private CertStore store = null;

	@FXML
	TextField ctlNameInput;

	@FXML
	TextField ctlFolderInput;

	@FXML
	Button ctlChooseFolderButton;

	@FXML
	ChoiceBox<TimePeriodOption> ctlDefCRTValiditySelection;

	@FXML
	ChoiceBox<TimePeriodOption> ctlDefCRLUpdateSelection;

	@FXML
	ComboBox<String> ctlDefKeyAlgSelection;

	@FXML
	ChoiceBox<Integer> ctlDefKeySizeSelection;

	@FXML
	ComboBox<String> ctlDefSigAlgSelection;

	@FXML
	Button ctlCreateOrSaveButton;

	@FXML
	void onChooseStoreFolder(ActionEvent evt) {
		DirectoryChooser storeFolderChooser = new DirectoryChooser();
		String storeFolderText = Strings.safeTrim(this.ctlFolderInput.getText());
		File storeFolderFile;

		if (Strings.notEmpty(storeFolderText)) {
			storeFolderFile = new File(storeFolderText);
			if (storeFolderFile.isDirectory()) {
				storeFolderChooser.setInitialDirectory(storeFolderFile);
			}
		}
		storeFolderFile = storeFolderChooser.showDialog(getStage());
		if (storeFolderFile != null) {
			this.ctlFolderInput.setText(storeFolderFile.getPath());
		}
	}

	@FXML
	void onCreateOrSave(ActionEvent evt) {
		boolean actionSucceeded = true;
		CertStore storeParam = this.store;

		if (storeParam == null) {
			Path storeHome = null;

			try {
				storeHome = validateAndGetStoreHome();
				storeParam = CertStore.create(storeHome);
			} catch (InvalidInputException e) {
				actionSucceeded = false;
				showMessageBox(e.getLocalizedMessage(), null, MessageBoxStyle.ICON_ERROR, MessageBoxStyle.BUTTON_OK);
			} catch (IOException e) {
				actionSucceeded = false;
				showMessageBox(I18N.format(I18N.MESSAGE_CREATESTORE_ERROR, storeHome), e, MessageBoxStyle.ICON_ERROR,
						MessageBoxStyle.BUTTON_OK);
			}
		}
		if (actionSucceeded) {
			try {
				StoreOptions storeOptions = validateAndGetStoreOptions();
				storeOptions.write(storeParam);
			} catch (InvalidInputException e) {
				actionSucceeded = false;
				showMessageBox(e.getLocalizedMessage(), null, MessageBoxStyle.ICON_ERROR, MessageBoxStyle.BUTTON_OK);
			} catch (IOException e) {
				actionSucceeded = false;
				showMessageBox(I18N.format(I18N.MESSAGE_WRITESTOREOPTIONS_ERROR, storeParam), e,
						MessageBoxStyle.ICON_ERROR, MessageBoxStyle.BUTTON_OK);
			}
		}
		if (actionSucceeded) {
			getStage().close();
			this.result.onStoreOptions(storeParam);
		}
	}

	@FXML
	void onCancel(ActionEvent evt) {
		getStage().close();
	}

	@FXML
	void onHelp(ActionEvent evt) {
		try {
			HelpController.showHelp(this, Help.TOPIC_STOREOPTIONS);
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	void onKeyAlgSelected(String keyAlg) {
		this.ctlDefKeySizeSelection.getItems().clear();
		this.ctlDefKeySizeSelection.getItems().addAll(CertStore.getKeySizes(keyAlg));
		this.ctlDefKeySizeSelection.getSelectionModel().select(CertStore.getDefaultKeySize(keyAlg));
		this.ctlDefSigAlgSelection.getItems().clear();
		this.ctlDefSigAlgSelection.getItems().addAll(CertStore.getSigAlgs(keyAlg));
		this.ctlDefSigAlgSelection.getSelectionModel().select(CertStore.getDefaultSigAlg(keyAlg));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		this.ctlDefKeyAlgSelection.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> property, String oldValue, String newValue) {
				onKeyAlgSelected(newValue);
			}

		});
	}

	/**
	 * Begin certificate store option editing.
	 *
	 * @param storeParam The certificate store whose options should be edited or null if a new store should be setup.
	 * @param callback The callback to report the result of the user actions.
	 */
	public void beginStoreOptions(CertStore storeParam, Result callback) {
		assert callback != null;

		this.result = callback;
		this.store = storeParam;
		this.ctlDefCRTValiditySelection.getItems().addAll(StoreOptions.getCRTValidityPeriods());
		this.ctlDefCRLUpdateSelection.getItems().addAll(StoreOptions.getCRLUpdatePeriods());
		this.ctlDefKeyAlgSelection.getItems().addAll(CertStore.getKeyAlgs());

		StoreOptions storeOptions = new StoreOptions();

		if (this.store != null) {
			getStage().setTitle(getBundle().getString(I18N.TEXT_OPTIONSTITLE));
			getStage().getIcons().addAll(Images.IMAGE_STOREOPTIONS16, Images.IMAGE_STOREOPTIONS32);

			Path storeHome = this.store.getHome();

			this.ctlNameInput.setText(storeHome.getFileName().toString());
			this.ctlNameInput.setDisable(true);
			this.ctlFolderInput.setText(storeHome.getParent().toString());
			this.ctlFolderInput.setDisable(true);
			this.ctlChooseFolderButton.setDisable(true);
			this.ctlCreateOrSaveButton.setText(getBundle().getString(I18N.TEXT_BUTTON_SAVE));
			storeOptions.load(this.store);
		} else {
			getStage().setTitle(getBundle().getString(I18N.TEXT_NEWTITLE));
			getStage().getIcons().addAll(Images.IMAGE_NEWSTORE16, Images.IMAGE_NEWSTORE32);
		}
		this.ctlDefCRTValiditySelection.getSelectionModel().select(storeOptions.getDefCRTValidity());
		this.ctlDefCRLUpdateSelection.getSelectionModel().select(storeOptions.getDefCRLUpdate());
		this.ctlDefKeyAlgSelection.getSelectionModel().select(storeOptions.getDefKeyAlg());
		this.ctlDefKeySizeSelection.getSelectionModel().select(storeOptions.getDefKeySize());
		this.ctlDefSigAlgSelection.getSelectionModel().select(storeOptions.getDefSigAlg());
		getStage().sizeToScene();
	}

	private Path validateAndGetStoreHome() throws InvalidInputException {
		String storeFolderInput = InputValidator.notEmpty(I18N.bundle(), I18N.MESSAGE_NOSTOREFOLDER,
				Strings.safeTrim(this.ctlFolderInput.getText()));
		Path storeFolderPath = InputValidator.isDirectory(I18N.bundle(), I18N.MESSAGE_INVALIDSTOREFOLDER,
				storeFolderInput);
		String storeNameInput = InputValidator.notEmpty(I18N.bundle(), I18N.MESSAGE_NOSTORENAME,
				Strings.safeTrim(this.ctlNameInput.getText()));

		return InputValidator.isPath(I18N.bundle(), I18N.MESSAGE_INVALIDSTORENAME, storeFolderPath, storeNameInput);
	}

	private StoreOptions validateAndGetStoreOptions() throws InvalidInputException {
		TimePeriodOption defCRTValidityInput = InputValidator.notNull(I18N.bundle(), I18N.MESSAGE_NODEFCRTVALIDTY,
				this.ctlDefCRTValiditySelection.getValue());
		TimePeriodOption defCRLUpdateInput = InputValidator.notNull(I18N.bundle(), I18N.MESSAGE_NODEFCRLUPDATE,
				this.ctlDefCRLUpdateSelection.getValue());
		String defKeyAlg = InputValidator.notEmpty(I18N.bundle(), I18N.MESSAGE_NODEFKEYALG,
				this.ctlDefKeyAlgSelection.getValue());
		Integer defKeySize = InputValidator.notNull(I18N.bundle(), I18N.MESSAGE_NODEFKEYSIZE,
				this.ctlDefKeySizeSelection.getValue());
		String defSigAlg = InputValidator.notEmpty(I18N.bundle(), I18N.MESSAGE_NODEFSIGALG,
				this.ctlDefSigAlgSelection.getValue());
		StoreOptions storeOptions = new StoreOptions();

		storeOptions.setDefCRTValidity(defCRTValidityInput);
		storeOptions.setDefCRLUpdate(defCRLUpdateInput);
		storeOptions.setDefKeyAlg(defKeyAlg);
		storeOptions.setDefKeySize(defKeySize.intValue());
		storeOptions.setDefSigAlg(defSigAlg);
		return storeOptions;
	}

}
