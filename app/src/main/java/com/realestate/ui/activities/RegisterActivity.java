package com.realestate.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.realestate.ApplicationVars;
import com.realestate.R;
import com.realestate.custom.CustomActivity;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;

/**
 * Created on 20/01/2016
 * Description:
 *
 * load registration form in webview (http://developer.android.com/guide/webapps/webview.html)
 * and on successful registration invoke activity finish()
 */
public class RegisterActivity extends CustomActivity {
	private WebView registrationWebView;
	@Override
	protected void onStart() {
		Common.log("RegisterActivity onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Common.log("RegisterActivity onStop");
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Common.log("RegisterActivity onCreate");
		super.onCreate(savedInstanceState);
		// start activity with WebView that loads url Constants.APIENDPOINT ...
		registrationWebView  = new WebView(this);
		// enable javascript
		registrationWebView.getSettings().setJavaScriptEnabled(true);
		final Activity activity = this;
		// Stop local links and redirects from opening in browser instead of WebView
		registrationWebView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Common.displayToast(description, activity.getApplicationContext());
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
		});
		// initiate interface that can be accessed by web application javascript code using variable Mermix
		registrationWebView.addJavascriptInterface(new WebAppInterface(this), getResources().getString(R.string.app_name));
		// load url
		registrationWebView .loadUrl(Constants.APIENDPOINT + ApplicationVars.restApiLocale + "/" + Constants.URI.REGISTRATIONFORM);
		setContentView(registrationWebView);
	}

	@Override
	public void onBackPressed() {
		Common.log("RegisterActivity onBackPressed");
		if(registrationWebView.canGoBack())		//return true if there is actually web page history for the user to visit
			registrationWebView.goBack();
		else
			super.onBackPressed();
	}

	@Override
	protected void onPause() {
		Common.log("RegisterActivity onPause");
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Common.log("RegisterActivity onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		Common.log("RegisterActivity onResume");
		super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * class that contain the methods available to web application javascript code
	 * check http://developer.android.com/guide/webapps/webview.html#BindingJavaScript
	 */
	public class WebAppInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		WebAppInterface(Context c) {
			mContext = c;
		}

		/** Show a toast from the web page */
		@JavascriptInterface
		public void showToast(String toast) {
			Common.displayToast(toast, mContext);
		}
	}
}
