package org.lunders.client.android.bmk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
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

	private static final String TAG = NyhetDetailFragment.class.getSimpleName();

	public static NyhetDetailFragment newInstance(Nyhet n) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_NYHET, n);

		NyhetDetailFragment fragment = new NyhetDetailFragment();
		fragment.setArguments(args);
		return fragment;
	}


	private View getNyhetDetailView() {
		View v = getView();
		if (v == null) {
			v = getActivity().getLayoutInflater().inflate(R.layout.dialog_nyhet_detalj, null);
		}
		return v;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.i(TAG, "onCreateDialog");

		nyhet = (Nyhet) getArguments().getSerializable(EXTRA_NYHET);

		View v = getNyhetDetailView();

		TextView nyhetsHeader = (TextView) v.findViewById(R.id.nyhetHeader);
		nyhetsHeader.setText(nyhet.getOverskrift());

		TextView nyhetslisteContent = (TextView) v.findViewById(R.id.nyhetContent);
		nyhetslisteContent.setText(nyhet.getFullStory());

		ImageView nyhetslisteIcon = (ImageView) v.findViewById(R.id.nyhetIcon);

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

		Dialog theDialog = new AlertDialog.Builder(getActivity())
			.setView(v)
			.setPositiveButton(android.R.string.ok, null)
			.create();

		theDialog.setCanceledOnTouchOutside(true);
		Log.i(TAG, "onCreateDialog exit");
		return theDialog;
	}
}
