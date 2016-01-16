package com.realestate.ui.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.realestate.R;
import com.realestate.custom.CustomActivity;
import com.realestate.custom.CustomFragment;
import com.realestate.model.EquipmentInView;
import com.realestate.model.SQLiteNode;
import com.realestate.model.common.Pojo;
import com.realestate.model.sqlite.DrupalNodes;
import com.realestate.ui.DataRetrieve;
import com.realestate.ui.activities.EquipmentDetail;
import com.realestate.ui.activities.MainActivity;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.utils.ImageBitmapCacheMap;
import com.realestate.utils.MainService;
import com.realestate.utils.net.InfoWindowImageDownload;
import com.realestate.utils.net.args.MapViewArgs;
import com.realestate.utils.net.args.UrlArgs;

import java.io.IOException;
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
 * On marker click, display InfoWindow with equipment's title & image.
 * On InfoWindow's click, start EquipmentDetail activity.
 * Markers Clustering.
 *
 * WORKFLOW
 * onCreate
 * 	if device's location provider is disabled then prompt user to enable it
 * onResume
 * 	if flag equipmentDetailActivityStart NOT SET
 * 		get device's enabled location provider and retrieve device's location, set coordsArgs and invoke REST API
 *		if coordsArgs not set (location provider is disabled) then zoom map on Greece
 * onClick (btn 'search near location')
 * 	set coordsArgs from inserted location and invoke REST API
 * onProviderEnabled
 * 	if location provider is enabled then onLocationChanged invoke REST API
 */
public class MapViewer extends CustomFragment implements DataRetrieve, LocationListener, GoogleMap.OnInfoWindowClickListener, ClusterManager.OnClusterClickListener<MapViewer.MarkersClusterItem>, ClusterManager.OnClusterInfoWindowClickListener<MapViewer.MarkersClusterItem>, ClusterManager.OnClusterItemClickListener<MapViewer.MarkersClusterItem>, ClusterManager.OnClusterItemInfoWindowClickListener<MapViewer.MarkersClusterItem> {

	/** The map view. */
	private MapView mMapView;
	/** The Google map. */
	private GoogleMap mMap;
	private int MapZoom;
	private ProgressDialog progress;
	private String[] coordsArgs = {"", ""};
	private double commonCoordsOffset = 0.01;
	private LocationManager locationManager;
	private boolean invokeAPIOnLocationChange;
	private boolean integrateMarkersClustering = true;
	private boolean equipmentDetailActivityStart;
	private ClusterManager<MarkersClusterItem> mClusterManager;
	private MarkersClusterItem currentMarkerClusterItem;
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

		if(Constants.devMode){
			EditText addressText = (EditText) v.findViewById(R.id.address);
			addressText.setText("λαμία");
		}
		else
			checkDeviceLocationService();
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
		invokeAPIOnLocationChange = false;
		equipmentDetailActivityStart = false;
		mMap = null;
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
		if(mMap == null) {
			mMap = mMapView.getMap();
			if (integrateMarkersClustering) {
				mClusterManager = new ClusterManager<MarkersClusterItem>(getActivity().getApplicationContext(), mMap);
				mMap.setOnCameraChangeListener(mClusterManager);
				mMap.setOnMarkerClickListener(mClusterManager);
				mMap.setOnInfoWindowClickListener(mClusterManager);
				mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

				mClusterManager.setOnClusterClickListener(this);
				mClusterManager.setOnClusterInfoWindowClickListener(this);
				mClusterManager.setOnClusterItemClickListener(this);
				mClusterManager.setOnClusterItemInfoWindowClickListener(this);

				mClusterManager.setRenderer(new MarkersClusterRenderer<MarkersClusterItem>(getActivity().getApplicationContext(), mMap, mClusterManager));
				mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MarkerInfoWindowAdapter(getActivity().getApplicationContext()));
				//mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(new MarkerInfoWindowAdapter(getActivity().getApplicationContext()));
			} else {
//			mMap.setMyLocationEnabled(true);
				mMap.setOnInfoWindowClickListener(this);
				mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(getActivity().getApplicationContext()));
			}
		}

		if(equipmentDetailActivityStart)
			equipmentDetailActivityStart = false;
		else{
			if (Constants.devMode) {
				Button btnSearch = (Button) getActivity().findViewById(R.id.btnSearch);
				btnSearch.performClick();
			} else {
				//getLastKnownLocation returns null for provider 'gps' if device in a building
				Location loc = locationManager.getLastKnownLocation(detectLocationProvider());
				if (loc != null) {
					onLocationChanged(loc);
					MapZoom = Constants.MAPZOOMS.COUNTY;
					startRequestService(new MapViewArgs(coordsArgs[0], coordsArgs[1]));
				}
				else if (coordsArgs[0].isEmpty() && coordsArgs[1].isEmpty() && ((CustomActivity) getActivity()).isRestApiAccessible()) {
					MapZoom = Constants.MAPZOOMS.COUNTRY;
					findCoordArgs("greece");
					focusOnCoordArgs();
					resetCoordArgs();
				} /*else {
					MapZoom = Constants.MAPZOOMS.COUNTY;
					startRequestService(new MapViewArgs(coordsArgs[0], coordsArgs[1]));
				}*/
			}
		}

		long minTime = 60 * 1000;	//ms
		float minDistance = 100;    //meters
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);

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
		invokeAPIOnLocationChange = false;
		progress.show();
