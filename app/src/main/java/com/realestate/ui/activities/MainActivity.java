package com.realestate.ui.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.realestate.ApplicationVars;
import com.realestate.R;
import com.realestate.custom.CustomActivity;
import com.realestate.model.Data;
import com.realestate.model.SQLiteTerm;
import com.realestate.model.sqlite.DrupalTerms;
import com.realestate.ui.adapters.LeftNavAdapter;
import com.realestate.ui.fragments.FeedList;
import com.realestate.ui.fragments.MapViewer;
import com.realestate.ui.fragments.NewEquipment;
import com.realestate.ui.fragments.Search;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class MainActivity is the base activity class of the application. This
 * activity is launched after the Splash and it holds all the Fragments used in
 * the app. It also creates the Navigation Drawer on left side.
 */
public class MainActivity extends CustomActivity
{

	/** The drawer layout. */
	private DrawerLayout drawerLayout;

	/** ListView for left side drawer. */
	private ListView drawerLeft;

	/** The drawer toggle. */
	private ActionBarDrawerToggle drawerToggle;

	/** The left navigation list adapter. */
	private LeftNavAdapter adapter;
	private int fragment2Launch = 3;

	/**
	 * Dispatch onPause() to fragments.
	 */
	@Override
	protected void onPause() {
		Common.log("MainActivity onPause");
		super.onPause();
	}

	/**
	 * Dispatch onResume() to fragments.  Note that for better inter-operation
	 * with older versions of the platform, at the point of this call the
	 * fragments attached to the activity are <em>not</em> resumed.  This means
	 * that in some cases the previous state may still be saved, not allowing
	 * fragment transactions that modify the state.  To correctly interact
	 * with fragments in their proper state, you should instead override
	 * {@link #onResumeFragments()}.
	 */
	@Override
	protected void onResume() {
		Common.log("MainActivity onResume");
		super.onResume();
	}

	/**
	 * Dispatch onStop() to all fragments.  Ensure all loaders are stopped.
	 */
	@Override
	protected void onStop() {
		Common.log("MainActivity onStop");
		super.onStop();
	}

	/**
	 * Destroy all fragments and loaders.
	 */
	@Override
	protected void onDestroy() {
		Common.log("MainActivity onDestroy");
		super.onDestroy();
	}

	/**
	 * Dispatch onStart() to all fragments.  Ensure any created loaders are
	 * now started.
	 */
	@Override
	protected void onStart() {
		Common.log("MainActivity onStart");
		super.onStart();
		ApplicationVars.initialize(getApplicationContext());
	}

	/* (non-Javadoc)
		 * @see com.newsfeeder.custom.CustomActivity#onCreate(android.os.Bundle)
		 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Common.log("MainActivity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupContainer();
		setupDrawer();
		fragment2Launch = getIntent().getExtras() != null ?
							getIntent().getExtras().getInt(Constants.INTENTVARS.FRAGMENTPOS) :
							fragment2Launch;
		launchFragment(fragment2Launch);
	}

	/**
	 * Setup the drawer layout. This method also includes the method calls for
	 * setting up the Left side drawer.
	 */
	private void setupDrawer()
	{
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View view)
			{
				setActionBarTitle();
			}

			@Override
			public void onDrawerOpened(View drawerView)
			{
				getActionBar().setTitle(R.string.app_name);
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);
		drawerLayout.closeDrawers();

