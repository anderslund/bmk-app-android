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

package org.lunders.client.android.bmk.services.impl.aktivitet;

import android.util.Log;
import org.lunders.client.android.bmk.model.aktivitet.AbstractAktivitet;
import org.lunders.client.android.bmk.services.AktivitetService;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AktivitetServiceImpl implements AktivitetService {

	private static final String TAG = AktivitetServiceImpl.class.getSimpleName();


	@Override
	public List<AbstractAktivitet> hentAktiviteter(Date tilDato) {
		Log.i(TAG, "Henter aktiviteter fra OneDrive...");

		List l = new ArrayList();

		Log.i(TAG, "Aktiviteter hentet");
		return l;
	}

	public static void main(String[] args) throws Exception {
		Yaml y = new Yaml();

		Iterable<Object> documents = y.loadAll(
			new InputStreamReader(
				new FileInputStream(
					"C:\\Users\\G009430\\SkyDrive\\Application Data\\bmk-app\\aktiviteter\\aktiviteter.yaml"))
		);

//		List<Map<String, Map<String, Object>>> steder = (List<Map<String, Map<String, Object>>>) documents.iterator().next();

		List<Map<String, Object>> aktiviteter = (List<Map<String, Object>>) documents.iterator().next();

		for (Map<String, Object> aktivitet: aktiviteter) {
			System.out.println("Navn: " + aktivitet.get("Navn"));
			System.out.println("Sted: " + aktivitet.get("Sted"));
		}
	}
}
