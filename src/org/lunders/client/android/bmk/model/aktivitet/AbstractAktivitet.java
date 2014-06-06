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

package org.lunders.client.android.bmk.model.aktivitet;

import org.lunders.client.android.bmk.model.lokasjon.Sted;
import org.lunders.client.android.bmk.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

public abstract class AbstractAktivitet implements Comparable<AbstractAktivitet>, Serializable {

	private String navn, beskrivelse;
	private Sted sted;
	private Date tidspunktStart, tidspunktSlutt;
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
		if (tidspunktStart != null && another.tidspunktStart != null) {
			return tidspunktStart.compareTo(another.tidspunktStart);
		}
		return 0;
	}



}
