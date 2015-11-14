package com.realestate.model;

import com.realestate.model.common.Pojo;

import java.io.Serializable;
import java.util.List;

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
