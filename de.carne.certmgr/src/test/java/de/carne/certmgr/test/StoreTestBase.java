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
package de.carne.certmgr.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.security.auth.x500.X500Principal;

import de.carne.certmgr.Main;
import de.carne.certmgr.store.CertStore;
import de.carne.certmgr.store.CertStoreEntry;
import de.carne.certmgr.store.PasswordCallback;
import de.carne.certmgr.store.x509.CertificateValidity;
import de.carne.certmgr.store.x509.ExtendedKeyUsage;
import de.carne.certmgr.store.x509.KeyParams;
import de.carne.certmgr.store.x509.KeyUsage;
import de.carne.certmgr.store.x509.X509BasicConstraintsExtension;
import de.carne.certmgr.store.x509.X509CertificateParams;
import de.carne.certmgr.store.x509.X509ExtendedKeyUsageExtension;
import de.carne.certmgr.store.x509.X509KeyUsageExtension;
import de.carne.util.logging.LogConfig;

/**
 * Base class providing functions common to all store test.
 */
abstract class StoreTestBase {

	public static final PasswordCallback TEST_PASSWORD = new PasswordCallback() {

		@Override
		public String requeryPassword(String resource, Exception details) {
			return null;
		}

		@Override
		public String queryPassword(String resource) {
			return "bo4FqIIiC0SIrx0A";
		}

	};

	/**
	 * Perform per test initialization.
	 */
	static {
		Main.init();
		LogConfig.applyConfig(LogConfig.CONFIG_DEBUG);
	}

	public static Path createTestRoot() throws IOException {
		Path testRoot = Files.createTempDirectory("Test");

		System.out.println(MessageFormat.format("Test root directory created: ''{0}''", testRoot));
		return testRoot;
	}

	public static Path deleteTestRoot(Path testRoot) throws IOException {
		if (testRoot != null) {
			Files.walkFileTree(testRoot, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					deletePath(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					deletePath(dir);
					return FileVisitResult.CONTINUE;
				}

			});
		}
		return null;
	}

	static void deletePath(Path path) throws IOException {
		System.out.println(MessageFormat.format("Deleting file/directory ''{0}''...", path));
		Files.delete(path);
	}

	public static String getStoreSignature(CertStore store) throws IOException {
		StringBuilder signature = new StringBuilder();

		formatStoreSignature(signature, store.getRootEntries(), "");
		return signature.toString();
	}

	private static StringBuilder formatStoreSignature(StringBuilder signature, Collection<CertStoreEntry> entries,
			String indent) throws IOException {
		ArrayList<CertStoreEntry> sortedEntries = new ArrayList<>(entries);

		sortedEntries.sort(new Comparator<CertStoreEntry>() {

			@Override
			public int compare(CertStoreEntry o1, CertStoreEntry o2) {
				return o1.getName().compareTo(o2.getName());
			}

		});
		for (CertStoreEntry entry : sortedEntries) {
			signature.append(indent).append("+").append(entry.getName());
			if (entry.hasKey()) {
				signature.append(" KEY");
			}
			if (entry.hasCRT()) {
				signature.append(" CRT");
			}
			if (entry.hasCSR()) {
				signature.append(" CSR");
			}
			if (entry.hasCRL()) {
				signature.append(" CRL");
			}
			signature.append("\n");

			Collection<CertStoreEntry> issuedEntries = entry.getStore().getIssuedEntries(entry);

			if (issuedEntries.size() > 0) {
				formatStoreSignature(signature, issuedEntries, indent + "\t");
			}
		}
		return signature;
	}

	public static String getCRTSignature(CertStoreEntry entry) throws IOException {
		X509Certificate crt = entry.getCRT().getObject();
		StringBuilder signature = new StringBuilder();

		signature.append(crt.getSubjectX500Principal().toString()).append(";");
		signature.append(crt.getIssuerX500Principal().toString()).append(";");
		signature.append(crt.getSigAlgName()).append(";");
		return signature.toString();
	}

	public static String getCRTParamsSignature(String alias) throws IOException {
		Properties params = getParams(alias);

		return params.getProperty("signature");
	}

	public static KeyParams getKeyParams(String alias) throws IOException {
		Properties params = getParams(alias);
		String keyAlg = params.getProperty("keyAlg");
		int keySize = Integer.parseInt(params.getProperty("keySize"));
		KeyParams keyParams = new KeyParams(keyAlg, keySize);

		return keyParams;
	}

	public static X509CertificateParams getCertificateParams(String alias) throws IOException {
		Properties params = getParams(alias);
		X500Principal subjectDN = new X500Principal(params.getProperty("subjectDN"));
		String sigAlg = params.getProperty("sigAlg");
		X509CertificateParams certificateParams = new X509CertificateParams(subjectDN, sigAlg);

		{
			StringTokenizer tokens = new StringTokenizer(params.getProperty("basicConstraints", ""), ",");

			if (tokens.hasMoreTokens()) {
				boolean critical = Boolean.valueOf(tokens.nextToken().trim());
				boolean ca = Boolean.valueOf(tokens.nextToken().trim());
				int pathLenConstraint = Integer.valueOf(tokens.nextToken().trim());

				certificateParams.addExtension(new X509BasicConstraintsExtension(critical, ca, pathLenConstraint));
			}
		}

		{
			StringTokenizer tokens = new StringTokenizer(params.getProperty("keyUsage", ""), ",");

			if (tokens.hasMoreTokens()) {
				boolean critical = Boolean.valueOf(tokens.nextToken().trim());
				ArrayList<KeyUsage> usageList = new ArrayList<>();

				while (tokens.hasMoreElements()) {
					usageList.add(KeyUsage.fromName(tokens.nextToken().trim()));
				}

				KeyUsage[] usages = usageList.toArray(new KeyUsage[usageList.size()]);

				certificateParams.addExtension(new X509KeyUsageExtension(critical, usages));
			}
		}

		{
			StringTokenizer tokens = new StringTokenizer(params.getProperty("extendedKeyUsage", ""), ",");

			if (tokens.hasMoreTokens()) {
				boolean critical = Boolean.valueOf(tokens.nextToken().trim());
				ArrayList<ExtendedKeyUsage> usageList = new ArrayList<>();

				while (tokens.hasMoreElements()) {
					usageList.add(ExtendedKeyUsage.fromName(tokens.nextToken().trim()));
				}

				ExtendedKeyUsage[] usages = usageList.toArray(new ExtendedKeyUsage[usageList.size()]);

				certificateParams.addExtension(new X509ExtendedKeyUsageExtension(critical, usages));
			}
		}

		return certificateParams;
	}

	public static CertificateValidity getCertificateValidity(String alias) throws IOException {
		Properties params = getParams(alias);
		LocalDate validFrom = LocalDate.parse(params.getProperty("validFrom"));
		LocalDate validTo = LocalDate.parse(params.getProperty("validTo"));
		CertificateValidity certificateValidity = new CertificateValidity(validFrom, validTo);

		return certificateValidity;
	}

	private static Properties getParams(String alias) throws IOException {
		Properties params = new Properties();

		try (InputStream paramsStream = StoreTestBase.class.getResourceAsStream(alias + ".properties")) {
			params.load(paramsStream);
		}
		return params;
	}

}