//		mMap.clear();
		Intent i = new Intent(this.getActivity(), MainService.class);
		i.putExtra(Constants.INTENTVARS.APIURL, apiUrl);
		i.putExtra(Constants.INTENTVARS.POJOCLASS, pojoClass);
		this.getActivity().startService(i);
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
		}
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {
		Common.log("MapViewer onStatusChanged");
	}

	@Override
	public void onProviderEnabled(String s) {
		Common.log("MapViewer onProviderEnabled " + s);
		invokeAPIOnLocationChange = true;
	}

	@Override
	public void onProviderDisabled(String s) {
		Common.log("MapViewer onProviderDisabled " + s);
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Common.log("MapViewer onInfoWindowClick");
		Double[] dblCoords = {marker.getPosition().latitude, marker.getPosition().longitude};
		String coordinatesKey = Common.doubleArr2String(dblCoords, Constants.CONCATDELIMETER);
		startEquipmentDetailView(coordinatesKey);
	}

	@Override
	public boolean onClusterClick(Cluster<MarkersClusterItem> cluster) {
		Common.log("MapViewer onClusterClick");
		//mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom + 2f));

		LatLngBounds.Builder builder = LatLngBounds.builder();
		for (ClusterItem item : cluster.getItems()) {
			builder.include(item.getPosition());
		}
		final LatLngBounds bounds = builder.build();
		mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
		return true;
	}

	@Override
	public void onClusterInfoWindowClick(Cluster<MarkersClusterItem> cluster) {
		Common.log("MapViewer onClusterInfoWindowClick");
	}

	@Override
	public boolean onClusterItemClick(MarkersClusterItem markersClusterItem) {
		Common.log("MapViewer onClusterItemClick");
		currentMarkerClusterItem = markersClusterItem;
		return false;
	}

	@Override
	public void onClusterItemInfoWindowClick(MarkersClusterItem markersClusterItem) {
		Common.log("MapViewer onClusterItemInfoWindowClick");
		Double[] dblCoords = {markersClusterItem.getPosition().latitude, markersClusterItem.getPosition().longitude};
		String coordinatesKey = Common.doubleArr2String(dblCoords, Constants.CONCATDELIMETER);
		startEquipmentDetailView(coordinatesKey);
	}

	private void startEquipmentDetailView(String coordinatesKey) {
		if (markersOnMap.containsKey(coordinatesKey)) {
			String nid = markersOnMap.get(coordinatesKey);
			equipmentDetailActivityStart = true;
			Intent i = new Intent(getActivity(), EquipmentDetail.class);
			i.putExtra(Constants.INTENTVARS.EQUIPMENTID, Integer.parseInt(nid));
			startActivity(i);
		} else
			Common.logError("EquipmentDetail failed to start because coordinates " + coordinatesKey + " DO NOT exist in HashMap markersOnMap");
	}

	private void checkDeviceLocationService() {
		Common.log("MapViewer checkDeviceLocationService");
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
							startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							invokeAPIOnLocationChange = true;
						}
					}
			);
			dialog.setNegativeButton(getString(R.string.later),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
						}
					}
			);
			dialog.show();
		}
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

	private void findCoordArgs(String loc){
		Common.log("MapViewer findCoordArgs");
		Geocoder gc = new Geocoder(getActivity().getApplicationContext());
		List<Address> list = null;
		Address addressLocation;
		try {
			list = gc.getFromLocationName(loc, 1);
			for(int idx=0;idx<list.size();idx++){
				addressLocation = list.get(idx);
				coordsArgs[0] = String.valueOf(addressLocation.getLatitude());
				coordsArgs[1] = String.valueOf(addressLocation.getLongitude());
			}
		} catch (IOException e) {
			Common.logError("MapViewer findCoordArgs IOException: "+e.getMessage());
			//e.printStackTrace();
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
		Common.log("MapViewer setupMap");
		MapsInitializer.initialize(getActivity());
		mMapView = (MapView) v.findViewById(R.id.map);
		//TODO mMapView.onCreate causes APP to freeze for about a second
		//http://stackoverflow.com/questions/26265526/what-makes-my-map-fragment-loading-slow
		//http://stackoverflow.com/questions/26178212/first-launch-of-activity-with-google-maps-is-very-slow
		mMapView.onCreate(savedInstanceState);
		markersOnMap = new HashMap<String, String>();
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
		String equipmentNid = equipment.getNid();
		Double[] dblCoords = equipment.getCoordinates();
		Boolean addMarker = true;
		String coords = Common.doubleArr2String(dblCoords, Constants.CONCATDELIMETER);
		LatLng l = new LatLng(dblCoords[0], dblCoords[1]);

		if(markersOnMap.containsKey(coords)){
			if(markersOnMap.get(coords).equals(equipmentNid) || isEquipmentOnMap(equipmentNid)) {
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
			Common.log("MapViewer setupMarker add marker with coordinates: " + coords + " of equipment: " + equipmentNid);
			markersOnMap.put(coords, equipmentNid);
			if(integrateMarkersClustering){
				MarkersClusterItem offsetItem = new MarkersClusterItem(l, equipment.getTitle());
				this.mClusterManager.addItem(offsetItem);
				this.mClusterManager.cluster();
			} else {
				MarkerOptions opt = new MarkerOptions();
				opt.position(l);
				opt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
				opt.title(equipment.getTitle());
				//opt.snippet(equipment.getBody());
				mMap.addMarker(opt);
				//mMap.setOnMarkerClickListener(this);
			}
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

	public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

		private Marker iwMarker;
		private Context iwContext;

		public MarkerInfoWindowAdapter(Context context) {
			iwContext = context;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			return null;
		}

		@Override
		public View getInfoContents(Marker marker) {
			iwMarker = marker;
			LayoutInflater inflater = (LayoutInflater) iwContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// Getting view from the layout file info_window_layout
			View popUp = inflater.inflate(R.layout.info_window, null);
			TextView popUpTitle = (TextView) popUp.findViewById(R.id.iw_title);
			//TextView popUpContent = (TextView) popUp.findViewById(R.id.popup_content);
			ImageView popUpImage = (ImageView) popUp.findViewById(R.id.iw_img);

			if(integrateMarkersClustering) {
				popUpTitle.setText(currentMarkerClusterItem.getTitle());
			}
			else {
				popUpTitle.setText(marker.getTitle());
				//popUpContent.setText(marker.getSnippet());
			}

			Double[] dblCoords = {marker.getPosition().latitude, marker.getPosition().longitude};
			String coordinatesKey = Common.doubleArr2String(dblCoords, Constants.CONCATDELIMETER);
			int equipmentId = Integer.parseInt(markersOnMap.get(coordinatesKey));
			DrupalNodes drupalNodes = new DrupalNodes(iwContext);
			SQLiteNode node = drupalNodes.getNode(equipmentId);
			String imageUrl = node.getImage(0);
			Bitmap cachedBitmap = new ImageBitmapCacheMap().getBitmap(imageUrl);
			if (cachedBitmap == null) {
				popUpImage.setImageDrawable(null);
				new InfoWindowImageDownload(popUpImage, iwMarker).execute(imageUrl);
			} else {
				popUpImage.setImageBitmap(cachedBitmap);
			}

			return popUp;
		}
	}

	public class MarkersClusterItem implements ClusterItem{
		private final LatLng mPosition;
		private final String title;

		public MarkersClusterItem(LatLng l, String title) {
			mPosition = l;
			this.title = title;
		}
		public String getTitle(){
			return this.title;
		}

		@Override
		public LatLng getPosition() {
			return mPosition;
		}
	}

	public class MarkersClusterRenderer<T extends ClusterItem> extends DefaultClusterRenderer<T>{

		public MarkersClusterRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager) {
			super(context, map, clusterManager);
		}

		@Override
		protected boolean shouldRenderAsCluster(Cluster<T> cluster) {
			//return super.shouldRenderAsCluster(cluster);
			return cluster.getSize() > 1;
		}
	}


}
