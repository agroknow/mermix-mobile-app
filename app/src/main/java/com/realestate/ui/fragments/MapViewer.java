package com.realestate.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
import com.realestate.model.common.Pojo;
import com.realestate.ui.DataRetrieve;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.utils.MainService;
import com.realestate.utils.net.args.MapViewArgs;
import com.realestate.utils.net.args.UrlArgs;

import java.io.IOException;
import java.util.List;

/**
 * The Class MapViewer is the Fragment class that is launched when the user
 * clicks on Map option in Left navigation drawer or when user tap on the Map
 * icon in action bar. It simply shows a Map View with a few dummy location
 * markers on map. You can customize this class to load and display actual
 * locations on map.
 *
 * FEATURES
 * Allows user to insert address and search for equipments near current address
 * (convert address to coordinates and invoke REST API).
 * Get current location coordinates (from GPS or network locationProvider) and invoke REST API
 *
 * WORKFLOW
 * onCreate
 * 	if device's location provider is disabled then prompt user to enable it
 * onResume
 * 	get device's enabled location provider and retrieve device's location (set coordsArgs)
 *	if coordsArgs not set then zoom map on Greece
 *	if coordsArgs set then invoke REST API
 * onClick (btn 'search near location')
 * 	set coordsArgs from inserted location and invoke REST API
 * onProviderEnabled
 * 	if location provider is enabled then onLocationChanged invoke REST API
 *
 * TODO
 * prompt user to enable device's location provider with message in greek
 * to display markers with identical coordinates add commonCoordsOffset to longitude value of one of them
 * on GMap Marker click, display equipment's title & image
 * on GMap Marker's title/image click, start EquipmentDetail activity
 */
public class MapViewer extends CustomFragment implements DataRetrieve, LocationListener
{

	/** The map view. */
	private MapView mMapView;

	/** The Google map. */
	private GoogleMap mMap;

	private int MapZoom = 6;
	private ProgressDialog progress;
	private String[] coordsArgs = {"", ""};
	private double commonCoordsOffset = 0.04;
	private LocationManager locationManager;
	private boolean invokeAPIOnLocationChange = false;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		Common.log("MapViewer onCreateView");
		View v = inflater.inflate(R.layout.map, null);
		setHasOptionsMenu(true);

