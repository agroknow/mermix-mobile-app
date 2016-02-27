package com.mermix.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mermix.ApplicationVars;
import com.mermix.R;
import com.mermix.custom.CustomFragment;
import com.mermix.model.BookEquipment;
import com.mermix.model.Equipment;
import com.mermix.model.SQLiteNode;
import com.mermix.model.common.Address;
import com.mermix.model.common.Availability;
import com.mermix.model.common.Body;
import com.mermix.model.common.Pojo;
import com.mermix.model.sqlite.DrupalNodes;
import com.mermix.ui.DataRetrieve;
import com.mermix.ui.adapters.CustomPagerAdapter;
import com.mermix.utils.Common;
import com.mermix.utils.Constants;
import com.mermix.utils.ImageBitmapCacheMap;
import com.mermix.utils.MainService;
import com.mermix.utils.net.InfoWindowImageDownload;
import com.mermix.utils.net.args.NewEquipmentArgs;
import com.mermix.utils.net.args.UrlArgs;

import java.util.ArrayList;
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
public class EquipmentDetail extends CustomFragment implements DataRetrieve {
    /** The map view. */
    private MapView mMapView;

    private List<ImageView> dots;
    /** The Google map. */
    private GoogleMap mMap;

    private int equipmentId = -1;
    private Equipment equipment = null;
    private final int defaultEquipmentId = -1;
    private Boolean invokeRestApi = false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Common.log("EquipmentDetailActivity onCreateview");
        View v = inflater.inflate(R.layout.property_detail, null);

        setTouchNClick(v.findViewById(R.id.btnContact));

        setupMap(v, savedInstanceState);
        this.invokeRestApi = getActivity().getIntent().getExtras().getBoolean(Constants.INTENTVARS.INVOKERESTAPI);
        this.equipmentId = getActivity().getIntent().getIntExtra(Constants.INTENTVARS.EQUIPMENTID, defaultEquipmentId);

        if (this.equipmentId > defaultEquipmentId) {
            DrupalNodes drupalNodes = new DrupalNodes(getActivity().getApplicationContext());
            SQLiteNode node = drupalNodes.getNode(equipmentId);
            if (node == null)
                invokeRestApi = true;
            else {
                //equipment from SQLite
                equipment = node.toEquipment();
            }
        }

