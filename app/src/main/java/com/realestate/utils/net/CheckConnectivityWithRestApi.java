package com.realestate.utils.net;

import android.os.AsyncTask;

import com.realestate.custom.CustomActivity;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by teo on 7/12/2015.
 */
public class CheckConnectivityWithRestApi extends AsyncTask<Void, Void, Boolean> {
	private CustomActivity activityOrigin = null;

	public CheckConnectivityWithRestApi(CustomActivity activityOrigin) {
		this.activityOrigin = activityOrigin;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Common.log("CheckConnectivityWithRestApi doInBackground");

		HttpURLConnection urlConnection = null;
		URL url;
		Boolean restApiAccessible = false;
		try {
			url = new URL(Constants.APIENDPOINT);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(Constants.CONNECTIONTIMEOUT);
			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
				restApiAccessible = true;
		} catch (MalformedURLException e) {
			Common.logError("MalformedURLException @ execRequest:"+e.getMessage());
			//e.printStackTrace();
		}  catch (UnknownHostException e){
			Common.logError("UnknownHostException @ execRequest:" + e.getMessage());
		} catch (IOException e) {
			Common.logError("IOException @ execRequest:" + e.getMessage());
			//e.printStackTrace();
		} finally {
			urlConnection.disconnect();
		}

		return restApiAccessible;
	}

	@Override
	protected void onPostExecute(Boolean aBoolean) {
		super.onPostExecute(aBoolean);
		if(!aBoolean)
			this.activityOrigin.onRestApiConnectionError();
	}
}
