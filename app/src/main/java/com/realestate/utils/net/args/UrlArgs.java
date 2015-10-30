package com.realestate.utils.net.args;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created on 11/10/2015
 * Description:
 * class that is extended by the url args classes
 */
public class UrlArgs {

	public String getUrlArgs(Map<String, String> args){
		String urlArgs = "";
		String urlArgKey;
		String urlArgValue;
		Iterator it = args.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry)it.next();
			urlArgKey = pair.getKey().toString();
			urlArgValue = pair.getValue().toString();
			urlArgs += (!urlArgValue.isEmpty()) ? "&"+urlArgKey+"="+urlArgValue : "";
			it.remove(); // avoids a ConcurrentModificationException
		}
		if(!urlArgs.isEmpty()){
			urlArgs = urlArgs.substring(1);
		}
		return urlArgs;
	}
}
