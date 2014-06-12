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

package org.lunders.client.android.bmk;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import org.lunders.client.android.bmk.fragments.nyhet.NyhetlisteFragment;
import org.lunders.client.android.bmk.util.BmkPagerAdapter;

import java.util.Arrays;

class InnstillingerPagerAdapter extends BmkPagerAdapter {

	//Antall tabs i pageren
	public static final int NUM_FRAGMENTS = 1;

	public InnstillingerPagerAdapter(FragmentManager fm) {
		super(fm, Arrays.asList("Innstillinger"));
	}

	@Override
	public Fragment getItem(int position) {

		return new NyhetlisteFragment();
	}

	@Override
	public int getCount() {
		return NUM_FRAGMENTS;
	}
}
