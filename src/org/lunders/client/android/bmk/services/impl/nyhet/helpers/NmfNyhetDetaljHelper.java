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

import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.ServiceHelper;

import java.io.IOException;

public class NmfNyhetDetaljHelper implements Runnable {

	private final Nyhet nyhet;
	private final NyhetService.NyhetDetaljListener listener;

	private static final String NMF_HTML_ARTICLE_HEADLINE = "div class=\"article-content\"";

	private static final String TAG = NmfNyhetDetaljHelper.class.getSimpleName();
	private final Handler responseHandler;

	public NmfNyhetDetaljHelper(Nyhet n, NyhetService.NyhetDetaljListener l) {
		this.nyhet = n;
		this.listener = l;
		responseHandler = new Handler(Looper.getMainLooper());
	}

	public void run() {
		Log.i(TAG, "Henter full story fra NMF");
		if ( nyhet.getFullStory() == null) {
			doHentNyhet();
			Log.i(TAG, "Full story hentet fra NMF");
		}
		else {
			Log.i(TAG, "Story allerede hentet");
		}

		responseHandler.post(
			new Runnable() {
				@Override
				public void run() {
					listener.onNyhetHentet(nyhet);
				}
			}
		);
	}

	private void doHentNyhet() {
		try {
			long t0 = System.currentTimeMillis();
			String nyhetssideHtml = new String(ServiceHelper.hentRaadata(nyhet.getFullStoryURL()));
			long t1 = System.currentTimeMillis();

			int innholdStartIndex = nyhetssideHtml.indexOf(NMF_HTML_ARTICLE_HEADLINE);
			int innholdEndIndex = nyhetssideHtml.indexOf("</div>", innholdStartIndex);

			String innholdHtml = nyhetssideHtml.substring(
				innholdStartIndex + NMF_HTML_ARTICLE_HEADLINE.length() + 1,
				innholdEndIndex);

			String s = Jsoup.clean(innholdHtml, Whitelist.basic());
			Spanned story = Html.fromHtml(s);
			long t2 = System.currentTimeMillis();
			nyhet.setFullStory(story);
			long t3 = System.currentTimeMillis();
			Log.i(TAG, "Hentet full story fra NMF på " + (t1 - t0) + "ms. HTML-parsing tok " + (t2 - t1) + "ms");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
