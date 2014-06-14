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

import android.content.Context;
import org.lunders.client.android.bmk.model.aktivitet.AbstractAktivitet;
import org.lunders.client.android.bmk.services.AktivitetService;
import org.lunders.client.android.bmk.services.impl.LocalStorageHelper;
import org.lunders.client.android.bmk.services.impl.aktivitet.helpers.HentAktiviteterHelper;
import org.lunders.client.android.bmk.util.ThreadPool;

import java.util.Collection;

public class AktivitetServiceImpl implements AktivitetService {

	private Context            mContext;
	private LocalStorageHelper mLocalStorageHelper;

	public static final  String AKTIVITET_CACHE = "aktivitetCache";
	private static final String TAG             = AktivitetServiceImpl.class.getSimpleName();


	public AktivitetServiceImpl(Context context) {
		mContext = context;
		mLocalStorageHelper = LocalStorageHelper.getInstance(context);
	}


	@Override
	public void hentAktiviteter(AktivitetListener listener) {
		ThreadPool.getInstance().execute(new HentAktiviteterHelper(mContext, listener));

		//Henter aktiviteter fra lokalt lager
		Collection<AbstractAktivitet> cachedAktiviteter = mLocalStorageHelper.loadFromStorage(AKTIVITET_CACHE);

		//Dersom vi fikk noe fra lokalt lager, så sier vi fra om det.
		if (cachedAktiviteter != null && !cachedAktiviteter.isEmpty()) {
			listener.onAktiviteterHentet(cachedAktiviteter);
		}
	}
}
