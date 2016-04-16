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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * {@link Formatter} implementation formatting {@link LogRecord}s to a simple single line of text.
 */
public class LogFormatter extends Formatter {

	private final DateFormat dateFormat = DateFormat.getDateTimeInstance();

	/*
	 * (non-Javadoc)
	 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
	 */
	@Override
	public String format(LogRecord record) {
		String message = null;

		try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
			StringBuffer sb = sw.getBuffer();

			sb.append(this.dateFormat.format(new Date(record.getMillis())));
			sb.append(" [");
			sb.append(record.getThreadID());
			sb.append("] ");
			sb.append(Log.formatLevel(record.getLevel()));
			sb.append(" ");
			sb.append(record.getLoggerName());
			sb.append(" ");
			sb.append(formatMessage(record));
			pw.println();

			Throwable thrown = record.getThrown();

			if (thrown != null) {
				thrown.printStackTrace(pw);
			}
			pw.flush();
			message = sw.toString();
		} catch (Exception e) {
			System.err.println("An exception occured while formatting a log message");
			e.printStackTrace();
		}
		return message;
	}

}
