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
package de.carne.certmgr.store;

import java.nio.file.Path;
import java.util.List;

/**
 *
 */
public final class CertStore implements CertSource<CertStoreEntry> {

	private class Entry implements CertStoreEntry {

		private final String id;

		private final String cn;

		Entry(String id, String cn) {
			this.id = id;
			this.cn = cn;
		}

		@Override
		public String id() {
			return this.id;
		}

		@Override
		public String cn() {
			return this.cn;
		}

		@Override
		public boolean hasKey() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean hasCRT() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean hasCSR() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean hasCRL() {
			// TODO Auto-generated method stub
			return false;
		}

	}

	private final Path storeHome;

	private CertStore(Path storeHome) {
		this.storeHome = storeHome;
	}

	public static CertStore open(Path storeHome) {
		return new CertStore(storeHome);
	}

	public Path getStoreHome() {
		return this.storeHome;
	}

	@Override
	public List<CertStoreEntry> getRootEntries() {
		// TODO Auto-generated method stub
		return null;
	}

}
