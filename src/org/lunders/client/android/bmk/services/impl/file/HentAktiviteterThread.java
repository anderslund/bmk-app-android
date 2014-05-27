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

package org.lunders.client.android.bmk.services.impl.file;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveDownloadOperation;
import com.microsoft.live.LiveOperationException;
import org.lunders.client.android.bmk.model.aktivitet.AbstractAktivitet;
import org.lunders.client.android.bmk.model.aktivitet.Konsert;
import org.lunders.client.android.bmk.model.aktivitet.Oppdrag;
import org.lunders.client.android.bmk.model.aktivitet.Ovelse;
import org.lunders.client.android.bmk.services.AktivitetService;
import org.lunders.client.android.bmk.util.DateUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class HentAktiviteterThread extends HandlerThread {

	private Handler responseHandler;
	private AktivitetService.AktivitetListener listener;
	private Context activity;
	private LiveConnectClient liveClient;
	private String aktiviteterFileId;

	private static final String TAG = HentAktiviteterThread.class.getSimpleName();

	public HentAktiviteterThread(Context activity, LiveConnectClient liveClient, AktivitetService.AktivitetListener listener, String aktiviteterFileId) {
		super(TAG);
		this.activity = activity;
		this.liveClient = liveClient;
		this.aktiviteterFileId = aktiviteterFileId;
		responseHandler = new Handler(Looper.getMainLooper());
		this.listener = listener;
	}

	@Override
	public void run() {
		try {
			final List<AbstractAktivitet> aktiviteter = doHentAktiviteter(aktiviteterFileId);
			storeAktiviteterToStorage(aktiviteter);
			responseHandler.post(
				new Runnable() {
					@Override
					public void run() {
						listener.onAktiviteterHentet(aktiviteter);
					}
				}
			);
		}
		catch (LiveOperationException e) {
			//TODO: Toast eller noe sånt
			e.printStackTrace();
		}
	}

	private List<AbstractAktivitet> doHentAktiviteter(String fileId) throws LiveOperationException {

		final LiveDownloadOperation download = liveClient.download(fileId + "/content");
		InputStream stream = download.getStream();

		Yaml y = new Yaml();
		List<Map<String, Object>> aktiviteter = null;
		try {
			aktiviteter = (List<Map<String, Object>>) y.load(new InputStreamReader(stream, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		List<AbstractAktivitet> result = new ArrayList<>(aktiviteter.size());
		for (Map<String, Object> rawAktivitet : aktiviteter) {
			AbstractAktivitet aktivitet;
			String type = (String) rawAktivitet.get("Aktivitet");
			String navn = (String) rawAktivitet.get("Navn");
			String tidspunktStart = (String) rawAktivitet.get("TidspunktStart");

			switch (type) {
				case "Øvelse":
					aktivitet = new Ovelse(navn, DateUtil.getDate(tidspunktStart));
					break;
				case "Oppdrag":
					aktivitet = new Oppdrag(navn, DateUtil.getDate(tidspunktStart));
					break;
				case "Konsert":
					aktivitet = new Konsert(navn, DateUtil.getDate(tidspunktStart));
					break;

				default:
					Log.w(TAG, "Ukjent aktivitetstype: " + type);
					continue;
			}
			result.add(aktivitet);
		}
		return result;
	}

	private void storeAktiviteterToStorage(List<AbstractAktivitet> aktiviteter) {
		try {
			final FileOutputStream fos = activity.openFileOutput("aktivitetCache", Context.MODE_PRIVATE);
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
