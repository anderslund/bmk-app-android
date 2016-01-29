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

package org.lunders.client.android.bmk.model.personell;

import java.io.Serializable;

public class Medlem implements Comparable<Medlem>, Serializable {

    private String fornavn, etternavn;

    //Medlemsnr NMF
    private String nmfMedlemsnr;

    // Om medlemmet også er styremedlem
    private boolean styremedlem;

    //Om medlemmet også er gruppeleder
    private boolean gruppeleder;

    private Instrumentgruppe instrumentgruppe;

    //Hvilket riggeteam medlemmet tilhører
    private Riggegruppe riggegruppe;


	public void setRiggegruppe(Riggegruppe riggegruppe) {
		this.riggegruppe = riggegruppe;
	}

	public Riggegruppe getRiggegruppe() {
		return riggegruppe;
	}

	@Override
	public int compareTo(Medlem another) {
		if (another != null) {
			return (fornavn + etternavn).compareTo(another.fornavn + another.etternavn);
		}
		return -1;
	}
}
