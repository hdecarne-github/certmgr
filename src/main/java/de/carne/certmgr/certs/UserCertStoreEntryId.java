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
package de.carne.certmgr.certs;

import java.util.Objects;

import de.carne.boot.check.Nullable;
import de.carne.util.Strings;

/**
 * Id class for identifying certificate store entries in a unique way and supporting transient and persistent entries.
 * <p>
 * The actual id consists of two elements. A required id value and an optional alias string. The semantic of the alias
 * string is handler specific.
 *
 * @see UserCertStoreHandler
 */
public final class UserCertStoreEntryId {

	private final int id;

	private final String alias;

	UserCertStoreEntryId(int id, @Nullable String alias) {
		this.id = id;
		this.alias = Strings.safe(alias);
	}

	/**
	 * Check whether this id has an alias.
	 *
	 * @return {@code true} if this id has an alias.
	 */
	public boolean hasAlias() {
		return Strings.notEmpty(this.alias);
	}

	/**
	 * Get this id's alias.
	 *
	 * @return This id's alias or the empty string if the id has no alias.
	 */
	public String getAlias() {
		return this.alias;
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		boolean equal = false;

		if (this == obj) {
			equal = true;
		} else if (obj instanceof UserCertStoreEntryId) {
			UserCertStoreEntryId o = (UserCertStoreEntryId) obj;

			equal = this.id == o.id && Objects.equals(this.alias, o.alias);
		}
		return equal;
	}

	@Override
	public String toString() {
		return (Strings.notEmpty(this.alias) ? this.alias : UserCertStoreI18N.formatSTR_TEXT_EXTERNALENTRY());
	}

}
