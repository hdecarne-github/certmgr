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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import de.carne.certmgr.store.CertStore;
import de.carne.certmgr.store.CertStoreEntry;

/**
 * Class used to represent a certificate store entry in the GUI.
 */
public class CertStoreEntryOption {

	private CertStoreEntry entry;
	private String name;

	private CertStoreEntryOption(String name) {
		this.entry = null;
		this.name = name;
	}

	private CertStoreEntryOption(CertStoreEntry entry, String namePrefix) {
		this.entry = entry;
		this.name = namePrefix + this.entry.getName() + " (" + this.entry.getAlias() + ")";
	}

	/**
	 * Create default entry representing the null entry.
	 *
	 * @param name The name for the default entry.
	 * @return The created default entry.
	 */
	public static CertStoreEntryOption defaultOption(String name) {
		assert name != null;

		return new CertStoreEntryOption(name);
	}

	/**
	 * Create option from single certificate store entry.
	 * 
	 * @param entry The entry to create the option from.
	 * @return The created option.
	 */
	public static CertStoreEntryOption fromStoreEntry(CertStoreEntry entry) {
		return new CertStoreEntryOption(entry, "");
	}

	/**
	 * Create options list from a certificate store's entries.
	 *
	 * @param store The store to create the options list from.
	 * @return The created options list.
	 */
	public static List<CertStoreEntryOption> fromStore(CertStore store) {
		return fromStoreWithPredicate(store, null);
	}

	/**
	 * Create options list from a certificate store's entries.
	 *
	 * @param store The store to create the options list from.
	 * @param predicate An optional predicate to filter the entries.
	 * @return The created options list.
	 */
	public static List<CertStoreEntryOption> fromStoreWithPredicate(CertStore store, Predicate<CertStoreEntry> predicate) {
		assert store != null;

		ArrayList<CertStoreEntryOption> options = new ArrayList<>();

		fromStoreHelper(options, store, store.getRootEntries(), predicate, "");
		return options;
	}

	private static void fromStoreHelper(ArrayList<CertStoreEntryOption> options, CertStore store,
			Collection<CertStoreEntry> entries, Predicate<CertStoreEntry> predicate, String prefix) {
		String nextPrefix = prefix + " ";

		for (CertStoreEntry entry : entries) {
			boolean testResult = predicate == null || predicate.test(entry);

			if (testResult) {
				options.add(new CertStoreEntryOption(entry, prefix));
				fromStoreHelper(options, store, store.getIssuedEntries(entry), predicate, nextPrefix);
			} else {
				fromStoreHelper(options, store, store.getIssuedEntries(entry), predicate, prefix);
			}
		}
	}

	/**
	 * Find the option representing a specific entry in an option list.
	 *
	 * @param options The option list to search.
	 * @param entry The entry to search for.
	 * @return The found option.
	 */
	public static CertStoreEntryOption findOption(Collection<CertStoreEntryOption> options, CertStoreEntry entry) {
		assert options != null;

		CertStoreEntryOption foundOption = null;

		for (CertStoreEntryOption option : options) {
			CertStoreEntry optionEntry = option.getEntry();

			if ((entry != null && entry.equals(optionEntry)) || entry == optionEntry) {
				foundOption = option;
				break;
			}
		}
		return foundOption;
	}

	/**
	 * Get the certificate store entry represented by this option.
	 *
	 * @return The certificate store entry represented by this option.
	 */
	public CertStoreEntry getEntry() {
		return this.entry;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}

}
