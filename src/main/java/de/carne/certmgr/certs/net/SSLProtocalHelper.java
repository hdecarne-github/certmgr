/*
 * Copyright (c) 2015-2017 Holger de Carne and contributors, All Rights Reserved.
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

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import de.carne.check.Check;
import de.carne.check.Nullable;
import de.carne.util.Exceptions;
import de.carne.util.logging.Log;

abstract class SSLProtocalHelper implements AutoCloseable {

	private static final Log LOG = new Log();

	private static final int INPUT_BUFFER_SIZE = 8192;

	private static final int INPUT_CHUNK_SIZE = INPUT_BUFFER_SIZE / 8;

	@Nullable
	private final Socket plainSocket;

	@Nullable
	private OutputStream outputStream = null;

	@Nullable
	private InputStream inputStream = null;

	protected SSLProtocalHelper(@Nullable Socket plainSocket) {
		this.plainSocket = plainSocket;
	}

	static SSLProtocalHelper getInstance(SSLPeer.Protocol protocol, InetAddress address, int port) throws IOException {
		switch (protocol) {
		case SSL:
			return new PlainSSLHelper();
		case STARTTLS_SMTP:
			return new StartTLSSMTPHelper(new Socket(address, port));
		case STARTTLS_IMAP:
			return new StartTLSIMAPHelper(new Socket(address, port));
		default:
			throw new IllegalArgumentException("Invalid StartTLS protocol: " + protocol);
		}
	}

	public abstract void start() throws IOException;

	public SSLSocket createSSLSocket(SSLSocketFactory sslSocketFactory, InetAddress address, int port)
			throws IOException {
		Socket checkedPlainSocket = this.plainSocket;

		return (SSLSocket) (checkedPlainSocket != null
				? sslSocketFactory.createSocket(checkedPlainSocket, address.getHostName(), port, false)
				: sslSocketFactory.createSocket(address, port));
	}

	@Override
	public void close() {
		Socket checkedPlainSocket = this.plainSocket;

		if (checkedPlainSocket != null) {
			try {
				checkedPlainSocket.close();
			} catch (IOException e) {
				LOG.warning(e, "An error occurred while closing plain socket.");
			}
		}
	}

	private Socket getPlainSocket() throws IOException {
		Socket checkedPlainSocket = Check.nonNull(this.plainSocket);

		if (this.outputStream == null && this.inputStream == null) {
			checkedPlainSocket.setSoTimeout(SSLPeer.SOCKET_TIMEOUT);
		}
		return checkedPlainSocket;
	}

	private OutputStream getOutputStream() throws IOException {
		OutputStream checkedOutputStream = this.outputStream;

		if (checkedOutputStream == null) {
			checkedOutputStream = this.outputStream = getPlainSocket().getOutputStream();
		}
		return checkedOutputStream;
	}

	private InputStream getInputStream() throws IOException {
		InputStream checkedInputStream = this.inputStream;

		if (checkedInputStream == null) {
			checkedInputStream = this.inputStream = new BufferedInputStream(getPlainSocket().getInputStream(),
					INPUT_BUFFER_SIZE);
		}
		return checkedInputStream;
	}

	protected String getHostname() {
		String hostname;

		try {
			hostname = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			Exceptions.ignore(e);
			hostname = "localhost";
		}
		return hostname;
	}

	protected void send(byte[] data) throws IOException {
		getOutputStream().write(data);
	}

	protected void send(byte[] data, int off, int len) throws IOException {
		getOutputStream().write(data, off, len);
	}

	protected void send(String data, Charset charset) throws IOException {
		send(data.getBytes(charset));
	}

	protected void send(String data) throws IOException {
		send(data, StandardCharsets.US_ASCII);
	}

	protected int receive(byte[] buffer) throws IOException {
		return getInputStream().read(buffer);
	}

	protected int receive(byte[] buffer, int off, int len) throws IOException {
		return getInputStream().read(buffer, off, len);
	}

	protected ByteBuffer receiveAll(byte[]... stopMarkers) throws IOException {
		InputStream checkedInputStream = getInputStream();

		byte[] matchingStopMarker = null;
		byte[] buffer = new byte[INPUT_CHUNK_SIZE];
		int pos = 0;

		while (matchingStopMarker == null) {
			int received = checkedInputStream.read();

			if (received == -1) {
				throw new EOFException();
			}
			if (pos == buffer.length) {
				byte[] resizedBuffer = new byte[buffer.length + INPUT_CHUNK_SIZE];

				System.arraycopy(buffer, 0, resizedBuffer, 0, pos);
				buffer = resizedBuffer;
			}
			buffer[pos++] = (byte) received;
			for (byte[] stopMarker : stopMarkers) {
				if (bytesEqual(buffer, pos - stopMarker.length, stopMarker, 0, stopMarker.length)) {
					matchingStopMarker = stopMarker;
					break;
				}
			}
		}
		return ByteBuffer.wrap(buffer, 0, pos);
	}

	private static boolean bytesEqual(byte[] b1, int off1, byte[] b2, int off2, int len) {
		boolean equal = false;

		if (0 <= off1 && off1 + len <= b1.length && 0 <= off2 && off2 + len <= b2.length) {
			int matchIndex = 0;

			while (matchIndex < len && b1[off1 + matchIndex] == b2[off2 + matchIndex]) {
				matchIndex++;
			}
			equal = matchIndex == len;
		}
		return equal;
	}

	protected String receiveAll(String stopMarker, Charset charset) throws IOException {
		ByteBuffer buffer = receiveAll(stopMarker.getBytes(charset));

		return charset.decode(buffer).toString();
	}

	protected String receiveAll(String stopMarker) throws IOException {
		return receiveAll(stopMarker, StandardCharsets.US_ASCII);
	}

}
