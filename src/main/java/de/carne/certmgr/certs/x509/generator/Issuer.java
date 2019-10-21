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
package de.carne.certmgr.certs.x509.generator;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.certmgr.certs.UserCertStoreEntry;
import de.carne.certmgr.certs.spi.CertGenerator;
import de.carne.certmgr.certs.x500.X500Names;

/**
 * This class represents an available issuer for certificate signing.
 */
public abstract class Issuer implements Comparable<Issuer> {

	@Nullable
	private final UserCertStoreEntry storeEntry;

	private final String name;

	/**
	 * Construct {@code Issuer}.
	 * <p>
	 *
	 * @param storeEntry The store entry represented by this issuer instance.
	 */
	protected Issuer(UserCertStoreEntry storeEntry) {
		this.storeEntry = storeEntry;
		this.name = CertGeneratorI18N.strStoreentryName(storeEntry.id().getAlias(),
				X500Names.toString(storeEntry.dn()));
	}

	/**
	 * Construct {@code Issuer}.
	 * <p>
	 *
	 * @param name The name of this issuer instance.
	 */
	protected Issuer(String name) {
		this.storeEntry = null;
		this.name = name;
	}

	/**
	 * Get the {@link CertGenerator} instance this issuer is associated with.
	 *
	 * @return This issuer's generator instance.
	 */
	public abstract CertGenerator generator();

	/**
	 * Get the {@link UserCertStoreEntry} represented by this issuer.
	 *
	 * @return The {@link UserCertStoreEntry} represented by this issuer, or {@code null} if this issuer does not
	 * represent an actual store entry.
	 */
	@Nullable
	public UserCertStoreEntry storeEntry() {
		return this.storeEntry;
	}

	@Override
	public int compareTo(@Nullable Issuer o) {
		Issuer checkedO = Objects.requireNonNull(o);

		if (!generator().equals(checkedO.generator())) {
			throw new IllegalArgumentException();
		}

		int comparison;

		if (this.storeEntry != null) {
			if (checkedO.storeEntry != null) {
				comparison = this.name.compareTo(checkedO.name);
			} else {
				comparison = 1;
			}
		} else {
			if (checkedO.storeEntry == null) {
				comparison = this.name.compareTo(checkedO.name);
			} else {
				comparison = -1;
			}
		}
		return comparison;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		boolean equal = false;

		if (this == obj) {
			equal = true;
		} else if (obj instanceof Issuer) {
			equal = compareTo((Issuer) obj) == 0;
		}
		return equal;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
