package org.lunders.client.android.bmk.model.aktivitet;

import org.lunders.client.android.bmk.model.personell.Riggegruppe;

import java.util.Calendar;
import java.util.Date;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class Ovelse extends AbstractAktivitet {

	private Riggegruppe riggegruppe;


	public Ovelse(Date tidspunktStart) {
		this("Ordinær", tidspunktStart);
	}

	public Ovelse(String navn, Date tidspunktStart) {
		super(navn, tidspunktStart);

		Calendar c = Calendar.getInstance();
		c.setTime(getTidspunktStart());
		setAktivitetstype(c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY ? Aktivitetstype.Øvelse : Aktivitetstype.Ekstraøvelse);
	}

	public Riggegruppe getRiggegruppe() {
		return riggegruppe;
	}

	public void setRiggegruppe(Riggegruppe riggegruppe) {
		this.riggegruppe = riggegruppe;
	}
}
