package com.realestate.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.realestate.ApplicationVars;
import com.realestate.R;
import com.realestate.model.ListOfTerms;
import com.realestate.model.PojoTerm;
import com.realestate.model.SQLiteTerm;
import com.realestate.model.common.Pojo;
import com.realestate.model.sqlite.DrupalTerms;
import com.realestate.ui.activities.SearchResultActivity;
import com.realestate.custom.CustomFragment;
import com.realestate.ui.adapters.SpinnerTermAdapter;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.ui.DataRetrieve;
import com.realestate.utils.MainService;
import com.realestate.utils.net.args.TermArgs;
import com.realestate.utils.net.args.UrlArgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class Search is the Fragment class that is launched when the user clicks
 * on Search option in Left navigation drawer and it simply shows a few dummy
 * options for Search property with options for Searching property for Buy and
 * Rent. You can customize this to display actual contents.
 */
public class Search extends CustomFragment implements DataRetrieve
{

	/** Flag to hold whether to search properties for buy or for rent. */
	private boolean buy;
	private boolean spinnersInitialized;
	private Map<String, Spinner> spinners = new HashMap<String, Spinner>();
	private int noneVocabularyId = -1;
	private int vocabularyId = noneVocabularyId;

	/**
	 * method
	 * to update UI with the REST API's retrieved data
	 *
	 * @param apiResponseData
	 */
	@Override
	public void updateUI(Pojo apiResponseData) {
		Common.log("Search updateUI");
		try {
			ListOfTerms termsList = (ListOfTerms) apiResponseData;
			List<PojoTerm> terms = termsList.getTerms();
			String vocabulary;
			if (terms.size() > 0) {
				vocabulary = terms.get(0).getVocabulary();
				ApplicationVars.updateTermsCache(terms, getActivity().getApplicationContext());
				ApplicationVars.dataRetrieved.put(vocabulary, true);
				updateSpinnerWithVocabularyTerms(vocabulary, spinners.get(vocabulary));
			}
		}
		catch (ClassCastException e){
			Common.logError("ClassCastException @ Search updateUI:" + e.getMessage());
		}
	}

	/**
	 * method
	 * to generate REST API url and
	 * to invoke startService
	 */
	@Override
	public void startRequestService(UrlArgs urlArgs) {
		TermArgs args = (TermArgs) urlArgs;
		String apiUrl = Constants.APIENDPOINT + Constants.URI.LISTOFTERMS +
				"?" + args.getUrlArgs() +
				"";
		String pojoClass = Constants.PojoClass.LISTOFTERMS;

		Intent i = new Intent(this.getActivity(), MainService.class);
		i.putExtra(Constants.INTENTVARS.APIURL, apiUrl);
		i.putExtra(Constants.INTENTVARS.POJOCLASS, pojoClass);
		this.getActivity().startService(i);
	}
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		Common.log("Search onCreateView");
		View v = inflater.inflate(R.layout.search, null);

		spinnersInitialized = false;
		setTouchNClick(v.findViewById(R.id.btnSearch));
//		setTouchNClick(v.findViewById(R.id.tab1));
//		setTouchNClick(v.findViewById(R.id.tab2));
		buy = true;
		return v;
	}
//region "start"

