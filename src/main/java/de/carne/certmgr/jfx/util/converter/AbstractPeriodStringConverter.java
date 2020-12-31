/*
 * Copyright (c) 2015-2021 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr.jfx.util.converter;

import java.time.Period;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.certmgr.certs.security.AbstractPeriod;
import de.carne.certmgr.util.Days;
import de.carne.util.Strings;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 * Utility class used to support editing of {@link AbstractPeriod} based values.
 *
 * @param <T> The type to edit.
 */
public abstract class AbstractPeriodStringConverter<T extends AbstractPeriod> extends StringConverter<T> {

	private final static Pattern STRING_PATTERN = Pattern.compile(".+ \\[(.+)\\]");

	/**
	 * Attach this converter to a {@link ComboBox}.
	 *
	 * @param comboBox The {@link ComboBox} to attach to.
	 */
	public void attach(ComboBox<T> comboBox) {
		comboBox.setConverter(this);
		if (comboBox.isEditable()) {

			TextField editor = comboBox.getEditor();

			editor.focusedProperty().addListener((p, o, n) -> onFocusedChanged(editor, n));
		}
	}

	private void onFocusedChanged(TextField textField, Boolean focused) {
		if (focused.booleanValue()) {
			Matcher stringPatternMatcher = STRING_PATTERN.matcher(textField.getText());

			if (stringPatternMatcher.matches()) {
				textField.setText(stringPatternMatcher.group(1));
			}
		}
	}

	@Override
	public String toString(@Nullable T object) {
		Days days = Objects.requireNonNull(object).days();

		return days.toLocalizedString() + " [" + days.toString() + "]";
	}

	@Override
	public T fromString(@Nullable String string) {
		Matcher stringPatternMatcher = STRING_PATTERN.matcher(Strings.safe(string));
		String periodString = (stringPatternMatcher.matches() ? stringPatternMatcher.group(1) : string);

		return fromDays(new Days(Period.parse(periodString)));
	}

	/**
	 * Construct target object from {@link Days} value.
	 *
	 * @param period The days value to use.
	 * @return The created target object.
	 */
	protected abstract T fromDays(Days period);

}
