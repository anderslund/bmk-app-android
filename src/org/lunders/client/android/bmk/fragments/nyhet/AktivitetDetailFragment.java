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

package org.lunders.client.android.bmk.fragments.nyhet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.lunders.client.android.bmk.R;
import org.lunders.client.android.bmk.model.aktivitet.AbstractAktivitet;
import org.lunders.client.android.bmk.model.lokasjon.Sted;
import org.lunders.client.android.bmk.services.SessionService;
import org.lunders.client.android.bmk.services.impl.session.SessionServiceImpl;
import org.lunders.client.android.bmk.util.DateUtil;

import java.text.MessageFormat;


public class AktivitetDetailFragment extends DialogFragment {

	private AbstractAktivitet mAktivitet;
	private SessionService mSessionService;

	public static final String EXTRA_AKTIVITET = "org.lunders.client.android.bmk.aktivitet_detalj";
	public static final int    COLOR_LINK_TEXT = 0xff33b5e5;

	private static final String TAG = AktivitetDetailFragment.class.getSimpleName();


	public static AktivitetDetailFragment newInstance(AbstractAktivitet a) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_AKTIVITET, a);

		AktivitetDetailFragment fragment = new AktivitetDetailFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mSessionService = SessionServiceImpl.getInstance(getActivity());

		View theView = getActivity().getLayoutInflater().inflate(R.layout.dialog_aktivitet_detalj, null);
		mAktivitet = (AbstractAktivitet) getArguments().getSerializable(EXTRA_AKTIVITET);

		TextView aktivitetHeader = (TextView) theView.findViewById(R.id.aktivitetListHeader);
		aktivitetHeader.setText(mAktivitet.getAktivitetstype() + ": " + mAktivitet.getNavn());

		final Sted sted = mAktivitet.getSted();
		TextView tvSted = (TextView) theView.findViewById(R.id.aktivitetListSted);
		if (sted == null) {
			tvSted.setText(getString(R.string.sted_ikke_avklart));
		}
		else {
			tvSted.setText(sted.getNavn());
			if (sted.getKoordinater() != null) {
				tvSted.setClickable(true);
				tvSted.setTextColor(COLOR_LINK_TEXT);
				tvSted.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
				tvSted.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							fireGoogleMaps(sted);
							AktivitetDetailFragment.this.dismiss();
						}
					}
				);
			}
		}
		tvSted.setVisibility(View.VISIBLE);

		TextView aktivitetStart = (TextView) theView.findViewById(R.id.aktivitetListStartTime);
		aktivitetStart.setText(DateUtil.getFormattedDateTime(mAktivitet.getTidspunktStart()) + DateUtil.getFormattedEndTime(mAktivitet.getTidspunktSlutt()));

		TextView aktivitetContent = (TextView) theView.findViewById(R.id.aktivitetListContent);
		aktivitetContent.setText(mAktivitet.getBeskrivelse());

		ImageView aktivitetIcon = (ImageView) theView.findViewById(R.id.aktivitetListIcon);

		if (System.currentTimeMillis() > mAktivitet.getTidspunktStart().getTime()) {
			aktivitetHeader.setPaintFlags(aktivitetHeader.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			aktivitetStart.setPaintFlags(aktivitetHeader.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			aktivitetContent.setPaintFlags(aktivitetHeader.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			tvSted.setPaintFlags(aktivitetHeader.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}

		int resourceId;
		switch (mAktivitet.getAktivitetstype()) {
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

		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
			.setView(theView)
			.setPositiveButton(R.string.lukk, null);

		if (mSessionService.isLoggedIn() && mAktivitet.getTidspunktStart() != null && mAktivitet.getTidspunktStart().getTime() > System.currentTimeMillis()) {
			dialogBuilder.setNeutralButton(
				mAktivitet.isDeltar() ? R.string.kommer_ikke : R.string.kommer_allikevel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						fireEmail();
					}
				});
		}

		Dialog loginDialog = dialogBuilder.create();
		loginDialog.setCanceledOnTouchOutside(true);
		return loginDialog;
	}


	//TODO: Send mail til gruppeleder/post@borgemusikken/dirigent
	//TODO: Lagre om man kommer eller ikke, på instansen som skrives til lokalt lager og sørge for at den
	//ikke overskrives når vi henter aktiviteter fra nett.
	private void fireEmail() {

		String subject = "Kommer " +
			(mAktivitet.isDeltar() ? "ikke" : "allikevel") +
			" på " + mAktivitet.getAktivitetstype().toString().toLowerCase();

		String content = getResources().getString(R.string.aktivitet_deltar_mail_content);
		content = MessageFormat.format(
			content,
			mAktivitet.isDeltar() ? "ikke" : "allikevel",
			String.valueOf(mAktivitet.getAktivitetstype()).toLowerCase(),
			DateUtil.getFormattedDate(mAktivitet.getTidspunktStart()),
			mSessionService.getCurrentUserDisplayName());

		Intent emailIntent = new Intent(
			Intent.ACTION_SENDTO,
			Uri.fromParts("mailto", "lunders.and@gmail.com, anders.lund@gjensidige.no", null));

		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, content);

		startActivity(emailIntent);
	}


	private void fireGoogleMaps(Sted sted) {
		//Har allerede sjekket at koordinater != null her.
		Uri mapURI = Uri.parse(sted.formatAsUri() + "&z=8");
		Intent mapsIntent = new Intent(Intent.ACTION_VIEW, mapURI);
		startActivity(mapsIntent);
	}
}
