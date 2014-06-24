package com.asb.brewzon;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.asb.adapters.NavDrawerListAdapter;
import com.asb.details.NavDrawerItemDetail;
import com.asb.fragments.LoginFragment;
import com.asb.fragments.RegistrationFragment;
import com.asb.helper.AppConstants;

public class LoginActivity extends BaseActivity {

	public DrawerLayout mDrawerLayout;

	public ListView mDrawerList;

	public ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	public CharSequence mDrawerTitle;

	// used to store app title
	public CharSequence mTitle;

	// slide menu items
	public String[] navMenuTitles;

	public TypedArray navMenuIcons;

	public ArrayList<NavDrawerItemDetail> navDrawerItems;

	public NavDrawerListAdapter adapter;

	/**************************************************/

	private int profileType = -1;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		inItView();
		setSlidingMenu(savedInstanceState);
	}

	private void inItView() {
		// TODO Auto-generated method stub

		if (getIntent() != null) {

			profileType = getIntent().getExtras().getInt(
					AppConstants.PROFILE_TYPE);
		}
	}

	/********************************************************************/

	@SuppressLint("NewApi")
	public void setSlidingMenu(Bundle savedInstanceState) {

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItemDetail>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItemDetail(navMenuTitles[0],
				navMenuIcons.getResourceId(0, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(true);

		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.header));

		getActionBar().setIcon(R.drawable.icon_header);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.menu_icon, // nav menu toggle icon
				R.string.header_university_login, // nav drawer open -
													// description for
				// accessibility
				R.string.header_university_login // nav drawer close -
													// description for
		// accessibility
		) {

			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(profileType);
		}
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			// displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	@SuppressLint("NewApi")
	private void displayView(int pType) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (pType) {
		case AppConstants.UNIVERSITY:
			fragment = new LoginFragment(profileType);
			setTitle(getString(R.string.header_university_login));
			break;

		case AppConstants.PROFESSIONAL:
			fragment = new LoginFragment(profileType);
			setTitle(getString(R.string.header_professional_login));
			break;

		case AppConstants.BREWZON:
			fragment = new LoginFragment(profileType);
			setTitle(getString(R.string.header_brewzon_login));
			break;

		case AppConstants.BREWZON_REGISTRATION:
			fragment = new RegistrationFragment(profileType);
			setTitle(getString(R.string.header_brewzon_signup));
			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			// mDrawerList.setItemChecked(pType, true);
			// mDrawerList.setSelection(pType);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

}
