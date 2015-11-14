package com.realestate.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

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

}
