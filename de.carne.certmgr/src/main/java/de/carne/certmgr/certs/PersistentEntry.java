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
package de.carne.certmgr.certs;

final class PersistentEntry {

	private final CRTEntry crtEntry;
	private final KeyEntry keyEntry;
	private final CSREntry csrEntry;
	private final CRLEntry crlEntry;

	PersistentEntry(PersistentEntry base, CRTEntry crtEntry, KeyEntry keyEntry, CSREntry csrEntry, CRLEntry crlEntry) {
		this.crtEntry = (crtEntry != null ? crtEntry : (base != null ? base.crtEntry : null));
		this.keyEntry = (keyEntry != null ? keyEntry : (base != null ? base.keyEntry : null));
		this.csrEntry = (csrEntry != null ? csrEntry : (base != null ? base.csrEntry : null));
		this.crlEntry = (crlEntry != null ? crlEntry : (base != null ? base.crlEntry : null));
	}

	public CRTEntry crtEntry() {
		return this.crtEntry;
	}

	public KeyEntry keyEntry() {
		return this.keyEntry;
	}

	public CSREntry csrEntry() {
		return this.csrEntry;
	}

	public CRLEntry crlEntry() {
		return this.crlEntry;
	}

}
