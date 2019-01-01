/*
 * Copyright (c) 2015-2019 Holger de Carne and contributors, All Rights Reserved.
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

import de.carne.certmgr.certs.security.CRTValidityPeriod;
import de.carne.certmgr.util.Days;
import javafx.util.StringConverter;

/**
 * {@link StringConverter} implementation for the {@code CRTValidityPeriod}
 * type.
 */
public class CRTValidityPeriodStringConverter extends AbstractPeriodStringConverter<CRTValidityPeriod> {

	@Override
	protected CRTValidityPeriod fromDays(Days period) {
		return new CRTValidityPeriod(period);
	}

}
