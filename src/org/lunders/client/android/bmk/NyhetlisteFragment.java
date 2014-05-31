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
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.nyhet.BMKWebNyhetServiceImpl;
import org.lunders.client.android.bmk.services.impl.nyhet.NmfNyhetServiceImpl;
import org.lunders.client.android.bmk.services.impl.nyhet.TwitterNyhetServiceImpl;
import org.lunders.client.android.bmk.util.DateUtil;
import org.lunders.client.android.bmk.util.StringUtil;

import java.io.Serializable;
import java.util.*;

public class NyhetlisteFragment extends ListFragment implements NyhetService.NyhetListener {

	private List<? extends NyhetService> nyhetServices;

	private NyhetService bmkWebNyhetService, nmfNyhetService, twitterNyhetService;

	private Set<Nyhet> currentNyheter;

	public static final int PREVIEW_SIZE = 100;

	private static final String TAG = NyhetlisteFragment.class.getSimpleName();

	public NyhetlisteFragment() {
		Log.i(TAG, "constructor");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		nyhetServices = Arrays.asList(
			bmkWebNyhetService = new BMKWebNyhetServiceImpl(),
			nmfNyhetService = new NmfNyhetServiceImpl(),
			twitterNyhetService = new TwitterNyhetServiceImpl(getActivity()));
		
		setRetainInstance(true);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().setDivider(null);
		getListView().setDividerHeight(20);

		if (savedInstanceState != null) {
			currentNyheter = (Set<Nyhet>) savedInstanceState.getSerializable(getString(R.string.state_current_nyheter));
		}

		if (currentNyheter != null) {
			Log.i(TAG, "Nyheter hentet fra saved instance state");
		}
		else {
			currentNyheter = new TreeSet<>();
			for (NyhetService nyhetService : nyhetServices) {
				nyhetService.hentNyheter(this);
			}
		}

		setListAdapter(new NyhetslisteAdapter(currentNyheter));
	}

	@Override
	public void onNyheterHentet(Collection<Nyhet> nyheter) {
		currentNyheter.addAll(nyheter);
		setListAdapter(new NyhetslisteAdapter(currentNyheter));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Nyhet nyhet = (Nyhet) getListAdapter().getItem(position);

		FragmentManager fm = getActivity().getSupportFragmentManager();
		NyhetDetailFragment dialog = NyhetDetailFragment.newInstance(getActivity(), nyhet);

		switch (nyhet.getKilde()) {
			case BMK:
				bmkWebNyhetService.hentNyhet(nyhet, dialog);
				break;

			case NMF:
				nmfNyhetService.hentNyhet(nyhet, dialog);
				break;

			case Twitter:
				twitterNyhetService.hentNyhet(nyhet, dialog);
				break;
		}

		dialog.show(fm, "nyhet_detalj");
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState");
		outState.putSerializable(getString(R.string.state_current_nyheter), (Serializable) currentNyheter);
	}

	private class NyhetslisteAdapter extends ArrayAdapter<Nyhet> {

		public NyhetslisteAdapter(Set<Nyhet> objects) {
			super(getActivity(), 0, new ArrayList(objects));
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_nyhet, null);
			}

			Nyhet nyhet = getItem(position);

			TextView nyhetslisteHeader = (TextView) convertView.findViewById(R.id.nyhetHeader);
			nyhetslisteHeader.setText(nyhet.getOverskrift());

			TextView nyhetslisteDato = (TextView) convertView.findViewById(R.id.nyhetDato);
			nyhetslisteDato.setText(DateUtil.getFormattedDateTime(nyhet.getDato()));

			TextView nyhetslisteContent = (TextView) convertView.findViewById(R.id.nyhetContent);
			nyhetslisteContent.setText(StringUtil.truncate(nyhet.getIngress(), PREVIEW_SIZE));

			ImageView nyhetslisteIcon = (ImageView) convertView.findViewById(R.id.nyhetIcon);

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
			return convertView;
		}
	}
}
