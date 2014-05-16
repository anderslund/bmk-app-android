package org.lunders.client.android.bmk.model.personell;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
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
