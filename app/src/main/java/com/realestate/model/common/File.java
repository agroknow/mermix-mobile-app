package com.realestate.model.common;

import java.io.Serializable;

/**
 * Created on 16/08/2015
 * Description:
 */
public class File implements Serializable {
	private String url = "";

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
