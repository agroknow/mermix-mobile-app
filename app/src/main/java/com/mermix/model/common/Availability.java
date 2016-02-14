package com.mermix.model.common;

import java.io.Serializable;

/**
 * Created on 10/09/2015
 * Description:
 */
public class Availability implements Serializable {
	private Integer enabled;
	private String name;

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
