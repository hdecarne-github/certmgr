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
package de.carne.util;

/**
 * Utility class providing string related functions.
 */
public final class Strings {

	/**
	 * Check whether a string is empty (null or "").
	 * 
	 * @param s The string to check.
	 * @return true, if the string is empty (null or "").
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	/**
	 * Check whether a string is not empty (not null and not "").
	 * 
	 * @param s The string to check.
	 * @return true, if the string is not empty (not null and not "").
	 */
	public static boolean notEmpty(String s) {
		return s != null && s.length() > 0;
	}

	/**
	 * Perform a safe trim on a string that may be null.
	 * 
	 * @param s The string to trim or null.
	 * @return The trimmed string or "" if the string is null.
	 */
	public static String safeTrim(String s) {
		return (s != null ? s.trim() : "");
	}

}
