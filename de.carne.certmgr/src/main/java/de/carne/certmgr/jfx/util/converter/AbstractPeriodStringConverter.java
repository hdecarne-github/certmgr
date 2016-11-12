/*
 * Copyright (c) 2015-2016 Holger de Carne and contributors, All Rights Reserved.
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.carne.certmgr.certs.security.AbstractPeriod;
import de.carne.certmgr.util.Days;
import javafx.util.StringConverter;

/**
 * Abstract {@link StringConverter} implementation for {@link AbstractPeriod}
 * based classes.
 *
 * @param <T> The type to convert.
 */
public abstract class AbstractPeriodStringConverter<T extends AbstractPeriod> extends StringConverter<T> {

	private final static Pattern STRING_PATTERN = Pattern.compile(".+ \\[(.+)\\]");

	@Override
	public String toString(T object) {
		return object.days().toLocalizedString() + " [" + object.days().toString() + "]";
	}

	@Override
	public T fromString(String string) {
		Matcher stringPatternMatcher = STRING_PATTERN.matcher(string);
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
