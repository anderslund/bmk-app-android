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

package org.lunders.client.android.bmk.model.aktivitet;

import org.lunders.client.android.bmk.model.personell.Riggegruppe;

import java.util.Date;

public class Konsert extends AbstractAktivitet {

	private Riggegruppe riggegruppe;

	private String antrekk;

	public Konsert(String navn, Date tidspunktStart) {
		super(navn, tidspunktStart);
		setAktivitetstype(Aktivitetstype.Konsert);
	}

	public String getAntrekk() {
		return antrekk;
	}

	public void setAntrekk(String antrekk) {
		this.antrekk = antrekk;
	}

	public Riggegruppe getRiggegruppe() {
		return riggegruppe;
	}

	public void setRiggegruppe(Riggegruppe riggegruppe) {
		this.riggegruppe = riggegruppe;
	}
}
