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


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import org.lunders.client.android.bmk.util.DateUtil;

/**
 * Hovedaktiviteten (den man "lander på") for appen. I denne activityen kan man se på kommende BMK-
 * aktiviteter (konserter etc), nyheter fra diverse kilder og bilder fra instagram.
 */
public class MainActivity extends FragmentActivity {

	/** For å støtte swiping mellom tabs */
	private ViewPager viewPager;

	public static final int NUM_FRAGMENTS = 3;


	/** Hovedmetoden i en aktivitet. Tilsvarende main i en vanlig Java-app */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupViewPagerForSwiping();
		setupActionBar();
	}

	/** Setter opp action bar (tool bar) */
	private void setupActionBar() {

		//Setter dagens dato som subtittel
		getActionBar().setSubtitle(DateUtil.getFormattedCurrentDate());

		//Setter bakgrunnsbilde
		Drawable d = getResources().getDrawable(R.drawable.notes_2_trans);
		getActionBar().setBackgroundDrawable(d);

		//Setter opp en listener på tabene slik at vi kan velge et annet fragment
		//når vi trykker på en tab.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				viewPager.setCurrentItem(tab.getPosition());
			}

			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
			}

			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
			}
		};

		//Setter opp tabs
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getActionBar().addTab(getActionBar().newTab().setText("Nyheter").setTabListener(tabListener));
		getActionBar().addTab(getActionBar().newTab().setText("Aktiviteter").setTabListener(tabListener));
		getActionBar().addTab(getActionBar().newTab().setText("Bilder").setTabListener(tabListener));
	}

	/** Setter opp en pager for å støtte swiping mellom tabs */
	private void setupViewPagerForSwiping() {
		viewPager = new ViewPager(this);
		viewPager.setId(R.id.viewPager);

		//Når vi swiper, må vi også sørge for å endre valgt tab. ellers velger vi bare nytt
		//fragment, uten å oppdatere valgt tab.
		viewPager.setOnPageChangeListener(
			new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					getActionBar().setSelectedNavigationItem(position);
				}
			}
		);

		//Pageren må ha en adapter som forteller hva som skal gjøres når vi swiper på siden.
		final FragmentManager fm = getSupportFragmentManager();
		viewPager.setAdapter(
			new FragmentStatePagerAdapter(fm) {
				@Override
				public Fragment getItem(int position) {
					//TODO: Tror ikke disse new'ene her er bra for ytelse etc, fordi de lager nye fragmenter hele tiden.
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
		);

		//Setter viewet til aktiviteten
		setContentView(viewPager);
	}
}
