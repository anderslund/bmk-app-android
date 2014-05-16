package org.lunders.client.android.bmk;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.ArrayAdapter;
import org.lunders.client.android.bmk.model.aktivitet.AbstractAktivitet;
import org.lunders.client.android.bmk.services.AktivitetService;
import org.lunders.client.android.bmk.services.impl.aktivitet.AktivitetServiceImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BilderFragment extends ListFragment {

	private AktivitetService aktivitetService;

	private List<AbstractAktivitet> currentAktiviteter;

	private static final String TAG = BilderFragment.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		aktivitetService = new AktivitetServiceImpl();

		currentAktiviteter = new ArrayList<>();
		currentAktiviteter.addAll(aktivitetService.hentAktiviteter(new Date()));

		ArrayAdapter<AbstractAktivitet> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, currentAktiviteter);
		setListAdapter(adapter);

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
		outState.putSerializable(getString(R.string.state_current_nyheter), (Serializable) currentAktiviteter);
	}
}
