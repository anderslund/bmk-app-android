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
import org.lunders.client.android.bmk.services.impl.nyhet.helpers.TwitterNyhetDetaljHelper;
import org.lunders.client.android.bmk.services.impl.nyhet.helpers.TwitterNyhetslisteHelper;
import org.lunders.client.android.bmk.util.ThreadPool;

import java.util.Collection;

public class TwitterNyhetServiceImpl implements NyhetService {

	private Context            mContext;
	private LocalStorageHelper mLocalStorageHelper;

	public static final String TWITTER_NYHET_CACHE = "twitterCache";

	private static final String TAG = TwitterNyhetServiceImpl.class.getSimpleName();

	public TwitterNyhetServiceImpl(Context context) {
		mContext = context;
		mLocalStorageHelper = LocalStorageHelper.getInstance(context);
	}

	@Override
	public void hentNyheter(NyhetListener nyhetlisteListener) {

		//Starter bakgrunnsjobben for å hente nyheter fra Twitter.
		ThreadPool.getInstance().execute(new TwitterNyhetslisteHelper(mContext, nyhetlisteListener));

		//Henter aktiviteter fra lokalt lager
		Collection<Nyhet> cachedNyheter = mLocalStorageHelper.loadFromStorage(TWITTER_NYHET_CACHE);

		//Dersom vi fikk noe fra lokalt lager, så sier vi fra om det.
		if (cachedNyheter != null && !cachedNyheter.isEmpty()) {
			nyhetlisteListener.onNyheterHentet(cachedNyheter);
		}
	}

	@Override
	public void hentNyhet(final Nyhet nyhet, final NyhetDetaljListener listener) {
		ThreadPool.getInstance().execute(new TwitterNyhetDetaljHelper(nyhet, listener));
	}
}
