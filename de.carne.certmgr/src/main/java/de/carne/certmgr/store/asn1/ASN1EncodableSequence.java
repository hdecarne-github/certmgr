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
package de.carne.certmgr.store.asn1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Generic ASN.1 sequence.
 *
 * @param <T> The sequence element type.
 */
public class ASN1EncodableSequence<T extends ASN1Encodable> implements ASN1Encodable, Iterable<T> {

	private List<T> elements = new ArrayList<>();

	/**
	 * Add a element.
	 *
	 * @param element The element to add.
	 * @return The updated sequence.
	 */
	public ASN1EncodableSequence<T> addElement(T element) {
		this.elements.add(element);
		return this;
	}

	/**
	 * Get the sequence size.
	 *
	 * @return The sequence size.
	 */
	public int size() {
		return this.elements.size();
	}

	/**
	 * Get the sequence values.
	 * 
	 * @return The sequence values.
	 */
	public Collection<T> values() {
		return Collections.unmodifiableCollection(this.elements);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return this.elements.iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.store.asn1.ASN1Encodable#asn1Encode(de.carne.certmgr.store.asn1.ASN1Encoder)
	 */
	@Override
	public void asn1Encode(ASN1Encoder encoder) {
		encoder.asn1EncodeSequence(new ASN1Encodable() {

			@Override
			public void asn1Encode(ASN1Encoder encoder2) {
				asn1Encode2(encoder2);
			}

		});
	}

	void asn1Encode2(ASN1Encoder encoder) {
		for (ASN1Encodable element : this.elements) {
			element.asn1Encode(encoder);
		}
	}

}
