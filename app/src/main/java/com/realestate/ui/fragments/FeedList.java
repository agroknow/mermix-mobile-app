package com.realestate.ui.fragments;

import android.app.AlertDialog.Builder;
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

import com.realestate.R;
import com.realestate.custom.CustomFragment;
import com.realestate.model.Equipment;
import com.realestate.model.ListOfEquipments;
import com.realestate.model.common.Pojo;
import com.realestate.ui.DataRetrieve;
import com.realestate.ui.activities.EquipmentDetail;
import com.realestate.ui.activities.MainActivity;
import com.realestate.ui.activities.MapViewActivity;
import com.realestate.ui.adapters.FeedAdapter;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.utils.MainService;
import com.realestate.utils.net.args.FeedListArgs;
import com.realestate.utils.net.args.UrlArgs;

import java.util.ArrayList;
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
	public int sortSelection = 0;

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
		startRequestService(new FeedListArgs("apartment", "10", "", ""));
		return v;
	}

	@Override
	public void startRequestService(UrlArgs urlArgs) {
		FeedListArgs args = (FeedListArgs) urlArgs;
		String apiUrl = Constants.APIENDPOINT + Constants.URI.LISTOFEQUIPMENTS +
				"?" + args.getUrlArgs() +
				"";
		String pojoClass = Constants.PojoClass.LISTOFEQUIPMENTS;

		Intent i = new Intent(this.getActivity(), MainService.class);
		i.putExtra(Constants.INTENTVARS.APIURL, apiUrl);
		i.putExtra(Constants.INTENTVARS.POJOCLASS, pojoClass);
		this.getActivity().startService(i);
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
		list.setAdapter(adapter);
		list.setOnItemClickListener(new onEquipmentClickListener());
	}

	/**
	 * Load a dummy list of feeds. You need to write your own logic to load
	 * actual list of feeds.
	 * DEPRECATED!!!!!!
	 * 
	 * @param sort
	 *            flag for whether sort the list or not
	 */
	private void loadDummyFeeds(boolean sort)
	{
		ArrayList<String[]> sl = new ArrayList<String[]>();
		sl.add(new String[]{"$950,000 - $1,200,000", "South Extenstion 324",
				"15 Fair bank place", "6", "2", "1"});

		sl.add(sort ? 0 : 1, new String[]{"$550,000 - $5,200,000",
				"North Extenstion 454", "14 Mount view place", "4", "3", "2"});

		/*adapterEquipmentsList = new ArrayList<String[]>(sl);
		adapterEquipmentsList.addAll(sl);
		adapterEquipmentsList.addAll(sl);
		adapterEquipmentsList.addAll(sl);
		adapterEquipmentsList.addAll(sl);
		adapterEquipmentsList.addAll(sl);
		adapterEquipmentsList.addAll(sl);*/
	}

	@Override
	public void updateUI(Pojo apiResponseData) {
		Common.log("FeedList updateUI");
		ListOfEquipments equipmentsList = (ListOfEquipments) apiResponseData;
		List<Equipment> equipments = equipmentsList.getEquipments();
		adapter.clear();
		int idx = 0;
		while(idx < equipments.size()){
			//Common.log(Integer.toString(idx)+". node title: " + equipments.get(idx).getTitle());
			//Common.log("1st node body: " + equipments.get(idx).getBody().getValue());
			adapter.addItem(equipments.get(idx));
			idx++;
		}
		adapter.notifyDataSetChanged();
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
						FeedListArgs feedListArgs = (which == 1) ?
										new FeedListArgs("apartment", "10", Constants.SORTPROPERTIES.CREATED, Constants.DIRECTIONS.DESC):
										new FeedListArgs("apartment", "10", "", "");
						sortSelection = which;
						startRequestService(feedListArgs);
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
			Intent i = new Intent(getActivity(), EquipmentDetail.class);
			Bundle mBundle = new Bundle();
			mBundle.putSerializable(Constants.INTENTVARS.EQUIPMENT, equipment);
			i.putExtras(mBundle);
			i.putExtra(Constants.INTENTVARS.INVOKERESTAPI, false);
			startActivity(i);
		}
	}

}
