package com.mermix.custom;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.mermix.R;
import com.mermix.model.common.Pojo;
import com.mermix.ui.DataRetrieve;
import com.mermix.utils.Common;
import com.mermix.utils.MainService;
import com.mermix.utils.TouchEffect;
import com.mermix.utils.net.CheckConnectivityWithRestApi;

/**
 * This is a common activity that all other activities of the app can extend to
 * inherit the common behaviors like implementing a common interface that can be
 * used in all child activities.
 */
public class CustomActivity extends FragmentActivity implements OnClickListener
{

	/**
	 * Apply this Constant as touch listener for views to provide alpha touch
	 * effect. The view must have a Non-Transparent background.
	 */
	public static final TouchEffect TOUCH = new TouchEffect();
	public MainBroadCastReceiver mainBroadCastReceiver ;
	private Boolean restApiAccessible = false;

	private class MainBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Common.log("CustomActivity MainBroadCastReceiver onReceive");
			Pojo apiResponseData = (Pojo) intent.getSerializableExtra("APIRESPONSEDATA");
			Fragment fragment = getActiveFragment();
			if(fragment != null) {
				//INVOKE fragment's updateUI implemented by interface DataRetrieve
				DataRetrieve customFragment = (DataRetrieve) fragment;
				customFragment.updateUI(apiResponseData);
			}
		}
	}

	/**
	 * Dispatch onStart() to all fragments.  Ensure any created loaders are
	 * now started.
	 */
	@Override
	protected void onStart() {
		Common.log("CustomActivity onStart");
		super.onStart();
		//Register BroadcastReceiver to receive event from our service
		mainBroadCastReceiver = new MainBroadCastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MainService.SERVICEACTION);
		registerReceiver(mainBroadCastReceiver, intentFilter);
		new CheckConnectivityWithRestApi(this).execute();
	}

	/**
	 * Dispatch onStop() to all fragments.  Ensure all loaders are stopped.
	 */
	@Override
	protected void onStop() {
		Common.log("CustomActivity onStop");
		super.onStop();
		unregisterReceiver(mainBroadCastReceiver);
	}

	/* (non-Javadoc)
			 * @see android.app.Activity#onCreate(android.os.Bundle)
			 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Common.log("CustomActivity onCreate");
		super.onCreate(savedInstanceState);
		setupActionBar();
	}

	/**
	 * This method will setup the top title bar (Action bar) content and display
	 * values. It will also setup the custom background theme for ActionBar. You
	 * can override this method to change the behavior of ActionBar for
	 * particular Activity
	 */
	protected void setupActionBar()
	{
		final ActionBar actionBar = getActionBar();
		if (actionBar == null)
			return;
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setLogo(R.drawable.icon);
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_bg));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle(R.string.app_name);
	}

	/**
	 * Sets the touch and click listener for a view with given id.
	 * 
	 * @param id
	 *            the id
	 * @return the view on which listeners applied
	 */
	public View setTouchNClick(int id)
	{

		View v = setClick(id);
		if (v != null)
			v.setOnTouchListener(TOUCH);
		return v;
	}

	/**
	 * Sets the click listener for a view with given id.
	 * 
	 * @param id
	 *            the id
	 * @return the view on which listener is applied
	 */
	public View setClick(int id)
	{

		View v = findViewById(id);
		if (v != null)
			v.setOnClickListener(this);
		return v;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{

	}

	public void onRestApiConnectionDetect(Boolean restApiAccessible){
		Common.log("CustomActivity onRestApiConnectionStatus");
		this.restApiAccessible = restApiAccessible;
		if(!restApiAccessible)
			Common.displayToast(getResources().getString(R.string.rest_api_connection_error), getApplicationContext());
	}

	public Boolean isRestApiAccessible(){
		return this.restApiAccessible;
	}

	public Fragment getActiveFragment(){
		FragmentManager fragManager = CustomActivity.this.getSupportFragmentManager();
		int count = CustomActivity.this.getSupportFragmentManager().getBackStackEntryCount();
		Fragment frag = fragManager.getFragments().get(count>0?count-1:count);
		return frag;
	}
}
