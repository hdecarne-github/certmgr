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
package de.carne.certmgr.jfx.crtoptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.carne.certmgr.jfx.NamedOption;

/**
 * Extension usage selection options.
 */
class ExtensionSelectionOption extends NamedOption<Boolean> {

	/**
	 * Extension is not used at all.
	 */
	public static final ExtensionSelectionOption DISABLED = new ExtensionSelectionOption(null,
			I18N.formatSTR_UNUSED_EXT_LABEL());

	/**
	 * Extension is critical.
	 */
	public static final ExtensionSelectionOption CRITICAL = new ExtensionSelectionOption(Boolean.TRUE,
			I18N.formatSTR_CRITICAL_EXT_LABEL());

	/**
	 * Extension is non-critical.
	 */
	public static final ExtensionSelectionOption NONCRITICAL = new ExtensionSelectionOption(Boolean.FALSE,
			I18N.formatSTR_NON_CRITICAL_EXT_LABEL());

	public static final Collection<ExtensionSelectionOption> VALUES;

	static {
		ArrayList<ExtensionSelectionOption> values = new ArrayList<>();

		values.add(DISABLED);
		values.add(CRITICAL);
		values.add(NONCRITICAL);
		VALUES = Collections.unmodifiableCollection(values);
	}

	ExtensionSelectionOption(Boolean value, String name) {
		super(value, name);
	}

	/**
	 * Check whether this option enables the extension.
	 *
	 * @return true, if this option enables the extension.
	 */
	public boolean isEnabled() {
		return getValue() != null;
	}

	/**
	 * Check whether this option makes the extension critical.
	 *
	 * @return true, if this option makes the extension critical.
	 */
	public boolean isCritical() {
		Boolean value = getValue();

		return value != null && value.booleanValue();
	}

	/**
	 * Get the option represented by the submitted critical flag.
	 *
	 * @param critical The critical flag to get the option for.
	 * @return The option representing the submitted critical flag.
	 */
	public static ExtensionSelectionOption fromCritical(boolean critical) {
		return (critical ? CRITICAL : NONCRITICAL);
	}

}
