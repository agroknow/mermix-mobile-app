package com.realestate.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Constants used in the application
 */
public class Constants {
	public static final String base64Prefix = "data:image/jpeg;base64,";
	public static final Boolean devMode = true;
	public static final int CONNECTIONTIMEOUT = 20000;	//ms
	public static final String APIENDPOINT = "http://dev.mermix.gr/";
	public static final String CONCATDELIMETER = ",";
	public static final String MULTIPRICEDELIMETER = "&";
	public static final String PRICEUNITDELIMETER = "/";
	public static final String LOCALAPIENDPOINT = "http://192.168.1.64/";

	public static final String APICREDENTIALS = "restws:r3stw5!";

	public class MAPZOOMS {
		public static final int COUNTY = 11;
		public static final int COUNTRY = 6;
	}

	public class ACTIVITYREQUESTCODES {
		public static final int OPENDEVICECAMERA = 100;
		public static final int SELECTFILEFROMGALLERY = 101;
	}

	public class URI {
		public static final String SINGLEEQUIPMENT = "node/NID.json";
		/*
		type=apartment&limit=4
		type=apartment&limit=1&page=4
		*/
		public static final String LISTOFEQUIPMENTS = "node.json";
		public static final String USERINFO = "restws/user";
		public static final String USERREGISTER = "restws/create-user";
		public static final String BOOKEQUIPMENT = "restws/book-equipment";
		public static final String LISTOFTERMS = "taxonomy_term.json";
		public static final String LISTOFNEARBYEQUIPMENTS = "views/mobile_app_machine_results/default.json";
		public static final String NEWEQUIPMENT = "node";
		public static final String NEWUSER = "user";
		public static final String REGISTRATIONFORM = "user/register?destination=equipment";

		public class PARAMS {
			public static final String PAGE = "page";
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
			public static final String JSONPAYLOAD = "JSON_PAYLOAD";
			public static final String JSONPAYLOAD_MULTI = "JSON_PAYLOAD_MULTI";
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
		public static final String BOOKEQUIPMENT = "com.realestate.model.BookEquipment";
		public static final String LISTOFTERMS = "com.realestate.model.ListOfTerms";
		public static final String USER = "com.realestate.model.common.User";
		public static final String EQUIPMENTINVIEW = "com.realestate.model.EquipmentInView";
		public static final String NEWEQUIPMENT = "com.realestate.model.NewEquipment";
		public static final String EQUIPMENTPOSTPAYLOAD = "com.realestate.model.payload.EquipmentPostPayload";
	}

	public class FRAGMENTS {
		public static final String FEEDLIST = "com.realestate.ui.fragments.FeedList";
		public class POSITION {
			public static final int FEEDLIST = 0;
		}
	}

	public class VOCABULARYNAMES {
		public static final String LOCATION = "location";
		public static final String MACHINETYPE = "appartment_type";
		public static final String CULTIVATION = "cultivation";
		public static final String CONTRACTTYPE = "contract_type";
		public static final String PRICEUNITS = "price_units";
		public static final String EMPTY = "";
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
		mMap.put(VOCABULARYNAMES.PRICEUNITS, 12);
		return mMap;
	}

	public class SQLITE{
		public static final String DBNAME = "mermix";
		public static final int DBVERSION = 8;
		public class TABLES {
			public static final String TERMS = "terms";
			public static final String NODES = "nodes";
		}
		public class COLUMNS {
			public static final String TID = "tid";
			public static final String NAME = "name";
			public static final String VOCABULARY= "vocabulary";
			public static final String NID = "nid";
			public static final String TITLE = "title";
			public static final String BODY = "body";
			public static final String COORDINATES = "coordinates";
			public static final String IMAGES = "images";
			public static final String MULTIPRICE = "multiprice";
		}
	}

	public class SPINNERITEMS {
		public class ALLTERM {
			public static final String NAME = "all";
			public static final int TID = -1;
		}
		public class EMPTYTERM {
			public static final String NAME = "";
			public static final int TID = -2;
		}
	}

	public class SORTPROPERTIES {
		public static final String CREATED = "created";
	}

	public class DIRECTIONS {
		public static final String DESC = "desc";
		public static final String ASC = "asc";
	}

	public class LOCALE{
		public class DRUPAL{
			public static final String GREEK = "el";
			public static final String ENGLISH = "en";
		}
		public class ANDROID{
			public static final String GREEK = "el_GR";
		}
	}
}
