package com.realestate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.realestate.model.common.Address;
import com.realestate.model.common.Body;
import com.realestate.model.common.DrupalListField;
import com.realestate.model.common.Pojo;

import java.util.List;

/**
 * Created on 20/12/2015
 * Description:
 * holds data that will be submitted in REST API using POST method, to create new equipment
 */
public class EquipmentPostPayload extends Pojo{
	private String title;
	private Body body;
	private String type;
	private int promote;
	private String language;
	private DrupalListField author;
	@JsonProperty("field_type")
	private DrupalListField equipmentType;
	@JsonProperty("field_contract_type")
	private DrupalListField contract;
	@JsonProperty("field_cultivation")
	private List<DrupalListField> cultivation;
	@JsonProperty("field_location")
	private DrupalListField location;
	@JsonProperty("field_address")
	private Address address;
	@JsonProperty("field_image")
	private List<String> image;

	/**
	 * use current function instead of constructor due to Jackson parsing limitations
	 */
	public void setData(String title, Body body, String type, int promote, String language, DrupalListField author, DrupalListField equipmentType, DrupalListField contract, List<DrupalListField> cultivation, DrupalListField location, Address address, List<String> image) {
		this.title = title;
		this.body = body;
		this.type = type;
		this.promote = promote;
		this.language = language;
		this.author = author;
		this.equipmentType = equipmentType;
		this.contract = contract;
		this.cultivation = cultivation;
		this.location = location;
		this.address = address;
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPromote() {
		return promote;
	}

	public void setPromote(int promote) {
		this.promote = promote;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public DrupalListField getAuthor() {
		return author;
	}

	public void setAuthor(DrupalListField author) {
		this.author = author;
	}

	public DrupalListField getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(DrupalListField equipmentType) {
		this.equipmentType = equipmentType;
	}

	public DrupalListField getContract() {
		return contract;
	}

	public void setContract(DrupalListField contract) {
		this.contract = contract;
	}

	public List<DrupalListField> getCultivation() {
		return cultivation;
	}

	public void setCultivation(List<DrupalListField> cultivation) {
		this.cultivation = cultivation;
	}

	public DrupalListField getLocation() {
		return location;
	}

	public void setLocation(DrupalListField location) {
		this.location = location;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<String> getImage() {
		return image;
	}

	public void setImage(List<String> image) {
		this.image = image;
	}
}
