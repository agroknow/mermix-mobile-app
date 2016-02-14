package com.mermix.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mermix.model.common.Pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 07/08/2015
 * Description:
 * po
 */
public class ListOfEquipments extends Pojo implements Serializable {
	@JsonProperty("list")
	private List<Equipment> equipments;

	public List<Equipment> getEquipments() {
		return equipments;
	}

	public void setEquipments(List<Equipment> equipments) {
		this.equipments = equipments;
	}

}
