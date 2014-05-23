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
		nyhetServices = Arrays.asList(
			bmkWebNyhetService = new BMKWebNyhetServiceImpl(this),
			nmfNyhetService = new NmfNyhetServiceImpl(),
			twitterNyhetService = new TwitterNyhetServiceImpl());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			currentNyheter = (Set<Nyhet>) savedInstanceState.getSerializable(getString(R.string.state_current_nyheter));
		}

		if (currentNyheter != null) {
			Log.i(TAG, "Nyheter hentet fra saved instance state");
		}
		else {
			currentNyheter = new TreeSet<>();
			for (NyhetService nyhetService : nyhetServices) {
				currentNyheter.addAll(nyhetService.hentNyheter());
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
	public void onNyhetHentet(Nyhet nyhet) {
		Log.i(TAG, "onNyhetHentet");
//		TextView tv = (TextView) getActivity().findViewById(R.id.nyhetContent);
//		tv.setText(nyhet.getFullStory());

		FragmentManager fm = getActivity().getSupportFragmentManager();
		NyhetDetailFragment dialog = NyhetDetailFragment.newInstance(nyhet);
		dialog.show(fm, "nyhet_detalj");
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Nyhet nyhet = (Nyhet) getListAdapter().getItem(position);

		switch (nyhet.getKilde()) {
			case BMK:
				bmkWebNyhetService.hentNyhet(nyhet);
				break;

			case NMF:
				//TODO
				nmfNyhetService.hentNyhet(nyhet);
				break;

			case Twitter:
				//TODO
				twitterNyhetService.hentNyhet(nyhet);
				break;
		}
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



//	//	@Override
//	public void onViewCreated(View view, Bundle savedInstanceState) {
//		super.onViewCreated(view, savedInstanceState);
//		getListView().setDivider(getResources().getDrawable(android.R.drawable.screen_background_light_transparent));
//		getListView().setDividerHeight(20);
//	}
}
