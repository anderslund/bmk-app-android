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

package org.lunders.client.android.bmk.services.impl.bilde;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import org.lunders.client.android.bmk.model.bilde.Bilde;
import org.lunders.client.android.bmk.services.BildeService;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImageDownloader<Token> extends HandlerThread {

	private BildeService bildeService;

	private Handler downloadRequestHandler, downloadResponseHandler;
	private DownloadListener<Token> responseListener;

	private Map<Token, Bilde> requestMap = Collections.synchronizedMap(new HashMap<Token, Bilde>());

	private static final String TAG = ImageDownloader.class.getSimpleName();

	private static final int MSG_TYPE_PICTURE_DOWNLOAD = 0;

	public static enum ImageType {THUMBNAIL, FULLSIZE}

	public ImageDownloader(BildeService bildeService, Handler downloadResponseHandler) {
		super(TAG);
		this.bildeService = bildeService;
		this.downloadResponseHandler = downloadResponseHandler;
	}


	public void queueImage(Token t, Bilde b, ImageType type) {
		Log.i(TAG, "Queued an URL: " + b.getThumbnailUrl() + ". Token: " + t);
		requestMap.put(t, b);
		downloadRequestHandler.obtainMessage(MSG_TYPE_PICTURE_DOWNLOAD, type.ordinal(), -1, t).sendToTarget();
	}


	@Override
	protected void onLooperPrepared() {
		downloadRequestHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == MSG_TYPE_PICTURE_DOWNLOAD) {
					Token token = (Token) msg.obj;
					ImageType type = ImageType.values()[msg.arg1];
					handleRequest(token, type);
				}
			}
		};
	}

	private void handleRequest(final Token token, final ImageType imageType) {
		final Bilde b = requestMap.get(token);
		if (b == null) {
			return;
		}

		final String url = (imageType == ImageType.THUMBNAIL ? b.getThumbnailUrl() : b.getFullSizeUrl());

		try {
			final byte[] bitmapBytes = bildeService.hentRaadata(url);
			if (imageType == ImageType.THUMBNAIL) {
				b.setThumbnailBytes(bitmapBytes);
			}
			else {
				b.setFullSizeBytes(bitmapBytes);
			}
			Log.i(TAG, "Image downloaded");

			//Sier fra til lytteren (på en annen tråd) om at nå er thumbnailen ferdig nedlastet!
			//Vi bruker post til å sende en melding til den andre køens "innboks".
			downloadResponseHandler.post(
				new Runnable() {
					@Override
					public void run() {
						if ( requestMap.get(token) == null) {
							return;
						}
						if (imageType == ImageType.THUMBNAIL && requestMap.get(token).getThumbnailUrl() != url ||
							imageType == ImageType.FULLSIZE && requestMap.get(token).getFullSizeUrl() != url) {
							return;
						}
						requestMap.remove(token);
						responseListener.onImageDownloaded(token, b);
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

	public void setDownloadListener(DownloadListener<Token> responseListener) {
		this.responseListener = responseListener;
	}
}
