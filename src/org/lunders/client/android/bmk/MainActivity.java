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

public class MainActivity extends FragmentActivity {

	private ViewPager viewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		viewPager = new ViewPager(this);
		viewPager.setId(R.id.viewPager);
		viewPager.setOnPageChangeListener(
			new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					// When swiping between pages, select the
					// corresponding tab.
					getActionBar().setSelectedNavigationItem(position);
				}
			}
		);

//		FrameLayout fl = (FrameLayout) findViewById(R.id.fragmentContainer);
//		fl.addView(viewPager);

		//TODO: Må prøve å få pageren under "Neste øvelse"
//		setContentView(R.layout.activity_nyheter);
		setContentView(viewPager);

		getActionBar().setSubtitle(DateUtil.getFormattedCurrentDate());

		Drawable d = getResources().getDrawable(R.drawable.notes_2_trans);
		getActionBar().setBackgroundDrawable(d);

		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				viewPager.setCurrentItem(tab.getPosition());
			}

			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// hide the given tab
			}

			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// probably ignore this event
			}
		};

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getActionBar().addTab(getActionBar().newTab().setText("Nyheter").setTabListener(tabListener));
		getActionBar().addTab(getActionBar().newTab().setText("Aktiviteter").setTabListener(tabListener));
		getActionBar().addTab(getActionBar().newTab().setText("Bilder").setTabListener(tabListener));

		final FragmentManager fm = getSupportFragmentManager();
		viewPager.setAdapter(
			new FragmentStatePagerAdapter(fm) {
				@Override
				public Fragment getItem(int position) {
					switch (position) {
						case 0:
							return new NyhetlisteFragment();

						case 1:
							return new AktivitetFragment();

						default:
							return new BilderFragment();
					}
				}

				@Override
				public int getCount() {
					return 3;
				}
			}
		);
	}
}
