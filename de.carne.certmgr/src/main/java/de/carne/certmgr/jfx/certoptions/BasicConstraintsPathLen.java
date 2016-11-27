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
package de.carne.certmgr.jfx.certoptions;

import java.math.BigInteger;
import java.util.Objects;

import de.carne.certmgr.util.DefaultSet;
import de.carne.util.Strings;
import javafx.util.StringConverter;

class BasicConstraintsPathLen implements Comparable<BasicConstraintsPathLen> {

	private static final BasicConstraintsPathLen NO_CONSTRAINT = new BasicConstraintsPathLen(null);

	public static final DefaultSet<BasicConstraintsPathLen> DEFAULT_SET = new DefaultSet<>();

	static {
		DEFAULT_SET.add(BasicConstraintsPathLen.valueOf(null));
		DEFAULT_SET.add(BasicConstraintsPathLen.valueOf(0));
		DEFAULT_SET.add(BasicConstraintsPathLen.valueOf(1));
		DEFAULT_SET.add(BasicConstraintsPathLen.valueOf(2));
		DEFAULT_SET.add(BasicConstraintsPathLen.valueOf(3));
		DEFAULT_SET.add(BasicConstraintsPathLen.valueOf(4));
		DEFAULT_SET.add(BasicConstraintsPathLen.valueOf(5));
	}

	public static final StringConverter<BasicConstraintsPathLen> CONVERTER = new StringConverter<BasicConstraintsPathLen>() {

		@Override
		public String toString(BasicConstraintsPathLen object) {
			return Objects.toString(object, "");
		}

		@Override
		public BasicConstraintsPathLen fromString(String string) {
			String trimmedString = Strings.safeTrim(string);

			return BasicConstraintsPathLen.valueOf(trimmedString != null ? new BigInteger(trimmedString) : null);
		}

	};

	private final BigInteger pathLenConstraint;

	private BasicConstraintsPathLen(BigInteger pathLenConstraint) {
		this.pathLenConstraint = pathLenConstraint;
	}

	public static BasicConstraintsPathLen valueOf(long pathLenConstraint) {
		return new BasicConstraintsPathLen(BigInteger.valueOf(pathLenConstraint));
	}

	public static BasicConstraintsPathLen valueOf(BigInteger pathLenConstraint) {
		return (pathLenConstraint != null ? new BasicConstraintsPathLen(pathLenConstraint) : NO_CONSTRAINT);
	}

	public BigInteger value() {
		return this.pathLenConstraint;
	}

	@Override
	public int compareTo(BasicConstraintsPathLen o) {
		int comparision;

		if (this.pathLenConstraint == null) {
			comparision = (o.pathLenConstraint == null ? 0 : 1);
		} else {
			comparision = (o.pathLenConstraint != null ? this.pathLenConstraint.compareTo(o.pathLenConstraint) : -1);
		}
		return comparision;
	}

	@Override
	public String toString() {
		return (this.pathLenConstraint != null ? this.pathLenConstraint.toString() : "\u221e");
	}

}
