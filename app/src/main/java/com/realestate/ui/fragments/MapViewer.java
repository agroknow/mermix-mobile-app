package com.realestate.ui.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.realestate.R;
import com.realestate.custom.CustomFragment;
import com.realestate.model.EquipmentInView;
import com.realestate.model.SQLiteNode;
import com.realestate.model.common.Pojo;
import com.realestate.model.sqlite.DrupalNodes;
import com.realestate.ui.DataRetrieve;
import com.realestate.ui.activities.EquipmentDetail;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.utils.MainService;
import com.realestate.utils.net.args.MapViewArgs;
import com.realestate.utils.net.args.UrlArgs;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
 * Get current location coordinates (from GPS or network locationProvider) and invoke REST API.
 * In order to display markers with identical coordinates add commonCoordsOffset to longitude value of one of them.
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
 * implement markers clustering
 * on marker click, display equipment's title & image
 * on marker's title/image click, start EquipmentDetail activity
 */
public class MapViewer extends CustomFragment implements DataRetrieve, LocationListener, GoogleMap.OnMarkerClickListener
{

	/** The map view. */
	private MapView mMapView;

	/** The Google map. */
	private GoogleMap mMap;

	private int MapZoom;
	private ProgressDialog progress;
	private String[] coordsArgs = {"", ""};
	private double commonCoordsOffset = 0.02;
	private LocationManager locationManager;
	private boolean invokeAPIOnLocationChange = false;
	private HashMap<String, String> markersOnMap;

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

		if(!Constants.devMode)
			checkDeviceLocationService();
		else{
			EditText addressText = (EditText) v.findViewById(R.id.address);
			addressText.setText("larisa");
		}

