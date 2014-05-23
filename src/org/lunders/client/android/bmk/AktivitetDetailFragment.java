package org.lunders.client.android.bmk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.lunders.client.android.bmk.model.aktivitet.AbstractAktivitet;
import org.lunders.client.android.bmk.model.lokasjon.Sted;
import org.lunders.client.android.bmk.util.DateUtil;


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

		final Sted sted = aktivitet.getSted();
		TextView tvSted = (TextView) theView.findViewById(R.id.aktivitetListSted);
		if (sted == null) {
			tvSted.setText(getString(R.string.sted_ikke_avklart));
		}
		else {
			tvSted.setText(sted.getNavn());
			if (sted.getKoordinater() != null) {
				tvSted.setClickable(true);
				tvSted.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
				tvSted.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
				tvSted.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							fireGoogleMaps(sted);
						}
					}
				);
			}
		}
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
				//.setTitle(aktivitet.getOverskrift())
			.setPositiveButton(android.R.string.ok, null)
			.create();

		d.setCanceledOnTouchOutside(true);
		return d;
	}

	private void fireGoogleMaps(Sted sted) {
		//Har allerede sjekket at koordinater != null her.
		Uri mapURI = Uri.parse(sted.formatAsUri() + "&z=8");
		Intent i = new Intent(Intent.ACTION_VIEW, mapURI);
		startActivity(i);
	}
}