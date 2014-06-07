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

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.lunders.client.android.bmk.model.bilde.Bilde;
import org.lunders.client.android.bmk.services.impl.bilde.DownloadListener;
import org.lunders.client.android.bmk.services.impl.bilde.ImageDownloader;

public class BildeDetailFragment extends DialogFragment implements DownloadListener<ImageView> {

	private Bilde                      mBilde;
	private ImageDownloader<ImageView> mImageDownloader;

	public static BildeDetailFragment newInstance(Bilde b) {
		BildeDetailFragment fragment = new BildeDetailFragment(b);
		return fragment;
	}

	private BildeDetailFragment() {

	}

	public BildeDetailFragment(Bilde bilde) {
		this();
		this.mBilde = bilde;

		mImageDownloader = new ImageDownloader();
		mImageDownloader.setDownloadListener(this);
		mImageDownloader.start();
		mImageDownloader.getLooper();
	}

	public void onImageDownloaded(ImageView imageView, Bilde image) {
		//imageView.setBackgroundResource(R.drawable.shape_image_dropshadow);
		byte[] fullSizeBytes = image.getFullSizeBytes();
		imageView.setImageBitmap(BitmapFactory.decodeByteArray(fullSizeBytes, 0, fullSizeBytes.length));
		imageView.getLayoutParams().height = imageView.getMeasuredWidth();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_bilde_detalj, null);

		TextView tvFotograf = (TextView) v.findViewById(R.id.bilde_detalj_fotograf);
		tvFotograf.setText(mBilde.getFotograf());

		TextView tvBeskrivelse = (TextView) v.findViewById(R.id.bilde_detalj_beskrivelse);
		tvBeskrivelse.setText(mBilde.getBeskrivelse());

		TextView tvNumLikes = (TextView) v.findViewById(R.id.bilde_detalj_num_likes);
		tvNumLikes.setText(String.valueOf(mBilde.getNumLikes()));

		ImageView ivBilde = (ImageView) v.findViewById(R.id.bilde_detalj_image);
		mImageDownloader.queueImage(ivBilde, mBilde, ImageDownloader.ImageType.FULLSIZE);

		Dialog d = new AlertDialog.Builder(getActivity())
			.setView(v)
			.setPositiveButton(android.R.string.ok, null)
			.create();

		d.setCanceledOnTouchOutside(true);
		return d;
	}
}
