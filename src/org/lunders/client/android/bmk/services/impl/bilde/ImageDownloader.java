package org.lunders.client.android.bmk.services.impl.bilde;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import org.lunders.client.android.bmk.services.BildeService;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class ImageDownloader<Token> extends HandlerThread {
	private static final String TAG = ImageDownloader.class.getSimpleName();
	private static final int MSG_TYPE_PICTURE_DOWNLOAD = 0;

	private BildeService bildeService;

	private Handler downloadRequestHandler, downloadResponseHandler;
	private Listener<Token> responseListener;

	private Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());


	public ImageDownloader(BildeService bildeService, Handler downloadResponseHandler) {
		super(TAG);
		this.bildeService = bildeService;
		this.downloadResponseHandler = downloadResponseHandler;
	}


	public void queueImage(Token t, String url) {
		Log.i(TAG, "Queued an URL: " + url + ". Token: " + t);
		requestMap.put(t, url);
		downloadRequestHandler.obtainMessage(MSG_TYPE_PICTURE_DOWNLOAD, t).sendToTarget();
	}


	@Override
	protected void onLooperPrepared() {
		downloadRequestHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == MSG_TYPE_PICTURE_DOWNLOAD) {
					Token token = (Token) msg.obj;
					handleRequest(token);
				}
			}
		};
	}

	private void handleRequest(final Token token) {
		final String url = requestMap.get(token);
		if (url == null) {
			return;
		}

		try {
			final byte[] bitmapBytes = bildeService.hentRaadata(url);
			final Bitmap bm = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
			Log.i(TAG, "Image downloaded");

			//Sier fra til lytteren (på en annen tråd) om at nå er thumbnailen ferdig nedlastet!
			//Vi bruker post til å sende en melding til den andre køens "innboks".
			downloadResponseHandler.post(
				new Runnable() {
					@Override
					public void run() {
						if (requestMap.get(token) != url) {
							return;
						}
						requestMap.remove(token);
						responseListener.onImageDownloaded(token, bm);
					}
				}
			);
		}
		catch (IOException e) {
			e.printStackTrace(); //TODO
		}
	}

	public void clearQueue() {
		downloadRequestHandler.removeMessages(MSG_TYPE_PICTURE_DOWNLOAD);
		requestMap.clear();
	}

	public interface Listener<Token> {
		void onImageDownloaded(Token token, Bitmap image);
	}

	public void setResponseListener(Listener<Token> responseListener) {
		this.responseListener = responseListener;
	}
}
