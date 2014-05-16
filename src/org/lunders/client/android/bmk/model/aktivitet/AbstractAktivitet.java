package org.lunders.client.android.bmk.model.aktivitet;

import org.lunders.client.android.bmk.model.lokasjon.Sted;
import org.lunders.client.android.bmk.util.DateUtil;

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
public abstract class AbstractAktivitet implements Comparable<AbstractAktivitet>, Serializable {

	private Sted sted;
	private Date tidspunktStart, tidspunktSlutt;
	private String navn, beskrivelse;
	private Aktivitetstype aktivitetstype;

	public AbstractAktivitet(String navn, Date tidspunktStart) {
		this.navn = navn;
		this.tidspunktStart = tidspunktStart;
	}

	@Override
	public String toString() {
		return String.format(
			"%s: %s\n%s", aktivitetstype, navn,
			DateUtil.getFormattedDateTime(tidspunktStart));
	}

	public String getNavn() {
		return navn;
	}

	public String getBeskrivelse() {
		return beskrivelse;
	}

	public Date getTidspunktStart() {
		return tidspunktStart;
	}

	public void setBeskrivelse(String beskrivelse) {
		this.beskrivelse = beskrivelse;
	}

	public Sted getSted() {
		return sted;
	}

	public void setSted(Sted sted) {
		this.sted = sted;
	}

	public void setTidspunktStart(Date tidspunktStart) {
		this.tidspunktStart = tidspunktStart;
	}

	public Date getTidspunktSlutt() {
		return tidspunktSlutt;
	}

	public void setTidspunktSlutt(Date tidspunktSlutt) {
		this.tidspunktSlutt = tidspunktSlutt;
	}

	public void setNavn(String navn) {
		this.navn = navn;
	}

	public Aktivitetstype getAktivitetstype() {
		return aktivitetstype;
	}

	public void setAktivitetstype(Aktivitetstype aktivitetstype) {
		this.aktivitetstype = aktivitetstype;
	}

	@Override
	public int compareTo(AbstractAktivitet another) {
		if (tidspunktStart != null) {
			return tidspunktStart.compareTo(another.tidspunktStart);
		}
		return 0;
	}

}
