/*
 * Copyright 2014 Anders Lund
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lunders.client.android.bmk.model.nyheter;

import android.text.Html;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class Nyhet implements Serializable, Comparable<Nyhet> {

    private String overskrift;

    private CharSequence ingress;

	private Date dato;

    private Nyhetskilde kilde;

	private String fullStoryURL;

	private CharSequence fullStory;

	public Nyhet() {

	}

	public Nyhet(String overskrift, CharSequence ingress, Nyhetskilde kilde) {
        this.overskrift = overskrift;
        this.ingress = ingress;
        this.kilde = kilde;
    }

	public void setOverskrift(String overskrift) {
		this.overskrift = overskrift;
	}

	public void setIngress(String ingress) {
		this.ingress = ingress;
	}

	public void setKilde(Nyhetskilde kilde) {
		this.kilde = kilde;
	}

	public String getOverskrift() {
        return overskrift;
    }

    public CharSequence getIngress() {
        return ingress;
    }

    public Nyhetskilde getKilde() {
        return kilde;
    }

	public String getFullStoryURL() {
		return fullStoryURL;
	}

	public void setFullStoryURL(String fullStoryURL) {
		this.fullStoryURL = fullStoryURL;
	}

	public Date getDato() {
		return dato;
	}

	public void setDato(Date dato) {
		this.dato = dato;
	}

	@Override
	public String toString() {
		return String.format("%s: %s\n%s", kilde, overskrift, ingress);
	}

	@Override
	public int compareTo(Nyhet another) {
		if ( dato == null || another.dato == null) {
			//Gjør at nyheter hvor vi ikke fant dato, sorterer foran de med dato
			return 1;
		}

		//Sorterer i synkende rekkefølge (nyeste først)
		return another.dato.compareTo(this.dato);
	}

	@Override
	public boolean equals(Object o) {
		if ( o != null && o instanceof Nyhet) {
			Nyhet other = (Nyhet)o;
			return kilde == other.kilde && overskrift.equals(other.overskrift);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = overskrift.hashCode();
		result = 31 * result + kilde.hashCode();
		return result;
	}

	public void setFullStory(CharSequence fullStory) {
		this.fullStory = fullStory;
	}

	public CharSequence getFullStory() {
		return fullStory;
	}

	private void writeObject(ObjectOutputStream stream)
		throws IOException {
		stream.writeObject(overskrift);
		stream.writeObject(dato);
		stream.writeObject(kilde);
		stream.writeObject(fullStoryURL);
		stream.writeObject(ingress != null ? ingress.toString() : null);
		stream.writeObject(fullStory != null ? fullStory.toString() : null);
	}

	private void readObject(ObjectInputStream stream)
		throws IOException, ClassNotFoundException {
		overskrift = (String) stream.readObject();
		dato = (Date) stream.readObject();
		kilde = (Nyhetskilde) stream.readObject();
		fullStoryURL = (String) stream.readObject();
		ingress = Html.fromHtml((String)stream.readObject());
		fullStory = Html.fromHtml((String) stream.readObject());
	}
}
