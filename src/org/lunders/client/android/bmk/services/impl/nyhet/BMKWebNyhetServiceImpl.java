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

import android.content.Context;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.LocalStorageHelper;
import org.lunders.client.android.bmk.services.impl.nyhet.helpers.BMKNyhetListeHelper;
import org.lunders.client.android.bmk.services.impl.nyhet.helpers.BmkNyhetDetaljHelper;
import org.lunders.client.android.bmk.util.ThreadPool;

import java.util.Collection;

public class BMKWebNyhetServiceImpl implements NyhetService {

	private final Context            mContext;
	private final LocalStorageHelper mLocalStorageHelper;

	public static final String BMK_NYHET_CACHE = "bmkCache";

	public BMKWebNyhetServiceImpl(Context context) {
		mContext = context;
		mLocalStorageHelper = LocalStorageHelper.getInstance(context);
	}

	@Override
	public void hentNyheter(NyhetListener nyhetListener) {
		ThreadPool.getInstance().execute(new BMKNyhetListeHelper(mContext, nyhetListener));

		//Henter aktiviteter fra lokalt lager
		Collection<Nyhet> cachedNyheter = mLocalStorageHelper.loadFromStorage(BMK_NYHET_CACHE);

		//Dersom vi fikk noe fra lokalt lager, så sier vi fra om det.
		if (cachedNyheter != null && !cachedNyheter.isEmpty()) {
			nyhetListener.onNyheterHentet(cachedNyheter);
		}
	}

	@Override
	public void hentNyhet(Nyhet n, NyhetDetaljListener listener) {
		ThreadPool.getInstance().execute(new BmkNyhetDetaljHelper(mContext, n, listener));
	}
}
