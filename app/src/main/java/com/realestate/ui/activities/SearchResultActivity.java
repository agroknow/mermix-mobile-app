package com.realestate.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.realestate.R;
import com.realestate.custom.CustomActivity;
import com.realestate.custom.CustomFragment;
import com.realestate.ui.fragments.SearchResult;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;

/**
 * The SearchResultActivity is the activity class that shows a dummy list of
 * property search results. You need to write your own code to load and display
 * actual search results.
 */
public class SearchResultActivity extends CustomActivity
{
	/* (non-Javadoc)
	 * @see com.food.custom.CustomActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Common.log("SearchResultActivity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().setTitle("Search Results");

		addFragment();
	}

	/**
	 * Dispatch onStart() to all fragments.  Ensure any created loaders are
	 * now started.
	 */
	@Override
	protected void onStart() {
		Common.log("SearchResultActivity onStart");
		super.onStart();
	}

	/**
	 * Dispatch onStop() to all fragments.  Ensure all loaders are stopped.
	 */
	@Override
	protected void onStop() {
		Common.log("SearchResultActivity onStop");
		super.onStop();
	}

	/**
	 * Dispatch onPause() to fragments.
	 */
	@Override
	protected void onPause() {
		Common.log("SearchResultActivity onPause");
		super.onPause();
	}

	/**
	 * Destroy all fragments and loaders.
	 */
	@Override
	protected void onDestroy() {
		Common.log("SearchResultActivity onDestroy");
		super.onDestroy();
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
		super.onResume();
	}

	/**
	 * Attach the appropriate SearchResult fragment with activity and also
	 * passes the bundle with fragment based on the 'buy' parameter in Intent to
	 * differentiate between Buy and Rent result listing. You may need to pass
	 * additional parameters based on your needs.
	 *
	 * more on bundles @
	 * http://stackoverflow.com/a/7149906
	 * http://developer.android.com/reference/android/os/Bundle.html
	 */
	private void addFragment()
	{
		CustomFragment f = new SearchResult();
		Bundle bundle = new Bundle();
		int locationTid = getIntent().getExtras().getInt(Constants.URI.PARAMS.LOCATION);
		int machineTypeTid = getIntent().getExtras().getInt(Constants.URI.PARAMS.MACHINETYPE);
		int cultivationTid = getIntent().getExtras().getInt(Constants.URI.PARAMS.CULTIVATION);
		int contractTYpeTid = getIntent().getExtras().getInt(Constants.URI.PARAMS.CONTRACTTYPE);

		bundle.putInt(Constants.URI.PARAMS.LOCATION, locationTid);
		bundle.putInt(Constants.URI.PARAMS.MACHINETYPE, machineTypeTid);
		bundle.putInt(Constants.URI.PARAMS.CULTIVATION, cultivationTid);
		bundle.putInt(Constants.URI.PARAMS.CONTRACTTYPE, contractTYpeTid);

		f.setArguments(bundle);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, f).commit();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home)
		{
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
