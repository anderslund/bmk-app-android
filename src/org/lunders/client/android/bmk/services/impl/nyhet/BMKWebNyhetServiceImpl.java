package org.lunders.client.android.bmk.services.impl.nyhet;

import android.util.Log;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.model.nyheter.Nyhetskilde;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class BMKWebNyhetServiceImpl implements NyhetService {

	private static final String TAG = BMKWebNyhetServiceImpl.class.getSimpleName();

	@Override
	public List<Nyhet> hentNyheter() {
		Log.i(TAG, "Henter nyheter...");

		List l = new ArrayList<>();
		l.add(new Nyhet("Lorem ipsum ", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque iaculis sit amet sem eget sodales. Mauris ultrices mattis dignissim. Cras porta ut enim nec condimentum. Nulla viverra est sit amet ipsum blandit blandit in non metus. Ut cursus justo nisi, eu lobortis est tristique eget.", Nyhetskilde.BMK));

		Log.i(TAG, "Nyheter hentet");
		return l;
	}
}
