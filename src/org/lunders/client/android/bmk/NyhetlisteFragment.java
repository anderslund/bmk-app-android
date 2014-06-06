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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nhaarman.listviewanimations.itemmanipulation.ExpandableListItemAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.model.nyheter.Nyhetskilde;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.nyhet.BMKWebNyhetServiceImpl;
import org.lunders.client.android.bmk.services.impl.nyhet.NmfNyhetServiceImpl;
import org.lunders.client.android.bmk.services.impl.nyhet.TwitterNyhetServiceImpl;
import org.lunders.client.android.bmk.util.DateUtil;
import org.lunders.client.android.bmk.util.StringUtil;

import java.io.Serializable;
import java.util.*;

public class NyhetlisteFragment extends ListFragment implements NyhetService.NyhetListener, NyhetService.NyhetDetaljListener {

	private List<? extends NyhetService> nyhetServices;

	private NyhetService bmkWebNyhetService, nmfNyhetService, twitterNyhetService;

	private AlphaInAnimationAdapter alphaInAnimationAdapter;
	private NyhetslisteAdapter      listAdapter;

	private Set<Nyhet> currentNyheter;

	public static final int PREVIEW_SIZE = 100;

	private static final String TAG = NyhetlisteFragment.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		currentNyheter = new TreeSet<>();

		nyhetServices = Arrays.asList(
			bmkWebNyhetService = new BMKWebNyhetServiceImpl(),
			nmfNyhetService = new NmfNyhetServiceImpl(),
			twitterNyhetService = new TwitterNyhetServiceImpl(getActivity()));

		//setRetainInstance(true);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().setDivider(null);

		if (savedInstanceState != null) {
			currentNyheter = (Set<Nyhet>) savedInstanceState.getSerializable(getString(R.string.state_current_nyheter));
			onNyheterHentet(currentNyheter);
		}

		if (!currentNyheter.isEmpty()) {
			Log.i(TAG, "Nyheter hentet fra saved instance state");
		}
		else {
			for (NyhetService nyhetService : nyhetServices) {
				nyhetService.hentNyheter(this);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(getString(R.string.state_current_nyheter), (Serializable) currentNyheter);
	}

	@Override
	public void onNyheterHentet(Collection<Nyhet> nyheter) {
		currentNyheter.addAll(nyheter);

		if (listAdapter == null) {
			listAdapter = new NyhetslisteAdapter(getActivity(), currentNyheter);
			alphaInAnimationAdapter = new AlphaInAnimationAdapter(listAdapter);
			alphaInAnimationAdapter.setAbsListView(getListView());
			alphaInAnimationAdapter.setInitialDelayMillis(500);
			setListAdapter(alphaInAnimationAdapter);
		}

		listAdapter.clear();
		listAdapter.addAll(currentNyheter);
	}


	@Override
	public void onNyhetHentet(Nyhet nyheten) {
		View contentView = listAdapter.getContentView(nyheten.getListPosition());
		if (contentView != null) {
			TextView tv = (TextView) contentView.findViewById(R.id.nyhetContent);

			if (nyheten.getKilde() == Nyhetskilde.Twitter) {
				tv.setAutoLinkMask(Linkify.WEB_URLS);
			}
			else {
				tv.setMovementMethod(LinkMovementMethod.getInstance());
			}
			tv.setText(nyheten.getFullStory(), TextView.BufferType.SPANNABLE);
		}
	}



	private class NyhetslisteAdapter extends ExpandableListItemAdapter<Nyhet> {

		public NyhetslisteAdapter(Context context, Set<Nyhet> objects) {
			super(context, R.layout.list_item_nyhet, R.id.nyhet_header_section, R.id.nyhet_content_section, new ArrayList(objects));
		}

		@Override
		public View getTitleView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.nyhet_header_section, null);
			}

			TextView nyhetslisteHeader = (TextView) convertView.findViewById(R.id.nyhetHeader);
			TextView nyhetslisteDato = (TextView) convertView.findViewById(R.id.nyhetDato);
			TextView nyhetslisteIngress = (TextView) convertView.findViewById(R.id.nyhetIngress);
			ImageView nyhetslisteIcon = (ImageView) convertView.findViewById(R.id.nyhetIcon);

			Nyhet nyhet = getItem(position);
			nyhetslisteHeader.setText(nyhet.getOverskrift());
			nyhetslisteIngress.setText(StringUtil.truncate(nyhet.getIngress(), PREVIEW_SIZE));
			nyhetslisteDato.setText(DateUtil.getFormattedDateTime(nyhet.getDato()));

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

		@Override
		public View getContentView(int position, View convertView, ViewGroup parent) {

			convertView = getActivity().getLayoutInflater().inflate(R.layout.nyhet_content_section, null);

			Nyhet nyhet = (Nyhet) getListAdapter().getItem(position);
			nyhet.setListPosition(position);

			switch (nyhet.getKilde()) {
				case BMK:
					bmkWebNyhetService.hentNyhet(nyhet, NyhetlisteFragment.this);
					break;

				case NMF:
					nmfNyhetService.hentNyhet(nyhet, NyhetlisteFragment.this);
					break;

				case Twitter:
					twitterNyhetService.hentNyhet(nyhet, NyhetlisteFragment.this);
					break;
			}
			return convertView;
		}
	}
}
