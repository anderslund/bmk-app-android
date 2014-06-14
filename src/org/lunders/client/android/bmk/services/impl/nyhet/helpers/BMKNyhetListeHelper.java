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
import android.util.Log;
import android.widget.Toast;
import org.lunders.client.android.bmk.R;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.model.nyheter.Nyhetskilde;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.LocalStorageHelper;
import org.lunders.client.android.bmk.services.impl.ServiceHelper;
import org.lunders.client.android.bmk.services.impl.nyhet.BMKWebNyhetServiceImpl;
import org.lunders.client.android.bmk.util.DisplayUtil;
import org.lunders.client.android.bmk.util.NetworkUtils;
import org.lunders.client.android.bmk.util.StringUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BMKNyhetListeHelper implements Runnable {

	private       Handler                    mResponseHandler;
	private       NyhetService.NyhetListener mListener;
	private       Collection<Nyhet>          mNyheter;
	private       Context                    mContext;
	private       NetworkUtils               mNetworkUtils;
	private final LocalStorageHelper         mLocalStorageHelper;

	private static final String  BMK_NEWS_PAGE          = "default.asp?fid=1001";
	private static final Pattern REGEX_DATO             = Pattern.compile("[a-zA-Z]*(\\d{1,2}\\.\\s*[a-zA-Z]*).*");
	private static final String  BMK_WEB_ROOT           = "http://borgemusikken.no/";
	private static final String  BMK_HTML_NEWS_HEADLINE = "class=\"ListTemp_Title\"";
	static final         String  BMK_ENCODING           = "ISO-8859-1";

	private static final String TAG = BMKNyhetListeHelper.class.getSimpleName();


	public BMKNyhetListeHelper(Context context, NyhetService.NyhetListener listener) {
		mContext = context;
		mListener = listener;
		mNetworkUtils = NetworkUtils.getInstance(context);
		mLocalStorageHelper = LocalStorageHelper.getInstance(context);
		mResponseHandler = new Handler(Looper.getMainLooper());
	}


	public void run() {
		if (mNetworkUtils.isNetworkAvailable()) {
			doHentNyheter();
			mLocalStorageHelper.saveToStorage(BMKWebNyhetServiceImpl.BMK_NYHET_CACHE, mNyheter);
		}

		mResponseHandler.post(
			new Runnable() {
				@Override
				public void run() {
					mListener.onNyheterHentet(mNyheter);
				}
			}
		);
	}

	private void doHentNyheter() {
		Log.i(TAG, "Henter nyheter fra BMK...");
		mNyheter = new ArrayList<>();

		try {
			String hovedsideHtml = new String(ServiceHelper.hentRaadata(BMK_WEB_ROOT + BMK_NEWS_PAGE), BMK_ENCODING);

			int overskriftStartIndex = hovedsideHtml.indexOf(BMK_HTML_NEWS_HEADLINE);
			while (overskriftStartIndex >= 0) {
				//Henter overskrift
				overskriftStartIndex = hovedsideHtml.indexOf('>', overskriftStartIndex) + 1;
				int overskriftEndIndex = hovedsideHtml.indexOf("</td>", overskriftStartIndex);
				String overskrift = StringUtil.cleanHtml(hovedsideHtml.substring(overskriftStartIndex, overskriftEndIndex));

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
						ingress = StringUtil.cleanHtml(potensiellIngress);
						break;
					}
					ingressStartIndex = hovedsideHtml.indexOf("<tr>", ingressEndIndex);
				}

				Nyhet n = new Nyhet(overskrift, ingress, Nyhetskilde.BMK);
				n.setDato(finnDato(overskrift));
				mNyheter.add(n);

				//Finner link til hoved-storyen
				String lesMerLink = null;
				int lesMerStartIndex = hovedsideHtml.indexOf("<a href=\"", ingressStartIndex);
				if (lesMerStartIndex >= 0) {
					lesMerStartIndex += "<a href=\"".length();
					int lesMerEndIndex = hovedsideHtml.indexOf('\"', lesMerStartIndex);
					lesMerLink = (hovedsideHtml.substring(lesMerStartIndex, lesMerEndIndex));
					n.setFullStoryURL(BMK_WEB_ROOT + lesMerLink);
				}

				overskriftStartIndex = hovedsideHtml.indexOf(BMK_HTML_NEWS_HEADLINE, ingressStartIndex + 1);
			}
			Log.i(TAG, "Nyheter hentet fra BMK");
		}
		catch (IOException e) {
			DisplayUtil.showToast((Activity) mContext, R.string.bmk_nyheter_feil, Toast.LENGTH_LONG);
			Log.e(TAG, mContext.getString(R.string.bmk_nyheter_feil), e);
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
}
