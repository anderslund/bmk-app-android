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

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.lunders.client.android.bmk.R;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.model.nyheter.Nyhetskilde;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.LocalStorageHelper;
import org.lunders.client.android.bmk.services.impl.ServiceHelper;
import org.lunders.client.android.bmk.services.impl.nyhet.TwitterNyhetServiceImpl;
import org.lunders.client.android.bmk.util.DisplayUtil;
import org.lunders.client.android.bmk.util.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class TwitterNyhetslisteHelper implements Runnable {

	private Handler                    mResponseHandler;
	private Context                    mContext;
	private NyhetService.NyhetListener mListener;
	private Collection<Nyhet>  mNyheter;
	private NetworkUtils               mNetworkUtils;
	private LocalStorageHelper mLocalStorageHelper;

	public static final  String TWITTER_ENCODING   = "UTF-8";
	private static final String TWITTER_SEARCH_URL =
		"https://twitter.com/search?q=%23allesnakkerkorps+OR+from%3Aborgemusikken&count=20";


	private static final String TAG = TwitterNyhetslisteHelper.class.getSimpleName();


	public TwitterNyhetslisteHelper(Context context, NyhetService.NyhetListener listener) {
		mContext = context;
		mListener = listener;
		mNetworkUtils = NetworkUtils.getInstance(context);
		mLocalStorageHelper = LocalStorageHelper.getInstance(context);
		mResponseHandler = new Handler(Looper.getMainLooper());
	}

	@Override
	public void run() {

		//Fyrer av henting av mNyheter fra Twitter i bakgrunnen, hvis vi har nettverk
		if (mNetworkUtils.isNetworkAvailable()) {
			mNyheter = doHentNyheter();
			mLocalStorageHelper.saveToStorage(TwitterNyhetServiceImpl.TWITTER_NYHET_CACHE, mNyheter);
		}

		//Her leverer vi resultatet fra bakgrunnsjobben til UI-tråden.
		mResponseHandler.post(
			new Runnable() {
				@Override
				public void run() {
					mListener.onNyheterHentet(mNyheter);
				}
			}
		);
	}

	private Collection<Nyhet> doHentNyheter() {
		Log.i(TAG, "Henter mNyheter fra Twitter...");

		String searchResultHtml;
		try {
			searchResultHtml = new String(ServiceHelper.hentRaadata(TWITTER_SEARCH_URL), TWITTER_ENCODING);
		}
		catch (IOException e) {
			DisplayUtil.showToast((Activity) mContext, R.string.twitter_nyheter_feil, Toast.LENGTH_LONG);
			Log.e(TAG, "Klarte ikke å hente mNyheter fra Twitter", e);
			return null;
		}

		Collection<Nyhet> nyheter = new ArrayList<>();

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

		Log.i(TAG, "Har hentet mNyheter fra Twitter.");
		return nyheter;
	}
}
