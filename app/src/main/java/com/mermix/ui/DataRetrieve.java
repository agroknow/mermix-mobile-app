package com.mermix.ui;

import com.mermix.model.common.Pojo;
import com.mermix.utils.net.args.UrlArgs;

/**
 * Created on 09/08/2015
 * Description:
 */
public interface DataRetrieve {
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
