/*
 * I18N resource strings
 *
 * Generated on 07.05.2016 08:43:41
 */
package de.carne.certmgr.jfx.storemanager;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Package localization resources.
 */
public final class I18N {

	/**
	 * The BUNDLE represented by this class.
	 */
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(I18N.class.getName());

	private static String format(String key, Object... arguments) {
		String pattern = BUNDLE.getString(key);

		return (arguments.length > 0 ? MessageFormat.format(pattern, arguments) : pattern);
	}

	/**
	 * Resource key {@code STR_EXT_OBJECT}
	 * <p>
	 * Extension {0}
	 * </p>
	 */
	public static final String STR_EXT_OBJECT = "STR_EXT_OBJECT";

	/**
	 * Resource string {@code STR_EXT_OBJECT}
	 * <p>
	 * Extension {0}
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_EXT_OBJECT(Object... arguments) {
		return format(STR_EXT_OBJECT, arguments);
	}

	/**
	 * Resource key {@code STR_STORE_MANAGER_TITLE}
	 * <p>
	 * Certificate Management
	 * </p>
	 */
	public static final String STR_STORE_MANAGER_TITLE = "STR_STORE_MANAGER_TITLE";

	/**
	 * Resource string {@code STR_STORE_MANAGER_TITLE}
	 * <p>
	 * Certificate Management
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_STORE_MANAGER_TITLE(Object... arguments) {
		return format(STR_STORE_MANAGER_TITLE, arguments);
	}

	/**
	 * Resource key {@code STR_CRT_OBJECT}
	 * <p>
	 * X.509 Certificate
	 * </p>
	 */
	public static final String STR_CRT_OBJECT = "STR_CRT_OBJECT";

	/**
	 * Resource string {@code STR_CRT_OBJECT}
	 * <p>
	 * X.509 Certificate
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CRT_OBJECT(Object... arguments) {
		return format(STR_CRT_OBJECT, arguments);
	}

	/**
	 * Resource key {@code STR_CONFIRM_DELETE_MESSAGE}
	 * <p>
	 * Do you really want to delete the entry<br/>''{0}''<br/>and all it's sub-entries?
	 * </p>
	 */
	public static final String STR_CONFIRM_DELETE_MESSAGE = "STR_CONFIRM_DELETE_MESSAGE";

	/**
	 * Resource string {@code STR_CONFIRM_DELETE_MESSAGE}
	 * <p>
	 * Do you really want to delete the entry<br/>''{0}''<br/>and all it's sub-entries?
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CONFIRM_DELETE_MESSAGE(Object... arguments) {
		return format(STR_CONFIRM_DELETE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_CERT_STORE}
	 * <p>
	 * Certificate Store
	 * </p>
	 */
	public static final String STR_CERT_STORE = "STR_CERT_STORE";

	/**
	 * Resource string {@code STR_CERT_STORE}
	 * <p>
	 * Certificate Store
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CERT_STORE(Object... arguments) {
		return format(STR_CERT_STORE, arguments);
	}

	/**
	 * Resource key {@code STR_ENTRY_ERROR_MESSAGE}
	 * <p>
	 * An error occurred while accessing certificate entry ''{0}'' (Cause: ''{1}'')
	 * </p>
	 */
	public static final String STR_ENTRY_ERROR_MESSAGE = "STR_ENTRY_ERROR_MESSAGE";

	/**
	 * Resource string {@code STR_ENTRY_ERROR_MESSAGE}
	 * <p>
	 * An error occurred while accessing certificate entry ''{0}'' (Cause: ''{1}'')
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ENTRY_ERROR_MESSAGE(Object... arguments) {
		return format(STR_ENTRY_ERROR_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_CSR_OBJECT}
	 * <p>
	 * PKCS#10 Certificate Signing Request
	 * </p>
	 */
	public static final String STR_CSR_OBJECT = "STR_CSR_OBJECT";

	/**
	 * Resource string {@code STR_CSR_OBJECT}
	 * <p>
	 * PKCS#10 Certificate Signing Request
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CSR_OBJECT(Object... arguments) {
		return format(STR_CSR_OBJECT, arguments);
	}

	/**
	 * Resource key {@code STR_CRL_OBJECT}
	 * <p>
	 * X.509 Certificate Revocation List
	 * </p>
	 */
	public static final String STR_CRL_OBJECT = "STR_CRL_OBJECT";

	/**
	 * Resource string {@code STR_CRL_OBJECT}
	 * <p>
	 * X.509 Certificate Revocation List
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CRL_OBJECT(Object... arguments) {
		return format(STR_CRL_OBJECT, arguments);
	}

	/**
	 * Resource key {@code STR_ABOUT_TITLE13}
	 * <p>
	 * Additional Copyrights
	 * </p>
	 */
	public static final String STR_ABOUT_TITLE13 = "STR_ABOUT_TITLE13";

	/**
	 * Resource string {@code STR_ABOUT_TITLE13}
	 * <p>
	 * Additional Copyrights
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ABOUT_TITLE13(Object... arguments) {
		return format(STR_ABOUT_TITLE13, arguments);
	}

	/**
	 * Resource key {@code STR_ABOUT_TITLE12}
	 * <p>
	 * Copyright BouncyCastle Library
	 * </p>
	 */
	public static final String STR_ABOUT_TITLE12 = "STR_ABOUT_TITLE12";

	/**
	 * Resource string {@code STR_ABOUT_TITLE12}
	 * <p>
	 * Copyright BouncyCastle Library
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ABOUT_TITLE12(Object... arguments) {
		return format(STR_ABOUT_TITLE12, arguments);
	}

	/**
	 * Resource key {@code STR_ABOUT_TITLE11}
	 * <p>
	 * Copyright CertMgr
	 * </p>
	 */
	public static final String STR_ABOUT_TITLE11 = "STR_ABOUT_TITLE11";

