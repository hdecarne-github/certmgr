/*
 * Copyright (c) 2015-2021 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.certmgr;

import java.io.IOException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.carne.boot.ApplicationMain;
import de.carne.boot.Exceptions;
import de.carne.boot.logging.Log;
import de.carne.boot.logging.Logs;
import de.carne.certmgr.jfx.CertMgrApplication;

/**
 * {@link ApplicationMain} class.
 */
public class CertMgrMain implements ApplicationMain {

	private static final Log LOG = new Log();

	static {
		try {
			Logs.readConfig(Logs.CONFIG_DEFAULT);
		} catch (IOException e) {
			Exceptions.ignore(e);
		}
		LOG.info("Adding BouncyCastle security provider...");
		Security.addProvider(new BouncyCastleProvider());
	}

	@Override
	public String name() {
		return "CertMgr";
	}

	@Override
	public int run(String[] args) {
		CertMgrApplication.launch(args);
		return 0;
	}

}
