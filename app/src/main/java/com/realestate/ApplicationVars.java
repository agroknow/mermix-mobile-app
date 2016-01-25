package com.realestate;

import android.content.Context;
import android.content.res.Resources;

import com.realestate.model.PojoTerm;
import com.realestate.model.SQLiteTerm;
import com.realestate.model.sqlite.DrupalNodes;
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
 * TODO
 * consider implementation using SharedPreferences instead, http://developer.android.com/guide/topics/data/data-storage.html#pref
 *
 * do not clear terms cache until new terms are retrieved from REST API
 * in case NO internet connection available OR implement in memory sqlite db for terms
 *
 * create in memory sqlite db for retrieved equipment (neccessary in MapViewer)
 * consider converting terms sqlite database from persistent to in memory
 * however in memory sqlite may slow down app and eventually crash if large amount of data are kept in
 *
 * check
 * https://attakornw.wordpress.com/2012/02/25/using-in-memory-sqlite-database-in-android-tests/
 * http://stackoverflow.com/a/12470494, (data in Application & data in memory SQLite)
 * http://stackoverflow.com/a/9286006, (SQLiteOpenHelper a static data member & abstract SQLite with ContentProvider)
 * http://stackoverflow.com/a/22642516, (general information)
 */
public class ApplicationVars {
	/**
	 * structure that indicates whether
	 * data have been retrieved from REST API
	 */
	public static Map<String, Boolean> dataRetrieved = new HashMap<>();
	private static boolean varsInitialized = false;
	public static String restApiLocale;
	public static class User {
		public static String credentials;
		public static String id;
	}

	public static void initialize(Context context) {
		Common.log("ApplicationVars initialize");
		if(!varsInitialized) {
			User.credentials = "";
			User.id = "";
			restApiLocale =  Resources.getSystem().getConfiguration().locale.toString().equals(Constants.LOCALE.ANDROID.GREEK) ?
					Constants.LOCALE.DRUPAL.GREEK:
					Constants.LOCALE.DRUPAL.ENGLISH;
			initDataRetrievedFlags();
			clearTermsCache(context);
			clearNodesCache(context);
			varsInitialized = true;
		}
	}

	private static void clearNodesCache(Context context) {
		Common.log("ApplicationVars clearNodesCache");
		DrupalNodes drupalNodes = new DrupalNodes(context);
		drupalNodes.clearNodes();
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
