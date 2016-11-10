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
package de.carne.certmgr.certs.security;

import java.util.Collection;
import java.util.HashSet;

/**
 * Helper class combining a result set with a corresponding default value.
 *
 * @param <T> The set element type.
 */
public final class DefaultSet<T> extends HashSet<T> {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 2969592903102269986L;

	private T defaultEntry = null;

	/**
	 * Construct {@code DefaultSet}.
	 */
	public DefaultSet() {
		super();
	}

	/**
	 * Construct {@code DefaultSet}.
	 *
	 * @param elements The initial set content.
	 */
	public DefaultSet(Collection<? extends T> elements) {
		super(elements);
	}

	@Override
	public boolean add(T entry) {
		if (this.defaultEntry == null) {
			this.defaultEntry = entry;
		}
		return super.add(entry);
	}

	@Override
	public void clear() {
		this.defaultEntry = null;
		super.clear();
	}

	/**
	 * Add an entry to the set and make it the default.
	 *
	 * @param entry The default entry.
	 */
	public void addDefault(T entry) {
		add(entry);
		this.defaultEntry = entry;
	}

	/**
	 * Get the default entry.
	 *
	 * @return The default entry.
	 */
	public T getDefault() {
		return this.defaultEntry;
	}

}
