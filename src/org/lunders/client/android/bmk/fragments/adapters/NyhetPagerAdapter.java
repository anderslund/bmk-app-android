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

package org.lunders.client.android.bmk.fragments.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import org.lunders.client.android.bmk.fragments.nyhet.AktivitetlisteFragment;
import org.lunders.client.android.bmk.fragments.nyhet.BildeFragment;
import org.lunders.client.android.bmk.fragments.nyhet.NyhetlisteFragment;

import java.util.Arrays;

public class NyhetPagerAdapter extends BmkPagerAdapter {

	//Antall tabs i pageren
	public static final int NUM_FRAGMENTS = 3;


	public NyhetPagerAdapter(FragmentManager fm) {
		super(fm, Arrays.asList("Nyheter", "Aktiviteter", "Bilder"));
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case 0:
				return new NyhetlisteFragment();

			case 1:
				return new AktivitetlisteFragment();

			default:
				return new BildeFragment();
		}
	}

	@Override
	public int getCount() {
		return NUM_FRAGMENTS;
	}
}