		setupLeftNavDrawer();
	}

	/**
	 * Setup the left navigation drawer/slider. You can add your logic to load
	 * the contents to be displayed on the left side drawer. You can also setup
	 * the Header and Footer contents of left drawer if you need them.
	 */
	private void setupLeftNavDrawer()
	{
		drawerLeft = (ListView) findViewById(R.id.left_drawer);

		adapter = new LeftNavAdapter(this, getLeftNavItems());
		drawerLeft.setAdapter(adapter);
		drawerLeft.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {
				drawerLayout.closeDrawers();
				launchFragment(pos);
			}
		});

	}

	/**
	 * This method returns a list of dummy items for left navigation slider. You
	 * can write or replace this method with the actual implementation for list
	 * items.
	 * 
	 * @return the dummy items
	 */
	private ArrayList<Data> getLeftNavItems()
	{
		ArrayList<Data> al = new ArrayList<Data>();
		al.add(new Data(getResources().getString(R.string.equipment), R.drawable.ic_nav1, R.drawable.ic_nav1_sel));
		al.add(new Data(getResources().getString(R.string.search), R.drawable.ic_nav2, R.drawable.ic_nav2_sel));
		al.add(new Data(getResources().getString(R.string.map), R.drawable.ic_nav3, R.drawable.ic_nav3_sel));
		al.add(new Data(getResources().getString(R.string.add_new_equipment), R.drawable.ic_plus, R.drawable.ic_plus));
		return al;
	}

	/**
	 * This method can be used to attach Fragment on activity view for a
	 * particular tab position. You can customize this method as per your need.
	 * 
	 * @param pos
	 *            the position of tab selected.
	 */
	public void launchFragment(int pos)
	{
		Fragment f = null;
		String title = null;
		if (pos == 0)
		{
			title = getResources().getString(R.string.equipment);
			f = new FeedList();
		}
		else if (pos == 1)
		{
			title = getResources().getString(R.string.search);
			f = new Search();
		}
		else if (pos == 2)
		{
			title = getResources().getString(R.string.map);
			f = new MapViewer();
		}
		else if (pos == 3)
		{
			title = getResources().getString(R.string.add_new_equipment);
			f = new NewEquipment();
		}

		if (f != null)
		{
			while (getSupportFragmentManager().getBackStackEntryCount() > 0)
			{
				getSupportFragmentManager().popBackStackImmediate();
			}
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, f).addToBackStack(title)
					.commit();
			if (adapter != null)
				adapter.setSelection(pos);
		}
	}

	/**
	 * Setup the container fragment for drawer layout. The current
	 * implementation of this method simply calls launchFragment method for tab
	 * position 0. You can customize this method as per your need to display
	 * specific content.
	 */
	private void setupContainer()
	{
		getSupportFragmentManager().addOnBackStackChangedListener(
				new OnBackStackChangedListener() {

					@Override
					public void onBackStackChanged() {
						setActionBarTitle();
					}
				});
	}

	/**
	 * Set the action bar title text.
	 */
	private void setActionBarTitle()
	{
		if (drawerLayout.isDrawerOpen(drawerLeft))
		{
			getActionBar().setTitle(R.string.app_name);
			return;
		}
		if (getSupportFragmentManager().getBackStackEntryCount() == 0)
			return;
		String title = getSupportFragmentManager().getBackStackEntryAt(
				getSupportFragmentManager().getBackStackEntryCount() - 1)
				.getName();
		getActionBar().setTitle(title);
		getActionBar().setLogo(R.drawable.icon);
	}

	//region "dummyDrupalTerms"
	private void initTermsData(){
		DrupalTerms drupalTerms = new DrupalTerms(this);
		drupalTerms.deleteAllTerms();	//remove any existent terms

		//CULTIVATION
		drupalTerms.insertTerm(new SQLiteTerm(115, "Almond trees", Constants.VOCABULARYNAMES.CULTIVATION));
		drupalTerms.insertTerm(new SQLiteTerm(109, "Barley", Constants.VOCABULARYNAMES.CULTIVATION));
		drupalTerms.insertTerm(new SQLiteTerm(113, "Cherry Tree", Constants.VOCABULARYNAMES.CULTIVATION));
		drupalTerms.insertTerm(new SQLiteTerm(112, "Herbs", Constants.VOCABULARYNAMES.CULTIVATION));
		drupalTerms.insertTerm(new SQLiteTerm(60, "Cotton", Constants.VOCABULARYNAMES.CULTIVATION));

		//LOCATION
		drupalTerms.insertTerm(new SQLiteTerm(175, "Agia Triada", Constants.VOCABULARYNAMES.LOCATION));
		drupalTerms.insertTerm(new SQLiteTerm(195, "Agrinio", Constants.VOCABULARYNAMES.LOCATION));
		drupalTerms.insertTerm(new SQLiteTerm(188, "Argos", Constants.VOCABULARYNAMES.LOCATION));

		drupalTerms.closeConnection();
		//debugTermsData();
	}

	private void debugTermsData() {
		DrupalTerms drupalTerms = new DrupalTerms(this);

		List<SQLiteTerm> cultivations = drupalTerms.getVocabularyTerms(Constants.VOCABULARYNAMES.CULTIVATION);
		for(int i=0; i<cultivations.size(); i++) {
			Common.log("cultivation:" + cultivations.get(i).getName());
		}

		List<SQLiteTerm> locations = drupalTerms.getVocabularyTerms(Constants.VOCABULARYNAMES.LOCATION);
		Iterator<SQLiteTerm> locationsIterator = locations.iterator();
		while(locationsIterator.hasNext()) {
			Common.log("location:" + locationsIterator.next().getName());
		}
		drupalTerms.closeConnection();
	}
	//endregion

	/* (non-Javadoc)
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
		drawerToggle.onConfigurationChanged(newConfig);
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}*/

	/* (non-Javadoc)
	 * @see com.newsfeeder.custom.CustomActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (drawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (getSupportFragmentManager().getBackStackEntryCount() > 1)
			{
				getSupportFragmentManager().popBackStackImmediate();
			}
			else
				finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
