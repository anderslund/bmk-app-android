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
import org.lunders.client.android.bmk.model.bilde.Bilde;
import org.lunders.client.android.bmk.services.impl.ServiceHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImageDownloader<Token> extends HandlerThread {

	private Handler                 mDownloadRequestHandler;
	private Handler                 mDownloadResponseHandler;
	private DownloadListener<Token> mResponseListener;

	private Map<Token, Bilde> mRequestMap = Collections.synchronizedMap(new HashMap<Token, Bilde>());

	public static enum ImageType {THUMBNAIL, FULLSIZE}

	private static final int    MSG_TYPE_PICTURE_DOWNLOAD = 0;
	private static final String TAG                       = ImageDownloader.class.getSimpleName();


	public ImageDownloader() {
		super(TAG);
		this.mDownloadResponseHandler = new Handler();
	}


	public void queueImage(Token t, Bilde b, ImageType type) {
		mRequestMap.put(t, b);
		mDownloadRequestHandler.obtainMessage(MSG_TYPE_PICTURE_DOWNLOAD, type.ordinal(), -1, t).sendToTarget();
	}


	@Override
	protected void onLooperPrepared() {
		mDownloadRequestHandler = new Handler() {
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
		final Bilde b = mRequestMap.get(token);
		if (b == null) {
			return;
		}

		final String url = (imageType == ImageType.THUMBNAIL ? b.getThumbnailUrl() : b.getFullSizeUrl());

		try {
			final byte[] bitmapBytes = ServiceHelper.hentRaadata(url);
			if (imageType == ImageType.THUMBNAIL) {
				b.setThumbnailBytes(bitmapBytes);
			}
			else {
				b.setFullSizeBytes(bitmapBytes);
			}

			//Sier fra til lytteren (på en annen tråd) om at nå er thumbnailen ferdig nedlastet!
			//Vi bruker post til å sende en melding til den andre køens "innboks".
			mDownloadResponseHandler.post(
				new Runnable() {
					@Override
					public void run() {
						if (mRequestMap.get(token) == null) {
							return;
						}
						if (imageType == ImageType.THUMBNAIL && mRequestMap.get(token).getThumbnailUrl() != url ||
							imageType == ImageType.FULLSIZE && mRequestMap.get(token).getFullSizeUrl() != url) {
							return;
						}
						mRequestMap.remove(token);
						mResponseListener.onImageDownloaded(token, b);
					}
				}
			);
		}
		catch (IOException e) {
			e.printStackTrace(); //TODO
		}
	}

	public void clearQueue() {
		mDownloadRequestHandler.removeMessages(MSG_TYPE_PICTURE_DOWNLOAD);
		mRequestMap.clear();
	}

	public void setDownloadListener(DownloadListener<Token> responseListener) {
		this.mResponseListener = responseListener;
	}
}
