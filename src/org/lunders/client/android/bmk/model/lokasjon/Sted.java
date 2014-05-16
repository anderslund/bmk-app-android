package org.lunders.client.android.bmk.model.lokasjon;

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

	private double lengdegrad, breddegrad;

	public Sted(String navn) {
		this.navn = navn;
	}

	@Override
	public String toString() {
		return navn;
	}
}
