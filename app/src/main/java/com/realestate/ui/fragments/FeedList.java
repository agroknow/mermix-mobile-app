package com.realestate.ui.fragments;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.realestate.ApplicationVars;
import com.realestate.R;
import com.realestate.custom.CustomFragment;
import com.realestate.model.Equipment;
import com.realestate.model.ListOfEquipments;
import com.realestate.model.common.Pojo;
import com.realestate.ui.DataRetrieve;
import com.realestate.ui.activities.EquipmentDetailActivity;
import com.realestate.ui.activities.MainActivity;
import com.realestate.ui.activities.MapViewActivity;
import com.realestate.ui.adapters.FeedAdapter;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.utils.MainService;
import com.realestate.utils.net.args.FeedListArgs;
import com.realestate.utils.net.args.UrlArgs;

import java.util.List;

/**
 * The Class FeedList is the Fragment class that is launched when the user
 * clicks on Feed option in Left navigation drawer and this is also used as a
 * default fragment for MainActivity. It simply shows a dummy list of Property
 * Feeds. You can customize this class to display actual Feed listing.
 */
public class FeedList extends CustomFragment implements DataRetrieve
{

	private FeedAdapter adapter = new FeedAdapter();
	private ProgressDialog progress;
	public int sortSelection = 0;

	public static class Args {
		static int page = 0;
		static String pageSize = "10";
		static String type = "apartment";
		static String sort = "";
		static String dir = "";
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		Common.log("FeedList onCreateView");
		View v = inflater.inflate(R.layout.feed, null);
		setFeedList(v, false);
		setHasOptionsMenu(true);
		setTouchNClick(v.findViewById(R.id.btnMore));
		startRequestService(new FeedListArgs(Args.type, Args.pageSize, Args.sort, Args.dir, Integer.toString(Args.page)));
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Common.log("FeedList onCreate");
		progress = new ProgressDialog(getActivity());
		progress.setTitle(getResources().getString(R.string.progress_dialog_title));
		progress.setMessage(getResources().getString(R.string.progress_dialog_retrieve_msg));
	}

	@Override
	public void onStop() {
		super.onStop();
		Common.log("FeedList onStop");
		Args.page = 0;
		Args.sort = "";
		Args.dir = "";
		sortSelection = 0;
	}

	@Override
	public void onStart() {
		super.onStart();
		Common.log("FeedList onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Common.log("FeedList onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Common.log("FeedList onPause");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Common.log("FeedList onDestroy");
	}

	@Override
	public void startRequestService(UrlArgs urlArgs) {
		Common.log("FeedList startRequestService");
		FeedListArgs args = (FeedListArgs) urlArgs;
		String apiUrl = Constants.APIENDPOINT + ApplicationVars.restApiLocale + "/" + Constants.URI.LISTOFEQUIPMENTS +
				"?" + args.getUrlArgs() +
				"";
		String pojoClass = Constants.PojoClass.LISTOFEQUIPMENTS;

		Intent i = new Intent(this.getActivity(), MainService.class);
		i.putExtra(Constants.INTENTVARS.APIURL, apiUrl);
		i.putExtra(Constants.INTENTVARS.POJOCLASS, pojoClass);
		this.getActivity().startService(i);
		progress.show();
	}

	/**
	 * Setup and initialize the feed list view.
	 * 
	 * @param v
	 *            the root view
	 * @param sort
	 *            flag for whether sort the list or not
	 */
	private void setFeedList(View v, boolean sort)
	{
		ListView list = (ListView) v.findViewById(R.id.list);
		list.addFooterView(getLayoutInflater(null).inflate(R.layout.footer, null));
		list.setAdapter(adapter);
		list.setOnItemClickListener(new onEquipmentClickListener());
	}

	@Override
	public void updateUI(Pojo apiResponseData) {
		Common.log("FeedList updateUI");
		progress.dismiss();
		if(apiResponseData != null) {
			try {
				ListOfEquipments equipmentsList = (ListOfEquipments) apiResponseData;
				List<Equipment> equipments = equipmentsList.getEquipments();
				if(Args.page == 0)
					adapter.clear();
				int idx = 0;
				while (idx < equipments.size()) {
					adapter.addItem(equipments.get(idx));
					idx++;
				}
				adapter.notifyDataSetChanged();
				int visibility = (idx > 0) ? View.VISIBLE : View.GONE;
				getActivity().findViewById(R.id.btnMore).setVisibility(visibility);
			} catch (ClassCastException e) {
				Common.logError("ClassCastException @ FeedList updateUI:" + e.getMessage());
			}
		}
		else{
			getActivity().findViewById(R.id.btnMore).setVisibility(View.GONE);
			Common.displayToast(getResources().getString(R.string.no_results), getActivity().getApplicationContext());
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.feed, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.menu_sort)
			showSortPopup();
		else if (item.getItemId() == R.id.menu_locate)
			startActivity(new Intent(getActivity(), MapViewActivity.class));
		else if (item.getItemId() == R.id.menu_search)
			//startActivity(new Intent(getActivity(), SearchResultActivity.class));
			((MainActivity) getActivity()).launchFragment(1);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.btnMore) {
			Args.page++;
			startRequestService(new FeedListArgs(Args.type, Args.pageSize, Args.sort, Args.dir, Integer.toString(Args.page)));
		}
	}

	/**
	 * Shows a sort dialog that holds a list of various sort options for sorting
	 * the feeds.
	 */
	private void showSortPopup()
	{
		Builder b = new Builder(getActivity());
		b.setTitle(R.string.sort_list_by);
		b.setSingleChoiceItems(R.array.arr_sort, sortSelection,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						sortSelection = which;
						Args.page = 0;
						Args.sort = (which == 1) ? Constants.SORTPROPERTIES.CREATED : "";
						Args.dir = (which == 1) ? Constants.DIRECTIONS.DESC : "";
						startRequestService(new FeedListArgs(Args.type, Args.pageSize, Args.sort, Args.dir, Integer.toString(Args.page)));
					}
				});
		b.create().show();
	}

	class onEquipmentClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
		long id)
		{
			Equipment equipment = (Equipment)parent.getItemAtPosition(position);
			Intent i = new Intent(getActivity(), EquipmentDetailActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putSerializable(Constants.INTENTVARS.EQUIPMENT, equipment);
			i.putExtras(mBundle);
			i.putExtra(Constants.INTENTVARS.INVOKERESTAPI, false);
			startActivity(i);
		}
	}

}
