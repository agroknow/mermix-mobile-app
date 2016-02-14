package com.mermix.model;

import com.mermix.model.common.Pojo;

import java.io.Serializable;

/**
 * Created on 29/10/2015
 * Description:
 */
public class ListOfNearbyEquipments extends Pojo implements Serializable {
	private EquipmentInView[] equipments;

	public EquipmentInView[] getEquipments() {
		return equipments;
	}

	public void setEquipments(EquipmentInView[] equipments) {
		this.equipments = equipments;
	}
}
