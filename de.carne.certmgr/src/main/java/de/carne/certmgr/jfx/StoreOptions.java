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

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import de.carne.certmgr.store.CertStore;

/**
 * Class collecting all application relevant certificate store options.
 */
public final class StoreOptions {

	private static final SortedSet<TimePeriodOption> CRTVALIDITY_PERIODS = new TreeSet<>();
	private static final TimePeriodOption DEFAULT_CRTVALIDITY_PERIOD = new TimePeriodOption(
			1 * TimePeriodOption.DAYS_PER_YEAR);

	private static final SortedSet<TimePeriodOption> CRLUPDATE_PERIODS = new TreeSet<>();
	private static final TimePeriodOption DEFAULT_CRLUPDATE_PERIOD = new TimePeriodOption(
			1 * TimePeriodOption.DAYS_PER_MONTH);

	static {
		for (int month = 1; month < 12; month++) {
			CRTVALIDITY_PERIODS.add(new TimePeriodOption(month * TimePeriodOption.DAYS_PER_MONTH));
			CRLUPDATE_PERIODS.add(new TimePeriodOption(month * TimePeriodOption.DAYS_PER_MONTH));
		}
		for (int year = 1; year < 11; year++) {
			CRTVALIDITY_PERIODS.add(new TimePeriodOption(year * TimePeriodOption.DAYS_PER_YEAR));
			CRLUPDATE_PERIODS.add(new TimePeriodOption(year * TimePeriodOption.DAYS_PER_YEAR));
		}
	}

	/**
	 * Get predefined CRT validity periods.
	 *
	 * @return Predefined CRT validity periods.
	 */
	public static SortedSet<TimePeriodOption> getCRTValidityPeriods() {
		return CRTVALIDITY_PERIODS;
	}

	/**
	 * Get the default CRT validity period.
	 *
	 * @return The default CRT validity period.
	 */
	public static TimePeriodOption getDefaultCRTValidity() {
		return DEFAULT_CRTVALIDITY_PERIOD;
	}

	/**
	 * Get predefined CRL update periods.
	 *
	 * @return Predefined CRL update periods.
	 */
	public static SortedSet<TimePeriodOption> getCRLUpdatePeriods() {
		return CRLUPDATE_PERIODS;
	}

	/**
	 * Get the default CRL update period.
	 *
	 * @return The default CRL update period.
	 */
	public static TimePeriodOption getDefaultCRLUpdate() {
		return DEFAULT_CRLUPDATE_PERIOD;
	}

	private static final String NODE_STORE = "store";

	private static final String KEY_DEFCRTVALIDITY = "defcrtvalidity";
	private static final String KEY_DEFCRLUPDATE = "defcrlupdate";
	private static final String KEY_DEFKEYALG = "defkeyalg";
	private static final String KEY_DEFKEYSIZE = "defkeysize";
	private static final String KEY_DEFSIGALG = "defsigalg";

	private TimePeriodOption defCRTValidity;
	private TimePeriodOption defCRLUpdate;
	private String defKeyAlg;
	private Integer defKeySize;
	private String defSigAlg;

	/**
	 * Construct StoreOptions.
	 */
	public StoreOptions() {
		this.defCRTValidity = getDefaultCRTValidity();
		this.defCRLUpdate = getDefaultCRLUpdate();
		this.defKeyAlg = CertStore.getDefaultKeyAlg();
		this.defKeySize = CertStore.getDefaultKeySize(this.defKeyAlg);
		this.defSigAlg = CertStore.getDefaultSigAlg(this.defKeyAlg);
	}

	/**
	 * Load options from an existing store.
	 *
	 * @param store The certificate store to load the options from.
	 */
	public void load(CertStore store) {
		Preferences storePreferences = store.getPreferences().node(NODE_STORE);

		int defCRTValidityDays = storePreferences.getInt(KEY_DEFCRTVALIDITY, -1);

		if (defCRTValidityDays >= 0) {
			this.defCRTValidity = new TimePeriodOption(defCRTValidityDays);
		}

		int defCRLUpdateDays = storePreferences.getInt(KEY_DEFCRLUPDATE, -1);

		if (defCRLUpdateDays >= 0) {
			this.defCRLUpdate = new TimePeriodOption(defCRLUpdateDays);
		}
		this.defKeyAlg = storePreferences.get(KEY_DEFKEYALG, this.defKeyAlg);
		this.defKeySize = storePreferences.getInt(KEY_DEFKEYSIZE, this.defKeySize);
		this.defSigAlg = storePreferences.get(KEY_DEFSIGALG, this.defSigAlg);
	}

	/**
	 * Write certificate store options.
	 *
	 * @param store The certificate store to write the options to.
	 * @throws IOException if an I/O error occurs while writing the options.
	 */
	public void write(CertStore store) throws IOException {
		Preferences storePreferences = store.getPreferences().node(NODE_STORE);

		storePreferences.putInt(KEY_DEFCRTVALIDITY, this.defCRTValidity.toDays());
		storePreferences.putInt(KEY_DEFCRLUPDATE, this.defCRLUpdate.toDays());
		storePreferences.put(KEY_DEFKEYALG, this.defKeyAlg);
		storePreferences.putInt(KEY_DEFKEYSIZE, this.defKeySize);
		storePreferences.put(KEY_DEFSIGALG, this.defSigAlg);
		try {
			storePreferences.sync();
		} catch (BackingStoreException e) {
			throw new IOException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * @return the defCRTValidity
	 */
	public TimePeriodOption getDefCRTValidity() {
		return this.defCRTValidity;
	}

	/**
	 * @param defCRTValidity the defCRTValidity to set
	 */
	public void setDefCRTValidity(TimePeriodOption defCRTValidity) {
		this.defCRTValidity = defCRTValidity;
	}

	/**
	 * @return the defCRLUpdate
	 */
	public TimePeriodOption getDefCRLUpdate() {
		return this.defCRLUpdate;
	}

	/**
	 * @param defCRLUpdate the defCRLUpdate to set
	 */
	public void setDefCRLUpdate(TimePeriodOption defCRLUpdate) {
		this.defCRLUpdate = defCRLUpdate;
	}

	/**
	 * @return the defKeyAlg
	 */
	public String getDefKeyAlg() {
		return this.defKeyAlg;
	}

	/**
	 * @param defKeyAlg the defKeyAlg to set
	 */
	public void setDefKeyAlg(String defKeyAlg) {
		this.defKeyAlg = defKeyAlg;
	}

	/**
	 * @return the defKeySize
	 */
	public Integer getDefKeySize() {
		return this.defKeySize;
	}

	/**
	 * @param defKeySize the defKeySize to set
	 */
	public void setDefKeySize(Integer defKeySize) {
		this.defKeySize = defKeySize;
	}

	/**
	 * @return the defSigAlg
	 */
	public String getDefSigAlg() {
		return this.defSigAlg;
	}

	/**
	 * @param defSigAlg the defSigAlg to set
	 */
	public void setDefSigAlg(String defSigAlg) {
		this.defSigAlg = defSigAlg;
	}

}
