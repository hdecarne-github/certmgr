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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import de.carne.certmgr.store.ExportTarget;

/**
 * Clipboard based export target implementation.
 */
public class ClipboardExportTarget implements ExportTarget.StringData, ExportTarget.FileList {

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.ExportTarget#getName()
	 */
	@Override
	public String getName() {
		return I18N.format(I18N.TEXT_CLIPBOARDEXPORTTARGET);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.ExportTarget.FileList#putFileList(java.util.List)
	 */
	@Override
	public void putFileList(List<Path> fileList) throws IOException {
		assert fileList != null;

		if (Platform.isFxApplicationThread()) {

			ArrayList<File> files = new ArrayList<>(fileList.size());

			for (Path file : fileList) {
				files.add(file.toFile());
			}

			ClipboardContent content = new ClipboardContent();

			content.putFiles(files);
			Clipboard.getSystemClipboard().setContent(content);
		} else {
			FxRunner<IOException> fxRunner = new FxRunner<IOException>() {
				private List<Path> fileList2 = fileList;

				@Override
				public void run() {
					try {
						putFileList(this.fileList2);
						setComplete(null);
					} catch (IOException e) {
						setComplete(e);
					}
				}

			};

			Platform.runLater(fxRunner);
			IOException runnerException = null;

			try {
				runnerException = fxRunner.waitComplete();
			} catch (InterruptedException e) {
				// nothing to do here
			}
			if (runnerException != null) {
				throw runnerException;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.ExportTarget.StringData#putStringData(java.lang.String)
	 */
	@Override
	public void putStringData(String stringData) throws IOException {
		assert stringData != null;

		if (Platform.isFxApplicationThread()) {
			ClipboardContent content = new ClipboardContent();

			// Replace Windows linebreak with Unix linebreak to avoid double line breaks upon pasting
			// looks like JavaFX bug
			String contentStringData = stringData.replace("\r\n", "\n");

			content.putString(contentStringData);
			Clipboard.getSystemClipboard().setContent(content);
		} else {
			FxRunner<IOException> fxRunner = new FxRunner<IOException>() {
				private String stringData2 = stringData;

				@Override
				public void run() {
					try {
						putStringData(this.stringData2);
						setComplete(null);
					} catch (IOException e) {
						setComplete(e);
					}
				}

			};

			Platform.runLater(fxRunner);
			IOException runnerException = null;

			try {
				runnerException = fxRunner.waitComplete();
			} catch (InterruptedException e) {
				// nothing to do here
			}
			if (runnerException != null) {
				throw runnerException;
			}
		}
	}

	private abstract class FxRunner<T> implements Runnable {

		private boolean queryCompleteFlag = false;
		private T result = null;

		FxRunner() {
			// Nothing to do here
		}
		
		public synchronized T waitComplete() throws InterruptedException {
			while (!this.queryCompleteFlag) {
				wait();
			}
			return this.result;
		}

		public synchronized void setComplete(T result) {
			this.queryCompleteFlag = true;
			this.result = result;
			notifyAll();
		}

	}

}
