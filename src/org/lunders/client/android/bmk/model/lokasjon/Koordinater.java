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
public class Koordinater implements Serializable {

	private double lengdegrad, breddegrad;

	public Koordinater(double lengdegrad, double breddegrad) {
		this.lengdegrad = lengdegrad;
		this.breddegrad = breddegrad;
	}

	public double getLengdegrad() {
		return lengdegrad;
	}

	public double getBreddegrad() {
		return breddegrad;
	}

	public String format() {
		return lengdegrad + ","  + breddegrad;
	}
}
