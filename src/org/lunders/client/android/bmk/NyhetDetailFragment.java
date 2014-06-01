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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.model.nyheter.Nyhetskilde;
import org.lunders.client.android.bmk.services.NyhetService;

public class NyhetDetailFragment extends DialogFragment implements NyhetService.NyhetDetaljListener {

	private Activity mActivity;
	private View     mNyhetDetailView;

	private static final String EXTRA_NYHET = "org.lunders.client.android.bmk.nyhet_detalj";

	private static final String TAG = NyhetDetailFragment.class.getSimpleName();

	private NyhetDetailFragment(Activity activity) {
		mActivity = activity;
	}

	public static NyhetDetailFragment newInstance(Activity activity, Nyhet n) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_NYHET, n);

		NyhetDetailFragment fragment = new NyhetDetailFragment(activity);
		fragment.setArguments(args);
		return fragment;
	}


	private View getNyhetDetailView() {
		if (mNyhetDetailView == null) {
			mNyhetDetailView = mActivity.getLayoutInflater().inflate(R.layout.dialog_nyhet_detalj, null);
		}
		return mNyhetDetailView;
	}


	@Override
	public void onNyhetHentet(Nyhet nyheten) {
		Log.i(TAG, "onNyhetHentet");


		ProgressBar prog = (ProgressBar) getNyhetDetailView().findViewById(R.id.nyhet_content_progress);
		prog.setVisibility(View.GONE);

		TextView nyhetslisteContent = (TextView) mNyhetDetailView.findViewById(R.id.nyhetContent);
		//nyhetslisteContent.setLinksClickable(true);
		if (nyheten.getKilde() == Nyhetskilde.Twitter) {
			nyhetslisteContent.setAutoLinkMask(Linkify.WEB_URLS);
		}
		else {
			nyhetslisteContent.setMovementMethod(LinkMovementMethod.getInstance());
		}
		nyhetslisteContent.setText(nyheten.getFullStory());
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.i(TAG, "onCreateDialog");

		Nyhet nyhet = (Nyhet) getArguments().getSerializable(EXTRA_NYHET);

		mNyhetDetailView = getNyhetDetailView();

		TextView nyhetsHeader = (TextView) mNyhetDetailView.findViewById(R.id.nyhetHeader);
		nyhetsHeader.setText(nyhet.getOverskrift());

		//Twitter-detaljer er allerede "lastet", så vi trenger ingen progress for det.
		if (nyhet.getKilde() != Nyhetskilde.Twitter) {
			ProgressBar prog = (ProgressBar) mNyhetDetailView.findViewById(R.id.nyhet_content_progress);
			prog.setVisibility(View.VISIBLE);
		}

		ImageView nyhetslisteIcon = (ImageView) mNyhetDetailView.findViewById(R.id.nyhetIcon);
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
			.setView(mNyhetDetailView)
			.setPositiveButton(android.R.string.ok, null)
			.create();

		theDialog.setCanceledOnTouchOutside(true);
		Log.i(TAG, "onCreateDialog exit");
		return theDialog;
	}
}
