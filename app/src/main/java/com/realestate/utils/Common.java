package com.realestate.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created on 12/07/2015
 * Description:
 * common methods to use throughout the application
 */
public class Common {
	public static void log(String msg){
		Log.d("Mermix", msg);
	}

	public static void logError(String error){
		Log.d("MermixError", error);
	}

	public static void displayToast(String s, Context context) {
		Toast.makeText(context,
				s,
				Toast.LENGTH_LONG).show();

	}

	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

	public static String concatString(List<String> listOfStrings, String delimeter){
		String concat = "";
		for(int i=0; i<listOfStrings.size(); i++){
			concat += delimeter + listOfStrings.get(i);
		}
		if(!concat.isEmpty())
			concat = concat.substring(1);
		return concat;
	}

	public static String doubleArr2String(Double[] doubleArray, String delimeter){
		String str = "";
		for(int i=0;i<doubleArray.length;i++){
			str += delimeter + Double.toString(doubleArray[i]);
		}
		if(!str.isEmpty())
			str = str.substring(1);
		return str;
	}

	public static Double[] stringToDoubleArr(String str, String delimeter){
		String[] strArr = str.split(delimeter);
		Double[] doubleArr = new Double[strArr.length];
		for(int i = 0; i < strArr.length; i++) {
			doubleArr[i] = Double.parseDouble(strArr[i]);
		}
		return doubleArr;
	}

	public static Double addDoubleValues(Double double1, Double double2){
		BigDecimal lng = BigDecimal.valueOf(double1);
		BigDecimal offset = BigDecimal.valueOf(double2);
		BigDecimal newLng = lng.add(offset);
		return newLng.doubleValue();
	}

}
