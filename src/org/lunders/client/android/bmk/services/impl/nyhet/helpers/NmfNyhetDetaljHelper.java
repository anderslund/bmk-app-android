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
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.ServiceHelper;
import org.lunders.client.android.bmk.util.NetworkUtils;

import java.io.IOException;

public class NmfNyhetDetaljHelper implements Runnable {

	private Nyhet                            mNyhet;
	private NyhetService.NyhetDetaljListener mListener;
	private Handler                          mResponseHandler;
	private NetworkUtils                     mNetworkUtils;

	private static final String NMF_HTML_ARTICLE_HEADLINE = "div class=\"article-content\"";

	private static final String TAG = NmfNyhetDetaljHelper.class.getSimpleName();


	public NmfNyhetDetaljHelper(Context c, Nyhet n, NyhetService.NyhetDetaljListener l) {
		mNyhet = n;
		mListener = l;
		mNetworkUtils = NetworkUtils.getInstance(c);
		mResponseHandler = new Handler(Looper.getMainLooper());
	}

	public void run() {

		if (mNyhet.getFullStory() == null) {
			doHentNyhet();
		}

		mResponseHandler.post(
			new Runnable() {
				@Override
				public void run() {
					mListener.onNyhetHentet(mNyhet);
				}
			}
		);
	}

	private void doHentNyhet() {
		if (!mNetworkUtils.isNetworkAvailable()) {
			//TODO Bør vel gi et signal til brukeren om at vi ikke fikk hentet historien!
			return;
		}

		String nyhetssideHtml;
		try {
			nyhetssideHtml = new String(ServiceHelper.hentRaadata(mNyhet.getFullStoryURL()));
		}
		catch (IOException e) {
			//Lager ikke toast her, for da ville det ha sprutet toasts når vi viser lista
			Log.e(TAG, "Klarte ikke å hente nyhet fra NMF", e);
			return;
		}

		int innholdStartIndex = nyhetssideHtml.indexOf(NMF_HTML_ARTICLE_HEADLINE);
		int innholdEndIndex = nyhetssideHtml.indexOf("</div>", innholdStartIndex);

		String innholdHtml = nyhetssideHtml.substring(
			innholdStartIndex + NMF_HTML_ARTICLE_HEADLINE.length() + 1,
			innholdEndIndex);

		String s = Jsoup.clean(innholdHtml, Whitelist.basic());
		mNyhet.setFullStory(Html.fromHtml(s));

	}
}
