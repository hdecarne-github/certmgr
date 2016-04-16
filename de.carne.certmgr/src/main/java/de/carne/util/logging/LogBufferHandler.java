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
package de.carne.util.logging;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * {@link Handler} implementation capable of buffering log messages and forwarding them to a list of registered
 * {@link Handler} instances.
 */
public class LogBufferHandler extends Handler {

	/**
	 * Maximum number of buffered entries.
	 */
	public static final int BUFFER_SIZE = 1000;

	private ArrayDeque<LogRecord> records = new ArrayDeque<>(BUFFER_SIZE);
	private HashSet<Handler> handlers = new HashSet<>();

	/**
	 * Retrieve the BufferedHandler attached to a logger.
	 *
	 * @param logger The logger to retrieve the BufferedHandler for.
	 * @return The found BufferedHandler or null if none is configured.
	 */
	public static LogBufferHandler getHandler(Logger logger) {
		assert logger != null;

		LogBufferHandler foundHandler = null;
		Logger currentLogger = logger;

		while (currentLogger != null && foundHandler == null) {
			for (Handler handler : currentLogger.getHandlers()) {
				if (handler instanceof LogBufferHandler) {
					foundHandler = (LogBufferHandler) handler;
					break;
				}
			}
			currentLogger = currentLogger.getParent();
		}
		return foundHandler;
	}

	/**
	 * Add a handler to forward any published log records to.
	 * <p>
	 * Already buffered log records are automatically forwarded to the submitted handler.
	 * </p>
	 *
	 * @param handler The handler to add.
	 */
	public synchronized void addHandler(Handler handler) {
		assert handler != null;

		for (LogRecord record : this.records) {
			handler.publish(record);
		}
		this.handlers.add(handler);
	}

	/**
	 * Remove a previously added handler.
	 *
	 * @param handler The handler to remove.
	 * @see #addHandler(Handler)
	 */
	public synchronized void removeHandler(Handler handler) {
		assert handler != null;

		this.handlers.remove(handler);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	@Override
	public void publish(LogRecord record) {
		publishRecord(record);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.logging.Handler#flush()
	 */
	@Override
	public void flush() {
		flushHandlers();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.logging.Handler#close()
	 */
	@Override
	public void close() throws SecurityException {
		closeHandlers();
	}

	private synchronized void publishRecord(LogRecord record) {
		while (this.records.size() >= BUFFER_SIZE) {
			this.records.removeFirst();
		}
		this.records.addLast(record);
		for (Handler handler : this.handlers) {
			handler.publish(record);
		}
	}

	private synchronized void flushHandlers() {
		for (Handler handler : this.handlers) {
			handler.flush();
		}
		this.records.clear();
	}

	private synchronized void closeHandlers() {
		for (Handler handler : this.handlers) {
			handler.close();
		}
		this.records.clear();
	}

}
