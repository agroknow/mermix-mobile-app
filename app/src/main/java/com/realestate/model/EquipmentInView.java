package com.realestate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.realestate.model.common.Pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 29/10/2015
 * Description:
 * equipment structure in drupal view: mobile_app_machine_results
 *
 */
public class EquipmentInView extends Pojo implements Serializable{
	@JsonProperty("node_title")
	private String title;
	private String nid;
	private String body;
	@JsonProperty("field_address")
	private String coordinates;
	@JsonProperty("field_image")
	private List<String> images;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}
}
