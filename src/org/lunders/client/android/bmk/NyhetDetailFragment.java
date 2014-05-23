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
import org.lunders.client.android.bmk.services.NyhetService;

public class NyhetDetailFragment extends DialogFragment implements NyhetService.NyhetDetaljListener {

	private Nyhet nyhet;

	public static final String EXTRA_NYHET = "org.lunders.client.android.bmk.nyhet_detalj";

	private static final String TAG = NyhetDetailFragment.class.getSimpleName();
	private View nyhetDetailView;

	public static NyhetDetailFragment newInstance(Nyhet n) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_NYHET, n);

		NyhetDetailFragment fragment = new NyhetDetailFragment();
		fragment.setArguments(args);
		return fragment;
	}


	private View getNyhetDetailView() {
		if (nyhetDetailView == null) {
			nyhetDetailView = getActivity().getLayoutInflater().inflate(R.layout.dialog_nyhet_detalj, null);
		}
		return nyhetDetailView;
	}

	@Override
	public void onNyhetHentet(Nyhet nyheten) {
		Log.i(TAG, "onNyhetHentet: " + nyheten.getFullStory());
		TextView nyhetslisteContent = (TextView) getNyhetDetailView().findViewById(R.id.nyhetContent);
		Log.i(TAG, nyhetslisteContent.toString());

		nyhetslisteContent.setText(nyheten.getFullStory());
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.i(TAG, "onCreateDialog");

		nyhet = (Nyhet) getArguments().getSerializable(EXTRA_NYHET);

		nyhetDetailView = getNyhetDetailView();

		TextView nyhetsHeader = (TextView) nyhetDetailView.findViewById(R.id.nyhetHeader);
		nyhetsHeader.setText(nyhet.getOverskrift());

		ImageView nyhetslisteIcon = (ImageView) nyhetDetailView.findViewById(R.id.nyhetIcon);

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
			.setView(nyhetDetailView)
			.setPositiveButton(android.R.string.ok, null)
			.create();

		theDialog.setCanceledOnTouchOutside(true);
		Log.i(TAG, "onCreateDialog exit");
		return theDialog;
	}
}
