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
package de.carne.certmgr.store;

import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;

import de.carne.certmgr.store.provider.StoreProvider;
import de.carne.certmgr.util.logging.Log;

/**
 * Helper class for exporting a certificate store entry to an export target.
 */
public class CertStoreEntryExporter {

	private static final Log LOG = new Log(CertStoreEntryExporter.class);

	private static final String EXT_KEY = ".key";
	private static final String EXT_CRT = ".crt";
	private static final String EXT_CSR = ".csr";
	private static final String EXT_CRL = ".crl";

	static final StoreProvider PROVIDER = StoreProvider.getInstance();

	String[] exportAliases;
	KeyPair exportKey;
	X509Certificate[] exportCRTs;
	PKCS10Object exportCSR;
	X509CRL exportCRL;

	/**
	 * Create an exporter for a specific store entry.
	 *
	 * @param entry The store entry to create an exporter for.
	 * @param includeKey Whether to include the key in the export or not.
	 * @param includeCRT Whether to include the CRT in the export or not.
	 * @param includeCRTChain Whether to include the certificate chain in the export or not.
	 * @param includeCRTAnchor Whether to include the certificate anchor in the export or not.
	 * @param includeCSR Whether to include the CSR in the export or not.
	 * @param includeCRL Whether to include the CRL in the export or not.
	 * @param password The password callback to use for key access.
	 * @return The created exporter.
	 * @throws IOException if an I/O error occurs while accessing the store entry.
	 * @throws PasswordRequiredException if a password is required but was not given.
	 */
	public static CertStoreEntryExporter forEntry(CertStoreEntry entry, boolean includeKey, boolean includeCRT,
			boolean includeCRTChain, boolean includeCRTAnchor, boolean includeCSR, boolean includeCRL,
			PasswordCallback password) throws IOException, PasswordRequiredException {
		assert entry != null;

		ArrayList<String> exportAliasList = new ArrayList<>();
		String entryAlias = entry.getAlias();

		exportAliasList.add(entryAlias);

		KeyPair exportKey = null;

		if (includeKey) {
			if (entry.hasKey()) {
				exportKey = entry.getKey(password).getObject();
			} else {
				LOG.warning(I18N.bundle(), I18N.MESSAGE_NOKEYTOEXPORT, entryAlias);
			}
		}

		X509Certificate[] exportCRTs = null;

		if (includeCRT) {
			if (entry.hasCRT()) {
				ArrayList<X509Certificate> exportCRTList = new ArrayList<>();

				exportCRTList.add(entry.getCRT().getObject());
				if (!entry.isRoot() && includeCRTChain) {
					CertStore store = entry.getStore();
					CertStoreEntry chainEntry = store.getEntry(entry.getIssuer());

					while (!chainEntry.isRoot()) {
						exportAliasList.add(chainEntry.getAlias());
						exportCRTList.add(chainEntry.getCRT().getObject());
						chainEntry = store.getEntry(chainEntry.getIssuer());
					}
					if (includeCRTAnchor) {
						exportAliasList.add(chainEntry.getAlias());
						exportCRTList.add(chainEntry.getCRT().getObject());
					}
				}
				exportCRTs = exportCRTList.toArray(new X509Certificate[exportCRTList.size()]);
			} else {
				LOG.warning(I18N.bundle(), I18N.MESSAGE_NOCRTTOEXPORT, entryAlias);
			}
		}

		String[] exportAliases = exportAliasList.toArray(new String[exportAliasList.size()]);
		PKCS10Object exportCSR = null;

		if (includeCSR) {
			if (entry.hasCSR()) {
				exportCSR = entry.getCSR().getObject();
			} else {
				LOG.warning(I18N.bundle(), I18N.MESSAGE_NOCSRTOEXPORT, entryAlias);
			}
		}

		X509CRL exportCRL = null;

		if (includeCRL) {
			if (entry.hasCRL()) {
				exportCRL = entry.getCRL().getObject();
			} else {
				LOG.warning(I18N.bundle(), I18N.MESSAGE_NOCRLTOEXPORT, entryAlias);
			}
		}
		return new CertStoreEntryExporter(exportAliases, exportKey, exportCRTs, exportCSR, exportCRL);
	}

