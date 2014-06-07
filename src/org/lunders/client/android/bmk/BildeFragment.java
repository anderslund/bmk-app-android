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
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import org.lunders.client.android.bmk.model.bilde.Bilde;
import org.lunders.client.android.bmk.services.impl.bilde.DownloadListener;
import org.lunders.client.android.bmk.services.impl.bilde.ImageDownloader;
import org.lunders.client.android.bmk.services.impl.bilde.InstagramBildeServiceHelper;
import org.lunders.client.android.bmk.util.UiUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class BildeFragment extends Fragment {

	private InstagramBildeServiceHelper mInstagramBildeService;
	private List<Bilde>                 mCurrentBilder;
	private GridView                    mGridView;
	private Context                     mContext;
	private ImageDownloader<ImageView>  mImageDownloader;

	private static final String TAG = BildeFragment.class.getSimpleName();

	public BildeFragment(Context context) {
		mContext = context;
		mInstagramBildeService = new InstagramBildeServiceHelper();
		setupImageDownloader();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		if (savedInstanceState != null) {
			mCurrentBilder = (List<Bilde>) savedInstanceState.getSerializable(getString(R.string.state_current_bilder));
		}

		// Hvis vi ikke fant bilder i state, må vi laste på nytt
		if (mCurrentBilder == null || mCurrentBilder.isEmpty()) {
			new GetAvailblePicturesTask().execute();
		}
	}


	private void setupImageDownloader() {
		//Setter i gang en tråd som laster ned thumbnails i bakgrunnen.
		//Den har en meldingskø som looperen plukker ut URLer fra.
		//Disse URLene settes fra getView på BildeAdapter, altså først når viewet trenger å vise en thumbnail.
		//Handleren her assosieres med den tråden som oppretter den (dvs UI-tråden)
		mImageDownloader = new ImageDownloader<>();
		mImageDownloader.setDownloadListener(
			new DownloadListener<ImageView>() {
				@Override
				public void onImageDownloaded(ImageView imageView, Bilde thumbnail) {
					if (isVisible()) {
						byte[] thumbnailBytes = thumbnail.getThumbnailBytes();
						imageView.setImageBitmap(BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length));
					}
				}
			}
		);
		mImageDownloader.start();
		mImageDownloader.getLooper();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		//Stopper tråden. Dette er viktig, ellers vil Android fortsette å kjøre den i bakgrunnen til enheten restartes.
		mImageDownloader.quit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_bilder, container, false);
		mGridView = (GridView) v.findViewById(R.id.gridView);

		//SetupAdapter kjøres både når viewet creates (for å vise placeholdere for bilder) og fra onPostExecute
		//i GetAvailablePicturesTast når den er ferdig med å laste URLer.
		setupAdapter();
		return v;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mImageDownloader.clearQueue();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(getString(R.string.state_current_bilder), (Serializable) mCurrentBilder);
	}


	private void setupAdapter() {
		if (getActivity() == null || mGridView == null) {
			return;
		}

		if (mCurrentBilder != null) {
			mGridView.setAdapter(new BildeAdapter(mCurrentBilder));
		}
		else {
			mGridView.setAdapter(null);
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

			final Bilde b = getItem(position);
			byte[] thumbnailBytes = b.getThumbnailBytes();
			if (thumbnailBytes != null) {
				imageView.setImageBitmap(BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length));
			}
			else {
				imageView.setImageResource(R.drawable.trumpet_icon);

				if (mImageDownloader != null) {
					mImageDownloader.queueImage(imageView, b, ImageDownloader.ImageType.THUMBNAIL);
				}
			}

			imageView.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//Åpne en dialog med full-size bilde, antall likes, hvem bildet er tatt av og hvilken tekst
						FragmentManager fm = getActivity().getSupportFragmentManager();
						BildeDetailFragment dialog = BildeDetailFragment.newInstance(b);
						dialog.show(fm, "bilde_detalj");
					}
				}
			);

			return convertView;
		}
	}

	/**
	 * Ansvarlig for å kontakte bildetjenesten(e) for å hente ut liste(r) over bilder/thumbnails som skal lastes ned.
	 */
	private class GetAvailblePicturesTask extends AsyncTask<Void, Void, List<Bilde>> {

		@Override
		protected List<Bilde> doInBackground(Void... params) {

			try {
				return mInstagramBildeService.hentBilder();
			}
			catch (final IOException e) {
				UiUtil.showToast((Activity) mContext, R.string.instagram_feil, Toast.LENGTH_LONG);
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Bilde> abilder) {
			mCurrentBilder = abilder;
			setupAdapter();
		}
	}
}
