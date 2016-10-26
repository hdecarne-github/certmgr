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
package de.carne.certmgr.certs.net;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import de.carne.certmgr.certs.CertProviderException;
import de.carne.util.PropertiesHelper;
import de.carne.util.logging.Log;

/**
 * This class implements a dummy SSL/TLS client to retrieve certificate
 * information from a SSL/TLS peer.
 */
public final class SSLPeer {

	/**
	 * Supported StartTLS protocols.
	 */
	public enum StartTLS {

		/**
		 * SMTP
		 */
		SMTP

	}

	private static final Log LOG = new Log();

	/**
	 * The socket timeout to use in milliseconds.
	 */
	public static final int SOCKET_TIMEOUT = PropertiesHelper.getInt(SSLPeer.class, ".socket-timeout", 1000);

	private static final TrustManager INSECURE_TRUST_MANAGER = new X509TrustManager() {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			// Trust all
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
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
	 * @throws UnknownHostException if the host cannot be resolved to a network
	 *         address.
	 */
	public static SSLPeer getInstance(String host, int port) throws UnknownHostException {
		assert host != null;

		InetAddress address = InetAddress.getByName(host);

		return new SSLPeer(address, port);
	}

	/**
	 * Trying to retrieve peer certificates via all kind of supported connection
	 * types.
	 *
	 * @return The retrieved certificates or {@code null} if none could be
	 *         retrieved.
	 */
	public Certificate[] readCertificates() {
		Certificate[] certificates = null;
		boolean generalSocketError = false;

		try {
			certificates = readCertificatesSSL();
		} catch (IOException e) {
			LOG.info(e, "SSL connection to {0} (port: {1}) failed", this.address, this.port);
			generalSocketError = e instanceof ConnectException || e instanceof SocketTimeoutException;
		}
		if (certificates == null && !generalSocketError) {
			for (StartTLS protocol : StartTLS.values()) {
				try {
					certificates = readCertificatesStartTLS(protocol);
					break;
				} catch (IOException e) {
					LOG.info(e, "({0}) StartTLS connection to {1} (port: {2}) failed", protocol, this.address,
							this.port);
				}
			}
		}
		return certificates;
	}

	/**
	 * Establish a SSL encrypted connection and retrieve the peer's
	 * certificates.
	 *
	 * @return The retrieved certificates.
	 * @throws IOException if an I/O error occurs while accessing the peer.
	 */
	public Certificate[] readCertificatesSSL() throws IOException {
		LOG.info("Establishing SSL connection to {0} (port: {1})...", this.address, this.port);

		Certificate[] certificates;

		try {
			certificates = readCertificatesHelper(null);
		} catch (GeneralSecurityException e) {
			throw new CertProviderException(e);
		}
		return certificates;
	}

	/**
	 * Establish a StartTLS encrypted connection and retrieve the peer's
	 * certificates.
	 *
	 * @param protocol The {@link StartTLS} protocol to use.
	 * @return The retrieved certificates.
	 * @throws IOException if an I/O error occurs while accessing the peer.
	 */
	public Certificate[] readCertificatesStartTLS(StartTLS protocol) throws IOException {
		LOG.info("Establishing {0} StartTLS connection to {1} (port: {2})...", protocol, this.address, this.port);

		Certificate[] certificates;

		try (StartTLSHelper startTLS = StartTLSHelper.getInstance(this.address, this.port, protocol)) {
			startTLS.start();
			try {
				certificates = readCertificatesHelper(startTLS);
			} catch (GeneralSecurityException e) {
				throw new CertProviderException(e);
			}
		}
		return certificates;
	}

	private Certificate[] readCertificatesHelper(StartTLSHelper startTLS) throws GeneralSecurityException, IOException {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		// Accept as much certificates as possible
		sslContext.init(null, new TrustManager[] { INSECURE_TRUST_MANAGER }, null);

		SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

		Certificate[] certificates = null;

		try (SSLSocket sslSocket = createSSLSocket(sslSocketFactory, startTLS)) {
			String[] supportedProtocols = sslSocket.getSupportedProtocols();

			LOG.debug("Enabling all supported protocols: {0}", Arrays.toString(supportedProtocols));

			sslSocket.setEnabledProtocols(supportedProtocols);
			sslSocket.setSoTimeout(SOCKET_TIMEOUT);
			sslSocket.startHandshake();
			certificates = sslSocket.getSession().getPeerCertificates();
		}
		return certificates;
	}

	private SSLSocket createSSLSocket(SSLSocketFactory sslSocketFactory, StartTLSHelper startTLS) throws IOException {
		return (SSLSocket) (startTLS != null
				? sslSocketFactory.createSocket(startTLS.plainSocket(), this.address.getHostName(), this.port, false)
				: sslSocketFactory.createSocket(this.address, this.port));
	}

}
