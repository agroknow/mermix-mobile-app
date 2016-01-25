package com.realestate.utils.net.args;

import com.realestate.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 24/01/2016
 * Description:
 */
public class UserArgs extends UrlArgs {
	private Map<String, String> userArgs = new HashMap<>();

	public UserArgs(String name){
		userArgs.put(Constants.URI.PARAMS.NAME, name);
	}

	public String getUrlArgs(){
		return super.getUrlArgs(userArgs);
	}
}
