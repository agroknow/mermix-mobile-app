package com.mermix.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.mermix.R;
import com.mermix.custom.CustomActivity;
import com.mermix.ui.fragments.MapViewer;
import com.mermix.utils.Common;

/**
 * The MapViewActivity is the activity class that shows Map fragment. This activity is
 * only created to show Back button on ActionBar.
 */
public class MapViewActivity extends CustomActivity
{
	/* (non-Javadoc)
	 * @see com.food.custom.CustomActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Common.log("MapViewActivity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().setTitle(getResources().getString(R.string.map));

		addFragment();
	}

	/**
	 * Attach the appropriate MapViewer fragment with activity.
	 */
	private void addFragment()
	{
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, new MapViewer()).commit();
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
