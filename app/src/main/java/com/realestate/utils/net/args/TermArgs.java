package com.realestate.utils.net.args;

import com.realestate.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 11/10/2015
 * Description:
 */
public class TermArgs extends UrlArgs {

	private Map<String, String> termArgs = new HashMap<String, String>();

	public TermArgs(String vocabulary){
		termArgs.put(Constants.URI.PARAMS.VOCABULARY, vocabulary);
	}

	public String getUrlArgs(){
		return super.getUrlArgs(termArgs);
	}
}
