package org.lunders.client.android.bmk.model.aktivitet;

import org.lunders.client.android.bmk.model.personell.Riggegruppe;

import java.util.Date;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class Konsert extends AbstractAktivitet {

	private Riggegruppe riggegruppe;

	private String antrekk;

	public Konsert(String navn, Date tidspunktStart) {
		super(navn, tidspunktStart);
		setAktivitetstype(Aktivitetstype.Konsert);
	}

	public String getAntrekk() {
		return antrekk;
	}

	public void setAntrekk(String antrekk) {
		this.antrekk = antrekk;
	}

	public Riggegruppe getRiggegruppe() {
		return riggegruppe;
	}

	public void setRiggegruppe(Riggegruppe riggegruppe) {
		this.riggegruppe = riggegruppe;
	}
}
