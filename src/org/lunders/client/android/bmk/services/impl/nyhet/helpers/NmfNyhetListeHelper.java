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
import org.lunders.client.android.bmk.model.nyheter.Nyhetskilde;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.ServiceHelper;
import org.lunders.client.android.bmk.util.DateUtil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;

public class NmfNyhetListeHelper implements Runnable {

	private Handler responseHandler;
	private NyhetService.NyhetListener listener;
	private Collection<Nyhet> nyheter;

	private static final String NMF_WEB_ROOT = "http://burns.idium.net/musikkorps.no/no/nyheter/?template=rssfeed";

	private static final String TAG = NmfNyhetListeHelper.class.getSimpleName();
	public static final String NMF_ENCODING = "UTF-8";

	public NmfNyhetListeHelper(NyhetService.NyhetListener listener) {
		this.listener = listener;
		responseHandler = new Handler(Looper.getMainLooper());
	}


	@Override
	public void run() {
		Log.i(TAG, "Henter nyheter fra NMF...");
		doHentNyheter();
		Log.i(TAG, "Nyheter hentet fra NMF");

		responseHandler.post(
			new Runnable() {
				@Override
				public void run() {
					listener.onNyheterHentet(nyheter);
				}
			}
		);
	}

	private void doHentNyheter() {
		nyheter = new ArrayList<>();

		try {
			String rssXML = new String(ServiceHelper.hentRaadata(NMF_WEB_ROOT), NMF_ENCODING);

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