	private CertStoreEntryExporter(String[] exportAliases, KeyPair exportKey, X509Certificate[] exportCRTs,
			PKCS10Object exportCSR, X509CRL exportCRL) {
		this.exportAliases = exportAliases;
		this.exportKey = exportKey;
		this.exportCRTs = exportCRTs;
		this.exportCSR = exportCSR;
		this.exportCRL = exportCRL;
	}

	/**
	 * Perform the export.
	 *
	 * @param format The format to use for the export.
	 * @param target The export target to export to.
	 * @param password The password callback to use for retrieving new passwords.
	 * @throws IOException if an I/O error occurs during export.
	 * @throws PasswordRequiredException if a password is required but was not given.
	 * @throws UnsupportedExportTargetException if the export target is not supported by the choosen format.
	 */
	public void export(CertFileFormat format, ExportTarget target, PasswordCallback password) throws IOException,
			PasswordRequiredException, UnsupportedExportTargetException {
		switch (format) {
		case PEM:
			exportPEM(target, password);
			break;
		case PKCS12:
			exportPKCS12(target, password);
			break;
		default:
			throw new IllegalArgumentException("Unexpected format: " + format);
		}
	}

	private void exportPEM(ExportTarget target, PasswordCallback password) throws IOException {
		if (target instanceof ExportTarget.StringData) {
			ExportTarget.StringData stringDataExportTarget = (ExportTarget.StringData) target;
			String stringData = PROVIDER.encodePEM(this.exportCRTs, this.exportKey, this.exportCSR, this.exportCRL,
					password, target.getName());

			stringDataExportTarget.putStringData(stringData);
		} else if (target instanceof ExportTarget.StringDataMap) {
			ExportTarget.StringDataMap stringDataMapExportTarget = (ExportTarget.StringDataMap) target;
			HashMap<String, String> stringDataMap = new HashMap<>();

			if (this.exportKey != null) {
				String keyFileName = this.exportAliases[0] + EXT_KEY;
				String keyData = PROVIDER.encodePEM(null, this.exportKey, null, null, password, keyFileName);

				stringDataMap.put(keyFileName, keyData);
			}
			if (this.exportCRTs != null) {
				int exportAliasIndex = 0;

				for (X509Certificate exportCRT : this.exportCRTs) {
					String crtFileName = this.exportAliases[exportAliasIndex] + EXT_CRT;
					String crtData = PROVIDER.encodePEM(new X509Certificate[] { exportCRT }, null, null, null,
							password, crtFileName);

					stringDataMap.put(crtFileName, crtData);
					exportAliasIndex++;
				}
			}
			if (this.exportCSR != null) {
				String csrFileName = this.exportAliases[0] + EXT_CSR;
				String csrData = PROVIDER.encodePEM(null, null, this.exportCSR, null, password, csrFileName);

				stringDataMap.put(csrFileName, csrData);
			}
			if (this.exportCRL != null) {
				String crlFileName = this.exportAliases[0] + EXT_CRL;
				String crlData = PROVIDER.encodePEM(null, null, null, this.exportCRL, password, crlFileName);

				stringDataMap.put(crlFileName, crlData);
			}
			stringDataMapExportTarget.putStringData(stringDataMap);
		} else {
			throw new UnsupportedExportTargetException(target);
		}
	}

	private void exportPKCS12(ExportTarget target, PasswordCallback password) throws IOException {
		if (target instanceof ExportTarget.ByteData) {
			ExportTarget.ByteData byteDataExportTarget = (ExportTarget.ByteData) target;
			byte[] byteData = PROVIDER.encodePKCS12(this.exportCRTs, this.exportKey, this.exportCSR, this.exportCRL,
					password, target.getName());

			byteDataExportTarget.putByteData(byteData);
		} else {
			throw new UnsupportedExportTargetException(target);
		}
	}

}
