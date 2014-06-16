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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.lunders.client.android.bmk.R;
import org.lunders.client.android.bmk.model.aktivitet.AbstractAktivitet;
import org.lunders.client.android.bmk.model.aktivitet.Oppdrag;
import org.lunders.client.android.bmk.services.AktivitetService;
import org.lunders.client.android.bmk.services.SessionService;
import org.lunders.client.android.bmk.services.impl.aktivitet.AktivitetServiceImpl;
import org.lunders.client.android.bmk.services.impl.session.SessionServiceImpl;
import org.lunders.client.android.bmk.util.DateUtil;
import org.lunders.client.android.bmk.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class AktivitetlisteFragment extends ListFragment implements AktivitetService.AktivitetListener {

	private AktivitetService                mAktivitetService;
	private SessionService mSessionService;
	private Collection<AbstractAktivitet>   mCurrentAktiviteter;
	private ArrayAdapter<AbstractAktivitet> mListAdapter;

	private static final String TAG = AktivitetlisteFragment.class.getSimpleName();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCurrentAktiviteter = new ArrayList<>();
		mAktivitetService = new AktivitetServiceImpl(getActivity());
		mSessionService = SessionServiceImpl.getInstance(getActivity());
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().setDivider(null);
		getListView().setDividerHeight(20);

		if (savedInstanceState != null) {
			mCurrentAktiviteter = (Collection<AbstractAktivitet>) savedInstanceState.getSerializable(getString(R.string.state_current_aktiviteter));
			onAktiviteterHentet(mCurrentAktiviteter);
		}

		if (mCurrentAktiviteter.isEmpty()) {
			mAktivitetService.hentAktiviteter(this);
		}
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, "onSaveInstanceState");
		outState.putSerializable(getString(R.string.state_current_aktiviteter), (Serializable) mCurrentAktiviteter);
	}


	@Override
	public void onAktiviteterHentet(Collection<AbstractAktivitet> aktiviteter) {

		if (aktiviteter == null) {
			return;
		}

		//Viser ikke oppdragsaktiviteter dersom vi ikke er logget inn
		mCurrentAktiviteter.clear();
		for (AbstractAktivitet aktivitet : aktiviteter) {
			if (aktivitet instanceof Oppdrag && !mSessionService.isLoggedIn()) {
				continue;
			}
			mCurrentAktiviteter.add(aktivitet);
		}

		if (mListAdapter == null) {
			Context c = getActivity();
			mListAdapter = new AktivitetlisteAdapter(c, mCurrentAktiviteter);
			setListAdapter(mListAdapter);
		}

		mListAdapter.clear();
		mListAdapter.addAll(mCurrentAktiviteter);
	}



	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		AbstractAktivitet valgtAktivitet = (AbstractAktivitet) getListAdapter().getItem(position);

		FragmentManager fm = getActivity().getSupportFragmentManager();
		AktivitetDetailFragment dialog = AktivitetDetailFragment.newInstance(valgtAktivitet);
		dialog.show(fm, "aktivitet_detalj");
	}


	private class AktivitetlisteAdapter extends ArrayAdapter<AbstractAktivitet> {

		public AktivitetlisteAdapter(Context c, Collection<AbstractAktivitet> objects) {
			super(c, 0, new ArrayList(objects));
		}

		@Override
		public View getView(int position, View theView, ViewGroup parent) {
			if (theView == null) {
				theView = getActivity().getLayoutInflater().inflate(R.layout.list_item_aktivitet, null);
			}

			AbstractAktivitet aktivitet = getItem(position);

			TextView aktivitetHeader = (TextView) theView.findViewById(R.id.aktivitetListHeader);
			aktivitetHeader.setText(aktivitet.getAktivitetstype() + ": " + aktivitet.getNavn());

			TextView aktivitetStart = (TextView) theView.findViewById(R.id.aktivitetListStartTime);
			aktivitetStart.setText(DateUtil.getFormattedDateTime(aktivitet.getTidspunktStart()));

			TextView aktivitetContent = (TextView) theView.findViewById(R.id.aktivitetListContent);
			aktivitetContent.setText(StringUtil.truncate(aktivitet.getBeskrivelse(), 100));

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
				case Sosialt:
					resourceId = R.drawable.ic_confetti;
					break;
				default:
					resourceId = R.drawable.ic_konsert_trans;
			}
			aktivitetIcon.setImageResource(resourceId);
			return theView;
		}
	}
}
