package org.lunders.client.android.bmk.model.personell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class Riggegruppe implements Serializable {

	private int gruppenr;

	private Set<Medlem> medlemmer;

	public Riggegruppe() {
		medlemmer = new TreeSet<>();
	}

	public void addMedlem(Medlem m) {
		medlemmer.add(m);
	}
}
