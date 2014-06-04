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

package org.lunders.client.android.bmk.services.impl.nyhet.helpers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.model.nyheter.Nyhetskilde;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.ServiceHelper;
import org.lunders.client.android.bmk.services.impl.nyhet.TwitterNyhetServiceImpl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class TwitterNyhetslisteHelper implements Runnable {

	private Handler responseHandler;
	private Context mContext;
	private NyhetService.NyhetListener listener;
	private Collection<Nyhet> nyheter;

	public static final String TWITTER_ENCODING = "UTF-8";
	private static final String TWITTER_SEARCH_URL =
		"https://twitter.com/search?q=%23allesnakkerkorps+OR+from%3Aborgemusikken&count=20";

	private static final String TAG = TwitterNyhetslisteHelper.class.getSimpleName();


	public TwitterNyhetslisteHelper(Context context, NyhetService.NyhetListener listener) {
		mContext = context;
		this.listener = listener;
		responseHandler = new Handler(Looper.getMainLooper());
	}

	@Override
	public void run() {
		Log.i(TAG, "Henter nyheter fra Twitter...");

		//Fyrer av henting av nyheter fra Twitter i bakgrunnen
		nyheter = doHentNyheter();
		storeNyheterToStorage(nyheter);

		Log.i(TAG, "Har hentet nyheter fra Twitter...");

		//Her leverer vi resultatet fra bakgrunnsjobben til UI-tråden.
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
			String searchResultHtml = new String(ServiceHelper.hentRaadata(TWITTER_SEARCH_URL), TWITTER_ENCODING);

			int tweetStartIndex = searchResultHtml.indexOf("<div class=\"tweet ");
			while (tweetStartIndex > 0) {
				int tweetEndIndex = searchResultHtml.indexOf("<div class=\"stream-item-footer\">", tweetStartIndex);

				int authorStartIndex = searchResultHtml.indexOf("data-name=\"", tweetStartIndex) + "data-name=\"".length();
				int authorEndIndex = searchResultHtml.indexOf('"', authorStartIndex);

				int contentStartIndex = searchResultHtml.indexOf('>', searchResultHtml.indexOf("<p class=\"js-tweet-text", authorEndIndex)) + 1;
				int contentEndIndex = searchResultHtml.indexOf("</p>", contentStartIndex);

				int timestampStartIndex = searchResultHtml.indexOf("data-time-ms=\"", authorEndIndex) + "data-time-ms=\"".length();
				int timestampEndIndex = searchResultHtml.indexOf('"', timestampStartIndex);

				String author = Html.fromHtml(searchResultHtml.substring(authorStartIndex, authorEndIndex)).toString();
				String substring = searchResultHtml.substring(contentStartIndex, contentEndIndex);
				String sContent = Jsoup.clean(substring, Whitelist.basic());
				sContent = sContent.replaceAll("&nbsp;", "");
				sContent = sContent.replaceAll("…", "");

				Spanned content = Html.fromHtml(sContent);

				String sTimestamp = searchResultHtml.substring(timestampStartIndex, timestampEndIndex);

				Nyhet nyhet = new Nyhet(author, content, Nyhetskilde.Twitter);
				nyhet.setFullStory(content.toString());
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

	private void storeNyheterToStorage(Collection<Nyhet> nyheter) {
		try {
			final FileOutputStream fos = mContext.openFileOutput(TwitterNyhetServiceImpl.TWITTER_NYHET_CACHE, Context.MODE_PRIVATE);
			final ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(nyheter);
			oos.flush();
			fos.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
