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
package de.carne.certmgr.jfx.certoptions;

import java.io.IOException;
import java.util.prefs.BackingStoreException;

import de.carne.certmgr.jfx.resources.Images;
import de.carne.check.Nullable;
import de.carne.jfx.scene.control.Alerts;
import de.carne.jfx.scene.control.DialogController;
import de.carne.jfx.scene.control.ListViewEditor;
import de.carne.jfx.scene.control.Tooltips;
import de.carne.jfx.util.validation.InputValidator;
import de.carne.jfx.util.validation.ValidationException;
import de.carne.util.Late;
import de.carne.util.Strings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * Manage Presets dialog.
 */
public class ManageTemplatesController extends DialogController<Void> implements Callback<ButtonType, Void> {

	private final ListViewEditor<CertOptionsTemplates.Template> templatesEditor = new ListViewEditor<CertOptionsTemplates.Template>() {

		@Override
		@Nullable
		protected CertOptionsTemplates.Template getInput() {
			return getTemplateInput();
		}

		@Override
		protected void setInput(@Nullable CertOptionsTemplates.Template input) {
			setTemplateInput(input);
		}

	};

	private final Late<CertOptionsPreset> presetParam = new Late<>();

	@Nullable
	private CertOptionsTemplates.Template templateEditorSelection = null;

	@SuppressWarnings("null")
	@FXML
	TextField ctlTemplateInput;

	@SuppressWarnings("null")
	@FXML
	Button cmdAddTemplate;

	@SuppressWarnings("null")
	@FXML
	Button cmdApplyTemplate;

	@SuppressWarnings("null")
	@FXML
	Button cmdDeleteTemplate;

	@SuppressWarnings("null")
	@FXML
	Button cmdMoveTemplateUp;

	@SuppressWarnings("null")
	@FXML
	Button cmdMoveTemplateDown;

	@SuppressWarnings("null")
	@FXML
	ListView<CertOptionsTemplates.Template> ctlTemplates;

	@FXML
	void onAddOrApplyTemplate(ActionEvent evt) {
		if (this.templateEditorSelection == null) {
			this.templatesEditor.onAddAction(evt);
		} else {
			this.templatesEditor.onApplyAction(evt);
		}
	}

	@Nullable
	CertOptionsTemplates.Template getTemplateInput() {
		CertOptionsTemplates.Template template = this.templateEditorSelection;

		try {
			String templateName = validateAndGetTemplateName();

			if (template != null) {
				template = new CertOptionsTemplates.Template(templateName, template);
			} else {
				template = new CertOptionsTemplates.Template(templateName, this.presetParam.get());
			}
		} catch (ValidationException e) {
			Tooltips.show(this.ctlTemplateInput, e.getLocalizedMessage(), Images.WARNING16);
		}
		return template;
	}

	void setTemplateInput(@Nullable CertOptionsTemplates.Template template) {
		this.templateEditorSelection = template;
		this.ctlTemplateInput
				.setText(this.templateEditorSelection != null ? this.templateEditorSelection.getName() : "");
	}

	private void onApply(ActionEvent evt) {
		try {
			CertOptionsTemplates.store(this.ctlTemplates.getItems());
		} catch (IOException | BackingStoreException e) {
			Alerts.unexpected(e);
			evt.consume();
		}
	}

	@Override
	protected void setupDialog(Dialog<Void> dialog) {
		dialog.setTitle(ManageTemplatesI18N.formatSTR_STAGE_TITLE());
		this.templatesEditor.init(this.ctlTemplates).setAddCommand(this.cmdAddTemplate)
				.setApplyCommand(this.cmdApplyTemplate).setDeleteCommand(this.cmdDeleteTemplate)
				.setMoveUpCommand(this.cmdMoveTemplateUp).setMoveDownCommand(this.cmdMoveTemplateDown);
		addButtonEventFilter(ButtonType.APPLY, this::onApply);
		this.ctlTemplateInput.requestFocus();
	}

	/**
	 * Initialize the dialog with the current cert options.
	 *
	 * @param preset The current cert options.
	 * @return This controller.
	 */
	public ManageTemplatesController init(CertOptionsPreset preset) {
		this.presetParam.set(preset);
		this.ctlTemplates.getItems().addAll(CertOptionsTemplates.load());
		return this;
	}

	@Override
	@Nullable
	public Void call(@Nullable ButtonType param) {
		return null;
	}

	private String validateAndGetTemplateName() throws ValidationException {
		return InputValidator.notEmpty(Strings.safeTrim(this.ctlTemplateInput.getText()),
				ManageTemplatesI18N::formatSTR_MESSAGE_NO_NAME);
	}

}
