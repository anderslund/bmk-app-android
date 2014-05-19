package org.lunders.client.android.bmk.model.lokasjon;

import android.net.Uri;

import java.io.Serializable;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class Sted implements Serializable {

	private String navn;

	private Koordinater koordinater;

	public Sted(String navn) {
		this.navn = navn;
	}

	public Sted(String navn, Koordinater koordinater) {
		this(navn);
		this.koordinater = koordinater;
	}

	public String getNavn() {
		return navn;
	}

	public void setNavn(String navn) {
		this.navn = navn;
	}

	public Koordinater getKoordinater() {
		return koordinater;
	}

	@Override
	public String toString() {
		return navn;
	}

	public String formatAsUri() {
		return "geo:" + koordinater.format() + "?q=" + Uri.encode(koordinater.format()+ "(" + navn + ")");
	}
}
