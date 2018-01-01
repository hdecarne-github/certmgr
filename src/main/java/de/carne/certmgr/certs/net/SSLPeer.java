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
package de.carne.certmgr.certs.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import de.carne.check.Nullable;
import de.carne.util.PropertiesHelper;
import de.carne.util.logging.Log;

/**
 * This class implements a dummy SSL/TLS client to retrieve certificate information from a SSL/TLS peer.
 */
public final class SSLPeer {

	/**
	 * Supported protocols.
	 */
	public enum Protocol {

		/**
		 * Plain SSL
		 */
		SSL(443),

		/**
		 * StartTLS SMTP
		 */
		STARTTLS_SMTP(25),

		/**
		 * StartTLS IMAP
		 */
		STARTTLS_IMAP(143);

		private int defaultPort;

		private Protocol(int defaultPort) {
			this.defaultPort = defaultPort;
		}

		/**
		 * Get the protocol's default port.
		 *
		 * @return The protocol's default port.
		 */
		public int defaultPort() {
			return this.defaultPort;
		}

	}

	private static final Log LOG = new Log();

	/**
	 * The socket timeout to use in milliseconds.
	 */
	public static final int SOCKET_TIMEOUT = PropertiesHelper.getInt(SSLPeer.class, ".socket-timeout", 5000);

	private static final TrustManager INSECURE_TRUST_MANAGER = new X509TrustManager() {

		@Override
		public void checkClientTrusted(@Nullable X509Certificate[] chain, @Nullable String authType)
				throws CertificateException {
			// Trust all
		}

		@Override
		public void checkServerTrusted(@Nullable X509Certificate[] chain, @Nullable String authType)
				throws CertificateException {
			// Trust all
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

	};

	private final InetAddress address;

	private final int port;

	private SSLPeer(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}

	/**
	 * Create {@code SSLPeer} instance for a specific host and port.
	 *
	 * @param host The host to access.
	 * @param port The port to access.
	 * @return The created {@code SSLPeer} instance.
	 * @throws UnknownHostException if the host cannot be resolved to a network address.
	 */
	public static SSLPeer getInstance(String host, int port) throws UnknownHostException {
		InetAddress address = InetAddress.getByName(host);

		return new SSLPeer(address, port);
	}

	/**
	 * Read peer certificates.
	 *
	 * @param protocol The protocol to use for peer access.
	 * @return The retrieved certificates, or {@code null} if none could be retrieved.
	 */
	@Nullable
	public Certificate[] readCertificates(Protocol protocol) {
		Certificate[] certificates = null;

		try (SSLProtocalHelper protocolHelper = SSLProtocalHelper.getInstance(protocol, this.address, this.port)) {
			protocolHelper.start();
			certificates = readCertificatesHelper(protocolHelper);
		} catch (IOException | GeneralSecurityException e) {
			LOG.info(e, "({0}) connection to {1} (port: {2}) failed", protocol, this.address, this.port);
		}
		return certificates;
	}

	private Certificate[] readCertificatesHelper(SSLProtocalHelper protocolHelper)
			throws GeneralSecurityException, IOException {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		// Accept as much certificates as possible
		sslContext.init(null, new TrustManager[] { INSECURE_TRUST_MANAGER }, null);

		SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

		// Prepare additional options: SNI server names
		List<SNIServerName> serverNames = Arrays.asList(new SNIHostName(this.address.getHostName()));

		// Start SSL handshake and retrieve certificates
		Certificate[] certificates = null;

		try (SSLSocket sslSocket = protocolHelper.createSSLSocket(sslSocketFactory, this.address, this.port)) {
			sslSocket.setSoTimeout(SOCKET_TIMEOUT);

			SSLParameters sslParams = sslSocket.getSSLParameters();

			sslParams.setServerNames(serverNames);
			sslSocket.setSSLParameters(sslParams);
			sslSocket.startHandshake();
			certificates = sslSocket.getSession().getPeerCertificates();
		}
		return certificates;
	}

}
