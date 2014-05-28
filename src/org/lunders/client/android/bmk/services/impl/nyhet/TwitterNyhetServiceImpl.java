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

package org.lunders.client.android.bmk.services.impl.nyhet;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.model.nyheter.Nyhetskilde;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.AbstractServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

public class TwitterNyhetServiceImpl extends AbstractServiceImpl implements NyhetService {

	private final NyhetListener nyhetlisteListener;
	private final Collection<Nyhet> currentNyheter;

	private static final String TWITTER_SEARCH_URL =
		"https://twitter.com/search?q=%23allesnakkerkorps+OR+from%3Aborgemusikken&count=20";

	private static final String TAG = TwitterNyhetServiceImpl.class.getSimpleName();

	public TwitterNyhetServiceImpl(NyhetListener nyhetlisteListener) {
		this.nyhetlisteListener = nyhetlisteListener;
		currentNyheter = new TreeSet<>();
	}


	@Override
	public Collection<Nyhet> hentNyheter() {
		new TwitterNyhetslisteFetcher(nyhetlisteListener).start();
		return currentNyheter;
	}


	@Override
	public void hentNyhet(final Nyhet nyhet, final NyhetDetaljListener h) {
		final Handler responseHandler = new Handler(Looper.getMainLooper());

		//Dette må skje i en egen tråd selv om vi egentlig ikke gjør så mye mer enn å
		//returnere nyheten (selve "storyen" er den samme som ingressen). Årsaken er at
		//GUI-tråden må tilbake og opprette dialogen slik at den er klar til resultatet
		//kommer
		new HandlerThread("Twitter-detaljer"){
			@Override
			public void run() {
				responseHandler.post(
					new Runnable() {
						@Override
						public void run() {
							h.onNyhetHentet(nyhet);
						}
					});
			}
		}.start();
	}


	private class TwitterNyhetslisteFetcher extends HandlerThread {

		private Handler responseHandler;
		private NyhetListener listener;

		public TwitterNyhetslisteFetcher(NyhetListener listener) {
			super(TAG);
			this.listener = listener;
			responseHandler = new Handler(Looper.getMainLooper());
		}

		@Override
		public void run() {
			Log.i(TAG, "Henter nyheter fra Twitter...");
			final Collection<Nyhet> nyheter = doHentNyheter();
			Log.i(TAG, "Har hentet nyheter fra Twitter...");

			responseHandler.post(
				new Runnable() {
					@Override
					public void run() {
						listener.onNyheterHentet(nyheter);
					}
				}
			);

		}


		private Collection<Nyhet> doHentNyheter() {
			Collection<Nyhet> nyheter = new ArrayList<>();

			try {
				String searchResultHtml = new String(hentRaadata(TWITTER_SEARCH_URL), "UTF-8");

				int tweetStartIndex = searchResultHtml.indexOf("<div class=\"tweet ");
				while (tweetStartIndex > 0) {
					int tweetEndIndex = searchResultHtml.indexOf("<div class=\"stream-item-footer\">", tweetStartIndex);

					int authorStartIndex = searchResultHtml.indexOf("data-name=\"", tweetStartIndex) + "data-name=\"".length();
					int authorEndIndex = searchResultHtml.indexOf('"', authorStartIndex);

					int contentStartIndex = searchResultHtml.indexOf('>', searchResultHtml.indexOf("<p class=\"js-tweet-text", authorEndIndex)) + 1;
					int contentEndIndex = searchResultHtml.indexOf("</p>", contentStartIndex);

					int timestampStartIndex = searchResultHtml.indexOf("data-time-ms=\"", authorEndIndex) + "data-time-ms=\"".length();
					int timestampEndIndex = searchResultHtml.indexOf('"', timestampStartIndex);

					String author = searchResultHtml.substring(authorStartIndex, authorEndIndex);
					Spanned content = Html.fromHtml(searchResultHtml.substring(contentStartIndex, contentEndIndex));
					String sTimestamp = searchResultHtml.substring(timestampStartIndex, timestampEndIndex);

					Nyhet nyhet = new Nyhet(author, content, Nyhetskilde.Twitter);
					nyhet.setFullStory(content);
					try {
						long timestamp = Long.parseLong(sTimestamp);
						nyhet.setDato(new Date(timestamp));
					}
					catch (NumberFormatException e) {
						Log.w(TAG, "Klarte ikke å parse dato for Twitter-nyhet", e);
					}
					nyheter.add(nyhet);
					tweetStartIndex = searchResultHtml.indexOf("<div class=\"tweet ", tweetEndIndex + 1);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			return nyheter;
		}
	}
}
