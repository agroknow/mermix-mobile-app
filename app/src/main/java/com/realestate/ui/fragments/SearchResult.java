package com.realestate.ui.fragments;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.realestate.R;
import com.realestate.custom.CustomFragment;
import com.realestate.model.Equipment;
import com.realestate.model.ListOfEquipments;
import com.realestate.model.common.Address;
import com.realestate.model.common.Pojo;
import com.realestate.ui.DataRetrieve;
import com.realestate.ui.activities.EquipmentDetail;
import com.realestate.ui.activities.MapViewActivity;
import com.realestate.ui.adapters.FeedAdapter;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.utils.MainService;
import com.realestate.utils.net.args.SearchArgs;
import com.realestate.utils.net.args.UrlArgs;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class SearchResult is the Fragment class that is launched when the user
 * clicks on Search option Search screen and it simply shows a dummy list of
 * Search results with a MapView that shows dummy locations for properties. You
 * can customize this to load and display actual search result listing.
 */
public class SearchResult extends CustomFragment implements DataRetrieve
{

	/** The search result list. */
	private ArrayList<String[]> sList;

	/** The Flag to hold whether to search properties for buy or for rent.. */
	private boolean rent;

	/** The map view. */
	private MapView mMapView;

	/** The Google map. */
	private GoogleMap mMap;

	private FeedAdapter adapter = new FeedAdapter();

	private ProgressDialog progress;
	private int sortSelection = 0;

	private static class SearchFilters{
		static String location = "";
		static String machineType = "";
		static String cultivation = "";
		static String contractType = "";
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		Common.log("SearchResult onCreateView");
		View v = inflater.inflate(R.layout.search_result, null);
		setHasOptionsMenu(true);
		Bundle bundle = getArguments();
		this.rent = (bundle != null);
		progress = new ProgressDialog(getActivity());
		progress.setTitle(getResources().getString(R.string.progress_dialog_title));
		progress.setMessage(getResources().getString(R.string.progress_dialog_search_msg));

