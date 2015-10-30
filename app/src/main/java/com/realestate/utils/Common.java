package com.realestate.utils;

import android.content.Context;
import android.util.Log;
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
				Toast.LENGTH_SHORT).show();

	}
}
