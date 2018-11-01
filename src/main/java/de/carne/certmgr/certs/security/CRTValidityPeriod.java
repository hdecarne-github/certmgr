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
package de.carne.certmgr.certs.security;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.certmgr.util.Days;
import de.carne.jfx.util.DefaultSet;

/**
 * CRT Validity Period provisioning.
 */
public class CRTValidityPeriod extends AbstractPeriod {

	/**
	 * Construct {@code CRTValidityPeriod}.
	 *
	 * @param period The period value.
	 */
	public CRTValidityPeriod(Days period) {
		super(period);
	}

	/**
	 * Get the standard CRT Validity Periods.
	 *
	 * @param defaultHint The default to return (may be {@code null}). If this period is contained in the default set,
	 * it is also set as the default.
	 * @return The standard CRT Validity Periods.
	 */
	public static DefaultSet<CRTValidityPeriod> getDefaultSet(@Nullable Days defaultHint) {
		DefaultSet<Days> defaultPeriods = SecurityDefaults.getCRTValidityPeriods();
		DefaultSet<CRTValidityPeriod> crlUpdatePeriods = new DefaultSet<>();

		if (defaultHint != null) {
			crlUpdatePeriods.addDefault(new CRTValidityPeriod(defaultHint));
		} else {
			crlUpdatePeriods.addDefault(new CRTValidityPeriod(Objects.requireNonNull(defaultPeriods.getDefault())));
		}
		for (Days period : defaultPeriods) {
			crlUpdatePeriods.add(new CRTValidityPeriod(period));
		}
		return crlUpdatePeriods;
	}

}
