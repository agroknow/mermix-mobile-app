package com.realestate.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.realestate.R;
import com.realestate.custom.CustomActivity;

/**
 * DEPRECATED
 * The Class PropertyDetail is the Activity class that is launched when the user
 * clicks on an item in Feed list or in Search results list. It simply shows
 * dummy details of a property with dummy image of property and also includes a
 * Google Map view. You need to write your own code to load and display actual
 * contents.
 */
public class PropertyDetail extends CustomActivity
{

	/** The map view. */
	private MapView mMapView;

	/** The Google map. */
	private GoogleMap mMap;

	/* (non-Javadoc)
	 * @see com.realestate.custom.CustomActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.property_detail);

		setupMap(savedInstanceState);

		setTouchNClick(R.id.btnContact);

	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause()
	{
		mMapView.onPause();
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy()
	{
		mMapView.onDestroy();
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume()
	{
		super.onResume();
		mMapView.onResume();

		mMap = mMapView.getMap();
		if (mMap != null)
		{
			mMap.setMyLocationEnabled(false);
			mMap.getUiSettings().setAllGesturesEnabled(false);
			mMap.setInfoWindowAdapter(null);
			setupMarker();
		}
	}

	/**
	 * Setup and initialize the Google map view.
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 */
	private void setupMap(Bundle savedInstanceState)
	{
		/*
		//new api of google play services has changed and method MapsInitializer.initialize
		//no longer throws the exception GooglePlayServicesNotAvailableException
		try
		{
			MapsInitializer.initialize(this);
		} catch (GooglePlayServicesNotAvailableException e)
		{
			e.printStackTrace();
		}*/
		MapsInitializer.initialize(this);
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);

	}

	/**
	 * This method simply place a dummy location marker on Map View. You can
	 * write your own logic for loading the locations and placing the marker for
	 * each location as per your need.
	 */
	private void setupMarker()
	{
		mMap.clear();
		LatLng l = new LatLng(-33.8600, 151.2111);
		MarkerOptions opt = new MarkerOptions();
		opt.position(l).title("South Extenstion 324")
				.snippet("Sydney, Australia");
		opt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));

		mMap.addMarker(opt);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 16));

	}

	/* (non-Javadoc)
	 * @see com.socialshare.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		if (v.getId() == R.id.btnContact)
		{
			startActivity(new Intent(Intent.ACTION_DIAL,
					Uri.parse("tel:0123456789")));
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.feed, menu);
		menu.findItem(R.id.menu_sort).setVisible(false);
		return super.onCreateOptionsMenu(menu);
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
		else if (item.getItemId() == R.id.menu_locate)
			startActivity(new Intent(this, MapViewActivity.class));
		else if (item.getItemId() == R.id.menu_search)
			startActivity(new Intent(this, SearchResultActivity.class));
		return super.onOptionsItemSelected(item);
	}
}
