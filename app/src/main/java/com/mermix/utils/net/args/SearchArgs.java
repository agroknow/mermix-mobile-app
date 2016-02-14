package com.mermix.utils.net.args;

import com.mermix.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 11/10/2015
 * Description:
 */
public class SearchArgs extends UrlArgs {
	private Map<String, String> searcArgs = new HashMap<String, String>();

	public SearchArgs(String type, String location, String machineType, String cultivation, String contractType, String sortProperty, String direction){
		searcArgs.put(Constants.URI.PARAMS.TYPE, type);
		searcArgs.put(Constants.URI.PARAMS.LOCATION, location);
		searcArgs.put(Constants.URI.PARAMS.MACHINETYPE, machineType);
		searcArgs.put(Constants.URI.PARAMS.CULTIVATION, cultivation);
		searcArgs.put(Constants.URI.PARAMS.CONTRACTTYPE, contractType);
		searcArgs.put(Constants.URI.PARAMS.SORT, sortProperty);
		searcArgs.put(Constants.URI.PARAMS.DIRECTION, direction);
	}

	public String getUrlArgs(){
		return super.getUrlArgs(searcArgs);
	}
}