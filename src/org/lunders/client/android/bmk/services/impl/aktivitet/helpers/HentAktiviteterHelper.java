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

package org.lunders.client.android.bmk.services.impl.aktivitet.helpers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import org.lunders.client.android.bmk.R;
import org.lunders.client.android.bmk.model.aktivitet.AbstractAktivitet;
import org.lunders.client.android.bmk.model.aktivitet.Konsert;
import org.lunders.client.android.bmk.model.aktivitet.Oppdrag;
import org.lunders.client.android.bmk.model.aktivitet.Ovelse;
import org.lunders.client.android.bmk.model.lokasjon.Koordinater;
import org.lunders.client.android.bmk.model.lokasjon.Sted;
import org.lunders.client.android.bmk.services.AktivitetService;
import org.lunders.client.android.bmk.services.impl.ServiceHelper;
import org.lunders.client.android.bmk.services.impl.aktivitet.AktivitetServiceImpl;
import org.lunders.client.android.bmk.util.DateUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HentAktiviteterHelper implements Runnable {

	private Handler                            mResponseHandler;
	private AktivitetService.AktivitetListener mListener;
	private List<AbstractAktivitet>            mCurrentAktiviteter;
	private Context                            mContext;

	private static final String GITHUB_AKTIVITETER
		= "https://raw.githubusercontent.com/anderslund/bmk/master/aktiviteter/aktiviteter.yaml";

	private static final String AKTIVITETER_ENCODING = "UTF-8";

	private static final String TAG = HentAktiviteterHelper.class.getSimpleName();



	public HentAktiviteterHelper(Context context, AktivitetService.AktivitetListener listener) {
		mContext = context;
		mResponseHandler = new Handler(Looper.getMainLooper());
		mListener = listener;
	}

	@Override
	public void run() {
		Log.i(TAG, "Henter aktiviteter fra GitHub...");

		doHentAktiviteter();
		storeAktiviteterToStorage(mCurrentAktiviteter);
		mResponseHandler.post(
			new Runnable() {
				@Override
				public void run() {
					mListener.onAktiviteterHentet(mCurrentAktiviteter);
				}
			}
		);
	}


	private void doHentAktiviteter() {

		String aktiviteterYaml;
		try {
			aktiviteterYaml = new String(ServiceHelper.hentRaadata(GITHUB_AKTIVITETER), AKTIVITETER_ENCODING);
		}
		catch (IOException e) {
			Toast.makeText(mContext, R.string.aktivitet_feil, Toast.LENGTH_LONG).show();
			return;
		}

		Yaml y = new Yaml();
		List<Map<String, Object>> aktiviteter = (List<Map<String, Object>>) y.load(aktiviteterYaml);

		mCurrentAktiviteter = new ArrayList<>(aktiviteter.size());
		for (Map<String, Object> rawAktivitet : aktiviteter) {
			AbstractAktivitet aktivitet;
			String type = (String) rawAktivitet.get("Aktivitet");
			String navn = (String) rawAktivitet.get("Navn");
			String beskrivelse = (String) rawAktivitet.get("Beskrivelse");
			String tidspunktStart = (String) rawAktivitet.get("TidspunktStart");
			String tidspunktSlutt = (String) rawAktivitet.get("TidspunktSlutt");

			Map<String, Object> rawSted = (Map<String, Object>) rawAktivitet.get("Sted");

			switch (type) {
				case "Øvelse":
					Ovelse ovelse = new Ovelse(navn, DateUtil.getDate(tidspunktStart));
					aktivitet = ovelse;
					break;

				case "Oppdrag":
					Oppdrag oppdrag = new Oppdrag(navn, DateUtil.getDate(tidspunktStart));
					aktivitet = oppdrag;
					break;

				case "Konsert":
					Konsert konsert = new Konsert(navn, DateUtil.getDate(tidspunktStart));
					aktivitet = konsert;
					break;

				default:
					Log.w(TAG, "Ukjent aktivitetstype: " + type);
					continue;
			}

			aktivitet.setBeskrivelse(beskrivelse);
			aktivitet.setTidspunktStart(DateUtil.getDate(tidspunktSlutt));
			aktivitet.setSted(getSted(rawSted));
			mCurrentAktiviteter.add(aktivitet);
		}
	}

	private Sted getSted(Map<String, Object> rawSted) {
		if (rawSted == null) {
			return null;
		}

		Koordinater k = null;
		Double breddegrad = (Double) rawSted.get("Breddegrad");
		Double lengdegrad = (Double) rawSted.get("Lengdegrad");
		if (breddegrad != null && lengdegrad != null) {
			k = new Koordinater(lengdegrad, breddegrad);
		}

		return new Sted((String) rawSted.get("Navn"), k);
	}


	private void storeAktiviteterToStorage(List<AbstractAktivitet> aktiviteter) {
		try {
			final FileOutputStream fos = mContext.openFileOutput(AktivitetServiceImpl.AKTIVITET_CACHE, Context.MODE_PRIVATE);
			final ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(aktiviteter);
			oos.flush();
			fos.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}