package com.realestate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.realestate.model.common.Pojo;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public Double[] getCoordinates() {
		return Common.stringToDoubleArr(coordinates, Constants.CONCATDELIMETER);
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public List<String> getImages() {
		//get url from <img src="
		Pattern p = Pattern.compile("src=\"(.*?)\"");
		for(int i = 0; i < images.size(); i++){
			Matcher m = p.matcher(images.get(i));
			if(m.find())
				images.set(i, m.group(1));
		}
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}
}
