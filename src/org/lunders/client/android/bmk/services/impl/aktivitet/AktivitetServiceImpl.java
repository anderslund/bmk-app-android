package org.lunders.client.android.bmk.services.impl.aktivitet;

import android.util.Log;
import org.lunders.client.android.bmk.model.aktivitet.AbstractAktivitet;
import org.lunders.client.android.bmk.model.aktivitet.Konsert;
import org.lunders.client.android.bmk.model.aktivitet.Oppdrag;
import org.lunders.client.android.bmk.model.aktivitet.Ovelse;
import org.lunders.client.android.bmk.model.lokasjon.Sted;
import org.lunders.client.android.bmk.services.AktivitetService;
import org.lunders.client.android.bmk.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class AktivitetServiceImpl implements AktivitetService {

	private static final String TAG = AktivitetServiceImpl.class.getSimpleName();

	private String testBeskrivelse = "Konserten er en «hyllest» til Erik Bye sine udødelige viser. " +
		"Vi har funnet frem noen av de mest kjente tingene han skrev til konserten.\n" +
		"Det er en link mellom Erik Bye og Alf Prøysen (som har jubileum i år – han ville vært 100 år i 2014)." +
		"Erik Bye skrev en vise til Alf Prøysen som het Alf." +
		"Alf Prøysen skrev også en sang til Erik Bye som heter Jørgen Hattemaker." +
		"Begge disse sangene er med på konserten.\n\n" +
		"I anledning knyttningen mellom Erik Bye og Alf Prøysen og det at Alf Prøysen har jubileum i år," +
		"skal vi også spille et Portpori over de mest kjente sangene til Alf Prøysen på konserten.\n\n" +
		"Konserten vil ha et «maritimt» preg og være på BåthusTeateret i Fredrikstad som ligger på brygga midt i Fr.stad sentrum." +
		"Solist og konferansier vil være Alexander Hermansen som er en lokal artist fra Fredrikstad med nære bånd til det maritime miljøet i byen.";

	@Override
	public List<AbstractAktivitet> hentAktiviteter(Date tilDato) {
		Log.i(TAG, "Henter aktiviteter fra OneDrive...");

		List l = new ArrayList();

		final Ovelse oe = new Ovelse("Erik Bye", DateUtil.getDate("2014-05-24 12:00"));
		oe.setTidspunktSlutt(DateUtil.getDate("2014-05-24 16:00"));
		oe.setBeskrivelse("Ekstraøvelse før Erik Bye-konserten.");
		oe.setSted(new Sted("Båthusteatret, Fredrikstad"));
		l.add(oe);

		final Konsert k = new Konsert("Erik Bye", DateUtil.getDate("2014-05-24 19:00"));
		k.setTidspunktSlutt(DateUtil.getDate("2014-05-24 21:00"));
		k.setBeskrivelse(testBeskrivelse);
		k.setSted(new Sted("Båthusteatret, Fredrikstad"));
		l.add(k);

		final Ovelse o = new Ovelse(DateUtil.getDate("2014-05-20 19:00"));
		o.setTidspunktSlutt(DateUtil.getDate("2014-05-20 22:00"));
		o.setBeskrivelse("Vanlig øvelse med Sverre som dirigent.");
		o.setSted(new Sted("Velferdsbygget Denofa"));
		l.add(o);

		final Oppdrag op1 = new Oppdrag("Varetelling REMA", DateUtil.getDate("2014-06-01 08:00"));
		op1.setBeskrivelse("Vi tar en heidundrende varetelling for Rema og tjener 10.000 raske.");
		l.add(op1);

		final Oppdrag op2 = new Oppdrag("Bylørdag", DateUtil.getDate("2014-06-07 11:00"));
		op2.setBeskrivelse("Bylørdag nr 2 i Fredrikstad sentrum. 10.000 kr i kassa!");
		l.add(op2);

		Log.i(TAG, "Aktiviteter hentet");
		return l;
	}
}
