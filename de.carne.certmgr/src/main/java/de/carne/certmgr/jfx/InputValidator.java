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
package de.carne.certmgr.jfx;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.security.auth.x500.X500Principal;

import de.carne.certmgr.store.x500.X500Names;
import de.carne.util.Strings;

/**
 * Utility class for input validation.
 */
public final class InputValidator {

	/**
	 * Ensure an input flag is true.
	 *
	 * @param bundle The resource bundle with the validation message.
	 * @param messageKey The validation message key.
	 * @param in The input flag to validate.
	 * @param arguments The message arguments.
	 * @return The validated flag (always true).
	 * @throws InvalidInputException if the validation fails.
	 */
	public static boolean isTrue(ResourceBundle bundle, String messageKey, boolean in, Object... arguments)
			throws InvalidInputException {
		if (!in) {
			throw new InvalidInputException(formatMessage(bundle, messageKey, arguments));
		}
		return in;
	}

	/**
	 * Ensure an input object is not null.
	 *
	 * @param bundle The resource bundle with the validation message.
	 * @param messageKey The validation message key.
	 * @param in The input object to validate.
	 * @return The validated object.
	 * @throws InvalidInputException if the validation fails.
	 */
	public static <T> T notNull(ResourceBundle bundle, String messageKey, T in) throws InvalidInputException {
		if (in == null) {
			throw new InvalidInputException(formatMessage(bundle, messageKey));
		}
		return in;
	}

	/**
	 * Ensure string input is not an empty string (null or "").
	 *
	 * @param bundle The resource bundle with the validation message.
	 * @param messageKey The validation message key.
	 * @param in The string input to validate.
	 * @return The validated string.
	 * @throws InvalidInputException if the validation fails.
	 */
	public static String notEmpty(ResourceBundle bundle, String messageKey, String in) throws InvalidInputException {
		if (Strings.isEmpty(in)) {
			throw new InvalidInputException(formatMessage(bundle, messageKey));
		}
		return in;
	}

	/**
	 * Ensure string input denotes an existing regular file.
	 *
	 * @param bundle The resource bundle with the validation message.
	 * @param messageKey The validation message key.
	 * @param in The string input to validate.
	 * @return The validated file path.
	 * @throws InvalidInputException if the validation fails.
	 */
	public static Path isRegularFile(ResourceBundle bundle, String messageKey, String in) throws InvalidInputException {
		Path out;

		try {
			out = Paths.get(in).toAbsolutePath();
		} catch (InvalidPathException e) {
			throw new InvalidInputException(formatMessage(bundle, messageKey, in), e);
		}
		if (!Files.isRegularFile(out)) {
			throw new InvalidInputException(formatMessage(bundle, messageKey, in));
		}
		return out;
	}

	/**
	 * Ensure string input denotes an existing directory.
	 *
	 * @param bundle The resource bundle with the validation message.
	 * @param messageKey The validation message key.
	 * @param in The string input to validate.
	 * @return The validated directory path.
	 * @throws InvalidInputException if the validation fails.
	 */
	public static Path isDirectory(ResourceBundle bundle, String messageKey, String in) throws InvalidInputException {
		Path out;

		try {
			out = Paths.get(in).toAbsolutePath();
		} catch (InvalidPathException e) {
			throw new InvalidInputException(formatMessage(bundle, messageKey, in), e);
		}
		if (!Files.isDirectory(out)) {
			throw new InvalidInputException(formatMessage(bundle, messageKey, in));
		}
		return out;
	}

	/**
	 * Ensure string input denotes a path.
	 *
	 * @param bundle The resource bundle with the validation message.
	 * @param messageKey The validation message key.
	 * @param in The string input to validate.
	 * @return The validated path.
	 * @throws InvalidInputException if the validation fails.
	 */
	public static Path isPath(ResourceBundle bundle, String messageKey, String in) throws InvalidInputException {
		Path out;

		try {
			out = Paths.get(in);
		} catch (InvalidPathException e) {
			throw new InvalidInputException(formatMessage(bundle, messageKey, in), e);
		}
		return out;
	}

	/**
	 * Ensure string input denotes a path element.
	 *
	 * @param bundle The resource bundle with the validation message.
	 * @param messageKey The validation message key.
	 * @param basePath The path to resolve the input against.
	 * @param in The string input to validate.
	 * @return The validated path.
	 * @throws InvalidInputException if the validation fails.
	 */
	public static Path isPath(ResourceBundle bundle, String messageKey, Path basePath, String in)
			throws InvalidInputException {
		Path out;

		try {
			out = basePath.resolve(in);
		} catch (InvalidPathException e) {
			throw new InvalidInputException(formatMessage(bundle, messageKey, in), e);
		}
		return out;
	}

	/**
	 * Ensure string input denotes a URL.
	 *
	 * @param bundle The resource bundle with the validation message.
	 * @param messageKey The validation message key.
	 * @param in The string input to validate.
	 * @return The validated URL.
	 * @throws InvalidInputException if the validation fails.
	 */
	public static URL isURL(ResourceBundle bundle, String messageKey, String in) throws InvalidInputException {
		URL out;

		try {
			out = new URL(in);
		} catch (MalformedURLException e) {
			throw new InvalidInputException(formatMessage(bundle, messageKey, in), e);
		}
		return out;
	}

	/**
	 * Ensure string input denots a DN.
	 *
	 * @param bundle The resource bundle with the validation message.
	 * @param messageKey The validation message key.
	 * @param in The string input to validate.
	 * @return The validated DN.
	 * @throws InvalidInputException if the validation fails.
	 */
	public static X500Principal isDN(ResourceBundle bundle, String messageKey, String in) throws InvalidInputException {
		X500Principal out;

		try {
			out = X500Names.toPrincipal(in);
		} catch (IllegalArgumentException e) {
			throw new InvalidInputException(formatMessage(bundle, messageKey, in), e);
		}
		return out;
	}

	private static String formatMessage(ResourceBundle bundle, String messageKey, Object... arguments) {
		String pattern = bundle.getString(messageKey);

		return (arguments.length > 0 ? MessageFormat.format(pattern, arguments) : pattern);
	}

}
