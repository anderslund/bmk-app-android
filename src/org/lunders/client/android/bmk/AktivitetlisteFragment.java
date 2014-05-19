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
import org.lunders.client.android.bmk.model.aktivitet.AbstractAktivitet;
import org.lunders.client.android.bmk.services.AktivitetService;
import org.lunders.client.android.bmk.services.impl.aktivitet.AktivitetServiceImpl;
import org.lunders.client.android.bmk.util.DateUtil;
import org.lunders.client.android.bmk.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AktivitetlisteFragment extends ListFragment {

	private AktivitetService aktivitetService;

	private List<AbstractAktivitet> currentAktiviteter;

	private static final String TAG = AktivitetlisteFragment.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			currentAktiviteter = (List<AbstractAktivitet>) savedInstanceState.getSerializable(getString(R.string.state_current_aktiviteter));
		}

		if (currentAktiviteter != null) {
			Log.i(TAG, "Aktiviteter hentet fra saved instance state");
		}
		else {
			aktivitetService = new AktivitetServiceImpl();

			currentAktiviteter = new ArrayList<>();
			currentAktiviteter.addAll(aktivitetService.hentAktiviteter(new Date()));
		}

		ArrayAdapter<AbstractAktivitet> adapter = new AktivitetlisteAdapter(currentAktiviteter);
		setListAdapter(adapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState");
		outState.putSerializable(getString(R.string.state_current_aktiviteter), (Serializable) currentAktiviteter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		AbstractAktivitet valgtAktivitet = (AbstractAktivitet) getListAdapter().getItem(position);
		Log.i(TAG, "Klikket på " + valgtAktivitet.getNavn());

		FragmentManager fm = getActivity().getSupportFragmentManager();
		AktivitetDetailFragment dialog = AktivitetDetailFragment.newInstance(valgtAktivitet);
		dialog.show(fm, "aktivitet_detalj");
	}

	private class AktivitetlisteAdapter extends ArrayAdapter<AbstractAktivitet> {

		public AktivitetlisteAdapter(List<AbstractAktivitet> objects) {
			super(getActivity(), 0, objects);
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
				default:
					resourceId = R.drawable.ic_konsert_trans;
			}
			aktivitetIcon.setImageResource(resourceId);
			return theView;
		}
	}
}
