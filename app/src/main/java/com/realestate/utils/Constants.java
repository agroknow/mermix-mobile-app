package com.realestate.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Constants used in the application
 */
public class Constants {
	public static final Boolean debugMode = true;
	public static final int CONNECTIONTIMEOUT = 20000;	//ms
	public static final String APIENDPOINT = "http://dev.mermix.gr/el/";
	//public static final String APIENDPOINT = "http://192.168.1.64/";

	public static final String APICREDENTIALS = "restws:r3stw5!";

	public class URI {
		public static final String SINGLEEQUIPMENT = "node/NID.json";
		/*
		type=apartment&limit=4
		type=apartment&limit=1&page=4
		*/
		public static final String LISTOFEQUIPMENTS = "node.json";
		public static final String LISTOFTERMS = "taxonomy_term.json";
		//public static final String LISTOFEQUIPMENTS = "json.php";
		public static final String LISTOFNEARBYEQUIPMENTS = "views/mobile_app_machine_results/default.json";

		public class PARAMS {
			public static final String LIMIT = "limit";
			public static final String SORT = "sort";
			public static final String TYPE = "type";
			public static final String DIRECTION = "direction";
			public static final String VOCABULARY = "vocabulary";
			public static final String MACHINETYPE = "field_type";
			public static final String CULTIVATION = "field_cultivation";
			public static final String CONTRACTTYPE = "field_contract_type";
			public static final String LOCATION = "field_location";
			public static final String COORDINATES = "args[0]";
		}
	}

	public class INTENTVARS {
		public static final String APIURL = "apiUrl";
		public static final String FRAGMENTCLASS = "fragment";
		public static final String POJOCLASS = "pojoClass";
		public static final String FRAGMENTPOS = "fragmentPos";
        public static final String EQUIPMENT = "equipment";
		public static final String EQUIPMENTID = "equipmentId";
		public static final String INVOKERESTAPI = "invokeRestApi";
	}

	public class HttpRequestMethods {
		public static final String GET = "GET";
		public static final String POST = "POST";
	}

	public class HttpResponseStatus {
		public static final int SUCCESS = 200;
		public static final int ACCESSDENIED = 403;
	}

	public class ErrorMessages{
		public static final String NO_ERROR = "No Error Detected";
		public static final String NO_DATA = "No Data Found";
		public static final String NO_OBJECT_IN_DATA = "Expected data to start with an Object or an Array";
		public static final String NO_CONNECTION = "No connection to remote server";
		public static final String AUTH_FAIL = "authentication failed";
	}

	public class CharSets{
		public static final String UTF8 = "UTF-8";
	}

	public class PojoClass{
		public static final String LISTOFEQUIPMENTS = "com.realestate.model.ListOfEquipments";
		public static final String EQUIPMENT = "com.realestate.model.Equipment";
		public static final String LISTOFTERMS = "com.realestate.model.ListOfTerms";
		public static final String EQUIPMENTINVIEW = "com.realestate.model.EquipmentInView";
	}

	public class FRAGMENTS {
		public static final String FEEDLIST = "com.realestate.ui.fragments.FeedList";
		public class POSITION {
			public static final int FEEDLIST = 0;
		}
	}

	public class TOPMENUITEMS{
		public static final String FEED = "Feed";
		public static final String SEARCH = "Search";
		public static final String MAP = "Map";
		public static final String SEARCHRESULTS = "Search Results";
		public static final String EQUIPMENT = "Equipment";
	}

	public class VOCABULARYNAMES {
		public static final String LOCATION = "location";
		public static final String MACHINETYPE = "appartment_type";
		public static final String CULTIVATION = "cultivation";
		public static final String CONTRACTTYPE = "contract_type";
	}

	/**
	 * vocabulary ids in drupal database
	 */
	public static final Map<String, Integer> TERMVOCABULARIES =
			Collections.unmodifiableMap(initializeMapping());
	private static Map<String, Integer> initializeMapping() {
		Map<String, Integer> mMap = new HashMap<String, Integer>();
		mMap.put(VOCABULARYNAMES.LOCATION, 11);
		mMap.put(VOCABULARYNAMES.MACHINETYPE, 2);
		mMap.put(VOCABULARYNAMES.CULTIVATION, 9);
		mMap.put(VOCABULARYNAMES.CONTRACTTYPE, 6);
		return mMap;
	}

	public class SQLITE{
		public static final String DBNAME = "mermix";
		public static final int DBVERSION = 1;
		public class TABLES {
			public static final String TERMS = "terms";
		}
		public class COLUMNS {
			public static final String TID = "tid";
			public static final String NAME = "name";
			public static final String VOCABULARY= "vocabulary";
		}
	}

	public class SPINNERITEMS {
		public class ALLTERM {
			public static final String NAME = "All";
			public static final int TID = -1;
		}
	}

	public class SORTPROPERTIES {
		public static final String CREATED = "created";
	}

	public class DIRECTIONS {
		public static final String DESC = "desc";
		public static final String ASC = "asc";
	}

}
