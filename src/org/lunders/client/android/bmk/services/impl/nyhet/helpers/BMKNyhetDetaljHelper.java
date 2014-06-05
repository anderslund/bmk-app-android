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
import android.util.Log;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.ServiceHelper;
import org.lunders.client.android.bmk.util.StringUtil;

import java.io.IOException;

import static org.lunders.client.android.bmk.services.impl.nyhet.helpers.BmkNyhetListeHelper.BMK_ENCODING;

//TODO: Mulig det kan gjøres noen triks med Html-klassen i stedet for lavnivå textparsing...
public class BmkNyhetDetaljHelper implements Runnable {

	private final Nyhet nyhet;
	private final NyhetService.NyhetDetaljListener listener;

	public static final String BMK_HTML_ARTICLE_HEADLINE = "class=\"ArtTemp_Title\"";

	private static final String TAG = BmkNyhetDetaljHelper.class.getSimpleName();
	private final Handler responseHandler;

	public BmkNyhetDetaljHelper(Nyhet n, NyhetService.NyhetDetaljListener l) {
		this.nyhet = n;
		this.listener = l;
		responseHandler = new Handler(Looper.getMainLooper());
	}

	public void run() {
		Log.i(TAG, "Henter full story fra BMK");
		if ( nyhet.getFullStory() == null) {
			doHentNyhet();
			Log.i(TAG, "Full story hentet fra BMK");
		}
		else {
			Log.i(TAG, "Story allerede hentet");
		}

		//Sender melding til listener om at nyheten er hentet og klar for visning.
		//En annen tråd (GUI-tråden) vil eksekvere denne koden.
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
			String nyhetssideHtml = new String(ServiceHelper.hentRaadata(nyhet.getFullStoryURL()), BMK_ENCODING);
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
			nyhet.setFullStory(story);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
