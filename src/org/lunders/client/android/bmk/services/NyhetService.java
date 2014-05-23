package org.lunders.client.android.bmk.services;

import org.lunders.client.android.bmk.model.nyheter.Nyhet;

import java.util.Collection;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public interface NyhetService {

    Collection<Nyhet> hentNyheter();

	void hentNyhet(Nyhet nyhet);

	static interface NyhetListener {
		void onNyheterHentet(Collection<Nyhet> nyheter);
		void onNyhetHentet(Nyhet nyheten);
	}
}