        if (invokeRestApi) {
            //invoke REST API
            //TODO startRequestService();
        } else {
            if (equipment == null)
                //equipment from intent parameters
                equipment = (Equipment) getActivity().getIntent().getSerializableExtra(Constants.INTENTVARS.EQUIPMENT);

            if (equipment == null) {
                Common.displayToast(getResources().getString(R.string.no_data), getActivity().getApplicationContext());
                return v;
            }

//            ImageView img = (ImageView) v.findViewById(R.id.img1);
//            if(Common.hasUrlFormat(equipment.getImage()))
//                img.setImageBitmap(new ImageBitmapCacheMap().getBitmap(equipment.getImage()));
//            else{
//                try {
//                    img.setImageBitmap(ImageUtils.configureBitmapSamplingRotation(equipment.getImage()));
//                } catch (IOException e) {
//                    //e.printStackTrace();
//                    Common.logError("IOException @ EquipmentDetailActivity.onCreate:" + e.getMessage());
//                }
//            }
            if (Constants.devMode) {
                TextView nid = (TextView) v.findViewById(R.id.nid);
                nid.setText(Integer.toString(equipment.getNid()));
                nid.setVisibility(View.VISIBLE);

                TextView nidHd = (TextView) v.findViewById(R.id.nidHd);
                nidHd.setVisibility(View.VISIBLE);
            }

            TextView price = (TextView) v.findViewById(R.id.multiprice);
            price.setText(equipment.getMultiPriceString2Display());

            if (Constants.devMode) {
                TextView available = (TextView) v.findViewById(R.id.available);
                TextView availablehd = (TextView) v.findViewById(R.id.availableHd);
                available.setVisibility(View.VISIBLE);
                availablehd.setVisibility(View.VISIBLE);
                List<Availability> availableList = equipment.getAvailability();
                if (availableList.size() > 0) {
                    available.setText(Integer.toString(availableList.get(0).getEnabled()));
                }
            }
            TextView location = (TextView) v.findViewById(R.id.lbl_location);
            if (equipment.getLocation() != null)
                location.setText(equipment.getLocation().getName());

            TextView title = (TextView) v.findViewById(R.id.title);
            title.setText(equipment.getTitle());

            TextView body = (TextView) v.findViewById(R.id.body);
            List<Body> bodyList = equipment.getBody();
            if (bodyList.size() > 0) {
                body.setText(Html.fromHtml(bodyList.get(0).getValue()));
            }
        }
        String[] imageUrl = equipment.getImage();
        int i ;
        Boolean ImageDownloadProcessing = false;
        for (i=0; i < equipment.getImage().length; i++){
            if (Common.hasUrlFormat(imageUrl[i])) {
                Bitmap cachedBitmap = new ImageBitmapCacheMap().getBitmap(imageUrl[i]);
                if (cachedBitmap == null) {
                    //popUpImage.setImageDrawable(null);
                    new InfoWindowImageDownload(null, null, this).execute(imageUrl);
                    ImageDownloadProcessing = true;
                    break;
                }
            }
        }
        if(!ImageDownloadProcessing)
            imagesDownloaded(v);
        return v;
    }

    public void imagesDownloaded(View v) {
		final CustomPagerAdapter mCustomPagerAdapter;
		ViewPager mViewPager;
        LinearLayout dotsLayout;

		LinearLayout img;
		if(v != null) {
			mCustomPagerAdapter = new CustomPagerAdapter(v.getContext(), equipment);
			mViewPager = (ViewPager) v.findViewById(R.id.pager);
			img = (LinearLayout) v.findViewById(R.id.progress);
            dotsLayout = (LinearLayout) v.findViewById(R.id.dots);
            
		}
		else{
			mCustomPagerAdapter = new CustomPagerAdapter(getActivity(),equipment);
			mViewPager = (ViewPager) getActivity().findViewById(R.id.pager);
			img = (LinearLayout) getActivity().findViewById(R.id.progress);
            dotsLayout = (LinearLayout) getActivity().findViewById(R.id.dots);

            dots = new ArrayList<>();
            for(int i = 0; i < mCustomPagerAdapter.getCount(); i++) {
                ImageView dot = new ImageView(getActivity());
                dot.setImageDrawable(getResources().getDrawable(i == 0 ? R.drawable.dot_active : R.drawable.dot_inactive));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                dotsLayout.addView(dot, params);

                dots.add(dot);
            }

		}
		if(img != null)
			img.setVisibility(View.GONE);

        mViewPager.setAdapter(mCustomPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectDot(position, mCustomPagerAdapter.getCount());

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

	}

    public void selectDot(int idx,int total) {
        Resources res = getResources();
        for(int i = 0; i < total; i++) {
            int drawableId = (i==idx)?(R.drawable.dot_active):(R.drawable.dot_inactive);
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
    }

	@Override
    public void onStart() {
        super.onStart();
        Common.log("EquipmentDetailActivity onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Common.log("EquipmentDetailActivity onStop");
    }
    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause()
    {
        Common.log("EquipmentDetailActivity onPause");
        mMapView.onPause();
        super.onPause();
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy()
    {
        Common.log("EquipmentDetailActivity onDestroy");
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
        Common.log("EquipmentDetailActivity onResume");
        mMapView.onResume();

        mMap = mMapView.getMap();
        if (mMap != null)
        {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setAllGesturesEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setInfoWindowAdapter(null);
            setupMarker();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btnContact) {
            // Creating alert Dialog with one Button
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            // Setting Dialog Title
            //alertDialog.setTitle(R.string.contact_agent);
            // Setting Dialog Message
            //alertDialog.setMessage(R.string.enter_phone);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.phone_dialog, null);
            alertDialog.setView(dialogView);

            /*final EditText input = new EditText(getActivity());
            input.setSingleLine(true);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            input.setLayoutParams(lp);

            alertDialog.setView(input);*/

            //alertDialog.setView(input);
            // Setting Icon to Dialog
            //alertDialog.setIcon(R.drawable.ic_launcher);
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton(R.string.submit,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //no code here
                            //must have valid input before dismiss dialog (http://stackoverflow.com/a/15619098)
                        }
                    });
            final AlertDialog dialog = alertDialog.create();
            /*dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.mermix_red));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
                }
            });*/
            dialog.show();
            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText input = (EditText) dialogView.findViewById(R.id.phone_number);
                    String regexStr = "^[+]?[0-9]{10,13}$";
                    String entered_number = input.getText().toString();

                    if (entered_number.matches(regexStr) == false) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.invalid_phone_msg, Toast.LENGTH_SHORT).show();
                    } else {
                        String uid = ApplicationVars.User.id == "" ? "0" : ApplicationVars.User.id;
                        String jsonString = "{\"nid\":\"" + equipment.getNid() + "\",\"tel\":\"" + input.getText() + "\",\"uid\":\"" + uid + "\"}";
                        Common.log(jsonString);
                        startRequestService(new NewEquipmentArgs(jsonString, true));
                        dialog.dismiss();
                    }
                }
            });
            // Setting Negative "NO" Button
