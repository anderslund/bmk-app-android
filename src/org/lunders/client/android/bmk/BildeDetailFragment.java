package org.lunders.client.android.bmk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.lunders.client.android.bmk.model.bilde.Bilde;
import org.lunders.client.android.bmk.services.BildeService;
import org.lunders.client.android.bmk.services.impl.bilde.ImageDownloader;

import java.io.IOException;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class BildeDetailFragment extends DialogFragment {

	private Bilde bilde;

	private ImageDownloader<ImageView> imageDownloader;
	private Handler imageResponseHandler;
	private BildeService bildeService;

	public static BildeDetailFragment newInstance(BildeService bildeService, Bilde b) {
		BildeDetailFragment fragment = new BildeDetailFragment(bildeService, b);
		return fragment;
	}

	public BildeDetailFragment() {

	}

	public BildeDetailFragment(BildeService bildeService, Bilde bilde) {
		this();
		this.bilde = bilde;
		this.bildeService = bildeService;

		imageDownloader = new ImageDownloader(bildeService, imageResponseHandler = new Handler());
		imageDownloader.setResponseListener(
			new ImageDownloader.Listener<ImageView>() {
				@Override
				public void onImageDownloaded(ImageView imageView, Bitmap image) {
					imageView.setImageBitmap(image);
					imageView.getLayoutParams().height = imageView.getMeasuredWidth();
					imageView.setBackgroundResource(R.drawable.shape_image_dropshadow);
				}
			}
		);
		imageDownloader.start();
		imageDownloader.getLooper();
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_bilde_detalj, null);

		TextView tvFotograf = (TextView) v.findViewById(R.id.bilde_detalj_fotograf);
		tvFotograf.setText(bilde.getFotograf());

		TextView tvBeskrivelse = (TextView) v.findViewById(R.id.bilde_detalj_beskrivelse);
		tvBeskrivelse.setText(bilde.getBeskrivelse());

		TextView tvNumLikes = (TextView) v.findViewById(R.id.bilde_detalj_num_likes);
		tvNumLikes.setText(String.valueOf(bilde.getNumLikes()));

		ImageView ivBilde = (ImageView) v.findViewById(R.id.bilde_detalj_image);
		imageDownloader.queueImage(ivBilde, bilde.getFullSizeUrl());

		Dialog d = new AlertDialog.Builder(getActivity())
			.setView(v)
				//.setTitle(bilde.getHeader())
			.setPositiveButton(android.R.string.ok, null)
			.create();

		d.setCanceledOnTouchOutside(true);
		return d;
	}
}
