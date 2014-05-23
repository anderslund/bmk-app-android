package org.lunders.client.android.bmk.model.nyheter;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class Nyhet implements Serializable, Comparable<Nyhet> {

    private String overskrift;

    private String ingress;

	private Date dato;

    private Nyhetskilde kilde;

	private String fullStoryURL;
	private String fullStory;

	public Nyhet(String overskrift, String ingress, Nyhetskilde kilde) {
        this.overskrift = overskrift;
        this.ingress = ingress;
        this.kilde = kilde;
    }

    public String getOverskrift() {
        return overskrift;
    }

    public String getIngress() {
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

	public void setFullStory(String fullStory) {
		this.fullStory = fullStory;
	}

	public String getFullStory() {
		return fullStory;
	}
}
