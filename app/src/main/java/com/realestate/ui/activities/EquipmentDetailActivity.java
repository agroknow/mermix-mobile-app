package com.realestate.ui.activities;

import android.content.Intent;
import android.content.res.Resources;
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
import com.realestate.ApplicationVars;
import com.realestate.R;
import com.realestate.custom.CustomActivity;
import com.realestate.model.BookEquipment;
import com.realestate.model.Equipment;
import com.realestate.model.SQLiteNode;
import com.realestate.model.common.Address;
import com.realestate.model.common.Availability;
import com.realestate.model.common.Body;
import com.realestate.model.common.Pojo;
import com.realestate.model.sqlite.DrupalNodes;
import com.realestate.ui.DataRetrieve;
import com.realestate.ui.fragments.EquipmentDetail;
import com.realestate.ui.fragments.MapViewer;
import com.realestate.utils.ImageBitmapCacheMap;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.utils.ImageUtils;
import com.realestate.utils.MainService;
import com.realestate.utils.net.args.NewEquipmentArgs;
import com.realestate.utils.net.args.UrlArgs;

import java.io.IOException;
import java.util.List;


public class EquipmentDetailActivity extends CustomActivity {

	/* (non-Javadoc)
 * @see com.food.custom.CustomActivity#onCreate(android.os.Bundle)
 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Common.log("EquipmentDetail onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchresult);

		addFragment();
	}

	/**
	 * Attach the appropriate MapViewer fragment with activity.
	 */
	private void addFragment()
	{
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, new EquipmentDetail()).commit();
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

}
