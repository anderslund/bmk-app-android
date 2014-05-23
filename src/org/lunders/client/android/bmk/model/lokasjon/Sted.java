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

package org.lunders.client.android.bmk.model.lokasjon;

import android.net.Uri;

import java.io.Serializable;

public class Sted implements Serializable {

	private String navn;

	private Koordinater koordinater;

	public Sted(String navn) {
		this.navn = navn;
	}

	public Sted(String navn, Koordinater koordinater) {
		this(navn);
		this.koordinater = koordinater;
	}

	public String getNavn() {
		return navn;
	}

	public void setNavn(String navn) {
		this.navn = navn;
	}

	public Koordinater getKoordinater() {
		return koordinater;
	}

	@Override
	public String toString() {
		return navn;
	}

	public String formatAsUri() {
		return "geo:" + koordinater.format() + "?q=" + Uri.encode(koordinater.format()+ "(" + navn + ")");
	}
}