		progress = new ProgressDialog(getActivity());
		progress.setTitle(getResources().getString(R.string.progress_dialog_title));
		progress.setMessage(getResources().getString(R.string.progress_dialog_search_map_msg));
		setTouchNClick(v.findViewById(R.id.btnSearch));
		setupMap(v, savedInstanceState);
		return v;
	}

	/* (non-Javadoc)
	 * @see com.realestate.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		Common.log("MapViewer onClick");
		super.onClick(v);
		if (v.getId() == R.id.btnSearch){
			String location;
			EditText addressTxtBox = (EditText) getActivity().findViewById(R.id.address);
			location = addressTxtBox.getText().toString();
			findCoordArgs(location);
			MapZoom = 10;
			Common.hideSoftKeyboard(getActivity());
			startRequestService(new MapViewArgs(coordsArgs[0], coordsArgs[1]));
		}
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
		Common.log("MapViewer onCreate");
		super.onCreate(savedInstanceState);

		locationManager = (LocationManager) getActivity().getSystemService(getActivity().getApplicationContext().LOCATION_SERVICE);

		Boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		Boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if(!isGPSEnabled && !isNetworkEnabled){
		}
	}

	private String detectLocationProvider() {
		Common.log("MapViewer detectLocationProvider");
		String provider;
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		Boolean getEnabledProvider = true;
		//with default criteria, provider 'gps' is selected
		//with criteria: POWER_LOW & ACCURACY_COARSE, provider 'network' is selected
		provider = locationManager.getBestProvider(criteria, getEnabledProvider);
		Common.log("locationProvider " + provider);
		return provider;
	}

	/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onPause()
		 */
	@Override
	public void onPause()
	{
		Common.log("MapViewer onPause");
		mMapView.onPause();
		super.onPause();

		locationManager.removeUpdates(this);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy()
	{
		Common.log("MapViewer onDestroy");
		mMapView.onDestroy();
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume()
	{
		Common.log("MapViewer onResume");
		super.onResume();
		mMapView.onResume();
		mMap = mMapView.getMap();
		if (mMap != null){
			//mMap.setMyLocationEnabled(true);
			mMap.setInfoWindowAdapter(null);

			//getLastKnownLocation returns null for provider 'gps' if device in a building
			Location loc = locationManager.getLastKnownLocation(detectLocationProvider());
			if(loc != null){
				onLocationChanged(loc);
			}
			if(coordsArgs[0].isEmpty() && coordsArgs[1].isEmpty()){
				MapZoom = 6;
				findCoordArgs("greece");
				focusOnCoordArgs();
				resetCoordArgs();
			}
			else{
				MapZoom = 10;
				startRequestService(new MapViewArgs(coordsArgs[0], coordsArgs[1]));
			}
			long minTime = 60 * 1000;	//ms
			float minDistance = 100;    //meters
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);
		}
	}

	private void findCoordArgs(String loc){
		Geocoder gc = new Geocoder(getActivity().getApplicationContext());
		List<Address> list = null;
		Address addressLocation;
		try {
			list = gc.getFromLocationName(loc, 1);
		} catch (IOException e) {
			Common.logError("MapViewer findCoordArgs IOException: "+e.getMessage());
			//e.printStackTrace();
		}
		for(int idx=0;idx<list.size();idx++){
			addressLocation = list.get(idx);
			coordsArgs[0] = String.valueOf(addressLocation.getLatitude());
			coordsArgs[1] = String.valueOf(addressLocation.getLongitude());
		}
	}

	private void focusOnCoordArgs() {
		if(!coordsArgs[0].isEmpty() && !coordsArgs[1].isEmpty()) {
			LatLng l = new LatLng(Double.parseDouble(coordsArgs[0]), Double.parseDouble(coordsArgs[1]));
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, MapZoom));
		}
	}

	private void resetCoordArgs(){
		coordsArgs[0] = "";
		coordsArgs[1] = "";
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
		progress.dismiss();
		if(apiResponseData != null) {
			setupMarker((EquipmentInView) apiResponseData);
		}
		else{
			focusOnCoordArgs();
			resetCoordArgs();
			Common.displayToast(getResources().getString(R.string.no_results), getActivity().getApplicationContext());
		}
	}

	/**
	 * method
	 * to generate REST API urlimplements Location and
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
		String pojoClass = Constants.PojoClass.EQUIPMENTINVIEW;
		progress.show();
//		mMap.clear();
		Intent i = new Intent(this.getActivity(), MainService.class);
		i.putExtra(Constants.INTENTVARS.APIURL, apiUrl);
		i.putExtra(Constants.INTENTVARS.POJOCLASS, pojoClass);
		this.getActivity().startService(i);
	}

	private void setupMarker(EquipmentInView equipment) {
		String[] coordinates;
		MarkerOptions opt;
		LatLng l = null;
		coordinates = equipment.getCoordinates().split(",");
		opt = new MarkerOptions();
		//Double.parseDouble(coordinates[1]) + commonCoordsOffset
		l = new LatLng(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
		opt.position(l);
		opt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
		mMap.addMarker(opt);
		if(l != null)
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, MapZoom));
//		mMap.setOnMapClickListener(new OnMapClickListener() {
//
//			@Override
//			public void onMapClick(LatLng l)
//			{
//				Log.e("LAT", l.latitude+","+l.longitude);
//			}
//		});
	}

	@Override
	public void onLocationChanged(Location loc) {
		Common.log("MapViewer onLocationChanged");
		coordsArgs[0] = String.valueOf(loc.getLatitude());
		coordsArgs[1] = String.valueOf(loc.getLongitude());
		if(invokeAPIOnLocationChange){
			Common.log("invokeAPIOnLocationChange");
			MapZoom = 10;
			startRequestService(new MapViewArgs(coordsArgs[0], coordsArgs[1]));
			invokeAPIOnLocationChange = false;
		}
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {
		Common.log("MapViewer onStatusChanged");
	}

	@Override
	public void onProviderEnabled(String s) {
		Common.log("MapViewer onProviderEnabled "+s);
		invokeAPIOnLocationChange = true;
	}

	@Override
	public void onProviderDisabled(String s) {
		Common.log("MapViewer onProviderDisabled "+s);
	}
}
