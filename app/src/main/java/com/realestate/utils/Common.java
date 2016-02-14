package com.realestate.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.model.common.Pojo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 12/07/2015
 * Description:
 * common methods to use throughout the application
 */
public class Common {
	public static void log(String msg){
		if(Constants.devMode)
			Log.d("Mermix", msg);
	}

	public static void logError(String error){
		if(Constants.devMode)
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

	public static Boolean isInArrayInteger(int[] arr, int targetValue){
		for(int s: arr){
			if(s == targetValue)
				return true;
		}
		return false;
	}

	public static Boolean isInArrayString(String[] arr, String targetValue) {
		for(String s: arr){
			if(s.equals(targetValue))
				return true;
		}
		return false;
	}

	public static <T extends Pojo> String pojo2Json(T pojoObject){
		String jsonString = "";

		ObjectMapper mapper = JacksonObjectMapper.getInstance();
		try {
			jsonString = mapper.writeValueAsString(pojoObject);
		} catch (JsonParseException e){
			Common.logError("JsonParseException @ Common.pojo2Json:" + e.getMessage());
			//e.printStackTrace();
		} catch (JsonProcessingException e) {
			Common.logError("JsonProcessingException @ Common.pojo2Json:" + e.getMessage());
			//e.printStackTrace();
		}
		return jsonString;
	}

	public static Pojo json2Pojo(String jsonString, String pojoClassName){
		Pojo pojoObject = null;
		try {
			ObjectMapper mapper = JacksonObjectMapper.getInstance();
			Class<? extends Pojo> pojoClass = (Class<? extends Pojo>) Class.forName(pojoClassName);
			pojoObject = mapper.readValue(jsonString, pojoClass);
		} catch (IOException|ClassNotFoundException e){
			Common.logError("IOException|ClassNotFoundException @ Common.json2Pojo:" + e.getMessage());
		}
		return pojoObject;
	}

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		Common.log("ExternalStorageState: " + state);
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public static String getGalleryImagePathFromUri(Uri imageUri, Activity activity){
		/*
		 * Android saves the images in its own database,
		 * and when we want to access that database,
		 * we have to retrieve the content by URI.
		 * Projection specifies how many columns do you want from the given data, http://developer.android.com/reference/android/provider/MediaStore.MediaColumns.html.
		 * You can add all the columns you want in the projection
		 * and data for all the records for those particular columns would be returned in a cursor.
		 */
		String imagePath = "";
		String[] projection = {MediaStore.Images.Media.DATA};
		int columnIndex;
		Cursor cursor;		//Cursor to get image uri to display
		CursorLoader cursorLoader = new CursorLoader(activity, imageUri, projection, null, null, null);
		cursor = cursorLoader.loadInBackground();
		if(cursor != null && cursor.moveToFirst()) {
			do {
				columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
				imagePath = cursor.getString(columnIndex);
			} while(cursor.moveToNext());
			cursor.close();
		}
		return imagePath;
	}

	/**
	 * check if string is a url
	 * use regular expressionn ^http(s)?:\/\/(.*?)
	 */
	public static Boolean hasUrlFormat(String str){
		Boolean isUrl = false;

		Pattern p = Pattern.compile("^http(s)?:\\/\\/(.*?)");
		Matcher m = p.matcher(str);
		if(m.find())
			isUrl = true;
		return isUrl;
	}

	public static String getFileNameFromUri(String uri){
		String file = "";
		int lastIndexOfSlash = -1;
		if(uri != null && !uri.isEmpty()){
			lastIndexOfSlash = uri.lastIndexOf('/');
			if(lastIndexOfSlash > -1)
				file = uri.substring(uri.lastIndexOf('/'), uri.length());
		}
		return file;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void logMemoryStats(Context context){
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);

		Long bytesOfMB = 1048576L;
		Common.log("MEMORY stats:" +
						" availableMem=" + Long.toString(mi.availMem / bytesOfMB) + "MB," +
						//" availableMemPercent=" + Long.toString(mi.availMem / mi.totalMem) + "%" +
						""
		);
	}
}
