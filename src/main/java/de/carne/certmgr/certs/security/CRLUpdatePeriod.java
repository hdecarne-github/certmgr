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

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.check.Check;
import de.carne.certmgr.util.Days;
import de.carne.jfx.util.DefaultSet;

/**
 * CRL Update Period provisioning.
 */
public class CRLUpdatePeriod extends AbstractPeriod {

	/**
	 * Construct {@code CRLUpdatePeriod}.
	 *
	 * @param period The period value.
	 */
	public CRLUpdatePeriod(Days period) {
		super(period);
	}

	/**
	 * Get the standard CRL Update Periods.
	 *
	 * @param defaultHint The default to return (may be {@code null}). If this period is contained in the default set,
	 * it is also set as the default.
	 * @return The standard CRL Update Periods.
	 */
	public static DefaultSet<CRLUpdatePeriod> getDefaultSet(@Nullable Days defaultHint) {
		DefaultSet<Days> defaultPeriods = SecurityDefaults.getCRLUpdatedPeriods();
		DefaultSet<CRLUpdatePeriod> crlUpdatePeriods = new DefaultSet<>();

		if (defaultHint != null) {
			crlUpdatePeriods.addDefault(new CRLUpdatePeriod(defaultHint));
		} else {
			crlUpdatePeriods.addDefault(new CRLUpdatePeriod(Check.notNull(defaultPeriods.getDefault())));
		}
		for (Days period : defaultPeriods) {
			crlUpdatePeriods.add(new CRLUpdatePeriod(period));
		}
		return crlUpdatePeriods;
	}

}
