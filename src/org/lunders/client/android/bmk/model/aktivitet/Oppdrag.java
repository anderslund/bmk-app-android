package org.lunders.client.android.bmk.model.aktivitet;

import java.util.Date;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class Oppdrag extends AbstractAktivitet {

	private String antrekk;

	public Oppdrag(String navn, Date tidspunktStart) {
		super(navn, tidspunktStart);
		setAktivitetstype(Aktivitetstype.Oppdrag);
	}

	public String getAntrekk() {
		return antrekk;
	}

	public void setAntrekk(String antrekk) {
		this.antrekk = antrekk;
	}
}
