package com.realestate.utils.net.args;

import com.realestate.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 11/10/2015
 * Description:
 */
public class FeedListArgs extends UrlArgs{
	private Map<String, String> feedListArgs = new HashMap<String, String>();

	public FeedListArgs(String type, String limit, String sortProperty, String direction){
		feedListArgs.put(Constants.URI.PARAMS.TYPE, type);
		feedListArgs.put(Constants.URI.PARAMS.LIMIT, limit);
		feedListArgs.put(Constants.URI.PARAMS.SORT, sortProperty);
		feedListArgs.put(Constants.URI.PARAMS.DIRECTION, direction);
	}

	public String getUrlArgs(){
		return super.getUrlArgs(feedListArgs);
	}
}
