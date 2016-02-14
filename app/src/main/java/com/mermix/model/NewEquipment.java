package com.mermix.model;

import com.mermix.model.common.Pojo;

import java.io.Serializable;

/**
 * Created on 24/12/2015
 * Description:
 */
public class NewEquipment extends Pojo implements Serializable {
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