//endregion


	/**
	 * Called when the Fragment is visible to the user.  This is generally
	 * tied to {@link Activity#onStart() Activity.onStart} of the containing
	 * Activity's lifecycle.
	 */
	@Override
	public void onStart() {
		Common.log("Search onStart");
		super.onStart();
		if(!spinnersInitialized) {
			spinners.put(Constants.VOCABULARYNAMES.LOCATION, (Spinner) getActivity().findViewById(R.id.location));
			spinners.put(Constants.VOCABULARYNAMES.MACHINETYPE, (Spinner) getActivity().findViewById(R.id.machine_type));
			spinners.put(Constants.VOCABULARYNAMES.CULTIVATION, (Spinner) getActivity().findViewById(R.id.cultivation));
			spinners.put(Constants.VOCABULARYNAMES.CONTRACTTYPE, (Spinner) getActivity().findViewById(R.id.contract_type));
			setSpinnersData();
			spinnersInitialized = true;
		}
	}

	private void setSpinnersData() {
		Common.log("Search setSpinnersData");
		updateSpinnerWithVocabularyTerms(Constants.VOCABULARYNAMES.LOCATION, spinners.get(Constants.VOCABULARYNAMES.LOCATION));
		updateSpinnerWithVocabularyTerms(Constants.VOCABULARYNAMES.MACHINETYPE, spinners.get(Constants.VOCABULARYNAMES.MACHINETYPE));
		updateSpinnerWithVocabularyTerms(Constants.VOCABULARYNAMES.CULTIVATION, spinners.get(Constants.VOCABULARYNAMES.CULTIVATION));
		updateSpinnerWithVocabularyTerms(Constants.VOCABULARYNAMES.CONTRACTTYPE, spinners.get(Constants.VOCABULARYNAMES.CONTRACTTYPE));
	}

	/**
	 * add items in spinners programmatically
	 * @ first check if spinner data have been retrieved from REST API
	 * if data have been retrieved spinner is updated
	 * if data have NOT been retrieved startRequestService is invoked
	 */
	private void updateSpinnerWithVocabularyTerms(String vocabulary, Spinner spinner) {
		Common.log("Search updateSpinnerWithVocabularyTerms");

		List<SQLiteTerm> SQLiteTerms = new ArrayList<SQLiteTerm>();
		if(ApplicationVars.dataRetrieved.get(vocabulary)){
			DrupalTerms drupalTerms = new DrupalTerms(getActivity());
			SQLiteTerms = drupalTerms.getVocabularyTerms(vocabulary);
			SQLiteTerms.add(0, new SQLiteTerm(Constants.SPINNERITEMS.ALLTERM.TID, Constants.SPINNERITEMS.ALLTERM.NAME, ""));
			drupalTerms.closeConnection();
			/*
			 * more android.R.layout. options in
			 * http://developer.android.com/reference/android/R.layout.html
			 */
			SpinnerTermAdapter spinAdapter = new SpinnerTermAdapter(getActivity(),
					android.R.layout.simple_list_item_checked,
					SQLiteTerms);
			spinner.setAdapter(spinAdapter);
		}
		else{
			SQLiteTerms.add(0, new SQLiteTerm(Constants.SPINNERITEMS.ALLTERM.TID, Constants.SPINNERITEMS.ALLTERM.NAME, ""));
			vocabularyId = Constants.TERMVOCABULARIES.containsKey(vocabulary) ? Constants.TERMVOCABULARIES.get(vocabulary) :
					noneVocabularyId;
			startRequestService(new TermArgs(Integer.toString(vocabularyId)));
		}

		/*
		 * more android.R.layout. options in
		 * http://developer.android.com/reference/android/R.layout.html
		 */
		SpinnerTermAdapter spinAdapter = new SpinnerTermAdapter(getActivity(),
				android.R.layout.simple_list_item_checked,
				SQLiteTerms);
		spinner.setAdapter(spinAdapter);
	}

	public void addListenerOnSpinnerItemSelection(){
		spinners.get(Constants.VOCABULARYNAMES.LOCATION).setOnItemSelectedListener(new CustomOnItemSelectedListener());
		spinners.get(Constants.VOCABULARYNAMES.MACHINETYPE).setOnItemSelectedListener(new CustomOnItemSelectedListener());
		spinners.get(Constants.VOCABULARYNAMES.CULTIVATION).setOnItemSelectedListener(new CustomOnItemSelectedListener());
		spinners.get(Constants.VOCABULARYNAMES.CONTRACTTYPE).setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}

	/* (non-Javadoc)
	 * @see com.realestate.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		/*if (v.getId() == R.id.tab1 || v.getId() == R.id.tab2)
		{
			//setupSearchOptions(v.getId() == R.id.tab1);
			v.setEnabled(false);
			if (v.getId() == R.id.tab1)
				getView().findViewById(R.id.tab2).setEnabled(true);
			else
				getView().findViewById(R.id.tab1).setEnabled(true);
		}
		else */
		if (v.getId() == R.id.btnSearch) {
			SQLiteTerm location = (SQLiteTerm) spinners.get(Constants.VOCABULARYNAMES.LOCATION).getSelectedItem();
			SQLiteTerm machineType = (SQLiteTerm) spinners.get(Constants.VOCABULARYNAMES.MACHINETYPE).getSelectedItem();
			SQLiteTerm cultivation = (SQLiteTerm) spinners.get(Constants.VOCABULARYNAMES.CULTIVATION).getSelectedItem();
			SQLiteTerm contractType = (SQLiteTerm) spinners.get(Constants.VOCABULARYNAMES.CONTRACTTYPE).getSelectedItem();

			Intent searchResults = new Intent(getActivity(), SearchResultActivity.class);
			searchResults.putExtra(Constants.URI.PARAMS.LOCATION, location.getTid());
			searchResults.putExtra(Constants.URI.PARAMS.MACHINETYPE, machineType.getTid());
			searchResults.putExtra(Constants.URI.PARAMS.CULTIVATION, cultivation.getTid());
			searchResults.putExtra(Constants.URI.PARAMS.CONTRACTTYPE, contractType.getTid());

			startActivity(searchResults);
		}
	}

	/**
	 * Sets the up search options for buy and rent.
	 * 
	 * @param buy
	 *            Flag to hold whether to search properties for buy or for rent.
	 */
	private void setupSearchOptions(boolean buy)
	{
		this.buy = buy;
		View v = getView();
		/*v.findViewById(R.id.rent1)
				.setVisibility(buy ? View.GONE : View.VISIBLE);
		v.findViewById(R.id.rent2)
				.setVisibility(buy ? View.GONE : View.VISIBLE);
		v.findViewById(R.id.buy1).setVisibility(buy ? View.VISIBLE : View.GONE);*/

	}

	public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			Toast.makeText(parent.getContext(),
					"OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	}
}
