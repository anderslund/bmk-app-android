package org.lunders.client.android.bmk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class NyhetDetailFragment extends DialogFragment {

	private Nyhet nyhet;

	public static final String EXTRA_NYHET = "org.lunders.client.android.bmk.nyhet_detalj";

	public static NyhetDetailFragment newInstance(Nyhet n) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_NYHET, n);

		NyhetDetailFragment fragment = new NyhetDetailFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_nyhet_detalj, null);
		nyhet = (Nyhet) getArguments().getSerializable(EXTRA_NYHET);

		TextView nyhetslisteHeader = (TextView) v.findViewById(R.id.nyhetListHeader);
		nyhetslisteHeader.setText(nyhet.getHeader());

		TextView nyhetslisteContent = (TextView) v.findViewById(R.id.nyhetListContent);
		nyhetslisteContent.setText(nyhet.getContent());

		ImageView nyhetslisteIcon = (ImageView) v.findViewById(R.id.nyhetListIcon);

		int resourceId;
		switch (nyhet.getKilde()) {
			case NMF:
				resourceId = R.drawable.nmf_logo;
				break;
			case Twitter:
				resourceId = R.drawable.twitter;
				break;
			default:
				resourceId = R.drawable.bmk_logo_transparent;
		}
		nyhetslisteIcon.setImageResource(resourceId);

		Dialog d = new AlertDialog.Builder(getActivity())
			.setView(v)
				//.setTitle(nyhet.getHeader())
			.setPositiveButton(android.R.string.ok, null)
			.create();

		d.setCanceledOnTouchOutside(true);
		return d;
	}
}
