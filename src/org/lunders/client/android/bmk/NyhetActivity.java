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


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;
import org.lunders.client.android.bmk.util.DateUtil;

/**
 * Hovedaktiviteten (den man "lander på") for appen. I denne activityen kan man se på kommende BMK-
 * aktiviteter (konserter etc), nyheter fra diverse kilder og bilder fra instagram.
 */
public class NyhetActivity extends FragmentActivity {

	/** For å støtte swiping mellom tabs */
	private ViewPager viewPager;

	public static final int NUM_FRAGMENTS = 3;
	private ActionBarDrawerToggle mDrawerToggle;


	/** Hovedmetoden i en aktivitet. Tilsvarende main i en vanlig Java-app */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nyheter);
		setupViewPagerForSwiping();
		setupActionBar();
		//setupNavigationDrawer();
	}

	/** Setter opp action bar (tool bar) */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
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

	private void setupNavigationDrawer() {
		DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		//ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
		//mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, new String[]{"Option 1", "Option2", "Option 3"}));

		mDrawerToggle = new ActionBarDrawerToggle(
			this,                  /* host Activity */
			mDrawerLayout,         /* DrawerLayout object */
			R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
			R.string.drawer_open,  /* "open drawer" description */
			R.string.drawer_close  /* "close drawer" description */
		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				//getActionBar().setTitle(mTitle);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				//getActionBar().setTitle(mDrawerTitle);
			}
		};

		getActionBar().setDisplayHomeAsUpEnabled(true);
		//getActionBar().setHomeButtonEnabled(true);

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		if ( mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
	}

	/** Setter opp en pager for å støtte swiping mellom tabs */
	private void setupViewPagerForSwiping() {

		viewPager = new ViewPager(this);
		viewPager.setId(R.id.viewPager);

		FrameLayout fl = (FrameLayout) findViewById(R.id.fragmentContainer);
		fl.addView(viewPager);

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
	}
}