package com.realestate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.realestate.R;
import com.realestate.custom.CustomActivity;
import com.realestate.model.Equipment;
import com.realestate.model.SQLiteNode;
import com.realestate.model.common.Address;
import com.realestate.model.common.Availability;
import com.realestate.model.common.Body;
import com.realestate.model.common.Pojo;
import com.realestate.model.sqlite.DrupalNodes;
import com.realestate.ui.DataRetrieve;
import com.realestate.utils.ImageBitmapCacheMap;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.utils.ImageUtils;
import com.realestate.utils.MainService;
import com.realestate.utils.net.args.UrlArgs;

import java.io.IOException;
import java.util.List;

/**
 * Created by teo on 7/9/2015.
 *
 * Display equipment node in detail.
 * To retrieve equipment details
 * either invoke REST API's request node/NID.json	(invokeRestApi = true, equipmentId != defaultEquipmentId)
 * or get equipment object from intent parameters	(invokeRestApi = false, equipmentId == defaultEquipmentId)
 * or get equipment object from SQLite				(invokeRestApi = false, equipmentId != defaultEquipmentId)
 */
public class EquipmentDetail extends CustomActivity implements DataRetrieve {
	/** The map view. */
	private MapView mMapView;

	/** The Google map. */
	private GoogleMap mMap;

    private int equipmentId = -1;
    private Equipment equipment = null;
    private final int defaultEquipmentId = -1;
    private Boolean invokeRestApi = false;


	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Common.log("EquipmentDetail onCreate");
		setContentView(R.layout.property_detail);
		setupMap(savedInstanceState);
		this.invokeRestApi = getIntent().getExtras().getBoolean(Constants.INTENTVARS.INVOKERESTAPI);
		this.equipmentId = getIntent().getIntExtra(Constants.INTENTVARS.EQUIPMENTID, defaultEquipmentId);

		if(this.equipmentId > defaultEquipmentId){
			DrupalNodes drupalNodes = new DrupalNodes(getApplicationContext());
			SQLiteNode node = drupalNodes.getNode(equipmentId);
			if(node == null)
				invokeRestApi = true;
			else{
				//equipment from SQLite
				equipment = node.toEquipment();
			}
		}

		if(invokeRestApi){
			//invoke REST API
			//TODO startRequestService();
		}
		else{
			if(equipment == null) {
				//equipment from intent parameters
				equipment = (Equipment) getIntent().getSerializableExtra(Constants.INTENTVARS.EQUIPMENT);
			}

			ImageView img = (ImageView) findViewById(R.id.img1);
			if(Common.hasUrlFormat(equipment.getImage()))
				img.setImageBitmap(new ImageBitmapCacheMap().getBitmap(equipment.getImage()));
			else{
				try {
					img.setImageBitmap(ImageUtils.configureBitmapSamplingRotation(equipment.getImage()));
				} catch (IOException e) {
					//e.printStackTrace();
					Common.logError("IOException @ EquipmentDetail.onCreate:" + e.getMessage());
				}
			}

			TextView nid = (TextView) findViewById(R.id.nid);
			nid.setText(Integer.toString(equipment.getNid()));

			TextView price = (TextView) findViewById(R.id.price);
			price.setText(Float.toString(equipment.getPrice()));

			TextView available = (TextView) findViewById(R.id.available);
			List<Availability> availableList = equipment.getAvailability();
			if(availableList.size() > 0) {
				available.setText(Integer.toString(availableList.get(0).getEnabled()));
			}

			TextView title = (TextView) findViewById(R.id.title);
			title.setText(equipment.getTitle());

			TextView body = (TextView) findViewById(R.id.body);
			List<Body> bodyList = equipment.getBody();
			if(bodyList.size() > 0) {
				body.setText(Html.fromHtml(bodyList.get(0).getValue()));
			}
		}
    }

	@Override
	protected void onStart() {
		super.onStart();
		Common.log("EquipmentDetail onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Common.log("EquipmentDetail onStop");
	}
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause()
	{
		Common.log("EquipmentDetail onPause");
		mMapView.onPause();
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy()
	{
		Common.log("EquipmentDetail onDestroy");
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
		Common.log("EquipmentDetail onResume");
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
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
        else if (item.getItemId() == R.id.menu_search) {
			Intent i = new Intent(this, MainActivity.class);
			i.putExtra(Constants.INTENTVARS.FRAGMENTPOS, 1);
			startActivity(i);
			finish();
			return true;
		}
			//TODO start MainActivity with intent variable the 'Search' fragment's position to start
        return super.onOptionsItemSelected(item);
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
	 * This method places a location marker on Map View for the current equipment.
	 */
	private void setupMarker()
	{
		List<Address> addressList = equipment.getAddress();
		if(addressList.size() > 0) {
			mMap.clear();
			LatLng l = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
			MarkerOptions opt = new MarkerOptions();
			opt.position(l);
				//.title("South Extenstion 324")
				//.snippet("Sydney, Australia");
			opt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));

			mMap.addMarker(opt);
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 12));
		}

	}

    @Override
    public void updateUI(Pojo apiResponseData) {
		Common.log("EquipmentDetail updateUI");
		try {
			Equipment equipment = (Equipment) apiResponseData;
		}
		catch (ClassCastException e){
			Common.logError("ClassCastException @ EquipmentDetail updateUI:" + e.getMessage());
		}
	}

	@Override
	public void startRequestService(UrlArgs urlArgs) {
		Common.log("EquipmentDetail startRequestService");
		equipmentId = getIntent().getIntExtra(Constants.INTENTVARS.EQUIPMENTID, defaultEquipmentId);
		String apiUrl = Constants.APIENDPOINT + Constants.URI.SINGLEEQUIPMENT.replace("NID", Integer.toString(equipmentId));
		Intent i = new Intent(this, MainService.class);
		i.putExtra(Constants.INTENTVARS.APIURL, apiUrl);
		i.putExtra(Constants.INTENTVARS.POJOCLASS, Constants.PojoClass.EQUIPMENT);
		this.startService(i);
	}
}
