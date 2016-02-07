package com.realestate.utils;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.realestate.utils.Common;

import java.io.IOException;
import java.util.List;

/**
 * Created on 06/02/2016
 * Description:
 */
public class LocationUtils {

	/**
	 * get location provider to use
	 * @param locationManager
	 * @return String provider
	 */
	public static String detectLocationProvider(LocationManager locationManager) {
		Common.log("LocationUtils detectLocationProvider");
		String provider;
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		Boolean getEnabledProvider = true;
		//with criteria ACCURACY_COARSE, provider 'network' is selected
		//with default criteria or ACCURACY_FINE, provider 'gps' is selected
		provider = locationManager.getBestProvider(criteria, getEnabledProvider);
		return provider;
	}

	/**
	 * uses Geocoder to get coordinates for location string
	 * @param location
	 * @param context
	 * @return object com.realestate.model.common.Address
	 */
	public static com.realestate.model.common.Address getAddressFromLocation(String location, Context context){
		Geocoder gc = new Geocoder(context);
		List<Address> list;
		Address addressAndroid;
		com.realestate.model.common.Address addressCustom = new com.realestate.model.common.Address();
		try {
			list = gc.getFromLocationName(location, 1);
			for(int idx=0;idx<list.size();idx++){
				addressAndroid = list.get(idx);
				addressCustom.setLatitude(addressAndroid.getLatitude());
				addressCustom.setLongitude(addressAndroid.getLongitude());
			}
		} catch (IOException e) {
			Common.logError("LocationUtils getAddressFromLocation IOException: "+e.getMessage());
			//e.printStackTrace();
		}

		return addressCustom;
	}

	public static String location2Address(Location loc, Context context){
		Geocoder gc = new Geocoder(context);
		List<Address> list;
		Address addressAndroid;
		String address = null;
		if(loc != null) {
			try {
				list = gc.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
				for (int idx = 0; idx < list.size(); idx++) {
					addressAndroid = list.get(idx);
					//http://developer.android.com/reference/android/location/Address.html
					address = addressAndroid.getAddressLine(idx) +
//								Constants.CONCATDELIMETER + addressAndroid.getFeatureName() +
//								Constants.CONCATDELIMETER + addressAndroid.getSubThoroughfare() +
//								Constants.CONCATDELIMETER + addressAndroid.getThoroughfare() +
//								Constants.CONCATDELIMETER + addressAndroid.getSubLocality() +
								Constants.CONCATDELIMETER + addressAndroid.getLocality() +
//								Constants.CONCATDELIMETER + addressAndroid.getSubAdminArea() +
//								Constants.CONCATDELIMETER + addressAndroid.getAdminArea() +
								"";
				}
			} catch (IOException e) {
				Common.logError("LocationUtils location2Address IOException: " + e.getMessage());
				//e.printStackTrace();
			}
		}
		return address;
	}
}
