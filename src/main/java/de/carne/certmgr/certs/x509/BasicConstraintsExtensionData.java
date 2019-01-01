/*
 * Copyright (c) 2015-2019 Holger de Carne and contributors, All Rights Reserved.
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
import java.math.BigInteger;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import org.eclipse.jdt.annotation.Nullable;

/**
 * X.509 <a href="https://tools.ietf.org/html/rfc5280#section-4.2.1.9">Basic Constraints Extension</a> data.
 */
public class BasicConstraintsExtensionData extends X509ExtensionData {

	/**
	 * Extension OID.
	 */
	public static final String OID = "2.5.29.19";

	/**
	 * The default to use for this extension's critical flag.
	 */
	public static final boolean CRITICAL_DEFAULT = true;

	private boolean ca = false;

	@Nullable
	private BigInteger pathLenConstraint = null;

	/**
	 * Construct {@code BasicConstraintsExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 */
	public BasicConstraintsExtensionData(boolean critical) {
		this(critical, false, null);
	}

	/**
	 * Construct {@code BasicConstraintsExtensionData}.
	 *
	 * @param critical The extension's critical flag.
	 * @param ca The extension's CA flag.
	 * @param pathLenConstraint The extension's path length constraint value or {@code null} if there is none.
	 */
	public BasicConstraintsExtensionData(boolean critical, boolean ca, @Nullable BigInteger pathLenConstraint) {
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
		BigInteger decodedPathLenConstraint = null;

		if (sequence.length > 0) {
			decodedCA = decodePrimitive(sequence[0], ASN1Boolean.class).isTrue();
		}
		if (sequence.length > 1) {
			decodedPathLenConstraint = decodePrimitive(sequence[1], ASN1Integer.class).getValue();
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
	 * @return This extension's path length constraint, or {@code null} if none has been defined.
	 */
	@Nullable
	public BigInteger getPathLenConstraint() {
		return this.pathLenConstraint;
	}

	/**
	 * Set this extension's path length constraint.
	 *
	 * @param pathLenConstraint The value to set.
	 */
	public void setPathLenConstraint(BigInteger pathLenConstraint) {
		this.pathLenConstraint = pathLenConstraint;
	}

	@Override
	public ASN1Encodable encode() throws IOException {
		ASN1EncodableVector sequence = new ASN1EncodableVector();

		sequence.add(ASN1Boolean.getInstance(this.ca));
		if (this.pathLenConstraint != null) {
			sequence.add(new ASN1Integer(this.pathLenConstraint));
		}
		return new DERSequence(sequence);
	}

	@Override
	public String toValueString() {
		return AttributesI18N.formatSTR_BC_VALUE(this.ca);
	}

	@Override
	public Attributes toAttributes() {
		Attributes extensionAttributes = super.toAttributes();

		extensionAttributes.add(AttributesI18N.formatSTR_BC_CA(), Boolean.toString(this.ca));

		BigInteger checkedPathLenConstraint = this.pathLenConstraint;

		if (checkedPathLenConstraint != null) {
			int pathLenConstraintValue = checkedPathLenConstraint.intValue();

			extensionAttributes.add(AttributesI18N.formatSTR_BC_PATHLENCONSTRAINT(),
					(pathLenConstraintValue >= 0 ? Integer.toString(pathLenConstraintValue) : "\u221E"));
		}
		return extensionAttributes;
	}

}
