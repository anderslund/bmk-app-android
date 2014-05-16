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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NyhetlisteFragment extends ListFragment {

	private List<NyhetService> nyhetServices;

	private List<Nyhet> currentNyheter;

	private static final String TAG = NyhetlisteFragment.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		nyhetServices = Arrays.asList(
			new BMKWebNyhetServiceImpl(),
			new NmfNyhetServiceImpl(),
			new TwitterNyhetServiceImpl());

		if (savedInstanceState != null) {
			currentNyheter = (List<Nyhet>) savedInstanceState.getSerializable(getString(R.string.state_current_nyheter));
		}

		if (currentNyheter != null) {
			Log.i(TAG, "Nyheter hentet fra saved instance state");
		}
		else {
			currentNyheter = new ArrayList<>();
			for (NyhetService nyhetService : nyhetServices) {
				currentNyheter.addAll(nyhetService.hentNyheter());
			}
		}

		ArrayAdapter<Nyhet> adapter = new NyhetslisteAdapter(currentNyheter);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Nyhet valgtNyhet = (Nyhet) getListAdapter().getItem(position);
		Log.i(TAG, "Klikket på " + valgtNyhet.getHeader());

		FragmentManager fm = getActivity().getSupportFragmentManager();
		NyhetDetailFragment dialog = NyhetDetailFragment.newInstance(valgtNyhet);
		dialog.show(fm, "nyhet_detalj");
	}

	//	@Override
//	public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
//		super.onInflate(activity, attrs, savedInstanceState);
//		CharSequence actionBarTitle = getActivity().getActionBar().getTitle();
//		String newTitle = actionBarTitle.toString() + ": Nyheter";
//		getActivity().getActionBar().setTitle(newTitle);
//	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState");
		outState.putSerializable(getString(R.string.state_current_nyheter), (Serializable) currentNyheter);
	}

	private class NyhetslisteAdapter extends ArrayAdapter<Nyhet> {

		public NyhetslisteAdapter(List<Nyhet> objects) {
			super(getActivity(), 0, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if ( convertView == null ){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_nyhet, null);
			}

			Nyhet nyhet = getItem(position);

			TextView nyhetslisteHeader = (TextView) convertView.findViewById(R.id.nyhetListHeader);
			nyhetslisteHeader.setText(nyhet.getHeader());

			TextView nyhetslisteContent = (TextView) convertView.findViewById(R.id.nyhetListContent);
			nyhetslisteContent.setText(StringUtil.truncate(nyhet.getContent(), 100));

			ImageView nyhetslisteIcon = (ImageView) convertView.findViewById(R.id.nyhetListIcon);

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
