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
package de.carne.certmgr.store.x500;

import java.util.ArrayList;

/**
 * Utility class for DN decoding.
 */
final class DNDecoder {

	private String name;
	private boolean strict;
	private int pos;
	private int len;

	DNDecoder(String name, boolean strict) {
		this.name = name;
		this.strict = strict;
		this.pos = 0;
		this.len = this.name.length();
	}

	public RDN[] decodeDN() {
		ArrayList<RDN> rdns = new ArrayList<>();
		RDN rdn = decodeRDN();

		while (rdn != null) {
			rdns.add(rdn);
			rdn = decodeRDN();
		}
		return rdns.toArray(new RDN[rdns.size()]);
	}

	private RDN decodeRDN() {
		RDN rdn = null;

		skipSpaces();
		if (this.pos < this.len) {
			String type = decodeToken();

			skipSpaces();

			String assign = decodeToken("=");

			skipSeparator();

			String value = decodeToken();

			if (type != null && assign != null && value != null) {
				rdn = new RDN(type, value);
				skipSeparator();
			}
		}
		return rdn;
	}

	private String decodeToken() {
		StringBuilder token = null;

		if (this.pos < this.len) {
			char c = this.name.charAt(this.pos);
			boolean escape = false;

			if (c != '"') {
				token = new StringBuilder();
				if (c != '\\') {
					token.append(c);
				} else {
					escape = true;
				}
				this.pos++;
				if (!isSpecial(c)) {
					while (this.pos < this.len && (!isSpecial(c = this.name.charAt(this.pos)) || escape)) {
						if (escape) {
							token.append(c);
							escape = false;
						} else if (c != '\\') {
							token.append(c);
						} else {
							escape = true;
						}
						this.pos++;
					}
				}
			} else {
				token = new StringBuilder();
				this.pos++;
				while (this.pos < this.len && (escape || (c = this.name.charAt(this.pos)) != '"')) {
					if (escape) {
						token.append(c);
						escape = false;
					} else if (c != '\\') {
						token.append(c);
					} else {
						escape = true;
					}
					this.pos++;
				}
				if (c == '"') {
					this.pos++;
				} else {
					if (this.strict) {
						throw new IllegalArgumentException("Non-terminated quoted token in name '" + this.name + "'");
					}
					token = null;
				}
			}
			if (escape) {
				if (this.strict) {
					throw new IllegalArgumentException("Non-terminated escape sequence in name '" + this.name + "'");
				}
				token = null;
			}
		}
		return (token != null ? token.toString() : null);
	}

	private String decodeToken(String expected) {
		String token = decodeToken();

		if (!expected.equals(token)) {
			if (this.strict) {
				throw new IllegalArgumentException("Unexpected token '" + token + "' in name '" + this.name + "'");
			}
			token = null;
		}
		return token;
	}

	private void skipSpaces() {
		while (this.pos < this.len && isSpace(this.name.charAt(this.pos))) {
			this.pos++;
		}
	}

	private boolean isSpace(char c) {
		return c == ' ' || c == '\r';
	}

	private void skipSeparator() {
		skipSpaces();
		if (this.pos < this.len && isSeparator(this.name.charAt(this.pos))) {
			this.pos++;
		}
	}

	private boolean isSeparator(char c) {
		return c == ',' || c == ';';
	}

	private boolean isSpecial(char c) {
		return c == '=' || isSeparator(c) || isSpace(c);
	}

}
