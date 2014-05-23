package org.lunders.client.android.bmk.services.impl.nyhet;

import android.util.Log;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.model.nyheter.Nyhetskilde;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.AbstractServiceImpl;

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
public class TwitterNyhetServiceImpl extends AbstractServiceImpl implements NyhetService {

	private static final String TAG = TwitterNyhetServiceImpl.class.getSimpleName();

	@Override
	public List<Nyhet> hentNyheter() {
		Log.i(TAG, "Henter nyheter fra Twitter...");

		List l = new ArrayList<>();
		l.add(new Nyhet("Morbi at arcu", "Morbi at arcu ut orci elementum molestie. Curabitur eu sapien augue. Nullam fermentum et dolor quis sagittis. Nunc mattis justo sit amet mi suscipit pharetra. Ut adipiscing, est quis congue faucibus, mi dui tempus libero, a faucibus nisl odio sed metus. Nullam blandit feugiat nisi, vitae dictum justo tristique et.", Nyhetskilde.Twitter));

		Log.i(TAG, "Nyheter hentet");
		return l;
	}

	@Override
	public void hentNyhet(Nyhet nyhet) {

	}
}
