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
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;
import org.lunders.client.android.bmk.fragments.felles.LoginFragment;
import org.lunders.client.android.bmk.util.BmkPagerAdapter;
import org.lunders.client.android.bmk.util.DateUtil;
import org.lunders.client.android.bmk.util.SessionUtils;

import java.util.List;

/**
 * Hovedaktiviteten (den man "lander på") for appen. I denne activityen kan man se på kommende BMK-
 * aktiviteter (konserter etc), nyheter fra diverse kilder og bilder fra instagram.
 */
public class MainActivity extends FragmentActivity {

	//For å støtte swiping mellom tabs
	private ViewPager mViewPager;

	// For navigasjonsmeny (fra venstre)
	private ActionBarDrawerToggle mDrawerToggle;

	private BmkPagerAdapter mPagerAdapter;
	private FragmentManager mFragmentManager;
	private FrameLayout     mFragmentContainer;
	private ActionBar       mActionBar;


	/** Hovedmetoden i en aktivitet. Tilsvarende main i en vanlig Java-app */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (SessionUtils.isLoggedIn()) {
			setTheme(R.style.Theme_bmk_logged_in);
		}

		setContentView(R.layout.activity_nyheter);

		//Pageren må ha en adapter som forteller hva som skal gjøres når vi swiper på siden.
		if (mPagerAdapter == null) {
			mFragmentManager = getSupportFragmentManager();
			mPagerAdapter = new NyhetPagerAdapter(mFragmentManager);
		}

		mFragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
		setupViewPager();
		setupActionBar();
		setupNavigationDrawer();
	}


	/** Setter opp action bar (tool bar) */
	private void setupActionBar() {

		//Setter dagens dato som subtittel
		mActionBar = getActionBar();

		mActionBar.setSubtitle(DateUtil.getFormattedCurrentDate());
//
//		//Setter opp en listener på tabene slik at vi kan velge et annet fragment
		//når vi trykker på en tab.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());
			}

			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
			}

			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
			}
		};

		//Setter opp tabs
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionBar.removeAllTabs();
		List<String> tabNames = mPagerAdapter.getTabNames();
		for (String tabName : tabNames) {
			mActionBar.addTab(getActionBar().newTab().setText(tabName).setTabListener(tabListener));
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.nyhet_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_login:
				LoginFragment dialog = LoginFragment.newInstance();
				dialog.show(mFragmentManager, "dialog_login");
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}


	private void setupNavigationDrawer() {
		final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(
			new ArrayAdapter<>(
				this, R.layout.drawer_list_item,
				new String[]{"Nyheter og sosiale media", "Min profil", "Innstillinger"}));

		mDrawerList.setSelection(0);
		mDrawerList.setOnItemClickListener(
			new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					System.out.println("Trykket på " + ((TextView) view).getText());
					final FragmentManager fm = getSupportFragmentManager();

					switch (position) {
						case 0:
							mPagerAdapter = new NyhetPagerAdapter(fm);
							break;

						case 1:
							mPagerAdapter = new ProfilPagerAdapter(fm);
							break;

						case 2:
							mPagerAdapter = new InnstillingerPagerAdapter(fm);
							break;
					}

					mDrawerLayout.closeDrawer(Gravity.LEFT);
					mFragmentContainer.removeView(mViewPager);
					setupViewPager();
					setupActionBar();
				}
			});

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
		getActionBar().setHomeButtonEnabled(true);

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}



	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
	}

	/** Setter opp en pager for å støtte swiping mellom tabs */
	private void setupViewPager() {

		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		mFragmentContainer.addView(mViewPager);

		//Når vi swiper, må vi også sørge for å endre valgt tab. ellers velger vi bare nytt
		//fragment, uten å oppdatere valgt tab.
		mViewPager.setOnPageChangeListener(
			new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					getActionBar().setSelectedNavigationItem(position);
				}
			}
		);

		mViewPager.setAdapter(mPagerAdapter);
	}
}
