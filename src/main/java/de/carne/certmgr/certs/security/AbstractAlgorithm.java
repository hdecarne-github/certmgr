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
package de.carne.certmgr.certs.security;

import java.security.Provider.Service;

/**
 * Abstract base class for {@link Service} based objects.
 */
public abstract class AbstractAlgorithm {

	private final Service service;

	/**
	 * Construct {@code AbstractAlgorithm}.
	 *
	 * @param service The service value to use for initialization.
	 */
	protected AbstractAlgorithm(Service service) {
		this.service = service;
	}

	/**
	 * Get this algorithm's {@link Service} object.
	 *
	 * @return This algorithm's {@link Service} object.
	 */
	protected Service service() {
		return this.service;
	}

	/**
	 * Get this algorithm's name.
	 *
	 * @return This algorithm's name.
	 */
	public String algorithm() {
		return this.service.getAlgorithm();
	}

	@Override
	public String toString() {
		return this.service.getAlgorithm() + "/" + this.service.getProvider().getName();
	}

}
