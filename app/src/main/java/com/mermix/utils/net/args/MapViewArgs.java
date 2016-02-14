package com.mermix.utils.net.args;

import com.mermix.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 29/10/2015
 * Description:
 */
public class MapViewArgs extends UrlArgs {
	private Map<String, String> mapViewArgs = new HashMap<String, String>();

	public MapViewArgs(String lat, String log){
		String coordinates = lat+","+log;
		mapViewArgs.put(Constants.URI.PARAMS.COORDINATES, coordinates);
	}

	public String getUrlArgs(){
		return super.getUrlArgs(mapViewArgs);
	}
}
