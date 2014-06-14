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

package org.lunders.client.android.bmk.services.impl;

import android.content.Context;
import android.util.Log;

import java.io.*;

public final class LocalStorageHelper {

	private Context mContext;

	private static LocalStorageHelper INSTANCE;

	private static final String TAG = LocalStorageHelper.class.getSimpleName();

	private LocalStorageHelper(Context context) {
		mContext = context;
	}

	public synchronized static LocalStorageHelper getInstance(Context c) {
		if (INSTANCE == null) {
			INSTANCE = new LocalStorageHelper(c);
		}
		return INSTANCE;
	}


	// Lagrer nyhetene lokalt på enheten
	public void saveToStorage(String fileName, Object o) {
		try {
			final FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
			final ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(o);
			oos.flush();
			fos.close();
		}
		catch (IOException e) {
			Log.w(TAG, "Klarte ikke å lagre nyheter lokalt", e);
		}
	}

	//Laster nyhetene fra lokalt lager på enheten
	public <T> T loadFromStorage(String fileName) {
		try {
			final FileInputStream fis = mContext.openFileInput(fileName);
			if (fis == null) {
				return null;
			}

			final ObjectInputStream ois = new ObjectInputStream(fis);
			T nyheter = (T) ois.readObject();
			return nyheter;
		}
		catch (IOException | ClassNotFoundException e) {
			Log.i(TAG, "Ingenting i twitter-cache: " + e.getMessage());
		}
		return null;
	}
}
