package com.realestate.utils;

import com.realestate.model.common.Pojo;
import com.realestate.utils.net.args.UrlArgs;

/**
 * Created on 09/08/2015
 * Description:
 */
public interface DataRetrieveUI {
	/**
	 * method
	 * to update UI with the REST API's retrieved data
	 * @param apiResponseData
	 */
	void updateUI(Pojo apiResponseData);

	/**
	 * method
	 * to generate REST API url and
	 * to invoke startService
	 */
	void startRequestService(UrlArgs urlArgs);
}
