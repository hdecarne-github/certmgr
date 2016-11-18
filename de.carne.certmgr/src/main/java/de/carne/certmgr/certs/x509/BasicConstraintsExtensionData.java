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
package de.carne.certmgr.certs.x509;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;

/**
 * X.509 <a href="https://tools.ietf.org/html/rfc5280#section-4.2.1.9">Basic
 * Constraints Extension</a> data.
 */
public class BasicConstraintsExtensionData extends X509ExtensionData {

	/**
	 * Extension OID.
	 */
	public static final String OID = "2.5.29.19";

	private boolean ca = false;

	private Integer pathLenConstraint = null;

	/**
	 * Construct {@code BasicConstraintsExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 */
	public BasicConstraintsExtensionData(boolean critical) {
		this(critical, false, -1);
	}

	/**
	 * Construct {@code BasicConstraintsExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 * @param ca The extension's CA flag.
	 * @param pathLenConstraint The extension's path length constraint value or
	 *        {@code null} if there is none.
	 */
	public BasicConstraintsExtensionData(boolean critical, boolean ca, Integer pathLenConstraint) {
		super(OID, critical);
		this.ca = ca;
		this.pathLenConstraint = pathLenConstraint;
	}

	/**
	 * Decode {@code BasicConstraintsExtensionData} from an ASN.1 data object.
	 *
	 * @param primitive The ASN.1 data object to decode.
	 * @param critical The extension's critical flag.
	 * @return The decoded extension data.
	 * @throws IOException if an I/O error occurs during decoding.
	 */
	public static BasicConstraintsExtensionData decode(ASN1Primitive primitive, boolean critical) throws IOException {
		ASN1Primitive[] sequence = decodeSequence(primitive, 0, 2);
		boolean decodedCA = false;
		Integer decodedPathLenConstraint = null;

		if (sequence.length > 0) {
			decodedCA = decodePrimitive(sequence[0], ASN1Boolean.class).isTrue();
		}
		if (sequence.length > 1) {
			decodedPathLenConstraint = decodePrimitive(sequence[1], ASN1Integer.class).getValue().intValue();
		}
		return new BasicConstraintsExtensionData(critical, decodedCA, decodedPathLenConstraint);
	}

	/**
	 * Get this extension's CA flag.
	 *
	 * @return This extension's CA flag.
	 */
	public boolean getCA() {
		return this.ca;
	}

	/**
	 * Set this extension's CA flag.
	 *
	 * @param ca The value to set.
	 */
	public void setCA(boolean ca) {
		this.ca = ca;
	}

	/**
	 * Get this extension's path length constraint.
	 *
	 * @return This extension's path length constraint, or {@code null} if none
	 *         has been defined.
	 */
	public Integer getPathLenConstraint() {
		return this.pathLenConstraint;
	}

	/**
	 * Set this extension's path length constraint.
	 *
	 * @param pathLenConstraint The value to set.
	 */
	public void setPathLenConstraint(Integer pathLenConstraint) {
		this.pathLenConstraint = pathLenConstraint;
	}

	@Override
	public Attributes toAttributes() {
		Attributes extensionAttributes = super.toAttributes();

		extensionAttributes.addChild(AttributesI18N.formatSTR_BASICCONSTRAINTS_CA(), Boolean.toString(this.ca));

		if (this.pathLenConstraint != null) {
			int pathLenConstraintValue = this.pathLenConstraint.intValue();

			extensionAttributes.addChild(AttributesI18N.formatSTR_BASICCONSTRAINTS_PATHLENCONSTRAINT(),
					(pathLenConstraintValue >= 0 ? Integer.toString(this.pathLenConstraint.intValue()) : "\u221E"));
		}
		return extensionAttributes;
	}

}