	/**
	 * Resource string {@code STR_ABOUT_TITLE11}
	 * <p>
	 * Copyright CertMgr
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ABOUT_TITLE11(Object... arguments) {
		return format(STR_ABOUT_TITLE11, arguments);
	}

	/**
	 * Resource key {@code STR_CERT_ENTRY}
	 * <p>
	 * Certificate Entry
	 * </p>
	 */
	public static final String STR_CERT_ENTRY = "STR_CERT_ENTRY";

	/**
	 * Resource string {@code STR_CERT_ENTRY}
	 * <p>
	 * Certificate Entry
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CERT_ENTRY(Object... arguments) {
		return format(STR_CERT_ENTRY, arguments);
	}

	/**
	 * Resource key {@code STR_ABOUT_INFO3}
	 * <p>
	 * This program makes use of the Farm-Fresh icon set (http:&frasl;&frasl;www.fatcow.com&frasl;free-icons) .<br/>© Copyright 2009-2014 FatCow Web Hosting. All rights reserved.<br/>http:&frasl;&frasl;www.fatcow.com<br/><br/>These icons are licensed under a Creative Commons Attribution 3.0 License.<br/>http:&frasl;&frasl;creativecommons.org&frasl;licenses&frasl;by&frasl;3.0&frasl;us&frasl;.
	 * </p>
	 */
	public static final String STR_ABOUT_INFO3 = "STR_ABOUT_INFO3";

	/**
	 * Resource string {@code STR_ABOUT_INFO3}
	 * <p>
	 * This program makes use of the Farm-Fresh icon set (http:&frasl;&frasl;www.fatcow.com&frasl;free-icons) .<br/>© Copyright 2009-2014 FatCow Web Hosting. All rights reserved.<br/>http:&frasl;&frasl;www.fatcow.com<br/><br/>These icons are licensed under a Creative Commons Attribution 3.0 License.<br/>http:&frasl;&frasl;creativecommons.org&frasl;licenses&frasl;by&frasl;3.0&frasl;us&frasl;.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ABOUT_INFO3(Object... arguments) {
		return format(STR_ABOUT_INFO3, arguments);
	}

	/**
	 * Resource key {@code STR_ABOUT_INFO2}
	 * <p>
	 * Copyright (c) 2000-2014 The Legion of the Bouncy Castle Inc. (http:&frasl;&frasl;www.bouncycastle.org)<br/><br/>Permission is hereby granted, free of charge, to any person obtaining a copy of this software<br/>and associated documentation files (the "Software"), to deal in the Software without restriction,<br/>including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,<br/>and&frasl;or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,<br/>subject to the following conditions:<br/><br/>The above copyright notice and this permission notice shall be included in all copies or substantial<br/>portions of the Software.<br/><br/>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,<br/>INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR<br/>PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE<br/>LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR<br/>OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER<br/>DEALINGS IN THE SOFTWARE.
	 * </p>
	 */
	public static final String STR_ABOUT_INFO2 = "STR_ABOUT_INFO2";

	/**
	 * Resource string {@code STR_ABOUT_INFO2}
	 * <p>
	 * Copyright (c) 2000-2014 The Legion of the Bouncy Castle Inc. (http:&frasl;&frasl;www.bouncycastle.org)<br/><br/>Permission is hereby granted, free of charge, to any person obtaining a copy of this software<br/>and associated documentation files (the "Software"), to deal in the Software without restriction,<br/>including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,<br/>and&frasl;or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,<br/>subject to the following conditions:<br/><br/>The above copyright notice and this permission notice shall be included in all copies or substantial<br/>portions of the Software.<br/><br/>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,<br/>INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR<br/>PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE<br/>LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR<br/>OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER<br/>DEALINGS IN THE SOFTWARE.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ABOUT_INFO2(Object... arguments) {
		return format(STR_ABOUT_INFO2, arguments);
	}

	/**
	 * Resource key {@code STR_ABOUT_INFO1}
	 * <p>
	 * Copyright © 2014-2016 Holger de Carne and contributors,<br/>All Rights Reserved.<br/><br/>This program is free software: you can redistribute it and&frasl;or modify<br/>it under the terms of the GNU General Public License as published by<br/>the Free Software Foundation, either version 3 of the License, or<br/>(at your option) any later version.<br/><br/>This program is distributed in the hope that it will be useful,<br/>but WITHOUT ANY WARRANTY; without even the implied warranty of<br/>MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the<br/>GNU General Public License for more details.<br/><br/>You should have received a copy of the GNU General Public License<br/>along with this program.  If not, see http:&frasl;&frasl;www.gnu.org&frasl;licenses.
	 * </p>
	 */
	public static final String STR_ABOUT_INFO1 = "STR_ABOUT_INFO1";

	/**
	 * Resource string {@code STR_ABOUT_INFO1}
	 * <p>
	 * Copyright © 2014-2016 Holger de Carne and contributors,<br/>All Rights Reserved.<br/><br/>This program is free software: you can redistribute it and&frasl;or modify<br/>it under the terms of the GNU General Public License as published by<br/>the Free Software Foundation, either version 3 of the License, or<br/>(at your option) any later version.<br/><br/>This program is distributed in the hope that it will be useful,<br/>but WITHOUT ANY WARRANTY; without even the implied warranty of<br/>MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the<br/>GNU General Public License for more details.<br/><br/>You should have received a copy of the GNU General Public License<br/>along with this program.  If not, see http:&frasl;&frasl;www.gnu.org&frasl;licenses.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ABOUT_INFO1(Object... arguments) {
		return format(STR_ABOUT_INFO1, arguments);
	}

}
