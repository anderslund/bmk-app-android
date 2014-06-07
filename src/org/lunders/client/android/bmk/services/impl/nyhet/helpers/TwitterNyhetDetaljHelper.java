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

import android.os.Handler;
import android.os.Looper;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.services.NyhetService;

public class TwitterNyhetDetaljHelper implements Runnable {

	private final Nyhet                            mNyhet;
	private final NyhetService.NyhetDetaljListener mListener;
	private final Handler                          mResponseHandler;

	public TwitterNyhetDetaljHelper(Nyhet n, NyhetService.NyhetDetaljListener l) {
		this.mNyhet = n;
		this.mListener = l;
		mResponseHandler = new Handler(Looper.getMainLooper());
	}

	@Override
	public void run() {
		// Trenger ikke å hente noe mer fra Twitter. "Ingress" og Story er samme sak
		mResponseHandler.post(
			new Runnable() {
				@Override
				public void run() {
					mListener.onNyhetHentet(mNyhet);
				}
			}
		);
	}
}
