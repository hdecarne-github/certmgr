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
package de.carne.certmgr.jfx.help;

/**
 * Available help topics.
 */
public enum Help {

	/**
	 * StoreManager Help.
	 */
	TOPIC_STORE_MANAGER(I18N.STR_TOPIC_STORE_MANAGER),

	/**
	 * CRTExport help.
	 */
	TOPIC_CERT_EXPORT(I18N.STR_TOPIC_CERT_EXPORT),

	/**
	 * CRTImport help.
	 */
	TOPIC_CERT_IMPORT(I18N.STR_TOPIC_CERT_IMPORT),

	/**
	 * CRLOptions help.
	 */
	TOPIC_CRL_OPTIONS(I18N.STR_TOPIC_CRL_OPTIONS),

	/**
	 * CRTOptions help.
	 */
	TOPIC_CRT_OPTIONS(I18N.STR_TOPIC_CRT_OPTIONS),

	/**
	 * DNEditor help.
	 */
	TOPIC_DN_EDITOR(I18N.STR_TOPIC_DN_EDITOR),

	/**
	 * EntryOptions help.
	 */
	TOPIC_ENTRY_OPTIONS(I18N.STR_TOPIC_ENTRY_OPTIONS),

	/**
	 * PasswordPrompt help.
	 */
	TOPIC_PASSWORD_PROMPT(I18N.STR_TOPIC_PASSWORD_PROMPT),

	/**
	 * NewPasswordPromp help.
	 */
	TOPIC_NEW_PASSWORD_PROMPT(I18N.STR_TOPIC_NEW_PASSWORD_PROMPT),

	/**
	 * StoreOptions help.
	 */
	TOPIC_STORE_OPTIONS(I18N.STR_TOPIC_STORE_OPTIONS);

	private String key;

	Help(String key) {
		this.key = key;
	}

	/**
	 * Get the topic resource key.
	 * 
	 * @return The topic resource key.
	 */
	public String key() {
		return this.key;
	}

}
