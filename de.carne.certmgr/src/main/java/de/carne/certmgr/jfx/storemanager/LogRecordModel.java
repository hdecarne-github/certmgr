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
package de.carne.certmgr.jfx.storemanager;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.LogRecord;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import de.carne.certmgr.jfx.Images;
import de.carne.util.logging.Log;

/**
 * Model class to display log records in a table.
 */
public final class LogRecordModel {

	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.S");

	private final SimpleObjectProperty<Image> level;
	private final SimpleStringProperty time;
	private final SimpleStringProperty message;

	LogRecordModel(LogRecord record) {
		assert record != null;

		this.level = new SimpleObjectProperty<>(formatLevel(record.getLevel().intValue()));
		this.time = new SimpleStringProperty(formatTime(record.getMillis()));
		this.message = new SimpleStringProperty(formatMessage(record));
	}

	/**
	 * @return the level
	 */
	public Image getLevel() {
		return this.level.getValue();
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(Image level) {
		this.level.setValue(level);
	}

	/**
	 * @return The level property.
	 */
	public SimpleObjectProperty<Image> levelProperty() {
		return this.level;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return this.time.getValue();
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time.setValue(time);
	}

	/**
	 * @return The time property.
	 */
	public SimpleStringProperty timeProperty() {
		return this.time;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message.getValue();
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message.setValue(message);
	}

	/**
	 * @return The message property.
	 */
	public SimpleStringProperty messageProperty() {
		return this.message;
	}

	private Image formatLevel(int levelValue) {
		Image levelImage;

		if (Log.LEVEL_NOTICE.intValue() <= levelValue) {
			levelImage = Images.IMAGE_NOTICE16;
		} else if (Log.LEVEL_ERROR.intValue() <= levelValue) {
			levelImage = Images.IMAGE_ERROR16;
		} else if (Log.LEVEL_WARNING.intValue() <= levelValue) {
			levelImage = Images.IMAGE_WARNING16;
		} else {
			levelImage = Images.IMAGE_INFO16;
		}
		return levelImage;
	}

	private String formatTime(long timeValue) {
		return TIME_FORMAT.format(new Date(timeValue));
	}

	private String formatMessage(LogRecord record) {
		String format = record.getMessage();
		ResourceBundle bundle = record.getResourceBundle();

		if (bundle != null) {
			try {
				format = bundle.getString(format);
			} catch (MissingResourceException e) {
				// Ignore and use original message string
			}
		}
		return MessageFormat.format(format, record.getParameters());
	}

}