//            alertDialog.setNegativeButton("NO",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // Write your code here to execute after dialog
//                            dialog.cancel();
//                        }
//                    });
            // closed
            // Showing Alert Message
            //alertDialog.show();
        }
    }

    /* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
    //    getMenuInflater().inflate(R.menu.feed, menu);
    //    menu.findItem(R.id.menu_sort).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Setup and initialize the Google map view.
     *
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
			MapsInitializer.initialize(this);
		} catch (GooglePlayServicesNotAvailableException e)
		{
			e.printStackTrace();
		}*/
        MapsInitializer.initialize(getActivity());
        mMapView = (MapView) v.findViewById(R.id.map);
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
        Common.log("EquipmentDetailActivity updateUI");
        try {
            //Equipment equipment = (Equipment) apiResponseData;
            BookEquipment response = (BookEquipment) apiResponseData;
            if(response != null) {
                Common.displayToast(response.getMessage(), getActivity().getApplicationContext());
            }
        }
        catch (ClassCastException e){
            Common.logError("ClassCastException @ EquipmentDetailActivity updateUI:" + e.getMessage());
        }
    }

    @Override
    public void startRequestService(UrlArgs urlArgs) {
        NewEquipmentArgs newEquipmentArgs = (NewEquipmentArgs) urlArgs;
        String queryString = newEquipmentArgs.getUrlArgs();
        Common.log("EquipmentDetailActivity startRequestService");
        //equipmentId = getActivity().getIntent().getIntExtra(Constants.INTENTVARS.EQUIPMENTID, defaultEquipmentId);
        //String apiUrl = Constants.APIENDPOINT + ApplicationVars.restApiLocale + "/" + Constants.URI.SINGLEEQUIPMENT.replace("NID", Integer.toString(equipmentId));
        String apiUrl = Constants.APIENDPOINT + ApplicationVars.restApiLocale + "/" + Constants.URI.BOOKEQUIPMENT + "?" + queryString;

        Intent i = new Intent(getActivity(), MainService.class);
        i.putExtra(Constants.INTENTVARS.APIURL, apiUrl);
        i.putExtra(Constants.INTENTVARS.POJOCLASS, Constants.PojoClass.BOOKEQUIPMENT);
        getActivity().startService(i);
    }
}
