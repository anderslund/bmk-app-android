package org.lunders.client.android.bmk;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import org.lunders.client.android.bmk.model.bilde.Bilde;
import org.lunders.client.android.bmk.services.impl.bilde.InstagramBildeServiceImpl;
import org.lunders.client.android.bmk.services.impl.bilde.ThumbnailDownloader;

import java.io.Serializable;
import java.util.List;

public class BildeFragment extends Fragment {

	private List<Bilde> currentBilder;
	private GridView gridView;

	private ThumbnailDownloader<ImageView> thumbnailDownloader;

	private static final String TAG = BildeFragment.class.getSimpleName();
	private InstagramBildeServiceImpl instagramBildeService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		if (savedInstanceState != null) {
			currentBilder = (List<Bilde>) savedInstanceState.getSerializable(getString(R.string.state_current_bilder));
		}

		if (currentBilder != null) {
			Log.i(TAG, "Bilder hentet fra saved instance state");
		}

		instagramBildeService = new InstagramBildeServiceImpl();

		//Denne har ansvar for å hente URLer (typisk via en slags spørring)  til bilder vi senere skal laste
		//thumbnails for
		new GetAvailblePicturesTask().execute();

		//Dette er en tråd som laster ned thumbnails i bakgrunn. Den har en meldingskø som looperen plukker ut URLer
		//fra. Disse URLene settes fra getView på BildeAdapter, altså først når viewet trenger å vise en thumbnail.
		thumbnailDownloader = new ThumbnailDownloader<>(instagramBildeService, new Handler());
		thumbnailDownloader.setResponseListener(
			new ThumbnailDownloader.Listener<ImageView>() {
				@Override
				public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
					if (isVisible()) {
						imageView.setImageBitmap(thumbnail);
					}
				}
			});
		thumbnailDownloader.start();
		thumbnailDownloader.getLooper();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		//Stopper tråden. Dette er viktig, ellers vil Android fortsette å kjøre den i bakgrunnen til enheten restartes.
		thumbnailDownloader.quit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_bilder, container, false);
		gridView = (GridView) v.findViewById(R.id.gridView);

		//SetupAdapter kjøres både når viewet creates (for å vise placeholdere for bilder) og fra onPostExecute
		//i GetAvailablePicturesTast når den er ferdig med å laste URLer.
		setupAdapter();
		return v;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		thumbnailDownloader.clearQueue();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState");
		outState.putSerializable(getString(R.string.state_current_bilder), (Serializable) currentBilder);
	}


	private void setupAdapter() {
		if (getActivity() == null || gridView == null) {
			return;
		}

		if (currentBilder != null) {
			gridView.setAdapter(new BildeAdapter(currentBilder));
		}
		else {
			gridView.setAdapter(null);
		}
	}


	private class BildeAdapter extends ArrayAdapter<Bilde> {

		public BildeAdapter(List<Bilde> bilder) {
			super(getActivity(), 0, bilder);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.grid_item_image, parent, false);
			}

			ImageView imageView = (ImageView) convertView.findViewById(R.id.bilde_item);
			imageView.setImageResource(R.drawable.trumpet_icon);

			Bilde b = getItem(position);
			thumbnailDownloader.queueThumbnail(imageView, b.getUrl());

			return convertView;
		}
	}

	/**
	 * Ansvarlig for å kontakte bildetjenesten(e) for å hente ut liste(r) over bilder/thumbnails som skal lastes ned.
	 */
	private class GetAvailblePicturesTask extends AsyncTask<Void, Void, List<Bilde>> {

		@Override
		protected List<Bilde> doInBackground(Void... params) {
			return instagramBildeService.hentBilder();
		}

		@Override
		protected void onPostExecute(List<Bilde> abilder) {
			currentBilder = abilder;
			setupAdapter();
		}
	}
}
