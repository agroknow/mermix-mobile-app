package com.realestate.utils.net;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.utils.JacksonObjectMapper;
import com.realestate.utils.MainService;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created on 14/07/2015
 * Description:
 * asynchTask class to invoke mermix REST API asynchronously.
 * @ doInBackground method request to API is executed,
 * and when request completes method onPostExecute is invoked.
 * @ onPostExecute method Object's CallBack method is invoked,
 * onPostExecute is invoked by UI thread so that it can make changes to UI
 */
public class RestApiConsumer extends AsyncTask<String, Void, JsonNode>{

	private MainService service;
	private String requestCharset = Constants.CharSets.UTF8;

	public RestApiConsumer(MainService service){
		this.service = service;
	}

	@Override
	protected JsonNode doInBackground(String... strings) {
		Common.log("RestApiConsumer doInBackground");

		String restApiUrl;
		HttpURLConnection urlConnection;
		String requestParams;
		OutputStream output;
		InputStream bufferedInput = null;
		JsonNode rootNode = null;

		String[] url = strings[0].split("\\?");
		restApiUrl = url.length > 0 ? url[0] : "";
		requestParams = url.length > 1 ? url[1] : "";

		urlConnection = execRequest(Constants.HttpRequestMethods.GET, restApiUrl, requestParams);
		if(urlConnection != null) {
			try {
				urlConnection.setConnectTimeout(Constants.CONNECTIONTIMEOUT);
				if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					Common.logError("HTTP Response " + urlConnection.getResponseCode() + " when requesting url: " + restApiUrl + " with params: " + requestParams);
					return null;
				}
				bufferedInput = new BufferedInputStream(urlConnection.getInputStream());
				rootNode = jacksonParse(bufferedInput);
				bufferedInput.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				urlConnection.disconnect();
			}
		}

		/*
		 * DefaultHttpClient & HttpGet implemenation
		 *
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		HttpGet httpGet = new HttpGet(restApiUrl + "?" + requestParams);
		httpGet.addHeader("Accept-Charset", requestCharset);
		httpGet.addHeader("Authorization", "Basic " + new String(android.util.Base64.encode(Constants.APICREDENTIALS.getBytes(), android.util.Base64.NO_WRAP)));
		httpGet.addHeader("Accept", "application/json");
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			org.apache.http.StatusLine statusResponse = httpResponse.getStatusLine();
			if ( statusResponse.getStatusCode() != HttpURLConnection.HTTP_OK ) {
				Common.logError("HTTP Response " + statusResponse.getStatusCode() + " : " + statusResponse.toString() + " : " +statusResponse.getReasonPhrase());
				return null;
			}
			bufferedInput = new BufferedInputStream(httpResponse.getEntity().getContent());
			rootNode = jacksonParse(bufferedInput);
			bufferedInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/

		return rootNode;
	}

	@Override
	protected void onPostExecute(JsonNode o) {
		super.onPostExecute(o);
		this.service.asyncTaskCB(o);
	}

	private HttpURLConnection execRequest(String httpMethod, String requestUrl, String requestParams){
		HttpURLConnection urlConnection = null;
		OutputStream output;
		URL url;
		Common.log("exec " + httpMethod + " request on url " + requestUrl + "?" + requestParams);

		try {
			switch(httpMethod){
				case Constants.HttpRequestMethods.GET:
					url = new URL(requestUrl + "?" + requestParams);
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestProperty("Accept-Charset", requestCharset);
					/*
					Basic Authentication
					http://stackoverflow.com/questions/496651/connecting-to-remote-url-which-requires-authentication-using-java
					*/
					//default authenticator
					/*Authenticator.setDefault(new Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							String username = "admin";
							String password = "gs34526";
							return new PasswordAuthentication(username, password.toCharArray());
						}
					});*/

					//Authorization Request Header
					urlConnection.setRequestProperty("Authorization", "Basic "+
						new String(android.util.Base64.encode(Constants.APICREDENTIALS.getBytes(), android.util.Base64.NO_WRAP))
					);
					break;
				case Constants.HttpRequestMethods.POST:
					url = new URL(requestUrl);
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestProperty("Accept-Charset", requestCharset);
					urlConnection.setDoOutput(true);
					urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + requestCharset);
					output = urlConnection.getOutputStream();
					try {
						output.write(requestParams.getBytes(requestCharset));
					} finally {
						try { output.close(); } catch (IOException logOrIgnore) {}
					}
					break;
				default:
			}
		} catch (MalformedURLException e) {
			Common.logError("MalformedURLException @ execRequest:"+e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			Common.logError("IOException @ execRequest:" + e.getMessage());
			//e.printStackTrace();
		}
		return urlConnection;
	}

	/**
	 *
	 * @param bufferedInput
	 * @return JsonNode
	 */
	private JsonNode jacksonParse(InputStream bufferedInput){
		JsonNode rootNode = null;
		try {
			JsonFactory jsonF = JacksonObjectMapper.getInstance().getFactory();
			JsonParser jsonP = jsonF.createParser(bufferedInput);
			rootNode = JacksonObjectMapper.getInstance().readTree(jsonP);
			jsonP.close();
		} catch (IOException e) {
			Common.logError("IOException @ jacksonParse:" + e.getMessage());
			//e.printStackTrace();
		}
		return rootNode;
	}
}
