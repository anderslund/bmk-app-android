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
import org.lunders.client.android.bmk.services.impl.nyhet.helpers.TwitterNyhetDetaljHelper;
import org.lunders.client.android.bmk.services.impl.nyhet.helpers.TwitterNyhetslisteHelper;
import org.lunders.client.android.bmk.util.ThreadPool;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;

public class TwitterNyhetServiceImpl implements NyhetService {

	private Context mContext;
	public static final String TWITTER_NYHET_CACHE = "twitterCache";

	public TwitterNyhetServiceImpl(Context context)  {
		mContext = context;
	}

	@Override
	public void hentNyheter(NyhetListener nyhetlisteListener) {

		//Setter opp bakgrunnsjobben for å hente nyheter fra Twitter.
		ThreadPool.getInstance().execute(new TwitterNyhetslisteHelper(mContext, nyhetlisteListener));

		//Henter aktiviteter fra lokalt lager
		Collection<Nyhet> cachedNyheter = loadNyheterFromStorage();

		//Dersom vi fikk noe fra lokalt lager, så sier vi fra om det.
		if (cachedNyheter != null && !cachedNyheter.isEmpty()) {
			nyhetlisteListener.onNyheterHentet(cachedNyheter);
		}
	}

	@Override
	public void hentNyhet(final Nyhet nyhet, final NyhetDetaljListener listener) {
		ThreadPool.getInstance().execute(new TwitterNyhetDetaljHelper(nyhet, listener));
	}

	private Collection<Nyhet> loadNyheterFromStorage() {
		try {
			final FileInputStream fis = mContext.openFileInput(TWITTER_NYHET_CACHE);
			if ( fis == null) {
				return null;
			}

			final ObjectInputStream ois = new ObjectInputStream(fis);
			Collection<Nyhet> nyheter = (Collection<Nyhet>) ois.readObject();
			return nyheter;
		}
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