		setupMap(v, savedInstanceState);
		setSearchResultList(v);
		if(bundle != null) {
			int locationTid = bundle.getInt(Constants.URI.PARAMS.LOCATION);
			int machineTypeTid = bundle.getInt(Constants.URI.PARAMS.MACHINETYPE);
			int cultivationTid = bundle.getInt(Constants.URI.PARAMS.CULTIVATION);
			int contractTypeTid = bundle.getInt(Constants.URI.PARAMS.CONTRACTTYPE);
			SearchFilters.location = (locationTid != Constants.SPINNERITEMS.ALLTERM.TID ? Integer.toString(locationTid) : "");
			SearchFilters.machineType = (machineTypeTid != Constants.SPINNERITEMS.ALLTERM.TID ? Integer.toString(machineTypeTid) : "");
			SearchFilters.cultivation = (cultivationTid != Constants.SPINNERITEMS.ALLTERM.TID ? Integer.toString(cultivationTid) : "");
			SearchFilters.contractType = (contractTypeTid != Constants.SPINNERITEMS.ALLTERM.TID ? Integer.toString(contractTypeTid) : "");

			startRequestService(new SearchArgs("apartment", SearchFilters.location, SearchFilters.machineType, SearchFilters.cultivation, SearchFilters.contractType, "", ""));
		}
		return v;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause()
	{
		Common.log("SearchResult onPause");
		mMapView.onPause();
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy()
	{
		Common.log("SearchResult onDestroy");
		mMapView.onDestroy();
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume()
	{
		Common.log("SearchResult onResume");
		super.onResume();
		mMapView.onResume();
		mMap = mMapView.getMap();
		if (mMap != null)
		{
			mMap.setMyLocationEnabled(false);
			mMap.setInfoWindowAdapter(null);
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
	 * DEPRECATED!!!!
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

	/**
	 * Setup and initialize the search result list view.
	 * 
	 * @param v
	 *            the root view
	 */
	private void setSearchResultList(View v) {
		ListView list = (ListView) v.findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {
				Equipment equipment = (Equipment) parent.getItemAtPosition(position);
				Intent i = new Intent(getActivity(), EquipmentDetail.class);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable(Constants.INTENTVARS.EQUIPMENT, equipment);
				i.putExtras(mBundle);
				i.putExtra(Constants.INTENTVARS.INVOKERESTAPI, false);
				startActivity(i);
			}
		});
	}

	@Override
	public void startRequestService(UrlArgs urlArgs) {
		SearchArgs args = (SearchArgs) urlArgs;
		String queryString = args.getUrlArgs();
		String apiUrl = Constants.APIENDPOINT + Constants.URI.LISTOFEQUIPMENTS +
						(!queryString.isEmpty() ? "?" + queryString : "" )+
						"";
			String pojoClass = Constants.PojoClass.LISTOFEQUIPMENTS;
			Intent i = new Intent(this.getActivity(), MainService.class);
			i.putExtra(Constants.INTENTVARS.APIURL, apiUrl);
			i.putExtra(Constants.INTENTVARS.POJOCLASS, pojoClass);
			this.getActivity().startService(i);
			progress.show();
	}

	@Override
	public void updateUI(Pojo apiResponseData) {
		Common.log("SearchResult updateUI");
		progress.dismiss();

		ListOfEquipments equipmentsList = (ListOfEquipments) apiResponseData;
		List<Equipment> equipments = equipmentsList.getEquipments();
		adapter.clear();
		int idx = 0;
		while(idx < equipments.size()){
			//Common.log(Integer.toString(idx)+". node title: " + equipments.get(idx).getTitle());
			//Common.log("1st node body: " + equipments.get(idx).getBody().getValue());
			adapter.addItem(equipments.get(idx));
			idx++;
		}
		adapter.notifyDataSetChanged();
		int visibility = (idx == 0) ? View.VISIBLE : View.GONE;
		getActivity().findViewById(R.id.no_results).setVisibility(visibility);
		setupMarkers(equipments);
	}

	private void setupMarkers(List<Equipment> equipments) {
		List<Address> addressList;
		MarkerOptions opt;
		LatLng l = null;
		mMap.clear();
		int idx = 0;
		while(idx < equipments.size()){
			addressList = equipments.get(idx).getAddress();
			if(addressList.size() > 0) {
				opt = new MarkerOptions();
				l = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
				opt.position(l);
						//.title("South Extenstion 324")
						//.snippet("Sydney, Australia");
				opt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
				mMap.addMarker(opt);
			}
			idx++;
		}
		if(l != null)
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 6));
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.feed, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.menu_sort)
			showSortPopup();
		else if (item.getItemId() == R.id.menu_locate)
			startActivity(new Intent(getActivity(), MapViewActivity.class));
		else if (item.getItemId() == R.id.menu_search)
			//startActivity(new Intent(getActivity(), SearchResultActivity.class));
			getActivity().finish();
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Shows a sort dialog that holds a list of various sort options for sorting
	 * the search results.
	 */
	private void showSortPopup()
	{
		Builder b = new Builder(getActivity());
		b.setTitle(R.string.sort_list_by);
		b.setSingleChoiceItems(R.array.arr_sort, sortSelection,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
						SearchArgs searchArgs = (which == 1) ?
								new SearchArgs("apartment", SearchFilters.location, SearchFilters.machineType, SearchFilters.cultivation, SearchFilters.contractType, Constants.SORTPROPERTIES.CREATED, Constants.DIRECTIONS.DESC):
								new SearchArgs("apartment", SearchFilters.location, SearchFilters.machineType, SearchFilters.cultivation, SearchFilters.contractType, "", "");
						sortSelection = which;
						startRequestService(searchArgs);
					}
				});
		b.create().show();
	}
}
