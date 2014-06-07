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
import android.util.Log;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.ServiceHelper;
import org.lunders.client.android.bmk.util.NetworkUtils;
import org.lunders.client.android.bmk.util.StringUtil;

import java.io.IOException;

import static org.lunders.client.android.bmk.services.impl.nyhet.helpers.BmkNyhetListeHelper.BMK_ENCODING;

public class BmkNyhetDetaljHelper implements Runnable {

	private final Nyhet                            mNyhet;
	private final NyhetService.NyhetDetaljListener mListener;
	private final Handler                          mResponseHandler;
	private final NetworkUtils                     mNetworkUtils;

	public static final String BMK_HTML_ARTICLE_HEADLINE = "class=\"ArtTemp_Title\"";

	private static final String TAG = BmkNyhetDetaljHelper.class.getSimpleName();


	public BmkNyhetDetaljHelper(Context c, Nyhet n, NyhetService.NyhetDetaljListener l) {
		mNyhet = n;
		mListener = l;
		mNetworkUtils = NetworkUtils.getInstance(c);
		mResponseHandler = new Handler(Looper.getMainLooper());
	}

	public void run() {

		if (mNyhet.getFullStory() == null) {
			doHentNyhet();
		}

		//Sender melding til mListener om at nyheten er hentet og klar for visning.
		//En annen tråd (GUI-tråden) vil eksekvere denne koden.
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

		Log.i(TAG, "Henter full story fra BMK");
		String nyhetssideHtml;
		try {
			nyhetssideHtml = new String(ServiceHelper.hentRaadata(mNyhet.getFullStoryURL()), BMK_ENCODING);
		}
		catch (IOException e) {
			//Lager ikke toast her, for da ville det ha sprutet toasts når vi viser lista
			Log.e(TAG, "Klarte ikke å hente nyhet fra BMK", e);
			return;
		}

		int overskriftStartIndex = nyhetssideHtml.indexOf(BMK_HTML_ARTICLE_HEADLINE);

		String story = null;
		int storyStartIndex = nyhetssideHtml.indexOf("<tr>", overskriftStartIndex);
		int tableEndIndex = nyhetssideHtml.indexOf("</table>", storyStartIndex);
		while (tableEndIndex > storyStartIndex) {
			storyStartIndex = nyhetssideHtml.indexOf(">", storyStartIndex + "<tr>".length()) + 1;
			int storyEndIndex = nyhetssideHtml.indexOf("</td>", storyStartIndex);

			String potensiellStory = nyhetssideHtml.substring(storyStartIndex, storyEndIndex);
			if (!StringUtil.isBlank(potensiellStory) && !potensiellStory.trim().startsWith("<a href")) {
				story = StringUtil.cleanHtml(potensiellStory);
			}
			storyStartIndex = nyhetssideHtml.indexOf("<tr>", storyEndIndex);
		}
		mNyhet.setFullStory(story);
		Log.i(TAG, "Full story hentet fra BMK");
	}
}
