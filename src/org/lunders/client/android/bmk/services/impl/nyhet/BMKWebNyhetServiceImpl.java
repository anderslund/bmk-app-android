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

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.model.nyheter.Nyhetskilde;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.AbstractServiceImpl;
import org.lunders.client.android.bmk.util.StringUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BMKWebNyhetServiceImpl extends AbstractServiceImpl implements NyhetService {

	private NyhetListener nyhetListener;

	private Collection<Nyhet> currentNyheter;

	private static final String TAG = BMKWebNyhetServiceImpl.class.getSimpleName();

	private static final String BMK_WEB_ROOT = "http://borgemusikken.no/";
	private static final String BMK_HTML_NEWS_HEADLINE = "class=\"ListTemp_Title\"";
	private static final String BMK_HTML_ARTICLE_HEADLINE = "class=\"ArtTemp_Title\"";
	private static final String BMK_ENCODING = "ISO-8859-1";
	private static final Pattern REGEX_DATO = Pattern.compile("[a-zA-Z]*(\\d{1,2}\\.\\s*[a-zA-Z]*).*");


	public BMKWebNyhetServiceImpl(NyhetListener nyhetlisteListener) {
		this.nyhetListener = nyhetlisteListener;
		currentNyheter = new TreeSet<>();
	}

	public Collection<Nyhet> hentNyheter() {
		//1. Hent cachede nyheter fra enhetens lager
		//2. Fyr av en jobb som henter oppdaterte nyheter fra BMK
		new BMKNyhetListeFetcher().execute();
		return currentNyheter;
	}

	public void hentNyhet(Nyhet n, NyhetDetaljListener listener) {
		new BMKNyhetDetaljFetcher(n, listener).execute();
	}


	//TODO: Mulig det kan gjøres noen triks med Html-klassen i stedet for lavnivå textparsing...
	private class BMKNyhetDetaljFetcher extends AsyncTask<Void, Void, Void> {

		private final Nyhet nyhet;
		private final NyhetDetaljListener listener;
		private Handler handler;

		public BMKNyhetDetaljFetcher(Nyhet n, NyhetDetaljListener l) {
			this.nyhet = n;
			this.listener = l;
			handler = new Handler(Looper.getMainLooper()) {
				@Override
				public void handleMessage(Message msg) {
					listener.onNyhetHentet(nyhet);
				}
			};
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			handler.sendEmptyMessage(0);
		}

		@Override
		protected Void doInBackground(Void... params) {

			// TODO: Gjøre noe med handlers her, slik at vi kan poppe dialogen og la selve teksten komme senere?
			try {
				String nyhetssideHtml = new String(hentRaadata(nyhet.getFullStoryURL()), BMK_ENCODING);
				int overskriftStartIndex = nyhetssideHtml.indexOf(BMKWebNyhetServiceImpl.BMK_HTML_ARTICLE_HEADLINE);

				String story = null;
				int storyStartIndex = nyhetssideHtml.indexOf("<tr>", overskriftStartIndex);
				int tableEndIndex = nyhetssideHtml.indexOf("</table>", storyStartIndex);
				while (tableEndIndex > storyStartIndex) {
					storyStartIndex = nyhetssideHtml.indexOf(">", storyStartIndex + "<tr>".length()) + 1;
					int storyEndIndex = nyhetssideHtml.indexOf("</td>", storyStartIndex);

					String potensiellStory = nyhetssideHtml.substring(storyStartIndex, storyEndIndex);
					if (!StringUtil.isBlank(potensiellStory) && !potensiellStory.trim().startsWith("<a href")) {
						story = clean(potensiellStory);
					}
					storyStartIndex = nyhetssideHtml.indexOf("<tr>", storyEndIndex);
				}
				nyhet.setFullStory(story);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				Log.i(TAG, "Full story hentet fra BMK");
			}
			return null;
		}
	}


	private class BMKNyhetListeFetcher extends AsyncTask<Void, Void, Void> {

		public static final String BMK_NEWS_PAGE = "default.asp?fid=1001";

		@Override
		protected void onPostExecute(Void aVoid) {
			nyhetListener.onNyheterHentet(currentNyheter);
		}

		@Override
		protected Void doInBackground(Void... params) {
			Log.i(TAG, "Henter nyheter fra BMK...");
			Collection<Nyhet> nyheter = new ArrayList<>();

			try {
				String hovedsideHtml = new String(hentRaadata(BMKWebNyhetServiceImpl.BMK_WEB_ROOT + BMK_NEWS_PAGE), BMK_ENCODING);

				int overskriftStartIndex = hovedsideHtml.indexOf(BMKWebNyhetServiceImpl.BMK_HTML_NEWS_HEADLINE);
				while (overskriftStartIndex >= 0) {
					//Henter overskrift
					overskriftStartIndex = hovedsideHtml.indexOf('>', overskriftStartIndex) + 1;
					int overskriftEndIndex = hovedsideHtml.indexOf("</td>", overskriftStartIndex);
					String overskrift = clean(hovedsideHtml.substring(overskriftStartIndex, overskriftEndIndex));

					//Henter ingress - looper gjennom evt tomme tabell-celler etter
					//overskriften, og tar den første ikke-tomme cellen
					String ingress = null;
					int ingressStartIndex = hovedsideHtml.indexOf("<tr>", overskriftEndIndex);
					int tableEndIndex = hovedsideHtml.indexOf("</table>", ingressStartIndex);
					while (ingress == null && tableEndIndex > ingressStartIndex) {
						ingressStartIndex = hovedsideHtml.indexOf(">", ingressStartIndex + "<tr>".length()) + 1;
						int ingressEndIndex = hovedsideHtml.indexOf("</td>", ingressStartIndex);

						String potensiellIngress = hovedsideHtml.substring(ingressStartIndex, ingressEndIndex);
						if (!StringUtil.isBlank(potensiellIngress)) {
							ingress = clean(potensiellIngress);
							break;
						}
						ingressStartIndex = hovedsideHtml.indexOf("<tr>", ingressEndIndex);
					}

					Nyhet n = new Nyhet(overskrift, ingress, Nyhetskilde.BMK);
					n.setDato(BMKWebNyhetServiceImpl.finnDato(overskrift));
					nyheter.add(n);

					//Finner link til hoved-storyen
					String lesMerLink = null;
					int lesMerStartIndex = hovedsideHtml.indexOf("<a href=\"", ingressStartIndex);
					if (lesMerStartIndex >= 0) {
						lesMerStartIndex += "<a href=\"".length();
						int lesMerEndIndex = hovedsideHtml.indexOf('\"', lesMerStartIndex);
						lesMerLink = (hovedsideHtml.substring(lesMerStartIndex, lesMerEndIndex));
						n.setFullStoryURL(BMKWebNyhetServiceImpl.BMK_WEB_ROOT + lesMerLink);
					}

					overskriftStartIndex = hovedsideHtml.indexOf(BMKWebNyhetServiceImpl.BMK_HTML_NEWS_HEADLINE, ingressStartIndex + 1);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				currentNyheter = nyheter;
				Log.i(TAG, "Nyheter hentet fra BMK");
			}
			return null;
		}
	}

	/**
	 * Forsøker å finne datoen i overskriften. Forutsetter at datoen
	 * har formen dd. MMM, f.eks 1. mai eller 01. mai
	 *
	 * @param overskrift
	 * @return
	 */
	private static Date finnDato(String overskrift) {
		if (StringUtil.isBlank(overskrift)) {
			return null;
		}

		//Pattern for å matche dato i overskriften
		Matcher matcher = REGEX_DATO.matcher(overskrift);
		if (matcher.matches()) {
			String match = matcher.group(1);
			match += " " + Calendar.getInstance().get(Calendar.YEAR);
			try {
				return new SimpleDateFormat("dd. MMM yyyy").parse(match);
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	/**
	 * Fjerner HTML-tagger fra s og trimmer den.
	 *
	 * @param s
	 * @return
	 */
	private String clean(String s) {
		if (StringUtil.isBlank(s)) {
			return "";
		}

		return Html.fromHtml(s).toString().trim();
	}

}
