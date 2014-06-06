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

import org.lunders.client.android.bmk.model.aktivitet.AbstractAktivitet;

import java.util.Collection;

public interface AktivitetService {
	void hentAktiviteter(AktivitetListener listener);

	static interface AktivitetListener {
		void onAktiviteterHentet(Collection<AbstractAktivitet> aktiviteter);
	}
}
