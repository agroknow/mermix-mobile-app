package com.realestate.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.realestate.R;
import com.realestate.custom.CustomFragment;
import com.realestate.model.EquipmentInView;
import com.realestate.model.ListOfNearbyEquipments;
import com.realestate.model.common.Pojo;
import com.realestate.ui.activities.SearchResultActivity;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.utils.DataRetrieveUI;
import com.realestate.utils.MainService;
import com.realestate.utils.net.args.MapViewArgs;
import com.realestate.utils.net.args.UrlArgs;

import java.util.List;

/**
 * The Class MapViewer is the Fragment class that is launched when the user
 * clicks on Map option in Left navigation drawer or when user tap on the Map
 * icon in action bar. It simply shows a Map View with a few dummy location
 * markers on map. You can customize this class to load and display actual
 * locations on map.
 * TODO on MapViewer load, display 10 equipments in nearby location
 * TODO on GMap Marker click, display equipment's title & image
 * TODO on GMap Marker's title/image click, start EquipmentDetail activity
 */
public class MapViewer extends CustomFragment implements DataRetrieveUI
{

	/** The map view. */
	private MapView mMapView;

	/** The Google map. */
	private GoogleMap mMap;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.map, null);
		setHasOptionsMenu(true);

		setTouchNClick(v.findViewById(R.id.btnSearch));
		//setupMap(v, savedInstanceState);
		startRequestService(new MapViewArgs("", ""));
		return v;
	}

	/* (non-Javadoc)
	 * @see com.realestate.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		if (v.getId() == R.id.btnSearch)
			startActivity(new Intent(getActivity(), SearchResultActivity.class));
	}

	/**
	 * Called to do initial creation of a fragment.  This is called after
	 * {@link #onAttach(Activity)} and before
	 * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
	 * <p/>
	 * <p>Note that this can be called while the fragment's activity is
	 * still in the process of being created.  As such, you can not rely
	 * on things like the activity's content view hierarchy being initialized
	 * at this point.  If you want to do work once the activity itself is
	 * created, see {@link #onActivityCreated(Bundle)}.
	 *
	 * @param savedInstanceState If the fragment is being re-created from
	 *                           a previous saved state, this is the state.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onPause()
		 */
	@Override
	public void onPause()
	{
		Common.log("MainActivity onPause");
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
			mMap.setMyLocationEnabled(true);
			mMap.setInfoWindowAdapter(null);
			setupMarker();
		}
	}

	/**
	 * Setup and initialize the Google map view.
	 * 
	 * @param v
	 *            the root view
	 * @param savedInstanceState
	 *            the saved instance state
	 */
	private void setupMap(View v, Bundle savedInstanceState)
	{
		/*
		//new api of google play services has changed and method MapsInitializer.initialize
		//no longer throws the exception GooglePlayServicesNotAvailableException
		try
		{
			MapsInitializer.initialize(getActivity());
		} catch (GooglePlayServicesNotAvailableException e)
		{
			e.printStackTrace();
		}*/
		MapsInitializer.initialize(getActivity());
		mMapView = (MapView) v.findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);

	}

	/**
	 * This method simply place a few dummy location markers on Map View. You can
	 * write your own logic for loading the locations and placing the marker for
	 * each location as per your need.
	 */
	private void setupMarker()
	{
		mMap.clear();
		LatLng l[] = { new LatLng(-33.89159150356934, 151.21157605201006),
				new LatLng(-33.89021413257428, 151.21306367218494),
				new LatLng(-33.89021413257428, 151.21709771454334),
				new LatLng(-33.890261446151726, 151.21967263519764) };
		for (LatLng ll : l)
		{
			MarkerOptions opt = new MarkerOptions();
			opt.position(ll).title("South Extenstion 324")
					.snippet("Sydney, Australia");
			opt.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.map_marker));

			mMap.addMarker(opt);
		}
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l[2], 15));
		/*mMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng l)
			{
				Log.e("LAT", l.latitude+","+l.longitude);
			}
		});*/
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.map, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * method
	 * to update UI with the REST API's retrieved data
	 *
	 * @param apiResponseData
	 */
	@Override
	public void updateUI(Pojo apiResponseData) {
		ListOfNearbyEquipments equipmentsList = (ListOfNearbyEquipments) apiResponseData;
		List<EquipmentInView> equipments = equipmentsList.getEquipments();
	}

	/**
	 * method
	 * to generate REST API url and
	 * to invoke startService
	 *
	 * @param urlArgs
	 */
	@Override
	public void startRequestService(UrlArgs urlArgs) {
		MapViewArgs args = (MapViewArgs) urlArgs;
		String apiUrl = Constants.APIENDPOINT + Constants.URI.LISTOFNEARBYEQUIPMENTS +
				"?" + args.getUrlArgs() +
				"";
		String pojoClass = Constants.PojoClass.LISTOFNEARBYEQUIPMENTS;

		Intent i = new Intent(this.getActivity(), MainService.class);
		i.putExtra(Constants.INTENTVARS.APIURL, apiUrl);
		i.putExtra(Constants.INTENTVARS.POJOCLASS, pojoClass);
		this.getActivity().startService(i);
	}
}
