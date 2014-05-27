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

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.model.nyheter.Nyhetskilde;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.AbstractServiceImpl;
import org.lunders.client.android.bmk.util.DateUtil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

public class NmfNyhetServiceImpl extends AbstractServiceImpl implements NyhetService {

	private final NyhetListener nyhetlisteListener;
	private Collection<Nyhet> currentNyheter;

	private static final String NMF_WEB_ROOT = "http://burns.idium.net/musikkorps.no/no/nyheter/?template=rssfeed";

	private static final String TAG = NmfNyhetServiceImpl.class.getSimpleName();
	private static final String NMF_HTML_ARTICLE_HEADLINE = "div class=\"article-content\"";

	public NmfNyhetServiceImpl(NyhetListener nyhetlisteListener) {
		this.nyhetlisteListener = nyhetlisteListener;
		currentNyheter = new TreeSet<>();
	}

	@Override
	public Collection<Nyhet> hentNyheter() {
		new NmfNyhetListeFetcher().execute();
		return currentNyheter;
	}

	@Override
	public void hentNyhet(Nyhet nyhet, NyhetDetaljListener listener) {
		new NmfNyhetDetaljFetcher(nyhet, listener).execute();
	}



	private class NmfNyhetListeFetcher extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void aVoid) {
			nyhetlisteListener.onNyheterHentet(currentNyheter);
		}

		@Override
		protected Void doInBackground(Void... params) {
			Log.i(TAG, "Henter nyheter fra BMK...");
			Collection<Nyhet> nyheter = new ArrayList<>();

			try {
				String rssXML = new String(hentRaadata(NMF_WEB_ROOT), "UTF-8");
				final XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
				xmlPullParser.setInput(new StringReader(rssXML));

				while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
					String elementName = xmlPullParser.getName();
					if ("item".equals(elementName) && xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
						Nyhet n = extractOneNyhet(xmlPullParser);
						nyheter.add(n);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				currentNyheter = nyheter;
				Log.i(TAG, "Nyheter hentet fra NMF");
			}
			return null;
		}

		private Nyhet extractOneNyhet(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {

			Nyhet n = new Nyhet();
			n.setKilde(Nyhetskilde.NMF);

			String text = null;

			newsItem:
			while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {

				switch (xmlPullParser.getEventType()) {

					case XmlPullParser.TEXT:
						text = xmlPullParser.getText();
						break;

					case (XmlPullParser.END_TAG):
						if ("title".equals(xmlPullParser.getName())) {
							n.setOverskrift(text);
						}
						else if ("pubDate".equals(xmlPullParser.getName())) {
							n.setDato(DateUtil.getNmfDate(text));
						}
						else if ("link".equals(xmlPullParser.getName())) {
							n.setFullStoryURL(text);
						}
						else if ("description".equals(xmlPullParser.getName())) {
							n.setIngress(text);
						}

						else if ("item".equals(xmlPullParser.getName())) {
							break newsItem;
						}
				}
			}
			return n;
		}
	}


	private class NmfNyhetDetaljFetcher extends AsyncTask<Void, Void, Void> {

		private final Nyhet nyhet;
		private final NyhetDetaljListener listener;

		public NmfNyhetDetaljFetcher(Nyhet n, NyhetDetaljListener l) {
			this.nyhet = n;
			this.listener = l;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			listener.onNyhetHentet(nyhet);
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				String nyhetssideHtml = new String(hentRaadata(nyhet.getFullStoryURL()));
				int innholdStartIndex = nyhetssideHtml.indexOf(NmfNyhetServiceImpl.NMF_HTML_ARTICLE_HEADLINE);
				int innholdEndIndex = nyhetssideHtml.indexOf("</div>", innholdStartIndex);

				String innholdHtml = nyhetssideHtml.substring(innholdStartIndex + NMF_HTML_ARTICLE_HEADLINE.length() + 1, innholdEndIndex);
				final Spanned story = Html.fromHtml(innholdHtml, configureImageGetter(), null);
				nyhet.setFullStory(story);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				Log.i(TAG, "Full story hentet fra NMF");
			}
			return null;
		}
	}

	public Html.ImageGetter configureImageGetter() {
		Html.ImageGetter imageGetter = new Html.ImageGetter() {
			public Drawable getDrawable(String source) {
				try {
					if (source.startsWith("/")) {
						source = "http:/" + source;
					}
					Drawable drawable = Drawable.createFromStream(new URL(source).openStream(), "src name");
					//TODO: Bredden på tekstfeltet den ligger i
					drawable.setBounds(0, 0, drawable.getIntrinsicHeight(), drawable.getIntrinsicHeight());
					return drawable;
				}
				catch (IOException exception) {
					Log.v("IOException", exception.getMessage());
					return null;
				}
			}
		};
		return imageGetter;
	}
}
