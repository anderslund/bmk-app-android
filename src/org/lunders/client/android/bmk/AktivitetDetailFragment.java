package org.lunders.client.android.bmk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.lunders.client.android.bmk.model.aktivitet.AbstractAktivitet;
import org.lunders.client.android.bmk.model.lokasjon.Sted;
import org.lunders.client.android.bmk.util.DateUtil;
import org.lunders.client.android.bmk.util.StringUtil;

import java.sql.Date;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class AktivitetDetailFragment extends DialogFragment {

	private AbstractAktivitet aktivitet;

	public static final String EXTRA_AKTIVITET = "org.lunders.client.android.bmk.aktivitet_detalj";

	public static AktivitetDetailFragment newInstance(AbstractAktivitet a) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_AKTIVITET, a);

		AktivitetDetailFragment fragment = new AktivitetDetailFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		View theView = getActivity().getLayoutInflater().inflate(R.layout.dialog_aktivitet_detalj, null);
		aktivitet = (AbstractAktivitet) getArguments().getSerializable(EXTRA_AKTIVITET);

		TextView aktivitetHeader = (TextView) theView.findViewById(R.id.aktivitetListHeader);
		aktivitetHeader.setText(aktivitet.getAktivitetstype() + ": " + aktivitet.getNavn());

		//TODO: Sette opp sted på en skikkelig måte, inkl Google Maps
		Sted sted = aktivitet.getSted();
		TextView tvSted = (TextView) theView.findViewById(R.id.aktivitetListSted);
		tvSted.setText(sted != null ? sted.toString() : "Sted ikke avklart");
		tvSted.setVisibility(View.VISIBLE);

		TextView aktivitetStart = (TextView) theView.findViewById(R.id.aktivitetListStartTime);
		aktivitetStart.setText(DateUtil.getFormattedDateTime(aktivitet.getTidspunktStart()) + DateUtil.getFormattedEndTime(aktivitet.getTidspunktSlutt()));

		TextView aktivitetContent = (TextView) theView.findViewById(R.id.aktivitetListContent);
		aktivitetContent.setText(aktivitet.getBeskrivelse());

		ImageView aktivitetIcon = (ImageView) theView.findViewById(R.id.aktivitetListIcon);

		int resourceId;
		switch (aktivitet.getAktivitetstype()) {
			case Øvelse:
			case Ekstraøvelse:
				resourceId = R.drawable.ovelse_icon;
				break;
			case Oppdrag:
				resourceId = R.drawable.oppdrag_mynt;
				break;
			default:
				resourceId = R.drawable.ic_konsert_trans;
		}
		aktivitetIcon.setImageResource(resourceId);

		Dialog d = new AlertDialog.Builder(getActivity())
			.setView(theView)
				//.setTitle(aktivitet.getHeader())
			.setPositiveButton(android.R.string.ok, null)
			.create();

		d.setCanceledOnTouchOutside(true);
		return d;
	}
}
