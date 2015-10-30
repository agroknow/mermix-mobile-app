package com.realestate;

import android.content.Context;

import com.realestate.model.PojoTerm;
import com.realestate.model.SQLiteTerm;
import com.realestate.model.sqlite.DrupalTerms;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created on 06/10/2015
 * Description:
 * class containing application's global scope variables
 */
public class ApplicationVars {
	/**
	 * structure that indicates whether
	 * data have been retrieved from REST API
	 */
	public static Map<String, Boolean> dataRetrieved = new HashMap<String, Boolean>();
	private static boolean varsInitialized = false;

	public static void initialize(Context context) {
		Common.log("ApplicationVars initialize");
		if(!varsInitialized) {
			initDataRetrievedFlags();
			clearTermsCache(context);
			varsInitialized = true;
		}
	}

	public static void updateTermsCache(List<PojoTerm> terms, Context context){
		Common.log("ApplicationVars updateTermsCache");
		DrupalTerms drupalTerms = new DrupalTerms(context);
		Iterator<PojoTerm> locationsIterator = terms.iterator();
		PojoTerm pojoTerm;
		while(locationsIterator.hasNext()) {
			pojoTerm = locationsIterator.next();
			drupalTerms.insertTerm(new SQLiteTerm(pojoTerm.getTid(), pojoTerm.getName(), pojoTerm.getVocabulary()));
		}
		drupalTerms.closeConnection();
	}

	private static void clearTermsCache(Context context){
		Common.log("ApplicationVars clearTermsCache");
		DrupalTerms drupalTerms = new DrupalTerms(context);
		drupalTerms.deleteAllTerms();
		drupalTerms.closeConnection();
	}

	private static void initDataRetrievedFlags() {
		Common.log("ApplicationVars initDataRetrievedFlags");
		dataRetrieved.put(Constants.VOCABULARYNAMES.LOCATION, false);
		dataRetrieved.put(Constants.VOCABULARYNAMES.MACHINETYPE, false);
		dataRetrieved.put(Constants.VOCABULARYNAMES.CULTIVATION, false);
		dataRetrieved.put(Constants.VOCABULARYNAMES.CONTRACTTYPE, false);
	}

}
