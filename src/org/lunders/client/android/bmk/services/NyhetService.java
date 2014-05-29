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

package org.lunders.client.android.bmk.services;

import org.lunders.client.android.bmk.model.nyheter.Nyhet;

import java.util.Collection;

public interface NyhetService {

	void hentNyheter(NyhetListener listener);

	void hentNyhet(Nyhet nyhet, NyhetDetaljListener listener);

	static interface NyhetListener {
		void onNyheterHentet(Collection<Nyhet> nyheter);
	}

	static interface NyhetDetaljListener {
		void onNyhetHentet(Nyhet nyheten);
	}
}
