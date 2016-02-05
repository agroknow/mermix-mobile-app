package com.realestate.utils.net.args;

import com.realestate.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 21/12/2015
 * Description:
 */
public class NewEquipmentArgs extends UrlArgs {
	private Map<String, String> newEquipmentArgs = new HashMap<String, String>();

	public NewEquipmentArgs(String jsonPayload) {
		newEquipmentArgs.put(Constants.URI.PARAMS.JSONPAYLOAD, jsonPayload);
	}
	public NewEquipmentArgs(String jsonPayload, boolean flag) {
		newEquipmentArgs.put(Constants.URI.PARAMS.JSONPAYLOAD_MULTI, jsonPayload);
	}

	public String getUrlArgs(){
		return super.getUrlArgs(newEquipmentArgs);
	}

}