		return v;
	}

	private void checkDeviceLocationService() {
		Boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		Boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if(!isGPSEnabled && !isNetworkEnabled){
			/*
			display dialog to prompt user to enable location detection services
			 */
			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setMessage(getResources().getString(R.string.enable_location_detect_service));
			dialog.setPositiveButton(getResources().getString(R.string.enable),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							// TODO Auto-generated method stub
							startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							invokeAPIOnLocationChange = true;
						}
					}
			);
			dialog.setNegativeButton(getString(R.string.cancel),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							// TODO Auto-generated method stub
						}
					}
			);
			dialog.show();
		}
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
			if(!location.isEmpty()) {
				findCoordArgs(location);
				MapZoom = Constants.MAPZOOMS.COUNTY;
				Common.hideSoftKeyboard(getActivity());
				startRequestService(new MapViewArgs(coordsArgs[0], coordsArgs[1]));
			}
			else{
				Common.displayToast(getResources().getString(R.string.enter_address), getActivity().getApplicationContext());
			}
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
	}

	private String detectLocationProvider() {
		Common.log("MapViewer detectLocationProvider");
		String provider;
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		Boolean getEnabledProvider = true;
		//with criteria ACCURACY_COARSE, provider 'network' is selected
		//with default criteria or ACCURACY_FINE, provider 'gps' is selected
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
	public void onResume() {
		Common.log("MapViewer onResume");
		super.onResume();
		mMapView.onResume();
		mMap = mMapView.getMap();
		if (mMap != null){
			//mMap.setMyLocationEnabled(true);
			mMap.setInfoWindowAdapter(null);
//		mMap.setOnMapClickListener(new OnMapClickListener() {
//			@Override
//			public void onMapClick(LatLng l){
//				Log.e("LAT", l.latitude+","+l.longitude);
//			}
//		});
			mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {
					Common.log("MapViewer onInfoWindowClick");
					Double[] dblCoords = {marker.getPosition().latitude, marker.getPosition().longitude};
					String coordinatesKey = Common.doubleArr2String(dblCoords, Constants.CONCATDELIMETER);
					if(markersOnMap.containsKey(coordinatesKey)) {
						String nid = markersOnMap.get(coordinatesKey);
						Intent i = new Intent(getActivity(), EquipmentDetail.class);
						i.putExtra(Constants.INTENTVARS.EQUIPMENTID, Integer.parseInt(nid));
						startActivity(i);
					}
					else
						Common.logError("coordinates "+coordinatesKey+" DO NOT exist in HashMap markersOnMap");
				}
			});

			if(Constants.devMode){
				Button btnSearch = (Button) getActivity().findViewById(R.id.btnSearch);
				btnSearch.performClick();
			}
			else {
				//getLastKnownLocation returns null for provider 'gps' if device in a building
				Location loc = locationManager.getLastKnownLocation(detectLocationProvider());
				if (loc != null) {
					onLocationChanged(loc);
				}
				if (coordsArgs[0].isEmpty() && coordsArgs[1].isEmpty()) {
					MapZoom = Constants.MAPZOOMS.COUNTRY;
					findCoordArgs("greece");
					focusOnCoordArgs();
					resetCoordArgs();
				} else {
					MapZoom = Constants.MAPZOOMS.COUNTY;
					startRequestService(new MapViewArgs(coordsArgs[0], coordsArgs[1]));
				}
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
		MapsInitializer.initialize(getActivity());
		mMapView = (MapView) v.findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		markersOnMap = new HashMap<String, String>();
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
		Common.log("MapViewer updateUI");
		progress.dismiss();
		if(apiResponseData != null) {
			try {
				EquipmentInView equipment = (EquipmentInView) apiResponseData;
				setupMarker(equipment);
				DrupalNodes drupalNodes = new DrupalNodes(getActivity().getApplicationContext());
				if(drupalNodes.getNode(Integer.parseInt(equipment.getNid())) == null) {
					Double[] dblCoords = equipment.getCoordinates();
					drupalNodes.insertNode(
							new SQLiteNode(Integer.parseInt(equipment.getNid()),
									equipment.getTitle(),
									equipment.getBody(),
									dblCoords,
									Common.concatString(equipment.getImages(), Constants.CONCATDELIMETER),
									(float) -1));
				}
				else
					Common.log("equipment "+equipment.getNid()+" already exists in sqlite");
			}
			catch (ClassCastException e){
				Common.logError("ClassCastException @ MapViewer updateUI:" + e.getMessage());
			}
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

	/**
	 * Add marker on Map.
	 * If equipment already exists in HashMap markersOnMap, then no new marker is added and map focuses on existent marker.
	 * If another equipment with identical coordinates exists in HashMap markersOnMap, then offset is added to longitude value of current equipment's coordinates
	 * before adding marker to map.
	 *
	 * @param equipment
	 */
	private void setupMarker(EquipmentInView equipment) {
		MarkerOptions opt =  new MarkerOptions();
		String equipmentNid = equipment.getNid();
		Double[] dblCoords = equipment.getCoordinates();
		Boolean addMarker = true;
		String coords = Common.doubleArr2String(dblCoords, Constants.CONCATDELIMETER);
		LatLng l = new LatLng(dblCoords[0], dblCoords[1]);

		if(markersOnMap.containsKey(coords)){
			if(markersOnMap.get(coords).equals(equipmentNid)) {
				addMarker = false;
			}
			else{
				Common.log("MapViewer setupMarker for equipments with identical coordinates, "+ markersOnMap.get(coords) +" & "+ equipmentNid +" , add offset in lng value");
				dblCoords = calcNewCoordinates(dblCoords);
				coords = Common.doubleArr2String(dblCoords, Constants.CONCATDELIMETER);
				l = new LatLng(dblCoords[0], dblCoords[1]);
			}
		}
		else if(isEquipmentOnMap(equipmentNid)){
			addMarker = false;
		}

		if(addMarker){
			Common.log("MapViewer setupMarker add marker with coordinates: "+coords+" of equipment: "+equipmentNid);
			opt.position(l);
			opt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
			opt.title(equipment.getTitle());
			opt.snippet(equipment.getBody());
			markersOnMap.put(coords, equipmentNid);
			mMap.setOnMarkerClickListener(this);
			mMap.addMarker(opt);
		}
		else{
			Common.log("MapViewer setupMarker marker of equipment " + equipmentNid + " already exists in map");
		}
		if(l != null)
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, MapZoom));
	}

	private boolean isEquipmentOnMap(String equipmentNid) {
		Iterator it = markersOnMap.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry)it.next();
			if(pair.getValue().equals(equipmentNid))
				return true;
		}
		return false;
	}

	/**custom
	 * Calculates new coordinates if equipments with identical ones exist.
	 * If new coordinates already exist in HashMap, then new calculation is executed with recursive call.
	 * @param dblCoords
	 * @return
	 */
	private Double[] calcNewCoordinates(Double[] dblCoords) {
		String coordinates;
		dblCoords[1] = Common.addDoubleValues(dblCoords[1], commonCoordsOffset);
		coordinates = Common.doubleArr2String(dblCoords, Constants.CONCATDELIMETER);
		if(markersOnMap.containsKey(coordinates)){
			dblCoords = calcNewCoordinates(dblCoords);
		}
		return dblCoords;
	}

	@Override
	public void onLocationChanged(Location loc) {
		Common.log("MapViewer onLocationChanged");
		coordsArgs[0] = String.valueOf(loc.getLatitude());
		coordsArgs[1] = String.valueOf(loc.getLongitude());
		if(invokeAPIOnLocationChange){
			Common.log("invokeAPIOnLocationChange");
			MapZoom = Constants.MAPZOOMS.COUNTY;
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

	@Override
	/**
	 * implements custom behaviour on marker click
	 * the default behaviour is to display title & snippet which are set when adding the marker
	 */
	public boolean onMarkerClick(Marker marker) {
		//Common.displayToast("click marker on position " + marker.getPosition(), getActivity().getApplicationContext());
		//marker.getPosition().latitude
		//marker.getPosition().longitude
		return false;
	}
}
